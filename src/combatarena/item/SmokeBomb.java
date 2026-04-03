package combatarena.item;

import combatarena.effect.SmokeBombEffect;
import combatarena.entity.ICombatant;
import combatarena.entity.Player;
import java.util.List;

/**
 * SmokeBomb — Person 3 owns this. Stub.
 * Applies SmokeBombEffect (2 turns) to the player.
 */
public class SmokeBomb implements Item {

    @Override
    public void use(Player player, List<ICombatant> enemies) {
        System.out.println(player.getName() + " throws a Smoke Bomb!");
        player.applyStatusEffect(new SmokeBombEffect(2));
    }

    @Override
    public String getItemName() {
        return "Smoke Bomb";
    }
}
