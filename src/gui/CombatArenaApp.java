package gui;

import engine.LevelConfig;
import engine.SpeedBasedTurnOrder;
import entities.*;
import interfaces.*;
import items.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX GUI entry point for the Combat Arena game.
 * Built on top of the UML class diagram — uses the flat-package structure.
 */
public class CombatArenaApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Combat Arena");
        stage.setMinWidth(720);
        stage.setMinHeight(540);

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

        String[] itemNames = {
            "Potion — Heal 100 HP",
            "Power Stone — Trigger skill (no CD)",
            "Smoke Bomb — Nullify damage 2 turns",
            "Chronos Hourglass — Reverse time and undo the last action"
        };
        ComboBox<String> item1 = new ComboBox<>();
        item1.getItems().addAll(itemNames);
        item1.getSelectionModel().selectFirst();
        item1.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        ComboBox<String> item2 = new ComboBox<>();
        item2.getItems().addAll(itemNames);
        item2.getSelectionModel().selectFirst();
        item2.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        HBox itemRow = new HBox(10,
                new Label("Item 1:") {{ setTextFill(Color.LIGHTGRAY); }}, item1,
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
            // Build items
            List<IItem> items = new ArrayList<>();
            items.add(createItem(item1.getSelectionModel().getSelectedIndex()));
            items.add(createItem(item2.getSelectionModel().getSelectedIndex()));

            // Build player
            Player player;
            if (rbWarrior.isSelected()) {
                player = new Warrior(items);
            } else {
                player = new Wizard(items);
            }

            // Build level config
            LevelConfig level;
            if (rbEasy.isSelected()) {
                level = new LevelConfig("Easy",
                    List.of(new Goblin("A"), new Goblin("B"), new Goblin("C")),
                    List.of());
            } else if (rbMedium.isSelected()) {
                // Medium: Wolf B gets a Power Ring (+5 ATK) via Decorator
                Wolf wolfB = new Wolf("B");
                interfaces.ICombatant equippedWolf = new PowerRingDecorator(wolfB);
                level = new LevelConfig("Medium",
                    List.of(new Goblin("A"), new Wolf("A")),
                    List.of(equippedWolf, new Wolf("C")));
            } else {
                // Hard: Goblin B is a healer (SupportStrategy)
                Goblin supportGoblin = new Goblin("B");
                supportGoblin.setStrategy(new strategies.SupportStrategy());
                // Backup: armored goblin (+10 DEF) and elite wolf (+5 ATK, +10 DEF)
                interfaces.ICombatant armoredGoblin = new IronArmorDecorator(new Goblin("C"));
                interfaces.ICombatant eliteWolf = new PowerRingDecorator(new IronArmorDecorator(new Wolf("A")));
                level = new LevelConfig("Hard",
                    List.of(new Goblin("A"), supportGoblin),
                    List.of(armoredGoblin, eliteWolf));
            }

            showBattleScreen(player, level);
        });

        root.getChildren().addAll(title, classBox, itemBox, diffBox, startBtn);
        primaryStage.setScene(new Scene(root, 720, 540));
    }

    private IItem createItem(int index) {
        switch (index) {
            case 0: return new Potion();
            case 1: return new PowerStone();
            case 2: return new SmokeBomb();
            case 3: return new items.ChronosHourglass();
            default: return new Potion();
        }
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        l.setTextFill(Color.web("#e94560"));
        return l;
    }

    // ═══════════════════════════════════════════════════
    //  BATTLE SCREEN
    // ═══════════════════════════════════════════════════

    private void showBattleScreen(Player player, LevelConfig level) {
        GUIBattleController controller = new GUIBattleController(
                primaryStage, player, level, new SpeedBasedTurnOrder(), this);
        controller.show();
    }

    /**
     * Called by GUIBattleController when battle ends and user wants a new game.
     */
    public void returnToSetup() {
        showSetupScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
