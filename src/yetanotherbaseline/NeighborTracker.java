package yetanotherbaseline;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

/*
 * This is merely a proof of concept.
 * For actual use, we'll what to make multiple NeighborLoaders,
 * And avoid loading in every single mask lol
 */

public class NeighborTracker {
    RobotController rc;
    static final int FE_MASK_WIDTH = 9;
    static final int FE_MASK_HEIGHT = 7;

    RobotInfo[] friends;
    RobotInfo[] enemies;

    long friend_amplifier_mask0 = 0;
    long friend_booster_mask0 = 0;
    long friend_carrier_mask0 = 0;
    long friend_destabilizer_mask0 = 0;
    long friend_headquarters_mask0 = 0;
    long friend_launcher_mask0 = 0;
    long friend_amplifier_mask1 = 0;
    long friend_booster_mask1 = 0;
    long friend_carrier_mask1 = 0;
    long friend_destabilizer_mask1 = 0;
    long friend_headquarters_mask1 = 0;
    long friend_launcher_mask1 = 0;
    
    long enemy_amplifier_mask0 = 0;
    long enemy_booster_mask0 = 0;
    long enemy_carrier_mask0 = 0;
    long enemy_destabilizer_mask0 = 0;
    long enemy_headquarters_mask0 = 0;
    long enemy_launcher_mask0 = 0;
    long enemy_amplifier_mask1 = 0;
    long enemy_booster_mask1 = 0;
    long enemy_carrier_mask1 = 0;
    long enemy_destabilizer_mask1 = 0;
    long enemy_headquarters_mask1 = 0;
    long enemy_launcher_mask1 = 0;

    public boolean hasLaunchersNear = false;
    public boolean hasCarriersNear = false;

    public NeighborTracker(RobotController rc) {
        this.rc = rc;
    }

    void updateNeighbors() throws GameActionException {
        int initial = Clock.getBytecodesLeft();
        NeighborLoader.load(this);
        hasLaunchersNear = ((enemy_launcher_mask0 | enemy_launcher_mask1) > 0);
        hasCarriersNear = ((enemy_carrier_mask0 | enemy_carrier_mask1) > 0);
        int end = Clock.getBytecodesLeft();
        System.out.println("Tracker Used: " + (initial - end - 200));
    }


    /*
     * Return the direction which minimizes the distance to attacked soldiers.
     * Ignores walls and cooldown for now.
     * We also ignore wraparound issues, but this may be ok, since we don't account for walls.
     * I.e the wraparound squares will never be closer then the squares that made them.
     * 
     * Also this isn't particularly optimized. The most obvious improvement - 
     * Change all statics to locals to avoid the extra loads.
     * Change the arrays to seperate variables so we don't have to load the reference every time.
     */

    Direction advance() throws GameActionException {
        return null;
    }

    void displayMap() throws GameActionException {
        long mask0 = friend_launcher_mask0;
        long mask1 = friend_launcher_mask1;
        MapLocation bl = (rc.getLocation()).translate(-4, -4);
        for (int i = FE_MASK_HEIGHT; i-- > 0;) {
            for (int j = FE_MASK_WIDTH; j-- > 0;) {
                if (Clock.getBytecodesLeft() < 1000) break;
                if ((mask0 >>> (i * FE_MASK_WIDTH + j + 1) & 1) > 0) {
                    rc.setIndicatorDot(bl.translate(j, i), 255, 0, 0);
                }
            }
        }

        bl = (rc.getLocation()).translate(-4, -4 + FE_MASK_HEIGHT);
        for (int i = 2; i-- > 0;) {
            for (int j = FE_MASK_WIDTH; j-- > 0;) {
                if (Clock.getBytecodesLeft() < 1000) break;
                if ((mask1 >>> (i * FE_MASK_WIDTH + j + 1) & 1) > 0) {
                    rc.setIndicatorDot(bl.translate(j, i), 255, 0, 0);
                }
            }
        }

        // rc.setIndicatorString("Map: " + Long.toBinaryString(mask0));
    }
}
