package combatarena.gui;

import combatarena.action.*;
import combatarena.engine.*;
import combatarena.entity.*;
import combatarena.item.Item;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controls the battle screen.
 *
 * The BattleEngine runs on a background thread. When it needs the player's
 * action (via ActionProvider), it blocks and waits for the JavaFX thread
 * to provide the choice through a synchronized handoff.
 */
public class GUIBattleController implements BattleObserver, ActionProvider {

    private final Stage stage;
    private final Player player;
    private final LevelConfig levelConfig;
    private final TurnOrderStrategy turnOrderStrategy;
    private final CombatArenaApp app;

    // UI elements
    private VBox root;
    private TextArea logArea;
    private VBox statusPanel;
    private HBox actionBar;
    private Label roundLabel;

    // Action handoff between engine thread and FX thread
    private final Object actionLock = new Object();
    private ActionChoice pendingAction = null;

    public GUIBattleController(Stage stage, Player player, LevelConfig levelConfig,
                                TurnOrderStrategy turnOrderStrategy, CombatArenaApp app) {
        this.stage = stage;
        this.player = player;
        this.levelConfig = levelConfig;
        this.turnOrderStrategy = turnOrderStrategy;
        this.app = app;
    }

    public void show() {
        root = new VBox(8);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #1a1a2e;");

        // Top: round label
        roundLabel = new Label("Preparing battle...");
        roundLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        roundLabel.setTextFill(Color.web("#e94560"));

        // Middle top: status panel (HP bars for all combatants)
        statusPanel = new VBox(3);
        statusPanel.setPadding(new Insets(5));
        statusPanel.setStyle("-fx-background-color: #16213e; -fx-background-radius: 5;");

        // Middle: scrollable log
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle("-fx-control-inner-background: #0f3460; -fx-text-fill: #e0e0e0; "
                + "-fx-font-family: 'Monospace'; -fx-font-size: 12;");
        logArea.setPrefRowCount(12);
        VBox.setVgrow(logArea, Priority.ALWAYS);

        // Bottom: action buttons
        actionBar = new HBox(8);
        actionBar.setAlignment(Pos.CENTER);
        actionBar.setPadding(new Insets(5));

        root.getChildren().addAll(roundLabel, statusPanel, logArea, actionBar);

        stage.setScene(new Scene(root, 720, 560));

        // Run the engine on a background thread
        Thread engineThread = new Thread(() -> {
            BattleEngine engine = new BattleEngine(player, levelConfig, turnOrderStrategy, this, this);
            engine.startBattle();
        });
        engineThread.setDaemon(true);
        engineThread.start();
    }

    // ═══════════════════════════════════════════════
    //  Logging helper
    // ═══════════════════════════════════════════════

    private void log(String message) {
        Platform.runLater(() -> {
            logArea.appendText(message + "\n");
        });
    }

    // ═══════════════════════════════════════════════
    //  Status panel refresh
    // ═══════════════════════════════════════════════

    private void refreshStatus(List<ICombatant> allCombatants) {
        Platform.runLater(() -> {
            statusPanel.getChildren().clear();
            for (ICombatant c : allCombatants) {
                if (!c.isAlive()) continue;
                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);

                Label nameLabel = new Label(padRight(c.getName(), 14));
                nameLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
                nameLabel.setTextFill(c instanceof Player ? Color.web("#00d2ff") : Color.web("#ff6b6b"));
                nameLabel.setMinWidth(110);

                // HP bar
                double ratio = (double) c.getHp() / c.getMaxHp();
                ProgressBar hpBar = new ProgressBar(ratio);
                hpBar.setPrefWidth(180);
                hpBar.setPrefHeight(16);
                String barColor = ratio > 0.5 ? "#27ae60" : ratio > 0.25 ? "#f39c12" : "#e74c3c";
                hpBar.setStyle("-fx-accent: " + barColor + ";");

                Label hpText = new Label(c.getHp() + "/" + c.getMaxHp());
                hpText.setFont(Font.font("Monospace", 11));
                hpText.setTextFill(Color.LIGHTGRAY);
                hpText.setMinWidth(60);

                // Status effects
                String effects = c.getStatusEffects().stream()
                        .map(e -> e.getEffectName() + "(" + e.getDuration() + ")")
                        .collect(Collectors.joining(", "));
                Label effectLabel = new Label(effects);
                effectLabel.setFont(Font.font("Monospace", 10));
                effectLabel.setTextFill(Color.web("#ffd93d"));

                row.getChildren().addAll(nameLabel, hpBar, hpText, effectLabel);
                statusPanel.getChildren().add(row);
            }
        });
    }

    // ═══════════════════════════════════════════════
    //  BattleObserver implementation
    // ═══════════════════════════════════════════════

    @Override
    public void onBattleStart(Player player, List<ICombatant> enemies) {
        log("═══ BATTLE START ═══");
        log("Player: " + player.getName() + " | HP:" + player.getHp()
                + " ATK:" + player.getAttack() + " DEF:" + player.getDefense()
                + " SPD:" + player.getSpeed());
        for (ICombatant e : enemies) {
            log("  Enemy: " + e.getName() + " | HP:" + e.getHp()
                    + " ATK:" + e.getAttack() + " DEF:" + e.getDefense()
                    + " SPD:" + e.getSpeed());
        }
        log("");
    }

    @Override
    public void onRoundStart(int roundNumber, List<ICombatant> allCombatants) {
        Platform.runLater(() -> roundLabel.setText("⚔  Round " + roundNumber));
        log("──── Round " + roundNumber + " ────");
        refreshStatus(allCombatants);
    }

    @Override
    public void onRoundEnd(int roundNumber, List<ICombatant> allCombatants) {
        // Optional
    }

    @Override
    public void onCombatantStunned(ICombatant combatant) {
        log("  💫 " + combatant.getName() + " is STUNNED — turn skipped!");
    }

    @Override
    public void onActionChosen(ICombatant source, Action action, List<ICombatant> targets) {
        // Nothing needed pre-execution in GUI
    }

    @Override
    public void onActionExecuted(ICombatant source, Action action, List<ICombatant> targets) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ➤ ").append(source.getName()).append(" uses ").append(action.getActionName());
        if (!targets.isEmpty()) {
            String tNames = targets.stream().map(ICombatant::getName).collect(Collectors.joining(", "));
            sb.append(" → ").append(tNames);
        }
        log(sb.toString());

        for (ICombatant t : targets) {
            if (t.isAlive()) {
                log("    " + t.getName() + " now at " + t.getHp() + "/" + t.getMaxHp() + " HP");
            } else {
                log("    " + t.getName() + " has been DEFEATED!");
            }
        }
    }

    @Override
    public void onDamageNullified(ICombatant attacker, ICombatant defender) {
        log("  🌫 " + attacker.getName() + "'s attack blocked by smoke! 0 damage.");
    }

    @Override
    public void onBackupSpawn(List<ICombatant> backupEnemies) {
        log("\n  ⚠ REINFORCEMENTS ARRIVE!");
        for (ICombatant e : backupEnemies) {
            log("    + " + e.getName() + " | HP:" + e.getHp());
        }
        log("");
    }

    @Override
    public void onBattleEnd(BattleResult result) {
        log("\n═══ BATTLE " + (result.isPlayerWon() ? "WON" : "LOST") + " ═══");
        log(result.toString());

        Platform.runLater(() -> {
            actionBar.getChildren().clear();
            roundLabel.setText(result.isPlayerWon() ? "🏆 VICTORY!" : "💀 DEFEAT!");

            Button newGameBtn = styledButton("New Game", "#27ae60");
            newGameBtn.setOnAction(e -> app.returnToSetup());

            Button exitBtn = styledButton("Exit", "#c0392b");
            exitBtn.setOnAction(e -> Platform.exit());

            actionBar.getChildren().addAll(newGameBtn, exitBtn);
        });
    }

    // ═══════════════════════════════════════════════
    //  ActionProvider — blocks engine thread until
    //  the player clicks an action button
    // ═══════════════════════════════════════════════

    @Override
    public ActionChoice getPlayerAction(Player player, List<ICombatant> enemies) {
        List<ICombatant> livingEnemies = enemies.stream()
                .filter(ICombatant::isAlive)
                .collect(Collectors.toList());

        // Show action buttons on FX thread
        Platform.runLater(() -> showActionButtons(player, livingEnemies));

        // Block engine thread until UI provides an action
        synchronized (actionLock) {
            pendingAction = null;
            while (pendingAction == null) {
                try {
                    actionLock.wait();
                } catch (InterruptedException ignored) {}
            }
        }

        // Hide buttons
        Platform.runLater(() -> actionBar.getChildren().clear());

        return pendingAction;
    }

    /**
     * Provide the chosen action and wake up the engine thread.
     */
    private void submitAction(ActionChoice choice) {
        synchronized (actionLock) {
            pendingAction = choice;
            actionLock.notifyAll();
        }
    }

    // ═══════════════════════════════════════════════
    //  Action button builders
    // ═══════════════════════════════════════════════

    private void showActionButtons(Player player, List<ICombatant> livingEnemies) {
        actionBar.getChildren().clear();

        // 1) Basic Attack
        Button atkBtn = styledButton("Basic Attack", "#3498db");
        atkBtn.setOnAction(e -> showTargetPicker(livingEnemies, target -> {
            submitAction(new ActionChoice(new BasicAttack(), Collections.singletonList(target)));
        }));

        // 2) Defend
        Button defBtn = styledButton("Defend", "#2ecc71");
        defBtn.setOnAction(e -> {
            submitAction(new ActionChoice(new DefendAction(), Collections.singletonList(player)));
        });

        actionBar.getChildren().addAll(atkBtn, defBtn);

        // 3) Use Item (if available)
        if (player.hasItems()) {
            Button itemBtn = styledButton("Use Item", "#f39c12");
            itemBtn.setOnAction(e -> showItemPicker(player, livingEnemies));
            actionBar.getChildren().add(itemBtn);
        }

        // 4) Special Skill
        if (player.isSkillReady()) {
            Button skillBtn = styledButton("Special Skill", "#9b59b6");
            skillBtn.setOnAction(e -> {
                if (player instanceof Warrior) {
                    showTargetPicker(livingEnemies, target -> {
                        submitAction(new ActionChoice(new SpecialSkillAction(), Collections.singletonList(target)));
                    });
                } else {
                    // Wizard targets all
                    submitAction(new ActionChoice(new SpecialSkillAction(), new ArrayList<>(livingEnemies)));
                }
            });
            actionBar.getChildren().add(skillBtn);
        } else {
            Button skillBtn = styledButton("Skill (CD:" + player.getSpecialSkillCooldown() + ")", "#7f8c8d");
            skillBtn.setDisable(true);
            actionBar.getChildren().add(skillBtn);
        }
    }

    // ── Target picker popup ─────────────────────

    private void showTargetPicker(List<ICombatant> enemies, java.util.function.Consumer<ICombatant> callback) {
        actionBar.getChildren().clear();

        Label prompt = new Label("Choose target:");
        prompt.setTextFill(Color.LIGHTGRAY);
        prompt.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        actionBar.getChildren().add(prompt);

        for (ICombatant enemy : enemies) {
            Button btn = styledButton(enemy.getName() + " (" + enemy.getHp() + " HP)", "#e74c3c");
            btn.setOnAction(e -> callback.accept(enemy));
            actionBar.getChildren().add(btn);
        }
    }

    // ── Item picker ─────────────────────────────

    private void showItemPicker(Player player, List<ICombatant> livingEnemies) {
        actionBar.getChildren().clear();

        Label prompt = new Label("Choose item:");
        prompt.setTextFill(Color.LIGHTGRAY);
        prompt.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        actionBar.getChildren().add(prompt);

        for (Item item : new ArrayList<>(player.getInventory())) {
            Button btn = styledButton(item.getItemName(), "#e67e22");
            btn.setOnAction(e -> {
                submitAction(new ActionChoice(new ItemAction(item), new ArrayList<>(livingEnemies)));
            });
            actionBar.getChildren().add(btn);
        }

        // Back button
        Button backBtn = styledButton("Back", "#7f8c8d");
        backBtn.setOnAction(e -> showActionButtons(player, livingEnemies));
        actionBar.getChildren().add(backBtn);
    }

    // ═══════════════════════════════════════════════
    //  Helpers
    // ═══════════════════════════════════════════════

    private Button styledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                + "-fx-font-size: 12; -fx-padding: 6 14; -fx-cursor: hand; -fx-background-radius: 4;");
        return btn;
    }

    private String padRight(String s, int width) {
        if (s.length() >= width) return s;
        return s + " ".repeat(width - s.length());
    }
}
