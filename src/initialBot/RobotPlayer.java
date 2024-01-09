package initialBot;
import battlecode.common.*;

public strictfp class RobotPlayer {
    public static void run(RobotController rc) throws GameActionException {
        Robot robot = new Duck(rc);
        while (true) {
            try {
                // if (rc.getRoundNum() > 200 && rc.getRobotCount() <= 5) {
                //     rc.resign();
                // }
                robot.run();
            } catch (GameActionException e) {
                System.out.println("Exception = ");
                e.printStackTrace();
            } finally {
                Clock.yield();
            }
        }
    }
}
