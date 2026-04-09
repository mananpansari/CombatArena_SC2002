package strategies;

import actions.BasicAttack;
import engine.ActionCommand;
import engine.BattleEngine;
import interfaces.ICombatant;
import java.util.List;

/**
 * Aggressive AI: always targets the player with the lowest HP
 * and uses BasicAttack.
 */
public class AggressiveStrategy implements ICombatStrategy {

    @Override
    public ActionCommand decideAction(BattleEngine engine, ICombatant self,
                                      List<ICombatant> players, List<ICombatant> allies) {

        // Find the living player with the lowest HP
        ICombatant weakestTarget = null;
        for (ICombatant p : players) {
            if (p.isAlive()) {
                if (weakestTarget == null || p.getHp() < weakestTarget.getHp()) {
                    weakestTarget = p;
                }
            }
        }

        // Fallback: if no living target found, just pick the first player
        if (weakestTarget == null) {
            weakestTarget = players.get(0);
        }

        return new ActionCommand(engine, new BasicAttack(), self, List.of(weakestTarget));
    }
}
