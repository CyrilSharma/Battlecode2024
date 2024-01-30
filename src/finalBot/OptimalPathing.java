package finalBot;
import battlecode.common.*;
public class OptimalPathing {
    MapTracker mt;
    NeighborTracker nt;
    RobotController rc;
    static int H, W;
    public OptimalPathing(Robot robot) {
        this.rc = robot.rc;
        this.mt = robot.mt;
        this.nt = robot.nt;
        H = rc.getMapHeight();
        W = rc.getMapWidth();
    }
    
    public void moveTo(MapLocation target) throws GameActionException {
        if (rc.getMovementCooldownTurns() >= 10) return;
        long mask0 = 0x7FFFFFFFFFFFFFFFL;
        long mask1 = 0x3FFFFL;
        long loverflow = 0x7fbfdfeff7fbfdfeL;
        long roverflow = 0x3fdfeff7fbfdfeffL;
        long water_mask0 = mt.water_mask0;
        long water_mask1 = mt.water_mask1;
        long passible0 = ~(mt.adjblocked | mt.wall_mask0) & mask0;
        long passible1 = ~(mt.wall_mask1) & mask1;
        long clear0 = ~(mt.adjblocked | mt.wall_mask0 | mt.water_mask0) & mask0;
        long clear1 = ~(mt.wall_mask1 | mt.water_mask1) & mask1;
        long temp = 0;
        long reach0 = 1099511627776L;
        long reach1 = 0;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        
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
            targetsqrs0 = (targetsqrs0 | ((targetsqrs0 << 1) & loverflow) | ((targetsqrs0 >>> 1) & roverflow));
            targetsqrs1 = (targetsqrs1 | ((targetsqrs1 << 1) & loverflow) | ((targetsqrs1 >>> 1) & roverflow));
            temp = targetsqrs0;
            targetsqrs0 = (targetsqrs0 | (targetsqrs0 << 9) | (targetsqrs0 >>> 9) | (targetsqrs1 << 54)) & mask0;
            targetsqrs1 = (targetsqrs1 | (targetsqrs1 << 9) | (targetsqrs1 >>> 9) | (temp >>> 54)) & mask1;
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
            back0[nidx] = (back0[idx] | ((back0[idx] << 1) & loverflow) | ((back0[idx] >>> 1) & roverflow));
            back1[nidx] = (back1[idx] | ((back1[idx] << 1) & loverflow) | ((back1[idx] >>> 1) & roverflow));
            temp = back0[nidx];
            back0[nidx] = (back0[nidx] | (back0[nidx] << 9) | (back0[nidx] >>> 9) | (back1[nidx] << 54));
            back1[nidx] = (back1[nidx] | (back1[nidx] << 9) | (back1[nidx] >>> 9) | (temp >>> 54));
            back0[(idx + 3) % 4] = (back0[nidx] & water_mask0);
            back0[nidx] = water0 | (back0[nidx] & clear0);
            back1[(idx + 3) % 4] = (back1[nidx] & water_mask1);
            back1[nidx] = water1 | (back1[nidx] & clear1);
            idx = nidx;
        }
        
        Direction bestDir = null;
        int bestDist = 1073741824;
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
            }
            if (rc.canMove(bestDir)) {
                rc.move(bestDir);
            }
        }


    }
    
    public void moveToNoWater(MapLocation target) throws GameActionException {
        if (rc.getMovementCooldownTurns() >= 10) return;
        long mask0 = 0x7FFFFFFFFFFFFFFFL;
        long mask1 = 0x3FFFFL;
        long loverflow = 0x7fbfdfeff7fbfdfeL;
        long roverflow = 0x3fdfeff7fbfdfeffL;
        long passible0 = ~(mt.adjblocked | mt.wall_mask0 | mt.water_mask0) & mask0;
        long passible1 = ~(mt.wall_mask1 | mt.water_mask1) & mask1;
        long temp = 0;
        long reach0 = 1099511627776L;
        long reach1 = 0;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        
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
            targetsqrs0 = (targetsqrs0 | ((targetsqrs0 << 1) & loverflow) | ((targetsqrs0 >>> 1) & roverflow));
            targetsqrs1 = (targetsqrs1 | ((targetsqrs1 << 1) & loverflow) | ((targetsqrs1 >>> 1) & roverflow));
            temp = targetsqrs0;
            targetsqrs0 = (targetsqrs0 | (targetsqrs0 << 9) | (targetsqrs0 >>> 9) | (targetsqrs1 << 54)) & mask0;
            targetsqrs1 = (targetsqrs1 | (targetsqrs1 << 9) | (targetsqrs1 >>> 9) | (temp >>> 54)) & mask1;
        }
        
        long back0 = targetsqrs0 & reach0;
        long back1 = targetsqrs1 & reach1;
        while ((back0 & 0x70381c0000000L) == 0) {
            back0 = (back0 | ((back0 << 1) & loverflow) | ((back0 >>> 1) & roverflow));
            back1 = (back1 | ((back1 << 1) & loverflow) | ((back1 >>> 1) & roverflow));
            temp = back0;
            back0 = (back0 | (back0 << 9) | (back0 >>> 9) | (back1 << 54)) & passible0;
            back1 = (back1 | (back1 << 9) | (back1 >>> 9) | (temp >>> 54)) & passible1;
        }
        
        Direction bestDir = null;
        int bestDist = 1073741824;
        long best = back0;
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
            if (rc.canMove(bestDir)) {
                rc.move(bestDir);
            }
        }


    }
    public void moveToAvoidEnemies(MapLocation target) throws GameActionException {
        if (rc.getMovementCooldownTurns() >= 10) return;
        long mask0 = 0x7FFFFFFFFFFFFFFFL;
        long mask1 = 0x3FFFFL;
        long loverflow = 0x7fbfdfeff7fbfdfeL;
        long roverflow = 0x3fdfeff7fbfdfeffL;
        long enemyaction0 = nt.enemy_mask0;
        long enemyaction1 = nt.enemy_mask1;
        long temp = 0;
        
        enemyaction0 = (enemyaction0 | ((enemyaction0 << 1) & loverflow) | ((enemyaction0 >>> 1) & roverflow));
        enemyaction1 = (enemyaction1 | ((enemyaction1 << 1) & loverflow) | ((enemyaction1 >>> 1) & roverflow));
        temp = enemyaction0;
        enemyaction0 = (enemyaction0 | (enemyaction0 << 9) | (enemyaction0 >>> 9) | (enemyaction1 << 54)) & mask0;
        enemyaction1 = (enemyaction1 | (enemyaction1 << 9) | (enemyaction1 >>> 9) | (temp >>> 54)) & mask1;
        
        long slow_mask0 = (enemyaction0);
        long slow_mask1 = (enemyaction1);
        long passible0 = ~(mt.adjblocked | mt.wall_mask0 | mt.water_mask0) & mask0;
        long passible1 = ~(mt.wall_mask1 | mt.water_mask1) & mask1;
        long clear0 = ~(mt.adjblocked | mt.wall_mask0 | mt.water_mask0 | enemyaction0) & mask0;
        long clear1 = ~(mt.wall_mask1 | mt.water_mask1 | enemyaction1) & mask1;
        long reach0 = 1099511627776L;
        long reach1 = 0;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
        temp = reach0;
        reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        
        reach0 = ((reach0 & ~enemyaction0) | (0x381c1c0000000L & ~mt.adjblocked)) & mask0;
        reach1 = ((reach1 & ~enemyaction1)) & mask1;
        if ((reach0 | reach1) == 0) return;
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
        
        rc.setIndicatorDot(target, 255, 0, 255);
        
        int i = ((target.y - (myloc.y - 4)) * 9) + (target.x - (myloc.x - 4));
        if (i >= 63) {
            targetsqrs1 = 1L << (i - 63);
        } else {
            targetsqrs0 = 1L << i;
        }
        
        while ((targetsqrs0 & reach0) == 0 && (targetsqrs1 & reach1) == 0) {
            targetsqrs0 = (targetsqrs0 | ((targetsqrs0 << 1) & loverflow) | ((targetsqrs0 >>> 1) & roverflow));
            targetsqrs1 = (targetsqrs1 | ((targetsqrs1 << 1) & loverflow) | ((targetsqrs1 >>> 1) & roverflow));
            temp = targetsqrs0;
            targetsqrs0 = (targetsqrs0 | (targetsqrs0 << 9) | (targetsqrs0 >>> 9) | (targetsqrs1 << 54)) & mask0;
            targetsqrs1 = (targetsqrs1 | (targetsqrs1 << 9) | (targetsqrs1 >>> 9) | (temp >>> 54)) & mask1;
        }
        
        int idx = 0;
        long[] back0 = {0, 0, 0, 0};
        long[] back1 = {0, 0, 0, 0};
        back0[0] = targetsqrs0 & reach0;
        back1[0] = targetsqrs1 & reach1;
        while ((back0[idx] & 0x70381c0000000L) == 0) {
            int nidx = (idx + 1) % 4;
            long slow0 = back0[nidx];
            long slow1 = back1[nidx];
            back0[nidx] = (back0[idx] | ((back0[idx] << 1) & loverflow) | ((back0[idx] >>> 1) & roverflow));
            back1[nidx] = (back1[idx] | ((back1[idx] << 1) & loverflow) | ((back1[idx] >>> 1) & roverflow));
            temp = back0[nidx];
            back0[nidx] = (back0[nidx] | (back0[nidx] << 9) | (back0[nidx] >>> 9) | (back1[nidx] << 54));
            back1[nidx] = (back1[nidx] | (back1[nidx] << 9) | (back1[nidx] >>> 9) | (temp >>> 54));
            back0[(idx + 3) % 4] = (back0[nidx] & slow_mask0);
            back0[nidx] = slow0 | (back0[nidx] & clear0);
            back1[(idx + 3) % 4] = (back1[nidx] & slow_mask1);
            back1[nidx] = slow1 | (back1[nidx] & clear1);
            idx = nidx;
        }
        
        Direction bestDir = null;
        int bestDist = 1073741824;
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
            if (rc.canMove(bestDir)) {
                rc.move(bestDir);
            }
        }


    }
}
