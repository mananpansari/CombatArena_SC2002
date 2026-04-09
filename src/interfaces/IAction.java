package interfaces;

import java.util.List;

public interface IAction {

    void perform(ICombatant attacker, List<ICombatant> targets);

    String getActionName();

    boolean isAvailable(ICombatant source);    
}