package initialBot;
import battlecode.common.*;

public class Util {
    public static void displayMask(RobotController rc, long mask0, long mask1) throws GameActionException {
        int dots = 0;
        int FE_MASK_WIDTH = 9;
        int FE_MASK_HEIGHT = 7;
        MapLocation bl = (rc.getLocation()).translate(-4, -4);
        for (int i = FE_MASK_HEIGHT; i-- > 0;) {
            for (int j = FE_MASK_WIDTH; j-- > 0;) {
                if (Clock.getBytecodesLeft() < 1000) break;
                if ((mask0 >>> (i * FE_MASK_WIDTH + j) & 1) > 0) {
                    if (rc.getRoundNum() % 81 == dots) rc.setIndicatorDot(bl.translate(j, i), 255, 0, 0);
                    dots++;
                }
            }
        }

        bl = (rc.getLocation()).translate(-4, -4 + FE_MASK_HEIGHT);
        for (int i = 2; i-- > 0;) {
            for (int j = FE_MASK_WIDTH; j-- > 0;) {
                if (Clock.getBytecodesLeft() < 1000) break;
                if ((mask1 >>> (i * FE_MASK_WIDTH + j) & 1) > 0) {
                    if (rc.getRoundNum() % 81 == dots) rc.setIndicatorDot(bl.translate(j, i), 255, 0, 0);
                    dots++;
                }
            }
        }
    }    
}