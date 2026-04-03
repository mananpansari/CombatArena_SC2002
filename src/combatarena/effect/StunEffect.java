package combatarena.effect;

import combatarena.entity.ICombatant;

/**
 * StunEffect — Person 3 owns this.
 * Stub so BattleEngine can instanceof-check for stun.
 * Target cannot act for {@code duration} turns.
 */
public class StunEffect implements StatusEffect {

    private int duration;

    public StunEffect(int duration) {
        this.duration = duration;
    }

    @Override public void onApply(ICombatant target) {
        System.out.println(target.getName() + " is stunned for " + duration + " turn(s)!");
    }

    @Override public void onTurnStart(ICombatant target) {
        // Stun message shown by BattleEngine when it skips the turn.
        duration--;
    }

    @Override public void onTurnEnd(ICombatant target) {
        // Nothing extra needed.
    }

    @Override public void onExpire(ICombatant target) {
        System.out.println(target.getName() + " is no longer stunned.");
    }

    @Override public boolean isExpired() { return duration <= 0; }
    @Override public int getDuration()    { return duration; }
    @Override public String getEffectName() { return "Stun"; }
}
