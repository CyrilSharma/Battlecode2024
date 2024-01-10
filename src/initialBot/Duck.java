package initialBot;
import battlecode.common.*;

/*
 * in the future we may want to diversify this class into the multiple specializations.
 */

public class Duck extends Robot {
    Pathing path;
    MapLocation target;
    Exploration explore;
    public Duck(RobotController rc) {
        super(rc);
        path = new Pathing(this);
        explore = new Exploration(this);
    }

    void run() throws GameActionException {
        if (!rc.isSpawned()) spawn();
        if (!rc.isSpawned()) return;
        // if (rc.getRoundNum() <= 170){
        //     MapLocation target = explore.tryExplore();
        //     path.moveTo(target);
        // }
        tryAttack();
        if (shouldMicro()) doMicro();
        else seekTarget();
        tryDesperateAttack();
        // this.mt.displayLocalMasks();
    }

    void spawn() throws GameActionException {
        MapLocation[] spawns = rc.getAllySpawnLocations();
        int st = rng.nextInt(spawns.length);
        for (int i = spawns.length; i-- > 0;) {
            MapLocation loc = spawns[(i + st) % spawns.length];
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
