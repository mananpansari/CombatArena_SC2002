package boundary;

import engine.CounterAttackObserver;
import engine.GameSession;
import engine.IStackObserver;
import engine.LevelConfig;
import entities.*;
import interfaces.ICombatant;
import interfaces.IItem;
import items.Potion;
import items.PowerStone;
import items.SmokeBomb;
import strategies.SupportStrategy;
import java.util.ArrayList;
import java.util.List;

public class LoadingScreen {
private final InputHandler input;

public LoadingScreen(InputHandler input) {
    this.input = input;
}

public GameSession setupGame() {
    System.out.println("========================================");
    System.out.println("  Welcome to the SC2002 Combat Arena.");
    System.out.println("========================================");
    Player player = createPlayer();
    LevelConfig level = selectDifficulty();
    return new GameSession(player, level);
}

private Player createPlayer() {
    System.out.println("\nSelect character:");
    System.out.println("1. Warrior - High DEF tank, Shield Bash stuns enemies");
    System.out.println("2. Wizard  - High ATK & SPD, Arcane Blast deals burst damage");
    int choice = input.getActionChoice(1, 2);
    List<IItem> items = chooseItems();
    if (choice == 1) return new Warrior(items);
    else return new Wizard(items);
}

private List<IItem> chooseItems() {
    List<IItem> selectedItems = new ArrayList<>();
    System.out.println("\nPick 2 items:");
    System.out.println("1. Potion - Restores 100 HP");
    System.out.println("2. Power Stone - Boosts next special skill");
    System.out.println("3. Smoke Bomb  - Reduces enemy accuracy");
    System.out.println("4. Chronos Hourglass - Rewind last action");
    for (int i = 1; i <= 2; i++) {
    System.out.printf("Pick Item %d (1-4): ", i);
    int choice = input.getActionChoice(1, 4);
    switch (choice) {
        case 1 -> selectedItems.add(new Potion());
        case 2 -> selectedItems.add(new PowerStone());
        case 3 -> selectedItems.add(new SmokeBomb());
        case 4 -> selectedItems.add(new items.ChronosHourglass());
    }
    }
    return selectedItems;
}

private LevelConfig selectDifficulty() {
    System.out.println("\nSelect Difficulty:");
    System.out.println("1. Easy  - 3 Goblins");
    System.out.println("2. Medium - Goblin + Wolves with equipment");
    System.out.println("3. Hard   - Full enemy team with AI strategies");
    int choice = input.getActionChoice(1, 3);
    switch (choice) {
    case 1:
        return new LevelConfig("Easy",
            List.of(new Goblin("A"), new Goblin("B"), new Goblin("C")),
            List.of());
    case 2:
        Wolf wolfB = new Wolf("B");
        ICombatant equippedWolf = new PowerRingDecorator(wolfB);
        return new LevelConfig("Medium",
            List.of(new Goblin("A"), new Wolf("A")),
            List.of(equippedWolf, new Wolf("C")));
    case 3:
        Goblin supportGoblin = new Goblin("B");
        supportGoblin.setStrategy(new SupportStrategy());
        Goblin gobC = new Goblin("C");
        ICombatant armoredGoblin = new IronArmorDecorator(gobC);
        Wolf wolfA = new Wolf("A");
        ICombatant eliteWolf = new PowerRingDecorator(new IronArmorDecorator(wolfA));
        List<IStackObserver> observers = new ArrayList<>();
        observers.add(new CounterAttackObserver(eliteWolf, 5));
        return new LevelConfig("Hard",
            List.of(new Goblin("A"), supportGoblin),
            List.of(armoredGoblin, eliteWolf),
            observers);
    default:
        return new LevelConfig("Easy",
            List.of(new Goblin("A"), new Goblin("B"), new Goblin("C")),
            List.of());
    }
}
}
