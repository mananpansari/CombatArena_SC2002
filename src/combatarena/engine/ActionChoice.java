package combatarena.engine;

import combatarena.action.Action;
import combatarena.entity.ICombatant;

import java.util.List;

/**
 * Encapsulates a player's action choice: the action and its target(s).
 * Returned by {@link ActionProvider#getPlayerAction}.
 */
public class ActionChoice {

    private final Action action;
    private final List<ICombatant> targets;

    public ActionChoice(Action action, List<ICombatant> targets) {
        this.action  = action;
        this.targets = targets;
    }

    public Action getAction() {
        return action;
    }

    public List<ICombatant> getTargets() {
        return targets;
    }
}
