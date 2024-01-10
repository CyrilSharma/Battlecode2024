package initialBot;
import battlecode.common.*;

public class FlagMicro {
    RobotController rc;
    Pathing path;
    RobotInfo[] friends;
    FlagInfo[] flags;
    public FlagMicro(Duck d) {
        this.rc = d.rc;
        this.path = d.path;
    }

    
}
