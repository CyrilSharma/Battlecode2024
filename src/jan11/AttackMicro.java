package jan11;
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
        if (enemies.length == 0) return false;
        addBestTarget();
        maneuver();
        return true;
    }

    // Finds the enemy closest to a flag, and marks it in comms.
    public void addBestTarget() throws GameActionException {
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
        int count = 0;
        MicroTarget[] microtargets = new MicroTarget[9];
        for (Direction dir: Direction.values()) { 
            microtargets[count++] = new MicroTarget(dir);
        }

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
        int healersVisionRange = 0;
        int enemiesAttackRange = 0;
        int enemiesVisionRange = 0;
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
                canLandHit = 1;
            }
            if (r.hasFlag) return;
            if (dist < minDistToEnemy) minDistToEnemy = dist;
            if (dist <= GameConstants.ATTACK_RADIUS_SQUARED) enemiesAttackRange++;
            if (dist <= GameConstants.VISION_RADIUS_SQUARED) enemiesVisionRange++;            
        } 

        
        void addAlly(RobotInfo r) throws GameActionException {
            if (!canMove) return;
            if (r.hasFlag) return;
            int d = nloc.distanceSquaredTo(r.location);
            if (d < minDistToAlly) minDistToAlly = d;
            if (d <= GameConstants.ATTACK_RADIUS_SQUARED) healersVisionRange++;
        }

        boolean inRange() {
            return minDistToEnemy <= GameConstants.ATTACK_RADIUS_SQUARED;
        }

        int attackScore() {
            return (Math.max(enemiesAttackRange - healersVisionRange / 2, 0) - canLandHit);
        }

        int visionScore() {
            return (Math.max(enemiesVisionRange - healersVisionRange / 2, 0) - canLandHit);
        }

        boolean isBetterThan(MicroTarget mt) {
            if (!canMove) return false;

            if (attackScore() < mt.attackScore()) return true;
            if (attackScore() > mt.attackScore()) return false;

            if (visionScore() < mt.visionScore()) return true;
            if (visionScore() > mt.visionScore()) return false;
            
            if (canLandHit > mt.canLandHit) return true;
            if (canLandHit < mt.canLandHit) return false;

            if (minDistToAlly < mt.minDistToAlly) return true;
            if (minDistToAlly > mt.minDistToAlly) return false;

            if (mt.inRange()) return minDistToEnemy >= mt.minDistToEnemy;
            else return minDistToEnemy <= mt.minDistToEnemy;
        }
    }
}
