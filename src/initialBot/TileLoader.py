# This can easily be made cheaper
# Don't load into longs, load into ints
# Fully unroll. Saves about 7 instruction per loop iteration.

RSQR = 20
MASK_WIDTH = 9
MASK_HEIGHT = 7
VISION = 9
CLASS_NAME = "TileLoader"
BITS_PER_MASK = 64
TILES = [
    "water",
    "wall"
]

class ClassPrinter:
    def __init__(self):
        self.tab = ' ' * 4
        self.level = 0
        self.file = open(f"{CLASS_NAME}.java", 'w')

    def __enter__(self):
        self.level += 1

    def __exit__(self, exc_type, exc_value, traceback):
        self.level -= 1

    def print(self, text=""):
        assert(self.level >= 0)
        self.file.write(f"{self.tab * self.level}{text}\n")
    
cp = ClassPrinter()

def printLoader():
    cp.print("package initialBot;")
    cp.print("import battlecode.common.*;")
    cp.print(f"public class {CLASS_NAME} {{")
    with cp:
        cp.print("RobotController rc;")
        cp.print(f"public {CLASS_NAME}(RobotController rc) {{")
        with cp:
            cp.print("this.rc = rc;")
        cp.print("}")
        cp.print("public void load(MapTracker mt) throws GameActionException {")
        with cp:
            load_tiles()
        cp.print("}")
    cp.print("}")

def load_tiles():
    cp.print(f"MapLocation myloc = rc.getLocation();")
    nmasks = (VISION * VISION + BITS_PER_MASK - 1) // BITS_PER_MASK
    for tile in TILES:
        for i in range(nmasks):
            cp.print(f"long t_{tile}_mask{i} = 0;")
    cp.print(f"long blocked = 0;")
    
    cp.print("MapLocation loc = null;")
    cp.print("MapInfo m = null;")
    for y in range(VISION):
        for x in range(VISION):
            dy = (y - (VISION // 2))
            dx = (x - (VISION // 2))
            if (dy * dy + dx * dx > RSQR): continue
            cp.print(f"loc = myloc.translate({dx}, {dy});")
            cp.print("if (rc.canSenseLocation(loc)) {")
            with cp:
                cp.print(f"m = rc.senseMapInfo(loc);")
                cp.print(f"if (m.isWater()) {{")
                with cp:
                    tile = "water"
                    idx = y * 9 + x
                    block = idx // BITS_PER_MASK; index = idx % (BITS_PER_MASK);
                    cp.print(f"t_{tile}_mask{block} += {hex(1 << index)}L;")
                cp.print(f"}} else if (m.isWall()) {{")
                with cp:
                    tile = "wall"
                    idx = y * 9 + x
                    block = idx // BITS_PER_MASK; index = idx % (BITS_PER_MASK);
                    cp.print(f"t_{tile}_mask{block} += {hex(1 << index)}L;")
                cp.print("}")
            cp.print("}")
    cp.print()

    # This is pretty useful and kind of fits with map tracking,
    # So I'm going to keep it here.
    start = 1 << (4 * MASK_WIDTH + 4)
    mp = {
        "Direction.NORTHWEST": start << (MASK_WIDTH + 1),
        "Direction.NORTH":     start << (MASK_WIDTH),
        "Direction.NORTHEAST": start << (MASK_WIDTH - 1),
        "Direction.EAST":      start << (1),
        "Direction.WEST":      start >> (1),
        "Direction.SOUTHWEST": start >> (MASK_WIDTH - 1),
        "Direction.SOUTH":     start >> (MASK_WIDTH),
        "Direction.SOUTHEAST": start >> (MASK_WIDTH + 1)
    }
    for key, value in mp.items():
        cp.print(f"if (!rc.canMove({key})) {{ blocked += {value}L;}}")


    # Will need fancier code to support different mask sizes.
    for tile in TILES:
        cp.print(f"mt.{tile}_mask1 = t_{tile}_mask1;")
        cp.print(f"mt.{tile}_mask0 = t_{tile}_mask0;")
    cp.print(f"mt.adjblocked = blocked;")

printLoader()