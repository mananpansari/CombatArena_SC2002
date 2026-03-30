package entities;

import effects.StunEffect;
import interfaces.ICombatant;
import interfaces.IItem;
import java.util.List;

public class Warrior extends Player{
    public static final int WARRIOR_HP = 260;
    public static final int WARRIOR_ATTACK = 40;
    public static final int WARRIOR_DEFENSE = 20;
    public static final int WARRIOR_SPEED = 30;

    // Constructor
    public Warrior(List<IItem> items){
        super("Warrior", WARRIOR_HP, WARRIOR_ATTACK, WARRIOR_DEFENSE, WARRIOR_SPEED, items);
    }
    @Override
    public void executeSkillEffect(List<ICombatant> targets){
        if (targets == null || targets.isEmpty()){
            System.out.println("  No valid targets for Shield Bash.");
            return;
        }

        ICombatant target = targets.get(0);
        // Deal basic attack damage
        int damage = Math.max(0, this.getAttack() - target.getDefense());
        target.takeDamage(damage);

        System.out.printf("  %s -> Shield Bash -> %s: %d damage%n",
            this.getName(), target.getName(), damage);

        target.applyStatusEffect(new StunEffect());
        System.out.printf("  %s is STUNNED%n", target.getName());
    }

    @Override
    public String getSkillName(){
        return "Shield Bash";
    }

    // Display
    public static void printClassInfo(){
        System.out.println("  +------------------------------------+");
        System.out.println("  | WARRIOR                            |");
        System.out.println("  | HP: 260  ATK: 40  DEF: 20  SPD: 30 |");
        System.out.println("  | Skill: Shield Bash (Cooldown: 3)   |");
        System.out.println("  | Deal BasicAttack to one            |");
        System.out.println("  | enemy + stun for 2 turns.          |");
        System.out.println("  +------------------------------------+");
    }
}
