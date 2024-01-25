MAX_HEIGHT = 60
MAX_WIDTH = 60
FE_MASK_WIDTH = 9
FE_MASK_HEIGHT = 7
VISION = 9
CLASS_NAME = "NeighborLoader"
BITS_PER_MASK = 63

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
    cp.print("package sprint2;")
    cp.print("import battlecode.common.*;")
    cp.print(f"public class {CLASS_NAME} {{")
    with cp:
        cp.print("RobotController rc;")
        cp.print(f"public {CLASS_NAME}(RobotController rc) {{")
        with cp:
            cp.print("this.rc = rc;")
        cp.print("}")
        cp.print("public void load(NeighborTracker am) throws GameActionException {")
        with cp:
            cp.print("if (!rc.isSpawned()) return;")
            cp.print("load_allies(am);")
            cp.print("load_enemies(am);")
        cp.print("}")
        cp.print()
        load_allies()
        cp.print()
        load_enemies()
    cp.print("}")

def load_allies():
    cp.print("public void load_allies(NeighborTracker am) throws GameActionException {")
    with cp: load_pieces(True)
    cp.print("}")

def load_enemies():
    cp.print("public void load_enemies(NeighborTracker am) throws GameActionException {")
    with cp: load_pieces(False)
    cp.print("}")

def load_pieces(friend):
    cp.print(f"int diff = {-(VISION//2)};")
    cp.print(f"int offset = rc.getLocation().translate(diff, diff).hashCode();")
    nmasks = (VISION * VISION + BITS_PER_MASK - 1) // BITS_PER_MASK
    for i in range(nmasks):
        cp.print(f"long t_mask{i} = 0;")
    
    team = "rc.getTeam()" if friend else "rc.getTeam().opponent()"
    cp.print(f"RobotInfo[] robots = rc.senseNearbyRobots(-1, {team});")
    cp.print("for (int j = robots.length; j-- > 0; ) {")
    with cp: load_switch(f"t_mask")
    cp.print("}")
    cp.print()

    prefix = "friend" if friend else "enemy"
    cp.print(f"am.{prefix}_mask1 = t_mask1;")
    cp.print(f"am.{prefix}_mask0 = t_mask0;")

    if (friend): cp.print("am.friends = robots;")
    else:        cp.print("am.enemies = robots;")



def load_switch(mask):
    cp.print("switch (robots[j].location.hashCode() - offset) {")
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