package entities;

import interfaces.ICombatant;

/**
 * Iron Armor equipment — adds +10 to the combatant's defense.
 *
 * Decorators can be stacked:
 *   ICombatant tank = new IronArmorDecorator(new IronArmorDecorator(goblin));
 *   // goblin now has +20 defense
 */
public class IronArmorDecorator extends CombatantDecorator {

    private static final int DEFENSE_BONUS = 10;

    public IronArmorDecorator(ICombatant wrappedCombatant) {
        super(wrappedCombatant);
    }

    @Override
    public int getDefense() {
        return wrappedCombatant.getDefense() + DEFENSE_BONUS;
    }

    @Override
    public String getName() {
        return wrappedCombatant.getName() + " [Iron Armor]";
    }
}
