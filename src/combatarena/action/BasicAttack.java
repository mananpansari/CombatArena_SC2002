package combatarena.action;

import combatarena.entity.ICombatant;
import java.util.List;

/**
 * BasicAttack — Person 2 owns this.
 * Stub so BattleEngine can use it for enemy AI (enemies always basic attack).
 * Damage formula: max(0, attackerATK − targetDEF)
 */
public class BasicAttack implements Action {

    @Override
    public void execute(ICombatant source, List<ICombatant> targets) {
        if (targets.isEmpty()) return;
        ICombatant target = targets.get(0);
        int damage = calculateDamage(source, target);
        target.takeDamage(damage);
    }

    /**
     * Reusable damage formula — other actions should call this static method
     * instead of copy-pasting the formula.
     */
    public static int calculateDamage(ICombatant attacker, ICombatant defender) {
        return Math.max(0, attacker.getAttack() - defender.getDefense());
    }

    @Override
    public String getActionName() {
        return "Basic Attack";
    }
}
