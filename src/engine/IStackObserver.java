package engine;

/**
 * Observer Pattern — allows entities, items, or passives to "listen"
 * to the BattleEngine's action stack and inject reactions.
 *
 * When an ActionCommand is about to be pushed onto the resolution stack,
 * the engine notifies every registered IStackObserver. If an observer
 * returns a non-null ActionCommand, that reaction is pushed ON TOP of
 * the original, ensuring LIFO resolution (reaction resolves first).
 *
 * OCP: New reaction types (Smoke Bomb trigger, Mirror Cape, Dispel, etc.)
 * are added by creating a new class — zero changes to BattleEngine.
 *
 * DIP: BattleEngine depends only on this interface, never on concrete
 * reaction implementations.
 */
public interface IStackObserver {

    /**
     * Called by the BattleEngine when an ActionCommand is pending.
     *
     * @param pendingAction the command about to resolve
     * @return a new ActionCommand (the reaction) to push on top of the stack,
     *         or null if this observer does not wish to react
     */
    ActionCommand onActionPending(ActionCommand pendingAction);
}
