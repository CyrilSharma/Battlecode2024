package initialBotOld;
import java.util.Random;

import battlecode.common.*;

public class Exploration {
    RobotController rc;
    MapLocation target;
    Pathing path;
    int minDist;
    int notCloser;

    final Random rng = new Random();
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };
    public Exploration(Robot robot){
        this.rc = robot.rc;
        target = new MapLocation(rng.nextInt() % rc.getMapWidth(), rng.nextInt() % rc.getMapHeight());
        minDist = (rc.getLocation() != null ? rc.getLocation().distanceSquaredTo(target) : 1000000000);
        notCloser = 0;
    }

    public MapLocation tryExplore() throws GameActionException {
        // go to one of the locations with crumbs, if it exists
        MapLocation[] crumbs = this.rc.senseNearbyCrumbs(-1);
        if (crumbs.length > 0) {
            return crumbs[0];
        }
        // otherwise move towards a random target
        if (rc.getLocation().distanceSquaredTo(target) >= minDist) {
            notCloser++;
        } else {
            minDist = rc.getLocation().distanceSquaredTo(target);
            notCloser = 0;
        }
        if (notCloser >= 5 || minDist <= 10) {
            target = new MapLocation(rng.nextInt() % rc.getMapWidth(), rng.nextInt() % rc.getMapHeight());
            minDist = rc.getLocation().distanceSquaredTo(target);
            notCloser = 0;
        }
        return target;
    }

}