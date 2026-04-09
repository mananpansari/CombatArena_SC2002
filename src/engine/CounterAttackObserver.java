package engine;

import actions.CounterAttackAction;
import interfaces.ICombatant;
import java.util.List;

/**
 * Concrete Observer — "Spiked Shield" / "Counter" passive.
 *
 * When an attack targets this observer's owner, it pushes a
 * CounterAttackAction onto the stack that deals thorns damage
 * back to the attacker BEFORE the original attack resolves (LIFO).
 *
 * Usage:
 *   Wolf boss = new Wolf("Boss");
 *   engine.registerObserver(new CounterAttackObserver(boss, 5));
 *   // Now when someone attacks the boss, they take 5 thorns damage first.
 */
public class CounterAttackObserver implements IStackObserver {

    private final ICombatant owner;
    private final int thornsDamage;

    public CounterAttackObserver(ICombatant owner, int thornsDamage) {
        this.owner = owner;
        this.thornsDamage = thornsDamage;
    }

    @Override
    public ActionCommand onActionPending(ActionCommand pendingAction) {
        // Guard: don't react to other counter-attacks (prevents infinite chains)
        if (pendingAction.getAction() instanceof CounterAttackAction) {
            return null;
        }

        // Guard: owner must be alive to react
        if (!owner.isAlive()) {
            return null;
        }

        // Check if any target of the pending action is our owner
        List<ICombatant> targets = pendingAction.getTargets();
        if (targets == null) {
            return null;
        }

        for (ICombatant target : targets) {
            if (target.getId().equals(owner.getId())) {
                // React! Push a counter-attack that hits the original attacker
                System.out.printf("  🛡️ %s's Spiked Shield reacts! [CounterAttack] added to stack.%n",
                        owner.getName());
                return new ActionCommand(
                        pendingAction.getEngine(),
                        new CounterAttackAction(thornsDamage),
                        owner,
                        List.of(pendingAction.getAttacker())
                );
            }
        }

        return null; // Not targeting our owner — no reaction
    }
}
