package combatarena.entity;

/**
 * Concrete Goblin enemy — Person 1 owns this. Stub for integration testing.
 * Stats: HP:55, ATK:35, DEF:15, SPD:25
 */
public class Goblin extends Enemy {

    private static int instanceCount = 0;

    public Goblin() {
        super("Goblin " + (++instanceCount), 55, 35, 15, 25);
    }

    /**
     * Reset instance counter (useful between levels / games).
     */
    public static void resetCounter() {
        instanceCount = 0;
    }
}
