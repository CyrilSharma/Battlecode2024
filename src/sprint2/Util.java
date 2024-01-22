package sprint2;
import battlecode.common.*;

public class Util {
    public static void displayMask(RobotController rc, long mask0, long mask1) {
        int FE_MASK_WIDTH = 9;
        int FE_MASK_HEIGHT = 7;
        MapLocation bl = (rc.getLocation()).translate(-4, -4);
        for (int i = FE_MASK_HEIGHT; i-- > 0;) {
            for (int j = FE_MASK_WIDTH; j-- > 0;) {
                if (Clock.getBytecodesLeft() < 1000) break;
                if ((mask0 >>> (i * FE_MASK_WIDTH + j) & 1) > 0) {
                    rc.setIndicatorDot(bl.translate(j, i), 255, 0, 0);
                }
            }
        }

        bl = (rc.getLocation()).translate(-4, -4 + FE_MASK_HEIGHT);
        for (int i = 2; i-- > 0;) {
            for (int j = FE_MASK_WIDTH; j-- > 0;) {
                if (Clock.getBytecodesLeft() < 1000) break;
                if ((mask1 >>> (i * FE_MASK_WIDTH + j) & 1) > 0) {
                    rc.setIndicatorDot(bl.translate(j, i), 255, 0, 0);
                }
            }
        }
    }

    public static void displayMask(RobotController rc, long mask0, long mask1, int r, int g, int b) {
        int FE_MASK_WIDTH = 9;
        int FE_MASK_HEIGHT = 7;
        MapLocation bl = (rc.getLocation()).translate(-4, -4);
        for (int i = FE_MASK_HEIGHT; i-- > 0;) {
            for (int j = FE_MASK_WIDTH; j-- > 0;) {
                if (Clock.getBytecodesLeft() < 1000) break;
                if ((mask0 >>> (i * FE_MASK_WIDTH + j) & 1) > 0) {
                    rc.setIndicatorDot(bl.translate(j, i), r, g, b);
                }
            }
        }

        bl = (rc.getLocation()).translate(-4, -4 + FE_MASK_HEIGHT);
        for (int i = 2; i-- > 0;) {
            for (int j = FE_MASK_WIDTH; j-- > 0;) {
                if (Clock.getBytecodesLeft() < 1000) break;
                if ((mask1 >>> (i * FE_MASK_WIDTH + j) & 1) > 0) {
                    rc.setIndicatorDot(bl.translate(j, i), r, g, b);
                }
            }
        }
    }

    public static void printMask(long cur0, long cur1) {
        String s = "\n";
        for (int i = 2; i-- > 0;) {
            for (int j = 0; j < 9; j++) {
                s += (cur1 >> (9*i + j)) & 1;
            }
            s += "\n";
        }
        for (int i = 7; i-- > 0;) {
            for (int j = 0; j < 9; j++) {
                s += (cur0 >> (9*i + j)) & 1;
            }
            s += "\n";
        }
        System.out.println(s);
    } 
}