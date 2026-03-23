package interfaces;

import java.util.List;

public interface ICombatant {

    String getName();

    int getHp();

    int getMaxHp();

    void takeDamage(int Damage);

    void heal(int amount);

    boolean isAlive();

    int getAttack();

    int getDefense();

    int getSpeed();

    void applyStatusEffect(IStatusEffect effect);

    void tickStatusEffects();

    boolean isStunned();

    boolean hasSmokeBombActive();

    List<IStatusEffect> getActiveEffects();

}