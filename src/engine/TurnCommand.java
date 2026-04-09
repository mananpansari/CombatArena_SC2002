package engine;

import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;

public class TurnCommand implements ICommand {
    private BattleEngine engine;
    private IAction chosenAction;
    private ICombatant attacker;
    private List<ICombatant> targets;
    private BattleSnapshot backupState; // The Memento

    public TurnCommand(BattleEngine engine, IAction action, ICombatant attacker, List<ICombatant> targets) {
        this.engine = engine;
        this.chosenAction = action;
        this.attacker = attacker;
        this.targets = targets;
    }

    @Override
    public void execute() {
        // 1. Take a snapshot of the engine BEFORE executing the move
        this.backupState = engine.createSnapshot(); 
        
        // 2. Execute the actual attack/item/skill
        chosenAction.execute(attacker, targets);
    }

    @Override
    public void undo() {
        // 1. Restore the engine to the backup state
        engine.restoreSnapshot(this.backupState);
    }

    @Override
    public BattleSnapshot getBackupState() {
        return backupState;
    }
}
