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
    TrapMicro tm;
    MapLocation[] spawnCenters;
    boolean putDefenses;
    Heist H;
    int lastSeen;
    public Duck(RobotController rc) {
        super(rc);
        path = new Pathing(this);
        exploration = new Exploration(this);
        am = new AttackMicro(this);
        tm = new TrapMicro(this);
        H = new Heist(this);
        getSpawnCenters();
        putDefenses = false;
        lastSeen = 0;
    }

    void run() throws GameActionException {
        // if (rc.getRoundNum() == 300) {
        //     rc.resign();
        // }
        if (rc.getRoundNum() == 1) communications.establishOrder();
        if (!rc.isSpawned()) spawn();
        if (!rc.isSpawned()) return;
        updateFlags();
        purchaseGlobal();
        considerTrap();
        collectCrumbs();

        // // boolean shouldheal = true;
        if (ranFlagMicro()) {}
        else if (builder()) {}
        else if (am.runMicro()) {}
        else if (H.needHeist()) { H.runHeist(); }
        else if (guardFlag()) {}
        else seekTarget();
        tryHeal();
    }

    public void collectCrumbs() throws GameActionException{
        // Seek crumbs.
        MapLocation[] crumbs = rc.senseNearbyCrumbs(-1);
        int best = 0;
        MapLocation bestLocation = null;
        for (int i = 0; i < crumbs.length; i++) {
            int c = rc.senseMapInfo(crumbs[i]).getCrumbs();
            if (c > best) {
                best = c;
                bestLocation = crumbs[i];
            }
        }
        if (bestLocation != null) {
            path.moveTo(bestLocation);
        }

    }

    public void getSpawnCenters() {
        spawnCenters = new MapLocation[3];
        int ind = 0;
        MapLocation[] sp = rc.getAllySpawnLocations();
        for (MapLocation m : sp) {
            int cnt = 0;
            for (MapLocation x : sp) {
                if(x.isAdjacentTo(m) && !x.equals(m)) {
                    cnt++;
                }
            }
            if (cnt == 8) spawnCenters[ind++] = m;
            if (ind == 3) break;
        }
    }

    public boolean shouldTrainBuilder() throws GameActionException {
        if(rc.getLevel(SkillType.ATTACK) > 3 || rc.getLevel(SkillType.HEAL) > 3) return false;
        MapInfo mi = rc.senseMapInfo(rc.getLocation());
        if (rc.getRoundNum() < 300) return (communications.order >= 3 && communications.order < 6 && rc.getLevel(SkillType.BUILD) < 4 && !mi.isSpawnZone());
        else if (rc.getRoundNum() > 1000) {
            return (communications.order >= 3 && communications.order < 9 && rc.getLevel(SkillType.BUILD) < 6 && !mi.isSpawnZone());
        }
        else return (communications.order >= 3 && communications.order < 6 && rc.getLevel(SkillType.BUILD) < 6 && !mi.isSpawnZone());
    }
    public boolean isBuilder() {
        if(rc.getLevel(SkillType.ATTACK) > 3 || rc.getLevel(SkillType.HEAL) > 3) return false;
        if (rc.getRoundNum() < 300) return (communications.order >= 3 && communications.order < 6 && rc.getLevel(SkillType.BUILD) >= 4);
        else if (rc.getRoundNum() > 1000) {
            return (communications.order >= 3 && communications.order < 9 && rc.getLevel(SkillType.BUILD) < 6);
        }
        return (communications.order >= 3 && communications.order < 6 && rc.getLevel(SkillType.BUILD) == 6);
    }

    public boolean trainBuilder() throws GameActionException {
        // returns true if the builder is next to water, (or will be because of digging) so that it stays there
        boolean nextToWater = false;
        for(Direction dir : directions) {
            MapLocation loc = rc.getLocation().add(dir);
            if (rc.canFill(loc)) {
                rc.fill(loc);
                return false;
            } 
            if (rc.canSenseLocation(loc) && rc.senseMapInfo(loc).isWater()) {
                nextToWater = true;
            }
        }
        for (Direction dir : directions) {
            MapLocation loc = rc.getLocation().add(dir);
            if(rc.canDig(rc.getLocation().add(dir))) {
                rc.dig(loc);
                return true;
            }
        }
        return nextToWater;
    }

    public boolean builder() throws GameActionException {
        if(shouldTrainBuilder()) return trainBuilder();
        if (!isBuilder()) return false;
        if(!putDefenses) return putInitialDefenses();
        return false;
    }

    public boolean putInitialDefenses() throws GameActionException {
        if (communications.order < 3 || communications.order >= 6) {
            putDefenses = true;
            return false;
        }
        MapLocation g = spawnCenters[communications.order - 3];
        if (rc.getLocation().distanceSquaredTo(g) > 0) path.moveTo(g);
        else {
            int cnt = 0;
            for (Direction dir : directions) {
                MapLocation place = rc.getLocation().add(dir);
                if (!rc.canSenseLocation(place)) continue;
                MapInfo mi = rc.senseMapInfo(place);
                if (place.distanceSquaredTo(g) == 2 || place.distanceSquaredTo(g) == 0) {
                    if (mi.getTrapType() == TrapType.STUN) cnt++;
                }
                if (mi.getTrapType() == TrapType.NONE) {
                    if (place.distanceSquaredTo(g) == 2) {
                        if (rc.canBuild(TrapType.STUN, place)) {
                            rc.build(TrapType.STUN, place);
                            cnt++;
                        }
                    }
                }
            }
            if(cnt == 4) putDefenses = true;
        }
        return true;
    }

    public boolean guardFlag() throws GameActionException {
        MapLocation[] fflags = spawnCenters;
        if (fflags.length <= communications.order) return false;
        if (communications.flagdead) return false;
        MapLocation fl = fflags[communications.order];
        rc.setIndicatorString("i am defending " + fl);
        RobotInfo[] r = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        MapLocation closest = null;
        int dist = 1000000;
        for (RobotInfo rob : r) {
            if(rob.hasFlag) {
                if (rc.canAttack(rob.location)) rc.attack(rob.location);
                path.moveTo(rob.location);
                break;
            }
            if (rc.getLocation().distanceSquaredTo(rob.location) < dist) {
                dist = rc.getLocation().distanceSquaredTo(rob.location);
                closest = rob.location;
            }
        }
        if (rc.getLocation().distanceSquaredTo(fl) > 2) {
            path.moveTo(fl);
            return true;
        }
        else {
            if (closest != null && rc.canAttack(closest)) rc.attack(closest);
            for (Direction dir : directions) {
                MapLocation place = rc.getLocation().add(dir);
                if (!rc.canSenseLocation(place)) continue;
                MapInfo mi = rc.senseMapInfo(place);
                if (mi.getTrapType() == TrapType.NONE && rc.getRoundNum() > 250) {
                    if (place.distanceSquaredTo(fl) == 2) {
                        if (rc.canBuild(TrapType.STUN, place)) rc.build(TrapType.STUN, place);
                    }
                    /*
                    else if (((place.x + place.y) & 1) == ((fl.x + fl.y) & 1) && rc.getRoundNum() < 200) {
                        if (rc.canDig(place)) rc.dig(place);
                    }
                    */
                }
            }
            Direction df = rc.getLocation().directionTo(fl);
            df = df.rotateRight();
            if (df == Direction.CENTER) df = directions[rng.nextInt(8)];
            if (rc.canMove(df)) rc.move(df);
            FlagInfo[] fi = rc.senseNearbyFlags(-1);
            for (FlagInfo ff : fi) {
                if (ff.getLocation().distanceSquaredTo(rc.getLocation()) <= 2) {
                    lastSeen = rc.getRoundNum();
                }
            }
            if (rc.getRoundNum() - lastSeen > 150) {
                communications.flagdead = true;
            }
        }
        return true;
    }

    public void tryHeal() throws GameActionException {
        int besthealth = 1001;
        RobotInfo bestfriend = null;
        RobotInfo[] friends = rc.senseNearbyRobots(-1, rc.getTeam());
        for (int i = friends.length; i-- > 0;) {
            int score = friends[i].health - 100 * friends[i].buildLevel;
            if ((score < besthealth) && rc.canHeal(friends[i].location)) {
                bestfriend = friends[i];
                besthealth = score;
            }
        }

        if ((bestfriend != null) && (rc.canHeal(bestfriend.location))) {
            rc.heal(bestfriend.location);
        }
    }

    void spawn() throws GameActionException {
        AttackTarget[] targets = communications.getAttackTargets();
        MapLocation[] spawns = rc.getAllySpawnLocations();

        int bestd = 1 << 30;
        MapLocation bestloc = null;
        for (int i = spawns.length; i-- > 0;) {
            MapLocation loc = spawns[i];
            for (int j = targets.length; j-- > 0;) {
                MapLocation tloc =  targets[j].m;
                int d = tloc.distanceSquaredTo(loc);
                if ((d < bestd) && (rc.canSpawn(loc))) {
                    bestd = d;
                    bestloc = loc;
                }
            }
        }
        if ((bestloc == null) || bestd >= 144) {
            int st = rng.nextInt(spawns.length);
            for (int i = spawns.length; i-- > 0;) {
                MapLocation loc = spawns[(i + st) % spawns.length];
                if (rc.canSpawn(loc)) {
                    rc.spawn(loc);
                    break;
                }
            }
        } else if (rc.canSpawn(bestloc)) {
            rc.spawn(bestloc);
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
            rc.buyGlobal(GlobalUpgrade.CAPTURING);
        }
    }

    void considerTrap() throws GameActionException {
        if (rc.getLevel(SkillType.BUILD) >= 4) {
            tm.placeTrap();
            return;
        }
        // still have a fail-safe where regular bots can place traps
        RobotInfo[] enemies = rc.senseNearbyRobots(13, rc.getTeam().opponent());
        if (enemies.length >= 9) {
            tm.placeTrap();
            // int bestd = 1 << 30;
            // MapLocation bestloc = null;
            // for (Direction d: directions) {
            //     MapLocation loc = myloc.add(d);
            //     if (!rc.canBuild(TrapType.EXPLOSIVE, loc)) continue;
            //     int dist = 0;
            //     for (RobotInfo e: enemies) {
            //         dist += loc.distanceSquaredTo(e.location);
            //     }
            //     if (dist < bestd) {
            //         bestd = dist;
            //         bestloc = loc;
            //     }
            // }

            // if (bestloc == null) return;
            // if (rc.canBuild(TrapType.EXPLOSIVE, bestloc)) {
            //     rc.build(TrapType.EXPLOSIVE, bestloc);
            // }
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
                if ((d < bestd)) { // && (score < 5)) {
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
            boolean dealt_with = false;
            if (rc.canSenseLocation(bestloc)) {
                FlagInfo[] nearbyflags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
                for (FlagInfo flag: nearbyflags) {
                    if (flag.getLocation() != bestloc) continue;
                    if (flag.isPickedUp()) {
                        dealt_with = true;
                        break;
                    }
                }
            }
            if (!dealt_with) {
                rc.setIndicatorString("Hunting flag: " + bestloc);
                return bestloc;
            }
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
            MapLocation[] locs = spawnCenters;
            for (int i = 3; i-- > 0; ) {
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
