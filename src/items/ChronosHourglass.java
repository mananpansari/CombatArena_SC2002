package items;

import interfaces.ICombatant;
import interfaces.IItem;
import java.util.List;

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
