package interfaces;

import java.util.List;

public interface ITurnOrderStrategy {

    List<ICombatant> determineTurnOrder(List<ICombatant> combatants);

}