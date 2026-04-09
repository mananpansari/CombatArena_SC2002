package actions;

import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;

/**
 * Deals fixed "thorns" damage to the attacker — used by CounterAttackObserver
 * as a reaction to incoming attacks.
 */
public class CounterAttackAction implements IAction {

    private final int thornsDamage;

    public CounterAttackAction(int thornsDamage) {
        this.thornsDamage = thornsDamage;
    }

    @Override
    public void perform(ICombatant source, List<ICombatant> targets) {
        if (!isAvailable(source) || targets == null || targets.isEmpty()) {
            return;
        }

        ICombatant target = targets.get(0);
        if (!target.isAlive()) {
            return;
        }

        target.takeDamage(thornsDamage);
        System.out.printf("  ⚡ %s counter-attacks %s for %d thorns damage!%n",
                source.getName(), target.getName(), thornsDamage);
    }

    @Override
    public String getActionName() {
        return "CounterAttack";
    }

    @Override
    public boolean isAvailable(ICombatant source) {
        return source != null && source.isAlive();
    }
}
