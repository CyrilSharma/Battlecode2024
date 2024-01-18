package initialBot;
import battlecode.common.*;

public class AttackMicro {
    long enemy_mask0 = 0;
    long enemy_mask1 = 0;
    long friend_mask0 = 0;
    long friend_mask1 = 0;
    RobotInfo[] friends = null;
    RobotInfo[] enemies = null;

    int mydmg;
    boolean canAttack;
    int[] healscores = new int[7];
    int[] dmgscores = new int[7];

    RobotController rc;
    Communications comms;
    MapTracker mt;
    Pathing path;
    MapLocation[] spawnCenters = null;
    SymmetryChecker sc;
    NeighborLoader nl;
    public AttackMicro(Robot r) {
        this.rc = r.rc;
        this.comms = r.communications;
        this.path = new Pathing(r);
        this.sc = r.sc;
        this.mt = r.mt;
        this.computeScores();
    }

    public void computeScores() {
        for (int i = 0; i < 7; i++) {
            healscores[i] = (100 + SkillType.HEAL.getSkillEffect(i)) * 100 /
                    (100 + SkillType.HEAL.getCooldown(i));
            dmgscores[i] = 2 * (100 + SkillType.ATTACK.getSkillEffect(i)) * 100 /
                    (100 + SkillType.ATTACK.getCooldown(i));
        }
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

    public void bombpath() throws GameActionException {
        long pprev0 = 0;
        long pprev1 = 0;
        long prev0 = 0;
        long prev1 = 0;
        long cur0 = enemy_mask0;
        long cur1 = enemy_mask1;
        long temp = 0;

        long passible0 = ~(mt.wall_mask0 | mt.water_mask0 | mt.bomb_mask0);
        long passible1 = ~(mt.wall_mask1 | mt.water_mask1 | mt.bomb_mask1);
        long loverflow = 0x7fbfdfeff7fbfdfeL;
        long roverflow = 0x3fdfeff7fbfdfeffL;
        while ((cur0 != prev0 || cur1 != prev1)) {
            cur0 = (cur0 | ((cur0 << 1) & loverflow) | ((cur0 >> 1) & roverflow));
            cur1 = (cur1 | ((cur1 << 1) & loverflow) | ((cur1 >> 1) & roverflow));
            temp = cur0;
            cur0 = (cur0 | (cur0 << 9) | (cur0 >> 9) | (cur1 << 54)) & passible0;
            cur1 = (cur1 | (cur1 << 9) | (cur1 >> 9) | (temp >> 54)) & passible1;
            pprev0 = prev0;
            pprev1 = prev1;
            prev0 = cur0;
            prev1 = cur1;
        }

        if (((cur0 & 0x7FFFFFFFFL) != 0x7FFFFFFFFL) ||
            ((cur1 & 0x7FFFFFFFL) != 0x7FFFFFFFFL)) {
            prev0 = 0;
            prev1 = 0;
            while ((cur0 != prev0 || cur1 != prev1)) {
                cur0 = (cur0 | ((cur0 << 1) & loverflow) | ((cur0 >> 1) & roverflow));
                cur1 = (cur1 | ((cur1 << 1) & loverflow) | ((cur1 >> 1) & roverflow));
                temp = cur0;
                cur0 = (cur0 | (cur0 << 9) | (cur0 >> 9) | (cur1 << 54));
                cur1 = (cur1 | (cur1 << 9) | (cur1 >> 9) | (temp >> 54));
                pprev0 = prev0;
                pprev1 = prev1;
                prev0 = cur0;
                prev1 = cur1;
            }
        }

        long del0 = (prev0 & ~pprev0);
        long del1 = (prev1 & ~pprev1);
        del0 = (del0 & -del0);
        del1 = (del1 & -del1);

        int idx = 0;
        for (int i = 0; i < 64; i += 8, del0 >>>= 8) {
            switch ((int)(del0 & 0xF)) {
                case 0: continue;
                case 0b1: idx = 0 + i; break;
                case 0b10: idx = 1 + i; break;
                case 0b100: idx = 2 + i; break;
                case 0b1000: idx = 3 + i; break;
                case 0b10000: idx = 4 + i; break;
                case 0b100000: idx = 5 + i; break;
                case 0b1000000: idx = 6 + i; break;
                case 0b10000000: idx = 7 + i; break;
            }
            break;
        }
        for (int i = 64; i < 81; i += 8, del1 >>>= 8) {
            switch ((int)(del1 & 0xF)) {
                case 0: continue;
                case 0b1: idx = 0 + i; break;
                case 0b10: idx = 1 + i; break;
                case 0b100: idx = 2 + i; break;
                case 0b1000: idx = 3 + i; break;
                case 0b10000: idx = 4 + i; break;
                case 0b100000: idx = 5 + i; break;
                case 0b1000000: idx = 6 + i; break;
                case 0b10000000: idx = 7 + i; break;
            }
            break;
        }
        int dy = (idx) / 9;
        int dx = (idx) % 9;
        MapLocation target = rc.getLocation().translate(dx - 4, dy - 4);
        path.moveTo(target);
    }

    public boolean notNearSpawn() throws GameActionException {
        for (MapLocation m : spawnCenters) {
            if (rc.getLocation().distanceSquaredTo(m) < 49) return false;
            if (sc.getSymLoc(m) != null) {
                if (rc.getLocation().distanceSquaredTo(sc.getSymLoc(m)) < 49) return false;
            }
            else {
                int status = rc.readSharedArray(Channels.SYMMETRY);
                if ((status & 1) == 0) {
                    if (rc.getLocation().distanceSquaredTo(sc.getHSym(m)) < 49) return false;
                }
                if (((status >> 1) & 1) == 0) {
                    if (rc.getLocation().distanceSquaredTo(sc.getVSym(m)) < 49) return false;
                }
                if (((status >> 2) & 1) == 0) {
                    if (rc.getLocation().distanceSquaredTo(sc.getRSym(m)) < 49) return false;
                }
            }
        }
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
        while (tryAttack()) ;

        canAttack = rc.isActionReady();
        mydmg = dmgscores[rc.getLevel(SkillType.ATTACK)];

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
            if (Clock.getBytecodesLeft() < 3000) break;
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
            if (Clock.getBytecodesLeft() < 3000) break;
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

        // microtargets[0].displayHitMask();
        // microtargets[0].displayHitMask();

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
        while (tryAttack()) ;
    }

    public boolean tryAttack() throws GameActionException {
        if (!rc.isActionReady()) return false;
        RobotInfo bestenemy = null;
        int besthealth = 1 << 30;
        for (int i = enemies.length; i-- > 0;) {
            if ((enemies[i].health < besthealth) && (rc.canAttack(enemies[i].location))) {
                bestenemy = enemies[i];
                besthealth = enemies[i].health;
            }
        }
        for (int i = enemies.length; i-- > 0;) {
            if (enemies[i].hasFlag && rc.canAttack(enemies[i].location)) bestenemy = enemies[i];
        }
        if ((bestenemy != null) && (rc.canAttack(bestenemy.location))) {
            rc.attack(bestenemy.location);
            return true;
        }
        return false;
    }

    // Choose best candidate for maneuvering in close encounters.
    class MicroTarget {
        // Debugging purposes only.
        // long hits0 = 0;
        // long hits1 = 0;
        long close0 = 0;
        long close1 = 0;
        int minDistToEnemy = 100000;
        int minDistToAlly = 100000;
        int healAttackRange = 0;
        int dmgAttackRange = 0;
        int dmgVisionRange = 0;
        boolean canMove;
        boolean isBuilder;
        int canLandHit;
        MapLocation nloc;
        MapLocation bl;
        Direction dir;
        int minDistToBuilder = -1;

        MicroTarget(Direction dir) throws GameActionException {
            MapLocation myloc = rc.getLocation();
            nloc = myloc.add(dir);
            bl = myloc.translate(-4, -4);
            canMove = rc.canMove(dir);
            this.dir = dir;
            computeHitMask();
        }

        void displayHitMask() throws GameActionException {
            rc.setIndicatorString("HitMask for " + dir);
            Util.displayMask(rc, close0, close1);
            // Util.displayMask(rc, hits0, hits1);
            rc.setIndicatorDot(nloc, 255, 165, 0);
        }

        // It's not too much overhead I promise.
        void computeHitMask() throws GameActionException {
            long action0 = 0b000010000000111000001111100000111000000010000000000000000000000L;
            long action1 = 0;
            switch (dir) {
                case NORTHEAST:     action0 <<= 10; action1 = 0b000001000; break;
                case NORTH:         action0 <<= 9;  action1 = 0b000010000; break;
                case NORTHWEST:     action0 <<= 8;  action1 = 0b000100000; break;
                case EAST:          action0 <<= 1;  break;
                case CENTER:        break;
                case WEST:          action0 >>= 1;  break;
                case SOUTHEAST:     action0 >>= 8;  break;
                case SOUTH:         action0 >>= 9;  break;
                case SOUTHWEST:     action0 >>= 10; break;
            }

            long passible0 = ~(mt.wall_mask0 | mt.water_mask0);
            long passible1 = ~(mt.wall_mask1 | mt.water_mask1);
            long loverflow = 0x7fbfdfeff7fbfdfeL;
            long roverflow = 0x3fdfeff7fbfdfeffL;            
            long t_close0 = (action0 & passible0);
            long t_close1 = (action1 & passible1);;
            long temp = 0;
            for (int i = 1; i-- > 0;) {
                t_close0 = (t_close0 | ((t_close0 << 1) & loverflow) | ((t_close0 >> 1) & roverflow));
                t_close1 = (t_close1 | ((t_close1 << 1) & loverflow) | ((t_close1 >> 1) & roverflow));
                temp = t_close0;
                t_close0 = (t_close0 | (t_close0 << 9) | (t_close0 >> 9) | (t_close1 << 54)) & passible0;
                t_close1 = (t_close1 | (t_close1 << 9) | (t_close1 >> 9) | (temp >> 54)) & passible1;
            }
            close0 = t_close0;
            close1 = t_close1;
        }

        boolean canHitSoon(MapLocation loc) throws GameActionException {
            int idx = (9 * (loc.y - bl.y)) + (loc.x - bl.x);
            if (idx >= 63) {
                // hits1 |= (close1 & (1L << (idx - 63)));
                return ((close1 & (1L << (idx - 63))) != 0);
            }
            else {
                // hits0 |= (close0 & (1L << idx));
                return ((close0 & (1L << idx)) != 0);
            }
        }
        
        void addEnemy(RobotInfo r) throws GameActionException {
            if (r.hasFlag) return;
            int dist = r.location.distanceSquaredTo(nloc);
            if (dist < minDistToEnemy) minDistToEnemy = dist;
            if (canHitSoon(r.location)) {
                int dmg = dmgscores[r.attackLevel];
                dmgVisionRange += dmg;            
                if (dist <= GameConstants.ATTACK_RADIUS_SQUARED) {
                    dmgAttackRange += dmg;
                    if (canAttack) canLandHit = mydmg;
                }
            }
        }
        
        void addAlly(RobotInfo r) throws GameActionException {
            if (!canMove) return;
            if (r.hasFlag) return;
            int d = nloc.distanceSquaredTo(r.location);
            if (d < minDistToAlly) minDistToAlly = d;
            if (d <= GameConstants.ATTACK_RADIUS_SQUARED) {
                healAttackRange += healscores[r.healLevel];
            }
        }

        boolean inRange() {
            return minDistToEnemy <= GameConstants.ATTACK_RADIUS_SQUARED;
        }

        int attackScore() {
            return (Math.max(dmgAttackRange - healAttackRange, 0) - canLandHit);
        }

        int visionScore() {
            return (Math.max(dmgVisionRange - healAttackRange, 0) - canLandHit);
        }

        boolean isBetterThan(MicroTarget mt) {
            if (!canMove) return false;
            if (rc.getHealth() <= GameConstants.DEFAULT_HEALTH / 4) {
                return minDistToEnemy > mt.minDistToEnemy;
            }

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
