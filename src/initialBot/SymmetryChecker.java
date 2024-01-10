package initialBot;
import battlecode.common.*;

class SymmetryChecker {
    int W, H;
    RobotController rc;
    MapInfo[][] tiles;
    boolean ready = false;
    int curW = 0;
    int curI = 0;

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
        MapLocation[] spawns = rc.getAllySpawnLocations();
        while (curI < spawns.length) {
            if (Clock.getBytecodesLeft() < 300) return false;
            MapLocation m = spawns[curI++];
            tiles[m.x][m.y] = new MapInfo(
                m, true, false, 0,
                false, 0, TrapType.NONE
            );
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
        if (getSymmetry() != -1) return;
        if (!isReady()) return;

        int status = rc.readSharedArray(Channels.SYMMETRY);
        MapLocation s = null;
        MapInfo mi = null;
        MapInfo[] infos = rc.senseNearbyMapInfos();
        for (int i = infos.length; i-- > 0;) {
            if (Clock.getBytecodesLeft() < 500) break;
            MapInfo m = infos[i];
            MapLocation mloc = m.getMapLocation();
            tiles[mloc.x][mloc.y] = mi;

            s = getHSym(mloc);
            mi = tiles[s.x][s.y];
            if ((mi != null)) {
                if ((mi.isPassable() != m.isPassable()) || (mi.isSpawnZone() != m.isSpawnZone())) {   
                    status |= 1;
                }
            }
            
            s = getVSym(mloc);
            mi = tiles[s.x][s.y];
            if ((mi != null)) {
                if ((mi.isPassable() != m.isPassable()) || (mi.isSpawnZone() != m.isSpawnZone())) {
                    status |= 2;
                }
            }

            s = getRSym(mloc);
            mi = tiles[s.x][s.y];
            if ((mi != null)) {
                if ((mi.isPassable() != m.isPassable()) || (mi.isSpawnZone() != m.isSpawnZone())) {
                    status |= 4;
                }
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