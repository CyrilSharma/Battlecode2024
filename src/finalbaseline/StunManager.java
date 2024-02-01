package finalbaseline;
import battlecode.common.*;

public class StunManager {
    long prev_stun_mask0 = 0;
    long prev_stun_mask1 = 0;
    long stun_trap_mask0 = 0;
    long stun_trap_mask1 = 0;
    long stunned_mask0 = 0;
    long stunned_mask1 = 0;
    long[] detonated_stun_mask0 = { 0, 0, 0, 0, 0 };
    long[] detonated_stun_mask1 = { 0, 0, 0, 0, 0 };
    int[] lastStunned = null;


    Communications comms;
    MapLocation prevloc = null;
    RobotController rc;
    MapTracker mt;
    NeighborTracker nt;
    public StunManager(RobotController rc, MapTracker mt, NeighborTracker nt, Communications comms) {
        this.rc = rc;
        this.mt = mt;
        this.nt = nt;
        this.comms = comms;
    }

    // Handles mantaining all of the stuff involving stuns.
    public void run() throws GameActionException {
        if (!rc.isSpawned()) return;
        if (prevloc == null) {
            for (int i = 5; i-- > 0;) {
                stunned_mask0 = 0;
                stunned_mask1 = 0;
            }
            prev_stun_mask0 = 0;
            prev_stun_mask1 = 0;
            prevloc = rc.getLocation();
            return;
        }

        shiftMasks();
        computeStunnable();
        prevloc = rc.getLocation();
        if (rc.getRoundNum() > 200) {
            if(lastStunned == null) lastStunned =  new int[4097];
            getLatest();
            updateStun();
        }
    }

    public void updateStun() throws GameActionException {
        long triggered_stuns0 = (mt.stun_mask0 ^ mt.prev_stun_trap0) & mt.prev_stun_trap0;
        long triggered_stuns1 = (mt.stun_mask1 ^ mt.prev_stun_trap1) & mt.prev_stun_trap1;
        Util.displayMask(rc, triggered_stuns0, triggered_stuns1, 150, 50, 50);
        //ok i can make it faster later, rn i just want to test it out
        while (triggered_stuns0 > 0) {
            long lsb = triggered_stuns0 & (-triggered_stuns0);
            int pos = (int)Math.round(Math.log(lsb) / Math.log(2));
            int dy = (pos / 9) - 4;
            int dx = (pos % 9) - 4;
            MapLocation explosion = new MapLocation(rc.getLocation().x + dx, rc.getLocation().y + dy);
            //rc.setIndicatorDot(explosion, 0, 0, 255);
            for(int i = nt.enemies.length; i-- > 0;) {
                int d = nt.enemies[i].location.distanceSquaredTo(explosion);
                if (d <= 13) {
                    if (lastStunned[nt.enemies[i].ID - 10000] != rc.getRoundNum() - 1) {
                        lastStunned[nt.enemies[i].ID - 10000] = rc.getRoundNum();
                        rc.setIndicatorDot(nt.enemies[i].location, 255, 0, 0);
                        comms.addStunned(nt.enemies[i].ID - 10000);
                    }
                }
            }
            triggered_stuns0 -= lsb;
        }
        while (triggered_stuns1 > 0) {
            long lsb = triggered_stuns1 & (-triggered_stuns1);
            int pos = (int)Math.round(Math.log(lsb) / Math.log(2));
            int dy = (pos / 9) + 3;
            int dx = (pos % 9) - 4;
            MapLocation explosion = new MapLocation(rc.getLocation().x + dx, rc.getLocation().y + dy);
            //rc.setIndicatorDot(explosion, 0, 0, 255);
            for(int i = nt.enemies.length; i-- > 0;) {
                int d = nt.enemies[i].location.distanceSquaredTo(explosion);
                if (d <= 13) {
                    if (lastStunned[nt.enemies[i].ID - 10000] != rc.getRoundNum() - 1) {
                        lastStunned[nt.enemies[i].ID - 10000] = rc.getRoundNum();
                        rc.setIndicatorDot(nt.enemies[i].location, 255, 0, 0);
                        comms.addStunned(nt.enemies[i].ID - 10000);
                    }
                }
            }
            triggered_stuns1 -= lsb;
        }
    }

    public void getLatest() throws GameActionException {
        for (int i = Channels.STUNNED_UNITS; i < Channels.STUNNED_UNITS_NUM + Channels.STUNNED_UNITS; i++) {
            int data = rc.readSharedArray(i);
            if(data == 0) continue;
            int id = data >> 3;
            int turns = data & 0b111;
            lastStunned[id] = rc.getRoundNum() - turns;
        }
    }

    // Precomputes the region friendly stuns protect for use in micro.
    public void computeStunnable() {
        long temp = 0;
        long mask0 = 0x7FFFFFFFFFFFFFFFL;
        long mask1 = 0x3FFFFL;
        long loverflow = 0x7fbfdfeff7fbfdfeL;
        long roverflow = 0x3fdfeff7fbfdfeffL;

        long stun0 = mt.stun_mask0;
        long stun1 = mt.stun_mask1;
        stun0 = (stun0 | ((stun0 << 1) & loverflow) | ((stun0 >>> 1) & roverflow));
        stun1 = (stun1 | ((stun1 << 1) & loverflow) | ((stun1 >>> 1) & roverflow));
        temp = stun0;
        stun0 = (stun0 | (stun0 << 9) | (stun0 >>> 9) | (stun1 << 54)) & mask0;
        stun1 = (stun1 | (stun1 << 9) | (stun1 >>> 9) | (temp >>> 54)) & mask1;
        stun_trap_mask0 = stun0;
        stun_trap_mask1 = stun1;

        stun0 = (~mt.stun_mask0 & prev_stun_mask0) & mask0;
        stun1 = (~mt.stun_mask1 & prev_stun_mask1) & mask1;
        prev_stun_mask0 = mt.stun_mask0;
        prev_stun_mask1 = mt.stun_mask1;


        for (int i = 3; i-- > 0;) {
            stun0 = (stun0 | ((stun0 << 1) & loverflow) | ((stun0 >>> 1) & roverflow));
            stun1 = (stun1 | ((stun1 << 1) & loverflow) | ((stun1 >>> 1) & roverflow));
            temp = stun0;
            stun0 = (stun0 | (stun0 << 9) | (stun0 >>> 9) | (stun1 << 54)) & mask0;
            stun1 = (stun1 | (stun1 << 9) | (stun1 >>> 9) | (temp >>> 54)) & mask1;
        }

        int expire_rounds = 5;
        int mod = (rc.getRoundNum() % expire_rounds);
        detonated_stun_mask0[mod] = stun0 & nt.enemy_mask0;
        detonated_stun_mask1[mod] = stun1 & nt.enemy_mask1;
        for (int i = expire_rounds; i-- > 0;) {
            detonated_stun_mask0[i] &= nt.enemy_mask0;
            detonated_stun_mask1[i] &= nt.enemy_mask1;
        }

        stunned_mask0 = 0;
        stunned_mask1 = 0;
        for (int i = (mod + 1) % expire_rounds; i != mod; i = (i + 1) % expire_rounds) {
            stunned_mask0 |= detonated_stun_mask0[i];
            stunned_mask1 |= detonated_stun_mask1[i];
        }
    }

    // This is not that expensive.
    // I'm doing it like this because the alternative is to read in all maplocations,
    // Store everything in a huge global array, actively mantain and delete every square...
    public void shiftMasks() {
        Direction dir = rc.getLocation().directionTo(prevloc);
        long loverflow = 0x7fbfdfeff7fbfdfeL;
        long roverflow = 0x3fdfeff7fbfdfeffL;
        long overflow = 0xFFFFFFFFFFFFFFFFL;
        long tm0 = 0;
        long tm1 = 0;

        int shift = 0;
        int right = 0;
        switch (dir) {
            case NORTHEAST: shift = 10;  right = -1; break;
            case NORTH:     shift = 9;               break;
            case NORTHWEST: shift = 8;   right = 1;  break;
            case EAST:      shift = 1;   right = -1; break;
            case CENTER:    shift = 0;               break;
            case WEST:      shift = -1;  right = 1;  break;
            case SOUTHEAST: shift = -8;  right = -1; break;
            case SOUTH:     shift = -9;              break;
            case SOUTHWEST: shift = -10; right = 1; break;
        }

        boolean positive = shift >= 0;
        shift = (positive) ? shift : -shift;
        if (right > 0) overflow = roverflow;
        else if (right < 0) overflow = loverflow;
        for (int i = 5; i-- > 0;) {
            tm0 = detonated_stun_mask0[i];
            tm1 = detonated_stun_mask1[i];
            if (positive) {
                detonated_stun_mask0[i] = ((tm0 << shift) & overflow);
                detonated_stun_mask1[i] = ((tm1 << shift) & overflow) | ((tm0 >>> (63 - shift)) & 0x1FF);
            } else {
                detonated_stun_mask0[i] = ((tm0 >>> shift) & overflow) | ((tm1 & 0x1FF) << (63 - shift));
                detonated_stun_mask1[i] = ((tm1 >>> shift) & overflow);
            }
        }


        tm0 = prev_stun_mask0;
        tm1 = prev_stun_mask1;
        if (positive) {
            prev_stun_mask0 = ((tm0 << shift) & overflow);
            prev_stun_mask1 = ((tm1 << shift) & overflow) | ((tm0 >>> (63 - shift)) & 0x1FF);
        } else {
            prev_stun_mask0 = ((tm0 >>> shift) & overflow) | ((tm1 & 0x1FF) << (63 - shift));
            prev_stun_mask1 = ((tm1 >>> shift) & overflow);
        }

        tm0 = mt.prev_stun_trap0;
        tm1 = mt.prev_stun_trap1;
        if (positive) {
            mt.prev_stun_trap0 = ((tm0 << shift) & overflow);
            mt.prev_stun_trap1 = ((tm1 << shift) & overflow) | ((tm0 >>> (63 - shift)) & 0x1FF);
        } else {
            mt.prev_stun_trap0 = ((tm0 >>> shift) & overflow) | ((tm1 & 0x1FF) << (63 - shift));
            mt.prev_stun_trap1 = ((tm1 >>> shift) & overflow);
        }

    }
}
