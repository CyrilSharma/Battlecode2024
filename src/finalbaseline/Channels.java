package finalbaseline;

public class Channels {
    static int SYMMETRY = 0;
    static int EFLAGS = 1; // 3
    static int FIRST = EFLAGS + 3;
    static int ATTACK_TARGETS = FIRST + 1;
    static int N_ATTACK_TARGETS = 10;

    static int FLAG_CARRIERS = ATTACK_TARGETS + N_ATTACK_TARGETS + 1;
    static int FLAG_NUM = 3;
    static int CARRIER_DEFENDER = FLAG_CARRIERS + FLAG_NUM + 1;
    static int CARRIER_DEFENDER_NUM = 3;
    static int RUNAWAY_FLAGS = CARRIER_DEFENDER + CARRIER_DEFENDER_NUM + 1;
    static int RUNAWAY_FLAGS_ID = RUNAWAY_FLAGS + FLAG_NUM + 1;
    static int CARRIER_TARGET = RUNAWAY_FLAGS_ID + FLAG_NUM + 1;
    static int ID_CHANNEL = CARRIER_TARGET + FLAG_NUM + 1;
    static int STUNNED_UNITS = ID_CHANNEL + 1;
    static int STUNNED_UNITS_NUM = 20;
}
