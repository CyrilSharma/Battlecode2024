package sprint2;
import battlecode.common.*;

import static java.util.Arrays.sort;

public class Communications {
    int order;
    boolean flagdead = false;
    RobotController rc;
    MapLocation[] spawnCenters;
    int W, H;
    public Communications(RobotController rc) {
        this.rc = rc;
        W = rc.getMapWidth();
        H = rc.getMapHeight();
    }

    public void establishOrder() throws GameActionException {
        order = rc.readSharedArray(Channels.FIRST);
        rc.writeSharedArray(Channels.FIRST, order + 1);
    }

    public void log_enemy_flag_spawn(MapLocation m) throws GameActionException {
        int hash = hashLocation(m);
        int start = Channels.EFLAGS;
        for (int i = start; i < start + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == 0) {
                rc.writeSharedArray(i, hash);
                break;
            } else {
                MapLocation loc = dehashLocation(data);
                if (loc.distanceSquaredTo(m) <= 36) return;
            }
        }
    }

    public void delete_enemy_flag_spawn(MapLocation m) throws GameActionException {
        int hash = hashLocation(m);
        int start = Channels.EFLAGS;
        for (int i = start; i < start + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == hash) {
                rc.writeSharedArray(i, 0);
                break;
            }
        }
    }

    public MapLocation[] get_enemy_flag_spawns() throws GameActionException {
        int ctr = 0;
        MapLocation[] locs = new MapLocation[3];
        int start = Channels.EFLAGS;
        for (int i = start; i < start + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == 0) continue;
            locs[ctr++] = dehashLocation(data);
        }
        MapLocation[] trim = new MapLocation[ctr];
        while (ctr-- > 0) trim[ctr] = locs[ctr];
        return trim;
    }

    public void log_runaway_flag(MapLocation m, int id) throws GameActionException {
        int hash = hashAttackTarget(new AttackTarget(m, 1));
        int start = Channels.RUNAWAY_FLAGS;
        boolean wrote = false;
        for (int i = start; i < start + Channels.FLAG_NUM; i++) {
            int data = rc.readSharedArray(i);
            if(data != 0) {
                if(id == rc.readSharedArray(Channels.RUNAWAY_FLAGS_ID + (i - start))) {
                    rc.writeSharedArray(i, hash);
                    wrote = true;
                    break;
                }
            }
        }
        if(wrote) return;
        for (int i = start; i < start + Channels.FLAG_NUM; i++) {
            int data = rc.readSharedArray(i);
            if (data == 0) {
                rc.writeSharedArray(i, hash);
                rc.writeSharedArray(Channels.RUNAWAY_FLAGS_ID + (i - start), id);
                break;
            }
        }
    }

    public AttackTarget[] get_runaway_flags() throws GameActionException {
        int ctr = 0;
        AttackTarget[] targets = new AttackTarget[Channels.FLAG_NUM];
        for (int i = 0; i < Channels.FLAG_NUM; i++) {
            int item = rc.readSharedArray(Channels.RUNAWAY_FLAGS + i);
            if (item == 0) continue;
            AttackTarget at = dehashAttackTarget(item);
            targets[ctr++] = at;
        }
        AttackTarget[] trim = new AttackTarget[ctr];
        while (ctr-- > 0) trim[ctr] = targets[ctr];
        return trim;
    }

    public void log_carrier(MapLocation m, MapLocation tar) throws GameActionException {
        int hash = hashAttackTarget(new AttackTarget(m, 0));
        int start = Channels.FLAG_CARRIERS;
        boolean wrote = false;
        for (int i = start; i < start + Channels.FLAG_NUM; i++) {
            int data = rc.readSharedArray(i);
            if(data != 0) {
                AttackTarget dh = dehashAttackTarget(data);
                if (dh.m.distanceSquaredTo(rc.getLocation()) <= 4) {
                    rc.writeSharedArray(i, hash);
                    rc.writeSharedArray(Channels.CARRIER_TARGET + (i - start), hashLocation(tar));
                    wrote = true;
                    break;
                }
            }
        }
        if (wrote) return;
        for (int i = start; i < start + Channels.FLAG_NUM; i++) {
            int data = rc.readSharedArray(i);
            if (data == 0) {
                rc.writeSharedArray(i, hash);
                rc.writeSharedArray(Channels.CARRIER_TARGET + (i - start), hashLocation(tar));
                break;
            }
        }
    }

    public AttackTarget[] get_carriers() throws GameActionException {
        int ctr = 0;
        AttackTarget[] targets = new AttackTarget[Channels.FLAG_NUM];
        for (int i = 0; i < Channels.FLAG_NUM; i++) {
            int item = rc.readSharedArray(Channels.FLAG_CARRIERS + i);
            if (item == 0) continue;
            AttackTarget at = dehashAttackTarget(item);
            at.num = rc.readSharedArray(Channels.CARRIER_DEFENDER + i);
            at.goal = dehashLocation(rc.readSharedArray(Channels.CARRIER_TARGET + i));
            targets[ctr++] = at;
        }
        AttackTarget[] trim = new AttackTarget[ctr];
        while (ctr-- > 0) trim[ctr] = targets[ctr];
        return trim;
    }

    public void markCarrier(MapLocation loc) throws GameActionException {
        int start = Channels.FLAG_CARRIERS;
        for (int i = start; i < start + Channels.FLAG_NUM; i++) {
            int data = rc.readSharedArray(i);
            if (data != 0) {
                AttackTarget cur = dehashAttackTarget(data);
                if (cur.m.equals(loc)) {
                    int v = rc.readSharedArray(Channels.CARRIER_DEFENDER + (i - start));
                    rc.writeSharedArray(Channels.CARRIER_DEFENDER + (i - start), v + 1);
                    break;
                }
            }
        }
    }

    public AttackTarget[] getAttackTargets() throws GameActionException {
        int ctr = 0;
        AttackTarget[] targets = new AttackTarget[Channels.N_ATTACK_TARGETS];
        for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
            int item = rc.readSharedArray(Channels.ATTACK_TARGETS + i);
            if (item == 0) continue;
            AttackTarget at = dehashAttackTarget(item);
            targets[ctr++] = at;
        }

        AttackTarget[] trim = new AttackTarget[ctr];
        while (ctr-- > 0) trim[ctr] = targets[ctr];
        return trim;
    }

    // Marks a target as something I'm going to.
    public void markAttackTarget(int id) throws GameActionException {
        int channel = Channels.ATTACK_TARGETS + id;
        int item = rc.readSharedArray(channel);
        AttackTarget at = dehashAttackTarget(item);
        at.score = Math.min(at.score + 1, 15);
        rc.writeSharedArray(channel, hashAttackTarget(at));
    }

    public void addAttackTarget(MapLocation m, int score) throws GameActionException {
        int hash = hashAttackTarget(new AttackTarget(m, score));
        // If we're close to other targets, don't add us.
        for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
            int item = rc.readSharedArray(Channels.ATTACK_TARGETS + i);
            if (item != 0) {
                AttackTarget at = dehashAttackTarget(item);
                if (at.m.distanceSquaredTo(m) <= 9) return;
            }
        }

        // If there's an empty spot, put us there.
        for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
            int item = rc.readSharedArray(Channels.ATTACK_TARGETS + i);
            if (item == 0) {
                rc.writeSharedArray(Channels.ATTACK_TARGETS + i, hash);
                return;
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

    MapLocation getHSym(MapLocation a) {
        return new MapLocation(a.x, H - a.y - 1);
    }

    MapLocation getVSym(MapLocation a) {
        return new MapLocation(W - a.x - 1, a.y);
    }

    MapLocation getRSym(MapLocation a) {
        return new MapLocation(W - a.x - 1, H - a.y - 1);
    }

    public MapLocation advanceToEnemySpawn(MapLocation loc) throws GameActionException {
        if (spawnCenters == null) getSpawnCenters();
        int bestDist = 1000000;
        MapLocation bestSpawn = null;
        for (int i = 0; i < 3; i++) {
            MapLocation cur = getHSym(spawnCenters[i]);
            if (cur == null) {
                int status = rc.readSharedArray(Channels.SYMMETRY);
                if ((status & 1) == 0) cur = getHSym(spawnCenters[i]);
                else if (((status >> 1) & 1) == 0) cur = getVSym(spawnCenters[i]);
                else cur = getRSym(spawnCenters[i]);
            }
            int d = cur.distanceSquaredTo(loc);
            if (d < bestDist) {
                bestDist = d;
                bestSpawn = cur;
            }
        }
        return loc.add(loc.directionTo(bestSpawn));
    }

    public void refreshTargets() throws GameActionException {
        if (order != 0) return;
        for (int i = 0; i < Channels.CARRIER_DEFENDER_NUM; i++) rc.writeSharedArray(Channels.CARRIER_DEFENDER + i, 0);
        for (int i = 0; i < Channels.FLAG_NUM; i++) {
            int data = rc.readSharedArray(Channels.RUNAWAY_FLAGS + i);
            if(data == 0) continue;
            AttackTarget a = dehashAttackTarget(data);
            MapLocation loc = a.m;
            if (a.score == 0) {
                loc = advanceToEnemySpawn(a.m);
            }
            a = new AttackTarget(loc, 0);
            rc.writeSharedArray(Channels.RUNAWAY_FLAGS + i, hashAttackTarget(a));
        }
        if (rc.getRoundNum() % 5 == 0) {
            for (int i = 0; i < Channels.FLAG_NUM; i++) {
                rc.writeSharedArray(Channels.RUNAWAY_FLAGS + i, 0);
                rc.writeSharedArray(Channels.RUNAWAY_FLAGS_ID + i, 0);
            }
        }
        if (rc.getRoundNum() % 5 != 0) return;
        for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
            rc.writeSharedArray(Channels.ATTACK_TARGETS + i, 0);
        }
        for (int i = 0; i < Channels.FLAG_NUM; i++) {
            rc.writeSharedArray(Channels.FLAG_CARRIERS + i, 0);
            rc.writeSharedArray(Channels.CARRIER_TARGET + i, 0);
            //rc.writeSharedArray(Channels.RUNAWAY_FLAGS + i, 0);
        }
    }

    public int hashAttackTarget(AttackTarget a) throws GameActionException {
        return hashLocation(a.m) + (a.score << 12);
    }

    public AttackTarget dehashAttackTarget(int data) throws GameActionException {
        return new AttackTarget(
            dehashLocation(data & 0b111111111111),
            data >> 12
        );
    }

    public int hashLocation(MapLocation m) throws GameActionException {
        return (m.x << 6) + (m.y + 1);
    }

    public MapLocation dehashLocation(int data) throws GameActionException {
        return new MapLocation(data >> 6, (data & 0b111111) - 1);
    }

    public class AttackTarget {
        MapLocation m;
        int score;
        int num = 0;
        MapLocation goal;
        public AttackTarget(MapLocation m, int score) {
            this.m = m;
            this.score = score;
        }
    }
}