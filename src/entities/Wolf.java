package entities;

import strategies.DefensiveStrategy;

public class Wolf extends Enemy {

    private static final int wolfHp = 40;
    private static final int wolfAttack = 45;
    private static final int wolfDefense = 5;
    private static final int wolfSpeed = 35;

    public Wolf(String label) {
        super("Wolf " + label, wolfHp, wolfAttack, wolfDefense, wolfSpeed,
              new DefensiveStrategy());
    }

    @Override
    public String getEnemyType() {
        return "Wolf";
    }
}