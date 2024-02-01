package finalbaseline;
import battlecode.common.*;
public class AttackMicro {
    int mydmg = -1;
    boolean hurt = false;
    int lastactivated = -1;
    int[] healscores = new int[7];
    int[] dmgscores = new int[7];

    // Flags
    boolean canAttack = false;
    boolean attacker = false;
    boolean healer = false;
    boolean updatedScores = false;

    // Utilities
    RobotController rc;
    StunManager sm;
    MapLocation carrier = null;
    Communications comms;
    MapTracker mt;
    NeighborTracker nt;
    MapLocation[] spawnCenters;
    boolean seeEnemyFlagCarrier;
    int distToSpawn;
    public AttackMicro(Duck d) {
        this.rc = d.rc;
        this.comms = d.communications;
        this.mt = d.mt;
        this.sm = d.sm;
        this.nt = d.nt;
        this.spawnCenters = d.spawnCenters;
        computeScores(false);
    }

    public boolean tryAttack() throws GameActionException {
        if (!rc.isActionReady()) return false;
        RobotInfo bestenemy = null;
        int besthealth = 1 << 30;
        RobotInfo[] enemies = nt.enemies;
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

    // Tracks density of enemies.
    public void addBestTarget() throws GameActionException {
        comms.addAttackTarget(
            rc.getLocation(),
            Math.min(nt.enemies.length, 15)
        );
    }

    public boolean runMicro() throws GameActionException {
        attacker = comms.order >= 30;
        healer = comms.order > 6 && comms.order < 30;
        if(attacker && rc.getHealth() < 300){
            attacker = false;
            healer = true;
        }
        if (rc.hasFlag()) return false;
        if (nt.enemies.length == 0) return false;
        if (rc.getRoundNum() < GameConstants.SETUP_ROUNDS) return false;
        rc.setIndicatorDot(rc.getLocation(), 0, 255, 0);
        if (!closeToEnemies()) return false;
        lastactivated = rc.getRoundNum();
        if (hasAttackUpgrade() && !updatedScores) {
            computeScores(true);
            updatedScores = true;
        }
        computeClosestSpawn();
        addBestTarget();
        while (tryAttack()) ;
        micro();
        while (tryAttack()) ;
        return true;
    }

    public void computeClosestSpawn() throws GameActionException {
        distToSpawn = 1000000;
        for (int i = 0; i < 3; i++) {
            int d = rc.getLocation().distanceSquaredTo(spawnCenters[i]);
            if(d < distToSpawn) distToSpawn = d;
        }
    }

    boolean healer(int id){
        return comms.ord[id - 10000] > 6 && comms.ord[id - 10000] < 30;
    }

    boolean attacker(int id){
        return comms.ord[id - 10000] >= 30;
    }

    public boolean closeToEnemies() throws GameActionException {
        long action0 = 0b000010000000111000001111100000111000000010000000000000000000000L;
        if ((nt.enemy_mask0 & action0) == 0) {
            // you couldn't attack an enemy from this location.
            // Additionally, the terrain is highly impassable, indicated by the fact
            // that there is no short path to the enemy. (or there is no path at all)
            long loverflow = 0x7fbfdfeff7fbfdfeL;
            long roverflow = 0x3fdfeff7fbfdfeffL;
            long mask0 = 0x7FFFFFFFFFFFFFFFL;
            long mask1 = 0x3FFFFL;
            // water and walls, since attack micro never goes in water
            long passible0 = (~(mt.wall_mask0 | mt.water_mask0)) & mask0;
            long passible1 = (~(mt.wall_mask1 | mt.water_mask1)) & mask1;
            long reach0 = 1099511627776L;
            long reach1 = 0;
            int i = 0;
            while ((((reach0 & nt.enemy_mask0) | (reach1 & nt.enemy_mask1)) == 0) && i < 8) {
                reach0 = (reach0 | ((reach0 << 1) & loverflow) | ((reach0 >>> 1) & roverflow));
                reach1 = (reach1 | ((reach1 << 1) & loverflow) | ((reach1 >>> 1) & roverflow));
                long temp = reach0;
                reach0 = (reach0 | (reach0 << 9) | (reach0 >>> 9) | (reach1 << 54)) & passible0;
                reach1 = (reach1 | (reach1 << 9) | (reach1 >>> 9) | (temp >>> 54)) & passible1;
                i++;
            }
            if (i >= 8) return false;
        }
        return true;
    }

    public void micro() throws GameActionException {
        // We can compute more stats here too, like,
        // Am I in a 1v1? Is every enemy stunned? etc.
        rc.setIndicatorString("Not Movement Ready?");
        if (!rc.isMovementReady()) return;
        /* boolean hasFlag = false;
        MapLocation floc = null;
        RobotInfo[] enemies = nt.enemies;
        for (int i = enemies.length; i-- > 0; ) {
            RobotInfo e = enemies[i];
            if (e.hasFlag) {
                hasFlag = true;
                floc = e.location;
            }
        }
        
        if (hasFlag) defendFlag(floc);
        else maneuver(); */
        maneuver();
    }

    public void defendFlag(MapLocation floc) throws GameActionException {
        rc.setIndicatorString("Defending the flag!");
        Direction[] dirs = Direction.values();
        EnemyFlagTarget[] flagtargets = new EnemyFlagTarget[9];
        for (int i = 9; i-- > 0;) {
            flagtargets[i] = new EnemyFlagTarget(floc, dirs[i]);
        }
        EnemyFlagTarget bestTarget = flagtargets[Direction.CENTER.ordinal()];
        for (int i = 9; i-- > 0;) {
            if (flagtargets[i].isBetterThan(bestTarget)) {
                bestTarget = flagtargets[i];
            }
        }
        if (rc.canMove(bestTarget.dir)) {
            rc.move(bestTarget.dir);
        }
    }

    void maneuver() throws GameActionException {
        rc.setIndicatorString("Maneuvering");
        rc.setIndicatorDot(rc.getLocation(), 0, 0, 0);
        canAttack = rc.isActionReady();
        mydmg = dmgscores[rc.getLevel(SkillType.ATTACK)];
        hurt = rc.getHealth() <= (GameConstants.DEFAULT_HEALTH / 3);
        if (hurt) {
            rc.setIndicatorDot(rc.getLocation(), 0, 0, 255);
        }

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


        RobotInfo[] robots = nt.enemies;
        seeEnemyFlagCarrier = false;
        carrier = null;
        for (int i = robots.length; i-- > 0;) {
            if (Clock.getBytecodesLeft() < 3000) break;
            RobotInfo r = robots[i];
            if (r.hasFlag) {
                seeEnemyFlagCarrier = true;
                carrier = r.location;
            }
            microtargets[0].addEnemy(r);
            microtargets[1].addEnemy(r);
            microtargets[2].addEnemy(r);
            microtargets[3].addEnemy(r);
            microtargets[4].addEnemy(r);
            microtargets[5].addEnemy(r);
            microtargets[6].addEnemy(r);
            microtargets[7].addEnemy(r);
            microtargets[8].addEnemy(r);
        }

        robots = nt.friends;
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
        }


        // Util.displayMask(rc, sm.stunned_mask0, sm.stunned_mask1);
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
            nt.friends = rc.senseNearbyRobots(-1, myteam);
            nt.enemies = rc.senseNearbyRobots(-1, myteam.opponent());
        }
        // rc.setIndicatorString("Iters: " + iters);
        rc.setIndicatorString("attack: " + best.dmgAttackRange + "| vision: " + best.dmgVisionRange + "| heal: " + best.healAttackRange + "| friend: " + best.friendDmg + "| safe: " + best.safe() + "| dir: " + best.dir);
    }

    // Choose best candidate for maneuvering in close encounters.
    class MicroTarget {
        int offset = 0;
        long close0 = 0;
        long close1 = 0;
        int minDistToEnemy = 100000;
        int minDistToAlly = 100000;
        int healAttackRange = 0;
        int dmgAttackRange = 0;
        int dmgVisionRange = 0;
        int distToGoal = 1000000;
        int minDistToFlag = 1000000;
        boolean canMove;
        int canLandHit;
        MapLocation nloc;
        MapLocation bl;
        Direction dir;
        int friendDmg = 0;
        int friendDmgMore = 0;
        int healAttackRangeMore = 0;
        MapLocation closestEnemy = null;
        boolean onFrontline = true;
        boolean even = false;
        int close = 1;
        int close2 = 1;
        int minDistToInjured = 1000000;

        MicroTarget(Direction dir) throws GameActionException {
            MapLocation myloc = rc.getLocation();
            nloc = myloc.add(dir);
            bl = myloc.translate(-4, -4);
            offset = bl.hashCode();
            canMove = rc.canMove(dir);
            this.dir = dir;
            computeHitMask();
            even = (nloc.x + nloc.y) % 2 == 0;
        }

        void displayHitMask() throws GameActionException {
            rc.setIndicatorString("HitMask for " + dir);
            Util.displayMask(rc, close0, close1);
            // Util.displayMask(rc, hits0, hits1);
            rc.setIndicatorDot(nloc, 255, 165, 0);
        }

        void log() throws GameActionException {
            System.out.println("dmgReceived: " + healAttackRange);
            System.out.println("dmgAttackRange: " + dmgAttackRange);
            System.out.println("dmgVisionRange: " + dmgVisionRange);
            System.out.println("minDistToEnemy: " + minDistToEnemy);
        }

        // It's not too much overhead I promise.
        void computeHitMask() throws GameActionException {
            long action0 = 0b000010000000111000001111100000111000000010000000000000000000000L;
            long action1 = 0;
            switch (dir) {
                case NORTHEAST:     action0 <<= 10; action1 = 0b000100000; break;
                case NORTH:         action0 <<= 9;  action1 = 0b000010000; break;
                case NORTHWEST:     action0 <<= 8;  action1 = 0b000001000; break;
                case EAST:          action0 <<= 1;  break;
                case CENTER:        break;
                case WEST:          action0 >>>= 1;  break;
                case SOUTHEAST:     action0 >>>= 8;  break;
                case SOUTH:         action0 >>>= 9;  break;
                case SOUTHWEST:     action0 >>>= 10; break;
            }

            // if ((dir == Direction.NORTH)) {
            //     Util.displayMask(rc, nt.enemy_mask0, nt.enemy_mask1, 0, 0, 0);
            //     rc.setIndicatorDot(rc.getLocation(), 255, 0, 255);
            // }
            if (canAttack && !hurt && ((nt.enemy_mask0 & action0) | (nt.enemy_mask1 & action1)) != 0) {
                canLandHit = mydmg;
            }

            long mask0 = 0x7FFFFFFFFFFFFFFFL;
            long mask1 = 0x3FFFFL;
            long passible0 = (~(mt.wall_mask0 | mt.water_mask0)) & mask0;
            long passible1 = (~(mt.wall_mask1 | mt.water_mask1)) & mask1;
            long loverflow = 0x7fbfdfeff7fbfdfeL;
            long roverflow = 0x3fdfeff7fbfdfeffL;            
            long t_close0 = (action0 & passible0);
            long t_close1 = (action1 & passible1);
            long temp = 0;
            for (int i = 1; i-- > 0;) {
                t_close0 = (t_close0 | ((t_close0 << 1) & loverflow) | ((t_close0 >>> 1) & roverflow));
                t_close1 = (t_close1 | ((t_close1 << 1) & loverflow) | ((t_close1 >>> 1) & roverflow));
                temp = t_close0;
                t_close0 = (t_close0 | (t_close0 << 9) | (t_close0 >>> 9) | (t_close1 << 54)) & passible0;
                t_close1 = (t_close1 | (t_close1 << 9) | (t_close1 >>> 9) | (temp >>> 54)) & passible1;
            }
            close0 = (t_close0 | action0);// & (~sm.stunned_mask0);
            close1 = (t_close1 | action1);// & (~sm.stunned_mask1);
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
            if (r.hasFlag) {
                if (dist < minDistToFlag) minDistToFlag = dist;
                return;
            }
            if (dist < minDistToEnemy) {
                minDistToEnemy = dist;
                closestEnemy = r.location;
            }
            if(rc.getRoundNum() > 201 && rc.getRoundNum() - sm.lastStunned[r.ID - 10000] < 2) {
                return;
            }
            if (canHitSoon(r.location) != 0) {
                int dmg = dmgscores[r.attackLevel];
                dmgVisionRange += dmg;
                if (dist <= GameConstants.ATTACK_RADIUS_SQUARED)
                    dmgAttackRange += dmg;
            }
        }
        
        void addAlly(RobotInfo r) throws GameActionException {
            if (!canMove) return;
            if (r.hasFlag) return;
            int d = nloc.distanceSquaredTo(r.location);
            if (d < minDistToAlly) minDistToAlly = d;
            if (d <= GameConstants.ATTACK_RADIUS_SQUARED) {
                healAttackRange += healscores[r.healLevel] / (attacker(r.ID) ? 2 : 1);
                healAttackRangeMore += healscores[r.healLevel] / (attacker(r.ID) ? 2 : 1);
            }
            else if (d <= 9) {
                healAttackRangeMore += healscores[r.healLevel] / (attacker(r.ID) ? 2 : 1);
            }
            if (r.getHealth() < 300) {
               if (d < minDistToInjured) {
                   minDistToInjured = d;
               }
            }
            if (closestEnemy != null && r.location.distanceSquaredTo(closestEnemy) < minDistToEnemy) onFrontline = false;
            if(closestEnemy != null && (r.location.distanceSquaredTo(closestEnemy) <= 4)){
                int dmg = dmgscores[r.attackLevel];
                friendDmg += dmg;
                friendDmgMore += dmg;
                close++;
                close2++;
            }
            else if(closestEnemy != null && r.location.distanceSquaredTo(closestEnemy) <= 9){
                int dmg = dmgscores[r.attackLevel];
                friendDmgMore += dmg;
                close2++;
            }
        }

        boolean inRange() {
            return minDistToEnemy <= GameConstants.ATTACK_RADIUS_SQUARED;
        }

        int safe() {
            if (dmgAttackRange > healAttackRange/close + friendDmg + canLandHit) return 1;
            if (dmgVisionRange > healAttackRangeMore/close2 + friendDmgMore + canLandHit) return 2;
            return 3;
        }

        boolean isBetterThan(MicroTarget mt) {
            if (!canMove) return false;
            if (seeEnemyFlagCarrier && rc.getLocation().distanceSquaredTo(carrier) > 3) {

                if (minDistToFlag < mt.minDistToFlag) return true;
                if (minDistToFlag > mt.minDistToFlag) return false;

            }

            if (safe() > mt.safe()) return true;
            if (safe() < mt.safe()) return false;

            if (healer) {

                if (even && !mt.even) return false;
                if (!even && mt.even) return true;

            }

            if (canLandHit > mt.canLandHit) return true;
            if (canLandHit < mt.canLandHit) return false;

            if (attacker) {

                if (inRange()) return minDistToEnemy >= mt.minDistToEnemy;
                else return minDistToEnemy <= mt.minDistToEnemy;

            }

            // if (hurt) {
            //     if (minDistToAlly < mt.minDistToAlly) return true;
            //     if (minDistToAlly > mt.minDistToAlly) return false;
            // } else {
            //     if ((minDistToAlly >= 2) && (mt.minDistToAlly < 2)) return true;
            //     if ((minDistToAlly < 2) && (mt.minDistToAlly >= 2)) return false;
            // }
            // if ((minDistToAlly >= 2) && (mt.minDistToAlly < 2)) return true;
            // if ((minDistToAlly < 2) && (mt.minDistToAlly >= 2)) return false;
            if (healer) {

                if ((minDistToInjured <= 4) && (mt.minDistToInjured > 4)) return true;
                if ((minDistToInjured > 4) && (mt.minDistToInjured <= 4)) return false;

            }

            if ((minDistToAlly <= 4) && (mt.minDistToAlly > 4)) return true;
            if ((minDistToAlly > 4) && (mt.minDistToAlly <= 4)) return false;
            // if (minDistToAlly < mt.minDistToAlly) return true;
            // if (minDistToAlly > mt.minDistToAlly) return false;

            if (inRange()) return minDistToEnemy >= mt.minDistToEnemy;
            else return minDistToEnemy <= mt.minDistToEnemy;
        }
    }


    class EnemyFlagTarget {
        int distToFlag;
        boolean canMove;
        MapLocation nloc;
        Direction dir;

        EnemyFlagTarget(MapLocation floc, Direction dir) throws GameActionException {
            nloc = rc.getLocation().add(dir);
            canMove = rc.canMove(dir);
            distToFlag = nloc.distanceSquaredTo(floc);
            this.dir = dir;
        }

        boolean isBetterThan(EnemyFlagTarget ft) {
            if (!canMove) return false;
            return distToFlag < ft.distToFlag;
        }
    }
}
