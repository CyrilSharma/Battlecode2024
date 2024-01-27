package jan253;
import battlecode.common.*;

public class GreedyPath {
    RobotController rc;
    boolean shouldBug = false;
    int goalRound = 0;
    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
            Direction.CENTER // Just in case.
    };

    public GreedyPath(RobotController rc) {
        this.rc = rc;
    }
    public static MapLocation bugTarget = null;
    public static int bestSoFar = 0;
    // which direction to start your checks in the next bugnav move
    public static int startDir = -1;
    // whether to search clockwise (as opposed to anticlockwise) in bugnav
    public static boolean clockwise = false;
    // number of turns in which startDir has been missing in a row
    public static int unitObstacle = 0;
    public Direction bug(MapLocation loc) throws GameActionException {
        // Exit condition: got closer to the destination then when I started.
        int dist = hybridDistance(rc.getLocation(), bugTarget);
        //rc.setIndicatorString("D: "+dist+" BEST: "+bestSoFar+", unit: " + unitObstacle);
        // rc.setIndicatorString("BUG: " + loc + ", ori: " + clockwise + ", goalR: " + goalRound);
        if(rc.getRoundNum() == goalRound + 5) bestSoFar = dist;
        if(rc.getRoundNum() - goalRound >= 30) {
            shouldBug = false;
            return null;
        }
        if ((dist < bestSoFar) && rc.getRoundNum() - goalRound >= 5) {
            shouldBug = false;
            return null;
        }
        //try switching over

        int dir = startDir;
        //StringBuilder uwu = new StringBuilder();
        //uwu.append("|" + clockwise + "|");
        for (int i = 0; i < 8; i++) {
            if (dir == 8) dir = 0;
            MapLocation next = rc.getLocation().add(directions[dir]);
            //uwu.append("|"+next+"|"+directions[dir]+"|"+unitObstacle);
            if(dir == startDir && (rc.canSenseLocation(next) && rc.senseRobotAtLocation(next) != null)){
                unitObstacle++;
            }
            if(dir == startDir && (rc.canSenseLocation(next) && rc.senseRobotAtLocation(next) == null)){
                unitObstacle = 0;
            }
            if(unitObstacle == 3){
                //resetBug(loc);
                bugTarget = loc;
                goalRound = rc.getRoundNum();
                startDir = rc.getLocation().directionTo(bugTarget).ordinal();
                bestSoFar = hybridDistance(rc.getLocation(), bugTarget);
                unitObstacle = 0;
                i = 0;
                dir = startDir;
                next = rc.adjacentLocation(directions[dir]);
                if(!rc.canSenseLocation(next)) break;
                if(dir == startDir && (rc.canSenseLocation(next) && rc.senseRobotAtLocation(next) != null)) unitObstacle++;
            }
            // If you hit the edge of the map, reverse direction
            if (!rc.onTheMap(next)) {
                clockwise = !clockwise;
                dir = startDir;
            }
            if (tryMove(directions[dir])) {
                // Safeguard 1: dir might equal startDir if this robot was blocked by another robot last turn
                // that has since moved.
                if (dir != startDir) {
                    if (clockwise) startDir = (dir + 6) % 8;
                    else startDir = (dir + 2) % 8;
                }
                if (!rc.onTheMap(rc.adjacentLocation(directions[startDir]))) {
                    startDir = rc.getLocation().directionTo(bugTarget).ordinal();
                }
                if (startDir == 8) {
                    startDir = 0;
                }
                //rc.setIndicatorString("dre: " + directions[dir] + ", start: " + directions[startDir]);
                //uwu.append("|"+directions[startDir]);
                //rc.setIndicatorString(uwu.toString());
                return directions[dir];
            }

            if (clockwise) dir = (dir + 1) % 8;
            else dir = (dir + 7) % 8;
        }
        //rc.setIndicatorString(uwu.toString());
        return null;
    }

    void resetBug(MapLocation loc) throws GameActionException {
        //here we need to consider both directions
        bugTarget = loc;
        bestSoFar = hybridDistance(rc.getLocation(), bugTarget);
        startDir = rc.getLocation().directionTo(loc).ordinal();
        unitObstacle = 0;
        goalRound = rc.getRoundNum();
        clockwise = Math.random() < 0.5;
    }


    // hybrid between manhattan distance (dx + dy) and max distance max(dx, dy)
    public static int hybridDistance(MapLocation a, MapLocation b) {
        int dy = Math.abs(a.y - b.y);
        int dx = Math.abs(a.x - b.x);
        return dy + dx;
    }

    public boolean tryMove(Direction dir) throws GameActionException{
        if(!rc.canSenseLocation(rc.getLocation().add(dir))) return false;
        if(rc.canMove(dir) && !dir.equals(Direction.CENTER)){
            rc.move(dir);
            return true;
        }
        return false;
    }

}
