package entities;

import interfaces.ICombatant;
import interfaces.IStatusEffect;
import java.util.List;

/**
 * Decorator Pattern — abstract base for all equipment decorators.
 *
 * Wraps an existing ICombatant and delegates every method call to it.
 * Concrete decorators (IronArmorDecorator, PowerRingDecorator, etc.)
 * override only the methods they need to modify.
 *
 * LSP: A CombatantDecorator IS-A ICombatant and can be used anywhere
 * an ICombatant is expected — in the turn order, in ActionCommands,
 * in BattleSnapshot, etc.
 *
 * OCP: Adding a new equipment type (e.g. GodModeDecorator) requires
 * only a new subclass; no existing code needs to change.
 */
public abstract class CombatantDecorator implements ICombatant {

    protected final ICombatant wrappedCombatant;

    protected CombatantDecorator(ICombatant wrappedCombatant) {
        this.wrappedCombatant = wrappedCombatant;
    }

    /**
     * Provides access to the underlying combatant.
     * Useful for the engine to resolve type-specific logic
     * (e.g. Player cooldowns, Enemy strategies) through decoration layers.
     */
    public ICombatant getWrappedCombatant() {
        return wrappedCombatant;
    }

    // ── All ICombatant methods delegate to the wrapped combatant ──

    @Override
    public String getId() {
        return wrappedCombatant.getId();
    }

    @Override
    public String getName() {
        return wrappedCombatant.getName();
    }

    @Override
    public int getHp() {
        return wrappedCombatant.getHp();
    }

    @Override
    public int getMaxHp() {
        return wrappedCombatant.getMaxHp();
    }

    @Override
    public void takeDamage(int damage) {
        wrappedCombatant.takeDamage(damage);
    }

    @Override
    public void setHp(int hp) {
        wrappedCombatant.setHp(hp);
    }

    @Override
    public void heal(int amount) {
        wrappedCombatant.heal(amount);
    }

    @Override
    public boolean isAlive() {
        return wrappedCombatant.isAlive();
    }

    @Override
    public int getAttack() {
        return wrappedCombatant.getAttack();
    }

    @Override
    public int getDefense() {
        return wrappedCombatant.getDefense();
    }

    @Override
    public int getSpeed() {
        return wrappedCombatant.getSpeed();
    }

    @Override
    public void applyStatusEffect(IStatusEffect effect) {
        wrappedCombatant.applyStatusEffect(effect);
    }

    @Override
    public void tickStatusEffects() {
        wrappedCombatant.tickStatusEffects();
    }

    @Override
    public boolean isStunned() {
        return wrappedCombatant.isStunned();
    }

    @Override
    public boolean hasSmokeBombActive() {
        return wrappedCombatant.hasSmokeBombActive();
    }

    @Override
    public List<IStatusEffect> getActiveEffects() {
        return wrappedCombatant.getActiveEffects();
    }

    @Override
    public String toString() {
        return wrappedCombatant.toString();
    }
}
