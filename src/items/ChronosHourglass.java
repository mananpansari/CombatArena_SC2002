package items;

import interfaces.ICombatant;
import interfaces.IItem;
import java.util.List;

/**
 * Triggers BattleEngine.timeReversal() when used, restoring
 * all HP, cooldowns and enemy state to the previous round.
 * The actual reversal logic is handled by BattleEngine since
 * it requires direct engine coordination beyond IItem.use().
 * Implements IItem. Consumed from inventory on use.
 */
public class ChronosHourglass implements IItem {

    @Override
    public void use(ICombatant user, List<ICombatant> enemies) {
        System.out.printf("  %s -> Chronos Hourglass  (reversing time...)%n", user.getName());
        // Actual time reversal is handled by the BattleEngine detecting this item.
    }

    @Override
    public String getItemName() {
        return "Chronos Hourglass";
    }

    @Override
    public String getDescription() {
        return "Reverse time and undo the last action!";
    }
}
