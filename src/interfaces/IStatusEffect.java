package interfaces;

public interface IStatusEffect {

    void onApply(ICombatant target);

    void onTurnStart(ICombatant target);

    void onExpire(ICombatant target);

    boolean isExpired();

    String getEffectName();

}