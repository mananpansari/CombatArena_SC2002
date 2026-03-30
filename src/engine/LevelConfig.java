package engine;

import interfaces.ICombatant;
import java.util.List;

public class LevelConfig {

    private final String difficulty;
    private final List<ICombatant> initialEnemies;
    private final List<ICombatant> backupEnemies;

    public LevelConfig(String difficulty, List<ICombatant> initialEnemies, List<ICombatant> backupEnemies) {
        this.difficulty = difficulty;
        this.initialEnemies = initialEnemies;
        this.backupEnemies = backupEnemies;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public List<ICombatant> getInitialEnemies() {
        return initialEnemies;
    }

    public List<ICombatant> getBackupEnemies() {
        return backupEnemies;
    }

    public boolean hasBackup() {
        return backupEnemies != null && !backupEnemies.isEmpty();
    }
}
