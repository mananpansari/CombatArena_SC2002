package engine;

import entities.Combatant;
import entities.Enemy;
import entities.Player;
import interfaces.IAction;
import interfaces.ICombatant;
import interfaces.ITurnOrderStrategy;

import java.util.ArrayList;
import java.util.List;

public class BattleEngine {

    private final Player player;
    private final List<ICombatant> enemies;
    private final ITurnOrderStrategy turnOrderStrategy;
    private final List<ICombatant> backupEnemies;
    private boolean backupSpawned;
    private int roundNumber;

    public BattleEngine(Player player, LevelConfig level, ITurnOrderStrategy turnOrderStrategy) {
        this.player = player;
        this.enemies = new ArrayList<>(level.getInitialEnemies());
        this.backupEnemies = new ArrayList<>(level.getBackupEnemies());
        this.turnOrderStrategy = turnOrderStrategy;
        this.backupSpawned = false;
        this.roundNumber = 0;
    }

    // Run one full round. Returns true if the battle is still going, false if over.
    public boolean runRound(ActionProvider actionProvider) {
        roundNumber++;

        // Build the full combatant list for turn order
        List<ICombatant> allCombatants = getAllCombatants();
        prepareRound(allCombatants);

        List<ICombatant> turnOrder = turnOrderStrategy.determineTurnOrder(allCombatants);

        for (ICombatant current : turnOrder) {
            if (!current.isAlive()) {
                continue;
            }

            // Tick status effects at the start of this combatant's turn
            current.tickStatusEffects();

            // Stun check: if stunned, skip the turn
            if (current.isStunned()) {
                System.out.printf("  %s is STUNNED and cannot act.%n", current.getName());

                // If this is the player, still decrement cooldown
                if (current instanceof Player p) {
                    p.decrementCooldown();
                }
                finishTurn(current);
                continue;
            }

            if (current instanceof Player p) {
                // Decrement cooldown on the player's turn
                p.decrementCooldown();

                // Get the action from the UI/input layer
                List<ICombatant> livingEnemies = getLivingEnemies();
                IAction action = actionProvider.getPlayerAction(p, livingEnemies);
                List<ICombatant> targets = actionProvider.getTargets(p, livingEnemies, action);
                action.execute(p, targets);

            } else if (current instanceof Enemy e) {
                // Enemies always use BasicAttack on the player
                e.takeTurn(player);
            }

            finishTurn(current);

            // Check loss condition after every action
            if (!player.isAlive()) {
                return false;
            }

            // Check if initial wave is cleared for backup spawn
            checkBackupSpawn();

            // Check win condition after every action
            if (allEnemiesDead()) {
                return false;
            }
        }

        previewUpcomingRound(getAllCombatants());
        return true; // battle continues
    }

    private List<ICombatant> getAllCombatants() {
        List<ICombatant> allCombatants = new ArrayList<>();
        allCombatants.add(player);
        allCombatants.addAll(enemies);
        return allCombatants;
    }

    private void prepareRound(List<ICombatant> combatants) {
        for (ICombatant combatant : combatants) {
            if (combatant instanceof Combatant concreteCombatant) {
                concreteCombatant.resetActedThisRound();
                concreteCombatant.setCurrentRound(roundNumber);
                concreteCombatant.purgeExpiredStatusEffects();
            }
        }
    }

    private void finishTurn(ICombatant combatant) {
        if (combatant instanceof Combatant concreteCombatant) {
            concreteCombatant.markActedThisRound();
            concreteCombatant.purgeExpiredStatusEffects();
        }
    }

    private void previewUpcomingRound(List<ICombatant> combatants) {
        int upcomingRound = roundNumber + 1;
        for (ICombatant combatant : combatants) {
            if (combatant instanceof Combatant concreteCombatant) {
                concreteCombatant.setCurrentRound(upcomingRound);
                concreteCombatant.purgeExpiredStatusEffects();
            }
        }
    }

    private void checkBackupSpawn() {
        if (backupSpawned || backupEnemies.isEmpty()) {
            return;
        }

        boolean initialWaveCleared = true;
        for (ICombatant e : enemies) {
            if (e.isAlive()) {
                initialWaveCleared = false;
                break;
            }
        }

        if (initialWaveCleared) {
            System.out.println("\n  Backup enemies have arrived!");
            for (ICombatant backup : backupEnemies) {
                System.out.printf("    %s joins the battle!%n", backup.getName());
                if (backup instanceof Combatant concreteCombatant) {
                    concreteCombatant.resetActedThisRound();
                    concreteCombatant.setCurrentRound(roundNumber);
                }
            }
            enemies.addAll(backupEnemies);
            backupSpawned = true;
        }
    }

    public boolean allEnemiesDead() {
        for (ICombatant e : enemies) {
            if (e.isAlive()) {
                return false;
            }
        }
        return true;
    }

    public List<ICombatant> getLivingEnemies() {
        List<ICombatant> living = new ArrayList<>();
        for (ICombatant e : enemies) {
            if (e.isAlive()) {
                living.add(e);
            }
        }
        return living;
    }

    public boolean isPlayerAlive() {
        return player.isAlive();
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Player getPlayer() {
        return player;
    }

    public List<ICombatant> getEnemies() {
        return enemies;
    }

    // Interface that the CLI layer implements to provide player actions
    public interface ActionProvider {
        IAction getPlayerAction(Player player, List<ICombatant> livingEnemies);
        List<ICombatant> getTargets(Player player, List<ICombatant> livingEnemies, IAction action);
    }
}
