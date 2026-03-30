package actions;

import entities.Player;
import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;

public class SpecialSkillAction implements IAction {

    public void execute(ICombatant source, List<ICombatant> targets) {
        if (!(source instanceof Player player) || !source.isAlive()) {
            return;
        }

        if (!player.isSkillReady()) {
            System.out.printf("  %s is on cooldown for %d more turn(s).%n",
                    player.getSkillName(), player.getSkillCooldown());
            return;
        }

        player.useSpecialSkill(targets);
    }
    
    public String getActionName() {
        return "SpecialSkillAction";
    }
}