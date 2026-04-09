package engine;

import entities.CombatantDecorator;
import entities.Combatant;
import entities.Enemy;
import entities.Player;
import interfaces.IAction;
import interfaces.ICombatant;
import interfaces.ITurnOrderStrategy;
import strategies.ICombatStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BattleEngine {

    private final Player player;
    private final List<ICombatant> enemies;
    private final ITurnOrderStrategy turnOrderStrategy;
    private final List<ICombatant> backupEnemies;
    private boolean backupSpawned;
    private int roundNumber;
    private final Stack<ICommand> commandHistory = new Stack<>();

    // ── Observer Pattern: reactive action stack ──
    private final List<IStackObserver> stackObservers = new ArrayList<>();
    private static final int MAX_REACTION_DEPTH = 5;

    public BattleEngine(Player player, LevelConfig level, ITurnOrderStrategy turnOrderStrategy) {
        this.player = player;
        this.enemies = new ArrayList<>(level.getInitialEnemies());
        this.backupEnemies = new ArrayList<>(level.getBackupEnemies());
        this.turnOrderStrategy = turnOrderStrategy;
        this.backupSpawned = false;
        this.roundNumber = 0;
    }

    // ═══════════════════════════════════════════════════
    //  OBSERVER REGISTRATION (Subject role)
    // ═══════════════════════════════════════════════════

    public void registerObserver(IStackObserver observer) {
        stackObservers.add(observer);
    }

    public void removeObserver(IStackObserver observer) {
        stackObservers.remove(observer);
    }

    // ═══════════════════════════════════════════════════
    //  COMMAND STACK — LIFO resolution with reactions
    // ═══════════════════════════════════════════════════

    /**
     * Core turn processing with reactive action stack.
     *
     * 1. Build the ActionCommand
     * 2. Push it onto a local resolution stack
     * 3. Notify all observers — reactions get pushed ON TOP (LIFO)
     * 4. Resolve the stack top-down: reactions execute BEFORE the original
     * 5. Every resolved command is also pushed onto commandHistory for undo
     */
    public void processTurn(IAction action, ICombatant attacker, List<ICombatant> targets) {
        processTurn(new ActionCommand(this, action, attacker, targets));
    }

    public void processTurn(ActionCommand command) {
        // Build the LIFO resolution stack
        Stack<ActionCommand> resolutionStack = new Stack<>();
        resolutionStack.push(command);

        System.out.printf("  ▸ %s → %s%n",
                command.getAttacker().getName(),
                command.getAction().getActionName());

        // Notify observers — reactions pile on top
        notifyObservers(resolutionStack, command, 0);

        // Resolve LIFO: reactions first, then original action
        if (resolutionStack.size() > 1) {
            System.out.println("  Resolving stack...");
        }
        while (!resolutionStack.isEmpty()) {
            ActionCommand cmd = resolutionStack.pop();
            cmd.execute();
            commandHistory.push(cmd);
        }
    }

    /**
     * Recursively notifies all observers about a pending action.
     * If an observer returns a reaction, it is pushed on top and
     * observers are notified again about the reaction (chain reactions).
     * Depth-limited to prevent infinite loops.
     */
    private void notifyObservers(Stack<ActionCommand> stack, ActionCommand pending, int depth) {
        if (depth >= MAX_REACTION_DEPTH) {
            return;
        }
        for (IStackObserver observer : stackObservers) {
            ActionCommand reaction = observer.onActionPending(pending);
            if (reaction != null) {
                stack.push(reaction);
                // Recursively check if anything reacts to the reaction
                notifyObservers(stack, reaction, depth + 1);
            }
        }
    }

    // ═══════════════════════════════════════════════════
    //  DECORATOR UTILITY
    // ═══════════════════════════════════════════════════

    private ICombatant unwrap(ICombatant c) {
        while (c instanceof CombatantDecorator d) {
            c = d.getWrappedCombatant();
        }
        return c;
    }

    // ═══════════════════════════════════════════════════
    //  ROUND LOOP
    // ═══════════════════════════════════════════════════

    public boolean runRound(ActionProvider actionProvider) {
        roundNumber++;

        List<ICombatant> allCombatants = getAllCombatants();
        prepareRound(allCombatants);

        List<ICombatant> turnOrder = turnOrderStrategy.determineTurnOrder(allCombatants);

        for (ICombatant current : turnOrder) {
            if (!current.isAlive()) {
                continue;
            }

            ICombatant base = unwrap(current);

            current.tickStatusEffects();

            if (current.isStunned()) {
                System.out.printf("  %s is STUNNED and cannot act.%n", current.getName());
                if (base instanceof Player p) {
                    p.decrementCooldown();
                }
                finishTurn(current);
                continue;
            }

            if (base instanceof Player p) {
                // ── PLAYER TURN ──
                p.decrementCooldown();

                List<ICombatant> livingEnemies = getLivingEnemies();
                IAction action = actionProvider.getPlayerAction(p, livingEnemies);

                boolean isChronos = false;
                if (action instanceof actions.ItemAction itemAct) {
                    if (itemAct.getItem(p) instanceof items.ChronosHourglass) {
                        isChronos = true;
                    }
                }

                if (isChronos) {
                    action.perform(p, null);
                    timeReversal();
                    return true;
                } else {
                    List<ICombatant> targets = actionProvider.getTargets(p, livingEnemies, action);
                    processTurn(action, current, targets);
                }

            } else if (base instanceof Enemy e) {
                // ── ENEMY TURN — Strategy Pattern ──
                ICombatStrategy strategy = e.getStrategy();
                if (strategy != null) {
                    ActionCommand cmd = strategy.decideAction(
                            this, current, List.of(player), getLivingEnemies());
                    processTurn(cmd); // Route through LIFO stack for observer reactions
                } else {
                    processTurn(new actions.BasicAttack(), current, List.of(player));
                }
            }

            finishTurn(current);

            if (!player.isAlive()) {
                return false;
            }

            checkBackupSpawn();

            if (allEnemiesDead()) {
                return false;
            }
        }

        previewUpcomingRound(getAllCombatants());
        return true;
    }

    // ═══════════════════════════════════════════════════
    //  ROUND MANAGEMENT
    // ═══════════════════════════════════════════════════

    private List<ICombatant> getAllCombatants() {
        List<ICombatant> allCombatants = new ArrayList<>();
        allCombatants.add(player);
        allCombatants.addAll(enemies);
        return allCombatants;
    }

    private void prepareRound(List<ICombatant> combatants) {
        for (ICombatant combatant : combatants) {
            ICombatant base = unwrap(combatant);
            if (base instanceof Combatant concreteCombatant) {
                concreteCombatant.resetActedThisRound();
                concreteCombatant.setCurrentRound(roundNumber);
                concreteCombatant.purgeExpiredStatusEffects();
            }
        }
    }

    private void finishTurn(ICombatant combatant) {
        ICombatant base = unwrap(combatant);
        if (base instanceof Combatant concreteCombatant) {
            concreteCombatant.markActedThisRound();
            concreteCombatant.purgeExpiredStatusEffects();
        }
    }

    private void previewUpcomingRound(List<ICombatant> combatants) {
        int upcomingRound = roundNumber + 1;
        for (ICombatant combatant : combatants) {
            ICombatant base = unwrap(combatant);
            if (base instanceof Combatant concreteCombatant) {
                concreteCombatant.setCurrentRound(upcomingRound);
                concreteCombatant.purgeExpiredStatusEffects();
            }
        }
    }

    private void checkBackupSpawn() {
        if (backupSpawned || backupEnemies.isEmpty()) {
            return;
        }

        boolean initialWaveCleared = true;
        for (ICombatant e : enemies) {
            if (e.isAlive()) {
                initialWaveCleared = false;
                break;
            }
        }

        if (initialWaveCleared) {
            System.out.println("\n  Backup enemies have arrived!");
            for (ICombatant backup : backupEnemies) {
                System.out.printf("    %s joins the battle!%n", backup.getName());
                ICombatant base = unwrap(backup);
                if (base instanceof Combatant concreteCombatant) {
                    concreteCombatant.resetActedThisRound();
                    concreteCombatant.setCurrentRound(roundNumber);
                }
            }
            enemies.addAll(backupEnemies);
            backupSpawned = true;
        }
    }

    // ═══════════════════════════════════════════════════
    //  WIN / LOSS
    // ═══════════════════════════════════════════════════

    public boolean allEnemiesDead() {
        for (ICombatant e : enemies) {
            if (e.isAlive()) {
                return false;
            }
        }
        return true;
    }

    // ═══════════════════════════════════════════════════
    //  MEMENTO — snapshot / restore
    // ═══════════════════════════════════════════════════

    public BattleSnapshot createSnapshot() {
        return new BattleSnapshot(this.getAllCombatants(), this.enemies, this.backupSpawned, this.roundNumber);
    }

    public void restoreSnapshot(BattleSnapshot snapshot) {
        this.roundNumber = snapshot.getRoundNumber();
        this.backupSpawned = snapshot.isBackupSpawned();

        this.enemies.clear();
        this.enemies.addAll(snapshot.getEnemiesSnapshot());

        for (ICombatant c : getAllCombatants()) {
            if (snapshot.getHealthMap().containsKey(c.getId())) {
                int pastHealth = snapshot.getHealthMap().get(c.getId());
                c.setHp(pastHealth);
            }
            ICombatant base = unwrap(c);
            if (base instanceof Player p && snapshot.getPlayerCooldownMap().containsKey(p.getId())) {
                p.setSkillCooldown(snapshot.getPlayerCooldownMap().get(p.getId()));
            }
        }
    }

    // ═══════════════════════════════════════════════════
    //  TIME REVERSAL (Chronos Hourglass)
    // ═══════════════════════════════════════════════════

    public void timeReversal() {
        if (commandHistory.isEmpty()) {
            System.out.println("  You cannot turn back time any further!");
            return;
        }

        int targetRound = Math.max(1, this.roundNumber - 1);

        while (!commandHistory.isEmpty()) {
            ICommand lastTurn = commandHistory.peek();
            if (lastTurn.getBackupState().getRoundNumber() >= targetRound) {
                lastTurn = commandHistory.pop();
                lastTurn.undo();
            } else {
                break;
            }
        }

        this.roundNumber = targetRound - 1;
        System.out.println("  Time has been reversed! You are back to the start of Round " + targetRound + "!");
    }

    // ═══════════════════════════════════════════════════
    //  ACCESSORS
    // ═══════════════════════════════════════════════════

    public List<ICombatant> getLivingEnemies() {
        List<ICombatant> living = new ArrayList<>();
        for (ICombatant e : enemies) {
            if (e.isAlive()) {
                living.add(e);
            }
        }
        return living;
    }

    public boolean isPlayerAlive() {
        return player.isAlive();
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Player getPlayer() {
        return player;
    }

    public List<ICombatant> getEnemies() {
        return enemies;
    }

    // ═══════════════════════════════════════════════════
    //  ACTION PROVIDER — Boundary layer contract
    // ═══════════════════════════════════════════════════

    public interface ActionProvider {
        IAction getPlayerAction(Player player, List<ICombatant> livingEnemies);
        List<ICombatant> getTargets(Player player, List<ICombatant> livingEnemies, IAction action);
    }
}
