package initialBot;
import battlecode.common.*;

/* 
 * in the future we may want to diversify this class into the multiple specializations.
 */

public class Duck extends Robot {
    Pathing path;
    MapLocation target;
    public Duck(RobotController rc) {
        super(rc);
        path = new Pathing(this);
    }

    void run() throws GameActionException {
        if (!rc.isSpawned()) spawn();
        if (!rc.isSpawned()) return;

        tryAttack();
        if (shouldMicro()) doMicro();
        else seekTarget();
        tryDesperateAttack();
        // this.mt.displayLocalMasks();
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
        mt.run();
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

    /*
     *  This is just here so I can test out some funky pathing.
     */

    void seekTarget() throws GameActionException {
        if ((target == null) || (rc.getRoundNum() % 100 == 0)) {
            // int x = rng.nextInt(rc.getMapWidth());
            // int y = rng.nextInt(rc.getMapHeight());
            // target = new MapLocation(x, y);
            MapLocation[] targets = {
                new MapLocation(0, 0),
                new MapLocation(0, rc.getMapHeight()),
                new MapLocation(rc.getMapWidth(), 0),
                new MapLocation(rc.getMapWidth(), rc.getMapHeight())
            };
            target = targets[(rc.getRoundNum() / 100) % 4];
        }
        path.moveTo(target);
    }
}
