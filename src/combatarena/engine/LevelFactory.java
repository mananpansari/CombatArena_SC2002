package combatarena.engine;

import combatarena.entity.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Factory that creates LevelConfig instances for each difficulty.
 *
 * Difficulty breakdown (from the assignment spec):
 *   Easy   — 3 Goblins, no backup.
 *   Medium — 1 Goblin + 1 Wolf, backup 2 Wolves.
 *   Hard   — 2 Goblins, backup 1 Goblin + 2 Wolves.
 *
 * Person 5 may move this to the UI package or customise — for now it lives
 * alongside BattleEngine so the engine can be tested standalone.
 */
public class LevelFactory {

    /**
     * Create a LevelConfig for the given difficulty.
     * Resets entity counters so naming (Goblin 1, Wolf 1, …) starts fresh.
     */
    public static LevelConfig createLevel(LevelConfig.Difficulty difficulty) {
        // Reset instance counters so names start at 1 each level
        Goblin.resetCounter();
        Wolf.resetCounter();

        switch (difficulty) {
            case EASY:
                return new LevelConfig(
                    difficulty,
                    new ArrayList<>(Arrays.asList(new Goblin(), new Goblin(), new Goblin())),
                    Collections.emptyList()
                );

            case MEDIUM:
                return new LevelConfig(
                    difficulty,
                    new ArrayList<>(Arrays.asList(new Goblin(), new Wolf())),
                    new ArrayList<>(Arrays.asList(new Wolf(), new Wolf()))
                );

            case HARD:
                return new LevelConfig(
                    difficulty,
                    new ArrayList<>(Arrays.asList(new Goblin(), new Goblin())),
                    new ArrayList<>(Arrays.asList(new Goblin(), new Wolf(), new Wolf()))
                );

            default:
                throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        }
    }
}
