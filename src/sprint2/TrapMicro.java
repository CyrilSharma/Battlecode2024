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

    int[] getTrapStats() throws GameActionException {
        MicroTarget[] microtargets = new MicroTarget[9];
        for (Direction d: Direction.values()) {
            microtargets[d.ordinal()] = new MicroTarget(d);
        }

        int[] scores = new int[9];
        for (Direction d: Direction.values()) {
            MicroTarget t = microtargets[d.ordinal()];
            if (!t.canPlace || !t.probTriggered || t.close) scores[d.ordinal()] = 0;
            else scores[d.ordinal()] = t.enemyDamageScore;
        }
        return scores;
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
            canPlace = rc.canBuild(TrapType.STUN, nloc) && ((nloc.x + nloc.y) % 2 == 0);
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
            int x = nloc.x - (myloc.x - 4);
            int y = nloc.y - (myloc.y - 4);
            long start = (1L << (y * 9 + x));
            long antidag = (start | (start << 8) | (start >>> 8) | (start << 16) | (start >>> 16));
            Util.displayMask(rc, antidag & mt.stun_mask0, 0);
            close = (Long.bitCount(antidag & mt.stun_mask0)) >= 1;
            System.out.println("Close: " + close);

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
            enemyDamageScore = (
                Long.bitCount(explosion0 & nt.enemy_mask0) +
                Long.bitCount(explosion1 & nt.enemy_mask1)
            );
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
