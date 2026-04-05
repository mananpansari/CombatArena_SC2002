package entities;

import interfaces.ICombatant;
import interfaces.IStatusEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class Combatant implements ICombatant {

    // Core identity
    private final String name;

    // Health
    private int hp;
    private final int maxHp;

    // Combat stats
    protected int attack;
    protected int defense; 
    protected final int speed;

    // Status effects
    private final List<IStatusEffect> activeEffects;
    private boolean hasActedThisRound;
    private int currentRound;

    // Constructor 
    protected Combatant(String name, int hp, int attack, int defense, int speed){
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.activeEffects = new ArrayList<>();
        this.hasActedThisRound = false;
        this.currentRound = 0;
    }

    // ICombatant: Identity

    @Override
    public String getName(){
        return name;
    }

    // ICombatant: Health
    @Override
    public int getHp(){
        return hp;
    }

    @Override
    public int getMaxHp(){
        return maxHp;
    }

    @Override
    public void takeDamage(int damage){
        hp = Math.max(0, hp - damage);
    }

    @Override
    public void heal(int amount){
        if (amount < 0){
            throw new IllegalArgumentException("Heal amount cannot be negative: " + amount);
        }
        hp = Math.min(maxHp, hp + amount);
    }

    @Override
    public boolean isAlive(){
        return hp > 0;
    }

    // ICombatant: Combat Stats

    @Override
    public int getAttack(){
        return attack;
    }
    @Override
    public int getDefense(){
        return defense;
    }

    @Override
    public int getSpeed(){
        return speed;
    }

    public void setAttack(int attack){
        this.attack = attack;
    }

    public void setDefense(int defense){
        this.defense = defense;
    }

    // ICombatant: Status Effects

    @Override
    public void applyStatusEffect(IStatusEffect effect){
        effect.onApply(this);
        activeEffects.add(effect);
    }

    @Override
    public void tickStatusEffects(){
        for (IStatusEffect effect : activeEffects) {
            effect.onTurnStart(this);
        }
    }

    public void purgeExpiredStatusEffects() {
        Iterator<IStatusEffect> it = activeEffects.iterator();
        while (it.hasNext()) {
            IStatusEffect effect = it.next();
            if (effect.isExpired()) {
                effect.onExpire(this);
                it.remove();
            }
        }
    }

    @Override
    public boolean isStunned(){
        for(IStatusEffect effect : activeEffects) {
            if ("stunned".equals(effect.getEffectName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasSmokeBombActive(){
        for(IStatusEffect effect:  activeEffects){
            if("smokeBomb".equals(effect.getEffectName()) && !effect.isExpired()){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<IStatusEffect> getActiveEffects(){
        return Collections.unmodifiableList(activeEffects);
    }

    public boolean hasActedThisRound() {
        return hasActedThisRound;
    }

    public void markActedThisRound() {
        hasActedThisRound = true;
    }

    public void resetActedThisRound() {
        hasActedThisRound = false;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    // Display Helper

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-14s HP: %3d/%-3d ATK:%-3d DEF:%-3d SPD:%-3d",
        name, hp, maxHp, attack, defense, speed));

        if(!activeEffects.isEmpty()){
            sb.append(" ");
            for (IStatusEffect e : activeEffects){
                if (!e.isExpired()) {
                    sb.append("[").append(e.getEffectName()).append("] ");
                }
            }
        }
        return sb.toString().trim();
    }
}
