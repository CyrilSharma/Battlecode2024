package sprint2;
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
        long t_stun_mask0 = 0;
        long t_stun_mask1 = 0;
        long blocked = 0;
        MapInfo[] infos = mt.rc.senseNearbyMapInfos();
        for (int j = infos.length; j-- > 0; ) {
            MapInfo m = infos[j];
            if (m.isWater()) {
                switch (m.getMapLocation().hashCode() - offset) {
                    case 0: t_water_mask0 += 0x1L; break;
                    case 65536: t_water_mask0 += 0x2L; break;
                    case 131072: t_water_mask0 += 0x4L; break;
                    case 196608: t_water_mask0 += 0x8L; break;
                    case 262144: t_water_mask0 += 0x10L; break;
                    case 327680: t_water_mask0 += 0x20L; break;
                    case 393216: t_water_mask0 += 0x40L; break;
                    case 458752: t_water_mask0 += 0x80L; break;
                    case 524288: t_water_mask0 += 0x100L; break;
                    case 1: t_water_mask0 += 0x200L; break;
                    case 65537: t_water_mask0 += 0x400L; break;
                    case 131073: t_water_mask0 += 0x800L; break;
                    case 196609: t_water_mask0 += 0x1000L; break;
                    case 262145: t_water_mask0 += 0x2000L; break;
                    case 327681: t_water_mask0 += 0x4000L; break;
                    case 393217: t_water_mask0 += 0x8000L; break;
                    case 458753: t_water_mask0 += 0x10000L; break;
                    case 524289: t_water_mask0 += 0x20000L; break;
                    case 2: t_water_mask0 += 0x40000L; break;
                    case 65538: t_water_mask0 += 0x80000L; break;
                    case 131074: t_water_mask0 += 0x100000L; break;
                    case 196610: t_water_mask0 += 0x200000L; break;
                    case 262146: t_water_mask0 += 0x400000L; break;
                    case 327682: t_water_mask0 += 0x800000L; break;
                    case 393218: t_water_mask0 += 0x1000000L; break;
                    case 458754: t_water_mask0 += 0x2000000L; break;
                    case 524290: t_water_mask0 += 0x4000000L; break;
                    case 3: t_water_mask0 += 0x8000000L; break;
                    case 65539: t_water_mask0 += 0x10000000L; break;
                    case 131075: t_water_mask0 += 0x20000000L; break;
                    case 196611: t_water_mask0 += 0x40000000L; break;
                    case 262147: t_water_mask0 += 0x80000000L; break;
                    case 327683: t_water_mask0 += 0x100000000L; break;
                    case 393219: t_water_mask0 += 0x200000000L; break;
                    case 458755: t_water_mask0 += 0x400000000L; break;
                    case 524291: t_water_mask0 += 0x800000000L; break;
                    case 4: t_water_mask0 += 0x1000000000L; break;
                    case 65540: t_water_mask0 += 0x2000000000L; break;
                    case 131076: t_water_mask0 += 0x4000000000L; break;
                    case 196612: t_water_mask0 += 0x8000000000L; break;
                    case 262148: t_water_mask0 += 0x10000000000L; break;
                    case 327684: t_water_mask0 += 0x20000000000L; break;
                    case 393220: t_water_mask0 += 0x40000000000L; break;
                    case 458756: t_water_mask0 += 0x80000000000L; break;
                    case 524292: t_water_mask0 += 0x100000000000L; break;
                    case 5: t_water_mask0 += 0x200000000000L; break;
                    case 65541: t_water_mask0 += 0x400000000000L; break;
                    case 131077: t_water_mask0 += 0x800000000000L; break;
                    case 196613: t_water_mask0 += 0x1000000000000L; break;
                    case 262149: t_water_mask0 += 0x2000000000000L; break;
                    case 327685: t_water_mask0 += 0x4000000000000L; break;
                    case 393221: t_water_mask0 += 0x8000000000000L; break;
                    case 458757: t_water_mask0 += 0x10000000000000L; break;
                    case 524293: t_water_mask0 += 0x20000000000000L; break;
                    case 6: t_water_mask0 += 0x40000000000000L; break;
                    case 65542: t_water_mask0 += 0x80000000000000L; break;
                    case 131078: t_water_mask0 += 0x100000000000000L; break;
                    case 196614: t_water_mask0 += 0x200000000000000L; break;
                    case 262150: t_water_mask0 += 0x400000000000000L; break;
                    case 327686: t_water_mask0 += 0x800000000000000L; break;
                    case 393222: t_water_mask0 += 0x1000000000000000L; break;
                    case 458758: t_water_mask0 += 0x2000000000000000L; break;
                    case 524294: t_water_mask0 += 0x4000000000000000L; break;
                    case 7: t_water_mask1 += 0x1L; break;
                    case 65543: t_water_mask1 += 0x2L; break;
                    case 131079: t_water_mask1 += 0x4L; break;
                    case 196615: t_water_mask1 += 0x8L; break;
                    case 262151: t_water_mask1 += 0x10L; break;
                    case 327687: t_water_mask1 += 0x20L; break;
                    case 393223: t_water_mask1 += 0x40L; break;
                    case 458759: t_water_mask1 += 0x80L; break;
                    case 524295: t_water_mask1 += 0x100L; break;
                    case 8: t_water_mask1 += 0x200L; break;
                    case 65544: t_water_mask1 += 0x400L; break;
                    case 131080: t_water_mask1 += 0x800L; break;
                    case 196616: t_water_mask1 += 0x1000L; break;
                    case 262152: t_water_mask1 += 0x2000L; break;
                    case 327688: t_water_mask1 += 0x4000L; break;
                    case 393224: t_water_mask1 += 0x8000L; break;
                    case 458760: t_water_mask1 += 0x10000L; break;
                    case 524296: t_water_mask1 += 0x20000L; break;
                    default: System.out.println("This shouldn't happen..."); break;
                }
            } else if (m.isWall()) {
                switch (m.getMapLocation().hashCode() - offset) {
                    case 0: t_wall_mask0 += 0x1L; break;
                    case 65536: t_wall_mask0 += 0x2L; break;
                    case 131072: t_wall_mask0 += 0x4L; break;
                    case 196608: t_wall_mask0 += 0x8L; break;
                    case 262144: t_wall_mask0 += 0x10L; break;
                    case 327680: t_wall_mask0 += 0x20L; break;
                    case 393216: t_wall_mask0 += 0x40L; break;
                    case 458752: t_wall_mask0 += 0x80L; break;
                    case 524288: t_wall_mask0 += 0x100L; break;
                    case 1: t_wall_mask0 += 0x200L; break;
                    case 65537: t_wall_mask0 += 0x400L; break;
                    case 131073: t_wall_mask0 += 0x800L; break;
                    case 196609: t_wall_mask0 += 0x1000L; break;
                    case 262145: t_wall_mask0 += 0x2000L; break;
                    case 327681: t_wall_mask0 += 0x4000L; break;
                    case 393217: t_wall_mask0 += 0x8000L; break;
                    case 458753: t_wall_mask0 += 0x10000L; break;
                    case 524289: t_wall_mask0 += 0x20000L; break;
                    case 2: t_wall_mask0 += 0x40000L; break;
                    case 65538: t_wall_mask0 += 0x80000L; break;
                    case 131074: t_wall_mask0 += 0x100000L; break;
                    case 196610: t_wall_mask0 += 0x200000L; break;
                    case 262146: t_wall_mask0 += 0x400000L; break;
                    case 327682: t_wall_mask0 += 0x800000L; break;
                    case 393218: t_wall_mask0 += 0x1000000L; break;
                    case 458754: t_wall_mask0 += 0x2000000L; break;
                    case 524290: t_wall_mask0 += 0x4000000L; break;
                    case 3: t_wall_mask0 += 0x8000000L; break;
                    case 65539: t_wall_mask0 += 0x10000000L; break;
                    case 131075: t_wall_mask0 += 0x20000000L; break;
                    case 196611: t_wall_mask0 += 0x40000000L; break;
                    case 262147: t_wall_mask0 += 0x80000000L; break;
                    case 327683: t_wall_mask0 += 0x100000000L; break;
                    case 393219: t_wall_mask0 += 0x200000000L; break;
                    case 458755: t_wall_mask0 += 0x400000000L; break;
                    case 524291: t_wall_mask0 += 0x800000000L; break;
                    case 4: t_wall_mask0 += 0x1000000000L; break;
                    case 65540: t_wall_mask0 += 0x2000000000L; break;
                    case 131076: t_wall_mask0 += 0x4000000000L; break;
                    case 196612: t_wall_mask0 += 0x8000000000L; break;
                    case 262148: t_wall_mask0 += 0x10000000000L; break;
                    case 327684: t_wall_mask0 += 0x20000000000L; break;
                    case 393220: t_wall_mask0 += 0x40000000000L; break;
                    case 458756: t_wall_mask0 += 0x80000000000L; break;
                    case 524292: t_wall_mask0 += 0x100000000000L; break;
                    case 5: t_wall_mask0 += 0x200000000000L; break;
                    case 65541: t_wall_mask0 += 0x400000000000L; break;
                    case 131077: t_wall_mask0 += 0x800000000000L; break;
                    case 196613: t_wall_mask0 += 0x1000000000000L; break;
                    case 262149: t_wall_mask0 += 0x2000000000000L; break;
                    case 327685: t_wall_mask0 += 0x4000000000000L; break;
                    case 393221: t_wall_mask0 += 0x8000000000000L; break;
                    case 458757: t_wall_mask0 += 0x10000000000000L; break;
                    case 524293: t_wall_mask0 += 0x20000000000000L; break;
                    case 6: t_wall_mask0 += 0x40000000000000L; break;
                    case 65542: t_wall_mask0 += 0x80000000000000L; break;
                    case 131078: t_wall_mask0 += 0x100000000000000L; break;
                    case 196614: t_wall_mask0 += 0x200000000000000L; break;
                    case 262150: t_wall_mask0 += 0x400000000000000L; break;
                    case 327686: t_wall_mask0 += 0x800000000000000L; break;
                    case 393222: t_wall_mask0 += 0x1000000000000000L; break;
                    case 458758: t_wall_mask0 += 0x2000000000000000L; break;
                    case 524294: t_wall_mask0 += 0x4000000000000000L; break;
                    case 7: t_wall_mask1 += 0x1L; break;
                    case 65543: t_wall_mask1 += 0x2L; break;
                    case 131079: t_wall_mask1 += 0x4L; break;
                    case 196615: t_wall_mask1 += 0x8L; break;
                    case 262151: t_wall_mask1 += 0x10L; break;
                    case 327687: t_wall_mask1 += 0x20L; break;
                    case 393223: t_wall_mask1 += 0x40L; break;
                    case 458759: t_wall_mask1 += 0x80L; break;
                    case 524295: t_wall_mask1 += 0x100L; break;
                    case 8: t_wall_mask1 += 0x200L; break;
                    case 65544: t_wall_mask1 += 0x400L; break;
                    case 131080: t_wall_mask1 += 0x800L; break;
                    case 196616: t_wall_mask1 += 0x1000L; break;
                    case 262152: t_wall_mask1 += 0x2000L; break;
                    case 327688: t_wall_mask1 += 0x4000L; break;
                    case 393224: t_wall_mask1 += 0x8000L; break;
                    case 458760: t_wall_mask1 += 0x10000L; break;
                    case 524296: t_wall_mask1 += 0x20000L; break;
                    default: System.out.println("This shouldn't happen..."); break;
                }
            }
            switch (m.getTrapType()) {
                case EXPLOSIVE:
                    switch (m.getMapLocation().hashCode() - offset) {
                        case 0: t_bomb_mask0 += 0x1L; break;
                        case 65536: t_bomb_mask0 += 0x2L; break;
                        case 131072: t_bomb_mask0 += 0x4L; break;
                        case 196608: t_bomb_mask0 += 0x8L; break;
                        case 262144: t_bomb_mask0 += 0x10L; break;
                        case 327680: t_bomb_mask0 += 0x20L; break;
                        case 393216: t_bomb_mask0 += 0x40L; break;
                        case 458752: t_bomb_mask0 += 0x80L; break;
                        case 524288: t_bomb_mask0 += 0x100L; break;
                        case 1: t_bomb_mask0 += 0x200L; break;
                        case 65537: t_bomb_mask0 += 0x400L; break;
                        case 131073: t_bomb_mask0 += 0x800L; break;
                        case 196609: t_bomb_mask0 += 0x1000L; break;
                        case 262145: t_bomb_mask0 += 0x2000L; break;
                        case 327681: t_bomb_mask0 += 0x4000L; break;
                        case 393217: t_bomb_mask0 += 0x8000L; break;
                        case 458753: t_bomb_mask0 += 0x10000L; break;
                        case 524289: t_bomb_mask0 += 0x20000L; break;
                        case 2: t_bomb_mask0 += 0x40000L; break;
                        case 65538: t_bomb_mask0 += 0x80000L; break;
                        case 131074: t_bomb_mask0 += 0x100000L; break;
                        case 196610: t_bomb_mask0 += 0x200000L; break;
                        case 262146: t_bomb_mask0 += 0x400000L; break;
                        case 327682: t_bomb_mask0 += 0x800000L; break;
                        case 393218: t_bomb_mask0 += 0x1000000L; break;
                        case 458754: t_bomb_mask0 += 0x2000000L; break;
                        case 524290: t_bomb_mask0 += 0x4000000L; break;
                        case 3: t_bomb_mask0 += 0x8000000L; break;
                        case 65539: t_bomb_mask0 += 0x10000000L; break;
                        case 131075: t_bomb_mask0 += 0x20000000L; break;
                        case 196611: t_bomb_mask0 += 0x40000000L; break;
                        case 262147: t_bomb_mask0 += 0x80000000L; break;
                        case 327683: t_bomb_mask0 += 0x100000000L; break;
                        case 393219: t_bomb_mask0 += 0x200000000L; break;
                        case 458755: t_bomb_mask0 += 0x400000000L; break;
                        case 524291: t_bomb_mask0 += 0x800000000L; break;
                        case 4: t_bomb_mask0 += 0x1000000000L; break;
                        case 65540: t_bomb_mask0 += 0x2000000000L; break;
                        case 131076: t_bomb_mask0 += 0x4000000000L; break;
                        case 196612: t_bomb_mask0 += 0x8000000000L; break;
                        case 262148: t_bomb_mask0 += 0x10000000000L; break;
                        case 327684: t_bomb_mask0 += 0x20000000000L; break;
                        case 393220: t_bomb_mask0 += 0x40000000000L; break;
                        case 458756: t_bomb_mask0 += 0x80000000000L; break;
                        case 524292: t_bomb_mask0 += 0x100000000000L; break;
                        case 5: t_bomb_mask0 += 0x200000000000L; break;
                        case 65541: t_bomb_mask0 += 0x400000000000L; break;
                        case 131077: t_bomb_mask0 += 0x800000000000L; break;
                        case 196613: t_bomb_mask0 += 0x1000000000000L; break;
                        case 262149: t_bomb_mask0 += 0x2000000000000L; break;
                        case 327685: t_bomb_mask0 += 0x4000000000000L; break;
                        case 393221: t_bomb_mask0 += 0x8000000000000L; break;
                        case 458757: t_bomb_mask0 += 0x10000000000000L; break;
                        case 524293: t_bomb_mask0 += 0x20000000000000L; break;
                        case 6: t_bomb_mask0 += 0x40000000000000L; break;
                        case 65542: t_bomb_mask0 += 0x80000000000000L; break;
                        case 131078: t_bomb_mask0 += 0x100000000000000L; break;
                        case 196614: t_bomb_mask0 += 0x200000000000000L; break;
                        case 262150: t_bomb_mask0 += 0x400000000000000L; break;
                        case 327686: t_bomb_mask0 += 0x800000000000000L; break;
                        case 393222: t_bomb_mask0 += 0x1000000000000000L; break;
                        case 458758: t_bomb_mask0 += 0x2000000000000000L; break;
                        case 524294: t_bomb_mask0 += 0x4000000000000000L; break;
                        case 7: t_bomb_mask1 += 0x1L; break;
                        case 65543: t_bomb_mask1 += 0x2L; break;
                        case 131079: t_bomb_mask1 += 0x4L; break;
                        case 196615: t_bomb_mask1 += 0x8L; break;
                        case 262151: t_bomb_mask1 += 0x10L; break;
                        case 327687: t_bomb_mask1 += 0x20L; break;
                        case 393223: t_bomb_mask1 += 0x40L; break;
                        case 458759: t_bomb_mask1 += 0x80L; break;
                        case 524295: t_bomb_mask1 += 0x100L; break;
                        case 8: t_bomb_mask1 += 0x200L; break;
                        case 65544: t_bomb_mask1 += 0x400L; break;
                        case 131080: t_bomb_mask1 += 0x800L; break;
                        case 196616: t_bomb_mask1 += 0x1000L; break;
                        case 262152: t_bomb_mask1 += 0x2000L; break;
                        case 327688: t_bomb_mask1 += 0x4000L; break;
                        case 393224: t_bomb_mask1 += 0x8000L; break;
                        case 458760: t_bomb_mask1 += 0x10000L; break;
                        case 524296: t_bomb_mask1 += 0x20000L; break;
                        default: System.out.println("This shouldn't happen..."); break;
                    }
                case STUN:
                    switch (m.getMapLocation().hashCode() - offset) {
                        case 0: t_stun_mask0 += 0x1L; break;
                        case 65536: t_stun_mask0 += 0x2L; break;
                        case 131072: t_stun_mask0 += 0x4L; break;
                        case 196608: t_stun_mask0 += 0x8L; break;
                        case 262144: t_stun_mask0 += 0x10L; break;
                        case 327680: t_stun_mask0 += 0x20L; break;
                        case 393216: t_stun_mask0 += 0x40L; break;
                        case 458752: t_stun_mask0 += 0x80L; break;
                        case 524288: t_stun_mask0 += 0x100L; break;
                        case 1: t_stun_mask0 += 0x200L; break;
                        case 65537: t_stun_mask0 += 0x400L; break;
                        case 131073: t_stun_mask0 += 0x800L; break;
                        case 196609: t_stun_mask0 += 0x1000L; break;
                        case 262145: t_stun_mask0 += 0x2000L; break;
                        case 327681: t_stun_mask0 += 0x4000L; break;
                        case 393217: t_stun_mask0 += 0x8000L; break;
                        case 458753: t_stun_mask0 += 0x10000L; break;
                        case 524289: t_stun_mask0 += 0x20000L; break;
                        case 2: t_stun_mask0 += 0x40000L; break;
                        case 65538: t_stun_mask0 += 0x80000L; break;
                        case 131074: t_stun_mask0 += 0x100000L; break;
                        case 196610: t_stun_mask0 += 0x200000L; break;
                        case 262146: t_stun_mask0 += 0x400000L; break;
                        case 327682: t_stun_mask0 += 0x800000L; break;
                        case 393218: t_stun_mask0 += 0x1000000L; break;
                        case 458754: t_stun_mask0 += 0x2000000L; break;
                        case 524290: t_stun_mask0 += 0x4000000L; break;
                        case 3: t_stun_mask0 += 0x8000000L; break;
                        case 65539: t_stun_mask0 += 0x10000000L; break;
                        case 131075: t_stun_mask0 += 0x20000000L; break;
                        case 196611: t_stun_mask0 += 0x40000000L; break;
                        case 262147: t_stun_mask0 += 0x80000000L; break;
                        case 327683: t_stun_mask0 += 0x100000000L; break;
                        case 393219: t_stun_mask0 += 0x200000000L; break;
                        case 458755: t_stun_mask0 += 0x400000000L; break;
                        case 524291: t_stun_mask0 += 0x800000000L; break;
                        case 4: t_stun_mask0 += 0x1000000000L; break;
                        case 65540: t_stun_mask0 += 0x2000000000L; break;
                        case 131076: t_stun_mask0 += 0x4000000000L; break;
                        case 196612: t_stun_mask0 += 0x8000000000L; break;
                        case 262148: t_stun_mask0 += 0x10000000000L; break;
                        case 327684: t_stun_mask0 += 0x20000000000L; break;
                        case 393220: t_stun_mask0 += 0x40000000000L; break;
                        case 458756: t_stun_mask0 += 0x80000000000L; break;
                        case 524292: t_stun_mask0 += 0x100000000000L; break;
                        case 5: t_stun_mask0 += 0x200000000000L; break;
                        case 65541: t_stun_mask0 += 0x400000000000L; break;
                        case 131077: t_stun_mask0 += 0x800000000000L; break;
                        case 196613: t_stun_mask0 += 0x1000000000000L; break;
                        case 262149: t_stun_mask0 += 0x2000000000000L; break;
                        case 327685: t_stun_mask0 += 0x4000000000000L; break;
                        case 393221: t_stun_mask0 += 0x8000000000000L; break;
                        case 458757: t_stun_mask0 += 0x10000000000000L; break;
                        case 524293: t_stun_mask0 += 0x20000000000000L; break;
                        case 6: t_stun_mask0 += 0x40000000000000L; break;
                        case 65542: t_stun_mask0 += 0x80000000000000L; break;
                        case 131078: t_stun_mask0 += 0x100000000000000L; break;
                        case 196614: t_stun_mask0 += 0x200000000000000L; break;
                        case 262150: t_stun_mask0 += 0x400000000000000L; break;
                        case 327686: t_stun_mask0 += 0x800000000000000L; break;
                        case 393222: t_stun_mask0 += 0x1000000000000000L; break;
                        case 458758: t_stun_mask0 += 0x2000000000000000L; break;
                        case 524294: t_stun_mask0 += 0x4000000000000000L; break;
                        case 7: t_stun_mask1 += 0x1L; break;
                        case 65543: t_stun_mask1 += 0x2L; break;
                        case 131079: t_stun_mask1 += 0x4L; break;
                        case 196615: t_stun_mask1 += 0x8L; break;
                        case 262151: t_stun_mask1 += 0x10L; break;
                        case 327687: t_stun_mask1 += 0x20L; break;
                        case 393223: t_stun_mask1 += 0x40L; break;
                        case 458759: t_stun_mask1 += 0x80L; break;
                        case 524295: t_stun_mask1 += 0x100L; break;
                        case 8: t_stun_mask1 += 0x200L; break;
                        case 65544: t_stun_mask1 += 0x400L; break;
                        case 131080: t_stun_mask1 += 0x800L; break;
                        case 196616: t_stun_mask1 += 0x1000L; break;
                        case 262152: t_stun_mask1 += 0x2000L; break;
                        case 327688: t_stun_mask1 += 0x4000L; break;
                        case 393224: t_stun_mask1 += 0x8000L; break;
                        case 458760: t_stun_mask1 += 0x10000L; break;
                        case 524296: t_stun_mask1 += 0x20000L; break;
                        default: System.out.println("This shouldn't happen..."); break;
                    }
                default: break;
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
        mt.stun_mask1 = t_stun_mask1;
        mt.stun_mask0 = t_stun_mask0;
        mt.adjblocked = blocked;
        mt.infos = infos;
    }
}
