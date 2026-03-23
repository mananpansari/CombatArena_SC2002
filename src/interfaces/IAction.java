package interfaces;

import java.util.List;

public interface IAction {

    void execute(ICombatant source, List<ICombatant> targets);

    String getActionName();

    boolean isAvailable(ICombatant source);    
}