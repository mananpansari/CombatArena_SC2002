package interfaces;

import java.util.List;

public interface IItem {

    void use(ICombatant user, List<ICombatant> enemies);

    String getItemName();

    String getDescription();
    
}