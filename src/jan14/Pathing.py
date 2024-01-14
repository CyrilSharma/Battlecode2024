import math

MASK_WIDTH = 9
MASK_HEIGHT = 7
CLASS_NAME = "OptimalPathing"
RSQR = 20
COST_OF_WATER = 3  # 2 actual turns, one for filling, and one for moving. +1 accounts for crumbs and base cooldown cost


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


def printPathing():
    cp.print("package initialBot;")
    cp.print("import battlecode.common.*;")
    cp.print(f"public class {CLASS_NAME} {{")
    with cp:
        cp.print("MapTracker mt;")
        cp.print("RobotController rc;")
        cp.print("static int H, W;")
        cp.print(f"public {CLASS_NAME}(Robot robot) {{")
        with cp:
            cp.print("this.rc = robot.rc;")
            cp.print("this.mt = robot.mt;")
            cp.print("H = rc.getMapHeight();")
            cp.print("W = rc.getMapWidth();")
        cp.print("}")
        cp.print("")
        cp.print("public void moveTo(MapLocation target) throws GameActionException {")
        with cp:
            moveTo()
        cp.print("}")
    cp.print("}")


def moveTo():
    # We could probably get away with 1 mask, if we're willing to discard squares
    # in the opposite direction of the target. We have so much bytecode tho, that I don't know
    # that this is necessary. Also, using more masks may generalize better if we ever use full-map pathing.

    start = 1 << (4 * MASK_WIDTH + 4)
    loverflow = 0b111111110111111110111111110111111110111111110111111110111111110
    roverflow = 0b011111111011111111011111111011111111011111111011111111011111111
    cp.print(f"if (rc.getMovementCooldownTurns() >= 10) return;")
    cp.print(f"long loverflow = {hex(loverflow)}L;")
    cp.print(f"long roverflow = {hex(roverflow)}L;")
    cp.print(f"long water_mask0 = mt.water_mask0;")
    cp.print(f"long water_mask1 = mt.water_mask1;")
    cp.print("long passible0 = ~(mt.adjblocked | mt.wall_mask0);")
    cp.print("long passible1 = ~(mt.wall_mask1);")
    cp.print("long clear0 = ~(mt.adjblocked | mt.wall_mask0 | mt.water_mask0);")
    cp.print("long clear1 = ~(mt.wall_mask1 | mt.water_mask1);")
    cp.print(f"long temp = 0;")

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
        cp.print(f"double mult = Math.sqrt(((double) {RSQR}) / dist);")
        cp.print(f"int x = (int) Math.round(myloc.x + (target.x - myloc.x) * mult);")
        cp.print(f"int y = (int) Math.round(myloc.y + (target.y - myloc.y) * mult);")
        cp.print("MapLocation estimate = new MapLocation(x, y);")
        cp.print("MapLocation bestTarget = estimate;")
        cp.print(f"int bestDist = estimate.distanceSquaredTo(target);")

        directions = [
            "Direction.NORTHWEST",
            "Direction.NORTH",
            "Direction.NORTHEAST",
            "Direction.EAST",
            "Direction.WEST",
            "Direction.SOUTHWEST",
            "Direction.SOUTH",
            "Direction.SOUTHEAST",
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
    # If you're trying to hunt down a bug after game constants changed it may be this hardcoded 4 lol.
    cp.print(
        f"int i = ((target.y - (myloc.y - 4)) * {MASK_WIDTH}) + (target.x - (myloc.x - 4));"
    )
    cp.print(f"if (i >= {MASK_WIDTH * MASK_HEIGHT}) {{")
    with cp:
        cp.print(f"targetsqrs1 = 1L << (i - {MASK_WIDTH * MASK_HEIGHT});")
    cp.print("} else {")
    with cp:
        cp.print(f"targetsqrs0 = 1L << i;")
    cp.print("}")
    cp.print()

    # From the target square, propogate backwards until you reach a reachable square.
    # This is important, because we want to go towards the target, even if it's not reachable.
    cp.print(f"while ((targetsqrs0 & reach0) == 0 && (targetsqrs1 & reach1) == 0) {{")
    with cp:
        advance_reachable("targetsqrs", False)
    cp.print("}")
    cp.print()

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

    adjacency_mask = start
    for key, value in mp.items():
        adjacency_mask |= value

    # From the optimal reachable square(s) propogate backwards until you hit the start square.
    cp.print("int idx = 0;")
    cp.print(f"long[] back0 = \u007b{', '.join(['0'] * (COST_OF_WATER + 1))}\u007d;")
    cp.print(f"long[] back1 = \u007b{', '.join(['0'] * (COST_OF_WATER + 1))}\u007d;")
    cp.print("back0[0] = targetsqrs0 & reach0;")
    cp.print("back1[0] = targetsqrs1 & reach1;")
    cp.print(f"while ((back0[idx] & {hex(adjacency_mask)}L) == 0) {{")
    with cp:
        advance_reachable2("back", True)
    cp.print("}")
    cp.print()

    # You can also use a switch statement here, but it doesn't help you too much. Maybe 100 bytecode saved.
    # The only squares that should be active in best are those which
    # Are part of the optimal path. Hence, we can simply choose any of them.
    cp.print("Direction bestDir = null;")
    cp.print("int bestDist = -1;")



    cp.print("long best = back0[idx];")
    for key, value in mp.items():
        cp.print(
            f"""if ((best & {hex(value)}L) > 0) {{
            MapLocation loc = rc.adjacentLocation({key});
            int d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {{
                bestDir = {key};
                bestDist = d;
            }}
        }}
    
"""
        )

    cp.print("""
        if (bestDir != null) {
            MapLocation loc = rc.adjacentLocation(bestDir);
            if (rc.senseMapInfo(loc).isWater()) {
                if (rc.canFill(loc)) {
                    rc.fill(loc);
                }
            } else {
                rc.move(bestDir);
            }
        }

""")
    


def advance_reachable(mask_name, walls):
    shift = MASK_WIDTH * (MASK_HEIGHT - 1)
    cp.print(
        f"{mask_name}0 = ({mask_name}0 | (({mask_name}0 << 1) & loverflow) | (({mask_name}0 >> 1) & roverflow));"
    )
    cp.print(
        f"{mask_name}1 = ({mask_name}1 | (({mask_name}1 << 1) & loverflow) | (({mask_name}1 >> 1) & roverflow));"
    )
    cp.print(f"temp = {mask_name}0;")
    cp.print(
        f"{mask_name}0 = ({mask_name}0 | ({mask_name}0 << {MASK_WIDTH}) |"
        + f" ({mask_name}0 >> {MASK_WIDTH}) | ({mask_name}1 << {shift})){' & passible0' if walls else ''};"
    )
    cp.print(
        f"{mask_name}1 = ({mask_name}1 | ({mask_name}1 << {MASK_WIDTH}) |"
        + f" ({mask_name}1 >> {MASK_WIDTH}) | (temp >> {shift})){' & passible1' if walls else ''};"
    )


def advance_reachable2(mask_name, walls):
    shift = MASK_WIDTH * (MASK_HEIGHT - 1)
    cp.print(f"int nidx = (idx + 1) % {COST_OF_WATER + 1};")
    cp.print(f"long water0 = {mask_name}0[nidx];")
    cp.print(f"long water1 = {mask_name}1[nidx];")
    cp.print(
        f"{mask_name}0[nidx] = ({mask_name}0[idx] | (({mask_name}0[idx] << 1) & loverflow) | (({mask_name}0[idx] >> 1) & roverflow));"
    )
    cp.print(
        f"{mask_name}1[nidx] = ({mask_name}1[idx] | (({mask_name}1[idx] << 1) & loverflow) | (({mask_name}1[idx] >> 1) & roverflow));"
    )
    cp.print(f"temp = {mask_name}0[nidx];")
    cp.print(
        f"{mask_name}0[nidx] = ({mask_name}0[nidx] | ({mask_name}0[nidx] << {MASK_WIDTH}) |"
        + f" ({mask_name}0[nidx] >> {MASK_WIDTH}) | ({mask_name}1[nidx] << {shift}));"
    )
    cp.print(
        f"{mask_name}1[nidx] = ({mask_name}1[nidx] | ({mask_name}1[nidx] << {MASK_WIDTH}) |"
        + f" ({mask_name}1[nidx] >> {MASK_WIDTH}) | (temp >> {shift}));"
    )

    if walls:
        cp.print(
            f"{mask_name}0[(idx + {COST_OF_WATER}) % {COST_OF_WATER + 1}] = ({mask_name}0[nidx] & water_mask0);"
        )
        cp.print(
            f"{mask_name}0[nidx] = water0 | ({mask_name}0[nidx] & clear0);"
        )
        cp.print(
            f"{mask_name}1[(idx + {COST_OF_WATER}) % {COST_OF_WATER + 1}] = ({mask_name}1[nidx] & water_mask1);"
        )
        cp.print(
            f"{mask_name}1[nidx] = water1 | ({mask_name}1[nidx] & clear1);"
        )

    cp.print("idx = nidx;")


printPathing()
