package combatarena.engine;

import combatarena.entity.ICombatant;
import combatarena.entity.Player;

import java.util.List;

/**
 * Callback interface that the CLI / UI implements to gather the player's
 * action choice each turn.
 *
 * BattleEngine calls this during the player's turn and receives an
 * {@link ActionChoice} back. This keeps input handling decoupled from the
 * engine (DIP).
 */
public interface ActionProvider {

    /**
     * Prompt the player for their action.
     *
     * @param player  the current player
     * @param enemies all living enemy combatants (for target selection)
     * @return the player's chosen action and targets
     */
    ActionChoice getPlayerAction(Player player, List<ICombatant> enemies);
}
