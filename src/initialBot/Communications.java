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

    public void addAttackTarget(MapLocation m, int score) throws GameActionException {
        int hash = hashAttackTarget(new AttackTarget(m, score));

        // If there's an empty spot, put it there.
        for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
            int item = rc.readSharedArray(Channels.ATTACK_TARGETS + i);
            if (item == 0) {
                rc.writeSharedArray(Channels.ATTACK_TARGETS + i, hash);
                return;
            }
        }

        // If there's any target relatively close to this one, and closer to flags,
        // This target should not be added.
        for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
            int item = rc.readSharedArray(Channels.ATTACK_TARGETS + i);
            if (item != 0) {
                AttackTarget at = dehashAttackTarget(item);
                if (at.m.distanceSquaredTo(m) <= 100) return;
            }
        }

        // Replace any target which isn't as close as the current target.
        for (int i = 0; i < Channels.N_ATTACK_TARGETS; i++) {
            int item = rc.readSharedArray(Channels.ATTACK_TARGETS + i);
            if (item != 0) {
                AttackTarget at = dehashAttackTarget(item);
                if (score < at.score) {
                    rc.writeSharedArray(Channels.ATTACK_TARGETS + i, hash);
                    return;
                }
            }
        }
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