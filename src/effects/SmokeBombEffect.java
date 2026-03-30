package effects;

import entities.Combatant;
import interfaces.ICombatant;
import interfaces.IStatusEffect;

public class SmokeBombEffect implements IStatusEffect {

    private Combatant owner;
    private int expiresAfterRound;

    @Override
    public void onApply(ICombatant target) {
        owner = requireCombatant(target);
        expiresAfterRound = owner.getCurrentRound() + 1;
    }

    @Override
    public void onTurnStart(ICombatant target) {
        // Duration is bound to round progression.
    }

    @Override
    public void onExpire(ICombatant target) {
        // Nothing to reverse for smoke bomb.
    }

    @Override
    public boolean isExpired() {
        return owner != null && owner.getCurrentRound() > expiresAfterRound;
    }

    @Override
    public String getEffectName() {
        return "SMOKE_BOMB";
    }

    private Combatant requireCombatant(ICombatant target) {
        if (target instanceof Combatant combatant) {
            return combatant;
        }
        throw new IllegalArgumentException("SmokeBombEffect requires a Combatant target.");
    }
}
