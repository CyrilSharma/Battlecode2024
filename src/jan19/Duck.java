package jan19;
import battlecode.common.*;
import jan19.Communications.AttackTarget;

/*
 * in the future we may want to diversify this class into the multiple specializations.
 */

public class Duck extends Robot {
    Pathing path;
    Exploration exploration;
    AttackMicro am;
    TrapMicro tm;
    MapLocation[] spawnCenters;
    boolean putDefenses;
    Heist H;
    int lastSeen;
    public Duck(RobotController rc) {
        super(rc);
        // Placing it here so other things can refer to it.
        getSpawnCenters();
        path = new Pathing(this);
        exploration = new Exploration(this);
        am = new AttackMicro(this);
        tm = new TrapMicro(this);
        H = new Heist(this);
        putDefenses = false;
        lastSeen = 0;
    }

    void run() throws GameActionException {
        if (rc.getRoundNum() == 1) communications.establishOrder();
        if (!rc.isSpawned()) spawn();
        if (!rc.isSpawned()) return;
        updateFlags();
        purchaseGlobal();
        considerTrap();
        collectCrumbs();
        ranFlagMicro();

        if (builder()) {}
        else if (am.runMicro()) {}
        else if (tryLevelUp()) {}
        else if (H.needHeist()) { H.runHeist(); }
        else if (guardFlag()) {}
        else seekTarget();
        tryHeal();
    }

    public boolean tryLevelUp() throws GameActionException {
        if ((rc.getRoundNum() < 1800) ||
            (rc.getLevel(SkillType.BUILD) == 6) ||
            (rc.getLevel(SkillType.HEAL) < 3) || 
            (rc.getLevel(SkillType.ATTACK) < 3) ||
            (rc.getRoundNum() % 20 != 0)) return false;
        trainBuilder();
        return true;
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

    public boolean trainBuilder() throws GameActionException {
        // returns true if the builder is next to water, (or will be because of digging) so that it stays there
        boolean nextToWater = false;
        for(Direction dir : directions) {
            MapLocation loc = rc.getLocation().add(dir);
            if (rc.canFill(loc)) {
                rc.fill(loc);
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
        return false;
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
        FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
        MapLocation[] locs = communications.get_enemy_flag_spawns();
        for (int i = locs.length; i-- > 0;) {
            if (!rc.canSenseLocation(locs[i])) continue;
            boolean found = false;
            for (int j = flags.length; j-- > 0;) {
                if (flags[j].getLocation().equals(locs[i])) {
                    found = true;
                    break;
                }
            }
            if (found) continue;
            communications.delete_enemy_flag_spawn(locs[i]);
        }
        
        flags = rc.senseNearbyFlags(-1, rc.getTeam());
        for (int j = flags.length; j-- > 0;) {
            if (!flags[j].isPickedUp()) continue;
            MapLocation floc = flags[j].getLocation();
            communications.log_runaway_flag(floc);
        }

        flags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
        for (int j = flags.length; j-- > 0;) {
            if (flags[j].isPickedUp()) continue;
            MapLocation floc = flags[j].getLocation();
            communications.log_enemy_flag_spawn(floc);
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

    public void protectCarrier(MapLocation loc) throws GameActionException {
        if (rc.getLocation().distanceSquaredTo(loc) > 16) path.moveTo(loc);
        else {
            Direction dir = rc.getLocation().directionTo(loc);
            dir = dir.rotateRight();
            if (rc.canMove(dir)) rc.move(dir);
            else {
                dir = dir.rotateRight();
                if (rc.canMove(dir)) rc.move(dir);
            }
        }
    }

    public void attackCarrier(MapLocation loc) throws GameActionException {
        while(am.tryAttack());
        path.moveTo(loc);
        while(am.tryAttack());
    }

    public MapLocation getHuntTarget() throws GameActionException {
        // Defend nearby carriers!
        MapLocation myloc = rc.getLocation();
        AttackTarget[] carriers = communications.get_carriers();
        if (carriers.length != 0) {
            int closest = 1 << 30;
            MapLocation closestCarrier = null;
            for (int i = carriers.length; i-- > 0;) {
                MapLocation loc = carriers[i].m;
                int d = loc.distanceSquaredTo(myloc);
                if (d < closest) {
                    closest = d;
                    closestCarrier = loc;
                }
            }
            if (closest <= 100) {
                protectCarrier(closestCarrier);
                return null;
            }
        }

        // Take back nearby flags!
        AttackTarget[] runaways = communications.get_runaway_flags();
        if (runaways.length != 0) {
            int closest = 1 << 30;
            MapLocation closestCarrier = null;
            for (int i = runaways.length; i-- > 0;) {
                MapLocation loc = runaways[i].m;
                int d = loc.distanceSquaredTo(myloc);
                if (d < closest) {
                    closest = d;
                    closestCarrier = loc;
                }
            }
            if (closest <= 100) {
                attackCarrier(closestCarrier);
                return null;
            }
        }
        
        int bestd = 1 << 30;
        MapLocation bestloc = null;
        // if (targets.length != 0) {
        //     int idx = -1;
        //     for (int i = targets.length; i-- > 0;) {
        //         MapLocation loc = targets[i].m;
        //         int d = loc.distanceSquaredTo(myloc);
        //         if ((d < bestd) && (d < 64)) { // && (score < 5)) {
        //             bestd = d;
        //             bestloc = loc;
        //             idx = i;
        //         }
        //     }
        //     if (bestloc != null) {
        //         rc.setIndicatorString("Hunting enemy: " + bestloc);
        //         communications.markAttackTarget(idx);
        //         return bestloc;
        //     }
        // }

        boolean dealt_with = false;
        FlagInfo[] nearbyflags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
        for (FlagInfo flag: nearbyflags) {
            if (flag.isPickedUp()) {
                dealt_with = true;
                break;
            }
        }

        // Go to nearby enemy flag spawns (if we know them)!
        MapLocation[] flags = communications.get_enemy_flag_spawns();
        if (flags.length != 0) {
            for (int i = flags.length; i-- > 0;) {
                MapLocation loc = flags[i];
                int d = loc.distanceSquaredTo(myloc);
                if ((d < bestd) && (!dealt_with || !rc.canSenseLocation(loc))) {
                    bestd = d;
                    bestloc = loc;
                }
            }
            if (bestloc != null) {
                rc.setIndicatorString("Hunting flag: " + bestloc);
                return bestloc;
            }
        }

        // Go to approximate flag locations!
        flags = rc.senseBroadcastFlagLocations();
        if (flags.length != 0) {
            for (int i = Math.min(10, flags.length); i-- > 0;) {
                MapLocation loc = flags[i];
                int d = loc.distanceSquaredTo(myloc);
                if ((d < bestd) && (!dealt_with || !rc.canSenseLocation(loc))) {
                    bestd = d;
                    bestloc = loc;
                }
            }
            if (bestloc != null) {
                rc.setIndicatorString("Hunting Approximate flag: " + bestloc);
                return bestloc;
            }
        }

        // Go to symmetric spawn loations.
        if (sc.getSymmetry() != -1) {
            for (int i = spawnCenters.length; i-- > 0;) {
                MapLocation loc = sc.getSymLoc(spawnCenters[i]);
                int d = loc.distanceSquaredTo(myloc);
                if (d < bestd) {
                    bestd = d;
                    bestloc = loc;
                }
            }
            return bestloc;
        }

        // Go to attack targets, if they exist hopefully.
        AttackTarget[] targets = communications.getAttackTargets();
        if (targets.length != 0) {
            int idx = -1;
            for (int i = targets.length; i-- > 0;) {
                MapLocation loc = targets[i].m;
                int d = loc.distanceSquaredTo(myloc);
                if (d < bestd) {
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
        return null;
    }

    public boolean ranFlagMicro() throws GameActionException {
        if (rc.hasFlag()) {
            MapLocation myloc = rc.getLocation();
            communications.log_carrier(myloc);
            int[] scores = new int[spawnCenters.length];
            AttackTarget[] targets = communications.getAttackTargets();

            // for (int i = spawnCenters.length; i-- > 0;) {
            //     MapLocation spawn = spawnCenters[i];
            //     int Fx = spawn.x - myloc.x;
            //     int Fy = spawn.y - myloc.y;
            //     int Fm = (int) Math.sqrt(Fx*Fx + Fy*Fy);
            //     int Px = -Fy;
            //     int Py = Fx;
            //     int Pm = (int) Math.sqrt(Px*Px + Py*Py);
            //     int det = Px*Fy - Py*Fx;
            //     for (int j = targets.length; j-- > 0;) {
            //         AttackTarget target = targets[j];
            //         int tscore = target.score;
            //         MapLocation loc = target.m;
            //         int Tx = loc.x - myloc.x;
            //         int Ty = loc.y - myloc.y;
            //         int pcomp = ((Tx*Fy - Ty*Fx) * Pm) / det;
            //         int fcomp = ((Px*Ty - Py*Tx) * Fm) / det;
            //         if ((pcomp < 5) && (pcomp > -5)) {
            //             if (fcomp <= 5) scores[i] += tscore * 3;
            //             else if (fcomp <= 10) scores[i] += tscore * 2;
            //             else scores[i] += tscore;
            //         }
            //     }
            // }

            // for (int i = targets.length; i-- > 0;) {
            //     AttackTarget target = targets[i];
            //     MapLocation tloc = target.m;
            //     int tscore = target.score;
            //     for (int j = spawnCenters.length; j-- > 0;) {
            //         int d = tloc.distanceSquaredTo(spawnCenters[j]);
            //         if (d <= 16) scores[j] += tscore * 2;
            //         if (d <= 64) scores[j] += tscore * 3;
            //         if (d <= 144) scores[j] += tscore;
            //     }
            // }

            
            // for (int i = spawnCenters.length; i-- > 0; ) {
            //     MapLocation m = spawnCenters[i];
            //     int d = m.distanceSquaredTo(myloc);
            //     int s = scores[i];
            //     if (s > 12) continue;
            //     if (d < bestdist) {
            //         bestdist = d;
            //         bestloc = m;
            //     }
            // }

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
        } else {
            // RUSH THE FLAG ALL AT ONCE. 
            MapLocation myloc = rc.getLocation();
            if (myloc.distanceSquaredTo(floc) > 9) return false;
            path.moveTo(floc);
        }
        return true;
    }
}
