package jan20;
import battlecode.common.*;

public class AttackMicro {
    int mydmg;
    int lastactivated = -1;
    boolean canAttack;
    RobotController rc;
    long enemy_mask0 = 0;
    long enemy_mask1 = 0;
    long friend_mask0 = 0;
    long friend_mask1 = 0;
    RobotInfo[] friends = null;
    RobotInfo[] enemies = null;
    int[] healscores = new int[7];
    int[] dmgscores = new int[7];

    boolean updatedScores = false;
    boolean seeEnemyFlagCarrier = false;
    MapLocation carrier = null;
    NeighborLoader nl;
    Communications comms;
    boolean attacker = false;
    MapTracker mt;
    public AttackMicro(Robot r) {
        this.rc = r.rc;
        this.comms = r.communications;
        this.mt = r.mt;
        if (comms.order >= 30) attacker = true;
        this.nl = new NeighborLoader(rc);
        computeScores(false);
    }

    public boolean hasAttackUpgrade() {
        GlobalUpgrade[] ug = rc.getGlobalUpgrades(rc.getTeam());
        for (int i = ug.length; i-- > 0;) {
            if (ug[i] == GlobalUpgrade.ACTION) return true;
        }
        return false;
    }

    public void computeScores(boolean attackupgrade) {
        int atop = (attackupgrade) ? 225 : 150;
        int abottom = 150;
        for (int i = 0; i < 7; i++) {
            healscores[i] = (100 + SkillType.HEAL.getSkillEffect(i)) * 100 /
                    (100 + SkillType.HEAL.getCooldown(i));
            dmgscores[i] = (2 * atop * (100 + SkillType.ATTACK.getSkillEffect(i)) * 100) /
                        (abottom * (100 + SkillType.ATTACK.getCooldown(i)));
        }
    } 

    public boolean runMicro() throws GameActionException {
        nl.load(this);
        if (enemies.length == 0) return false;
        if (rc.getRoundNum() < GameConstants.SETUP_ROUNDS) return false;
        lastactivated = rc.getRoundNum();
        if (hasAttackUpgrade() && !updatedScores) {
            computeScores(true);
            updatedScores = true;
        }
        seeEnemyFlagCarrier = false;
        addBestTarget();
        // int bombcount = Long.bitCount(mt.bomb_mask0) + Long.bitCount(mt.bomb_mask1);
        // if ((bombcount >= 3 && enemies.length >= 3)) {
        //     bombpath();
        //     rc.setIndicatorDot(rc.getLocation(), 179, 3, 33);
        // } else {
        //     maneuver();
        // }
        maneuver();
        return true;
    }

    // public boolean shouldRunMicro(MapLocation target) throws GameActionException {
    //     if (target == null) return enemies.length != 0;
    //     MapLocation myloc = rc.getLocation();
    //     Direction dirtarget = myloc.directionTo(target);
    //     for (int i = enemies.length; i-- > 0;) {
    //         MapLocation eloc = enemies[i].location;
    //         Direction direnemy = myloc.directionTo(eloc);
    //         if ((direnemy != dirtarget.opposite()) &&
    //             (direnemy != dirtarget.opposite().rotateRight()) &&
    //             (direnemy != dirtarget.opposite().rotateLeft())) {
    //             continue;
    //         }
    //         return true;
    //     }
    //     return false;
    // }

    // Tracks density of enemies.
    public void addBestTarget() throws GameActionException {
        comms.addAttackTarget(
            rc.getLocation(),
            Math.min(enemies.length, 15)
        );
    }

    public void bombpath() throws GameActionException {
        rc.setIndicatorString("Bomb Pathing!");
        long mask0 = 0x7FFFFFFFFFFFFFFFL;
        long mask1 = 0x3FFFFL;
        long loverflow0 = 0x7fbfdfeff7fbfdfeL & mask0;
        long roverflow0 = 0x3fdfeff7fbfdfeffL & mask0;
        long loverflow1 = 0x7fbfdfeff7fbfdfeL & mask1;
        long roverflow1 = 0x3fdfeff7fbfdfeffL & mask1;
        long passible0 = 0;
        long passible1 = 0;
        long temp = 0;

        // Compute the reachability mask.
        long reach0 = 1099511627776L;
        long reach1 = 0L;
        passible0 = ~(mt.wall_mask0 | mt.water_mask0 | mt.adjblocked) & mask0;
        passible1 = ~(mt.wall_mask1 | mt.water_mask1) & mask1;
        for (int i = 10; i-- > 0;) {
            reach0 = (reach0 | ((reach0 << 1) & loverflow0) | ((reach0 >>> 1) & roverflow0));
            reach1 = (reach1 | ((reach1 << 1) & loverflow1) | ((reach1 >>> 1) & roverflow1));
            temp = reach0;
            reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
            reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
        }

        // Using the enemy mask as a starting point, shift it around, assuming impassible bombs,
        // Using our normal bitmasking tricks. Do this until you reach a fixed point.
        long pprev0 = 0;
        long pprev1 = 0;
        long prev0 = 0;
        long prev1 = 0;
        long cur0 = enemy_mask0;
        long cur1 = enemy_mask1;
        passible0 = ~(mt.wall_mask0 | mt.water_mask0 | mt.bomb_mask0) & mask0;
        passible1 = ~(mt.wall_mask1 | mt.water_mask1 | mt.bomb_mask1) & mask1;
        while ((cur0 != prev0 || cur1 != prev1)) {
            rc.setIndicatorString("Stuck in L1!");
            pprev0 = prev0;
            pprev1 = prev1;
            prev0 = cur0;
            prev1 = cur1;
            cur0 = (cur0 | ((cur0 << 1) & loverflow0) | ((cur0 >>> 1) & roverflow0));
            cur1 = (cur1 | ((cur1 << 1) & loverflow1) | ((cur1 >>> 1) & roverflow1));
            temp = cur0;
            cur0 = (cur0 | (cur0 << 9) | (cur0 >>> 9) | (cur1 << 54)) & passible0;
            cur1 = (cur1 | (cur1 << 9) | (cur1 >>> 9) | (temp >>> 54)) & passible1;
        }

        // Reset it to before the redundant iteration.
        prev0 = pprev0;
        prev1 = pprev1;
        rc.setIndicatorString("Past L1!");
        if ((cur0 & reach0) == 0 && (cur1 & reach1) == 0) {
            MapLocation myloc = rc.getLocation();
            int dists[] = new int[9];
            for (Direction d: Direction.values()) {
                dists[d.ordinal()] = 1 << 30;
                MapLocation loc = myloc.add(d);
                if (!rc.canMove(d)) continue;
                int bestdist = 1 << 30;
                for (RobotInfo r: enemies) {
                    int dist = loc.distanceSquaredTo(r.location);
                    if ((dist < bestdist)) {
                        bestdist = dist;
                    }
                }
                dists[d.ordinal()] = bestdist;
            }

            int bestd = -1;
            Direction bestdir = null;
            for (Direction d: Direction.values()) {
                if (!rc.canMove(d)) continue;
                int dist = dists[d.ordinal()];
                if (dist > bestd) {
                    bestd = dist;
                    bestdir = d;
                }
            }
            if ((bestdir != null) && (rc.canMove(bestdir))) {
                rc.move(bestdir);
            }
        }

        // should probably choose the location furthest from enemies.
        // should be guaranteed reachable?
        long del0 = cur0 & ~prev0;
        long del1 = cur1 & ~prev1;
        
        int idx = 0;
        int nz0 = Long.numberOfTrailingZeros(del0);
        int nz1 = Long.numberOfTrailingZeros(del1);
        if (nz0 == 64) idx = 63 + nz1;
        else idx = nz0;
        int dy = (idx / 9);
        int dx = (idx % 9);
        MapLocation target = rc.getLocation().translate(dx - 4, dy - 4);
        rc.setIndicatorDot(target, 0, 0, 0);

        // passible0 = ~(mt.wall_mask0 | mt.water_mask0 | mt.adjblocked) & mask0;
        // passible1 = ~(mt.wall_mask1 | mt.water_mask1) & mask1;
        // while ((del0 & 0x70381c0000000L) == 0) {
        //     rc.setIndicatorString("Stuck in L4!");
        //     del0 = (del0 | ((del0 << 1) & loverflow0) | ((del0 >>> 1) & roverflow0));
        //     del1 = (del1 | ((del1 << 1) & loverflow1) | ((del1 >>> 1) & roverflow1));
        //     temp = del0;
        //     del0 = (del0 | (del0 << 9) | (del0 >>> 9) | (del1 << 54)) & passible0;
        //     del1 = (del1 | (del1 << 9) | (del1 >>> 9) | (temp >>> 54)) & passible1;
        // }
        // rc.setIndicatorString("Past L4!");

        // Target square is in vision range so no need to compute
        // `best` direction, i.e closest to target location.
        Direction bestDir = null;
        int bestDist = (1 << 30);
        MapLocation loc = null;
        int d = 0;

        for (Direction dir: Direction.values()) {
            loc = rc.adjacentLocation(dir);
            d = target.distanceSquaredTo(loc);
            if ((d < bestDist) && rc.canMove(dir)) {
                bestDir = dir;
                bestDist = d;
            }
        }
        
        if (bestDir != null) {
            rc.setIndicatorString("Best Dir: " + bestDir);
            if (rc.canMove(bestDir)) {
                rc.move(bestDir);
            }
        }
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
            if (r.hasFlag) seeEnemyFlagCarrier = true;
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
        rc.setIndicatorString("Iters: " + iters);
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
        int offset = 0;
        long close0 = 0;
        long close1 = 0;
        int minDistToEnemy = 100000;
        int minDistToAlly = 100000;
        int minDistToFlag = 100000;
        int healAttackRange = 0;
        int dmgAttackRange = 0;
        int dmgVisionRange = 0;
        boolean canMove;
        int canLandHit;
        MapLocation nloc;
        MapLocation bl;
        Direction dir;

        MicroTarget(Direction dir) throws GameActionException {
            MapLocation myloc = rc.getLocation();
            nloc = myloc.add(dir);
            bl = myloc.translate(-4, -4);
            offset = bl.hashCode();
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
                case WEST:          action0 >>>= 1;  break;
                case SOUTHEAST:     action0 >>>= 8;  break;
                case SOUTH:         action0 >>>= 9;  break;
                case SOUTHWEST:     action0 >>>= 10; break;
            }

            long mask0 = 0x7FFFFFFFFFFFFFFFL;
            long mask1 = 0x3FFFFL;
            long passible0 = ~(mt.wall_mask0 | mt.water_mask0) & mask0;
            long passible1 = ~(mt.wall_mask1 | mt.water_mask1) & mask1;
            long loverflow = 0x7fbfdfeff7fbfdfeL;
            long roverflow = 0x3fdfeff7fbfdfeffL;            
            long t_close0 = (action0 & passible0);
            long t_close1 = (action1 & passible1);;
            long temp = 0;
            for (int i = 1; i-- > 0;) {
                t_close0 = (t_close0 | ((t_close0 << 1) & loverflow) | ((t_close0 >>> 1) & roverflow));
                t_close1 = (t_close1 | ((t_close1 << 1) & loverflow) | ((t_close1 >>> 1) & roverflow));
                temp = t_close0;
                t_close0 = (t_close0 | (t_close0 << 9) | (t_close0 >>> 9) | (t_close1 << 54)) & passible0;
                t_close1 = (t_close1 | (t_close1 << 9) | (t_close1 >>> 9) | (temp >>> 54)) & passible1;
            }
            close0 = t_close0;
            close1 = t_close1;
        }

        long canHitSoon(MapLocation loc) throws GameActionException {
            switch (loc.hashCode() - offset) {
                case 0: return (close0 & 0x1L);
                case 65536: return (close0 & 0x2L);
                case 131072: return (close0 & 0x4L);
                case 196608: return (close0 & 0x8L);
                case 262144: return (close0 & 0x10L);
                case 327680: return (close0 & 0x20L);
                case 393216: return (close0 & 0x40L);
                case 458752: return (close0 & 0x80L);
                case 524288: return (close0 & 0x100L);
                case 1: return (close0 & 0x200L);
                case 65537: return (close0 & 0x400L);
                case 131073: return (close0 & 0x800L);
                case 196609: return (close0 & 0x1000L);
                case 262145: return (close0 & 0x2000L);
                case 327681: return (close0 & 0x4000L);
                case 393217: return (close0 & 0x8000L);
                case 458753: return (close0 & 0x10000L);
                case 524289: return (close0 & 0x20000L);
                case 2: return (close0 & 0x40000L);
                case 65538: return (close0 & 0x80000L);
                case 131074: return (close0 & 0x100000L);
                case 196610: return (close0 & 0x200000L);
                case 262146: return (close0 & 0x400000L);
                case 327682: return (close0 & 0x800000L);
                case 393218: return (close0 & 0x1000000L);
                case 458754: return (close0 & 0x2000000L);
                case 524290: return (close0 & 0x4000000L);
                case 3: return (close0 & 0x8000000L);
                case 65539: return (close0 & 0x10000000L);
                case 131075: return (close0 & 0x20000000L);
                case 196611: return (close0 & 0x40000000L);
                case 262147: return (close0 & 0x80000000L);
                case 327683: return (close0 & 0x100000000L);
                case 393219: return (close0 & 0x200000000L);
                case 458755: return (close0 & 0x400000000L);
                case 524291: return (close0 & 0x800000000L);
                case 4: return (close0 & 0x1000000000L);
                case 65540: return (close0 & 0x2000000000L);
                case 131076: return (close0 & 0x4000000000L);
                case 196612: return (close0 & 0x8000000000L);
                case 262148: return (close0 & 0x10000000000L);
                case 327684: return (close0 & 0x20000000000L);
                case 393220: return (close0 & 0x40000000000L);
                case 458756: return (close0 & 0x80000000000L);
                case 524292: return (close0 & 0x100000000000L);
                case 5: return (close0 & 0x200000000000L);
                case 65541: return (close0 & 0x400000000000L);
                case 131077: return (close0 & 0x800000000000L);
                case 196613: return (close0 & 0x1000000000000L);
                case 262149: return (close0 & 0x2000000000000L);
                case 327685: return (close0 & 0x4000000000000L);
                case 393221: return (close0 & 0x8000000000000L);
                case 458757: return (close0 & 0x10000000000000L);
                case 524293: return (close0 & 0x20000000000000L);
                case 6: return (close0 & 0x40000000000000L);
                case 65542: return (close0 & 0x80000000000000L);
                case 131078: return (close0 & 0x100000000000000L);
                case 196614: return (close0 & 0x200000000000000L);
                case 262150: return (close0 & 0x400000000000000L);
                case 327686: return (close0 & 0x800000000000000L);
                case 393222: return (close0 & 0x1000000000000000L);
                case 458758: return (close0 & 0x2000000000000000L);
                case 524294: return (close0 & 0x4000000000000000L);
                case 7: return (close1 & 0x1L);
                case 65543: return (close1 & 0x2L);
                case 131079: return (close1 & 0x4L);
                case 196615: return (close1 & 0x8L);
                case 262151: return (close1 & 0x10L);
                case 327687: return (close1 & 0x20L);
                case 393223: return (close1 & 0x40L);
                case 458759: return (close1 & 0x80L);
                case 524295: return (close1 & 0x100L);
                case 8: return (close1 & 0x200L);
                case 65544: return (close1 & 0x400L);
                case 131080: return (close1 & 0x800L);
                case 196616: return (close1 & 0x1000L);
                case 262152: return (close1 & 0x2000L);
                case 327688: return (close1 & 0x4000L);
                case 393224: return (close1 & 0x8000L);
                case 458760: return (close1 & 0x10000L);
                case 524296: return (close1 & 0x20000L);
                default: return 0;
            }
        }
        
        void addEnemy(RobotInfo r) throws GameActionException {
            int dist = r.location.distanceSquaredTo(nloc);
            if (dist <= GameConstants.ATTACK_RADIUS_SQUARED && canAttack){
                canLandHit = mydmg;
            }
            if (r.hasFlag) {
                if (dist < minDistToFlag) {
                    minDistToFlag = dist;
                    carrier = r.location;
                }
                return;
            }
            if (dist < minDistToEnemy) minDistToEnemy = dist;

            int dmg = dmgscores[r.attackLevel];
            if (dist <= GameConstants.ATTACK_RADIUS_SQUARED) dmgAttackRange += dmg;
            if (canHitSoon(r.location) != 0) dmgVisionRange += dmg;
        }
        
        void addAlly(RobotInfo r) throws GameActionException {
            if (!canMove) return;
            if (r.hasFlag) return;
            int d = nloc.distanceSquaredTo(r.location);
            if (d < minDistToAlly) minDistToAlly = d;
            if (d <= GameConstants.ATTACK_RADIUS_SQUARED) {
                healAttackRange += healscores[r.healLevel] * (attacker ? 2 : 1);
            }
        }

        boolean inRange() {
            return minDistToEnemy <= GameConstants.ATTACK_RADIUS_SQUARED;
        }

        int attackScore() {
            return (Math.max(dmgAttackRange - healAttackRange, 0) - canLandHit * (attacker ? 2 : 1));
        }

        int visionScore() {
            return (Math.max(dmgVisionRange - healAttackRange, 0) - canLandHit * (attacker ? 2 : 1));
        }

        boolean isBetterThan(MicroTarget mt) {
            if (!canMove) return false;
            if (seeEnemyFlagCarrier && rc.getLocation().distanceSquaredTo(carrier) > 3) {
                if (minDistToFlag < mt.minDistToFlag) return true;
                if (minDistToFlag > mt.minDistToFlag) return false;
            }

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
