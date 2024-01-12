package initialBot;
import battlecode.common.*;
import initialBot.Communications.AttackTarget;

/*
 * in the future we may want to diversify this class into the multiple specializations.
 */

public class Duck extends Robot {
    Pathing path;
    Exploration exploration;
    MapLocation target;
    AttackMicro am;
    public Duck(RobotController rc) {
        super(rc);
        path = new Pathing(this);
        exploration = new Exploration(this);
        am = new AttackMicro(this);
    }

    void run() throws GameActionException {
        if (rc.getRoundNum() == 1) communications.establishOrder();
        if (!rc.isSpawned()) spawn();
        if (!rc.isSpawned()) return;
        updateFlags();
        purchaseGlobal();
        considerTrap();

        // boolean shouldheal = true;
        if (ranFlagMicro()) {}
        else if (am.runMicro()) {}
        else if (guardFlag()) {}
        else seekTarget();
        tryHeal();
    }

    public boolean guardFlag() throws GameActionException {
        if (communications.order >= 3) return false;
        MapLocation[] fflags = communications.get_flags(true);
        if ((communications.order < fflags.length)) {
            path.moveTo(fflags[communications.order]);
        }
        return true;
    }

    public void tryHeal() throws GameActionException {
        int besthealth = -1;
        RobotInfo bestfriend = null;
        RobotInfo[] friends = am.friends;
        for (int i = friends.length; i-- > 0;) {
            if ((friends[i].health > besthealth) && rc.canHeal(friends[i].location)) {
                bestfriend = friends[i];
                besthealth = friends[i].health;
            }
        }

        if ((bestfriend != null) && (rc.canHeal(bestfriend.location))) {
            rc.heal(bestfriend.location);
        }
    }

    void spawn() throws GameActionException {
        AttackTarget[] targets = communications.getAttackTargets();
        MapLocation[] spawns = rc.getAllySpawnLocations();
        if (targets.length == 0) {
            int st = rng.nextInt(spawns.length);
            for (int i = spawns.length; i-- > 0;) {
                MapLocation loc = spawns[(i + st) % spawns.length];
                if (rc.canSpawn(loc)) {
                    rc.spawn(loc);
                    break;
                }
            }
        } else {
            int bestd = 1 << 30;
            MapLocation bestloc = null;
            for (int i = spawns.length; i-- > 0;) {
                MapLocation loc = spawns[i];
                for (int j = targets.length; j-- > 0;) {
                    MapLocation tloc =  targets[j].m;
                    int d = tloc.distanceSquaredTo(loc);
                    if (d < bestd) {
                        bestd = d;
                        bestloc = loc;
                    }
                }
            }
            if (rc.canSpawn(bestloc)) {
                rc.spawn(bestloc);
            }
        }
        mt.run();
    }

    void updateFlags() throws GameActionException {
        for (int ctr = 0; ctr < 2; ctr++) {
            boolean friendly = (ctr == 0) ? true : false;
            Team team = (friendly) ? rc.getTeam() : rc.getTeam().opponent();
            MapLocation[] locs = communications.get_flags(friendly);
            FlagInfo[] flags = rc.senseNearbyFlags(-1, team);
            for (int i = locs.length; i-- > 0;) {
                if (!rc.canSenseLocation(locs[i])) continue;
                boolean found = false;
                for (int j = flags.length; j-- > 0;) {
                    if (flags[j].getLocation() == locs[i]) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    communications.delete_flag(locs[i], friendly);
                }
            }
            for (int j = flags.length; j-- > 0;) {
                communications.log_flag(flags[j].getLocation(), friendly);
            }
        }
    }

    void purchaseGlobal() throws GameActionException {
        // Factored into it's own method because logic may
        // Become complex.
        if (rc.canBuyGlobal(GlobalUpgrade.ACTION)) {
            rc.buyGlobal(GlobalUpgrade.ACTION);
        } else if (rc.canBuyGlobal(GlobalUpgrade.HEALING)) {
            rc.buyGlobal(GlobalUpgrade.HEALING);
        } else if (rc.canBuyGlobal(GlobalUpgrade.CAPTURING)) {
            rc.buyGlobal(GlobalUpgrade.HEALING);
        }
    }

    void considerTrap() throws GameActionException {
        // In the future this may have some fancier logic
        // I.e if barrier still in place, try adding traps to barrier.
        // If attack targets are set, try adding traps near them... 
        int acceptabledist = 0;
        boolean shouldbuild = false;
        FlagInfo[] flags = rc.senseNearbyFlags(2, rc.getTeam());
        if (flags.length != 0) {
            shouldbuild = true;
            acceptabledist = 9;
        }

        MapLocation myloc = rc.getLocation();
        AttackTarget[] targets = communications.getAttackTargets();
        for (int i = targets.length; i-- > 0;) {
            if (myloc.distanceSquaredTo(targets[i].m) <= 9) {
                shouldbuild = true;
                acceptabledist = 1;
                break;
            }
        }


        if (!shouldbuild) return;
        MapInfo[] infos = rc.senseNearbyMapInfos(acceptabledist);
        for (int i = infos.length; i-- > 0;) {
            if (infos[i].getTrapType() != TrapType.NONE) return;
        }
        if (rc.canBuild(TrapType.EXPLOSIVE, myloc)) {
            rc.build(TrapType.EXPLOSIVE, myloc);
            return;
        }
    }

    void seekTarget() throws GameActionException {
        if ((rc.getRoundNum() > GameConstants.SETUP_ROUNDS - 30)) hunt();
        else explore();
    }

    void explore() throws GameActionException {
        MapLocation exploreTarget = exploration.tryExplore();
        rc.setIndicatorString("Exploring: " + exploreTarget);
        if (exploreTarget != null) path.moveTo(exploreTarget);
    }

    public void hunt() throws GameActionException {
        MapLocation target = getHuntTarget();
        if (target != null) path.moveTo(target);
    }

    public MapLocation getHuntTarget() throws GameActionException {
        int bestd = 1 << 30;
        MapLocation bestloc = null;
        MapLocation myloc = rc.getLocation();
        AttackTarget[] targets = communications.getAttackTargets();
        if (targets.length != 0) {
            int idx = -1;
            for (int i = targets.length; i-- > 0;) {
                MapLocation loc = targets[i].m;
                int score = targets[i].score;
                int d = loc.distanceSquaredTo(myloc);
                // Find closest unmanned target.
                if ((d < bestd) && (d < 900)) { // && (score < 5)) {
                    bestd = d;
                    bestloc = loc;
                    idx = i;
                }
            }
            if (bestloc != null) {
                rc.setIndicatorString("Hunting enemy: " + bestloc);
                communications.markAttackTarget(idx);
                return bestloc;
            }
        }

        MapLocation[] flags = communications.get_flags(false);
        if (flags.length != 0) {
            for (int i = flags.length; i-- > 0;) {
                MapLocation loc = flags[i];
                int d = loc.distanceSquaredTo(myloc);
                if (d < bestd) {
                    bestd = d;
                    bestloc = loc;
                }
            }
            rc.setIndicatorString("Hunting flag: " + bestloc);
            return bestloc;
        }

        flags = rc.senseBroadcastFlagLocations();
        if (flags.length != 0) {
            for (int i = Math.min(10, flags.length); i-- > 0;) {
                MapLocation loc = flags[i];
                int d = loc.distanceSquaredTo(myloc);
                if (d < bestd && myloc.distanceSquaredTo(loc) > 4) {
                    bestd = d;
                    bestloc = loc;
                }
            }
            rc.setIndicatorString("Hunting Approximate flag: " + bestloc);
            return bestloc;
        }

        if (sc.getSymmetry() != -1) {
            MapLocation[] allies = rc.getAllySpawnLocations();
            for (int i = Math.min(10, allies.length); i-- > 0;) {
                MapLocation loc = sc.getSymLoc(allies[i]);
                int d = loc.distanceSquaredTo(myloc);
                if (d < bestd) {
                    bestd = d;
                    bestloc = loc;
                }
            }
            return bestloc;
        }

        return null;
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

        FlagInfo f = flags[0];
        if (f.isPickedUp()) return false;

        MapLocation floc = f.getLocation();
        if (rc.canPickupFlag(floc)) {
            rc.pickupFlag(floc);
            communications.delete_flag(floc, false);
        } else {
             // To prevent everyone rushing a flag at once.
            int id = rc.getID();
            RobotInfo[] friends = rc.senseNearbyRobots(-1, rc.getTeam());
            for (int i = friends.length; i-- > 0;) {
                if (friends[i].ID > id) return false;
            }
            path.moveTo(floc);
        }
        return true;
    }
}
