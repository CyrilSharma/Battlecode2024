MASK_WIDTH = 9
MASK_HEIGHT = 7
VISION = 9
CLASS_NAME = "TileLoader"
BITS_PER_MASK = 63
TILES = ["water", "wall", "bomb", "stun"]


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
    cp.print("package sprint2;")
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
    cp.print(f"int offset = mt.rc.getLocation().translate(diff, diff).hashCode();")
    nmasks = (VISION * VISION + BITS_PER_MASK - 1) // BITS_PER_MASK
    for tile in TILES:
        for i in range(nmasks):
            cp.print(f"long t_{tile}_mask{i} = 0;")
    cp.print(f"long blocked = 0;")

    cp.print(f"MapInfo[] infos = mt.rc.senseNearbyMapInfos();")
    cp.print("for (int j = infos.length; j-- > 0; ) {")
    with cp:
        name = "m"
        cp.print(f"MapInfo {name} = infos[j];")
        cp.print(f"if ({name}.isWater()) {{")
        with cp: load_switch(f"t_water_mask")
        cp.print(f"}} else if ({name}.isWall()) {{")
        with cp: load_switch(f"t_wall_mask")
        cp.print("}")
        cp.print(f"switch ({name}.getTrapType()) {{")
        with cp: 
            cp.print("case EXPLOSIVE:")
            with cp: load_switch(f"t_bomb_mask")
        with cp: 
            cp.print("case STUN:")
            with cp: load_switch(f"t_stun_mask")
            cp.print("default: break;")
        cp.print("}")
    cp.print("}")
    cp.print()

    load_switch2()

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
    cp.print("switch (m.getMapLocation().hashCode() - offset) {")
    with cp:
        for idx in range(VISION * VISION):
            y = idx // 9
            x = idx % 9
            line = f"case {(1 << 16) * x + y}: "

            # we don't store bit 0 at position 0, instead we store directly in it's final position.
            block = idx // BITS_PER_MASK
            index = idx % (BITS_PER_MASK)
            line += f"{mask}{block} += {hex(1 << index)}L; break;"
            cp.print(line)
        cp.print('default: System.out.println("This shouldn\'t happen..."); break;')
    cp.print("}")


def load_switch2():
    # bottom rows
    cp.print("switch (rc.getLocation().y) {")
    with cp:
        mask = 0
        for idx in range(VISION // 2):
            # fill out row as unreachable
            mask += ((1 << VISION) - 1) * (1 << idx * VISION)
            cp.print(
                f"case {VISION//2 - 1 - idx}: t_wall_mask0 += {hex(mask)}L; break;"
            )
    cp.print("}")

    # left side
    cp.print("switch (rc.getLocation().x) {")
    mask0_ = 0b000000001000000001000000001000000001000000001000000001000000001
    mask1_ = 0b000000001000000001
    with cp:
        mask0 = 0
        mask1 = 0
        i = 0
        for idx in range(VISION // 2 - 1, -1, -1):
            # fill out row as unreachable
            mask0 += mask0_ << i
            mask1 += mask1_ << i
            cp.print(
                f"case {idx}: t_wall_mask0 += {hex(mask0)}L; t_wall_mask1 += {hex(mask1)}L; break;"
            )
            i += 1
    cp.print("}")

    # top rows
    cp.print("switch (rc.getMapHeight() - rc.getLocation().y) {")
    with cp:
        mask1 = ((1 << VISION) - 1) << VISION
        mask2 = mask1 + ((1 << VISION) - 1)
        mask3 = ((1 << VISION) - 1) * (1 << VISION * 6)
        mask4 = mask3 + ((1 << VISION) - 1) * (1 << VISION * 5)
        cp.print(f"case 4: t_wall_mask1 += {hex(mask1)}L; break;")
        cp.print(f"case 3: t_wall_mask1 += {hex(mask2)}L; break;")
        cp.print(
            f"case 2: t_wall_mask1 += {hex(mask2)}L; t_wall_mask0 += {hex(mask3)}L; break;"
        )
        cp.print(
            f"case 1: t_wall_mask1 += {hex(mask2)}L; t_wall_mask0 += {hex(mask4)}L; break;"
        )
    cp.print("}")

    # right side
    cp.print("switch (rc.getMapWidth() - rc.getLocation().x) {")
    mask0_ = 0b100000000100000000100000000100000000100000000100000000100000000
    mask1_ = 0b100000000100000000
    with cp:
        mask0 = 0
        mask1 = 0
        i = 0
        for idx in range(VISION // 2, 0, -1):
            # fill out row as unreachable
            mask0 += mask0_ >> i
            mask1 += mask1_ >> i
            cp.print(
                f"case {idx}: t_wall_mask0 += {hex(mask0)}L; t_wall_mask1 += {hex(mask1)}L; break;"
            )
            i += 1
    cp.print("}")
    cp.print("")


printLoader()
