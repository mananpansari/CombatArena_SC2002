package combatarena.engine;

import combatarena.action.Action;
import combatarena.entity.ICombatant;
import combatarena.entity.Player;

import java.util.List;

/**
 * Observer / callback interface for the BattleEngine to communicate with the UI.
 *
 * The BattleEngine never calls System.out.println directly — instead it fires
 * events through this interface. Person 5's CLIDisplay implements this to print
 * formatted output to the console.
 *
 * This decoupling keeps BattleEngine testable and satisfies DIP: the engine
 * depends on this abstraction, not on a concrete CLI class.
 */
public interface BattleObserver {

    /** Called once at the start of the battle. */
    void onBattleStart(Player player, List<ICombatant> enemies);

    /** Called at the beginning of each round. */
    void onRoundStart(int roundNumber, List<ICombatant> allCombatants);

    /** Called at the end of each round. */
    void onRoundEnd(int roundNumber, List<ICombatant> allCombatants);

    /** Called when a combatant is stunned and their turn is skipped. */
    void onCombatantStunned(ICombatant combatant);

    /** Called when a combatant chooses an action (before execution). */
    void onActionChosen(ICombatant source, Action action, List<ICombatant> targets);

    /** Called after an action has been executed. */
    void onActionExecuted(ICombatant source, Action action, List<ICombatant> targets);

    /** Called when enemy damage is nullified by SmokeBombEffect. */
    void onDamageNullified(ICombatant attacker, ICombatant defender);

    /** Called when backup enemies spawn. */
    void onBackupSpawn(List<ICombatant> backupEnemies);

    /** Called when the battle ends. */
    void onBattleEnd(BattleResult result);
}
