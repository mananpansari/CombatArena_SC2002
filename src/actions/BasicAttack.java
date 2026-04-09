package actions;

import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;

public class BasicAttack implements IAction {

    @Override
    public void perform(ICombatant source, List<ICombatant> targets) {
        if (!isAvailable(source) || targets == null || targets.isEmpty()) {
            return;
        }

        ICombatant target = targets.get(0);
        if (!target.isAlive()) {
            return;
        }

        int damage = Math.max(0, source.getAttack() - target.getDefense());
        
        if (target.hasSmokeBombActive()) {
            damage = 0;
        }

        target.takeDamage(damage);

        System.out.printf("  %s -> Basic Attack -> %s: %d damage%n",
                source.getName(), target.getName(), damage);
    }

    @Override
    public String getActionName() {
        return "BasicAttack";
    }

    @Override
    public boolean isAvailable(ICombatant source) {
        return source != null && source.isAlive();
    }
}
