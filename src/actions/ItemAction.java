package actions;

import entities.Player;
import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;

public class ItemAction implements IAction {
    private final int itemIndex;

    public ItemAction(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    @Override
    public void execute(ICombatant source, List<ICombatant> targets) {
        if (!(source instanceof Player player) || !isAvailable(source)) {
            return;
        }

        player.useItem(itemIndex, targets);
    }

    @Override
    public String getActionName() {
        return "ItemAction";
    }

    @Override
    public boolean isAvailable(ICombatant source) {
        if (!(source instanceof Player player) || !source.isAlive()) {
            return false;
        }
        return itemIndex >= 0 && itemIndex < player.getInventory().size();
    }

    public interfaces.IItem getItem(Player player) {
        if (itemIndex >= 0 && itemIndex < player.getInventory().size()) {
            return player.getInventory().get(itemIndex);
        }
        return null;
    }
}
