package initialBot;
import java.util.Random;

import battlecode.common.*;

public class Exploration {
    RobotController rc;
    final Random rng = new Random();
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };
    public Exploration(RobotController rc){
        this.rc = rc;
    }
}