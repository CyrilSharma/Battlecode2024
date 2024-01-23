import math

MASK_WIDTH = 9
MASK_HEIGHT = 7
CLASS_NAME = "OptimalPathing"
RSQR = 20
COST_OF_WATER = 2  # 2 actual turns, one for filling and moving. +1 accounts for crumbs and base cooldown cost


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
    cp.print("package sprint2;")
    cp.print("import battlecode.common.*;")
    cp.print(f"public class {CLASS_NAME} {{")
    with cp:
        cp.print("MapTracker mt;")
        cp.print("NeighborTracker nt;")
        cp.print("RobotController rc;")
        cp.print("static int H, W;")
        cp.print(f"public {CLASS_NAME}(Robot robot) {{")
        with cp:
            cp.print("this.rc = robot.rc;")
            cp.print("this.mt = robot.mt;")
            cp.print("this.nt = robot.nt;")
            cp.print("H = rc.getMapHeight();")
            cp.print("W = rc.getMapWidth();")
        cp.print("}")
        cp.print("")
        cp.print("public void moveTo(MapLocation target, boolean avoid_enemies, boolean water_passable) throws GameActionException {")
        with cp: moveTo()
        cp.print("}")
    cp.print("}")

def moveTo():
    # We could probably get away with 1 mask, if we're willing to discard squares
    # in the opposite direction of the target. We have so much bytecode tho, that I don't know
    # that this is necessary. Also, using more masks may generalize better if we ever use full-map pathing.
    pathing_setup()
    NITERS = 10
    nmasks = 2
    start = 1 << (4 * MASK_WIDTH + 4)
    cp.print("long enemyaction0 = 0;")
    cp.print("long enemyaction1 = 0;")
    cp.print("if (avoid_enemies) {")
    with cp:
        cp.print("enemyaction0 = nt.enemy_mask0;")
        cp.print("enemyaction1 = nt.enemy_mask1;")
        for i in range(2):
            advance_reachable("enemyaction", False)
    cp.print("}")
    cp.print()

    cp.print(f"long slow0 = 0;")
    cp.print(f"long slow1 = 0;")
    cp.print(f"long slow_mask0 = 0;")
    cp.print(f"long slow_mask1 = 0;")
    cp.print("if (avoid_enemies) {")
    with cp:
        cp.print(f"slow_mask0 = (enemyaction0);")
        cp.print(f"slow_mask1 = (enemyaction1);")
    cp.print("} else {")
    with cp:
        cp.print(f"slow_mask0 = (mt.water_mask0);")
        cp.print(f"slow_mask1 = (mt.water_mask1);")
    cp.print("}")

    cp.print("long clear0 = 0;")
    cp.print("long clear1 = 0;")
    cp.print("if (water_passable) {")
    with cp:
        cp.print("clear0 = ~(mt.adjblocked | mt.wall_mask0 | enemyaction0) & mask0;")
        cp.print("clear1 = ~(mt.wall_mask1 | enemyaction1) & mask1;")
    cp.print("} else {")
    with cp:
        cp.print("clear0 = ~(mt.adjblocked | mt.wall_mask0 | mt.water_mask0 | enemyaction0) & mask0;")
        cp.print("clear1 = ~(mt.wall_mask1 | mt.water_mask1 | enemyaction1) & mask1;")
    cp.print("}")

    cp.print()
    cp.print(f"long[] reach0_round = new long[{NITERS}];")
    cp.print(f"long[] reach1_round = new long[{NITERS}];")

    for i in range(nmasks):
        for j in range(COST_OF_WATER + 1):
            if i == 0 and j == 0: 
                cp.print(f"long reach0_0 = {start}L;")
            else:
                cp.print(f"long reach{i}_{j} = 0;")

    for i in range(NITERS):
        advance_reachable_water("reach", i)
        if (i == 0):
            cp.print(f"reach0_round[{i}] = reach0_{(i + 1) % (COST_OF_WATER + 1)};")
            cp.print(f"reach1_round[{i}] = reach1_{(i + 1) % (COST_OF_WATER + 1)};")
        else:
            cp.print(f"reach0_round[{i}] = reach0_{(i + 1) % (COST_OF_WATER + 1)} & ~reach0_{(i) % (COST_OF_WATER + 1)};")
            cp.print(f"reach1_round[{i}] = reach1_{(i + 1) % (COST_OF_WATER + 1)} & ~reach1_{(i) % (COST_OF_WATER + 1)};")
    cp.print()

    pathing_heuristic()

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
    cp.print("long best = 0;")
    for i in range(nmasks):
        for j in range(COST_OF_WATER + 1):
            cp.print(f"long back{i}_{j} = 0;")    

    cp.print("back0_0 = targetsqrs0;")
    cp.print("back1_0 = targetsqrs1;")
    cp.print("outer: {")
    with cp:
        for i in range(NITERS):
            nidx = (i + 1) % (COST_OF_WATER + 1)
            advance_reachable_water("back", i)
            cp.print(f"if ((back0_{nidx} & {hex(adjacency_mask)}L) != 0) {{")
            with cp:
                cp.print(f"best = back0_{nidx};")
                cp.print("break outer;")
            cp.print(f"}}")
    cp.print("}")
    cp.print()

    pathing_tiebreaker()
    
def pathing_setup():
    loverflow = 0b111111110111111110111111110111111110111111110111111110111111110
    roverflow = 0b011111111011111111011111111011111111011111111011111111011111111
    cp.print(f"if (rc.getMovementCooldownTurns() >= 10) return;")
    cp.print(f"long mask0 = 0x7FFFFFFFFFFFFFFFL;")
    cp.print(f"long mask1 = 0x3FFFFL;")
    cp.print(f"long loverflow = {hex(loverflow)}L;")
    cp.print(f"long roverflow = {hex(roverflow)}L;")
    cp.print(f"long temp = 0;")

def pathing_heuristic():
    cp.print(f"MapLocation loc00 = rc.getLocation().translate(-4, -4);")
    for x in range(1, 9):
        cp.print(f"MapLocation loc{0}{x} = loc{0}{x - 1}.add(Direction.EAST);")
    for y in range(1, 9):
        for x in range(9):
            cp.print(f"MapLocation loc{y}{x} = loc{y - 1}{x}.add(Direction.NORTH);")
    cp.print()
    cp.print(f"int d = 0;")
    cp.print("long temp0 = 0;")
    cp.print("long temp1 = 0;")
    cp.print("long temp2 = 0;")
    cp.print(f"long bestdist = {1 << 30};")
    cp.print("long targetsqrs0 = 0;")
    cp.print("long targetsqrs1 = 0;")
    cp.print("for (int i = 0; i < 10; i++) {")
    with cp:
        cp.print(f"temp0 = reach0_round[i];")
        cp.print(f"temp1 = reach1_round[i];")
        cp.print(f"outer: for (;; temp0 &= (temp0 - 1), temp1 &= (temp1 - 1)) {{")
        with cp:
            cp.print("temp2 = (temp0 & -temp0);")
            cp.print("switch ((int)(temp2 & 0xFFFFFFFF)) {")
            with cp:
                for j in range(32):
                    cp.print(f"case {hex(1 << j)}: ")
                    with cp:
                        y = j // 9; x = j % 9
                        cp.print(f"d = ((int) Math.sqrt(loc{y}{x}.distanceSquaredTo(target))) + i;")
                        cp.print(f"if (d < bestdist) {{")
                        with cp:
                            cp.print("bestdist = d;")
                            cp.print(f"targetsqrs0 = {hex(1 << j)}L;")
                        cp.print(f"}}")
                        cp.print(f"continue;")
                cp.print("default: break;")
            cp.print("}")
            cp.print("switch ((int)(temp2 >>> 32)) {")
            with cp:
                offset = 32
                for j in range(32, 63):
                    cp.print(f"case {hex(1 << (j - offset))}: ")
                    with cp:
                        y = j // 9; x = j % 9
                        cp.print(f"d = ((int) Math.sqrt(loc{y}{x}.distanceSquaredTo(target))) + i;")
                        cp.print(f"if (d < bestdist) {{")
                        with cp:
                            cp.print("bestdist = d;")
                            cp.print(f"targetsqrs0 = {hex(1 << j)}L;")
                        cp.print(f"}}")
                        cp.print(f"continue;")
                cp.print("default: break;")
            cp.print("}")
            cp.print("switch ((int) (temp1 & -temp1)) {")
            with cp:
                offset = 63
                for j in range(63, 81):
                    cp.print(f"case {hex(1 << (j - offset))}: ")
                    with cp:
                        y = j // 9; x = j % 9
                        cp.print(f"d = ((int) Math.sqrt(loc{y}{x}.distanceSquaredTo(target))) + i;")
                        cp.print(f"if (d < bestdist) {{")
                        with cp:
                            cp.print("bestdist = d;")
                            cp.print(f"targetsqrs1 = {hex(1 << (j - offset))}L;")
                        cp.print(f"}}")
                        cp.print(f"continue;")
                cp.print("default: break outer;")
            cp.print("}")
        cp.print(f"}}")
    cp.print("}")
    cp.print()

def pathing_tiebreaker():
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
    cp.print("Direction bestDir = null;")
    cp.print(f"int bestDist = {1 << 30};")
    for key, value in mp.items():
        cp.print(
            f"""if ((best & {hex(value)}L) > 0) {{
            MapLocation loc = rc.adjacentLocation({key});
            d = target.distanceSquaredTo(loc);
            if (bestDir == null || (d < bestDist)) {{
                bestDir = {key};
                bestDist = d;
            }}
        }}""")

    cp.print("""
        if (bestDir != null) {
            MapLocation loc = rc.adjacentLocation(bestDir);
            if (rc.senseMapInfo(loc).isWater()) {
                if (rc.canFill(loc)) {
                    rc.fill(loc);
                }
            }
            if (rc.canMove(bestDir)) {
                rc.move(bestDir);
            }
        }""")


# --------- Reachability Masks ------------------
# Computes reachable squares after moving one step
def advance_reachable(mask_name, walls):
    shift = MASK_WIDTH * (MASK_HEIGHT - 1)
    cp.print(
        f"{mask_name}0 = ({mask_name}0 | (({mask_name}0 << 1) & loverflow) | (({mask_name}0 >>> 1) & roverflow));"
    )
    cp.print(
        f"{mask_name}1 = ({mask_name}1 | (({mask_name}1 << 1) & loverflow) | (({mask_name}1 >>> 1) & roverflow));"
    )
    cp.print(f"temp = {mask_name}0;")
    cp.print(
        f"{mask_name}0 = ({mask_name}0 | ({mask_name}0 << {MASK_WIDTH}) |"
        + f" ({mask_name}0 >>> {MASK_WIDTH}) | ({mask_name}1 << {shift})){' & passible0' if walls else ' & mask0'};"
    )
    cp.print(
        f"{mask_name}1 = ({mask_name}1 | ({mask_name}1 << {MASK_WIDTH}) |"
        + f" ({mask_name}1 >>> {MASK_WIDTH}) | (temp >>> {shift})){' & passible1' if walls else ' & mask1'};"
    )

# Advance Reachable2 but unrolled
def advance_reachable_water(mask_name, idx):
    shift = MASK_WIDTH * (MASK_HEIGHT - 1)
    idx = (idx) % (COST_OF_WATER + 1)
    nidx = (idx + 1) % (COST_OF_WATER + 1)
    cp.print(f"slow0 = {mask_name}0_{nidx};")
    cp.print(f"slow1 = {mask_name}1_{nidx};")
    cp.print(
        f"{mask_name}0_{nidx} = ({mask_name}0_{idx} | (({mask_name}0_{idx} << 1) & loverflow) | (({mask_name}0_{idx} >>> 1) & roverflow));"
    )
    cp.print(
        f"{mask_name}1_{nidx} = ({mask_name}1_{idx} | (({mask_name}1_{idx} << 1) & loverflow) | (({mask_name}1_{idx} >>> 1) & roverflow));"
    )
    cp.print(f"temp = {mask_name}0_{nidx};")
    cp.print(
        f"{mask_name}0_{nidx} = ({mask_name}0_{nidx} | ({mask_name}0_{nidx} << {MASK_WIDTH}) |"
        + f" ({mask_name}0_{nidx} >>> {MASK_WIDTH}) | ({mask_name}1_{nidx} << {shift}));"
    )
    cp.print(
        f"{mask_name}1_{nidx} = ({mask_name}1_{nidx} | ({mask_name}1_{nidx} << {MASK_WIDTH}) |"
        + f" ({mask_name}1_{nidx} >>> {MASK_WIDTH}) | (temp >>> {shift}));"
    )
    cp.print(
        f"{mask_name}0_{(idx + COST_OF_WATER) % (COST_OF_WATER + 1)} = ({mask_name}0_{nidx} & slow_mask0);"
    )
    cp.print(
        f"{mask_name}0_{nidx} = slow0 | ({mask_name}0_{nidx} & clear0);"
    )
    cp.print(
        f"{mask_name}1_{(idx + COST_OF_WATER) % (COST_OF_WATER + 1)} = ({mask_name}1_{nidx} & slow_mask1);"
    )
    cp.print(
        f"{mask_name}1_{nidx} = slow1 | ({mask_name}1_{nidx} & clear1);"
    )


printPathing()