package engine;

import interfaces.ICombatant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import entities.Player;

public class BattleSnapshot {
    // Stores a snapshot of combatant IDs and their exact HP at a given time.
    private final Map<String, Integer> combatantHealthMap;
    private final int roundNumber;

    // Additional state info necessary for accurate restoration
    private final List<ICombatant> enemiesSnapshot;
    private final boolean backupSpawnedSnapshot;
    private final Map<String, Integer> playerCooldownMap;

    public BattleSnapshot(List<ICombatant> allCombatants, List<ICombatant> enemies, boolean backupSpawned, int roundNumber) {
        this.combatantHealthMap = new HashMap<>();
        this.playerCooldownMap = new HashMap<>();
        
        for (ICombatant c : allCombatants) {
            this.combatantHealthMap.put(c.getId(), c.getHp());
            if (c instanceof Player) {
                this.playerCooldownMap.put(c.getId(), ((Player) c).getSkillCooldown());
            }
        }
        
        this.roundNumber = roundNumber;
        this.enemiesSnapshot = new ArrayList<>(enemies); // Shallow copy of the list structure
        this.backupSpawnedSnapshot = backupSpawned;
    }

    // Getters for restoration (No setters allowed! Mementos must be immutable)
    public Map<String, Integer> getHealthMap() { return combatantHealthMap; }
    public Map<String, Integer> getPlayerCooldownMap() { return playerCooldownMap; }
    public int getRoundNumber() { return roundNumber; }
    public List<ICombatant> getEnemiesSnapshot() { return enemiesSnapshot; }
    public boolean isBackupSpawned() { return backupSpawnedSnapshot; }
}
