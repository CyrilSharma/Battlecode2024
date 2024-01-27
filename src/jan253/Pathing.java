package jan253;
import battlecode.common.*;
public class Pathing {
    MapTracker mt;
    RobotController rc;
    OptimalPathing opt;
    //bug stuff
    MapLocation goal;
    int bestDistance;
    int turnsWithoutProgress;
    GreedyPath gp;
    //bug stuff ends
    static int H, W;
    public Pathing(Robot robot) {
        this.rc = robot.rc;
        this.mt = robot.mt;
        this.opt = new OptimalPathing(robot);
        //bug stuff
        this.gp = new GreedyPath(rc);
        this.goal = null;
        bestDistance = 0;
        turnsWithoutProgress = 0;
        //bug stuff end
        H = rc.getMapHeight();
        W = rc.getMapWidth();
    }
    
    public void moveTo(MapLocation target) throws GameActionException {
        if (rc.getMovementCooldownTurns() >= 10) return;
        //bug stuff starts
        //if we have not made progress for 15 turns, we turn to bug
        //then we just continue bugging until bug decides we are over the obstacle
        if (!target.equals(goal)) {
            gp.shouldBug = false;
        }
        if (!gp.shouldBug) {
            if (!target.equals(goal)) {
                goal = target;
                bestDistance = rc.getLocation().distanceSquaredTo(goal);
                turnsWithoutProgress = 0;
            } else {
                int curDist = rc.getLocation().distanceSquaredTo(goal);
                if (curDist < bestDistance) {
                    bestDistance = curDist;
                    turnsWithoutProgress = 0;
                } else turnsWithoutProgress++;
            }
            if (turnsWithoutProgress >= 11) {
                gp.shouldBug = true;
                turnsWithoutProgress = 0;
                bestDistance = 0;
                gp.resetBug(goal);
            }
        }
        if (gp.shouldBug) {
            gp.bug(goal);
        }
        //bug stuff ends
        if (rc.getRoundNum() < GameConstants.SETUP_ROUNDS - 30) {
            opt.moveToNoWater(target);
        } else {
            //if(rc.hasFlag()) opt.moveToAvoidEnemies(target);
            opt.moveTo(target);
        }
    }
}
