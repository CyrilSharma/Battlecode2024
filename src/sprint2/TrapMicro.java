package sprint2;
import battlecode.common.*;

public class TrapMicro {
    RobotController rc;
    MapTracker mt;
    NeighborTracker nt;
    public TrapMicro(Robot r) {
        this.rc = r.rc;
        this.mt = r.mt;
        this.nt = r.nt;
    }

    void placeTrap() throws GameActionException {
        if (!rc.isActionReady()) return;
        MicroTarget[] microtargets = new MicroTarget[9];

        microtargets[0] = new MicroTarget(Direction.CENTER);
        microtargets[1] = new MicroTarget(Direction.NORTH);
        microtargets[2] = new MicroTarget(Direction.NORTHEAST);
        microtargets[3] = new MicroTarget(Direction.NORTHWEST);
        microtargets[4] = new MicroTarget(Direction.EAST);
        microtargets[5] = new MicroTarget(Direction.WEST);
        microtargets[6] = new MicroTarget(Direction.SOUTH);
        microtargets[7] = new MicroTarget(Direction.SOUTHEAST);
        microtargets[8] = new MicroTarget(Direction.SOUTHWEST);

        MicroTarget best = microtargets[0];
        if (microtargets[1].isBetterThan(best)) best = microtargets[1];
        if (microtargets[2].isBetterThan(best)) best = microtargets[2];
        if (microtargets[3].isBetterThan(best)) best = microtargets[3];
        if (microtargets[4].isBetterThan(best)) best = microtargets[4];
        if (microtargets[5].isBetterThan(best)) best = microtargets[5];
        if (microtargets[6].isBetterThan(best)) best = microtargets[6];
        if (microtargets[7].isBetterThan(best)) best = microtargets[7];
        if (microtargets[8].isBetterThan(best)) best = microtargets[8];

        int crumbs = rc.getCrumbs();
        // if (rc.getLevel(SkillType.BUILD) < 3) crumbs /= 2;
        if ((best.close)) return;
        if ((!best.probTriggered)) return;
        if ((best.enemyDamageScore >= 500 * 5) ||
            (best.enemyDamageScore >= 500 * 3 && crumbs > 500) ||
            (best.enemyDamageScore >= 500 * 2 && crumbs >= 1500) || (best.enemyDamageScore >= 500 && crumbs >= 5000)) {
            rc.build(TrapType.STUN, best.nloc);
        }
    }

    class MicroTarget {
        MapLocation nloc;
        Direction dir;
        boolean canPlace;
        boolean close = false;
        boolean probTriggered = true;
        int enemyDamageScore;
        
        MicroTarget(Direction dir) throws GameActionException {
            nloc = rc.getLocation().add(dir);
            canPlace = rc.canBuild(TrapType.STUN, nloc);
            this.dir = dir;
            computeStats();
        }

        void computeStats() throws GameActionException {
            if (!canPlace) return;
            long action0 = 0b000000000000111000000111000000111000000000000000000000000000000L;
            long action1 = 0;
            switch (dir) {
                case NORTHEAST:     action0 <<= 10;  break;
                case NORTH:         action0 <<= 9;   break;
                case NORTHWEST:     action0 <<= 8;   break;
                case EAST:          action0 <<= 1;   break;
                case CENTER:        break;
                case WEST:          action0 >>>= 1;  break;
                case SOUTHEAST:     action0 >>>= 8;  break;
                case SOUTH:         action0 >>>= 9;  break;
                case SOUTHWEST:     action0 >>>= 10; break;
            }

            MapLocation myloc = rc.getLocation();
            long start = 1L << (9 * (nloc.y - (myloc.y - 4)) + (nloc.x - (myloc.x - 4)));
            long adjacent = (start << 9) | (start >>> 9) | (start << 1) | (start >>> 1);
            close = ((adjacent & mt.stun_mask0) != 0);

            long mask0 = 0x7FFFFFFFFFFFFFFFL;
            long mask1 = 0x3FFFFL;
            long passible0 = ~(mt.wall_mask0 | mt.water_mask0) & mask0;
            long passible1 = ~(mt.wall_mask1 | mt.water_mask1) & mask1;
            long loverflow = 0x7fbfdfeff7fbfdfeL;
            long roverflow = 0x3fdfeff7fbfdfeffL;            
            long t_close0 = (nt.enemy_mask0);
            long t_close1 = (nt.enemy_mask1);
            long temp = 0;
            for (int i = 2; i-- > 0;) {
                t_close0 = (t_close0 | ((t_close0 << 1) & loverflow) | ((t_close0 >>> 1) & roverflow));
                t_close1 = (t_close1 | ((t_close1 << 1) & loverflow) | ((t_close1 >>> 1) & roverflow));
                temp = t_close0;
                t_close0 = (t_close0 | (t_close0 << 9) | (t_close0 >>> 9) | (t_close1 << 54)) & passible0;
                t_close1 = (t_close1 | (t_close1 << 9) | (t_close1 >>> 9) | (temp >>> 54)) & passible1;
            }
            long enemyreach0 = t_close0;
            long enemyreach1 = t_close1;
            // if (dir == Direction.CENTER) {
            //     Util.displayMask(rc, enemyreach0 & action0, enemyreach1 & action1);
            //     rc.setIndicatorDot(nloc, 0, 0, 0);
            // }
            probTriggered = ((enemyreach0 & action0) != 0 || (enemyreach1 & action1) != 0);


            t_close0 = (action0);
            t_close1 = (action1);
            for (int i = 2; i-- > 0;) {
                t_close0 = (t_close0 | ((t_close0 << 1) & loverflow) | ((t_close0 >>> 1) & roverflow));
                t_close1 = (t_close1 | ((t_close1 << 1) & loverflow) | ((t_close1 >>> 1) & roverflow));
                temp = t_close0;
                t_close0 = (t_close0 | (t_close0 << 9) | (t_close0 >>> 9) | (t_close1 << 54)) & mask0;
                t_close1 = (t_close1 | (t_close1 << 9) | (t_close1 >>> 9) | (temp >>> 54)) & mask1;
            }
            long explosion0 = t_close0;
            long explosion1 = t_close1;
            // if (dir == Direction.CENTER) {
            //     Util.displayMask(rc, explosion0, explosion1);
            //     rc.setIndicatorDot(nloc, 0, 0, 0);
            // }
            enemyDamageScore += 500 * Long.bitCount(explosion0 & nt.enemy_mask0);
            enemyDamageScore += 500 * Long.bitCount(explosion1 & nt.enemy_mask1);
        }

        boolean isBetterThan(MicroTarget mt) {
            if (!canPlace || close || !probTriggered) return false;
            if (enemyDamageScore > mt.enemyDamageScore) {
                return true;
            }
            return false;
        }
    }
}
