package combatarena.entity;

import combatarena.effect.StatusEffect;
import java.util.List;

/**
 * Shared abstraction for all combatants in the arena.
 * BattleEngine depends on this interface — never on concrete classes.
 * Person 1 will implement this on the Combatant abstract class.
 */
public interface ICombatant {

    String getName();

    int getHp();

    int getMaxHp();

    int getAttack();

    void setAttack(int attack);

    int getDefense();

    void setDefense(int defense);

    int getSpeed();

    void takeDamage(int damage);

    boolean isAlive();

    // ── Status effect hooks ──────────────────────────────────────────
    void applyStatusEffect(StatusEffect effect);

    void removeStatusEffect(StatusEffect effect);

    List<StatusEffect> getStatusEffects();

    /**
     * Called at the start of this combatant's turn — ticks every active effect.
     */
    void tickStatusEffectsOnTurnStart();

    /**
     * Called at the end of this combatant's turn — ticks every active effect.
     */
    void tickStatusEffectsOnTurnEnd();

    /**
     * Convenience: true if any active StunEffect is present.
     */
    boolean isStunned();
}
