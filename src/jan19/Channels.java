package jan19;

public class Channels {
    static int SYMMETRY = 0;
    static int EFLAGS = 1; // 3
    static int FIRST = EFLAGS + 3;
    static int ATTACK_TARGETS = FIRST + 1;
    static int N_ATTACK_TARGETS = 10;

    static int FLAG_CARRIERS = ATTACK_TARGETS + N_ATTACK_TARGETS + 1;
    static int FLAG_NUM = 3;
    static int RUNAWAY_FLAGS = FLAG_CARRIERS + FLAG_NUM + 1;
}
