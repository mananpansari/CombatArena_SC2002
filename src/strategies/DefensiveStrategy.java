package strategies;

import actions.BasicAttack;
import actions.DefendAction;
import engine.ActionCommand;
import engine.BattleEngine;
import interfaces.ICombatant;
import java.util.List;

/**
 * Defensive AI: if own HP drops below 30%, uses DefendAction to
 * boost defense. Otherwise, falls back to BasicAttack on the
 * weakest player.
 */
public class DefensiveStrategy implements ICombatStrategy {

    private static final double LOW_HP_THRESHOLD = 0.30;

    @Override
    public ActionCommand decideAction(BattleEngine engine, ICombatant self,
                                      List<ICombatant> players, List<ICombatant> allies) {

        double hpPercent = (double) self.getHp() / self.getMaxHp();

        if (hpPercent < LOW_HP_THRESHOLD) {
            // Defend — the DefendAction targets the source itself
            return new ActionCommand(engine, new DefendAction(), self, List.of(self));
        }

        // Otherwise, attack the weakest player
        ICombatant weakest = null;
        for (ICombatant p : players) {
            if (p.isAlive()) {
                if (weakest == null || p.getHp() < weakest.getHp()) {
                    weakest = p;
                }
            }
        }
        if (weakest == null) {
            weakest = players.get(0);
        }

        return new ActionCommand(engine, new BasicAttack(), self, List.of(weakest));
    }
}
