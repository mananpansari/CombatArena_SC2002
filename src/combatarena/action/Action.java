package combatarena.action;

import combatarena.entity.ICombatant;
import java.util.List;

/**
 * Single method interface for all actions a combatant can perform.
 * Person 2 owns all implementations; this interface is the contract
 * BattleEngine depends on (DIP).
 */
public interface Action {

    /**
     * Execute this action.
     *
     * @param source  the combatant performing the action
     * @param targets the target(s) of the action
     */
    void execute(ICombatant source, List<ICombatant> targets);

    /**
     * Human-readable name for CLI display.
     */
    String getActionName();
}
