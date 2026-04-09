package entities;

import interfaces.ICombatant;
import strategies.ICombatStrategy;
import strategies.AggressiveStrategy;

public abstract class Enemy extends Combatant {

    // Strategy Pattern: each enemy holds a pluggable AI strategy
    private ICombatStrategy strategy;

    // Constructor — defaults to AggressiveStrategy
    protected Enemy(String name, int hp, int attack, int defense, int speed) {
        super(name, hp, attack, defense, speed);
        this.strategy = new AggressiveStrategy(); // sensible default
    }

    // Constructor with explicit strategy
    protected Enemy(String name, int hp, int attack, int defense, int speed, ICombatStrategy strategy) {
        super(name, hp, attack, defense, speed);
        this.strategy = strategy;
    }

    // ── Strategy accessors ────────────────────────

    public ICombatStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ICombatStrategy strategy) {
        this.strategy = strategy;
    }

    // ── Legacy turn method (kept for backward compatibility) ──

    public void takeTurn(ICombatant player) {
        int rawDamage = this.getAttack() - player.getDefense();
        int damage = Math.max(0, rawDamage);

        if (player.hasSmokeBombActive()) {
            damage = 0;
        }

        System.out.printf("  %s -> BasicAttack -> %s: %d damage%n",
                this.getName(), player.getName(), damage);

        player.takeDamage(damage);
    }

    public abstract String getEnemyType();

    public void printStats() {
        System.out.printf("  %-8s HP:%-4d ATK:%-4d DEF:%-4d SPD:%-4d%n",
                getEnemyType(),
                getMaxHp(),
                getAttack(),
                getDefense(),
                getSpeed());
    }
}