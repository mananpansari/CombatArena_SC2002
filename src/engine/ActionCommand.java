package engine;

import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;

public class ActionCommand implements ICommand {
    private BattleEngine engine;
    private IAction action;
    private ICombatant attacker;
    private List<ICombatant> targets;
    private BattleSnapshot backup;

    public ActionCommand(BattleEngine engine, IAction action, ICombatant attacker, List<ICombatant> targets) {
        this.engine = engine;
        this.action = action;
        this.attacker = attacker;
        this.targets = targets;
    }

    @Override
    public void execute() {
        this.backup = engine.createSnapshot();
        action.perform(attacker, targets);
    }

    @Override
    public void undo() {
        engine.restoreSnapshot(this.backup);
    }

    @Override
    public BattleSnapshot getBackupState() {
        return backup;
    }
}
