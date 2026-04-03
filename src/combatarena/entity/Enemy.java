package combatarena.entity;

/**
 * Abstract enemy — Person 1 owns the full implementation.
 * Stub here so BattleEngine can distinguish enemies from players.
 */
public abstract class Enemy extends Combatant {

    public Enemy(String name, int hp, int attack, int defense, int speed) {
        super(name, hp, attack, defense, speed);
    }
}
