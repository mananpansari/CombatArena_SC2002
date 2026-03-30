package Boundary;

import interfaces.ICombatant;
import entities.Player;
import java.util.List;

public class GameUi {

  public void displayCombatantStatus(Player player, List<ICombatant> enemies) {
    System.out.printf(" Player: %-15s HP: %d/%d%n, player.getName(),player.getHp(), player.getMaxHp());
    for(ICombatant e: enemies){
      if (e.isAlive()) {
        System.out.printf("Enemy: %-16s HP: %d%n", e.getName(), e.getHp());
      }
    }

  }
  public void displayEvent(String message) {
    System.out.println(">>" + message);
}
