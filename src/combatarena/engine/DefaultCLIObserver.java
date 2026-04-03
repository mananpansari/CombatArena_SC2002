package combatarena.engine;

import combatarena.action.*;
import combatarena.effect.StatusEffect;
import combatarena.entity.*;
import combatarena.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default CLI-based BattleObserver and ActionProvider.
 *
 * This is a working implementation so the BattleEngine can be tested
 * end-to-end from the terminal. Person 5 is expected to replace or
 * extend this with a polished {@code GameUI / CLIDisplay}.
 *
 * Implements both {@link BattleObserver} and {@link ActionProvider}
 * so the engine has a single object to call back into for display and
 * for gathering player input.
 */
public class DefaultCLIObserver implements BattleObserver, ActionProvider {

    private final java.util.Scanner scanner;

    public DefaultCLIObserver(java.util.Scanner scanner) {
        this.scanner = scanner;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  BattleObserver implementation
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public void onBattleStart(Player player, List<ICombatant> enemies) {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         ⚔️  BATTLE START  ⚔️         ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("  Player: " + player.getName()
                + " | HP:" + player.getHp() + " ATK:" + player.getAttack()
                + " DEF:" + player.getDefense() + " SPD:" + player.getSpeed());
        System.out.println("  Enemies:");
        for (ICombatant e : enemies) {
            System.out.println("    - " + e.getName()
                    + " | HP:" + e.getHp() + " ATK:" + e.getAttack()
                    + " DEF:" + e.getDefense() + " SPD:" + e.getSpeed());
        }
        System.out.println();
    }

    @Override
    public void onRoundStart(int roundNumber, List<ICombatant> allCombatants) {
        System.out.println("\n┌──────────────── Round " + roundNumber + " ────────────────┐");
        for (ICombatant c : allCombatants) {
            if (!c.isAlive()) continue;
            String bar = hpBar(c);
            String effects = statusString(c);
            System.out.println("  " + padRight(c.getName(), 12) + " " + bar + effects);
        }
        System.out.println("└─────────────────────────────────────────┘");
    }

    @Override
    public void onRoundEnd(int roundNumber, List<ICombatant> allCombatants) {
        // Optional summary — kept minimal; Person 5 can expand.
    }

    @Override
    public void onCombatantStunned(ICombatant combatant) {
        System.out.println("  💫 " + combatant.getName() + " is STUNNED and cannot act this turn!");
    }

    @Override
    public void onActionChosen(ICombatant source, Action action, List<ICombatant> targets) {
        // Pre-action log — kept brief.
    }

    @Override
    public void onActionExecuted(ICombatant source, Action action, List<ICombatant> targets) {
        System.out.print("  ➤ " + source.getName() + " uses " + action.getActionName());
        if (!targets.isEmpty()) {
            String targetNames = targets.stream()
                    .map(ICombatant::getName)
                    .collect(Collectors.joining(", "));
            System.out.print(" → " + targetNames);
        }
        System.out.println();
        // Show target HP after action
        for (ICombatant t : targets) {
            if (t.isAlive()) {
                System.out.println("    " + t.getName() + " now at " + t.getHp() + "/" + t.getMaxHp() + " HP");
            } else {
                System.out.println("    " + t.getName() + " has been DEFEATED!");
            }
        }
    }

    @Override
    public void onDamageNullified(ICombatant attacker, ICombatant defender) {
        System.out.println("  🌫️ " + attacker.getName() + "'s attack is blocked by smoke! "
                + defender.getName() + " takes 0 damage.");
    }

    @Override
    public void onBackupSpawn(List<ICombatant> backupEnemies) {
        System.out.println("\n  ⚠️  REINFORCEMENTS ARRIVE!");
        for (ICombatant e : backupEnemies) {
            System.out.println("    + " + e.getName()
                    + " | HP:" + e.getHp() + " ATK:" + e.getAttack()
                    + " DEF:" + e.getDefense() + " SPD:" + e.getSpeed());
        }
        System.out.println();
    }

    @Override
    public void onBattleEnd(BattleResult result) {
        System.out.println("\n╔══════════════════════════════════════╗");
        if (result.isPlayerWon()) {
            System.out.println("║          🏆  VICTORY!  🏆            ║");
        } else {
            System.out.println("║          💀  DEFEAT!  💀             ║");
        }
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println(result);
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ActionProvider implementation
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public ActionChoice getPlayerAction(Player player, List<ICombatant> enemies) {
        List<ICombatant> livingEnemies = enemies.stream()
                .filter(ICombatant::isAlive)
                .collect(Collectors.toList());

        // ── Build available actions ─────────────────────
        List<String> options = new ArrayList<>();
        options.add("Basic Attack");                // 1
        options.add("Defend");                      // 2

        boolean hasItems = player.hasItems();
        if (hasItems) {
            options.add("Use Item");                // 3
        }

        boolean skillReady = player.isSkillReady();
        String skillLabel = "Special Skill";
        if (!skillReady) {
            skillLabel += " (CD: " + player.getSpecialSkillCooldown() + ")";
        }
        options.add(skillLabel);                    // 3 or 4

        // ── Display options ─────────────────────────────
        System.out.println("\n  Choose your action:");
        for (int i = 0; i < options.size(); i++) {
            System.out.println("    [" + (i + 1) + "] " + options.get(i));
        }
        System.out.print("  > ");

        int choice = readInt(1, options.size());
        String chosen = options.get(choice - 1);

        // ── Resolve action ──────────────────────────────

        // Basic Attack
        if (chosen.equals("Basic Attack")) {
            ICombatant target = chooseTarget(livingEnemies);
            return new ActionChoice(new BasicAttack(), Collections.singletonList(target));
        }

        // Defend
        if (chosen.equals("Defend")) {
            return new ActionChoice(new DefendAction(), Collections.singletonList(player));
        }

        // Use Item
        if (chosen.equals("Use Item")) {
            return handleItemAction(player, livingEnemies);
        }

        // Special Skill
        if (chosen.startsWith("Special Skill")) {
            if (!skillReady) {
                System.out.println("  ⚠ Skill on cooldown! Choose again.");
                return getPlayerAction(player, enemies);
            }
            // Warrior targets one enemy; Wizard targets all enemies
            List<ICombatant> targets;
            if (player instanceof Warrior) {
                ICombatant target = chooseTarget(livingEnemies);
                targets = Collections.singletonList(target);
            } else {
                // Wizard — all living enemies
                targets = new ArrayList<>(livingEnemies);
            }
            return new ActionChoice(new SpecialSkillAction(), targets);
        }

        // Fallback — shouldn't happen
        return getPlayerAction(player, enemies);
    }

    // ── Item selection ───────────────────────────────────────────────

    private ActionChoice handleItemAction(Player player, List<ICombatant> livingEnemies) {
        List<Item> items = player.getInventory();
        System.out.println("  Choose an item:");
        for (int i = 0; i < items.size(); i++) {
            System.out.println("    [" + (i + 1) + "] " + items.get(i).getItemName());
        }
        System.out.print("  > ");
        int idx = readInt(1, items.size());
        Item chosenItem = items.get(idx - 1);
        return new ActionChoice(new ItemAction(chosenItem), new ArrayList<>(livingEnemies));
    }

    // ── Target selection ─────────────────────────────────────────────

    private ICombatant chooseTarget(List<ICombatant> livingEnemies) {
        System.out.println("  Choose a target:");
        for (int i = 0; i < livingEnemies.size(); i++) {
            ICombatant e = livingEnemies.get(i);
            System.out.println("    [" + (i + 1) + "] " + e.getName()
                    + " (HP:" + e.getHp() + "/" + e.getMaxHp() + ")");
        }
        System.out.print("  > ");
        int idx = readInt(1, livingEnemies.size());
        return livingEnemies.get(idx - 1);
    }

    // ── Input helpers ────────────────────────────────────────────────

    private int readInt(int min, int max) {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) return val;
                System.out.print("  Invalid choice (" + min + "-" + max + "). Try again: ");
            } catch (NumberFormatException e) {
                System.out.print("  Please enter a number (" + min + "-" + max + "): ");
            }
        }
    }

    // ── Display helpers ──────────────────────────────────────────────

    private String hpBar(ICombatant c) {
        int total = 20;
        double ratio = (double) c.getHp() / c.getMaxHp();
        int filled = (int) Math.round(ratio * total);
        int empty = total - filled;
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < filled; i++) sb.append("█");
        for (int i = 0; i < empty; i++)  sb.append("░");
        sb.append("] ").append(c.getHp()).append("/").append(c.getMaxHp());
        return sb.toString();
    }

    private String statusString(ICombatant c) {
        List<StatusEffect> effects = c.getStatusEffects();
        if (effects.isEmpty()) return "";
        String names = effects.stream()
                .map(e -> e.getEffectName() + "(" + e.getDuration() + ")")
                .collect(Collectors.joining(", "));
        return "  [" + names + "]";
    }

    private String padRight(String s, int width) {
        if (s.length() >= width) return s;
        return s + " ".repeat(width - s.length());
    }
}
