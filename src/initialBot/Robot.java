package initialBot;
import java.util.Random;
import battlecode.common.*;

public abstract class Robot {
    RobotController rc;
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
    }
    
    abstract void run() throws GameActionException;
}
