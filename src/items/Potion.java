package items;

import interfaces.ICombatant;
import interfaces.IItem;

import java.util.List;

public class Potion implements IItem {

    private static final int healAmount = 100;

    @Override
    public void use(ICombatant user, List<ICombatant> enemies) {
        int previousHp = user.getHp();
        user.heal(healAmount);

        System.out.printf("  %s -> Item -> Potion used: HP: %d -> %d (+%d)%n",
                user.getName(),
                previousHp,
                user.getHp(),
                user.getHp() - previousHp);
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
