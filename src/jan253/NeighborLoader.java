package jan253;
import battlecode.common.*;
public class NeighborLoader {
    RobotController rc;
    public NeighborLoader(RobotController rc) {
        this.rc = rc;
    }
    public void load(NeighborTracker am) throws GameActionException {
        if (!rc.isSpawned()) return;
        load_allies(am);
        load_enemies(am);
    }
    
    public void load_allies(NeighborTracker am) throws GameActionException {
        int diff = -4;
        int offset = rc.getLocation().translate(diff, diff).hashCode();
        long t_mask0 = 0;
        long t_mask1 = 0;
        RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam());
        for (int j = robots.length; j-- > 0; ) {
            switch (robots[j].location.hashCode() - offset) {
                case 0: t_mask0 += 0x1L; continue;
                case 65536: t_mask0 += 0x2L; continue;
                case 131072: t_mask0 += 0x4L; continue;
                case 196608: t_mask0 += 0x8L; continue;
                case 262144: t_mask0 += 0x10L; continue;
                case 327680: t_mask0 += 0x20L; continue;
                case 393216: t_mask0 += 0x40L; continue;
                case 458752: t_mask0 += 0x80L; continue;
                case 524288: t_mask0 += 0x100L; continue;
                case 1: t_mask0 += 0x200L; continue;
                case 65537: t_mask0 += 0x400L; continue;
                case 131073: t_mask0 += 0x800L; continue;
                case 196609: t_mask0 += 0x1000L; continue;
                case 262145: t_mask0 += 0x2000L; continue;
                case 327681: t_mask0 += 0x4000L; continue;
                case 393217: t_mask0 += 0x8000L; continue;
                case 458753: t_mask0 += 0x10000L; continue;
                case 524289: t_mask0 += 0x20000L; continue;
                case 2: t_mask0 += 0x40000L; continue;
                case 65538: t_mask0 += 0x80000L; continue;
                case 131074: t_mask0 += 0x100000L; continue;
                case 196610: t_mask0 += 0x200000L; continue;
                case 262146: t_mask0 += 0x400000L; continue;
                case 327682: t_mask0 += 0x800000L; continue;
                case 393218: t_mask0 += 0x1000000L; continue;
                case 458754: t_mask0 += 0x2000000L; continue;
                case 524290: t_mask0 += 0x4000000L; continue;
                case 3: t_mask0 += 0x8000000L; continue;
                case 65539: t_mask0 += 0x10000000L; continue;
                case 131075: t_mask0 += 0x20000000L; continue;
                case 196611: t_mask0 += 0x40000000L; continue;
                case 262147: t_mask0 += 0x80000000L; continue;
                case 327683: t_mask0 += 0x100000000L; continue;
                case 393219: t_mask0 += 0x200000000L; continue;
                case 458755: t_mask0 += 0x400000000L; continue;
                case 524291: t_mask0 += 0x800000000L; continue;
                case 4: t_mask0 += 0x1000000000L; continue;
                case 65540: t_mask0 += 0x2000000000L; continue;
                case 131076: t_mask0 += 0x4000000000L; continue;
                case 196612: t_mask0 += 0x8000000000L; continue;
                case 262148: t_mask0 += 0x10000000000L; continue;
                case 327684: t_mask0 += 0x20000000000L; continue;
                case 393220: t_mask0 += 0x40000000000L; continue;
                case 458756: t_mask0 += 0x80000000000L; continue;
                case 524292: t_mask0 += 0x100000000000L; continue;
                case 5: t_mask0 += 0x200000000000L; continue;
                case 65541: t_mask0 += 0x400000000000L; continue;
                case 131077: t_mask0 += 0x800000000000L; continue;
                case 196613: t_mask0 += 0x1000000000000L; continue;
                case 262149: t_mask0 += 0x2000000000000L; continue;
                case 327685: t_mask0 += 0x4000000000000L; continue;
                case 393221: t_mask0 += 0x8000000000000L; continue;
                case 458757: t_mask0 += 0x10000000000000L; continue;
                case 524293: t_mask0 += 0x20000000000000L; continue;
                case 6: t_mask0 += 0x40000000000000L; continue;
                case 65542: t_mask0 += 0x80000000000000L; continue;
                case 131078: t_mask0 += 0x100000000000000L; continue;
                case 196614: t_mask0 += 0x200000000000000L; continue;
                case 262150: t_mask0 += 0x400000000000000L; continue;
                case 327686: t_mask0 += 0x800000000000000L; continue;
                case 393222: t_mask0 += 0x1000000000000000L; continue;
                case 458758: t_mask0 += 0x2000000000000000L; continue;
                case 524294: t_mask0 += 0x4000000000000000L; continue;
                case 7: t_mask1 += 0x1L; continue;
                case 65543: t_mask1 += 0x2L; continue;
                case 131079: t_mask1 += 0x4L; continue;
                case 196615: t_mask1 += 0x8L; continue;
                case 262151: t_mask1 += 0x10L; continue;
                case 327687: t_mask1 += 0x20L; continue;
                case 393223: t_mask1 += 0x40L; continue;
                case 458759: t_mask1 += 0x80L; continue;
                case 524295: t_mask1 += 0x100L; continue;
                case 8: t_mask1 += 0x200L; continue;
                case 65544: t_mask1 += 0x400L; continue;
                case 131080: t_mask1 += 0x800L; continue;
                case 196616: t_mask1 += 0x1000L; continue;
                case 262152: t_mask1 += 0x2000L; continue;
                case 327688: t_mask1 += 0x4000L; continue;
                case 393224: t_mask1 += 0x8000L; continue;
                case 458760: t_mask1 += 0x10000L; continue;
                case 524296: t_mask1 += 0x20000L; continue;
                default: continue;
            }
        }
        
        am.friend_mask1 = t_mask1;
        am.friend_mask0 = t_mask0;
        am.friends = robots;
    }
    
    public void load_enemies(NeighborTracker am) throws GameActionException {
        int diff = -4;
        int offset = rc.getLocation().translate(diff, diff).hashCode();
        long t_mask0 = 0;
        long t_mask1 = 0;
        RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for (int j = robots.length; j-- > 0; ) {
            switch (robots[j].location.hashCode() - offset) {
                case 0: t_mask0 += 0x1L; continue;
                case 65536: t_mask0 += 0x2L; continue;
                case 131072: t_mask0 += 0x4L; continue;
                case 196608: t_mask0 += 0x8L; continue;
                case 262144: t_mask0 += 0x10L; continue;
                case 327680: t_mask0 += 0x20L; continue;
                case 393216: t_mask0 += 0x40L; continue;
                case 458752: t_mask0 += 0x80L; continue;
                case 524288: t_mask0 += 0x100L; continue;
                case 1: t_mask0 += 0x200L; continue;
                case 65537: t_mask0 += 0x400L; continue;
                case 131073: t_mask0 += 0x800L; continue;
                case 196609: t_mask0 += 0x1000L; continue;
                case 262145: t_mask0 += 0x2000L; continue;
                case 327681: t_mask0 += 0x4000L; continue;
                case 393217: t_mask0 += 0x8000L; continue;
                case 458753: t_mask0 += 0x10000L; continue;
                case 524289: t_mask0 += 0x20000L; continue;
                case 2: t_mask0 += 0x40000L; continue;
                case 65538: t_mask0 += 0x80000L; continue;
                case 131074: t_mask0 += 0x100000L; continue;
                case 196610: t_mask0 += 0x200000L; continue;
                case 262146: t_mask0 += 0x400000L; continue;
                case 327682: t_mask0 += 0x800000L; continue;
                case 393218: t_mask0 += 0x1000000L; continue;
                case 458754: t_mask0 += 0x2000000L; continue;
                case 524290: t_mask0 += 0x4000000L; continue;
                case 3: t_mask0 += 0x8000000L; continue;
                case 65539: t_mask0 += 0x10000000L; continue;
                case 131075: t_mask0 += 0x20000000L; continue;
                case 196611: t_mask0 += 0x40000000L; continue;
                case 262147: t_mask0 += 0x80000000L; continue;
                case 327683: t_mask0 += 0x100000000L; continue;
                case 393219: t_mask0 += 0x200000000L; continue;
                case 458755: t_mask0 += 0x400000000L; continue;
                case 524291: t_mask0 += 0x800000000L; continue;
                case 4: t_mask0 += 0x1000000000L; continue;
                case 65540: t_mask0 += 0x2000000000L; continue;
                case 131076: t_mask0 += 0x4000000000L; continue;
                case 196612: t_mask0 += 0x8000000000L; continue;
                case 262148: t_mask0 += 0x10000000000L; continue;
                case 327684: t_mask0 += 0x20000000000L; continue;
                case 393220: t_mask0 += 0x40000000000L; continue;
                case 458756: t_mask0 += 0x80000000000L; continue;
                case 524292: t_mask0 += 0x100000000000L; continue;
                case 5: t_mask0 += 0x200000000000L; continue;
                case 65541: t_mask0 += 0x400000000000L; continue;
                case 131077: t_mask0 += 0x800000000000L; continue;
                case 196613: t_mask0 += 0x1000000000000L; continue;
                case 262149: t_mask0 += 0x2000000000000L; continue;
                case 327685: t_mask0 += 0x4000000000000L; continue;
                case 393221: t_mask0 += 0x8000000000000L; continue;
                case 458757: t_mask0 += 0x10000000000000L; continue;
                case 524293: t_mask0 += 0x20000000000000L; continue;
                case 6: t_mask0 += 0x40000000000000L; continue;
                case 65542: t_mask0 += 0x80000000000000L; continue;
                case 131078: t_mask0 += 0x100000000000000L; continue;
                case 196614: t_mask0 += 0x200000000000000L; continue;
                case 262150: t_mask0 += 0x400000000000000L; continue;
                case 327686: t_mask0 += 0x800000000000000L; continue;
                case 393222: t_mask0 += 0x1000000000000000L; continue;
                case 458758: t_mask0 += 0x2000000000000000L; continue;
                case 524294: t_mask0 += 0x4000000000000000L; continue;
                case 7: t_mask1 += 0x1L; continue;
                case 65543: t_mask1 += 0x2L; continue;
                case 131079: t_mask1 += 0x4L; continue;
                case 196615: t_mask1 += 0x8L; continue;
                case 262151: t_mask1 += 0x10L; continue;
                case 327687: t_mask1 += 0x20L; continue;
                case 393223: t_mask1 += 0x40L; continue;
                case 458759: t_mask1 += 0x80L; continue;
                case 524295: t_mask1 += 0x100L; continue;
                case 8: t_mask1 += 0x200L; continue;
                case 65544: t_mask1 += 0x400L; continue;
                case 131080: t_mask1 += 0x800L; continue;
                case 196616: t_mask1 += 0x1000L; continue;
                case 262152: t_mask1 += 0x2000L; continue;
                case 327688: t_mask1 += 0x4000L; continue;
                case 393224: t_mask1 += 0x8000L; continue;
                case 458760: t_mask1 += 0x10000L; continue;
                case 524296: t_mask1 += 0x20000L; continue;
                default: continue;
            }
        }
        
        am.enemy_mask1 = t_mask1;
        am.enemy_mask0 = t_mask0;
        am.enemies = robots;
    }
}
