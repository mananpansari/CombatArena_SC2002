package combatarena.item;

import combatarena.entity.ICombatant;
import combatarena.entity.Player;
import java.util.List;

/**
 * PowerStone — Person 3 owns this. Stub.
 * Triggers player's skill effect WITHOUT affecting cooldown.
 */
public class PowerStone implements Item {

    @Override
    public void use(Player player, List<ICombatant> enemies) {
        System.out.println(player.getName() + " uses the Power Stone!");
        player.executeSkillEffect(enemies);
    }

    @Override
    public String getItemName() {
        return "Power Stone";
    }
}
