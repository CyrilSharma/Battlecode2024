package initialBot;
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
        long passible0 = ~(mt.adjblocked | mt.wall_mask0 | mt.water_mask0);
        long passible1 = ~(mt.wall_mask1 | mt.water_mask1);
        long reach0 = 1099511627776L;
        long reach1 = 0;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (reach0 >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (reach0 >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (reach0 >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (reach0 >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (reach0 >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (reach0 >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (reach0 >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (reach0 >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (reach0 >> 54)) & passible1;
        reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >> 1) & roverflow));
        reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >> 1) & roverflow));
        reach0 = (reach0 | (reach0 << 9) | (reach0 >> 9) | (reach1 << 54)) & passible0;
        reach1 = (reach1 | (reach1 << 9) | (reach1 >> 9) | (reach0 >> 54)) & passible1;
        
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
            targetsqrs0 = (targetsqrs0 | (targetsqrs0 << 9) | (targetsqrs0 >> 9) | (targetsqrs1 << 54));
            targetsqrs1 = (targetsqrs1 | (targetsqrs1 << 9) | (targetsqrs1 >> 9) | (targetsqrs0 >> 54));
        }
        
        long[] back0 = {0, 0, 0, 0};
        long[] back1 = {0, 0, 0, 0};
        back0[0] = targetsqrs0 & reach0;
        back1[0] = targetsqrs1 & reach1;
        int idx = 0;
        while ((back0[idx] & 0x70381c0000000L) == 0) {
            int mask = 0b11;
            int nidx = (idx + 1) & mask;
            long water0 = back0[nidx];
            long water1 = back1[nidx];
            back0[nidx] = (back0[idx] | ((back0[idx] << 1) & loverflow) | ((back0[idx] >> 1) & roverflow));
            back1[nidx] = (back1[idx] | ((back1[idx] << 1) & loverflow) | ((back1[idx] >> 1) & roverflow));
            long temp = back0[nidx];
            back0[nidx] = (back0[nidx] | (back0[nidx] << 9) | (back0[nidx] >> 9) | (back1[nidx] << 54));
            back1[nidx] = (back1[nidx] | (back1[nidx] << 9) | (back1[nidx] >> 9) | (temp >> 54));
            back0[(idx + 3) & mask] = (back0[nidx] & mt.water_mask0);
            back0[nidx] = water0 | back0[idx] | (back0[nidx] & passible0);
            back1[(idx + 3) & mask] = (back1[nidx] & mt.water_mask1);
            back1[nidx] = water1 | back1[idx] | (back1[nidx] & passible1);
            idx = nidx;
        }
        
        if (rc.getMovementCooldownTurns() >= 10) return;
        long best = back0[idx] & 0x70381c0000000L;
        if ((best & 0x1000000000000L) > 0) {
            if (rc.canFill(rc.getLocation().add(Direction.NORTHWEST))) {
                rc.fill(rc.getLocation().add(Direction.NORTHWEST));
            } else {
                rc.move(Direction.NORTHWEST); return; 
            }
        }
    

        if ((best & 0x2000000000000L) > 0) {
            if (rc.canFill(rc.getLocation().add(Direction.NORTH))) {
                rc.fill(rc.getLocation().add(Direction.NORTH));
            } else {
                rc.move(Direction.NORTH); return; 
            }
        }
    

        if ((best & 0x4000000000000L) > 0) {
            if (rc.canFill(rc.getLocation().add(Direction.NORTHEAST))) {
                rc.fill(rc.getLocation().add(Direction.NORTHEAST));
            } else {
                rc.move(Direction.NORTHEAST); return; 
            }
        }
    

        if ((best & 0x20000000000L) > 0) {
            if (rc.canFill(rc.getLocation().add(Direction.EAST))) {
                rc.fill(rc.getLocation().add(Direction.EAST));
            } else {
                rc.move(Direction.EAST); return; 
            }
        }
    

        if ((best & 0x8000000000L) > 0) {
            if (rc.canFill(rc.getLocation().add(Direction.WEST))) {
                rc.fill(rc.getLocation().add(Direction.WEST));
            } else {
                rc.move(Direction.WEST); return; 
            }
        }
    

        if ((best & 0x40000000L) > 0) {
            if (rc.canFill(rc.getLocation().add(Direction.SOUTHWEST))) {
                rc.fill(rc.getLocation().add(Direction.SOUTHWEST));
            } else {
                rc.move(Direction.SOUTHWEST); return; 
            }
        }
    

        if ((best & 0x80000000L) > 0) {
            if (rc.canFill(rc.getLocation().add(Direction.SOUTH))) {
                rc.fill(rc.getLocation().add(Direction.SOUTH));
            } else {
                rc.move(Direction.SOUTH); return; 
            }
        }
    

        if ((best & 0x100000000L) > 0) {
            if (rc.canFill(rc.getLocation().add(Direction.SOUTHEAST))) {
                rc.fill(rc.getLocation().add(Direction.SOUTHEAST));
            } else {
                rc.move(Direction.SOUTHEAST); return; 
            }
        }
    

    }
}
