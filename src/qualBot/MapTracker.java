package qualBot;
import battlecode.common.*;

public class MapTracker {
    RobotController rc;
    TileLoader tl;
    long water_mask0 = 0;
    long water_mask1 = 0;
    long wall_mask0 = 0;
    long wall_mask1 = 0;
    long bomb_mask0 = 0;
    long bomb_mask1 = 0;
    long stun_mask0 = 0;
    long stun_mask1 = 0;
    // This is fully contained in the bottom mask.
    long adjblocked = 0;
    MapInfo[] infos = null;
    public MapTracker(RobotController rc) {
        this.rc = rc;
        this.tl = new TileLoader(rc);
    }

    public void run() throws GameActionException {
        // This can be kind of a chunky method,
        // So we keep it seperate.
        if (!rc.isSpawned()) return;
        int initial = Clock.getBytecodesLeft();
        tl.load(this);
        int end = Clock.getBytecodesLeft();
        // System.out.println("Used: " + (initial - end));
    }

    public void displayLocalMasks() throws GameActionException {
        // System.out.println("Printing masks...");
        System.out.println(String.format("wall_mask0: 0x%08X", wall_mask0));
        System.out.println(String.format("wall_mask1: 0x%08X", wall_mask1));
        System.out.println(String.format("water_mask0: 0x%08X", water_mask0));
        System.out.println(String.format("water_mask1: 0x%08X", water_mask1));

        rc.setIndicatorString(String.format("wall_mask0: 0x%08X", wall_mask0));

        int FE_MASK_WIDTH = 9;
        int FE_MASK_HEIGHT = 7;
        MapLocation bl = (rc.getLocation()).translate(-4, -4);
        for (int i = FE_MASK_HEIGHT; i-- > 0;) {
            for (int j = FE_MASK_WIDTH; j-- > 0;) {
                if (Clock.getBytecodesLeft() < 1000) break;
                if ((wall_mask0 >>> (i * FE_MASK_WIDTH + j) & 1) > 0) {
                    rc.setIndicatorDot(bl.translate(j, i), 0, 0, 0);
                } else if ((water_mask0 >>> (i * FE_MASK_WIDTH + j + 1) & 1) > 0) {
                    rc.setIndicatorDot(bl.translate(j, i), 0, 0, 255);
                }
            }
        }

        bl = (rc.getLocation()).translate(-4, -4 + FE_MASK_HEIGHT);
        for (int i = 2; i-- > 0;) {
            for (int j = FE_MASK_WIDTH; j-- > 0;) {
                if (Clock.getBytecodesLeft() < 1000) break;
                if ((wall_mask1 >>> (i * FE_MASK_WIDTH + j) & 1) > 0) {
                    rc.setIndicatorDot(bl.translate(j, i), 0, 0, 0);
                } else if ((water_mask1 >>> (i * FE_MASK_WIDTH + j) & 1) > 0) {
                    rc.setIndicatorDot(bl.translate(j, i), 0, 0, 255);
                }
            }
        }
    }
}
