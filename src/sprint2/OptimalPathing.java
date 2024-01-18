package sprint2;
import battlecode.common.*;
public class OptimalPathing {
    MapTracker mt;
    RobotController rc;
    static int H, W;
    public OptimalPathing(Robot robot) {
        this.rc = robot.rc;
        this.mt = robot.mt;
        H = rc.getMapHeight();
        W = rc.getMapWidth();
    }
    
    public void moveTo(MapLocation target) throws GameActionException {
        if (rc.getMovementCooldownTurns() >= 10) return;
        long loverflow = 0x7fbfdfeff7fbfdfeL;
        long roverflow = 0x3fdfeff7fbfdfeffL;
        long water_mask0 = mt.water_mask0;
        long water_mask1 = mt.water_mask1;
        long passible0 = ~(mt.adjblocked | mt.wall_mask0);
        long passible1 = ~(mt.wall_mask1);
        long clear0 = ~(mt.adjblocked | mt.wall_mask0 | mt.water_mask0);
        long clear1 = ~(mt.wall_mask1 | mt.water_mask1);
        long temp = 0;
        long reach0 = 1099511627776L;
        long reach1 = 0;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (temp >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (temp >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (temp >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (temp >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (temp >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (temp >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (temp >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (temp >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (temp >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (temp >> 54)) & passible1;
        
        long targetsqrs0 = 0;
        long targetsqrs1 = 0;
        MapLocation myloc = rc.getLocation();
        int dist = myloc.distanceSquaredTo(target);
        if (dist > 20) {
            double mult = Math.sqrt(((double) 20) / dist);
            int x = (int) Math.round(myloc.x + (target.x - myloc.x) * mult);
            int y = (int) Math.round(myloc.y + (target.y - myloc.y) * mult);
            MapLocation estimate = new MapLocation(x, y);
            MapLocation bestTarget = estimate;
            int bestDist = estimate.distanceSquaredTo(target);
            MapLocation adj = null;
            int d = 0;
            
            adj = estimate.add(Direction.NORTHWEST);
            d = adj.distanceSquaredTo(target);
            if (rc.canSenseLocation(adj) && d < bestDist) {
                bestDist = d;
                bestTarget = adj;
            }
            adj = estimate.add(Direction.NORTH);
            d = adj.distanceSquaredTo(target);
            if (rc.canSenseLocation(adj) && d < bestDist) {
                bestDist = d;
                bestTarget = adj;
            }
            adj = estimate.add(Direction.NORTHEAST);
            d = adj.distanceSquaredTo(target);
            if (rc.canSenseLocation(adj) && d < bestDist) {
                bestDist = d;
                bestTarget = adj;
            }
            adj = estimate.add(Direction.EAST);
            d = adj.distanceSquaredTo(target);
            if (rc.canSenseLocation(adj) && d < bestDist) {
                bestDist = d;
                bestTarget = adj;
            }
            adj = estimate.add(Direction.WEST);
            d = adj.distanceSquaredTo(target);
            if (rc.canSenseLocation(adj) && d < bestDist) {
                bestDist = d;
                bestTarget = adj;
            }
            adj = estimate.add(Direction.SOUTHWEST);
            d = adj.distanceSquaredTo(target);
            if (rc.canSenseLocation(adj) && d < bestDist) {
                bestDist = d;
                bestTarget = adj;
            }
            adj = estimate.add(Direction.SOUTH);
            d = adj.distanceSquaredTo(target);
            if (rc.canSenseLocation(adj) && d < bestDist) {
                bestDist = d;
                bestTarget = adj;
            }
            adj = estimate.add(Direction.SOUTHEAST);
            d = adj.distanceSquaredTo(target);
            if (rc.canSenseLocation(adj) && d < bestDist) {
                bestDist = d;
                bestTarget = adj;
            }
            target = bestTarget;
        }
        
        int i = ((target.y - (myloc.y - 4)) * 9) + (target.x - (myloc.x - 4));
        if (i >= 63) {
            targetsqrs1 = 1L << (i - 63);
        } else {
            targetsqrs0 = 1L << i;
        }
        
        while ((targetsqrs0 & reach0) == 0 && (targetsqrs1 & reach1) == 0) {
            targetsqrs0 = (targetsqrs0 | ((targetsqrs0 << 1) & loverflow) | ((targetsqrs0 >> 1) & roverflow));
            targetsqrs1 = (targetsqrs1 | ((targetsqrs1 << 1) & loverflow) | ((targetsqrs1 >> 1) & roverflow));
            temp = targetsqrs0;
            targetsqrs0 = (targetsqrs0 | (targetsqrs0 << 9) | (targetsqrs0 >> 9) | (targetsqrs1 << 54));
            targetsqrs1 = (targetsqrs1 | (targetsqrs1 << 9) | (targetsqrs1 >> 9) | (temp >> 54));
        }
        
        int idx = 0;
        long[] back0 = {0, 0, 0, 0};
        long[] back1 = {0, 0, 0, 0};
        back0[0] = targetsqrs0 & reach0;
        back1[0] = targetsqrs1 & reach1;
        while ((back0[idx] & 0x70381c0000000L) == 0) {
            int nidx = (idx + 1) % 4;
            long water0 = back0[nidx];
            long water1 = back1[nidx];
            back0[nidx] = (back0[idx] | ((back0[idx] << 1) & loverflow) | ((back0[idx] >> 1) & roverflow));
            back1[nidx] = (back1[idx] | ((back1[idx] << 1) & loverflow) | ((back1[idx] >> 1) & roverflow));
            temp = back0[nidx];
            back0[nidx] = (back0[nidx] | (back0[nidx] << 9) | (back0[nidx] >> 9) | (back1[nidx] << 54));
            back1[nidx] = (back1[nidx] | (back1[nidx] << 9) | (back1[nidx] >> 9) | (temp >> 54));
            back0[(idx + 3) % 4] = (back0[nidx] & water_mask0);
            back0[nidx] = water0 | (back0[nidx] & clear0);
            back1[(idx + 3) % 4] = (back1[nidx] & water_mask1);
            back1[nidx] = water1 | (back1[nidx] & clear1);
            idx = nidx;
        }
        
        Direction bestDir = null;
        int bestDist = -1;
        long best = back0[idx];
        if ((best & 0x1000000000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.NORTHWEST);
            int d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.NORTHWEST;
                bestDist = d;
            }
        }
    

        if ((best & 0x2000000000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.NORTH);
            int d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.NORTH;
                bestDist = d;
            }
        }
    

        if ((best & 0x4000000000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.NORTHEAST);
            int d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.NORTHEAST;
                bestDist = d;
            }
        }
    

        if ((best & 0x20000000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.EAST);
            int d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.EAST;
                bestDist = d;
            }
        }
    

        if ((best & 0x8000000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.WEST);
            int d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.WEST;
                bestDist = d;
            }
        }
    

        if ((best & 0x40000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.SOUTHWEST);
            int d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.SOUTHWEST;
                bestDist = d;
            }
        }
    

        if ((best & 0x80000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.SOUTH);
            int d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.SOUTH;
                bestDist = d;
            }
        }
    

        if ((best & 0x100000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.SOUTHEAST);
            int d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.SOUTHEAST;
                bestDist = d;
            }
        }
    

        
        if (bestDir != null) {
            MapLocation loc = rc.adjacentLocation(bestDir);
            if (rc.senseMapInfo(loc).isWater()) {
                if (rc.canFill(loc)) {
                    rc.fill(loc);
                }
            } else if (rc.canMove(bestDir)) {
                rc.move(bestDir);
            }
        }


    }
}
