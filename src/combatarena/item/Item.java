package combatarena.item;

import combatarena.entity.ICombatant;
import combatarena.entity.Player;
import java.util.List;

/**
 * Item interface — Person 3 owns all implementations.
 * BattleEngine and ItemAction depend on this contract.
 */
public interface Item {

    /**
     * Use this item.
     *
     * @param player  the player using the item
     * @param enemies the list of enemy combatants (for offensive items)
     */
    void use(Player player, List<ICombatant> enemies);

    /**
     * Human-readable name for CLI display.
     */
    String getItemName();
}
