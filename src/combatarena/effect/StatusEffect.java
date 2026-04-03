package combatarena.effect;

import combatarena.entity.ICombatant;

/**
 * StatusEffect interface — Person 3 owns all implementations.
 * BattleEngine depends on this interface for ticking/checking effects (DIP).
 */
public interface StatusEffect {

    /** Called once when the effect is first applied. */
    void onApply(ICombatant target);

    /** Called at the start of each of the target's turns. */
    void onTurnStart(ICombatant target);

    /** Called at the end of each of the target's turns. */
    void onTurnEnd(ICombatant target);

    /** Called when the effect expires and is about to be removed. */
    void onExpire(ICombatant target);

    /** True if the effect's remaining duration has reached 0. */
    boolean isExpired();

    /** Remaining turns for this effect. */
    int getDuration();

    /** Human-readable name for CLI display. */
    String getEffectName();
}
