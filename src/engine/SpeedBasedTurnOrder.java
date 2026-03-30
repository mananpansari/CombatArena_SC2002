package engine;

import interfaces.ICombatant;
import interfaces.ITurnOrderStrategy;
import java.util.ArrayList;
import java.util.List;

public class SpeedBasedTurnOrder implements ITurnOrderStrategy {

    public List<ICombatant> determineTurnOrder(List<ICombatant> combatants) {
        List<ICombatant> alive = new ArrayList<>();
        for (ICombatant c : combatants) {
            if (c.isAlive()) {
                alive.add(c);
            }
        }

        alive.sort((a, b) -> {
            int speedDiff = b.getSpeed() - a.getSpeed();
            if (speedDiff != 0) {
                return speedDiff;
            }
            return a.getName().compareTo(b.getName());
        });

        return alive;
    }
}
