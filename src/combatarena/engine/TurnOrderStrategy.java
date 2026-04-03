package combatarena.engine;

import combatarena.entity.ICombatant;
import java.util.List;

/**
 * Strategy interface for determining combatant turn order each round.
 * Follows the Strategy pattern (OCP, DIP) — BattleEngine depends on this
 * abstraction, not on a concrete sorting algorithm.
 */
public interface TurnOrderStrategy {

    /**
     * Return a new list of combatants sorted in the order they should act
     * this round. The input list must not be mutated.
     *
     * @param combatants all living combatants for the current round
     * @return a fresh list in turn order
     */
    List<ICombatant> determineTurnOrder(List<ICombatant> combatants);
}
