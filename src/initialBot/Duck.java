package initialBot;
import battlecode.common.*;

/* 
 * in the future we may want to diversify this class into the multiple specializations.
 */

public class Duck extends Robot {
    Pathing path;
    MapLocation exploreTarget;
    MapLocation target;
    AttackMicro am;
    public Duck(RobotController rc) {
        super(rc);
        path = new Pathing(this);
        am = new AttackMicro(rc);
    }

    void run() throws GameActionException {
        if (!rc.isSpawned()) spawn();
        if (!rc.isSpawned()) return;
        if (am.runMicro()) return;
        if (ranFlagMicro()) return;
        seekTarget();
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

    void seekTarget() throws GameActionException {
        if ((target == null) && sc.getSymmetry() != -1) {
            int bestd = 1 << 30;
            MapLocation bestloc = null;
            MapLocation myloc = rc.getLocation();
            MapLocation[] allies = rc.getAllySpawnLocations();
            for (int i = Math.min(10, allies.length); i-- > 0;) {
                MapLocation loc = sc.getSymLoc(allies[i]);
                int d = loc.distanceSquaredTo(myloc);
                if (d < bestd) {
                    bestd = d;
                    bestloc = loc;
                }
            }
            target = bestloc;
        } else if (exploreTarget == null) {
            // This is just here to test out pathing.
            // We'll add some exploration logic here eventually.
            MapLocation[] targets = {
                new MapLocation(0, 0),
                new MapLocation(0, rc.getMapHeight()),
                new MapLocation(rc.getMapWidth(), 0),
                new MapLocation(rc.getMapWidth(), rc.getMapHeight())
            };
            int idx = rng.nextInt(4);
            exploreTarget = targets[idx];
        }

        if (target == null) {
            rc.setIndicatorString("Exploring: " + exploreTarget);
            path.moveTo(exploreTarget);
        } else {
            rc.setIndicatorString("Hunting: " + target);
            path.moveTo(target);
        }
    }

    public boolean ranFlagMicro() throws GameActionException {
        if (rc.hasFlag()) {
            // Not sure how well this works. Ideally we just move directly
            // Towards friendly territory.
            MapLocation myloc = rc.getLocation();
            int bestdist = 1 << 30;
            MapLocation bestloc = null;
            MapLocation[] locs = rc.getAllySpawnLocations();
            for (int i = Math.min(locs.length, 10); i-- > 0; ) {
                MapLocation m = locs[i];
                int d = m.distanceSquaredTo(myloc);
                if (d < bestdist) {
                    bestdist = d;
                    bestloc = m;
                }
            }
            path.moveTo(bestloc);
            return true;
        }

        FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
        if (flags.length == 0) return false;

        // To prevent everyone rushing a flag at once.
        int id = rc.getID();
        RobotInfo[] friends = rc.senseNearbyRobots(-1, rc.getTeam());
        for (int i = friends.length; i-- > 0;) {
            if (friends[i].ID > id) return false;
        }

        for (int i = flags.length; i-- > 0;) {
            FlagInfo f = flags[i];
            if (!f.isPickedUp()) {
                MapLocation floc = f.getLocation();
                if (rc.canPickupFlag(floc)) {
                    rc.pickupFlag(floc);
                } else {
                    path.moveTo(floc);
                }
            }
        }
        return true;
    }
}
