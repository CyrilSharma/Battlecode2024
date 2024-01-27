package jan253;
import java.util.Random;
import battlecode.common.*;

public abstract class Robot {
    AttackMicro am;
    RobotController rc;
    MapTracker mt;
    StunManager sm;
    NeighborTracker nt;
    Communications communications;
    SymmetryChecker sc;
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
    final Random rng = new Random();
    public Robot(RobotController rc){
        this.rc = rc;
        rng.setSeed((long) rc.getID());
        communications = new Communications(rc);
        mt = new MapTracker(rc);
        sc = new SymmetryChecker(rc);
        nt = new NeighborTracker(rc);
        sm = new StunManager(rc, mt, nt);
    }
    
    public void init_turn() throws GameActionException {
        mt.run();
        nt.run();
        sm.run();
        communications.refreshTargets();
    }

    public void post_turn() throws GameActionException {
        sc.updateSymmetry();
        // if (rc.getRoundNum() % 50 == 0) {
        //     System.out.println("Symmetry: " + sc.getSymmetry());
        // }
    }

    abstract void run() throws GameActionException;
}
