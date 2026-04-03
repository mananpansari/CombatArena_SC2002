package combatarena.entity;

import combatarena.effect.StatusEffect;
import combatarena.effect.StunEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract base for every character in the arena.
 * Person 1 owns this class — the version here is a working stub so Person 4's
 * BattleEngine can compile and run independently.
 */
public abstract class Combatant implements ICombatant {

    protected String name;
    protected int hp;
    protected int maxHp;
    protected int attack;
    protected int defense;
    protected int speed;
    protected List<StatusEffect> statusEffects;

    public Combatant(String name, int hp, int attack, int defense, int speed) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.statusEffects = new ArrayList<>();
    }

    // ── Getters / Setters ────────────────────────────────────────────

    @Override public String getName()       { return name; }
    @Override public int    getHp()         { return hp; }
    @Override public int    getMaxHp()      { return maxHp; }
    @Override public int    getAttack()     { return attack; }
    @Override public void   setAttack(int a){ this.attack = a; }
    @Override public int    getDefense()    { return defense; }
    @Override public void   setDefense(int d){ this.defense = d; }
    @Override public int    getSpeed()      { return speed; }

    // ── Combat ───────────────────────────────────────────────────────

    @Override
    public void takeDamage(int damage) {
        if (damage < 0) damage = 0;
        this.hp = Math.max(0, this.hp - damage);
    }

    /**
     * Heal this combatant by the given amount, capped at maxHp.
     */
    public void heal(int amount) {
        if (amount < 0) return;
        this.hp = Math.min(this.maxHp, this.hp + amount);
    }

    @Override
    public boolean isAlive() {
        return hp > 0;
    }

    // ── Status Effects ───────────────────────────────────────────────

    @Override
    public void applyStatusEffect(StatusEffect effect) {
        statusEffects.add(effect);
        effect.onApply(this);
    }

    @Override
    public void removeStatusEffect(StatusEffect effect) {
        statusEffects.remove(effect);
    }

    @Override
    public List<StatusEffect> getStatusEffects() {
        return statusEffects;
    }

    @Override
    public void tickStatusEffectsOnTurnStart() {
        Iterator<StatusEffect> it = statusEffects.iterator();
        while (it.hasNext()) {
            StatusEffect effect = it.next();
            effect.onTurnStart(this);
            if (effect.isExpired()) {
                effect.onExpire(this);
                it.remove();
            }
        }
    }

    @Override
    public void tickStatusEffectsOnTurnEnd() {
        Iterator<StatusEffect> it = statusEffects.iterator();
        while (it.hasNext()) {
            StatusEffect effect = it.next();
            effect.onTurnEnd(this);
            if (effect.isExpired()) {
                effect.onExpire(this);
                it.remove();
            }
        }
    }

    @Override
    public boolean isStunned() {
        for (StatusEffect effect : statusEffects) {
            if (effect instanceof StunEffect && !effect.isExpired()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return name + " (HP:" + hp + "/" + maxHp + ")";
    }
}
