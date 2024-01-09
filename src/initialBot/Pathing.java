package initialBot;
import battlecode.common.*;


public class Pathing {
    RobotController rc;
    static int H, W;

    Pathing(RobotController rc) {
        this.rc = rc;
        H = rc.getMapHeight();
        W = rc.getMapWidth();
    }
}