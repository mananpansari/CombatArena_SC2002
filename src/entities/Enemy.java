package entities;

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

    public abstract String getEnemyType();
}