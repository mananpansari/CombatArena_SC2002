package combatarena.engine;

import combatarena.entity.*;
import combatarena.item.*;

import java.util.Scanner;

/**
 * Top-level class that wires everything together and runs the full game.
 *
 * Flow:
 *   1. Player picks class (Warrior / Wizard).
 *   2. Player picks 2 items (Potion / Power Stone / Smoke Bomb).
 *   3. Player picks difficulty (Easy / Medium / Hard).
 *   4. BattleEngine runs the battle.
 *   5. End screen — option to replay, new game, or exit.
 *
 * Person 5 will expand the loading/completion screens; this is a functional
 * skeleton so the game is playable for integration testing.
 */
public class GameSession {

    private final Scanner scanner;

    public GameSession() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            // Reset counters
            Goblin.resetCounter();
            Wolf.resetCounter();

            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║        ⚔️  COMBAT ARENA  ⚔️          ║");
            System.out.println("╚══════════════════════════════════════╝\n");

            // ── Step 1: Choose class ────────────────────
            Player player = chooseClass();

            // ── Step 2: Choose items ────────────────────
            chooseItems(player);

            // ── Step 3: Choose difficulty ────────────────
            LevelConfig level = chooseDifficulty();

            // ── Step 4: Run the battle ──────────────────
            TurnOrderStrategy strategy = new SpeedBasedTurnOrder();
            DefaultCLIObserver cliObserver = new DefaultCLIObserver(scanner);

            BattleEngine engine = new BattleEngine(
                    player, level, strategy, cliObserver, cliObserver);
            engine.startBattle();

            // ── Step 5: End screen ──────────────────────
            running = endScreen();
        }

        System.out.println("Thanks for playing Combat Arena! 👋");
        scanner.close();
    }

    // ── Class selection ──────────────────────────────────────────────

    private Player chooseClass() {
        System.out.println("Choose your class:");
        System.out.println("  ┌───────────┬──────┬──────┬──────┬──────┐");
        System.out.println("  │  Class    │  HP  │  ATK │  DEF │  SPD │");
        System.out.println("  ├───────────┼──────┼──────┼──────┼──────┤");
        System.out.println("  │ 1.Warrior │ 260  │  40  │  20  │  30  │");
        System.out.println("  │ 2.Wizard  │ 200  │  50  │  10  │  20  │");
        System.out.println("  └───────────┴──────┴──────┴──────┴──────┘");
        System.out.print("  > ");

        int choice = readInt(1, 2);
        Player player;
        if (choice == 1) {
            player = new Warrior();
            System.out.println("  You chose the Warrior! ⚔️ \n");
        } else {
            player = new Wizard();
            System.out.println("  You chose the Wizard! 🧙\n");
        }
        return player;
    }

    // ── Item selection ───────────────────────────────────────────────

    private void chooseItems(Player player) {
        System.out.println("Choose 2 items (duplicates allowed):");
        System.out.println("  [1] Potion     — Heal 100 HP (capped at max)");
        System.out.println("  [2] Power Stone — Trigger your special skill effect (no cooldown)");
        System.out.println("  [3] Smoke Bomb — Nullify all incoming damage for 2 turns");

        for (int i = 1; i <= 2; i++) {
            System.out.print("  Item " + i + " > ");
            int choice = readInt(1, 3);
            Item item = createItem(choice);
            player.addItem(item);
            System.out.println("    Added: " + item.getItemName());
        }
        System.out.println();
    }

    private Item createItem(int choice) {
        switch (choice) {
            case 1: return new Potion();
            case 2: return new PowerStone();
            case 3: return new SmokeBomb();
            default: return new Potion();
        }
    }

    // ── Difficulty selection ─────────────────────────────────────────

    private LevelConfig chooseDifficulty() {
        System.out.println("Choose difficulty:");
        System.out.println("  [1] Easy   — 3 Goblins");
        System.out.println("  [2] Medium — 1 Goblin + 1 Wolf → backup: 2 Wolves");
        System.out.println("  [3] Hard   — 2 Goblins → backup: 1 Goblin + 2 Wolves");
        System.out.print("  > ");

        int choice = readInt(1, 3);
        LevelConfig.Difficulty diff;
        switch (choice) {
            case 1: diff = LevelConfig.Difficulty.EASY; break;
            case 2: diff = LevelConfig.Difficulty.MEDIUM; break;
            default: diff = LevelConfig.Difficulty.HARD; break;
        }

        LevelConfig level = LevelFactory.createLevel(diff);
        System.out.println("  Difficulty set to " + diff + ". " + level);
        System.out.println();
        return level;
    }

    // ── End screen ───────────────────────────────────────────────────

    private boolean endScreen() {
        System.out.println("What would you like to do?");
        System.out.println("  [1] Replay (same settings)");
        System.out.println("  [2] New Game");
        System.out.println("  [3] Exit");
        System.out.print("  > ");
        int choice = readInt(1, 3);
        switch (choice) {
            case 1:
            case 2:
                return true;
            default:
                return false;
        }
    }

    // ── Input helpers ────────────────────────────────────────────────

    private int readInt(int min, int max) {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) return val;
                System.out.print("  Invalid choice (" + min + "-" + max + "). Try again: ");
            } catch (NumberFormatException e) {
                System.out.print("  Please enter a number (" + min + "-" + max + "): ");
            }
        }
    }

    // ── Entry point ──────────────────────────────────────────────────

    public static void main(String[] args) {
        new GameSession().start();
    }
}
