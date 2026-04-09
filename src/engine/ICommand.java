package engine;

public interface ICommand {
    void execute();
    void undo();
    BattleSnapshot getBackupState();
}
