package combatarena.entity;

/**
 * Concrete Wolf enemy — Person 1 owns this. Stub for integration testing.
 * Stats: HP:40, ATK:45, DEF:5, SPD:35
 */
public class Wolf extends Enemy {

    private static int instanceCount = 0;

    public Wolf() {
        super("Wolf " + (++instanceCount), 40, 45, 5, 35);
    }

    /**
     * Reset instance counter (useful between levels / games).
     */
    public static void resetCounter() {
        instanceCount = 0;
    }
}
