package engine;

import entities.Enemy;
import interfaces.ICombatant;

public class EnemyTurnCommand implements ICommand {
    private BattleEngine engine;
    private Enemy attacker;
    private ICombatant target;
    private BattleSnapshot backupState; // The Memento

    public EnemyTurnCommand(BattleEngine engine, Enemy attacker, ICombatant target) {
        this.engine = engine;
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public void execute() {
        // 1. Take a snapshot of the engine BEFORE executing the move
        this.backupState = engine.createSnapshot(); 
        
        // 2. Execute the actual attack/item/skill
        attacker.takeTurn(target);
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
