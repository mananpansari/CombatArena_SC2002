package items;

import effects.SmokeBombEffect;
import interfaces.ICombatant;
import interfaces.IItem;

import java.util.List;

public class SmokeBomb implements IItem {

    @Override
    public void use(ICombatant user, List<ICombatant> enemies) {
        user.applyStatusEffect(new SmokeBombEffect());
        System.out.printf("  %s -> Smoke Bomb  (nullify damage for 2 rounds)%n",
                user.getName());
    }

    @Override
    public String getItemName() {
        return "Smoke Bomb";
    }

    @Override
    public String getDescription() {
        return "Enemy basic attacks deal 0 damage this turn and next round.";
    }
}
