package boundary;

import engine.GameSession;
import engine.LevelConfig;
import entities.Goblin;
import entities.Player;
import entities.Warrior;
import entities.Wizard;
import entities.Wolf;
import interfaces.IItem;
import items.Potion;
import items.PowerStone;
import items.SmokeBomb;
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
    System.out.println("Pick 2 items: 1. Potion, 2. Power Stone, 3. Smoke Bomb");

    for (int i = 1; i <= 2; i++){
      System.out.println("Pick Item " + i + " (1. Potion, 2. Power Stone, 3. Smoke Bomb):");
      int choice = input.getActionChoice(1, 3);
      switch (choice) {
        case 1 -> selectedItems.add(new Potion());
        case 2 -> selectedItems.add(new PowerStone());
        case 3 -> selectedItems.add(new SmokeBomb());
      }
    }
    return selectedItems;
  }

  private LevelConfig selectDifficulty() {
    System.out.println("\nSelect Difficulty: 1. Easy, 2. Medium, 3. Hard");
    int choice = input.getActionChoice(1, 3);

    switch (choice) {
      case 1:
        return new LevelConfig("Easy",
            List.of(new Goblin("A"), new Goblin("B"), new Goblin("C")),
            List.of());
      case 2:
        return new LevelConfig("Medium",
            List.of(new Goblin("A"), new Wolf("A")),
            List.of(new Wolf("B"), new Wolf("C")));
      case 3:
        return new LevelConfig("Hard",
            List.of(new Goblin("A"), new Goblin("B")),
            List.of(new Goblin("C"), new Wolf("A"), new Wolf("B")));
      default:
        return new LevelConfig("Easy",
            List.of(new Goblin("A"), new Goblin("B"), new Goblin("C")),
            List.of());
    }
  }
}
