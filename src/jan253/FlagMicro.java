package jan253;
import battlecode.common.*;
import jan253.Communications.AttackTarget;

public class FlagMicro {
    RobotController rc;
    Communications communications;
    MapLocation[] spawnCenters;
    NeighborTracker nt;
    Pathing path;
    FlagInfo[] flags;
    public FlagMicro(Duck d) {
        this.rc = d.rc;
        this.communications = d.communications;
        this.spawnCenters = d.spawnCenters;
        this.path = d.path;
        this.nt = d.nt;
    }

    public boolean run() throws GameActionException {
        flags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
        if (rc.hasFlag()) {
            if (nt.enemies.length != 0) flee();
            else goHome();
            return true;
        }
        if (flags.length == 0) return false;
        FlagInfo f = flags[0];
        if (f.isPickedUp()) return false;
        MapLocation floc = f.getLocation();
        if (rc.canPickupFlag(floc)) {
            rc.pickupFlag(floc);
        } else {
            MapLocation myloc = rc.getLocation();
            if (myloc.distanceSquaredTo(floc) > 9) return false;
            path.moveTo(floc);
        }
        return true;
    }

    public void flee() throws GameActionException {
        rc.setIndicatorString("Fleeing!");
        FleeTarget[] fleeTargets = new FleeTarget[9];
        for (Direction dir: Direction.values()) {
            fleeTargets[dir.ordinal()] = new FleeTarget(dir);
        }
    
        RobotInfo[] robots = nt.friends;
        for (int i = robots.length; i-- > 0;) {
            if (Clock.getBytecodesLeft() < 3000) break;
            RobotInfo r = robots[i];
            for (Direction dir: Direction.values()) {
                fleeTargets[dir.ordinal()].addAlly(r);
            }
        }

        robots = nt.enemies;
        for (int i = robots.length; i-- > 0;) {
            if (Clock.getBytecodesLeft() < 3000) break;
            RobotInfo r = robots[i];
            for (Direction dir: Direction.values()) {
                fleeTargets[dir.ordinal()].addEnemy(r);
            }
        }

        FleeTarget best = fleeTargets[Direction.CENTER.ordinal()];
        for (Direction dir: Direction.values()) {
            FleeTarget t = fleeTargets[dir.ordinal()];
            if (t.isBetterThan(best)) best = t;
        }

        if (rc.canMove(best.dir)) {
            rc.move(best.dir);
            Team myteam = rc.getTeam();
            nt.friends = rc.senseNearbyRobots(-1, myteam);
            nt.enemies = rc.senseNearbyRobots(-1, myteam.opponent());
        }
    }

    public void goHome() throws GameActionException {
        rc.setIndicatorString("Running Home!");
        MapLocation myloc = rc.getLocation();
        int bestdist = 1 << 30;
        MapLocation bestloc = null;
        if (bestloc == null) {
            bestdist = 1 << 30;
            for (int i = spawnCenters.length; i-- > 0; ) {
                MapLocation m = spawnCenters[i];
                int d = m.distanceSquaredTo(myloc);
                if (d < bestdist) {
                    bestdist = d;
                    bestloc = m;
                }
            }
        }

        AttackTarget[] at = communications.getAttackTargets();
        MapLocation closestEnemy = null;
        int dist = 1000000;
        for (int i = at.length; i-- > 0;) {
            int d = at[i].m.distanceSquaredTo(rc.getLocation());
            if (d < dist) {
                dist = d;
                closestEnemy = at[i].m;
            }
        }
        if (closestEnemy != null) {
            int x = rc.getLocation().x;
            int y = rc.getLocation().y;
            int dx = (closestEnemy.x - x);
            int dy = (closestEnemy.y - y);
            MapLocation a = new MapLocation(x - dy, y + dx);
            MapLocation b = new MapLocation(x + dy, y - dx);
            a = norm(a);
            b = norm(b);
            MapLocation curBest = null;
            if (distToClosestSpawn(a) < distToClosestSpawn(b)) curBest = a;
            else curBest = b;
            if (dist <= 80 && distToClosestSpawn(rc.getLocation()) > distToClosestSpawn(closestEnemy))
                bestloc = curBest;
        }

        communications.log_carrier(myloc, bestloc);
        path.moveTo(bestloc);
    }

    int distToClosestSpawn(MapLocation loc) {
        int dist = 100000;
        for (int i = 0; i < 3; i++) {
            int d = loc.distanceSquaredTo(spawnCenters[i]);
            if (d < dist) dist = d;
        }
        return dist;
     }
 
     MapLocation norm(MapLocation loc) {
         return new MapLocation(
             Math.min(rc.getMapWidth(), Math.max(0, loc.x)),
             Math.min(rc.getMapHeight(), Math.max(0, loc.y))
         );
     }

    class FleeTarget {
        int minDistToEnemy = 100000;
        int enemiesAttacking = 0;
        int enemiesTargetting = 0;
        int alliesGuarding = 0;
        int closestSpawnDist = 0;
        int spawnDist = 0;
        boolean canMove;
        MapLocation nloc;
        Direction dir;

        FleeTarget(Direction dir) throws GameActionException {
            MapLocation myloc = rc.getLocation();
            nloc = myloc.add(dir);
            canMove = (rc.canMove(dir) || dir == Direction.CENTER);
            closestSpawnDist = distToClosestSpawn(nloc);
            this.dir = dir;
        }
        
        void addEnemy(RobotInfo r) throws GameActionException {
            int dist = r.location.distanceSquaredTo(nloc);
            if (dist < minDistToEnemy) minDistToEnemy = dist;
            enemiesTargetting++;
            if (dist <= GameConstants.ATTACK_RADIUS_SQUARED)
                enemiesAttacking++;
        }
        
        void addAlly(RobotInfo r) throws GameActionException {
            if (!canMove) return;
            int dist = r.location.distanceSquaredTo(nloc);
            if (dist <= GameConstants.ATTACK_RADIUS_SQUARED)
                alliesGuarding++;
        }

        int safe() {
            if (enemiesAttacking > 0) return 0;
            if (enemiesTargetting > alliesGuarding) return 1;
            return 2;
        }

        boolean isBetterThan(FleeTarget mt) {
            if (!canMove) return false;
            if (safe() > mt.safe()) return true;
            if (safe() < mt.safe()) return false;
            if (minDistToEnemy > mt.minDistToEnemy) return true;
            if (minDistToEnemy < mt.minDistToEnemy) return false;
            return false;
            // return closestSpawnDist < mt.closestSpawnDist;
        }
    }
}

