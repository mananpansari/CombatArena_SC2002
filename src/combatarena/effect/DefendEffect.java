package combatarena.effect;

import combatarena.entity.ICombatant;

/**
 * DefendEffect — Person 3 owns this.
 * Stub: +10 DEF for 2 turns, reversed on expiry.
 */
public class DefendEffect implements StatusEffect {

    private int duration;
    private boolean applied = false;

    public DefendEffect(int duration) {
        this.duration = duration;
    }

    @Override
    public void onApply(ICombatant target) {
        target.setDefense(target.getDefense() + 10);
        applied = true;
        System.out.println(target.getName() + " raises their guard! (+10 DEF for " + duration + " turns)");
    }

    @Override public void onTurnStart(ICombatant target) {
        duration--;
    }

    @Override public void onTurnEnd(ICombatant target) { }

    @Override
    public void onExpire(ICombatant target) {
        if (applied) {
            target.setDefense(target.getDefense() - 10);
            System.out.println(target.getName() + "'s defensive stance fades. (-10 DEF)");
        }
    }

    @Override public boolean isExpired() { return duration <= 0; }
    @Override public int getDuration()    { return duration; }
    @Override public String getEffectName() { return "Defend"; }
}
