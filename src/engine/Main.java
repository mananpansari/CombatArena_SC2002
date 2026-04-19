package engine;

import entities.*;
import interfaces.ICombatant;
import interfaces.IAction;
import interfaces.IItem;
import items.*;
import actions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();
        List<IItem> items = chooseItems();
        Player player = chooseClass(items);
        LevelConfig level = chooseDifficulty();
        System.out.println();
        new GameSession(player, level).start(new CLIActionProvider());
    }

    private static void printBanner() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║       COMBAT ARENA  v1.0         ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println();
    }

    private static Player chooseClass(List<IItem> items) {
        System.out.println("Choose your class:");
        System.out.println("  1. Warrior  (HP:260  ATK:40  DEF:20  SPD:30  Skill: Shield Bash)");
        System.out.println("  2. Wizard   (HP:200  ATK:50  DEF:10  SPD:20  Skill: Arcane Blast)");
        int choice = promptInt(1, 2);
        return choice == 1 ? new Warrior(items) : new Wizard(items);
    }

    private static List<IItem> chooseItems() {
        System.out.println("Choose 2 items (duplicates allowed):");
        System.out.println("  1. Potion             - Heal 100 HP");
        System.out.println("  2. Power Stone        - Free use of special skill (no cooldown effect)");
        System.out.println("  3. Smoke Bomb         - Enemy attacks deal 0 damage this turn + next");
        System.out.println("  4. Chronos Hourglass  - Reverse time by 1 round");
        List<IItem> items = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            System.out.printf("Item %d: ", i);
            items.add(makeItem(promptInt(1, 4)));
        }
        return items;
    }

    private static IItem makeItem(int choice) {
        return switch (choice) {
            case 1 -> new Potion();
            case 2 -> new PowerStone();
            case 3 -> new SmokeBomb();
            case 4 -> new ChronosHourglass();
            default -> new Potion();
        };
    }

    private static LevelConfig chooseDifficulty() {
        System.out.println();
        System.out.println("Choose difficulty:");
        System.out.println("  1. Easy   - 3 Goblins");
        System.out.println("  2. Medium - 1 Goblin + 1 Wolf  (backup: 2 Wolves)");
        System.out.println("  3. Hard   - 2 Goblins          (backup: 1 Goblin + 2 Wolves)");
        return switch (promptInt(1, 3)) {
            case 1 -> new LevelConfig("Easy",
                    List.of(new entities.Goblin("A"), new entities.Goblin("B"), new entities.Goblin("C")),
                    List.of());
            case 2 -> new LevelConfig("Medium",
                    List.of(new entities.Goblin("A"), new entities.Wolf("A")),
                    List.of(new entities.Wolf("B"), new entities.Wolf("C")));
            case 3 -> new LevelConfig("Hard",
                    List.of(new entities.Goblin("A"), new entities.Goblin("B")),
                    List.of(new entities.Goblin("C"), new entities.Wolf("A"), new entities.Wolf("B")));
            default -> new LevelConfig("Easy",
                    List.of(new entities.Goblin("A"), new entities.Goblin("B"), new entities.Goblin("C")),
                    List.of());
        };
    }

    private static int promptInt(int min, int max) {
        while (true) {
            System.out.print("> ");
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) {}
            System.out.printf("Please enter a number between %d and %d.%n", min, max);
        }
    }

    private static class CLIActionProvider implements BattleEngine.ActionProvider {

        @Override
        public IAction getPlayerAction(Player p, List<ICombatant> livingEnemies) {
            System.out.println("  Choose action:");
            System.out.println("    1. Basic Attack");
            System.out.println("    2. Defend");
            List<IItem> items = p.getInventory();
            if (!items.isEmpty()) {
                System.out.printf("    3. Use Item  (%s)%n", itemListStr(items));
            } else {
                System.out.println("    3. Use Item  (none left)");
            }
            int cooldown = p.getSkillCooldown();
            if (cooldown == 0) {
                System.out.println("    4. Special Skill  [READY]");
            } else {
                System.out.printf("    4. Special Skill  [COOLDOWN: %d]%n", cooldown);
            }

            int choice = promptInt(1, 4);
            return switch (choice) {
                case 1 -> new BasicAttack();
                case 2 -> new DefendAction();
                case 3 -> {
                    if (items.isEmpty()) {
                        System.out.println("  No items left - using Basic Attack instead.");
                        yield new BasicAttack();
                    }
                    yield new ItemAction(chooseItemIndex(items));
                }
                case 4 -> new SpecialSkillAction();
                default -> new BasicAttack();
            };
        }

        @Override
        public List<ICombatant> getTargets(Player p, List<ICombatant> livingEnemies, IAction action) {
            if (action instanceof DefendAction) return List.of();
            if (action instanceof ItemAction ia){
                if (ia.getItem(p) instanceof items.PowerStone){
                    return livingEnemies;
                }
                return List.of();
            }
            if (action instanceof SpecialSkillAction && p instanceof entities.Wizard) {
                return livingEnemies;
            }
            if (livingEnemies.size() == 1) return livingEnemies;
            System.out.println("  Choose target:");
            for (int i = 0; i < livingEnemies.size(); i++) {
                System.out.printf("    %d. %s%n", i + 1, livingEnemies.get(i));
            }
            int choice = promptInt(1, livingEnemies.size());
            return List.of(livingEnemies.get(choice - 1));
        }

        private int chooseItemIndex(List<IItem> items) {
            if (items.size() == 1) return 0;
            System.out.println("  Choose item:");
            for (int i = 0; i < items.size(); i++) {
                System.out.printf("    %d. %s%n", i + 1, items.get(i).getItemName());
            }
            return promptInt(1, items.size()) - 1;
        }

        private String itemListStr(List<IItem> items) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(items.get(i).getItemName());
            }
            return sb.toString();
        }
    }
}