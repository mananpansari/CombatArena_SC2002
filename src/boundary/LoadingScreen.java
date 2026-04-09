package boundary;

import engine.GameSession;
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

  public LoadingScreen(InputHandler input){
    this.input=input;
  }

  public GameSession setupGame() {
    System.out.println("Welcome to the SC2002 Combat Arena.");

    Player player = createPlayer();
    LevelConfig level = selectDifficulty();

    return new GameSession(player, level);
  }

  private Player createPlayer() {
    Warrior.printClassInfo();
    Wizard.printClassInfo();
    System.out.println("\nSelect character: \n1. Warrior - Tank\n2. Wizard - High ATK & SPD");
    int choice = input.getActionChoice(1,2);

    List<IItem> items = chooseItems();

    if (choice == 1){
      return new Warrior(items);
    }
    else{
      return new Wizard(items);
    }
  }

  private List<IItem> chooseItems() {
    List<IItem> selectedItems = new ArrayList<>();
    System.out.println("Pick 2 items: 1. Potion, 2. Power Stone, 3. Smoke Bomb, 4. Chronos Hourglass");

    for (int i = 1; i <= 2; i++){
      System.out.println("Pick Item " + i + " (1. Potion, 2. Power Stone, 3. Smoke Bomb, 4. Chronos Hourglass):");
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
    System.out.println("\nSelect Difficulty: 1. Easy, 2. Medium, 3. Hard");
    int choice = input.getActionChoice(1, 3);

    switch (choice) {
      case 1:
        // Easy: 3 Aggressive Goblins (default strategy)
        return new LevelConfig("Easy",
            List.of(new Goblin("A"), new Goblin("B"), new Goblin("C")),
            List.of());

      case 2:
        // Medium: Mixed strategies
        // - Goblin A: Aggressive (default)
        // - Wolf A: Defensive (default)
        // - Backup: Wolf B with Power Ring (+5 ATK) via Decorator Pattern
        Wolf wolfB = new Wolf("B");
        ICombatant equippedWolf = new PowerRingDecorator(wolfB);
        return new LevelConfig("Medium",
            List.of(new Goblin("A"), new Wolf("A")),
            List.of(equippedWolf, new Wolf("C")));

      case 3:
        // Hard: Full pattern showcase
        // - Goblin A: Aggressive (default)
        // - Goblin B: Support strategy — heals wounded allies
        Goblin supportGoblin = new Goblin("B");
        supportGoblin.setStrategy(new SupportStrategy());

        // - Backup wave: an armored goblin (Iron Armor +10 DEF)
        //   and a wolf with both Power Ring (+5 ATK) AND Iron Armor (+10 DEF)
        Goblin gobC = new Goblin("C");
        ICombatant armoredGoblin = new IronArmorDecorator(gobC);

        Wolf wolfA = new Wolf("A");
        ICombatant eliteWolf = new PowerRingDecorator(new IronArmorDecorator(wolfA));

        return new LevelConfig("Hard",
            List.of(new Goblin("A"), supportGoblin),
            List.of(armoredGoblin, eliteWolf));

      default:
        return new LevelConfig("Easy",
            List.of(new Goblin("A"), new Goblin("B"), new Goblin("C")),
            List.of());
    }
  }
}
