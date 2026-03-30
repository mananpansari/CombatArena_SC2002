package actions;

import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;

public class DefendAction implements IAction {

    public void execute(ICombatant source, List<ICombatant> targets) {
        if (!source.isAlive()) {
            return;
        }

        //actual defend effect to be added later

        System.out.printf("  %s -> Defend%n", source.getName());
    }

    public String getActionName() {
        return "DefendAction";
    }
}