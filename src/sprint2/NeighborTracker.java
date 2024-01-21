package sprint2;
import battlecode.common.*;

public class NeighborTracker {
    long enemy_mask0 = 0;
    long enemy_mask1 = 0;
    long friend_mask0 = 0;
    long friend_mask1 = 0;
    RobotInfo[] friends = null;
    RobotInfo[] enemies = null;
    NeighborLoader nl;

    public NeighborTracker(RobotController rc) {
        this.nl = new NeighborLoader(rc);
    }

    public void run() throws GameActionException {
        nl.load(this);
    }
}
