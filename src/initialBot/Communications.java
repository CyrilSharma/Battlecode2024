package initialBot;
import battlecode.common.*;

public class Communications {
    boolean first;
    RobotController rc;
    public Communications(RobotController rc) {
        this.rc = rc;
    }

    public void logflag(MapLocation m) throws GameActionException {
        int hash = hashLocation(m);
        for (int i = Channels.FLAGS; i < Channels.FLAGS + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == 0) rc.writeSharedArray(i, hash);
        }
    }

    public void deleteflag(MapLocation m) throws GameActionException {
        int hash = hashLocation(m);
        for (int i = Channels.FLAGS; i < Channels.FLAGS + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == hash) rc.writeSharedArray(i, 0);
        }
    }

    public MapLocation[] getflags() throws GameActionException {
        int ctr = 0;
        MapLocation[] locs = new MapLocation[3];
        for (int i = Channels.FLAGS; i < Channels.FLAGS + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == 0) continue;
            locs[ctr++] = dehashLocation(data);
        }
        MapLocation[] trim = new MapLocation[ctr];
        for (int i = ctr; i-- > 0;) {
            trim[i] = locs[i];
        }
        return trim;
    }

    public void decideFirst() throws GameActionException {
        if (rc.readSharedArray(Channels.FIRST) == 0) first = true;
        rc.writeSharedArray(Channels.FIRST, 1);
    }

    public AttackTarget[] getAttackTargets() throws GameActionException {
        int ctr = 0;
        AttackTarget[] targets = new AttackTarget[Channels.N_ATTACK_TARGETS];
        for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
            int item = rc.readSharedArray(Channels.ATTACK_TARGETS + i);
            if (item == 0) continue;
            AttackTarget at = dehashAttackTarget(item);
            targets[ctr++] = at;
        }

        AttackTarget[] trim = new AttackTarget[ctr];
        for (int i = ctr; i-- > 0;) {
            trim[i] = targets[i];
        }
        return trim;
    }

    public void addAttackTarget(MapLocation m, int score) throws GameActionException {
        int hash = hashAttackTarget(new AttackTarget(m, score));
        for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
            int item = rc.readSharedArray(Channels.ATTACK_TARGETS + i);
            if (item != 0) {
                AttackTarget at = dehashAttackTarget(item);
                if (at.m.distanceSquaredTo(m) <= 100) continue;
            }
            rc.writeSharedArray(Channels.ATTACK_TARGETS + i, hash);
            break;
        }
    }

    public void refreshTargets() throws GameActionException {
        if (!first) return;
        if (rc.getRoundNum() % 5 != 0) return;
        for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
            rc.writeSharedArray(Channels.ATTACK_TARGETS + i, 0);
        }
    }

    public int hashAttackTarget(AttackTarget a) throws GameActionException {
        return hashLocation(a.m) + (a.score << 12);
    }

    public AttackTarget dehashAttackTarget(int data) throws GameActionException {
        return new AttackTarget(
            dehashLocation(data & 0b111111111111),
            data >> 12
        );
    }

    public int hashLocation(MapLocation m) throws GameActionException {
        return (m.x << 6) + (m.y + 1);
    }

    public MapLocation dehashLocation(int data) throws GameActionException {
        return new MapLocation(data >> 6, (data & 0b111111) - 1);
    }

    public class AttackTarget {
        MapLocation m;
        int score;
        public AttackTarget(MapLocation m, int score) {
            this.m = m;
            this.score = score;
        }
    }
}