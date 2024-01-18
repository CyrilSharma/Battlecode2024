package initialBot;
import battlecode.common.*;

public class NeighborTracker {
    long friend_mask0 = 0;
    long friend_mask1 = 0;
    long enemy_mask0 = 0;
    long enemy_mask1 = 0;
    long friend_attack_mask0[] = new long[7];
    long friend_attack_mask1[] = new long[7];
    long friend_heal_mask0[] = new long[7];
    long friend_heal_mask1[] = new long[7];
    long enemy_attack_mask0[] = new long[7];
    long enemy_attack_mask1[] = new long[7];
    long enemy_heal_mask0[] = new long[7];
    long enemy_heal_mask1[] = new long[7];
    RobotInfo[] friends = null;
    RobotInfo[] enemies = null;
    RobotController rc;
    NeighborLoader nl;

    public NeighborTracker(RobotController rc) throws GameActionException {
        this.rc = rc;
        this.nl = new NeighborLoader(rc);
    }

    public void run() throws GameActionException {
        nl.load(this);
    }
}
