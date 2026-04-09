package strategies;

import actions.BasicAttack;
import actions.HealAction;
import engine.ActionCommand;
import engine.BattleEngine;
import interfaces.ICombatant;
import java.util.List;

/**
 * Support AI: scans allied enemies for one whose HP is below 50%.
 * If found, heals the most wounded ally. Otherwise, falls back to
 * BasicAttack on the weakest player.
 */
public class SupportStrategy implements ICombatStrategy {

    private static final double ALLY_LOW_HP_THRESHOLD = 0.50;

    @Override
    public ActionCommand decideAction(BattleEngine engine, ICombatant self,
                                      List<ICombatant> players, List<ICombatant> allies) {

        // Check if any living ally (excluding self) is below the HP threshold
        ICombatant woundedAlly = null;
        for (ICombatant ally : allies) {
            if (ally == self || !ally.isAlive()) {
                continue;
            }
            double allyHpPercent = (double) ally.getHp() / ally.getMaxHp();
            if (allyHpPercent < ALLY_LOW_HP_THRESHOLD) {
                // Pick the most wounded ally
                if (woundedAlly == null || ally.getHp() < woundedAlly.getHp()) {
                    woundedAlly = ally;
                }
            }
        }

        if (woundedAlly != null) {
            return new ActionCommand(engine, new HealAction(), self, List.of(woundedAlly));
        }

        // No ally needs healing — attack the weakest player
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
