package entities;

public class Goblin extends Enemy {

    private static final int GOBLIN_HP = 55;
    private static final int GOBLIN_ATTACK = 35;
    private static final int GOBLIN_DEFENSE = 15;
    private static final int GOBLIN_SPEED = 25;


    public Goblin(String label){
    super("Goblin " + label, GOBLIN_HP, GOBLIN_ATTACK, GOBLIN_DEFENSE, GOBLIN_SPEED);
    }

    @Override
    public String getEnemyType(){
        return "Goblin";
    }
}