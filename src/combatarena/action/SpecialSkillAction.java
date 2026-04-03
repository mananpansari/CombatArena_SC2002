package combatarena.action;

import combatarena.entity.ICombatant;
import combatarena.entity.Player;
import java.util.List;

/**
 * SpecialSkillAction — Person 2 owns this.
 * Checks cooldown, executes the player's special skill, sets cooldown to 3.
 */
public class SpecialSkillAction implements Action {

    @Override
    public void execute(ICombatant source, List<ICombatant> targets) {
        if (!(source instanceof Player)) return;
        Player player = (Player) source;
        if (!player.isSkillReady()) {
            System.out.println(player.getName() + "'s special skill is on cooldown! ("
                    + player.getSpecialSkillCooldown() + " turns remaining)");
            return;
        }
        player.useSpecialSkill(targets);
    }

    @Override
    public String getActionName() {
        return "Special Skill";
    }
}
