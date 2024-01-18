MAX_HEIGHT = 60
MAX_WIDTH = 60
FE_MASK_WIDTH = 9
FE_MASK_HEIGHT = 7
VISION = 9
CLASS_NAME = "NeighborLoader"
BITS_PER_MASK = 63
PIECES = [
    "heal",
    "attack"
]
MAX_LEVEL = 6

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
        cp.print("public void load(NeighborTracker nt) throws GameActionException {")
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
    cp.print("public void load_allies(NeighborTracker nt) throws GameActionException {")
    with cp: load_pieces(True)
    cp.print("}")

def load_enemies():
    cp.print("public void load_enemies(NeighborTracker nt) throws GameActionException {")
    with cp: load_pieces(False)
    cp.print("}")

def load_pieces(friend):
    cp.print(f"int diff = {-(VISION//2)};")
    cp.print(f"int offset = rc.getLocation().translate(diff, diff).hashCode();")
    nmasks = (VISION * VISION + BITS_PER_MASK - 1) // BITS_PER_MASK
    for piece in PIECES:
        for level in range(0, MAX_LEVEL + 1):
            for i in range(nmasks):
                cp.print(f"long t_{piece}{level}_mask{i} = 0;")
    
    team = "rc.getTeam()" if friend else "rc.getTeam().opponent()"
    cp.print(f"RobotInfo[] robots = rc.senseNearbyRobots(-1, {team});")
    cp.print("for (int j = robots.length; j-- > 0; ) {")
    with cp:
        cp.print("RobotInfo r = robots[j];")
        cp.print("switch (r.getAttackLevel()) {")
        for j in range(0, MAX_LEVEL + 1):
            with cp:
                cp.print(f"case {j}:")
                with cp: load_switch(f"t_attack{level}_mask")
        cp.print("}")
        cp.print("switch (r.getHealLevel()) {")
        for j in range(0, MAX_LEVEL + 1):
            with cp:
                cp.print(f"case {j}:")
                with cp: load_switch(f"t_heal{level}_mask")
        cp.print("}")
    cp.print("}")
    cp.print()

    prefix = "friend" if friend else "enemy"
    for level in range(0, MAX_LEVEL + 1):
        for piece in PIECES:
            for i in range(nmasks):
                cp.print(f"nt.{prefix}_{piece}_mask1[{level}] = t_{piece}{level}_mask{i};")
                cp.print(f"nt.{prefix}_{piece}_mask0[{level}] = t_{piece}{level}_mask{i};")

    if (friend): cp.print("nt.friends = robots;")
    else:        cp.print("nt.enemies = robots;")



def load_switch(mask):
    cp.print("switch (r.location.hashCode() - offset) {")
    with cp:
        for idx in range(VISION * VISION):
            y = idx // 9; x = idx % 9;
            line = f"case {(1 << 16) * x + y}: "

            # we don't store bit 0 at position 0, instead we store directly in it's final position.
            block = idx // BITS_PER_MASK; index = idx % (BITS_PER_MASK);
            line += f"{mask}{block} += {hex(1 << index)}L; continue;"
            cp.print(line)
        cp.print("default: continue;")
    cp.print("}")

printLoader()