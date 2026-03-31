package Boundary;

import engine.GameSession;
import engine.LevelConfig;
import entities.Player;
import entities.Warrior;
import entities.Wizard;
import java.util.List;
import java.util.ArrayList;

public class LoadingScreen {
  private final InputHandler input;

  public LoadingScreen(InputHandler input){
    this.input=input;
  }

  public GameSession setupGame() {
    System.out.println("Welcome to the SC2002 Combat Arena.");

    Player player =createPlayer();
    LevelConfig level = selectDifficulty();

    return new GameSession(player,level);

}
  private Player createPlayer() {
    System.out.println("\nSelect character: \n1. Warrior-tank\n2. Wizard- High ATK & SPD");
    int choice = input.getActionChoice(1,2);

    List<String> items =chooseItems();

    if (choice ==1){
      return new Warrior ("warrior",items);
    }
    else{
      return new Wizard ("wizard",items);
    }
  }
  private List <String> chooseItems() {
    String[] availableItems = {"Potion", "Power Stone", "Smoke Bomb"};
    List<String> selectedItems = new ArrayList<>();
    System.out.println("Pick 2 items: 1.Potion, 2.Power Stone, 3.Smoke Bomb");

    for (int i=1;i <= 2;i++){
     System.out.println("Pick Item " + i + " (1. Potion, 2. Power Stone, 3. Smoke Bomb):");
        int choice = input.getActionChoice(1, 3);
        selectedItems.add(availableItems[choice - 1]);
    }
    return selectedItems;
  }
  private LevelConfig selectDifficulty() {
    System.out.println("\nSelect Difficulty: 1. Easy, 2. Medium, 3. Hard");
    int choice = input.getActionChoice(1, 3);
    
    return new LevelConfig(choice); 
 }
}  
