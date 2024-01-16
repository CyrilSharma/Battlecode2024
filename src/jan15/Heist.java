package jan15;
import battlecode.common.*;

public class Heist {
    RobotController rc;
    Communications communications;
    SymmetryChecker sc;
    MapLocation[] spawnCenters;
    boolean heist;
    Pathing path;
    int cur = 0;
    MapLocation goal, goal1, goal2;

    public Heist(Robot robot) {
        this.rc = robot.rc;
        this.communications = robot.communications;
        this.heist = false;
        this.path = new Pathing(robot);
        this.sc = robot.sc;
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
    }

    public boolean needHeist() throws GameActionException {
        if (communications.order >= 9 && communications.order <= 20 && rc.getRoundNum() % 200 <= 10) {
            int v = rc.getRoundNum() / 200;
            goal = spawnCenters[v % 3];
            MapLocation s = spawnCenters[v % 3];
            if (sc.getSymLoc(s) != null) goal2 = sc.getSymLoc(s);
            else {
                int status = rc.readSharedArray(Channels.SYMMETRY);
                MapLocation[] pos = new MapLocation[3];
                int ind = 0;
                if ((status & 1) == 0) pos[ind++] = sc.getHSym(s);
                if (((status >> 1) & 1) == 0) pos[ind++] = sc.getVSym(s);
                if (((status >> 2) & 1) == 0) pos[ind++] = sc.getRSym(s);
                goal2 = pos[v % ind];
            }
            int dx = goal2.x - goal.x;
            int dy = goal2.y - goal.y;
            if(v % 2 == 0) {
                goal1 = new MapLocation(goal.x, goal2.y + (dy > 0 ? 7 : -7));
            }
            else {
                goal1 = new MapLocation(goal2.x + (dx > 0 ? 7 : -7), goal.y);
            }
            goal1 = new MapLocation(Math.min(rc.getMapWidth(), Math.max(0, goal1.x)), Math.min(rc.getMapHeight(), Math.max(0, goal1.y)));
            heist = true;
            cur = 0;
        }
        if (rc.getRoundNum() % 200 > (rc.getMapHeight() + rc.getMapWidth() + 20) || rc.hasFlag()) heist = false;
        return heist;
    }

    public void runHeist() throws GameActionException{
        rc.setIndicatorString("heisting " + goal + ", " + goal1 + ", " + goal2);
        if (rc.getRoundNum() % 300 <= 30) path.moveTo(goal);
        else {
            if (cur == 0 && rc.getLocation().distanceSquaredTo(goal1) < 10) cur = 1;
            if (cur == 0) path.moveTo(goal1);
            else {
                if (rc.getLocation().distanceSquaredTo(goal2) < 10) heist = false;
                path.moveTo(goal2);
            }
        }
    }
}