package Boundary;

import engine.BattleEngine;
import entities.Player;
import interfaces.IAction;
import interfaces.ICombatant;
import java.util.List;
import java.util.Scanner;

public class InputHandler implements BattleEngine.ActionProvider {
  private final Scanner sc = new Scanner(System.in);

  // Safety input precaution to handle invalid input
  public int getActionChoice(int min,int max){
    while(true){
      try{
        int choice = Integer.parseInt(sc.nextLine());
        if(choice >= min && choice <=max){
          return choice;
        }
        else{
          System.out.println("Invalid range, enter a number within range.");
         }
      }
        catch(NumberFormatException exc) {
          System.out.println("Enter a VALID number.");
      }
    }
  }
  public boolean getReplay(){
    System.out.println("Replay? enter 'yes' OR 'no'. ");
    while(true) {
      String input = sc.nextLine().toLowerCase();
      if (input.equals("yes")) {
        return true;
      }
      if (input.equals("no")) {
        return false;
      }
      System.out.println("Replay again? Enter 'yes' OR 'no'.");
  }  
 }
  // implement battleEngine methods
  @Override
  public IAction getPlayerAction(Player player, List<ICombatant> livingEnemies){
    System.out.println("\n--- Your Turn ---");
    System.out.println("1. Basic Attack");
    System.out.println("2. Special Skill (CD: "+ player.getSpecialSkillCooldown() + ")");
    System.out.println("3. Use Item");

    int choice = getActionChoice(1,3);

    return player.getActions().get(choice-1);
  }
  @Override
  public List<ICombatant> getTargets(Player player, List<ICombatant> livingEnemies, IAction action){
    System.out.println("Select Target:");
    for(int i = 0; i< livingEnemies.size(); i++){
      System.out.printf("%d. %s (HP: %d)%n", i + 1, livingEnemies.get(i).getName(), livingEnemies.get(i).getHp());
    }
    int targetID = getActionChoice(1,livingEnemies.size());
    return List.of(livingEnemies.get(targetID-1));
 }
}  
