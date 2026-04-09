package actions;

import entities.Player;
import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;

public class SpecialSkillAction implements IAction {

    @Override
    public void perform(ICombatant source, List<ICombatant> targets) {
        if (!(source instanceof Player player) || !source.isAlive()) {
            return;
        }

        if (!isAvailable(source)) {
            System.out.printf("  %s is on cooldown for %d more turn(s).%n",
                    player.getSkillName(), player.getSkillCooldown());
            return;
        }

        player.useSpecialSkill(targets);
    }
    
    @Override
    public String getActionName() {
        return "SpecialSkillAction";
    }

    @Override
    public boolean isAvailable(ICombatant source) {
        return source instanceof Player player && source.isAlive() && player.isSkillReady();
    }
}
