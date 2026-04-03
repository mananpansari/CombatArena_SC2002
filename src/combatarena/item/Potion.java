package combatarena.item;

import combatarena.entity.ICombatant;
import combatarena.entity.Player;
import java.util.List;

/**
 * Potion — Person 3 owns this. Stub.
 * Heals 100 HP, capped at maxHP.
 */
public class Potion implements Item {

    @Override
    public void use(Player player, List<ICombatant> enemies) {
        int before = player.getHp();
        player.heal(100);
        int healed = player.getHp() - before;
        System.out.println(player.getName() + " drinks a Potion and recovers " + healed + " HP! ("
                + player.getHp() + "/" + player.getMaxHp() + ")");
    }

    @Override
    public String getItemName() {
        return "Potion";
    }
}
