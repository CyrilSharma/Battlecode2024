package microbaseline;
import battlecode.common.*;

public class Communications {
    RobotController rc;
    public Communications(RobotController rc) {
        this.rc = rc;
    }

    public void logflag(MapLocation m) throws GameActionException {
        int hash = (m.x << 6) + (m.y + 1);
        for (int i = Channels.FLAGS; i < Channels.FLAGS + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == 0) rc.writeSharedArray(i, hash);
        }
    }

    public void deleteflag(MapLocation m) throws GameActionException {
        int hash = (m.x << 6) + (m.y + 1);
        for (int i = Channels.FLAGS; i < Channels.FLAGS + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == hash) rc.writeSharedArray(i, 0);
        }
    }

    public MapLocation[] getflags() throws GameActionException {
        int ctr = 0;
        MapLocation[] locs = new MapLocation[3];
        for (int i = Channels.FLAGS; i < Channels.FLAGS + 3; i++) {
            int data = rc.readSharedArray(i);
            if (data == 0) continue;
            locs[ctr++] = new MapLocation(data >> 6, (data & 0b111111) - 1);
        }
        MapLocation[] trim = new MapLocation[ctr];
        for (int i = ctr; i-- > 0;) {
            trim[i] = locs[i];
        }
        return trim;
    }
}