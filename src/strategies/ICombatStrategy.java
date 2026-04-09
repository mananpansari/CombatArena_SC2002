package strategies;

import engine.ActionCommand;
import engine.BattleEngine;
import interfaces.ICombatant;
import java.util.List;

/**
 * Strategy Pattern interface for Enemy AI decision-making.
 * Each concrete strategy encapsulates a different combat behaviour.
 * 
 * The returned ActionCommand is "stack-aware" — it will be pushed
 * onto the engine's commandHistory, enabling time-reversal via undo().
 *
 * OCP: New AI behaviours (e.g. BerserkerAIStrategy) can be added
 * by creating a new class that implements this interface, without
 * modifying the BattleEngine or Enemy classes.
 */
public interface ICombatStrategy {

    /**
     * Decide which action the combatant should take this turn.
     *
     * @param engine    the BattleEngine (needed to construct an ActionCommand)
     * @param self      the enemy combatant making the decision
     * @param players   the opposing player combatants (targets)
     * @param allies    the other enemy combatants (potential heal targets)
     * @return a fully-constructed ActionCommand ready to be executed and pushed
     */
    ActionCommand decideAction(BattleEngine engine, ICombatant self,
                               List<ICombatant> players, List<ICombatant> allies);
}
