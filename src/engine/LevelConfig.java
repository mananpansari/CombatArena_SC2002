package engine;

import interfaces.ICombatant;
import java.util.ArrayList;
import java.util.List;

public class LevelConfig {

    private final String difficulty;
    private final List<ICombatant> initialEnemies;
    private final List<ICombatant> backupEnemies;
    private final List<IStackObserver> observers;

    // Original constructor (backward compatible)
    public LevelConfig(String difficulty, List<ICombatant> initialEnemies, List<ICombatant> backupEnemies) {
        this(difficulty, initialEnemies, backupEnemies, new ArrayList<>());
    }

    // New constructor with observer support
    public LevelConfig(String difficulty, List<ICombatant> initialEnemies,
                       List<ICombatant> backupEnemies, List<IStackObserver> observers) {
        this.difficulty = difficulty;
        this.initialEnemies = initialEnemies;
        this.backupEnemies = backupEnemies;
        this.observers = observers;
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

    public List<IStackObserver> getObservers() {
        return observers;
    }

    public boolean hasBackup() {
        return backupEnemies != null && !backupEnemies.isEmpty();
    }
}
