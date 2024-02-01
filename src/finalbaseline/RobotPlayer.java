package finalbaseline;
import battlecode.common.*;

public strictfp class RobotPlayer {
    public static void run(RobotController rc) throws GameActionException {
        Robot robot = new Duck(rc);
        while (true) {
            try {
                // PURELY FOR TESTING PLEASE DISABLE.
                // if (rc.getRoundNum() > 500) {
                //     rc.resign();
                // }
                robot.init_turn();
                robot.run();
                robot.post_turn();
            } catch (GameActionException e) {
                System.out.println("Exception = ");
                e.printStackTrace();
            } finally {
                Clock.yield();
            }
        }
    }
}
