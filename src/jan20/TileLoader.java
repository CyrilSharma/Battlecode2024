package jan20;
import battlecode.common.*;
public class TileLoader {
    RobotController rc;
    public TileLoader(RobotController rc) {
        this.rc = rc;
    }
    public void load(MapTracker mt) throws GameActionException {
        int diff = -4;
        int offset = mt.rc.getLocation().translate(diff, diff).hashCode();
        long t_water_mask0 = 0;
        long t_water_mask1 = 0;
        long t_wall_mask0 = 0;
        long t_wall_mask1 = 0;
        long t_bomb_mask0 = 0;
        long t_bomb_mask1 = 0;
        long blocked = 0;
        MapInfo[] infos = mt.rc.senseNearbyMapInfos();
        for (int j = infos.length; j-- > 0; ) {
            MapInfo m = infos[j];
            if (m.isWater()) {
                switch (m.getMapLocation().hashCode() - offset) {
                    case 0: t_water_mask0 += 0x1L; continue;
                    case 65536: t_water_mask0 += 0x2L; continue;
                    case 131072: t_water_mask0 += 0x4L; continue;
                    case 196608: t_water_mask0 += 0x8L; continue;
                    case 262144: t_water_mask0 += 0x10L; continue;
                    case 327680: t_water_mask0 += 0x20L; continue;
                    case 393216: t_water_mask0 += 0x40L; continue;
                    case 458752: t_water_mask0 += 0x80L; continue;
                    case 524288: t_water_mask0 += 0x100L; continue;
                    case 1: t_water_mask0 += 0x200L; continue;
                    case 65537: t_water_mask0 += 0x400L; continue;
                    case 131073: t_water_mask0 += 0x800L; continue;
                    case 196609: t_water_mask0 += 0x1000L; continue;
                    case 262145: t_water_mask0 += 0x2000L; continue;
                    case 327681: t_water_mask0 += 0x4000L; continue;
                    case 393217: t_water_mask0 += 0x8000L; continue;
                    case 458753: t_water_mask0 += 0x10000L; continue;
                    case 524289: t_water_mask0 += 0x20000L; continue;
                    case 2: t_water_mask0 += 0x40000L; continue;
                    case 65538: t_water_mask0 += 0x80000L; continue;
                    case 131074: t_water_mask0 += 0x100000L; continue;
                    case 196610: t_water_mask0 += 0x200000L; continue;
                    case 262146: t_water_mask0 += 0x400000L; continue;
                    case 327682: t_water_mask0 += 0x800000L; continue;
                    case 393218: t_water_mask0 += 0x1000000L; continue;
                    case 458754: t_water_mask0 += 0x2000000L; continue;
                    case 524290: t_water_mask0 += 0x4000000L; continue;
                    case 3: t_water_mask0 += 0x8000000L; continue;
                    case 65539: t_water_mask0 += 0x10000000L; continue;
                    case 131075: t_water_mask0 += 0x20000000L; continue;
                    case 196611: t_water_mask0 += 0x40000000L; continue;
                    case 262147: t_water_mask0 += 0x80000000L; continue;
                    case 327683: t_water_mask0 += 0x100000000L; continue;
                    case 393219: t_water_mask0 += 0x200000000L; continue;
                    case 458755: t_water_mask0 += 0x400000000L; continue;
                    case 524291: t_water_mask0 += 0x800000000L; continue;
                    case 4: t_water_mask0 += 0x1000000000L; continue;
                    case 65540: t_water_mask0 += 0x2000000000L; continue;
                    case 131076: t_water_mask0 += 0x4000000000L; continue;
                    case 196612: t_water_mask0 += 0x8000000000L; continue;
                    case 262148: t_water_mask0 += 0x10000000000L; continue;
                    case 327684: t_water_mask0 += 0x20000000000L; continue;
                    case 393220: t_water_mask0 += 0x40000000000L; continue;
                    case 458756: t_water_mask0 += 0x80000000000L; continue;
                    case 524292: t_water_mask0 += 0x100000000000L; continue;
                    case 5: t_water_mask0 += 0x200000000000L; continue;
                    case 65541: t_water_mask0 += 0x400000000000L; continue;
                    case 131077: t_water_mask0 += 0x800000000000L; continue;
                    case 196613: t_water_mask0 += 0x1000000000000L; continue;
                    case 262149: t_water_mask0 += 0x2000000000000L; continue;
                    case 327685: t_water_mask0 += 0x4000000000000L; continue;
                    case 393221: t_water_mask0 += 0x8000000000000L; continue;
                    case 458757: t_water_mask0 += 0x10000000000000L; continue;
                    case 524293: t_water_mask0 += 0x20000000000000L; continue;
                    case 6: t_water_mask0 += 0x40000000000000L; continue;
                    case 65542: t_water_mask0 += 0x80000000000000L; continue;
                    case 131078: t_water_mask0 += 0x100000000000000L; continue;
                    case 196614: t_water_mask0 += 0x200000000000000L; continue;
                    case 262150: t_water_mask0 += 0x400000000000000L; continue;
                    case 327686: t_water_mask0 += 0x800000000000000L; continue;
                    case 393222: t_water_mask0 += 0x1000000000000000L; continue;
                    case 458758: t_water_mask0 += 0x2000000000000000L; continue;
                    case 524294: t_water_mask0 += 0x4000000000000000L; continue;
                    case 7: t_water_mask1 += 0x1L; continue;
                    case 65543: t_water_mask1 += 0x2L; continue;
                    case 131079: t_water_mask1 += 0x4L; continue;
                    case 196615: t_water_mask1 += 0x8L; continue;
                    case 262151: t_water_mask1 += 0x10L; continue;
                    case 327687: t_water_mask1 += 0x20L; continue;
                    case 393223: t_water_mask1 += 0x40L; continue;
                    case 458759: t_water_mask1 += 0x80L; continue;
                    case 524295: t_water_mask1 += 0x100L; continue;
                    case 8: t_water_mask1 += 0x200L; continue;
                    case 65544: t_water_mask1 += 0x400L; continue;
                    case 131080: t_water_mask1 += 0x800L; continue;
                    case 196616: t_water_mask1 += 0x1000L; continue;
                    case 262152: t_water_mask1 += 0x2000L; continue;
                    case 327688: t_water_mask1 += 0x4000L; continue;
                    case 393224: t_water_mask1 += 0x8000L; continue;
                    case 458760: t_water_mask1 += 0x10000L; continue;
                    case 524296: t_water_mask1 += 0x20000L; continue;
                    default: System.out.println("This shouldn't happen..."); continue;
                }
            } else if (m.isWall()) {
                switch (m.getMapLocation().hashCode() - offset) {
                    case 0: t_wall_mask0 += 0x1L; continue;
                    case 65536: t_wall_mask0 += 0x2L; continue;
                    case 131072: t_wall_mask0 += 0x4L; continue;
                    case 196608: t_wall_mask0 += 0x8L; continue;
                    case 262144: t_wall_mask0 += 0x10L; continue;
                    case 327680: t_wall_mask0 += 0x20L; continue;
                    case 393216: t_wall_mask0 += 0x40L; continue;
                    case 458752: t_wall_mask0 += 0x80L; continue;
                    case 524288: t_wall_mask0 += 0x100L; continue;
                    case 1: t_wall_mask0 += 0x200L; continue;
                    case 65537: t_wall_mask0 += 0x400L; continue;
                    case 131073: t_wall_mask0 += 0x800L; continue;
                    case 196609: t_wall_mask0 += 0x1000L; continue;
                    case 262145: t_wall_mask0 += 0x2000L; continue;
                    case 327681: t_wall_mask0 += 0x4000L; continue;
                    case 393217: t_wall_mask0 += 0x8000L; continue;
                    case 458753: t_wall_mask0 += 0x10000L; continue;
                    case 524289: t_wall_mask0 += 0x20000L; continue;
                    case 2: t_wall_mask0 += 0x40000L; continue;
                    case 65538: t_wall_mask0 += 0x80000L; continue;
                    case 131074: t_wall_mask0 += 0x100000L; continue;
                    case 196610: t_wall_mask0 += 0x200000L; continue;
                    case 262146: t_wall_mask0 += 0x400000L; continue;
                    case 327682: t_wall_mask0 += 0x800000L; continue;
                    case 393218: t_wall_mask0 += 0x1000000L; continue;
                    case 458754: t_wall_mask0 += 0x2000000L; continue;
                    case 524290: t_wall_mask0 += 0x4000000L; continue;
                    case 3: t_wall_mask0 += 0x8000000L; continue;
                    case 65539: t_wall_mask0 += 0x10000000L; continue;
                    case 131075: t_wall_mask0 += 0x20000000L; continue;
                    case 196611: t_wall_mask0 += 0x40000000L; continue;
                    case 262147: t_wall_mask0 += 0x80000000L; continue;
                    case 327683: t_wall_mask0 += 0x100000000L; continue;
                    case 393219: t_wall_mask0 += 0x200000000L; continue;
                    case 458755: t_wall_mask0 += 0x400000000L; continue;
                    case 524291: t_wall_mask0 += 0x800000000L; continue;
                    case 4: t_wall_mask0 += 0x1000000000L; continue;
                    case 65540: t_wall_mask0 += 0x2000000000L; continue;
                    case 131076: t_wall_mask0 += 0x4000000000L; continue;
                    case 196612: t_wall_mask0 += 0x8000000000L; continue;
                    case 262148: t_wall_mask0 += 0x10000000000L; continue;
                    case 327684: t_wall_mask0 += 0x20000000000L; continue;
                    case 393220: t_wall_mask0 += 0x40000000000L; continue;
                    case 458756: t_wall_mask0 += 0x80000000000L; continue;
                    case 524292: t_wall_mask0 += 0x100000000000L; continue;
                    case 5: t_wall_mask0 += 0x200000000000L; continue;
                    case 65541: t_wall_mask0 += 0x400000000000L; continue;
                    case 131077: t_wall_mask0 += 0x800000000000L; continue;
                    case 196613: t_wall_mask0 += 0x1000000000000L; continue;
                    case 262149: t_wall_mask0 += 0x2000000000000L; continue;
                    case 327685: t_wall_mask0 += 0x4000000000000L; continue;
                    case 393221: t_wall_mask0 += 0x8000000000000L; continue;
                    case 458757: t_wall_mask0 += 0x10000000000000L; continue;
                    case 524293: t_wall_mask0 += 0x20000000000000L; continue;
                    case 6: t_wall_mask0 += 0x40000000000000L; continue;
                    case 65542: t_wall_mask0 += 0x80000000000000L; continue;
                    case 131078: t_wall_mask0 += 0x100000000000000L; continue;
                    case 196614: t_wall_mask0 += 0x200000000000000L; continue;
                    case 262150: t_wall_mask0 += 0x400000000000000L; continue;
                    case 327686: t_wall_mask0 += 0x800000000000000L; continue;
                    case 393222: t_wall_mask0 += 0x1000000000000000L; continue;
                    case 458758: t_wall_mask0 += 0x2000000000000000L; continue;
                    case 524294: t_wall_mask0 += 0x4000000000000000L; continue;
                    case 7: t_wall_mask1 += 0x1L; continue;
                    case 65543: t_wall_mask1 += 0x2L; continue;
                    case 131079: t_wall_mask1 += 0x4L; continue;
                    case 196615: t_wall_mask1 += 0x8L; continue;
                    case 262151: t_wall_mask1 += 0x10L; continue;
                    case 327687: t_wall_mask1 += 0x20L; continue;
                    case 393223: t_wall_mask1 += 0x40L; continue;
                    case 458759: t_wall_mask1 += 0x80L; continue;
                    case 524295: t_wall_mask1 += 0x100L; continue;
                    case 8: t_wall_mask1 += 0x200L; continue;
                    case 65544: t_wall_mask1 += 0x400L; continue;
                    case 131080: t_wall_mask1 += 0x800L; continue;
                    case 196616: t_wall_mask1 += 0x1000L; continue;
                    case 262152: t_wall_mask1 += 0x2000L; continue;
                    case 327688: t_wall_mask1 += 0x4000L; continue;
                    case 393224: t_wall_mask1 += 0x8000L; continue;
                    case 458760: t_wall_mask1 += 0x10000L; continue;
                    case 524296: t_wall_mask1 += 0x20000L; continue;
                    default: System.out.println("This shouldn't happen..."); continue;
                }
            }
            if (m.getTrapType() != TrapType.NONE) {
                switch (m.getMapLocation().hashCode() - offset) {
                    case 0: t_bomb_mask0 += 0x1L; continue;
                    case 65536: t_bomb_mask0 += 0x2L; continue;
                    case 131072: t_bomb_mask0 += 0x4L; continue;
                    case 196608: t_bomb_mask0 += 0x8L; continue;
                    case 262144: t_bomb_mask0 += 0x10L; continue;
                    case 327680: t_bomb_mask0 += 0x20L; continue;
                    case 393216: t_bomb_mask0 += 0x40L; continue;
                    case 458752: t_bomb_mask0 += 0x80L; continue;
                    case 524288: t_bomb_mask0 += 0x100L; continue;
                    case 1: t_bomb_mask0 += 0x200L; continue;
                    case 65537: t_bomb_mask0 += 0x400L; continue;
                    case 131073: t_bomb_mask0 += 0x800L; continue;
                    case 196609: t_bomb_mask0 += 0x1000L; continue;
                    case 262145: t_bomb_mask0 += 0x2000L; continue;
                    case 327681: t_bomb_mask0 += 0x4000L; continue;
                    case 393217: t_bomb_mask0 += 0x8000L; continue;
                    case 458753: t_bomb_mask0 += 0x10000L; continue;
                    case 524289: t_bomb_mask0 += 0x20000L; continue;
                    case 2: t_bomb_mask0 += 0x40000L; continue;
                    case 65538: t_bomb_mask0 += 0x80000L; continue;
                    case 131074: t_bomb_mask0 += 0x100000L; continue;
                    case 196610: t_bomb_mask0 += 0x200000L; continue;
                    case 262146: t_bomb_mask0 += 0x400000L; continue;
                    case 327682: t_bomb_mask0 += 0x800000L; continue;
                    case 393218: t_bomb_mask0 += 0x1000000L; continue;
                    case 458754: t_bomb_mask0 += 0x2000000L; continue;
                    case 524290: t_bomb_mask0 += 0x4000000L; continue;
                    case 3: t_bomb_mask0 += 0x8000000L; continue;
                    case 65539: t_bomb_mask0 += 0x10000000L; continue;
                    case 131075: t_bomb_mask0 += 0x20000000L; continue;
                    case 196611: t_bomb_mask0 += 0x40000000L; continue;
                    case 262147: t_bomb_mask0 += 0x80000000L; continue;
                    case 327683: t_bomb_mask0 += 0x100000000L; continue;
                    case 393219: t_bomb_mask0 += 0x200000000L; continue;
                    case 458755: t_bomb_mask0 += 0x400000000L; continue;
                    case 524291: t_bomb_mask0 += 0x800000000L; continue;
                    case 4: t_bomb_mask0 += 0x1000000000L; continue;
                    case 65540: t_bomb_mask0 += 0x2000000000L; continue;
                    case 131076: t_bomb_mask0 += 0x4000000000L; continue;
                    case 196612: t_bomb_mask0 += 0x8000000000L; continue;
                    case 262148: t_bomb_mask0 += 0x10000000000L; continue;
                    case 327684: t_bomb_mask0 += 0x20000000000L; continue;
                    case 393220: t_bomb_mask0 += 0x40000000000L; continue;
                    case 458756: t_bomb_mask0 += 0x80000000000L; continue;
                    case 524292: t_bomb_mask0 += 0x100000000000L; continue;
                    case 5: t_bomb_mask0 += 0x200000000000L; continue;
                    case 65541: t_bomb_mask0 += 0x400000000000L; continue;
                    case 131077: t_bomb_mask0 += 0x800000000000L; continue;
                    case 196613: t_bomb_mask0 += 0x1000000000000L; continue;
                    case 262149: t_bomb_mask0 += 0x2000000000000L; continue;
                    case 327685: t_bomb_mask0 += 0x4000000000000L; continue;
                    case 393221: t_bomb_mask0 += 0x8000000000000L; continue;
                    case 458757: t_bomb_mask0 += 0x10000000000000L; continue;
                    case 524293: t_bomb_mask0 += 0x20000000000000L; continue;
                    case 6: t_bomb_mask0 += 0x40000000000000L; continue;
                    case 65542: t_bomb_mask0 += 0x80000000000000L; continue;
                    case 131078: t_bomb_mask0 += 0x100000000000000L; continue;
                    case 196614: t_bomb_mask0 += 0x200000000000000L; continue;
                    case 262150: t_bomb_mask0 += 0x400000000000000L; continue;
                    case 327686: t_bomb_mask0 += 0x800000000000000L; continue;
                    case 393222: t_bomb_mask0 += 0x1000000000000000L; continue;
                    case 458758: t_bomb_mask0 += 0x2000000000000000L; continue;
                    case 524294: t_bomb_mask0 += 0x4000000000000000L; continue;
                    case 7: t_bomb_mask1 += 0x1L; continue;
                    case 65543: t_bomb_mask1 += 0x2L; continue;
                    case 131079: t_bomb_mask1 += 0x4L; continue;
                    case 196615: t_bomb_mask1 += 0x8L; continue;
                    case 262151: t_bomb_mask1 += 0x10L; continue;
                    case 327687: t_bomb_mask1 += 0x20L; continue;
                    case 393223: t_bomb_mask1 += 0x40L; continue;
                    case 458759: t_bomb_mask1 += 0x80L; continue;
                    case 524295: t_bomb_mask1 += 0x100L; continue;
                    case 8: t_bomb_mask1 += 0x200L; continue;
                    case 65544: t_bomb_mask1 += 0x400L; continue;
                    case 131080: t_bomb_mask1 += 0x800L; continue;
                    case 196616: t_bomb_mask1 += 0x1000L; continue;
                    case 262152: t_bomb_mask1 += 0x2000L; continue;
                    case 327688: t_bomb_mask1 += 0x4000L; continue;
                    case 393224: t_bomb_mask1 += 0x8000L; continue;
                    case 458760: t_bomb_mask1 += 0x10000L; continue;
                    case 524296: t_bomb_mask1 += 0x20000L; continue;
                    default: System.out.println("This shouldn't happen..."); continue;
                }
            }
        }
        
        switch (rc.getLocation().y) {
            case 3: t_wall_mask0 += 0x1ffL; break;
            case 2: t_wall_mask0 += 0x3ffffL; break;
            case 1: t_wall_mask0 += 0x7ffffffL; break;
            case 0: t_wall_mask0 += 0xfffffffffL; break;
        }
        switch (rc.getLocation().x) {
            case 3: t_wall_mask0 += 0x40201008040201L; t_wall_mask1 += 0x201L; break;
            case 2: t_wall_mask0 += 0xc06030180c0603L; t_wall_mask1 += 0x603L; break;
            case 1: t_wall_mask0 += 0x1c0e070381c0e07L; t_wall_mask1 += 0xe07L; break;
            case 0: t_wall_mask0 += 0x3c1e0f0783c1e0fL; t_wall_mask1 += 0x1e0fL; break;
        }
        switch (rc.getMapHeight() - rc.getLocation().y) {
            case 4: t_wall_mask1 += 0x3fe00L; break;
            case 3: t_wall_mask1 += 0x3ffffL; break;
            case 2: t_wall_mask1 += 0x3ffffL; t_wall_mask0 += 0x7fc0000000000000L; break;
            case 1: t_wall_mask1 += 0x3ffffL; t_wall_mask0 += 0x7fffe00000000000L; break;
        }
        switch (rc.getMapWidth() - rc.getLocation().x) {
            case 4: t_wall_mask0 += 0x4020100804020100L; t_wall_mask1 += 0x20100L; break;
            case 3: t_wall_mask0 += 0x6030180c06030180L; t_wall_mask1 += 0x30180L; break;
            case 2: t_wall_mask0 += 0x70381c0e070381c0L; t_wall_mask1 += 0x381c0L; break;
            case 1: t_wall_mask0 += 0x783c1e0f0783c1e0L; t_wall_mask1 += 0x3c1e0L; break;
        }
        
        if (!rc.canMove(Direction.NORTHWEST)) { blocked += 0x1000000000000L;}
        if (!rc.canMove(Direction.NORTH)) { blocked += 0x2000000000000L;}
        if (!rc.canMove(Direction.NORTHEAST)) { blocked += 0x4000000000000L;}
        if (!rc.canMove(Direction.EAST)) { blocked += 0x20000000000L;}
        if (!rc.canMove(Direction.WEST)) { blocked += 0x8000000000L;}
        if (!rc.canMove(Direction.SOUTHWEST)) { blocked += 0x40000000L;}
        if (!rc.canMove(Direction.SOUTH)) { blocked += 0x80000000L;}
        if (!rc.canMove(Direction.SOUTHEAST)) { blocked += 0x100000000L;}
        blocked ^= (blocked & t_water_mask0);
        mt.water_mask1 = t_water_mask1;
        mt.water_mask0 = t_water_mask0;
        mt.wall_mask1 = t_wall_mask1;
        mt.wall_mask0 = t_wall_mask0;
        mt.bomb_mask1 = t_bomb_mask1;
        mt.bomb_mask0 = t_bomb_mask0;
        mt.adjblocked = blocked;
        mt.infos = infos;
    }
}
