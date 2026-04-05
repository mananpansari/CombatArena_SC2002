package effects;

import entities.Combatant;
import interfaces.ICombatant;
import interfaces.IStatusEffect;

public class DefendEffect implements IStatusEffect {

    private static final int defenseBonus = 10;

    private Combatant owner;
    private int expiresAfterRound;

    @Override
    public void onApply(ICombatant target) {
        owner = requireCombatant(target);
        expiresAfterRound = owner.getCurrentRound() + 1;
        owner.setDefense(owner.getDefense() + defenseBonus);
    }

    @Override
    public void onTurnStart(ICombatant target) {
        // Duration is bound to round progression.
    }

    @Override
    public void onExpire(ICombatant target) {
        if (owner != null) {
            owner.setDefense(owner.getDefense() - defenseBonus);
        }
    }

    @Override
    public boolean isExpired() {
        return owner != null && owner.getCurrentRound() > expiresAfterRound;
    }

    @Override
    public String getEffectName() {
        return "defend";
    }

    private Combatant requireCombatant(ICombatant target) {
        if (target instanceof Combatant combatant) {
            return combatant;
        }
        throw new IllegalArgumentException("DefendEffect requires a Combatant target.");
    }
}
