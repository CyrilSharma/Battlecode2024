package initialBot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.TrapType;

public class TrapMicro {

    RobotController rc;

    public TrapMicro(Robot r) {
        this.rc = r.rc;
    }

    void placeTrap() throws GameActionException {
        if (!rc.isActionReady()) return;
        MicroTarget[] microtargets = new MicroTarget[9];

        microtargets[0] = new MicroTarget(Direction.CENTER);
        microtargets[1] = new MicroTarget(Direction.NORTH);
        microtargets[2] = new MicroTarget(Direction.NORTHEAST);
        microtargets[3] = new MicroTarget(Direction.NORTHWEST);
        microtargets[4] = new MicroTarget(Direction.EAST);
        microtargets[5] = new MicroTarget(Direction.WEST);
        microtargets[6] = new MicroTarget(Direction.SOUTH);
        microtargets[7] = new MicroTarget(Direction.SOUTHEAST);
        microtargets[8] = new MicroTarget(Direction.SOUTHWEST);

        RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for (int i = robots.length; i-- > 0;) {
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
        }

        MicroTarget best = microtargets[0];
        if (microtargets[1].isBetterThan(best)) best = microtargets[1];
        if (microtargets[2].isBetterThan(best)) best = microtargets[2];
        if (microtargets[3].isBetterThan(best)) best = microtargets[3];
        if (microtargets[4].isBetterThan(best)) best = microtargets[4];
        if (microtargets[5].isBetterThan(best)) best = microtargets[5];
        if (microtargets[6].isBetterThan(best)) best = microtargets[6];
        if (microtargets[7].isBetterThan(best)) best = microtargets[7];
        if (microtargets[8].isBetterThan(best)) best = microtargets[8];

        if (best.enemyDamageScore >= 750 * 7) {
            rc.build(TrapType.EXPLOSIVE, best.nloc);
        }

    }

    class MicroTarget {
        MapLocation nloc;
        boolean canPlace;
        int enemyDamageScore;
        
        MicroTarget(Direction dir) throws GameActionException {
            nloc = rc.getLocation().add(dir);
            canPlace = rc.canBuild(TrapType.EXPLOSIVE, nloc);
        }
        void addEnemy(RobotInfo r) throws GameActionException {
            if (!canPlace) return;
            int dist = r.getLocation().distanceSquaredTo(nloc);
            if (dist <= 13){
                enemyDamageScore += 500;
            }
            if (dist <= 5){
                enemyDamageScore += 200;
            }
        } 

        boolean isBetterThan(MicroTarget mt) {
            if (!canPlace) {
                return false;
            }
            if (enemyDamageScore > mt.enemyDamageScore) {
                return true;
            }
            return false;
        }
    }
}
