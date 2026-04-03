package combatarena.engine;

import combatarena.entity.ICombatant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts combatants in descending speed order.
 * Tie-breaker: alphabetical name order (consistent & deterministic).
 */
public class SpeedBasedTurnOrder implements TurnOrderStrategy {

    @Override
    public List<ICombatant> determineTurnOrder(List<ICombatant> combatants) {
        List<ICombatant> ordered = new ArrayList<>(combatants);
        ordered.sort(Comparator
                .comparingInt(ICombatant::getSpeed).reversed()     // highest speed first
                .thenComparing(ICombatant::getName));              // alphabetical tiebreaker
        return ordered;
    }
}
