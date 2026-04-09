package items;

import interfaces.ICombatant;
import interfaces.IItem;
import java.util.List;

public class ChronosHourglass implements IItem {

    @Override
    public void use(ICombatant user, List<ICombatant> enemies) {
        System.out.println("  The sands of time begin to swirl around " + user.getName() + "... Time is reversing!");
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
