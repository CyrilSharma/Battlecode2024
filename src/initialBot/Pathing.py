MASK_WIDTH = 9
MASK_HEIGHT = 7
CLASS_NAME = "Pathing"
RSQR = 20

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
def printPathing():
    cp.print("package initialBot;")
    cp.print("import battlecode.common.*;")
    cp.print(f"public class {CLASS_NAME} {{")
    with cp:
        cp.print("MapTracker mt;")
        cp.print("RobotController rc;")
        cp.print("static int H, W;")
        cp.print("public Pathing(Robot robot) {")
        with cp:
            cp.print("this.rc = robot.rc;")
            cp.print("this.mt = robot.mt;")
            cp.print("H = rc.getMapHeight();")
            cp.print("W = rc.getMapWidth();")
        cp.print("}")
        cp.print("")
        cp.print("public void moveTo(MapLocation target) throws GameActionException {")
        with cp: moveTo()
        cp.print("}")
    cp.print("}")


def moveTo():
    # We could probably get away with 1 mask, if we're willing to discard squares
    # in the opposite direction of the target. We have so much bytecode tho, that I don't know
    # that this is necessary. Also, using more masks may generalize better if we ever use full-map pathing.
    cp.print(f"long loverflow = {hex(0b111111110111111110111111110111111110111111110111111110111111110)}L;")
    cp.print(f"long roverflow = {hex(0b011111111011111111011111111011111111011111111011111111011111111)}L;")
    cp.print("long passible0 = ~(mt.adjblocked | mt.wall_mask0 | mt.water_mask0);")
    cp.print("long passible1 = ~(mt.wall_mask1 | mt.water_mask1);")

    start = 1 << (4 * MASK_WIDTH + 4)

    # Compute all squares we can reach in 10 iterations.
    cp.print(f"long reach0 = {start}L;")
    cp.print(f"long reach1 = 0;")
    for _ in range(10):
        advance_reachable("reach", True)
    cp.print()


    cp.print("long targetsqrs0 = 0;")
    cp.print("long targetsqrs1 = 0;")
    cp.print("MapLocation myloc = rc.getLocation();")
    cp.print("int dist = myloc.distanceSquaredTo(target);")
    cp.print(f"if (dist > {RSQR}) {{")
    with cp:
        # If we can't see the location, just path towards the closest location to the
        # Target in our vision radius.
        cp.print(f"double mult = ((double) {RSQR}) / dist;")
        cp.print(f"int x = (int) Math.round(myloc.x + (target.x - myloc.x) * mult);")
        cp.print(f"int y = (int) Math.round(myloc.y + (target.y - myloc.y) * mult);")
        cp.print("MapLocation estimate = new MapLocation(x, y);")
        cp.print("MapLocation bestTarget = estimate;")
        cp.print(f"int bestDist = {1 << 30};")

        directions = [
            "Direction.NORTHWEST",
            "Direction.NORTH",
            "Direction.NORTHEAST",
            "Direction.EAST",
            "Direction.WEST",
            "Direction.SOUTHWEST",
            "Direction.SOUTH",
            "Direction.SOUTHEAST"
        ]
        cp.print(f"MapLocation adj = null;")
        cp.print(f"int d = 0;")
        cp.print()
        for dir in directions:
            cp.print(f"adj = estimate.add({dir});")
            cp.print("d = adj.distanceSquaredTo(target);")
            cp.print("if (rc.canSenseLocation(adj) && d < bestDist) {")
            with cp:
                cp.print("bestDist = d;")
                cp.print("bestTarget = adj;")
            cp.print("}")
        cp.print("target = bestTarget;")
    cp.print("}")
    cp.print()

    # Load the target into a bitmask.
    cp.print(f"int idx = (target.y * {MASK_WIDTH}) + target.x;")
    cp.print(f"if (idx >= {MASK_WIDTH * MASK_HEIGHT}) {{")
    with cp:
        cp.print(f"targetsqrs1 = 1 << (idx - {MASK_WIDTH * MASK_HEIGHT});")
    cp.print("} else {")
    with cp:
        cp.print(f"targetsqrs0 = 1 << idx;")
    cp.print("}")
    cp.print()

    # From the target square, propogate backwards until you reach a reachable square.
    # This is important, because we want to go towards the target, even if it's not reachable.
    cp.print(f"while ((targetsqrs0 & reach0) == 0 && (targetsqrs1 & reach1) == 0) {{")
    with cp: advance_reachable("targetsqrs", False)
    cp.print("}")
    cp.print()

    adjacency_mask = start | (start << 1) | (start >> 1)
    adjacency_mask = adjacency_mask | (adjacency_mask << MASK_HEIGHT) | (adjacency_mask >> MASK_HEIGHT)

    # From the optimal reachable square(s) propogate backwards until you hit the start square.
    cp.print(f"long back0 = targetsqrs0 & reach0;")
    cp.print(f"long back1 = targetsqrs1 & reach1;")
    cp.print(f"while ((back0 & {adjacency_mask}L) == 0) {{")
    with cp: advance_reachable("back", True)
    cp.print("}")
    cp.print()

    # You can also use a switch statement here, but it doesn't help you too much. Maybe 100 bytecode saved.
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

    # The only squares that should be active in best are those which
    # Are part of the optimal path. Hence, we can simply choose any of them.
    cp.print(f"long best = back0 & {adjacency_mask}L;")
    for key, value in mp.items():
        cp.print(f"if ((best & {value}L) > 0) {{ rc.move({key}); return; }}")


def advance_reachable(mask_name, walls):
    shift = (MASK_WIDTH * (MASK_HEIGHT - 1))
    cp.print(f"{mask_name}0 = ({mask_name}0 | (({mask_name}0 << 1) & loverflow) | (({mask_name}0 >> 1) & roverflow));")
    cp.print(f"{mask_name}1 = ({mask_name}1 | (({mask_name}1 << 1) & loverflow) | (({mask_name}1 >> 1) & roverflow));")
    cp.print(f"{mask_name}0 = ({mask_name}0 | ({mask_name}0 << {MASK_WIDTH}) |"
        +    f" ({mask_name}0 >> {MASK_WIDTH}) | ({mask_name}1 << {shift})){' & passible0' if walls else ''};")
    cp.print(f"{mask_name}1 = ({mask_name}1 | ({mask_name}1 << {MASK_WIDTH}) |"
        +    f" ({mask_name}1 >> {MASK_WIDTH}) | ({mask_name}0 >> {shift})){' & passible1' if walls else ''};")

printPathing()