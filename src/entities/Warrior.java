package entities;

import effects.StunEffect;
import interfaces.ICombatant;
import interfaces.IItem;
import java.util.List;

public class Warrior extends Player {
    public static final int warriorHp = 260;
    public static final int warriorAttack = 40;
    public static final int warriorDefense = 20;
    public static final int warriorSpeed = 30;

    // Constructor
    public Warrior(List<IItem> items) {
        super("Warrior", warriorHp, warriorAttack, warriorDefense, warriorSpeed, items);
    }

    @Override
    public void executeSkillEffect(List<ICombatant> targets) {
        if (targets == null || targets.isEmpty()) {
            System.out.println("  No valid targets for Shield Bash.");
            return;
        }

        ICombatant target = targets.get(0);
        // Deal basic attack damage
        int damage = Math.max(0, this.getAttack() - target.getDefense());
        target.takeDamage(damage);

        System.out.printf("  %s -> Shield Bash -> %s  (%d damage)%n",
                this.getName(), target.getName(), damage);

        if (target.isAlive()) { 
        target.applyStatusEffect(new StunEffect());
        System.out.printf("    %s is now stunned (2 turns)%n", target.getName());
}
    }

    @Override
    public String getSkillName() {
        return "Shield Bash";
    }
}
