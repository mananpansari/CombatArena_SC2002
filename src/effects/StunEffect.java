package effects;

import entities.Combatant;
import interfaces.ICombatant;
import interfaces.IStatusEffect;

public class StunEffect implements IStatusEffect {

    private int remainingSkips;

    @Override
    public void onApply(ICombatant target) {
        Combatant combatant = requireCombatant(target);
        remainingSkips = combatant.hasActedThisRound() ? 1 : 2;
    }

    @Override
    public void onTurnStart(ICombatant target) {
        if (remainingSkips > 0) {
            remainingSkips--;
        }
    }

    @Override
    public void onExpire(ICombatant target) {
        // Nothing to revert for stun.
    }

    @Override
    public boolean isExpired() {
        return remainingSkips <= 0;
    }

    @Override
    public String getEffectName() {
        return "STUNNED";
    }

    private Combatant requireCombatant(ICombatant target) {
        if (target instanceof Combatant combatant) {
            return combatant;
        }
        throw new IllegalArgumentException("StunEffect requires a Combatant target.");
    }
}
