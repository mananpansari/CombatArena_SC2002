package items;

import entities.Player;
import interfaces.ICombatant;
import interfaces.IItem;

import java.util.List;

/**
 * Triggers the player's special skill effect once without
 * starting or modifying the cooldown timer.
 * Calls executeSkillEffect() directly rather than useSpecialSkill()
 * to avoid incorrectly resetting the cooldown.
 * Implements IItem. Consumed from inventory on use.
 */
public class PowerStone implements IItem {

    @Override
    public void use(ICombatant user, List<ICombatant> enemies) {
        if (!(user instanceof Player player)) {
            throw new IllegalArgumentException("Power Stone can only be used by a player.");
        }

        System.out.printf("  %s -> Power Stone  (triggers %s, no cooldown)%n",
                player.getName(),
                player.getSkillName());
        player.executeSkillEffect(enemies);
    }

    @Override
    public String getItemName() {
        return "Power Stone";
    }

    @Override
    public String getDescription() {
        return "Trigger the special skill once without changing cooldown.";
    }
}
