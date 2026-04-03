package combatarena.action;

import combatarena.effect.DefendEffect;
import combatarena.entity.ICombatant;
import java.util.List;

/**
 * DefendAction — applies +10 DEF as a DefendEffect lasting 2 turns
 * (current + next round).
 */
public class DefendAction implements Action {

    @Override
    public void execute(ICombatant source, List<ICombatant> targets) {
        source.applyStatusEffect(new DefendEffect(2));
    }

    @Override
    public String getActionName() {
        return "Defend";
    }
}
