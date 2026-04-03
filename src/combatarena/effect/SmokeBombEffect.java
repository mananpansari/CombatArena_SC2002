package combatarena.effect;

import combatarena.entity.ICombatant;

/**
 * SmokeBombEffect — Person 3 owns this.
 * Stub so BattleEngine can check for damage nullification.
 * All incoming enemy damage = 0 for current + next turn.
 */
public class SmokeBombEffect implements StatusEffect {

    private int duration;

    public SmokeBombEffect(int duration) {
        this.duration = duration;
    }

    @Override public void onApply(ICombatant target) {
        System.out.println(target.getName() + " is shrouded in smoke! (Damage nullified for " + duration + " turns)");
    }

    @Override public void onTurnStart(ICombatant target) {
        duration--;
    }

    @Override public void onTurnEnd(ICombatant target) {
        // Nothing extra.
    }

    @Override public void onExpire(ICombatant target) {
        System.out.println("The smoke around " + target.getName() + " clears.");
    }

    @Override public boolean isExpired() { return duration <= 0; }
    @Override public int getDuration()    { return duration; }
    @Override public String getEffectName() { return "Smoke Bomb"; }
}
