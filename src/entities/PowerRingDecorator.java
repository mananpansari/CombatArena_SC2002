package entities;

import interfaces.ICombatant;

/**
 * Power Ring equipment — adds +5 to the combatant's attack.
 *
 * Decorators can be stacked:
 *   ICombatant bruiser = new PowerRingDecorator(new IronArmorDecorator(wolf));
 *   // wolf now has +5 ATK and +10 DEF
 */
public class PowerRingDecorator extends CombatantDecorator {

    private static final int ATTACK_BONUS = 5;

    public PowerRingDecorator(ICombatant wrappedCombatant) {
        super(wrappedCombatant);
    }

    @Override
    public int getAttack() {
        return wrappedCombatant.getAttack() + ATTACK_BONUS;
    }

    @Override
    public String getName() {
        return wrappedCombatant.getName() + " [Power Ring]";
    }
}
