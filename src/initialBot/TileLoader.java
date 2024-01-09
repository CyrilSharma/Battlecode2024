package initialBot;
import battlecode.common.*;
public class TileLoader {
    RobotController rc;
    public TileLoader(RobotController rc) {
        this.rc = rc;
    }
    public void load(MapTracker mt) throws GameActionException {
        MapLocation myloc = rc.getLocation();
        long t_water_mask0 = 0;
        long t_water_mask1 = 0;
        long t_wall_mask0 = 0;
        long t_wall_mask1 = 0;
        long blocked = 0;
        MapLocation loc = null;
        MapInfo m = null;
        loc = myloc.translate(-2, -4);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x4L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x4L;
            }
        }
        loc = myloc.translate(-1, -4);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x8L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x8L;
            }
        }
        loc = myloc.translate(0, -4);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x10L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x10L;
            }
        }
        loc = myloc.translate(1, -4);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x20L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x20L;
            }
        }
        loc = myloc.translate(2, -4);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x40L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x40L;
            }
        }
        loc = myloc.translate(-3, -3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x400L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x400L;
            }
        }
        loc = myloc.translate(-2, -3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x800L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x800L;
            }
        }
        loc = myloc.translate(-1, -3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x1000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x1000L;
            }
        }
        loc = myloc.translate(0, -3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x2000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x2000L;
            }
        }
        loc = myloc.translate(1, -3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x4000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x4000L;
            }
        }
        loc = myloc.translate(2, -3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x8000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x8000L;
            }
        }
        loc = myloc.translate(3, -3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x10000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x10000L;
            }
        }
        loc = myloc.translate(-4, -2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x40000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x40000L;
            }
        }
        loc = myloc.translate(-3, -2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x80000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x80000L;
            }
        }
        loc = myloc.translate(-2, -2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x100000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x100000L;
            }
        }
        loc = myloc.translate(-1, -2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x200000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x200000L;
            }
        }
        loc = myloc.translate(0, -2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x400000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x400000L;
            }
        }
        loc = myloc.translate(1, -2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x800000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x800000L;
            }
        }
        loc = myloc.translate(2, -2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x1000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x1000000L;
            }
        }
        loc = myloc.translate(3, -2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x2000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x2000000L;
            }
        }
        loc = myloc.translate(4, -2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x4000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x4000000L;
            }
        }
        loc = myloc.translate(-4, -1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x8000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x8000000L;
            }
        }
        loc = myloc.translate(-3, -1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x10000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x10000000L;
            }
        }
        loc = myloc.translate(-2, -1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x20000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x20000000L;
            }
        }
        loc = myloc.translate(-1, -1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x40000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x40000000L;
            }
        }
        loc = myloc.translate(0, -1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x80000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x80000000L;
            }
        }
        loc = myloc.translate(1, -1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x100000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x100000000L;
            }
        }
        loc = myloc.translate(2, -1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x200000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x200000000L;
            }
        }
        loc = myloc.translate(3, -1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x400000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x400000000L;
            }
        }
        loc = myloc.translate(4, -1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x800000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x800000000L;
            }
        }
        loc = myloc.translate(-4, 0);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x1000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x1000000000L;
            }
        }
        loc = myloc.translate(-3, 0);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x2000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x2000000000L;
            }
        }
        loc = myloc.translate(-2, 0);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x4000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x4000000000L;
            }
        }
        loc = myloc.translate(-1, 0);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x8000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x8000000000L;
            }
        }
        loc = myloc.translate(0, 0);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x10000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x10000000000L;
            }
        }
        loc = myloc.translate(1, 0);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x20000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x20000000000L;
            }
        }
        loc = myloc.translate(2, 0);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x40000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x40000000000L;
            }
        }
        loc = myloc.translate(3, 0);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x80000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x80000000000L;
            }
        }
        loc = myloc.translate(4, 0);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x100000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x100000000000L;
            }
        }
        loc = myloc.translate(-4, 1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x200000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x200000000000L;
            }
        }
        loc = myloc.translate(-3, 1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x400000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x400000000000L;
            }
        }
        loc = myloc.translate(-2, 1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x800000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x800000000000L;
            }
        }
        loc = myloc.translate(-1, 1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x1000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x1000000000000L;
            }
        }
        loc = myloc.translate(0, 1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x2000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x2000000000000L;
            }
        }
        loc = myloc.translate(1, 1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x4000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x4000000000000L;
            }
        }
        loc = myloc.translate(2, 1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x8000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x8000000000000L;
            }
        }
        loc = myloc.translate(3, 1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x10000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x10000000000000L;
            }
        }
        loc = myloc.translate(4, 1);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x20000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x20000000000000L;
            }
        }
        loc = myloc.translate(-4, 2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x40000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x40000000000000L;
            }
        }
        loc = myloc.translate(-3, 2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x80000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x80000000000000L;
            }
        }
        loc = myloc.translate(-2, 2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x100000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x100000000000000L;
            }
        }
        loc = myloc.translate(-1, 2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x200000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x200000000000000L;
            }
        }
        loc = myloc.translate(0, 2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x400000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x400000000000000L;
            }
        }
        loc = myloc.translate(1, 2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x800000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x800000000000000L;
            }
        }
        loc = myloc.translate(2, 2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x1000000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x1000000000000000L;
            }
        }
        loc = myloc.translate(3, 2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x2000000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x2000000000000000L;
            }
        }
        loc = myloc.translate(4, 2);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask0 += 0x4000000000000000L;
            } else if (m.isWall()) {
                t_wall_mask0 += 0x4000000000000000L;
            }
        }
        loc = myloc.translate(-3, 3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x1L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x1L;
            }
        }
        loc = myloc.translate(-2, 3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x2L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x2L;
            }
        }
        loc = myloc.translate(-1, 3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x4L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x4L;
            }
        }
        loc = myloc.translate(0, 3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x8L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x8L;
            }
        }
        loc = myloc.translate(1, 3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x10L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x10L;
            }
        }
        loc = myloc.translate(2, 3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x20L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x20L;
            }
        }
        loc = myloc.translate(3, 3);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x40L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x40L;
            }
        }
        loc = myloc.translate(-2, 4);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x400L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x400L;
            }
        }
        loc = myloc.translate(-1, 4);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x800L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x800L;
            }
        }
        loc = myloc.translate(0, 4);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x1000L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x1000L;
            }
        }
        loc = myloc.translate(1, 4);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x2000L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x2000L;
            }
        }
        loc = myloc.translate(2, 4);
        if (rc.canSenseLocation(loc)) {
            m = rc.senseMapInfo(loc);
            if (m.isWater()) {
                t_water_mask1 += 0x4000L;
            } else if (m.isWall()) {
                t_wall_mask1 += 0x4000L;
            }
        }
        
        if (!rc.canMove(Direction.NORTHWEST)) { blocked += 1125899906842624L;}
        if (!rc.canMove(Direction.NORTH)) { blocked += 562949953421312L;}
        if (!rc.canMove(Direction.NORTHEAST)) { blocked += 281474976710656L;}
        if (!rc.canMove(Direction.EAST)) { blocked += 2199023255552L;}
        if (!rc.canMove(Direction.WEST)) { blocked += 549755813888L;}
        if (!rc.canMove(Direction.SOUTHWEST)) { blocked += 4294967296L;}
        if (!rc.canMove(Direction.SOUTH)) { blocked += 2147483648L;}
        if (!rc.canMove(Direction.SOUTHEAST)) { blocked += 1073741824L;}
        mt.water_mask1 = t_water_mask1;
        mt.water_mask0 = t_water_mask0;
        mt.wall_mask1 = t_wall_mask1;
        mt.wall_mask0 = t_wall_mask0;
        mt.adjblocked = blocked;
    }
}
