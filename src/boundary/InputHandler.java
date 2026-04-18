package boundary;

import engine.BattleEngine;
import entities.Player;
import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;
import java.util.Scanner;

public class InputHandler implements BattleEngine.ActionProvider {
  private final Scanner sc = new Scanner(System.in);

  public int getActionChoice(int min, int max) {
    while (true) {
      try {
        int choice = Integer.parseInt(sc.nextLine());
        if (choice >= min && choice <= max) {
          return choice;
        } else {
          System.out.println("Invalid range, enter a number within range.");
        }
      } catch (NumberFormatException exc) {
        System.out.println("Enter a VALID number.");
      }
    }
  }

  public boolean getReplay() {
    System.out.println("Replay? enter 'yes' OR 'no'. ");
    while (true) {
      String input = sc.nextLine().toLowerCase();
      if (input.equals("yes")) return true;
      if (input.equals("no")) return false;
      System.out.println("Replay again? Enter 'yes' OR 'no'.");
    }
  }

  @Override
  public IAction getPlayerAction(Player player, List<ICombatant> livingEnemies) {
    System.out.println("\n--- Your Turn ---");
    System.out.println("1. Basic Attack");
    System.out.println("2. Defend");
    System.out.println("3. Special Skill (CD: " + player.getSpecialSkillCooldown() + ")");
    System.out.println("4. Use Item");
    int choice = getActionChoice(1, 4);
    switch (choice) {
      case 1: return new actions.BasicAttack();
      case 2: return new actions.DefendAction();
      case 3: return new actions.SpecialSkillAction();
      case 4:
        if (!player.hasItemsLeft()) {
          System.out.println("No items left! Using Basic Attack.");
          return new actions.BasicAttack();
        }
        List<interfaces.IItem> items = player.getInventory();
        for (int i = 0; i < items.size(); i++) {
          System.out.printf("%d. %s%n", i + 1, items.get(i).getItemName());
        }
        int itemChoice = getActionChoice(1, items.size());
        return new actions.ItemAction(itemChoice - 1);
      default: return new actions.BasicAttack();
    }
  }

  @Override
  public List<ICombatant> getTargets(Player player, List<ICombatant> livingEnemies, IAction action) {
    if (action instanceof actions.DefendAction) return List.of(player);
    if (action instanceof actions.ItemAction) return List.of(player);
    System.out.println("Select Target:");
    for (int i = 0; i < livingEnemies.size(); i++) {
      System.out.printf("%d. %s (HP: %d)%n", i + 1, livingEnemies.get(i).getName(), livingEnemies.get(i).getHp());
    }
    int targetID = getActionChoice(1, livingEnemies.size());
    return List.of(livingEnemies.get(targetID - 1));
  }
}
