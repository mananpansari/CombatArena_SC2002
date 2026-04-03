package combatarena.engine;

import combatarena.entity.ICombatant;
import combatarena.entity.Player;

import java.util.List;

/**
 * Immutable result of a completed battle.
 * Consumed by Person 5's end-screen display.
 */
public class BattleResult {

    private final boolean playerWon;
    private final int totalRounds;
    private final Player player;
    private final List<ICombatant> remainingEnemies;
    private final int livingEnemyCount;

    public BattleResult(boolean playerWon, int totalRounds, Player player,
                        List<ICombatant> remainingEnemies, int livingEnemyCount) {
        this.playerWon       = playerWon;
        this.totalRounds     = totalRounds;
        this.player          = player;
        this.remainingEnemies = remainingEnemies;
        this.livingEnemyCount = livingEnemyCount;
    }

    public boolean isPlayerWon()              { return playerWon; }
    public int     getTotalRounds()           { return totalRounds; }
    public Player  getPlayer()                { return player; }
    public List<ICombatant> getRemainingEnemies() { return remainingEnemies; }
    public int     getLivingEnemyCount()       { return livingEnemyCount; }

    @Override
    public String toString() {
        if (playerWon) {
            return "VICTORY! " + player.getName() + " won in " + totalRounds
                    + " round(s) with " + player.getHp() + "/" + player.getMaxHp() + " HP remaining.";
        } else {
            return "DEFEAT! " + player.getName() + " fell after " + totalRounds
                    + " round(s). " + livingEnemyCount + " enemy/enemies remaining.";
        }
    }
}
