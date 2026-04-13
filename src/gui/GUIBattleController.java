package gui;

import actions.*;
import engine.BattleEngine;
import engine.LevelConfig;
import entities.*;
import interfaces.*;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controls the battle screen GUI.
 *
 * Implements BattleEngine.ActionProvider so the engine can request
 * player input. The engine runs on a background thread, and the GUI
 * provides actions via a synchronized handoff.
 */
public class GUIBattleController implements BattleEngine.ActionProvider {

    private final Stage stage;
    private final Player player;
    private final LevelConfig levelConfig;
    private final ITurnOrderStrategy turnOrderStrategy;
    private final CombatArenaApp app;

    // UI elements
    private VBox root;
    private TextArea logArea;
    private VBox statusPanel;
    private HBox actionBar;
    private Label roundLabel;

    // Action handoff between engine thread and FX thread
    private final Object actionLock = new Object();
    private IAction pendingAction = null;
    private List<ICombatant> pendingTargets = null;

    // Reference to the engine for accessing state
    private BattleEngine engine;

    public GUIBattleController(Stage stage, Player player, LevelConfig levelConfig,
                                ITurnOrderStrategy turnOrderStrategy, CombatArenaApp app) {
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

        // Middle top: status panel
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
            // Redirect System.out so engine/action printfs appear in the GUI log
            PrintStream originalOut = System.out;
            PrintStream guiOut = new PrintStream(new OutputStream() {
                private final StringBuilder line = new StringBuilder();
                @Override
                public void write(int b) {
                    if (b == '\n') {
                        String text = line.toString();
                        line.setLength(0);
                        log(text);
                    } else if (b != '\r') {
                        line.append((char) b);
                    }
                }
            }, true);
            System.setOut(guiOut);

            try {
                engine = new BattleEngine(player, levelConfig, turnOrderStrategy);
                runBattleLoop();
            } catch (Exception ex) {
                ex.printStackTrace(originalOut);
                log("ERROR: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            } finally {
                System.setOut(originalOut);
            }
        });
        engineThread.setDaemon(true);
        engineThread.start();
    }

    // ═══════════════════════════════════════════════
    //  Battle loop — runs on background thread
    //  Mirrors the sequence diagram flow
    // ═══════════════════════════════════════════════

    private void runBattleLoop() {
        log("══════════ BATTLE START ══════════");
        log("");
        log("  " + player.getName() + "  HP:" + player.getHp()
                + "  ATK:" + player.getAttack() + "  DEF:" + player.getDefense()
                + "  SPD:" + player.getSpeed());
        log("");
        log("  Enemies:");
        for (ICombatant e : engine.getEnemies()) {
            log("    " + e.getName() + "  HP:" + e.getHp()
                    + "  ATK:" + e.getAttack() + "  DEF:" + e.getDefense()
                    + "  SPD:" + e.getSpeed());
        }
        log("");
        log("══════════════════════════════════");

        boolean battleContinues = true;
        while (battleContinues) {
            int round = engine.getRoundNumber() + 1;
            Platform.runLater(() -> roundLabel.setText("⚔  Round " + round));
            log("");
            log("─────── Round " + round + " ───────");
            refreshStatus();

            battleContinues = engine.runRound(this);

            // Refresh status after every round so HP/deaths are visible
            refreshStatus();

            // Small delay between rounds for readability
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }

        // Battle ended — refresh one last time so final HP state is shown
        refreshStatus();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        showBattleResult();
    }

    private void showBattleResult() {
        boolean playerWon = player.isAlive() && engine.allEnemiesDead();
        log("");
        log("══════════════════════════════════");
        log(playerWon ? "         VICTORY" : "          DEFEAT");
        log("══════════════════════════════════");

        if (playerWon) {
            log("  " + player.getName() + "  " + player.getHp() + "/" + player.getMaxHp() + " HP remaining");
            log("  Rounds: " + engine.getRoundNumber());
        } else {
            log("  " + player.getName() + " has fallen");
            log("  Enemies remaining: " + engine.getLivingEnemies().size());
            log("  Rounds survived: " + engine.getRoundNumber());
        }

        Platform.runLater(() -> {
            actionBar.getChildren().clear();
            roundLabel.setText(playerWon ? "🏆 VICTORY!" : "💀 DEFEAT!");

            Button newGameBtn = styledButton("New Game", "#27ae60");
            newGameBtn.setOnAction(e -> app.returnToSetup());

            Button exitBtn = styledButton("Exit", "#c0392b");
            exitBtn.setOnAction(e -> Platform.exit());

            actionBar.getChildren().addAll(newGameBtn, exitBtn);
        });
    }

    // ═══════════════════════════════════════════════
    //  BattleEngine.ActionProvider implementation
    //  Follows the sequence diagram: getPlayerAction → getTargets
    // ═══════════════════════════════════════════════

    @Override
    public IAction getPlayerAction(Player player, List<ICombatant> livingEnemies) {
        refreshStatus();

        // Show action buttons on FX thread
        Platform.runLater(() -> showActionButtons(player, livingEnemies));

        // Block engine thread until UI provides an action
        synchronized (actionLock) {
            pendingAction = null;
            pendingTargets = null;
            while (pendingAction == null) {
                try {
                    actionLock.wait();
                } catch (InterruptedException ignored) {}
            }
        }

        return pendingAction;
    }

    @Override
    public List<ICombatant> getTargets(Player player, List<ICombatant> livingEnemies, IAction action) {
        // If targets were already resolved (e.g. during target picker), return them
        if (pendingTargets != null) {
            List<ICombatant> targets = pendingTargets;
            // Clear and hide buttons
            Platform.runLater(() -> actionBar.getChildren().clear());
            return targets;
        }

        // For DefendAction, target is self
        if (action instanceof DefendAction) {
            Platform.runLater(() -> actionBar.getChildren().clear());
            return List.of(player);
        }

        // Otherwise prompt for targets
        Platform.runLater(() -> actionBar.getChildren().clear());
        return livingEnemies;
    }

    /**
     * Provide the chosen action and wake up the engine thread.
     */
    private void submitAction(IAction action, List<ICombatant> targets) {
        synchronized (actionLock) {
            pendingAction = action;
            pendingTargets = targets;
            actionLock.notifyAll();
        }
    }

    // ═══════════════════════════════════════════════
    //  UI Builders
    // ═══════════════════════════════════════════════

    private void showActionButtons(Player player, List<ICombatant> livingEnemies) {
        actionBar.getChildren().clear();

        // 1) Basic Attack
        Button atkBtn = styledButton("Basic Attack", "#3498db");
        atkBtn.setOnAction(e -> showTargetPicker(player, livingEnemies, target -> {
            submitAction(new BasicAttack(), List.of(target));
        }));

        // 2) Defend
        Button defBtn = styledButton("Defend", "#2ecc71");
        defBtn.setOnAction(e -> {
            submitAction(new DefendAction(), List.of(player));
        });

        actionBar.getChildren().addAll(atkBtn, defBtn);

        // 3) Use Item (if available)
        if (player.hasItemsLeft()) {
            Button itemBtn = styledButton("Use Item", "#f39c12");
            itemBtn.setOnAction(e -> showItemPicker(player, livingEnemies));
            actionBar.getChildren().add(itemBtn);
        }

        // 4) Special Skill
        if (player.isSkillReady()) {
            Button skillBtn = styledButton("Special Skill", "#9b59b6");
            skillBtn.setOnAction(e -> {
                if (player instanceof Warrior) {
                    showTargetPicker(player, livingEnemies, target -> {
                        submitAction(new SpecialSkillAction(), List.of(target));
                    });
                } else {
                    // Wizard targets all
                    submitAction(new SpecialSkillAction(), new ArrayList<>(livingEnemies));
                }
            });
            actionBar.getChildren().add(skillBtn);
        } else {
            Button skillBtn = styledButton("Skill (CD:" + player.getSpecialSkillCooldown() + ")", "#7f8c8d");
            skillBtn.setDisable(true);
            actionBar.getChildren().add(skillBtn);
        }
    }

    // ── Target picker ───────────────────────────

    private void showTargetPicker(Player player, List<ICombatant> enemies, java.util.function.Consumer<ICombatant> callback) {
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

        // Back button
        Button backBtn = styledButton("Back", "#7f8c8d");
        backBtn.setOnAction(e -> showActionButtons(player, enemies));
        actionBar.getChildren().add(backBtn);
    }

    // ── Item picker ─────────────────────────────

    private void showItemPicker(Player player, List<ICombatant> livingEnemies) {
        actionBar.getChildren().clear();

        Label prompt = new Label("Choose item:");
        prompt.setTextFill(Color.LIGHTGRAY);
        prompt.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        actionBar.getChildren().add(prompt);

        List<IItem> items = player.getInventory();
        for (int i = 0; i < items.size(); i++) {
            IItem item = items.get(i);
            final int itemIndex = i;
            Button btn = styledButton(item.getItemName(), "#e67e22");
            btn.setOnAction(e -> {
                submitAction(new ItemAction(itemIndex), new ArrayList<>(livingEnemies));
            });
            actionBar.getChildren().add(btn);
        }

        // Back button
        Button backBtn = styledButton("Back", "#7f8c8d");
        backBtn.setOnAction(e -> showActionButtons(player, livingEnemies));
        actionBar.getChildren().add(backBtn);
    }

    // ═══════════════════════════════════════════════
    //  Status panel & log
    // ═══════════════════════════════════════════════

    private void log(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    private void refreshStatus() {
        if (engine == null) return;

        List<ICombatant> allCombatants = new ArrayList<>();
        allCombatants.add(player);
        allCombatants.addAll(engine.getEnemies());

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
                String effects = c.getActiveEffects().stream()
                        .filter(e -> !e.isExpired())
                        .map(IStatusEffect::getEffectName)
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
