package combatarena.action;

import combatarena.entity.ICombatant;
import combatarena.entity.Player;
import combatarena.item.Item;
import java.util.List;

/**
 * ItemAction — Person 2 owns this.
 * Delegates to the item's use() method.
 */
public class ItemAction implements Action {

    private final Item item;

    public ItemAction(Item item) {
        this.item = item;
    }

    @Override
    public void execute(ICombatant source, List<ICombatant> targets) {
        if (source instanceof Player) {
            Player player = (Player) source;
            item.use(player, targets);
            player.removeItem(item);
        }
    }

    @Override
    public String getActionName() {
        return "Use " + item.getItemName();
    }

    public Item getItem() {
        return item;
    }
}
