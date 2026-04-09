package entities;

import strategies.AggressiveStrategy;

public class Goblin extends Enemy {

    private static final int goblinHp = 55;
    private static final int goblinAttack = 35;
    private static final int goblinDefense = 15;
    private static final int goblinSpeed = 25;

    public Goblin(String label) {
        super("Goblin " + label, goblinHp, goblinAttack, goblinDefense, goblinSpeed,
              new AggressiveStrategy());
    }

    @Override
    public String getEnemyType() {
        return "Goblin";
    }
}