package initialBot;
import battlecode.common.*;

public class AttackMicro {
    boolean canAttack;
    RobotController rc;
    RobotInfo[] friends = null;
    RobotInfo[] enemies = null;
    Communications comms;
    public AttackMicro(Robot r) {
        this.rc = r.rc;
        this.comms = r.communications;
    }

    public boolean runMicro() throws GameActionException {
        Team myteam = rc.getTeam();
        friends = rc.senseNearbyRobots(-1, myteam);
        enemies = rc.senseNearbyRobots(-1, myteam.opponent());
        if (!close() && (enemies.length == 0)) return false;
        addBestTarget();
        maneuver();
        return true;
    }

    public boolean close() throws GameActionException {
        return false;
        // MapLocation myloc = rc.getLocation();
        // AttackTarget[] targets = comms.getAttackTargets();
        // int bestd = 1 << 30;
        // for (int i = targets.length; i-- > 0;) {
        //     MapLocation targetloc = targets[i].m;
        //     int d = targetloc.distanceSquaredTo(myloc);
        //     if (d < bestd) bestd = d;
        // }
        // return (bestd <= 64);
    }

    // Finds the enemy closest to a flag, and marks it in comms.
    public void addBestTarget() throws GameActionException {
        if (enemies.length == 0) return;
        boolean hasFlag = false;
        MapLocation bestloc = enemies[0].location;
        for (int i = enemies.length; i-- > 0;) {
            if (enemies[i].hasFlag) {
                bestloc = enemies[i].location;
                hasFlag = true;
                break;
            }
        }
        comms.addAttackTarget(bestloc, hasFlag);
    }

    void maneuver() throws GameActionException {
        rc.setIndicatorString("Maneuvering");
        if (rc.isActionReady()) tryAttack();
        canAttack = rc.isActionReady();

        // Needs 1k Bytecode.
        MicroTarget[] microtargets = new MicroTarget[9];

        // It's important that the center location is first.
        microtargets[0] = new MicroTarget(Direction.CENTER);

        microtargets[1] = new MicroTarget(Direction.NORTH);
        microtargets[2] = new MicroTarget(Direction.NORTHEAST);
        microtargets[3] = new MicroTarget(Direction.NORTHWEST);
        microtargets[4] = new MicroTarget(Direction.EAST);
        microtargets[5] = new MicroTarget(Direction.WEST);
        microtargets[6] = new MicroTarget(Direction.SOUTH);
        microtargets[7] = new MicroTarget(Direction.SOUTHEAST);
        microtargets[8] = new MicroTarget(Direction.SOUTHWEST);


        int iters = 0;
        RobotInfo[] robots = friends;
        for (int i = robots.length; i-- > 0;) {
            if (Clock.getBytecodesLeft() < 1500) break;
            RobotInfo r = robots[i];
            microtargets[0].addAlly(r);
            microtargets[1].addAlly(r);
            microtargets[2].addAlly(r);
            microtargets[3].addAlly(r);
            microtargets[4].addAlly(r);
            microtargets[5].addAlly(r);
            microtargets[6].addAlly(r);
            microtargets[7].addAlly(r);
            microtargets[8].addAlly(r);
            iters++;
        }

        robots = enemies;
        for (int i = robots.length; i-- > 0;) {
            if (Clock.getBytecodesLeft() < 1500) break;
            RobotInfo r = robots[i];
            microtargets[0].addEnemy(r);
            microtargets[1].addEnemy(r);
            microtargets[2].addEnemy(r);
            microtargets[3].addEnemy(r);
            microtargets[4].addEnemy(r);
            microtargets[5].addEnemy(r);
            microtargets[6].addEnemy(r);
            microtargets[7].addEnemy(r);
            microtargets[8].addEnemy(r);
            iters++;
        }

        // Needs 1k Bytecode.
        MicroTarget best = microtargets[0];
        if (microtargets[1].isBetterThan(best)) best = microtargets[1];
        if (microtargets[2].isBetterThan(best)) best = microtargets[2];
        if (microtargets[3].isBetterThan(best)) best = microtargets[3];
        if (microtargets[4].isBetterThan(best)) best = microtargets[4];
        if (microtargets[5].isBetterThan(best)) best = microtargets[5];
        if (microtargets[6].isBetterThan(best)) best = microtargets[6];
        if (microtargets[7].isBetterThan(best)) best = microtargets[7];
        if (microtargets[8].isBetterThan(best)) best = microtargets[8];
        if (rc.canMove(best.dir)) {
            rc.move(best.dir);
            Team myteam = rc.getTeam();
            friends = rc.senseNearbyRobots(-1, myteam);
            enemies = rc.senseNearbyRobots(-1, myteam.opponent());
        }
        rc.setIndicatorString("ITERS: " + iters);
        if (rc.isActionReady()) tryAttack();
    }

    public void tryAttack() throws GameActionException {
        RobotInfo bestenemy = null;
        int besthealth = 1 << 30;
        for (int i = enemies.length; i-- > 0;) {
            if ((enemies[i].health < besthealth) && (rc.canAttack(enemies[i].location))) {
                bestenemy = enemies[i];
                besthealth = enemies[i].health;
            }
        }
        if ((bestenemy != null) && (rc.canAttack(bestenemy.location))) {
            rc.attack(bestenemy.location);
        }
    }

    // Choose best candidate for maneuvering in close encounters.
    class MicroTarget {
        int minDistToEnemy = 100000;
        int minDistToAlly = 100000;
        int healInVisionRange = 0;
        int dmgInAttackRange = 0;
        int dmgInVisionRange = 0;
        int friendsNearby = 0;
        boolean canMove;
        int canLandHit;
        MapLocation nloc;
        Direction dir;

        MicroTarget(Direction dir) throws GameActionException {
            nloc = rc.getLocation().add(dir);
            canMove = rc.canMove(dir);
            this.dir = dir;
        }
        
        void addEnemy(RobotInfo r) throws GameActionException {
            int dist = r.location.distanceSquaredTo(nloc);
            if (dist <= GameConstants.ATTACK_RADIUS_SQUARED && canAttack){
                canLandHit = 6;
            }
            if (r.hasFlag) return;
            if (dist < minDistToEnemy) minDistToEnemy = dist;
            if (dist <= GameConstants.ATTACK_RADIUS_SQUARED) dmgInAttackRange += 6;
            if (dist <= GameConstants.VISION_RADIUS_SQUARED) dmgInVisionRange += 6;            
        } 

        
        void addAlly(RobotInfo r) throws GameActionException {
            if (!canMove) return;
            if (r.hasFlag) return;
            int d = nloc.distanceSquaredTo(r.location);
            if (d < minDistToAlly) minDistToAlly = d;
            if (d <= GameConstants.ATTACK_RADIUS_SQUARED) healInVisionRange += 2;
            if (d <= 2) friendsNearby += 1;
        }

        boolean inRange() {
            return minDistToEnemy <= GameConstants.ATTACK_RADIUS_SQUARED;
        }

        int attackScore() {
            return (Math.max(dmgInAttackRange - healInVisionRange, 0) - canLandHit);
        }

        int visionScore() {
            return (Math.max(dmgInVisionRange - healInVisionRange, 0) - canLandHit);
        }

        // If I can't move don't waste computation.
        // If something can attack me GET AWAY.
        // If I'm a
        // Tie-break on net damage received, first.
        boolean isBetterThan(MicroTarget mt) {
            if (!canMove) return false;
            if (rc.getHealth() < (GameConstants.DEFAULT_HEALTH / 4)) {
                if (dmgInAttackRange < mt.dmgInAttackRange) return true;
                if (dmgInAttackRange > mt.dmgInAttackRange) return false;
                return minDistToEnemy > mt.minDistToEnemy;
            } else {
                if (attackScore() < mt.attackScore()) return true;
                if (attackScore() > mt.attackScore()) return false;
                if (3 * visionScore() < 2 * visionScore()) return true;
                if (2 * visionScore() > 3 * visionScore()) return false;
                return friendsNearby < mt.friendsNearby;
            }
        }
    }
}

// If I have decent health - 
//  If I have an attack, and there's nothing in range, there's no rush, let them come to you.
//  If I have an attack, and something's in range, attack it, then back away to some safe location.
//  If I can't attack something, stay where I am or move to a sparser (but still safe) location.
// Otherwise -
//  Backup to a safe location.
