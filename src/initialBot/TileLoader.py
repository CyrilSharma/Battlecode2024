MASK_WIDTH = 9
MASK_HEIGHT = 7
VISION = 9
CLASS_NAME = "TileLoader"
BITS_PER_MASK = 63
TILES = ["water", "wall"]


class ClassPrinter:
    def __init__(self):
        self.tab = " " * 4
        self.level = 0
        self.file = open(f"{CLASS_NAME}.java", "w")

    def __enter__(self):
        self.level += 1

    def __exit__(self, exc_type, exc_value, traceback):
        self.level -= 1

    def print(self, text=""):
        assert self.level >= 0
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
    cp.print(f"int diff = {-(VISION//2)};")
    cp.print("MapLocation myloc = mt.rc.getLocation();")
    cp.print(f"int offset = myloc.translate(diff, diff).hashCode();")
    nmasks = (VISION * VISION + BITS_PER_MASK - 1) // BITS_PER_MASK
    for tile in TILES:
        for i in range(nmasks):
            cp.print(f"long t_{tile}_mask{i} = 0;")
    cp.print(f"long blocked = 0;")

    cp.print(f"MapInfo[] infos = mt.rc.senseNearbyMapInfos();")
    cp.print(f"for (int diffx = {-(VISION//2)}; diffx <= {(VISION//2)}; diffx++) {{")
    with cp:
        cp.print(f"for (int diffy = {-(VISION//2)}; diffy <= {(VISION//2)}; diffy++) {{")
        with cp:
            name = "m"
            cp.print(f"MapLocation {name} = myloc.translate(diffx, diffy);")
            # cp.print(f"MapInfo {name} = infos[j];")
            cp.print(f"if (rc.canSenseLocation({name}) && rc.senseMapInfo({name}).isWater()) {{")
            with cp:
                tile = "water"
                load_switch(f"t_{tile}_mask")
            cp.print(f"}} else if (!rc.onTheMap({name}) || (rc.canSenseLocation({name}) && rc.senseMapInfo({name}).isWall())) {{")
            with cp:
                tile = "wall"
                load_switch(f"t_{tile}_mask")
            cp.print("}")

        cp.print("}")
    cp.print("}")
    cp.print()

    # This is pretty useful and kind of fits with map tracking,
    # So I'm going to keep it here.
    start = 1 << (4 * MASK_WIDTH + 4)
    mp = {
        "Direction.NORTHWEST": start << (MASK_WIDTH - 1),
        "Direction.NORTH": start << (MASK_WIDTH),
        "Direction.NORTHEAST": start << (MASK_WIDTH + 1),
        "Direction.EAST": start << (1),
        "Direction.WEST": start >> (1),
        "Direction.SOUTHWEST": start >> (MASK_WIDTH + 1),
        "Direction.SOUTH": start >> (MASK_WIDTH),
        "Direction.SOUTHEAST": start >> (MASK_WIDTH - 1),
    }

    for key, value in mp.items():
        cp.print(f"if (!rc.canMove({key})) {{ blocked += {hex(value)}L;}}")
    cp.print("blocked ^= (blocked & t_water_mask0);")

    # Will need fancier code to support different mask sizes.
    for tile in TILES:
        cp.print(f"mt.{tile}_mask1 = t_{tile}_mask1;")
        cp.print(f"mt.{tile}_mask0 = t_{tile}_mask0;")
    cp.print(f"mt.adjblocked = blocked;")
    cp.print("mt.infos = infos;")


def load_switch(mask):
    cp.print("switch (m.hashCode() - offset) {")
    with cp:
        for idx in range(VISION * VISION):
            y = idx // 9
            x = idx % 9
            line = f"case {(1 << 16) * x + y}: "

            # we don't store bit 0 at position 0, instead we store directly in it's final position.
            block = idx // BITS_PER_MASK
            index = idx % (BITS_PER_MASK)
            line += f"{mask}{block} += {hex(1 << index)}L; continue;"
            cp.print(line)
        cp.print('default: System.out.println("This shouldn\'t happen..."); continue;')
    cp.print("}")


printLoader()
