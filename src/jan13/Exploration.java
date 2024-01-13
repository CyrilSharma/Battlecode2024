package jan13;
import java.util.Random;

import battlecode.common.*;

public class Exploration {
    RobotController rc;
    Communications communications;
    MapLocation target;
    Pathing path;
    int minDist;
    int notCloser;

    final Random rng = new Random();
    public Exploration(Robot robot){
        this.rc = robot.rc;
        this.communications = robot.communications;
    }

    public MapLocation tryExplore() throws GameActionException {
        if (target == null) reset();

        // Mine water aggressively.
        // MapLocation myloc = rc.getLocation();
        // for (Direction d: Direction.values()) {
        //     if (rc.canFill(myloc.add(d))) {
        //         rc.fill(myloc.add(d));
        //     }
        // }

        // Seek crumbs.
        MapLocation[] crumbs = this.rc.senseNearbyCrumbs(-1);
        if (crumbs.length > 0) return crumbs[0];

        // Seek water.
        // MapInfo[] infos = this.rc.senseNearbyMapInfos(-1);
        // for (MapInfo m: infos) {
        //     if (m.isWater()) return m.getMapLocation();
        // }

        // If we don't make progress reset.
        if (rc.getLocation().distanceSquaredTo(target) >= minDist) {
            notCloser++;
        } else {
            minDist = rc.getLocation().distanceSquaredTo(target);
            notCloser = 0;
        }
        if (notCloser >= 5 || minDist <= 10) reset();
        return target;
    }

    public void reset() throws GameActionException {
        int order = communications.order;
        // if (rc.getRoundNum() >= 10) order = (order + rng.nextInt(49)) % 49;
        int tilewidth = rc.getMapWidth() / 7;
        int tileheight = rc.getMapHeight() / 7;
        int idxh = order / 7, idxw = order % 7;

        // In case we accidentally don't search our tile thoroughly.
        int diffw = rng.nextInt(2 * tilewidth), diffh = rng.nextInt(2 * tileheight);
        target = new MapLocation(idxw * tilewidth + diffw, idxh * tileheight + diffh);
        minDist = (rc.getLocation() != null ? rc.getLocation().distanceSquaredTo(target) : 1000000000);
        notCloser = 0;
    }
}