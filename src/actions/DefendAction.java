package actions;

import effects.DefendEffect;
import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;

public class DefendAction implements IAction {

    @Override
    public void execute(ICombatant source, List<ICombatant> targets) {
        if (!isAvailable(source)) {
            return;
        }

        source.applyStatusEffect(new DefendEffect());

        System.out.printf("  %s -> Defend: +10 DEF for this round and next round%n", source.getName());
    }

    @Override
    public String getActionName() {
        return "DefendAction";
    }

    @Override
    public boolean isAvailable(ICombatant source) {
        return source != null && source.isAlive();
    }
}
