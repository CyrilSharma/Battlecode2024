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
    SymmetryChecker sc;
    public Duck(RobotController rc) {
        super(rc);
        path = new Pathing(this);
        am = new AttackMicro(rc);
        sc = new SymmetryChecker(rc);
    }

    void run() throws GameActionException {
        if (!rc.isSpawned()) spawn();
        if (!rc.isSpawned()) return;

        boolean ranmicro = am.runMicro();
        if (!ranmicro) seekTarget();
        sc.updateSymmetry();
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
}
