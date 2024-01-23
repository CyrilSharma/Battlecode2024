package sprint2;
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
        Long.numberOfTrailingZeros(0);

        outer: for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                break outer;
            }
        }

        // Mine water aggressively.
        // MapLocation myloc = rc.getLocation();
        // for (Direction d: Direction.values()) {
        //     if (rc.canFill(myloc.add(d))) {
        //         rc.fill(myloc.add(d));
        //     }
        // }

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
        int tileheight = rc.getMapHeight() / 9;
        int tilewidth = rc.getMapWidth() / 5;
        int idxh = order / 5, idxw = order % 5;

        // In case we accidentally don't search our tile thoroughly.
        int diffw = rng.nextInt(2 * tilewidth), diffh = rng.nextInt(2 * tileheight);
        target = new MapLocation(idxw * tilewidth + diffw, idxh * tileheight + diffh);
        minDist = (rc.getLocation() != null ? rc.getLocation().distanceSquaredTo(target) : 1000000000);
        notCloser = 0;
    }
}