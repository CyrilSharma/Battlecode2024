MAX_HEIGHT = 60
MAX_WIDTH = 60
FE_MASK_WIDTH = 9
FE_MASK_HEIGHT = 5
VISION = 9
PIECES = [
    "amplifier",
    "booster",
    "carrier",
    "destabilizer",
    "headquarters",
    "launcher"
]
CLASS_NAME = "NeighborLoader"
BITS_PER_MASK = 64

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
    cp.print("package optimizedBot;")
    cp.print("import battlecode.common.GameActionException;")
    cp.print("import battlecode.common.RobotInfo;")
    cp.print(f"public class {CLASS_NAME} {{")
    with cp:
        cp.print("public static void load(NeighborTracker nt) throws GameActionException {")
        with cp:
            cp.print("load_allies(nt);")
            cp.print("load_enemies(nt);")
        cp.print("}")
        cp.print()
        load_allies()
        cp.print()
        load_enemies()
    cp.print("}")

def load_allies():
    cp.print("public static void load_allies(NeighborTracker nt) throws GameActionException {")
    with cp: load_pieces(True)
    cp.print("}")

def load_enemies():
    cp.print("public static void load_enemies(NeighborTracker nt) throws GameActionException {")
    with cp: load_pieces(False)
    cp.print("}")

def load_pieces(friend):
    cp.print(f"int offset = ((nt.rc.getLocation()).translate({-(VISION//2)}, {-(VISION//2)})).hashCode();")
    nmasks = (VISION * VISION + BITS_PER_MASK - 1) // BITS_PER_MASK
    for piece in PIECES:
        for i in range(nmasks):
            cp.print(f"long t_{piece}_mask{i} = 0;")
    
    team = "nt.rc.getTeam()" if friend else "nt.rc.getTeam().opponent()"
    cp.print(f"RobotInfo[] robots = nt.rc.senseNearbyRobots(-1, {team});")
    cp.print("for (int j = robots.length; j-- > 0; ) {")
    with cp:
        cp.print("RobotInfo r = robots[j];")
        cp.print("switch (r.type) {")
        with cp:
            for piece in PIECES:
                cp.print(f"case {piece.upper()}: ")
                with cp: load_switch(f"t_{piece}_mask")
            cp.print("default: continue;")
        cp.print("}")
    cp.print("}")
    cp.print()


    # Will need fancier code to support different mask sizes.
    prefix = "friend" if friend else "enemy"
    for piece in PIECES:
        cp.print(f"nt.{prefix}_{piece}_mask1 = t_{piece}_mask1;")
        cp.print(f"nt.{prefix}_{piece}_mask0 = t_{piece}_mask0;")

    if (friend): cp.print("nt.friends = robots;")
    else:        cp.print("nt.enemies = robots;")



def load_switch(mask):
    offset = 1 # this should be dynamically computed.
    cp.print("switch (r.location.hashCode() - offset) {")
    with cp:
        for idx in range(VISION * VISION):
            y = idx // 9; x = idx % 9;
            line = f"case {(1 << 16) * x + y}: "

            # we don't store bit 0 at position 0, instead we store directly in it's final position.
            block = (idx + offset) // BITS_PER_MASK; index = (idx + offset) % (BITS_PER_MASK);
            line += f"{mask}{block} += {hex(1 << index)}L; continue;"
            cp.print(line)
        cp.print("default: continue;")
    cp.print("}")

printLoader()