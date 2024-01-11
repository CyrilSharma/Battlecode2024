package initialBot;
import battlecode.common.*;

public class Communications {
    int order;
    RobotController rc;
    public Communications(RobotController rc) {
        this.rc = rc;
    }

    public void establishOrder() throws GameActionException {
        order = rc.readSharedArray(Channels.FIRST);
        rc.writeSharedArray(Channels.FIRST, order + 1);
    }

    public void log_flag(MapLocation m, boolean friendly) throws GameActionException {
        int hash = hashLocation(m);
        int start = (friendly) ? Channels.FFLAGS : Channels.EFLAGS;
        for (int i = start; i < start + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == 0) {
                rc.writeSharedArray(i, hash);
                break;
            }
        }
    }

    public void delete_flag(MapLocation m, boolean friendly) throws GameActionException {
        int hash = hashLocation(m);
        int start = (friendly) ? Channels.FFLAGS : Channels.EFLAGS;
        for (int i = start; i < start + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == hash) {
                rc.writeSharedArray(i, 0);
                break;
            }
        }
    }

    public MapLocation[] get_flags(boolean friendly) throws GameActionException {
        int ctr = 0;
        MapLocation[] locs = new MapLocation[3];
        int start = (friendly) ? Channels.FFLAGS : Channels.EFLAGS;
        for (int i = start; i < start + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == 0) continue;
            locs[ctr++] = dehashLocation(data);
        }
        MapLocation[] trim = new MapLocation[ctr];
        for (int i = ctr; i-- > 0;) {
            trim[i] = locs[i];
        }
        return trim;
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
        for (int i = ctr; i-- > 0;) {
            trim[i] = targets[i];
        }
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

    public void addAttackTarget(MapLocation m, boolean hasflag) throws GameActionException {
        int hash = hashAttackTarget(new AttackTarget(m, 0));
        if (!hasflag) {
            // If we're close to other targets, don't add us.
            for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
                int item = rc.readSharedArray(Channels.ATTACK_TARGETS + i);
                if (item != 0) {
                    AttackTarget at = dehashAttackTarget(item);
                    if (at.m.distanceSquaredTo(m) <= 9) return;
                }
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

        // If we still haven't written the target, and it's a flag, just overwrite something.
        if (hasflag) rc.writeSharedArray(Channels.ATTACK_TARGETS, hash);
    }

    public void refreshTargets() throws GameActionException {
        if (order != 0) return;
        if (rc.getRoundNum() % 5 != 0) return;
        for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
            rc.writeSharedArray(Channels.ATTACK_TARGETS + i, 0);
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
        public AttackTarget(MapLocation m, int score) {
            this.m = m;
            this.score = score;
        }
    }
}