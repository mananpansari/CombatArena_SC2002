package combatarena.engine;

import combatarena.action.Action;
import combatarena.action.BasicAttack;
import combatarena.effect.SmokeBombEffect;
import combatarena.effect.StatusEffect;
import combatarena.entity.Enemy;
import combatarena.entity.ICombatant;
import combatarena.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BattleEngine — the central controller that orchestrates an entire battle.
 *
 * Design principles:
 *   • Depends ONLY on abstractions: {@link ICombatant}, {@link Action},
 *     {@link StatusEffect}, {@link TurnOrderStrategy} — never on Warrior,
 *     Goblin, etc. directly (DIP).
 *   • Turn ordering is delegated to a {@link TurnOrderStrategy} (Strategy
 *     pattern — OCP).
 *   • UI output is delegated to a {@link BattleObserver} callback so the
 *     engine is testable without a real console (Observer / port pattern).
 *
 * Lifecycle:
 *   1. Construct with a Player, LevelConfig, TurnOrderStrategy, and observer.
 *   2. Call {@link #startBattle()} — runs the round loop until win or loss.
 *   3. After completion, query {@link #getResult()}.
 */
public class BattleEngine {

    // ── Battle state ─────────────────────────────────────────────────

    private final Player player;
    private final LevelConfig levelConfig;
    private final TurnOrderStrategy turnOrderStrategy;
    private final BattleObserver observer;
    private final ActionProvider actionProvider;

    private final List<ICombatant> allCombatants;      // living combatants only
    private final List<ICombatant> initialWaveEnemies;  // track initial wave for backup trigger
    private boolean backupSpawned;
    private int roundNumber;
    private BattleResult result;

    // ── Constructor ──────────────────────────────────────────────────

    /**
     * @param player            the player character (Warrior or Wizard)
     * @param levelConfig       the level configuration (initial + backup waves)
     * @param turnOrderStrategy the strategy for determining turn order
     * @param observer          callback for UI events (display, messages)
     * @param actionProvider    callback that asks the UI for the player's action
     */
    public BattleEngine(Player player,
                        LevelConfig levelConfig,
                        TurnOrderStrategy turnOrderStrategy,
                        BattleObserver observer,
                        ActionProvider actionProvider) {
        this.player            = player;
        this.levelConfig       = levelConfig;
        this.turnOrderStrategy = turnOrderStrategy;
        this.observer          = observer;
        this.actionProvider    = actionProvider;

        this.allCombatants      = new ArrayList<>();
        this.initialWaveEnemies = new ArrayList<>();
        this.backupSpawned      = false;
        this.roundNumber        = 0;
        this.result             = null;

        // Populate the arena
        allCombatants.add(player);
        for (ICombatant enemy : levelConfig.getInitialWave()) {
            allCombatants.add(enemy);
            initialWaveEnemies.add(enemy);
        }
    }

    // ── Main loop ────────────────────────────────────────────────────

    /**
     * Run the battle until either the player or all enemies are dead.
     */
    public void startBattle() {
        observer.onBattleStart(player, getEnemies());

        while (result == null) {
            roundNumber++;
            executeRound();
        }

        observer.onBattleEnd(result);
    }

    /**
     * Execute a single round: determine turn order, then process each
     * combatant's turn sequentially.
     */
    private void executeRound() {
        observer.onRoundStart(roundNumber, allCombatants);

        // Turn order is re-evaluated every round (new enemies may have spawned)
        List<ICombatant> turnOrder = turnOrderStrategy.determineTurnOrder(
                getLivingCombatants());

        for (ICombatant combatant : turnOrder) {
            // Dead combatants don't act (may have died earlier this round)
            if (!combatant.isAlive()) continue;

            // ── Status effect tick (turn start) ──────────────────
            combatant.tickStatusEffectsOnTurnStart();

            // ── Stun check ───────────────────────────────────────
            if (combatant.isStunned()) {
                observer.onCombatantStunned(combatant);
                // Cooldown still decrements on a stunned player's turn
                if (combatant instanceof Player) {
                    ((Player) combatant).decrementCooldown();
                }
                combatant.tickStatusEffectsOnTurnEnd();
                continue;
            }

            // ── Choose and execute action ────────────────────────
            if (combatant instanceof Player) {
                processPlayerTurn((Player) combatant);
            } else if (combatant instanceof Enemy) {
                processEnemyTurn(combatant);
            }

            // ── Status effect tick (turn end) ────────────────────
            combatant.tickStatusEffectsOnTurnEnd();

            // ── Win / loss check after every action ──────────────
            if (checkBattleOver()) return;

            // ── Backup spawn check ───────────────────────────────
            checkAndSpawnBackup();
        }

        observer.onRoundEnd(roundNumber, allCombatants);
    }

    // ── Player turn ──────────────────────────────────────────────────

    private void processPlayerTurn(Player p) {
        // Decrement cooldown each time this player acts
        p.decrementCooldown();

        // Ask the UI/CLI for the player's action + targets
        List<ICombatant> enemies = getEnemies();
        ActionChoice choice = actionProvider.getPlayerAction(p, enemies);

        Action action   = choice.getAction();
        List<ICombatant> targets = choice.getTargets();

        observer.onActionChosen(p, action, targets);

        // Execute
        action.execute(p, targets);

        observer.onActionExecuted(p, action, targets);
    }

    // ── Enemy turn ───────────────────────────────────────────────────

    private void processEnemyTurn(ICombatant enemy) {
        // Enemies always use BasicAttack against the player
        BasicAttack attack = new BasicAttack();
        List<ICombatant> targets = Collections.singletonList(player);

        observer.onActionChosen(enemy, attack, targets);

        // ── SmokeBomb check: if player has SmokeBombEffect, damage = 0 ──
        if (playerHasSmokeBombEffect()) {
            observer.onDamageNullified(enemy, player);
            // Don't execute the attack (damage is nullified)
        } else {
            attack.execute(enemy, targets);
        }

        observer.onActionExecuted(enemy, attack, targets);
    }

    // ── SmokeBomb damage nullification ───────────────────────────────

    /**
     * Checks if the player currently has an active SmokeBombEffect.
     */
    private boolean playerHasSmokeBombEffect() {
        for (StatusEffect effect : player.getStatusEffects()) {
            if (effect instanceof SmokeBombEffect && !effect.isExpired()) {
                return true;
            }
        }
        return false;
    }

    // ── Backup spawn logic ───────────────────────────────────────────

    /**
     * After every kill, check: if all initial-wave enemies are dead AND
     * backup exists AND hasn't spawned yet → spawn backup immediately.
     */
    private void checkAndSpawnBackup() {
        if (backupSpawned) return;
        if (!levelConfig.hasBackupWave()) return;

        // Check if all initial wave enemies are dead
        boolean initialWaveCleared = initialWaveEnemies.stream()
                .noneMatch(ICombatant::isAlive);

        if (initialWaveCleared) {
            backupSpawned = true;
            List<ICombatant> backup = levelConfig.getBackupWave();
            allCombatants.addAll(backup);
            observer.onBackupSpawn(backup);
        }
    }

    // ── Win / loss checks ────────────────────────────────────────────

    /**
     * Check win/loss after every action resolution.
     *
     * @return true if the battle is over
     */
    private boolean checkBattleOver() {
        if (!player.isAlive()) {
            result = new BattleResult(false, roundNumber, player,
                    getEnemies(), getLivingEnemyCount());
            return true;
        }

        if (allEnemiesDead()) {
            result = new BattleResult(true, roundNumber, player,
                    Collections.emptyList(), 0);
            return true;
        }

        return false;
    }

    private boolean allEnemiesDead() {
        return getEnemies().stream().noneMatch(ICombatant::isAlive);
    }

    // ── Helpers ──────────────────────────────────────────────────────

    /**
     * Living combatants (both player and enemies).
     */
    private List<ICombatant> getLivingCombatants() {
        return allCombatants.stream()
                .filter(ICombatant::isAlive)
                .collect(Collectors.toList());
    }

    /**
     * All enemy combatants (alive or dead — needed for display).
     */
    private List<ICombatant> getEnemies() {
        return allCombatants.stream()
                .filter(c -> c instanceof Enemy)
                .collect(Collectors.toList());
    }

    /**
     * Count of living enemies.
     */
    private int getLivingEnemyCount() {
        return (int) allCombatants.stream()
                .filter(c -> c instanceof Enemy && c.isAlive())
                .count();
    }

    // ── Accessors ────────────────────────────────────────────────────

    public BattleResult getResult() {
        return result;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Player getPlayer() {
        return player;
    }

    public List<ICombatant> getAllCombatants() {
        return Collections.unmodifiableList(allCombatants);
    }
}
