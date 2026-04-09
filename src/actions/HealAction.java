package actions;

import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;

/**
 * HealAction allows a combatant to heal a target ally.
 * Used primarily by the SupportStrategy for enemy healers.
 */
public class HealAction implements IAction {

    private static final int HEAL_AMOUNT = 20;

    @Override
    public void perform(ICombatant source, List<ICombatant> targets) {
        if (!isAvailable(source) || targets == null || targets.isEmpty()) {
            return;
        }

        ICombatant target = targets.get(0);
        if (!target.isAlive()) {
            return;
        }

        int prevHp = target.getHp();
        target.heal(HEAL_AMOUNT);
        int healed = target.getHp() - prevHp;

        System.out.printf("  %s -> Heal -> %s: +%d HP (HP: %d -> %d)%n",
                source.getName(), target.getName(), healed, prevHp, target.getHp());
    }

    @Override
    public String getActionName() {
        return "HealAction";
    }

    @Override
    public boolean isAvailable(ICombatant source) {
        return source != null && source.isAlive();
    }
}
