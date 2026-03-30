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

    public void execute(ICombatant source, List<ICombatant> targets) {
        if (!(source instanceof Player player) || !source.isAlive()) {
            return;
        }

        if (!player.hasItemsLeft()) {
            System.out.println("  No items left.");
            return;
        }

        player.useItem(itemIndex, targets);
    }

    public String getActionName() {
        return "ItemAction";
    }
}