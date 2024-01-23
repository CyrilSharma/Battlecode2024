package sprint2;
import battlecode.common.*;
public class OptimalPathing {
    MapTracker mt;
    NeighborTracker nt;
    RobotController rc;
    static int H, W;
    public OptimalPathing(Robot robot) {
        this.rc = robot.rc;
        this.mt = robot.mt;
        this.nt = robot.nt;
        H = rc.getMapHeight();
        W = rc.getMapWidth();
    }
    
    public void moveTo(MapLocation target, boolean avoid_enemies, boolean water_passable) throws GameActionException {
        if (rc.getMovementCooldownTurns() >= 10) return;
        long mask0 = 0x7FFFFFFFFFFFFFFFL;
        long mask1 = 0x3FFFFL;
        long loverflow = 0x7fbfdfeff7fbfdfeL;
        long roverflow = 0x3fdfeff7fbfdfeffL;
        long temp = 0;
        long enemyaction0 = 0;
        long enemyaction1 = 0;
        if (avoid_enemies) {
            enemyaction0 = nt.enemy_mask0;
            enemyaction1 = nt.enemy_mask1;
            enemyaction0 = (enemyaction0 | ((enemyaction0 << 1) & loverflow) | ((enemyaction0 >>> 1) & roverflow));
            enemyaction1 = (enemyaction1 | ((enemyaction1 << 1) & loverflow) | ((enemyaction1 >>> 1) & roverflow));
            temp = enemyaction0;
            enemyaction0 = (enemyaction0 | (enemyaction0 << 9) | (enemyaction0 >>> 9) | (enemyaction1 << 54)) & mask0;
            enemyaction1 = (enemyaction1 | (enemyaction1 << 9) | (enemyaction1 >>> 9) | (temp >>> 54)) & mask1;
            enemyaction0 = (enemyaction0 | ((enemyaction0 << 1) & loverflow) | ((enemyaction0 >>> 1) & roverflow));
            enemyaction1 = (enemyaction1 | ((enemyaction1 << 1) & loverflow) | ((enemyaction1 >>> 1) & roverflow));
            temp = enemyaction0;
            enemyaction0 = (enemyaction0 | (enemyaction0 << 9) | (enemyaction0 >>> 9) | (enemyaction1 << 54)) & mask0;
            enemyaction1 = (enemyaction1 | (enemyaction1 << 9) | (enemyaction1 >>> 9) | (temp >>> 54)) & mask1;
        }
        
        long slow0 = 0;
        long slow1 = 0;
        long slow_mask0 = 0;
        long slow_mask1 = 0;
        if (avoid_enemies) {
            slow_mask0 = (enemyaction0);
            slow_mask1 = (enemyaction1);
        } else {
            slow_mask0 = (mt.water_mask0);
            slow_mask1 = (mt.water_mask1);
        }
        long clear0 = 0;
        long clear1 = 0;
        if (water_passable) {
            clear0 = ~(mt.adjblocked | mt.wall_mask0 | enemyaction0) & mask0;
            clear1 = ~(mt.wall_mask1 | enemyaction1) & mask1;
        } else {
            clear0 = ~(mt.adjblocked | mt.wall_mask0 | mt.water_mask0 | enemyaction0) & mask0;
            clear1 = ~(mt.wall_mask1 | mt.water_mask1 | enemyaction1) & mask1;
        }
        
        long[] reach0_round = new long[10];
        long[] reach1_round = new long[10];
        long reach0_0 = 1099511627776L;
        long reach0_1 = 0;
        long reach0_2 = 0;
        long reach1_0 = 0;
        long reach1_1 = 0;
        long reach1_2 = 0;
        slow0 = reach0_1;
        slow1 = reach1_1;
        reach0_1 = (reach0_0 | ((reach0_0 << 1) & loverflow) | ((reach0_0 >>> 1) & roverflow));
        reach1_1 = (reach1_0 | ((reach1_0 << 1) & loverflow) | ((reach1_0 >>> 1) & roverflow));
        temp = reach0_1;
        reach0_1 = (reach0_1 | (reach0_1 << 9) | (reach0_1 >>> 9) | (reach1_1 << 54));
        reach1_1 = (reach1_1 | (reach1_1 << 9) | (reach1_1 >>> 9) | (temp >>> 54));
        reach0_2 = (reach0_1 & slow_mask0);
        reach0_1 = slow0 | (reach0_1 & clear0);
        reach1_2 = (reach1_1 & slow_mask1);
        reach1_1 = slow1 | (reach1_1 & clear1);
        reach0_round[0] = reach0_1;
        reach1_round[0] = reach1_1;
        slow0 = reach0_2;
        slow1 = reach1_2;
        reach0_2 = (reach0_1 | ((reach0_1 << 1) & loverflow) | ((reach0_1 >>> 1) & roverflow));
        reach1_2 = (reach1_1 | ((reach1_1 << 1) & loverflow) | ((reach1_1 >>> 1) & roverflow));
        temp = reach0_2;
        reach0_2 = (reach0_2 | (reach0_2 << 9) | (reach0_2 >>> 9) | (reach1_2 << 54));
        reach1_2 = (reach1_2 | (reach1_2 << 9) | (reach1_2 >>> 9) | (temp >>> 54));
        reach0_0 = (reach0_2 & slow_mask0);
        reach0_2 = slow0 | (reach0_2 & clear0);
        reach1_0 = (reach1_2 & slow_mask1);
        reach1_2 = slow1 | (reach1_2 & clear1);
        reach0_round[1] = reach0_2 & ~reach0_1;
        reach1_round[1] = reach1_2 & ~reach1_1;
        slow0 = reach0_0;
        slow1 = reach1_0;
        reach0_0 = (reach0_2 | ((reach0_2 << 1) & loverflow) | ((reach0_2 >>> 1) & roverflow));
        reach1_0 = (reach1_2 | ((reach1_2 << 1) & loverflow) | ((reach1_2 >>> 1) & roverflow));
        temp = reach0_0;
        reach0_0 = (reach0_0 | (reach0_0 << 9) | (reach0_0 >>> 9) | (reach1_0 << 54));
        reach1_0 = (reach1_0 | (reach1_0 << 9) | (reach1_0 >>> 9) | (temp >>> 54));
        reach0_1 = (reach0_0 & slow_mask0);
        reach0_0 = slow0 | (reach0_0 & clear0);
        reach1_1 = (reach1_0 & slow_mask1);
        reach1_0 = slow1 | (reach1_0 & clear1);
        reach0_round[2] = reach0_0 & ~reach0_2;
        reach1_round[2] = reach1_0 & ~reach1_2;
        slow0 = reach0_1;
        slow1 = reach1_1;
        reach0_1 = (reach0_0 | ((reach0_0 << 1) & loverflow) | ((reach0_0 >>> 1) & roverflow));
        reach1_1 = (reach1_0 | ((reach1_0 << 1) & loverflow) | ((reach1_0 >>> 1) & roverflow));
        temp = reach0_1;
        reach0_1 = (reach0_1 | (reach0_1 << 9) | (reach0_1 >>> 9) | (reach1_1 << 54));
        reach1_1 = (reach1_1 | (reach1_1 << 9) | (reach1_1 >>> 9) | (temp >>> 54));
        reach0_2 = (reach0_1 & slow_mask0);
        reach0_1 = slow0 | (reach0_1 & clear0);
        reach1_2 = (reach1_1 & slow_mask1);
        reach1_1 = slow1 | (reach1_1 & clear1);
        reach0_round[3] = reach0_1 & ~reach0_0;
        reach1_round[3] = reach1_1 & ~reach1_0;
        slow0 = reach0_2;
        slow1 = reach1_2;
        reach0_2 = (reach0_1 | ((reach0_1 << 1) & loverflow) | ((reach0_1 >>> 1) & roverflow));
        reach1_2 = (reach1_1 | ((reach1_1 << 1) & loverflow) | ((reach1_1 >>> 1) & roverflow));
        temp = reach0_2;
        reach0_2 = (reach0_2 | (reach0_2 << 9) | (reach0_2 >>> 9) | (reach1_2 << 54));
        reach1_2 = (reach1_2 | (reach1_2 << 9) | (reach1_2 >>> 9) | (temp >>> 54));
        reach0_0 = (reach0_2 & slow_mask0);
        reach0_2 = slow0 | (reach0_2 & clear0);
        reach1_0 = (reach1_2 & slow_mask1);
        reach1_2 = slow1 | (reach1_2 & clear1);
        reach0_round[4] = reach0_2 & ~reach0_1;
        reach1_round[4] = reach1_2 & ~reach1_1;
        slow0 = reach0_0;
        slow1 = reach1_0;
        reach0_0 = (reach0_2 | ((reach0_2 << 1) & loverflow) | ((reach0_2 >>> 1) & roverflow));
        reach1_0 = (reach1_2 | ((reach1_2 << 1) & loverflow) | ((reach1_2 >>> 1) & roverflow));
        temp = reach0_0;
        reach0_0 = (reach0_0 | (reach0_0 << 9) | (reach0_0 >>> 9) | (reach1_0 << 54));
        reach1_0 = (reach1_0 | (reach1_0 << 9) | (reach1_0 >>> 9) | (temp >>> 54));
        reach0_1 = (reach0_0 & slow_mask0);
        reach0_0 = slow0 | (reach0_0 & clear0);
        reach1_1 = (reach1_0 & slow_mask1);
        reach1_0 = slow1 | (reach1_0 & clear1);
        reach0_round[5] = reach0_0 & ~reach0_2;
        reach1_round[5] = reach1_0 & ~reach1_2;
        slow0 = reach0_1;
        slow1 = reach1_1;
        reach0_1 = (reach0_0 | ((reach0_0 << 1) & loverflow) | ((reach0_0 >>> 1) & roverflow));
        reach1_1 = (reach1_0 | ((reach1_0 << 1) & loverflow) | ((reach1_0 >>> 1) & roverflow));
        temp = reach0_1;
        reach0_1 = (reach0_1 | (reach0_1 << 9) | (reach0_1 >>> 9) | (reach1_1 << 54));
        reach1_1 = (reach1_1 | (reach1_1 << 9) | (reach1_1 >>> 9) | (temp >>> 54));
        reach0_2 = (reach0_1 & slow_mask0);
        reach0_1 = slow0 | (reach0_1 & clear0);
        reach1_2 = (reach1_1 & slow_mask1);
        reach1_1 = slow1 | (reach1_1 & clear1);
        reach0_round[6] = reach0_1 & ~reach0_0;
        reach1_round[6] = reach1_1 & ~reach1_0;
        slow0 = reach0_2;
        slow1 = reach1_2;
        reach0_2 = (reach0_1 | ((reach0_1 << 1) & loverflow) | ((reach0_1 >>> 1) & roverflow));
        reach1_2 = (reach1_1 | ((reach1_1 << 1) & loverflow) | ((reach1_1 >>> 1) & roverflow));
        temp = reach0_2;
        reach0_2 = (reach0_2 | (reach0_2 << 9) | (reach0_2 >>> 9) | (reach1_2 << 54));
        reach1_2 = (reach1_2 | (reach1_2 << 9) | (reach1_2 >>> 9) | (temp >>> 54));
        reach0_0 = (reach0_2 & slow_mask0);
        reach0_2 = slow0 | (reach0_2 & clear0);
        reach1_0 = (reach1_2 & slow_mask1);
        reach1_2 = slow1 | (reach1_2 & clear1);
        reach0_round[7] = reach0_2 & ~reach0_1;
        reach1_round[7] = reach1_2 & ~reach1_1;
        slow0 = reach0_0;
        slow1 = reach1_0;
        reach0_0 = (reach0_2 | ((reach0_2 << 1) & loverflow) | ((reach0_2 >>> 1) & roverflow));
        reach1_0 = (reach1_2 | ((reach1_2 << 1) & loverflow) | ((reach1_2 >>> 1) & roverflow));
        temp = reach0_0;
        reach0_0 = (reach0_0 | (reach0_0 << 9) | (reach0_0 >>> 9) | (reach1_0 << 54));
        reach1_0 = (reach1_0 | (reach1_0 << 9) | (reach1_0 >>> 9) | (temp >>> 54));
        reach0_1 = (reach0_0 & slow_mask0);
        reach0_0 = slow0 | (reach0_0 & clear0);
        reach1_1 = (reach1_0 & slow_mask1);
        reach1_0 = slow1 | (reach1_0 & clear1);
        reach0_round[8] = reach0_0 & ~reach0_2;
        reach1_round[8] = reach1_0 & ~reach1_2;
        slow0 = reach0_1;
        slow1 = reach1_1;
        reach0_1 = (reach0_0 | ((reach0_0 << 1) & loverflow) | ((reach0_0 >>> 1) & roverflow));
        reach1_1 = (reach1_0 | ((reach1_0 << 1) & loverflow) | ((reach1_0 >>> 1) & roverflow));
        temp = reach0_1;
        reach0_1 = (reach0_1 | (reach0_1 << 9) | (reach0_1 >>> 9) | (reach1_1 << 54));
        reach1_1 = (reach1_1 | (reach1_1 << 9) | (reach1_1 >>> 9) | (temp >>> 54));
        reach0_2 = (reach0_1 & slow_mask0);
        reach0_1 = slow0 | (reach0_1 & clear0);
        reach1_2 = (reach1_1 & slow_mask1);
        reach1_1 = slow1 | (reach1_1 & clear1);
        reach0_round[9] = reach0_1 & ~reach0_0;
        reach1_round[9] = reach1_1 & ~reach1_0;
        
        MapLocation loc00 = rc.getLocation().translate(-4, -4);
        MapLocation loc01 = loc00.add(Direction.EAST);
        MapLocation loc02 = loc01.add(Direction.EAST);
        MapLocation loc03 = loc02.add(Direction.EAST);
        MapLocation loc04 = loc03.add(Direction.EAST);
        MapLocation loc05 = loc04.add(Direction.EAST);
        MapLocation loc06 = loc05.add(Direction.EAST);
        MapLocation loc07 = loc06.add(Direction.EAST);
        MapLocation loc08 = loc07.add(Direction.EAST);
        MapLocation loc10 = loc00.add(Direction.NORTH);
        MapLocation loc11 = loc01.add(Direction.NORTH);
        MapLocation loc12 = loc02.add(Direction.NORTH);
        MapLocation loc13 = loc03.add(Direction.NORTH);
        MapLocation loc14 = loc04.add(Direction.NORTH);
        MapLocation loc15 = loc05.add(Direction.NORTH);
        MapLocation loc16 = loc06.add(Direction.NORTH);
        MapLocation loc17 = loc07.add(Direction.NORTH);
        MapLocation loc18 = loc08.add(Direction.NORTH);
        MapLocation loc20 = loc10.add(Direction.NORTH);
        MapLocation loc21 = loc11.add(Direction.NORTH);
        MapLocation loc22 = loc12.add(Direction.NORTH);
        MapLocation loc23 = loc13.add(Direction.NORTH);
        MapLocation loc24 = loc14.add(Direction.NORTH);
        MapLocation loc25 = loc15.add(Direction.NORTH);
        MapLocation loc26 = loc16.add(Direction.NORTH);
        MapLocation loc27 = loc17.add(Direction.NORTH);
        MapLocation loc28 = loc18.add(Direction.NORTH);
        MapLocation loc30 = loc20.add(Direction.NORTH);
        MapLocation loc31 = loc21.add(Direction.NORTH);
        MapLocation loc32 = loc22.add(Direction.NORTH);
        MapLocation loc33 = loc23.add(Direction.NORTH);
        MapLocation loc34 = loc24.add(Direction.NORTH);
        MapLocation loc35 = loc25.add(Direction.NORTH);
        MapLocation loc36 = loc26.add(Direction.NORTH);
        MapLocation loc37 = loc27.add(Direction.NORTH);
        MapLocation loc38 = loc28.add(Direction.NORTH);
        MapLocation loc40 = loc30.add(Direction.NORTH);
        MapLocation loc41 = loc31.add(Direction.NORTH);
        MapLocation loc42 = loc32.add(Direction.NORTH);
        MapLocation loc43 = loc33.add(Direction.NORTH);
        MapLocation loc44 = loc34.add(Direction.NORTH);
        MapLocation loc45 = loc35.add(Direction.NORTH);
        MapLocation loc46 = loc36.add(Direction.NORTH);
        MapLocation loc47 = loc37.add(Direction.NORTH);
        MapLocation loc48 = loc38.add(Direction.NORTH);
        MapLocation loc50 = loc40.add(Direction.NORTH);
        MapLocation loc51 = loc41.add(Direction.NORTH);
        MapLocation loc52 = loc42.add(Direction.NORTH);
        MapLocation loc53 = loc43.add(Direction.NORTH);
        MapLocation loc54 = loc44.add(Direction.NORTH);
        MapLocation loc55 = loc45.add(Direction.NORTH);
        MapLocation loc56 = loc46.add(Direction.NORTH);
        MapLocation loc57 = loc47.add(Direction.NORTH);
        MapLocation loc58 = loc48.add(Direction.NORTH);
        MapLocation loc60 = loc50.add(Direction.NORTH);
        MapLocation loc61 = loc51.add(Direction.NORTH);
        MapLocation loc62 = loc52.add(Direction.NORTH);
        MapLocation loc63 = loc53.add(Direction.NORTH);
        MapLocation loc64 = loc54.add(Direction.NORTH);
        MapLocation loc65 = loc55.add(Direction.NORTH);
        MapLocation loc66 = loc56.add(Direction.NORTH);
        MapLocation loc67 = loc57.add(Direction.NORTH);
        MapLocation loc68 = loc58.add(Direction.NORTH);
        MapLocation loc70 = loc60.add(Direction.NORTH);
        MapLocation loc71 = loc61.add(Direction.NORTH);
        MapLocation loc72 = loc62.add(Direction.NORTH);
        MapLocation loc73 = loc63.add(Direction.NORTH);
        MapLocation loc74 = loc64.add(Direction.NORTH);
        MapLocation loc75 = loc65.add(Direction.NORTH);
        MapLocation loc76 = loc66.add(Direction.NORTH);
        MapLocation loc77 = loc67.add(Direction.NORTH);
        MapLocation loc78 = loc68.add(Direction.NORTH);
        MapLocation loc80 = loc70.add(Direction.NORTH);
        MapLocation loc81 = loc71.add(Direction.NORTH);
        MapLocation loc82 = loc72.add(Direction.NORTH);
        MapLocation loc83 = loc73.add(Direction.NORTH);
        MapLocation loc84 = loc74.add(Direction.NORTH);
        MapLocation loc85 = loc75.add(Direction.NORTH);
        MapLocation loc86 = loc76.add(Direction.NORTH);
        MapLocation loc87 = loc77.add(Direction.NORTH);
        MapLocation loc88 = loc78.add(Direction.NORTH);
        
        int d = 0;
        long temp0 = 0;
        long temp1 = 0;
        long temp2 = 0;
        long bestdist = 1073741824;
        long targetsqrs0 = 0;
        long targetsqrs1 = 0;
        for (int i = 0; i < 10; i++) {
            temp0 = reach0_round[i];
            temp1 = reach1_round[i];
            outer: for (;; temp0 &= (temp0 - 1), temp1 &= (temp1 - 1)) {
                temp2 = (temp0 & -temp0);
                switch ((int)(temp2 & 0xFFFFFFFF)) {
                    case 0x1: 
                        d = ((int) Math.sqrt(loc00.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x1L;
                        }
                        continue;
                    case 0x2: 
                        d = ((int) Math.sqrt(loc01.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x2L;
                        }
                        continue;
                    case 0x4: 
                        d = ((int) Math.sqrt(loc02.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x4L;
                        }
                        continue;
                    case 0x8: 
                        d = ((int) Math.sqrt(loc03.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x8L;
                        }
                        continue;
                    case 0x10: 
                        d = ((int) Math.sqrt(loc04.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x10L;
                        }
                        continue;
                    case 0x20: 
                        d = ((int) Math.sqrt(loc05.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x20L;
                        }
                        continue;
                    case 0x40: 
                        d = ((int) Math.sqrt(loc06.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x40L;
                        }
                        continue;
                    case 0x80: 
                        d = ((int) Math.sqrt(loc07.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x80L;
                        }
                        continue;
                    case 0x100: 
                        d = ((int) Math.sqrt(loc08.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x100L;
                        }
                        continue;
                    case 0x200: 
                        d = ((int) Math.sqrt(loc10.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x200L;
                        }
                        continue;
                    case 0x400: 
                        d = ((int) Math.sqrt(loc11.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x400L;
                        }
                        continue;
                    case 0x800: 
                        d = ((int) Math.sqrt(loc12.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x800L;
                        }
                        continue;
                    case 0x1000: 
                        d = ((int) Math.sqrt(loc13.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x1000L;
                        }
                        continue;
                    case 0x2000: 
                        d = ((int) Math.sqrt(loc14.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x2000L;
                        }
                        continue;
                    case 0x4000: 
                        d = ((int) Math.sqrt(loc15.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x4000L;
                        }
                        continue;
                    case 0x8000: 
                        d = ((int) Math.sqrt(loc16.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x8000L;
                        }
                        continue;
                    case 0x10000: 
                        d = ((int) Math.sqrt(loc17.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x10000L;
                        }
                        continue;
                    case 0x20000: 
                        d = ((int) Math.sqrt(loc18.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x20000L;
                        }
                        continue;
                    case 0x40000: 
                        d = ((int) Math.sqrt(loc20.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x40000L;
                        }
                        continue;
                    case 0x80000: 
                        d = ((int) Math.sqrt(loc21.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x80000L;
                        }
                        continue;
                    case 0x100000: 
                        d = ((int) Math.sqrt(loc22.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x100000L;
                        }
                        continue;
                    case 0x200000: 
                        d = ((int) Math.sqrt(loc23.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x200000L;
                        }
                        continue;
                    case 0x400000: 
                        d = ((int) Math.sqrt(loc24.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x400000L;
                        }
                        continue;
                    case 0x800000: 
                        d = ((int) Math.sqrt(loc25.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x800000L;
                        }
                        continue;
                    case 0x1000000: 
                        d = ((int) Math.sqrt(loc26.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x1000000L;
                        }
                        continue;
                    case 0x2000000: 
                        d = ((int) Math.sqrt(loc27.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x2000000L;
                        }
                        continue;
                    case 0x4000000: 
                        d = ((int) Math.sqrt(loc28.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x4000000L;
                        }
                        continue;
                    case 0x8000000: 
                        d = ((int) Math.sqrt(loc30.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x8000000L;
                        }
                        continue;
                    case 0x10000000: 
                        d = ((int) Math.sqrt(loc31.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x10000000L;
                        }
                        continue;
                    case 0x20000000: 
                        d = ((int) Math.sqrt(loc32.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x20000000L;
                        }
                        continue;
                    case 0x40000000: 
                        d = ((int) Math.sqrt(loc33.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x40000000L;
                        }
                        continue;
                    case 0x80000000: 
                        d = ((int) Math.sqrt(loc34.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x80000000L;
                        }
                        continue;
                    default: break;
                }
                switch ((int)(temp2 >>> 32)) {
                    case 0x1: 
                        d = ((int) Math.sqrt(loc35.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x100000000L;
                        }
                        continue;
                    case 0x2: 
                        d = ((int) Math.sqrt(loc36.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x200000000L;
                        }
                        continue;
                    case 0x4: 
                        d = ((int) Math.sqrt(loc37.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x400000000L;
                        }
                        continue;
                    case 0x8: 
                        d = ((int) Math.sqrt(loc38.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x800000000L;
                        }
                        continue;
                    case 0x10: 
                        d = ((int) Math.sqrt(loc40.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x1000000000L;
                        }
                        continue;
                    case 0x20: 
                        d = ((int) Math.sqrt(loc41.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x2000000000L;
                        }
                        continue;
                    case 0x40: 
                        d = ((int) Math.sqrt(loc42.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x4000000000L;
                        }
                        continue;
                    case 0x80: 
                        d = ((int) Math.sqrt(loc43.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x8000000000L;
                        }
                        continue;
                    case 0x100: 
                        d = ((int) Math.sqrt(loc44.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x10000000000L;
                        }
                        continue;
                    case 0x200: 
                        d = ((int) Math.sqrt(loc45.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x20000000000L;
                        }
                        continue;
                    case 0x400: 
                        d = ((int) Math.sqrt(loc46.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x40000000000L;
                        }
                        continue;
                    case 0x800: 
                        d = ((int) Math.sqrt(loc47.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x80000000000L;
                        }
                        continue;
                    case 0x1000: 
                        d = ((int) Math.sqrt(loc48.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x100000000000L;
                        }
                        continue;
                    case 0x2000: 
                        d = ((int) Math.sqrt(loc50.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x200000000000L;
                        }
                        continue;
                    case 0x4000: 
                        d = ((int) Math.sqrt(loc51.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x400000000000L;
                        }
                        continue;
                    case 0x8000: 
                        d = ((int) Math.sqrt(loc52.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x800000000000L;
                        }
                        continue;
                    case 0x10000: 
                        d = ((int) Math.sqrt(loc53.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x1000000000000L;
                        }
                        continue;
                    case 0x20000: 
                        d = ((int) Math.sqrt(loc54.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x2000000000000L;
                        }
                        continue;
                    case 0x40000: 
                        d = ((int) Math.sqrt(loc55.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x4000000000000L;
                        }
                        continue;
                    case 0x80000: 
                        d = ((int) Math.sqrt(loc56.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x8000000000000L;
                        }
                        continue;
                    case 0x100000: 
                        d = ((int) Math.sqrt(loc57.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x10000000000000L;
                        }
                        continue;
                    case 0x200000: 
                        d = ((int) Math.sqrt(loc58.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x20000000000000L;
                        }
                        continue;
                    case 0x400000: 
                        d = ((int) Math.sqrt(loc60.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x40000000000000L;
                        }
                        continue;
                    case 0x800000: 
                        d = ((int) Math.sqrt(loc61.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x80000000000000L;
                        }
                        continue;
                    case 0x1000000: 
                        d = ((int) Math.sqrt(loc62.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x100000000000000L;
                        }
                        continue;
                    case 0x2000000: 
                        d = ((int) Math.sqrt(loc63.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x200000000000000L;
                        }
                        continue;
                    case 0x4000000: 
                        d = ((int) Math.sqrt(loc64.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x400000000000000L;
                        }
                        continue;
                    case 0x8000000: 
                        d = ((int) Math.sqrt(loc65.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x800000000000000L;
                        }
                        continue;
                    case 0x10000000: 
                        d = ((int) Math.sqrt(loc66.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x1000000000000000L;
                        }
                        continue;
                    case 0x20000000: 
                        d = ((int) Math.sqrt(loc67.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x2000000000000000L;
                        }
                        continue;
                    case 0x40000000: 
                        d = ((int) Math.sqrt(loc68.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs0 = 0x4000000000000000L;
                        }
                        continue;
                    default: break;
                }
                switch ((int) (temp1 & -temp1)) {
                    case 0x1: 
                        d = ((int) Math.sqrt(loc70.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x1L;
                        }
                        continue;
                    case 0x2: 
                        d = ((int) Math.sqrt(loc71.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x2L;
                        }
                        continue;
                    case 0x4: 
                        d = ((int) Math.sqrt(loc72.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x4L;
                        }
                        continue;
                    case 0x8: 
                        d = ((int) Math.sqrt(loc73.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x8L;
                        }
                        continue;
                    case 0x10: 
                        d = ((int) Math.sqrt(loc74.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x10L;
                        }
                        continue;
                    case 0x20: 
                        d = ((int) Math.sqrt(loc75.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x20L;
                        }
                        continue;
                    case 0x40: 
                        d = ((int) Math.sqrt(loc76.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x40L;
                        }
                        continue;
                    case 0x80: 
                        d = ((int) Math.sqrt(loc77.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x80L;
                        }
                        continue;
                    case 0x100: 
                        d = ((int) Math.sqrt(loc78.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x100L;
                        }
                        continue;
                    case 0x200: 
                        d = ((int) Math.sqrt(loc80.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x200L;
                        }
                        continue;
                    case 0x400: 
                        d = ((int) Math.sqrt(loc81.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x400L;
                        }
                        continue;
                    case 0x800: 
                        d = ((int) Math.sqrt(loc82.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x800L;
                        }
                        continue;
                    case 0x1000: 
                        d = ((int) Math.sqrt(loc83.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x1000L;
                        }
                        continue;
                    case 0x2000: 
                        d = ((int) Math.sqrt(loc84.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x2000L;
                        }
                        continue;
                    case 0x4000: 
                        d = ((int) Math.sqrt(loc85.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x4000L;
                        }
                        continue;
                    case 0x8000: 
                        d = ((int) Math.sqrt(loc86.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x8000L;
                        }
                        continue;
                    case 0x10000: 
                        d = ((int) Math.sqrt(loc87.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x10000L;
                        }
                        continue;
                    case 0x20000: 
                        d = ((int) Math.sqrt(loc88.distanceSquaredTo(target))) + i;
                        if (d < bestdist) {
                            bestdist = d;
                            targetsqrs1 = 0x20000L;
                        }
                        continue;
                    default: break outer;
                }
            }
        }
        
        long best = 0;
        long back0_0 = 0;
        long back0_1 = 0;
        long back0_2 = 0;
        long back1_0 = 0;
        long back1_1 = 0;
        long back1_2 = 0;
        back0_0 = targetsqrs0;
        back1_0 = targetsqrs1;
        outer: {
            slow0 = back0_1;
            slow1 = back1_1;
            back0_1 = (back0_0 | ((back0_0 << 1) & loverflow) | ((back0_0 >>> 1) & roverflow));
            back1_1 = (back1_0 | ((back1_0 << 1) & loverflow) | ((back1_0 >>> 1) & roverflow));
            temp = back0_1;
            back0_1 = (back0_1 | (back0_1 << 9) | (back0_1 >>> 9) | (back1_1 << 54));
            back1_1 = (back1_1 | (back1_1 << 9) | (back1_1 >>> 9) | (temp >>> 54));
            back0_2 = (back0_1 & slow_mask0);
            back0_1 = slow0 | (back0_1 & clear0);
            back1_2 = (back1_1 & slow_mask1);
            back1_1 = slow1 | (back1_1 & clear1);
            if ((back0_1 & 0x70381c0000000L) != 0) {
                best = back0_1;
                break outer;
            }
            slow0 = back0_2;
            slow1 = back1_2;
            back0_2 = (back0_1 | ((back0_1 << 1) & loverflow) | ((back0_1 >>> 1) & roverflow));
            back1_2 = (back1_1 | ((back1_1 << 1) & loverflow) | ((back1_1 >>> 1) & roverflow));
            temp = back0_2;
            back0_2 = (back0_2 | (back0_2 << 9) | (back0_2 >>> 9) | (back1_2 << 54));
            back1_2 = (back1_2 | (back1_2 << 9) | (back1_2 >>> 9) | (temp >>> 54));
            back0_0 = (back0_2 & slow_mask0);
            back0_2 = slow0 | (back0_2 & clear0);
            back1_0 = (back1_2 & slow_mask1);
            back1_2 = slow1 | (back1_2 & clear1);
            if ((back0_2 & 0x70381c0000000L) != 0) {
                best = back0_2;
                break outer;
            }
            slow0 = back0_0;
            slow1 = back1_0;
            back0_0 = (back0_2 | ((back0_2 << 1) & loverflow) | ((back0_2 >>> 1) & roverflow));
            back1_0 = (back1_2 | ((back1_2 << 1) & loverflow) | ((back1_2 >>> 1) & roverflow));
            temp = back0_0;
            back0_0 = (back0_0 | (back0_0 << 9) | (back0_0 >>> 9) | (back1_0 << 54));
            back1_0 = (back1_0 | (back1_0 << 9) | (back1_0 >>> 9) | (temp >>> 54));
            back0_1 = (back0_0 & slow_mask0);
            back0_0 = slow0 | (back0_0 & clear0);
            back1_1 = (back1_0 & slow_mask1);
            back1_0 = slow1 | (back1_0 & clear1);
            if ((back0_0 & 0x70381c0000000L) != 0) {
                best = back0_0;
                break outer;
            }
            slow0 = back0_1;
            slow1 = back1_1;
            back0_1 = (back0_0 | ((back0_0 << 1) & loverflow) | ((back0_0 >>> 1) & roverflow));
            back1_1 = (back1_0 | ((back1_0 << 1) & loverflow) | ((back1_0 >>> 1) & roverflow));
            temp = back0_1;
            back0_1 = (back0_1 | (back0_1 << 9) | (back0_1 >>> 9) | (back1_1 << 54));
            back1_1 = (back1_1 | (back1_1 << 9) | (back1_1 >>> 9) | (temp >>> 54));
            back0_2 = (back0_1 & slow_mask0);
            back0_1 = slow0 | (back0_1 & clear0);
            back1_2 = (back1_1 & slow_mask1);
            back1_1 = slow1 | (back1_1 & clear1);
            if ((back0_1 & 0x70381c0000000L) != 0) {
                best = back0_1;
                break outer;
            }
            slow0 = back0_2;
            slow1 = back1_2;
            back0_2 = (back0_1 | ((back0_1 << 1) & loverflow) | ((back0_1 >>> 1) & roverflow));
            back1_2 = (back1_1 | ((back1_1 << 1) & loverflow) | ((back1_1 >>> 1) & roverflow));
            temp = back0_2;
            back0_2 = (back0_2 | (back0_2 << 9) | (back0_2 >>> 9) | (back1_2 << 54));
            back1_2 = (back1_2 | (back1_2 << 9) | (back1_2 >>> 9) | (temp >>> 54));
            back0_0 = (back0_2 & slow_mask0);
            back0_2 = slow0 | (back0_2 & clear0);
            back1_0 = (back1_2 & slow_mask1);
            back1_2 = slow1 | (back1_2 & clear1);
            if ((back0_2 & 0x70381c0000000L) != 0) {
                best = back0_2;
                break outer;
            }
            slow0 = back0_0;
            slow1 = back1_0;
            back0_0 = (back0_2 | ((back0_2 << 1) & loverflow) | ((back0_2 >>> 1) & roverflow));
            back1_0 = (back1_2 | ((back1_2 << 1) & loverflow) | ((back1_2 >>> 1) & roverflow));
            temp = back0_0;
            back0_0 = (back0_0 | (back0_0 << 9) | (back0_0 >>> 9) | (back1_0 << 54));
            back1_0 = (back1_0 | (back1_0 << 9) | (back1_0 >>> 9) | (temp >>> 54));
            back0_1 = (back0_0 & slow_mask0);
            back0_0 = slow0 | (back0_0 & clear0);
            back1_1 = (back1_0 & slow_mask1);
            back1_0 = slow1 | (back1_0 & clear1);
            if ((back0_0 & 0x70381c0000000L) != 0) {
                best = back0_0;
                break outer;
            }
            slow0 = back0_1;
            slow1 = back1_1;
            back0_1 = (back0_0 | ((back0_0 << 1) & loverflow) | ((back0_0 >>> 1) & roverflow));
            back1_1 = (back1_0 | ((back1_0 << 1) & loverflow) | ((back1_0 >>> 1) & roverflow));
            temp = back0_1;
            back0_1 = (back0_1 | (back0_1 << 9) | (back0_1 >>> 9) | (back1_1 << 54));
            back1_1 = (back1_1 | (back1_1 << 9) | (back1_1 >>> 9) | (temp >>> 54));
            back0_2 = (back0_1 & slow_mask0);
            back0_1 = slow0 | (back0_1 & clear0);
            back1_2 = (back1_1 & slow_mask1);
            back1_1 = slow1 | (back1_1 & clear1);
            if ((back0_1 & 0x70381c0000000L) != 0) {
                best = back0_1;
                break outer;
            }
            slow0 = back0_2;
            slow1 = back1_2;
            back0_2 = (back0_1 | ((back0_1 << 1) & loverflow) | ((back0_1 >>> 1) & roverflow));
            back1_2 = (back1_1 | ((back1_1 << 1) & loverflow) | ((back1_1 >>> 1) & roverflow));
            temp = back0_2;
            back0_2 = (back0_2 | (back0_2 << 9) | (back0_2 >>> 9) | (back1_2 << 54));
            back1_2 = (back1_2 | (back1_2 << 9) | (back1_2 >>> 9) | (temp >>> 54));
            back0_0 = (back0_2 & slow_mask0);
            back0_2 = slow0 | (back0_2 & clear0);
            back1_0 = (back1_2 & slow_mask1);
            back1_2 = slow1 | (back1_2 & clear1);
            if ((back0_2 & 0x70381c0000000L) != 0) {
                best = back0_2;
                break outer;
            }
            slow0 = back0_0;
            slow1 = back1_0;
            back0_0 = (back0_2 | ((back0_2 << 1) & loverflow) | ((back0_2 >>> 1) & roverflow));
            back1_0 = (back1_2 | ((back1_2 << 1) & loverflow) | ((back1_2 >>> 1) & roverflow));
            temp = back0_0;
            back0_0 = (back0_0 | (back0_0 << 9) | (back0_0 >>> 9) | (back1_0 << 54));
            back1_0 = (back1_0 | (back1_0 << 9) | (back1_0 >>> 9) | (temp >>> 54));
            back0_1 = (back0_0 & slow_mask0);
            back0_0 = slow0 | (back0_0 & clear0);
            back1_1 = (back1_0 & slow_mask1);
            back1_0 = slow1 | (back1_0 & clear1);
            if ((back0_0 & 0x70381c0000000L) != 0) {
                best = back0_0;
                break outer;
            }
            slow0 = back0_1;
            slow1 = back1_1;
            back0_1 = (back0_0 | ((back0_0 << 1) & loverflow) | ((back0_0 >>> 1) & roverflow));
            back1_1 = (back1_0 | ((back1_0 << 1) & loverflow) | ((back1_0 >>> 1) & roverflow));
            temp = back0_1;
            back0_1 = (back0_1 | (back0_1 << 9) | (back0_1 >>> 9) | (back1_1 << 54));
            back1_1 = (back1_1 | (back1_1 << 9) | (back1_1 >>> 9) | (temp >>> 54));
            back0_2 = (back0_1 & slow_mask0);
            back0_1 = slow0 | (back0_1 & clear0);
            back1_2 = (back1_1 & slow_mask1);
            back1_1 = slow1 | (back1_1 & clear1);
            if ((back0_1 & 0x70381c0000000L) != 0) {
                best = back0_1;
                break outer;
            }
        }
        
        Direction bestDir = null;
        int bestDist = 1073741824;
        if ((best & 0x1000000000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.NORTHWEST);
            d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.NORTHWEST;
                bestDist = d;
            }
        }
        if ((best & 0x2000000000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.NORTH);
            d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.NORTH;
                bestDist = d;
            }
        }
        if ((best & 0x4000000000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.NORTHEAST);
            d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.NORTHEAST;
                bestDist = d;
            }
        }
        if ((best & 0x20000000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.EAST);
            d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.EAST;
                bestDist = d;
            }
        }
        if ((best & 0x8000000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.WEST);
            d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.WEST;
                bestDist = d;
            }
        }
        if ((best & 0x40000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.SOUTHWEST);
            d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.SOUTHWEST;
                bestDist = d;
            }
        }
        if ((best & 0x80000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.SOUTH);
            d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.SOUTH;
                bestDist = d;
            }
        }
        if ((best & 0x100000000L) > 0) {
            MapLocation loc = rc.adjacentLocation(Direction.SOUTHEAST);
            d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {
                bestDir = Direction.SOUTHEAST;
                bestDist = d;
            }
        }
        
        if (bestDir != null) {
            MapLocation loc = rc.adjacentLocation(bestDir);
            if (rc.senseMapInfo(loc).isWater()) {
                if (rc.canFill(loc)) {
                    rc.fill(loc);
                }
            }
            if (rc.canMove(bestDir)) {
                rc.move(bestDir);
            }
        }
    }
}
