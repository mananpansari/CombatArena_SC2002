package combatarena.gui;

import combatarena.engine.*;
import combatarena.entity.*;
import combatarena.item.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;


/**
 * Extremely simple JavaFX GUI for the Combat Arena game.
 * Replaces the CLI with a basic windowed interface.
 */
public class CombatArenaApp extends Application {

    private Stage primaryStage;
    private Player player;
    private LevelConfig.Difficulty difficulty;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Combat Arena");
        stage.setMinWidth(700);
        stage.setMinHeight(500);

        showSetupScreen();

        stage.show();
    }

    // ═══════════════════════════════════════════════════
    //  SETUP SCREEN — class, items, difficulty
    // ═══════════════════════════════════════════════════

    private void showSetupScreen() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #1a1a2e;");

        // Title
        Label title = new Label("⚔  COMBAT ARENA  ⚔");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#e94560"));

        // ── Class selection ──────────────────
        Label classLabel = sectionLabel("Choose your class:");

        ToggleGroup classGroup = new ToggleGroup();
        RadioButton rbWarrior = new RadioButton("Warrior  (HP:260 ATK:40 DEF:20 SPD:30)");
        rbWarrior.setToggleGroup(classGroup);
        rbWarrior.setSelected(true);
        rbWarrior.setTextFill(Color.LIGHTGRAY);

        RadioButton rbWizard = new RadioButton("Wizard   (HP:200 ATK:50 DEF:10 SPD:20)");
        rbWizard.setToggleGroup(classGroup);
        rbWizard.setTextFill(Color.LIGHTGRAY);

        VBox classBox = new VBox(5, classLabel, rbWarrior, rbWizard);
        classBox.setPadding(new Insets(5, 0, 10, 20));

        // ── Item selection ──────────────────
        Label itemLabel = sectionLabel("Choose 2 items (duplicates allowed):");

        String[] itemNames = {"Potion — Heal 100 HP", "Power Stone — Trigger skill (no CD)", "Smoke Bomb — Nullify damage 2 turns"};
        ComboBox<String> item1 = new ComboBox<>();
        item1.getItems().addAll(itemNames);
        item1.getSelectionModel().selectFirst();
        item1.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        ComboBox<String> item2 = new ComboBox<>();
        item2.getItems().addAll(itemNames);
        item2.getSelectionModel().selectFirst();
        item2.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        HBox itemRow = new HBox(10, new Label("Item 1:") {{ setTextFill(Color.LIGHTGRAY); }}, item1,
                new Label("Item 2:") {{ setTextFill(Color.LIGHTGRAY); }}, item2);
        itemRow.setAlignment(Pos.CENTER_LEFT);
        itemRow.setPadding(new Insets(0, 0, 0, 20));

        VBox itemBox = new VBox(5, itemLabel, itemRow);

        // ── Difficulty selection ─────────────
        Label diffLabel = sectionLabel("Choose difficulty:");

        ToggleGroup diffGroup = new ToggleGroup();
        RadioButton rbEasy = new RadioButton("Easy   — 3 Goblins");
        rbEasy.setToggleGroup(diffGroup);
        rbEasy.setSelected(true);
        rbEasy.setTextFill(Color.LIGHTGRAY);

        RadioButton rbMedium = new RadioButton("Medium — 1 Goblin + 1 Wolf, backup: 2 Wolves");
        rbMedium.setToggleGroup(diffGroup);
        rbMedium.setTextFill(Color.LIGHTGRAY);

        RadioButton rbHard = new RadioButton("Hard   — 2 Goblins, backup: 1 Goblin + 2 Wolves");
        rbHard.setToggleGroup(diffGroup);
        rbHard.setTextFill(Color.LIGHTGRAY);

        VBox diffBox = new VBox(5, diffLabel, rbEasy, rbMedium, rbHard);
        diffBox.setPadding(new Insets(5, 0, 10, 20));

        // ── Start button ─────────────────────
        Button startBtn = new Button("START BATTLE");
        startBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-size: 16; "
                + "-fx-padding: 10 30; -fx-cursor: hand;");
        startBtn.setOnAction(e -> {
            // Build player
            Goblin.resetCounter();
            Wolf.resetCounter();

            if (rbWarrior.isSelected()) {
                player = new Warrior();
            } else {
                player = new Wizard();
            }

            player.addItem(createItem(item1.getSelectionModel().getSelectedIndex()));
            player.addItem(createItem(item2.getSelectionModel().getSelectedIndex()));

            if (rbEasy.isSelected()) difficulty = LevelConfig.Difficulty.EASY;
            else if (rbMedium.isSelected()) difficulty = LevelConfig.Difficulty.MEDIUM;
            else difficulty = LevelConfig.Difficulty.HARD;

            showBattleScreen();
        });

        root.getChildren().addAll(title, classBox, itemBox, diffBox, startBtn);

        primaryStage.setScene(new Scene(root, 700, 520));
    }

    private Item createItem(int index) {
        switch (index) {
            case 0: return new Potion();
            case 1: return new PowerStone();
            case 2: return new SmokeBomb();
            default: return new Potion();
        }
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        l.setTextFill(Color.web("#0f3460"));
        l.setTextFill(Color.web("#e94560"));
        return l;
    }

    // ═══════════════════════════════════════════════════
    //  BATTLE SCREEN
    // ═══════════════════════════════════════════════════

    private void showBattleScreen() {
        LevelConfig level = LevelFactory.createLevel(difficulty);
        TurnOrderStrategy strategy = new SpeedBasedTurnOrder();

        // Create the battle UI controller
        GUIBattleController controller = new GUIBattleController(primaryStage, player, level, strategy, this);
        controller.show();
    }

    /**
     * Called by GUIBattleController when battle ends and user wants a new game.
     */
    public void returnToSetup() {
        showSetupScreen();
    }

    // ═══════════════════════════════════════════════════

    public static void main(String[] args) {
        launch(args);
    }
}
