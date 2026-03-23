package entities;

import interfaces.ICombatant;
import java.util.List;

public abstract class Enemy extends Combatant{
    // Constructor
    protected Enemy(String name, int hp, int attack, int defense, int speed){
        super(name, hp, attack, defense, speed);
    }

    public void takeTurn(ICombatant player){
        int rawDamage = this.getAttack() - player.getDefense();
        int damage = Math.max(0, rawDamage);

        // To check if plater is protected by Smokebomb
        if(player.hasSmokeBombActive()){
            damage = 0;
        }

        System.out.printf("  %s -> BasicAttack -> %s: %d damage%n",
            this.getName(), player.getName(), damage);

            player.takeDamage(damage);
        }

        public abstract String getEnemyType();

        public void printStats(){
            System.out.printf("  %-8s HP:%-4d ATK:%-4d DEF:%-4d SPD:%-4d%n",
                getEnemyType(),
                getMaxHp(),
                getAttack(),
                getDefense(),
                getSpeed());
        }




}