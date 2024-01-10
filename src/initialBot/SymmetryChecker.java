package initialBot;
import battlecode.common.*;

class SymmetryChecker {
    int W, H;
    RobotController rc;
    MapInfo[][] tiles;
    boolean ready = false;
    int curW = 0;

    SymmetryChecker(RobotController rc) {
        this.rc = rc;
        W = rc.getMapWidth();
        H = rc.getMapHeight();
        tiles = new MapInfo[W][];
    }

    boolean isReady() {
        if (ready) return true;
        while (curW < W) {
            if (Clock.getBytecodesLeft() < 300) return false;
            tiles[curW] = new MapInfo[H];
            curW++;
        }
        ready = true;
        return true;
    }

    MapLocation getSymLoc(MapLocation m) throws GameActionException {
        int status = rc.readSharedArray(Channels.SYMMETRY);
        switch (status & 0b111) {
            case 0b110: return getHSym(m);
            case 0b101: return getVSym(m);
            case 0b011: return getRSym(m);
            default: return null;
        }
    }

    int getSymmetry() throws GameActionException {
        int status = rc.readSharedArray(Channels.SYMMETRY);
        switch (status & 0b111) {
            case 0b110: return 0;
            case 0b101: return 1;
            case 0b011: return 2;
            default: return -1;
        }
    }

    void updateSymmetry() throws GameActionException {
        // Mark whether or not you can see an enemy HQ.
        if (getSymmetry() != -1) return;

        int status = rc.readSharedArray(Channels.SYMMETRY);
        MapLocation s = null;
        MapLocation[] spawns = rc.getAllySpawnLocations();
        for (int i = spawns.length; i-- > 0;) {
            if (Clock.getBytecodesLeft() < 2000) break;
            MapLocation m = spawns[i];
            s = getHSym(m);
            if (rc.canSenseLocation(s)) {
                MapInfo e = rc.senseMapInfo(s);
                if (!e.isSpawnZone()) status |= 1;
            }
            s = getVSym(m);
            if (rc.canSenseLocation(s)) {
                MapInfo e = rc.senseMapInfo(s);
                if (!e.isSpawnZone()) status |= 2;
            }
            s = getRSym(m);
            if (rc.canSenseLocation(s)) {
                MapInfo e = rc.senseMapInfo(s);
                if (!e.isSpawnZone()) status |= 4;
            }
        }

        if (isReady()) {
            MapInfo mi = null;
            MapInfo[] infos = rc.senseNearbyMapInfos();
            for (int i = infos.length; i-- > 0;) {
                if (Clock.getBytecodesLeft() < 500) break;
                MapInfo m = infos[i];
                s = getHSym(m.getMapLocation());
                mi = tiles[s.x][s.y];
                if (mi != null && mi.isPassable() != m.isPassable()) status |= 1;
                
                s = getVSym(m.getMapLocation());
                mi = tiles[s.x][s.y];
                if (mi != null && mi.isPassable() != m.isPassable()) status |= 2;

                s = getRSym(m.getMapLocation());
                mi = tiles[s.x][s.y];
                if (mi != null && mi.isPassable() != m.isPassable()) status |= 4;
            }
        }
        rc.writeSharedArray(Channels.SYMMETRY, status);
    }

    MapLocation getHSym(MapLocation a) {
        return new MapLocation(a.x, H - a.y - 1);
    }

    MapLocation getVSym(MapLocation a) {
        return new MapLocation(W - a.x - 1, a.y);
    }

    MapLocation getRSym(MapLocation a) {
        return new MapLocation(W - a.x - 1, H - a.y - 1);
    }
}

