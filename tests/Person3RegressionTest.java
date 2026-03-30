import actions.BasicAttack;
import actions.DefendAction;
import actions.ItemAction;
import actions.SpecialSkillAction;
import engine.BattleEngine;
import engine.LevelConfig;
import engine.SpeedBasedTurnOrder;
import entities.Goblin;
import entities.Player;
import entities.Warrior;
import entities.Wizard;
import entities.Wolf;
import interfaces.IAction;
import interfaces.ICombatant;
import interfaces.IItem;
import items.Potion;
import items.PowerStone;
import items.SmokeBomb;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Person3RegressionTest {

    private static final List<String> FAILURES = new ArrayList<>();

    public static void main(String[] args) {
        runTest("Shield Bash stuns an enemy that has not acted yet", Person3RegressionTest::testStunOnUnactedEnemy);
        runTest("Shield Bash stuns an enemy that already acted this round", Person3RegressionTest::testStunOnAlreadyActedEnemy);
        runTest("Defend lasts for current and next round only", Person3RegressionTest::testDefendDuration);
        runTest("Smoke Bomb blocks damage and inventory empties after item use", Person3RegressionTest::testSmokeBombAndInventory);
        runTest("Potion heals but never exceeds max HP", Person3RegressionTest::testPotionCap);
        runTest("Power Stone triggers Warrior skill without changing cooldown", Person3RegressionTest::testPowerStoneWarriorCooldown);
        runTest("Power Stone triggers Wizard skill and preserves Arcane Blast scaling", Person3RegressionTest::testPowerStoneWizardScaling);

        if (!FAILURES.isEmpty()) {
            System.err.println();
            System.err.printf("%d test(s) failed:%n", FAILURES.size());
            for (String failure : FAILURES) {
                System.err.println("  - " + failure);
            }
            System.exit(1);
        }

        System.out.println();
        System.out.println("All Person 3 regression tests passed.");
    }

    private static void runTest(String name, CheckedRunnable test) {
        try {
            test.run();
            System.out.println("PASS: " + name);
        } catch (Throwable t) {
            String message = t.getMessage() == null ? t.getClass().getSimpleName() : t.getMessage();
            FAILURES.add(name + " -> " + message);
            System.err.println("FAIL: " + name);
        }
    }

    private static void testStunOnUnactedEnemy() {
        runMuted(() -> {
            Warrior warrior = new Warrior(List.of());
            LevelConfig level = new LevelConfig("test", List.of(new Goblin("A")), List.of());
            BattleEngine engine = new BattleEngine(warrior, level, new SpeedBasedTurnOrder());
            ScriptedActionProvider provider = new ScriptedActionProvider(List.of(
                    new SpecialSkillAction(),
                    new SpecialSkillAction(),
                    new SpecialSkillAction()));

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP, warrior.getHp(),
                    "Goblin should skip its turn in the round Shield Bash is applied.");

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP, warrior.getHp(),
                    "Goblin should also skip its next turn after being stunned.");

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP - 15, warrior.getHp(),
                    "Goblin should attack again after the stun expires.");
        });
    }

    private static void testStunOnAlreadyActedEnemy() {
        runMuted(() -> {
            Warrior warrior = new Warrior(List.of());
            LevelConfig level = new LevelConfig("test", List.of(new Wolf("A")), List.of());
            BattleEngine engine = new BattleEngine(warrior, level, new SpeedBasedTurnOrder());
            ScriptedActionProvider provider = new ScriptedActionProvider(List.of(
                    new SpecialSkillAction(),
                    new SpecialSkillAction(),
                    new SpecialSkillAction()));

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP - 25, warrior.getHp(),
                    "Wolf should attack before Warrior can stun it.");

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP - 25, warrior.getHp(),
                    "A faster enemy that already acted should only lose one future turn to stun.");

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP - 50, warrior.getHp(),
                    "Wolf should act again after the single skipped turn.");
        });
    }

    private static void testDefendDuration() {
        runMuted(() -> {
            Warrior warrior = new Warrior(List.of());
            LevelConfig level = new LevelConfig("test", List.of(new Goblin("A")), List.of());
            BattleEngine engine = new BattleEngine(warrior, level, new SpeedBasedTurnOrder());
            ScriptedActionProvider provider = new ScriptedActionProvider(List.of(
                    new DefendAction(),
                    new BasicAttack(),
                    new BasicAttack()));

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP - 5, warrior.getHp(),
                    "Defend should reduce damage during the round it is used.");

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP - 10, warrior.getHp(),
                    "Defend should still reduce damage in the next round.");

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP - 25, warrior.getHp(),
                    "Defend should expire before the following enemy attack.");
        });
    }

    private static void testSmokeBombAndInventory() {
        runMuted(() -> {
            List<IItem> items = new ArrayList<>();
            items.add(new SmokeBomb());
            items.add(new Potion());
            Warrior warrior = new Warrior(items);
            LevelConfig level = new LevelConfig("test", List.of(new Goblin("A")), List.of());
            BattleEngine engine = new BattleEngine(warrior, level, new SpeedBasedTurnOrder());
            ScriptedActionProvider provider = new ScriptedActionProvider(List.of(
                    new ItemAction(0),
                    new ItemAction(0),
                    new BasicAttack()));

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP, warrior.getHp(),
                    "Smoke Bomb should block damage in the same round it is used.");

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP, warrior.getHp(),
                    "Smoke Bomb should continue blocking damage in the next round.");
            assertFalse(warrior.hasItemsLeft(),
                    "Inventory should be empty after the Smoke Bomb and Potion are both consumed.");
            assertFalse(new ItemAction(0).isAvailable(warrior),
                    "ItemAction should become unavailable when no items remain.");

            engine.runRound(provider);
            assertEquals(Warrior.WARRIOR_HP - 15, warrior.getHp(),
                    "Smoke Bomb should expire after protecting one additional round.");
        });
    }

    private static void testPotionCap() {
        runMuted(() -> {
            Warrior warrior = new Warrior(List.of());
            Potion potion = new Potion();

            warrior.takeDamage(50);
            potion.use(warrior, List.of());

            assertEquals(Warrior.WARRIOR_HP, warrior.getHp(),
                    "Potion should heal up to max HP only.");
        });
    }

    private static void testPowerStoneWarriorCooldown() {
        runMuted(() -> {
            Warrior warrior = new Warrior(List.of());
            Goblin goblin = new Goblin("A");
            Wolf wolf = new Wolf("A");

            warrior.useSpecialSkill(List.of(goblin));
            assertEquals(3, warrior.getSkillCooldown(),
                    "Using Shield Bash normally should start the cooldown.");

            new PowerStone().use(warrior, List.of(wolf));
            assertEquals(3, warrior.getSkillCooldown(),
                    "Power Stone should not modify the Warrior's cooldown.");
            assertEquals(5, wolf.getHp(),
                    "Power Stone should still apply Shield Bash damage.");
            assertTrue(wolf.isStunned(),
                    "Power Stone-triggered Shield Bash should apply stun.");
        });
    }

    private static void testPowerStoneWizardScaling() {
        runMuted(() -> {
            Wizard wizard = new Wizard(List.of());
            Wolf firstWaveWolf = new Wolf("A");
            Goblin firstWaveGoblin = new Goblin("A");

            wizard.useSpecialSkill(List.of(firstWaveWolf, firstWaveGoblin));
            assertEquals(3, wizard.getSkillCooldown(),
                    "Using Arcane Blast normally should start the cooldown.");
            assertEquals(60, wizard.getAttack(),
                    "Wizard should gain +10 ATK for a kill during Arcane Blast.");

            Wolf wolfA = new Wolf("B");
            Wolf wolfB = new Wolf("C");
            new PowerStone().use(wizard, List.of(wolfA, wolfB));

            assertEquals(3, wizard.getSkillCooldown(),
                    "Power Stone should not modify the Wizard's cooldown.");
            assertFalse(wolfA.isAlive(), "Power Stone Arcane Blast should defeat the first wolf.");
            assertFalse(wolfB.isAlive(), "Power Stone Arcane Blast should defeat the second wolf.");
            assertEquals(80, wizard.getAttack(),
                    "Wizard ATK should increase sequentially for each Arcane Blast kill.");
        });
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void runMuted(CheckedRunnable runnable) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        PrintStream mutedOut = new PrintStream(sink);
        try {
            System.setOut(mutedOut);
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            mutedOut.close();
            System.setOut(originalOut);
            try {
                sink.close();
            } catch (Exception ignored) {
                // Nothing to do.
            }
        }
    }

    @FunctionalInterface
    private interface CheckedRunnable {
        void run() throws Exception;
    }

    private static final class ScriptedActionProvider implements BattleEngine.ActionProvider {
        private final List<IAction> actions;
        private int actionIndex = 0;

        private ScriptedActionProvider(List<IAction> actions) {
            this.actions = actions;
        }

        @Override
        public IAction getPlayerAction(Player player, List<ICombatant> livingEnemies) {
            int index = Math.min(actionIndex, actions.size() - 1);
            IAction action = actions.get(index);
            actionIndex++;
            return action;
        }

        @Override
        public List<ICombatant> getTargets(Player player, List<ICombatant> livingEnemies, IAction action) {
            if (action instanceof DefendAction || livingEnemies.isEmpty()) {
                return List.of();
            }
            return List.of(livingEnemies.get(0));
        }
    }
}
