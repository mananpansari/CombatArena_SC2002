package items;

import interfaces.ICombatant;
import interfaces.IItem;

import java.util.List;

/**
 * Restores 100 HP to the user, capped at max HP.
 * New HP = min(current HP + 100, max HP).
 * Implements IItem. Consumed from inventory on use.
 */
public class Potion implements IItem {

    private static final int healAmount = 100;

    @Override
    public void use(ICombatant user, List<ICombatant> enemies) {
        int previousHp = user.getHp();
        user.heal(healAmount);

        System.out.printf("  %s -> Potion  (+%d HP, now %d/%d)%n",
                user.getName(),
                user.getHp() - previousHp,
                user.getHp(),
                user.getMaxHp());
    }

    @Override
    public String getItemName() {
        return "Potion";
    }

    @Override
    public String getDescription() {
        return "Heal 100 HP, capped at max HP.";
    }
}
