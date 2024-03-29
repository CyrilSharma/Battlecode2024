package finalBot;
import battlecode.common.*;
import finalBot.Communications.AttackTarget;

import static java.util.Arrays.sort;

/*
 * in the future we may want to diversify this class into the multiple specializations.
 */

public class Duck extends Robot {
    Pathing path;
    FlagMicro fm;
    Exploration exploration;
    TrapMicro tm;
    MapLocation[] spawnCenters;
    boolean putDefenses;
    int lastSeen;

    public Duck(RobotController rc) {
        super(rc);
        // Placing it here so other things can refer to it.
        getSpawnCenters();
        path = new Pathing(this);
        exploration = new Exploration(this);
        am = new AttackMicro(this);
        tm = new TrapMicro(this);
        fm = new FlagMicro(this);
        putDefenses = false;
        lastSeen = 0;
    }

    void run() throws GameActionException {
        if (rc.getRoundNum() == 1) communications.establishOrder();
        if (rc.getRoundNum() >= 100 && rc.getRoundNum() < 200) communications.getIDs();
        if (!rc.isSpawned()) spawn();
        if (!rc.isSpawned()) return;
        updateFlags();
        purchaseGlobal();
        considerTrap();
        collectCrumbs();
        if (fm.run()) {}
        else if (builder()) {}
        else if (am.runMicro()) {}
        else if (tryLevelUp()) {}
        else if (guardFlag()) {}
        else seekTarget();
        tryHeal();
        if (communications.order >= 30) rc.setIndicatorString("attack");
        if (communications.order > 6 && communications.order < 30) rc.setIndicatorString("heal");
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

    public MapLocation getReturnSpawn() throws GameActionException{
        int bestdist = 1 << 30;
        MapLocation bestloc = null;
        for (int i = spawnCenters.length; i-- > 0; ) {
            MapLocation m = spawnCenters[i];
            int d = m.distanceSquaredTo(rc.getLocation());
            if (d < bestdist) {
                bestdist = d;
                bestloc = m;
            }
        }
        return bestloc;
    }

    public void collectCrumbs() throws GameActionException {
        if (rc.hasFlag()) return;
        if (rc.getRoundNum() > 300) return; // give up.
        int bestd = 1 << 30;
        MapLocation bestLocation = null;
        MapLocation myloc = rc.getLocation();
        MapLocation[] crumbs = rc.senseNearbyCrumbs(-1);
        for (int i = 0; i < crumbs.length; i++) {
            MapLocation loc = crumbs[i];
            int d = loc.distanceSquaredTo(myloc);
            MapInfo s = rc.senseMapInfo(loc);
            if (s.isDam()) continue;
            if (d < bestd) {
                bestd = d;
                bestLocation = loc;
            }
        }
        if (bestLocation != null) {
            // We still seem to have some bugs in pathing.
            // This is a bandaid fix.
            if (bestLocation.isAdjacentTo(myloc)) {
                MapInfo mi = rc.senseMapInfo(bestLocation);
                if (mi.isWater()) {
                    if (rc.canFill(bestLocation)) {
                        rc.fill(bestLocation);
                    }
                }
                Direction dir = myloc.directionTo(bestLocation);
                if (rc.canMove(dir)) rc.move(dir);
            } else {
                path.moveTo(bestLocation);
            }
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
        sort(spawnCenters);
    }

    public boolean shouldTrainBuilder() throws GameActionException {
        if(rc.getLevel(SkillType.ATTACK) > 3 || rc.getLevel(SkillType.HEAL) > 3) return false;
        MapInfo mi = rc.senseMapInfo(rc.getLocation());
        if (rc.getRoundNum() < 300) return (communications.order >= 3 && communications.order < 6 && rc.getLevel(SkillType.BUILD) < 4 && !mi.isSpawnZone());
        /*
        else if (rc.getRoundNum() > 1000) {
            return (communications.order >= 3 && communications.order < 9 && rc.getLevel(SkillType.BUILD) < 6 && !mi.isSpawnZone());
        }
         */
        else return (communications.order >= 3 && communications.order < 6 && rc.getLevel(SkillType.BUILD) < 6 && !mi.isSpawnZone());
    }

    public boolean trainBuilder() throws GameActionException {
        // returns true if the builder is next to land
        for (Direction dir : directions) {
            MapLocation loc = rc.getLocation().add(dir);
            if(rc.canDig(rc.getLocation().add(dir)) && ((loc.x + loc.y) & 1) == 0) {
                rc.dig(loc);
            }
        }
        return false;
    }

    public boolean builder() throws GameActionException {
        if (rc.hasFlag()) return false;
        if (shouldTrainBuilder()) return trainBuilder();
        return false;
    }

    public boolean guardFlag() throws GameActionException {
        MapLocation[] fflags = spawnCenters;
        if (fflags.length <= communications.order) return false;
        if (communications.flagdead) return false;
        MapLocation fl = fflags[communications.order];
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
        if (!rc.isActionReady()) return;
        if (communications.order >= 30) {
            if (rc.getExperience(SkillType.HEAL) >= 98) return;
        }
        RobotInfo[] r = rc.senseNearbyRobots(5, rc.getTeam().opponent());
        if (r.length > 0) return;

        int besthealth = 1001;
        RobotInfo bestfriend = null;
        RobotInfo[] friends = rc.senseNearbyRobots(-1, rc.getTeam());
        for (int i = friends.length; i-- > 0;) {
            RobotInfo f = friends[i];
            int score = f.health - 100 * f.buildLevel;
            if (rc.getRoundNum() >= 200) {
                if(communications.ord[f.ID - 10000] >= 30) score -= 200 * f.attackLevel;
            }
            if (f.hasFlag) score = 0;
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
        int bestd = 1 << 30;
        MapLocation bestloc = null;
        AttackTarget[] targets = communications.getAttackTargets();
        MapLocation[] spawns = rc.getAllySpawnLocations();

        // Special handling if you're a flag defender.
        if (!communications.flagdead && communications.order < 3) {
            for (int i = spawns.length; i-- > 0;) {
                MapLocation loc = spawns[i];
                if (!loc.isAdjacentTo(spawnCenters[communications.order])) continue;
                if (rc.canSpawn(loc)) {
                    rc.spawn(loc);
                    break;
                }
            }
            init_turn();
            return;
        }
        
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
        if ((bestloc == null) || (bestd >= 144)) {
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
        init_turn();
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
            communications.log_runaway_flag(floc, flags[j].getID());
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
        if (rc.getRoundNum() < GameConstants.SETUP_ROUNDS) return;
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
        if ((rc.getRoundNum() > GameConstants.SETUP_ROUNDS)) hunt();
        else if (rc.getRoundNum() > GameConstants.SETUP_ROUNDS - 50) wallRush();
        else explore();
    }

    public void wallRush() throws GameActionException {
        boolean seeDam = false;
        boolean nextTo = false;
        MapInfo[] mi = rc.senseNearbyMapInfos();
        MapLocation d = null;
        for(int i = mi.length; i-- > 0;) {
            if (mi[i].isDam()) {
                seeDam = true;
                d = mi[i].getMapLocation();
                if(mi[i].getMapLocation().distanceSquaredTo(rc.getLocation()) < 2) nextTo = true;
            }
        }
        if(nextTo) return;
        hunt();
        if (!seeDam) {
            hunt();
            return;
        }
        Direction dir = rc.getLocation().directionTo(d);
        if(rc.canMove(dir)) rc.move(dir);
        Direction dir2 = dir.rotateRight().rotateRight();
        if(rc.getID() % 2 == 0) dir2 = dir.rotateLeft().rotateRight();
        if(rc.canMove(dir2)) rc.move(dir2);
    }

    void explore() throws GameActionException {
        MapLocation exploreTarget = exploration.tryExplore();
        if (exploreTarget != null) path.moveTo(exploreTarget);
    }

    public void hunt() throws GameActionException {
        MapLocation target = getHuntTarget();
        if (target != null) path.moveTo(target);
    }

    public void protectCarrier(MapLocation loc, MapLocation tar) throws GameActionException {
        RobotInfo[] r = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for(int i = r.length; i-- > 0;) {
            if(r[i].hasFlag) {
                loc = r[i].location;
                break;
            }
        }
        if (rc.getLocation().distanceSquaredTo(loc) > 12) path.moveTo(loc);
        else {
            path.moveTo(tar);
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
            int spa = -1;
           // int numb = 0;
            MapLocation closestCarrier = null;
            MapLocation tar = null;
            for (int i = carriers.length; i-- > 0;) {
                MapLocation loc = carriers[i].m;
                int d = loc.distanceSquaredTo(myloc);
                if (d < closest && carriers[i].num < 7) {
                    closest = d;
                    closestCarrier = loc;
                    spa = carriers[i].score;
                    tar = carriers[i].goal;
                    //numb = carriers[i].num;
                }
            }
            if (closest <= 100) {
                communications.markCarrier(closestCarrier);
                protectCarrier(closestCarrier, tar);
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
                if (rc.getLocation().equals(bestloc)) {
                    // don't just stay stuck there, try a place nearby!
                    bestloc = new MapLocation(bestloc.x + rng.nextInt(15) - 7, bestloc.y + rng.nextInt(15) - 7);

                }
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
}
