package initialBot;
import battlecode.common.*;

/* 
 * in the future we may want to diversify this class into the multiple specializations.
 */

public class Duck extends Robot {
    public Duck(RobotController rc) {
        super(rc);
    }

    void run() throws GameActionException {
        if (!rc.isSpawned()) spawn();
        if (!rc.isSpawned()) return;

        tryAttack();
        if (shouldMicro()) doMicro();
        else seekTarget();
        tryDesperateAttack();
    }

    void spawn() throws GameActionException {
        MapLocation[] spawns = rc.getAllySpawnLocations();
        for (int i = spawns.length; i-- > 0;) {
            MapLocation loc = spawns[i];
            if (rc.canSpawn(loc)) {
                rc.spawn(loc);
                break;
            }
        }
    }

    boolean shouldMicro() throws GameActionException {
        return false;
    }

    void doMicro() throws GameActionException {}

    void tryAttack() throws GameActionException {
        return;
    }

    void tryDesperateAttack() throws GameActionException {
        return;
    }

    void seekTarget() throws GameActionException {
        int x = rng.nextInt(rc.getMapWidth());
        int y = rng.nextInt(rc.getMapHeight());
        MapLocation target = new MapLocation(x, y);
        
    }
}
