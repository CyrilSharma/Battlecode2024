package initialBot;
import java.util.Random;
import battlecode.common.*;

public abstract class Robot {
    RobotController rc;
    MapTracker mt;
    final Random rng = new Random();
    Communications communications;
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
        Direction.CENTER
    };

    public Robot(RobotController rc){
        this.rc = rc;
        rng.setSeed((long) rc.getID());
        communications = new Communications(rc);
        mt = new MapTracker(rc);
    }
    
    public void init_turn() throws GameActionException {
        mt.run();
    }

    abstract void run() throws GameActionException;
}
