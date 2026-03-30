package actions;

import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;

public class BasicAttack implements IAction {

    public void execute(ICombatant source, List<ICombatant> targets) {
        if (targets == null || targets.isEmpty()) {
            return;
        }

        ICombatant target = targets.get(0);
        if (!target.isAlive()) {
            return;
        }

        int damage = Math.max(0, source.getAttack() - target.getDefense());
        target.takeDamage(damage);

        System.out.printf("  %s -> Basic Attack -> %s: %d damage%n",
                source.getName(), target.getName(), damage);
    }

    public String getActionName() {
        return "BasicAttack";
    }
}