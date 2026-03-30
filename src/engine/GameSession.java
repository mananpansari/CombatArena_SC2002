package engine;

import entities.Player;
import interfaces.ITurnOrderStrategy;

public class GameSession {

    private final Player player;
    private final LevelConfig level;
    private final ITurnOrderStrategy turnOrderStrategy;
    private BattleEngine engine;

    public GameSession(Player player, LevelConfig level) {
        this.player = player;
        this.level = level;
        this.turnOrderStrategy = new SpeedBasedTurnOrder();
    }

    public GameSession(Player player, LevelConfig level, ITurnOrderStrategy turnOrderStrategy) {
        this.player = player;
        this.level = level;
        this.turnOrderStrategy = turnOrderStrategy;
    }

    public void start(BattleEngine.ActionProvider actionProvider) {
        engine = new BattleEngine(player, level, turnOrderStrategy);

        System.out.println("=== BATTLE START ===");
        System.out.printf("  Difficulty: %s%n", level.getDifficulty());
        System.out.printf("  Player: %s%n", player.getName());
        System.out.println();

        boolean battleContinues = true;
        while (battleContinues) {
            System.out.printf("%n=== ROUND %d ===%n", engine.getRoundNumber() + 1);
            printStatus();
            battleContinues = engine.runRound(actionProvider);
        }

        printResult();
    }

    private void printStatus() {
        System.out.println("  " + player);
        System.out.println("  ---");
        for (interfaces.ICombatant e : engine.getEnemies()) {
            if (e.isAlive()) {
                System.out.println("  " + e);
            }
        }
        System.out.println();
    }

    private void printResult() {
        System.out.println();
        if (player.isAlive() && engine.allEnemiesDead()) {
            System.out.println("=== VICTORY ===");
            System.out.printf("  %s wins with %d/%d HP remaining.%n",
                    player.getName(), player.getHp(), player.getMaxHp());
            System.out.printf("  Rounds survived: %d%n", engine.getRoundNumber());
        } else {
            System.out.println("=== DEFEAT ===");
            System.out.printf("  %s has fallen.%n", player.getName());
            System.out.printf("  Enemies remaining: %d%n", engine.getLivingEnemies().size());
            System.out.printf("  Rounds survived: %d%n", engine.getRoundNumber());
        }
    }

    public BattleEngine getEngine() {
        return engine;
    }

    public Player getPlayer() {
        return player;
    }

    public LevelConfig getLevel() {
        return level;
    }
}
