package entities;

public class Wolf extends Enemy{

    private static final int WOLF_HP = 40;
    private static final int WOLF_ATTACK = 45;
    private static final int WOLF_DEFENSE = 5;
    private static final int WOLF_SPEED = 35;

    

    public Wolf(String label){
        super("Wolf " + label, WOLF_HP, WOLF_ATTACK, WOLF_DEFENSE, WOLF_SPEED);
    }

    @Override
    public String getEnemyType(){
        return "Wolf";
    }
}