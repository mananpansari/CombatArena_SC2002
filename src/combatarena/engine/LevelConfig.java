package combatarena.engine;

import combatarena.entity.ICombatant;
import java.util.List;

/**
 * Encapsulates the enemy composition for a single level / difficulty.
 *
 * Each level has:
 *   - an initial wave of enemies
 *   - an optional backup wave that spawns after the initial wave is fully defeated
 *
 * Person 5 (LevelFactory) will create concrete LevelConfig instances.
 * BattleEngine reads these to know which enemies to spawn.
 */
public class LevelConfig {

    public enum Difficulty { EASY, MEDIUM, HARD }

    private final Difficulty difficulty;
    private final List<ICombatant> initialWave;
    private final List<ICombatant> backupWave;   // may be empty

    public LevelConfig(Difficulty difficulty,
                       List<ICombatant> initialWave,
                       List<ICombatant> backupWave) {
        this.difficulty  = difficulty;
        this.initialWave = initialWave;
        this.backupWave  = backupWave;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<ICombatant> getInitialWave() {
        return initialWave;
    }

    public List<ICombatant> getBackupWave() {
        return backupWave;
    }

    public boolean hasBackupWave() {
        return backupWave != null && !backupWave.isEmpty();
    }

    @Override
    public String toString() {
        return "Level [" + difficulty + "] — initial: " + initialWave.size()
                + " enemies, backup: " + (hasBackupWave() ? backupWave.size() : 0) + " enemies";
    }
}
