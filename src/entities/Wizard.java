package entities;

import interfaces.ICombatant;
import interfaces.IItem;
import java.util.List;

public class Wizard extends Player{

    private static final int WIZARD_HP = 200;
    private static final int WIZARD_ATTACK = 50;
    private static final int WIZARD_DEFENSE = 10;
    private static final int WIZARD_SPEED = 20;

    private int arcaneBlastBonusAtk = 0;

    public Wizard(List<IItem> items){
        super("Wizard", WIZARD_HP,WIZARD_ATTACK, WIZARD_DEFENSE, WIZARD_SPEED, items);
    }


    @Override
    public void executeSkillEffect(List<ICombatant> targets){
        if (targets == null || targets.isEmpty()){
            System.out.println("  No targets for Arcane Blast.");
            return;
        }

        System.out.printf("  %s -> Arcane Blast -> All Enemies (ATK: %d):%n",
        this.getName(), this.getAttack());

        for (ICombatant target : targets){
            if(!target.isAlive()){
                continue; // skip already-dead enemies
            }

            // Damages uses CURRENT attack (may have increased from earlier kills this blast)
            int damage = Math.max(0, this.getAttack() - target.getDefense());
            target.takeDamage(damage);

            System.out.printf("    %s HP: %d -> %d (dmg: %d-%d=%d)",
            target.getName(),
            target.getHp() + damage, // HP before damage
            target.getHp(),          // HP after damage
            this.getAttack(),
            target.getDefense(),
            damage);

            // If this kill was the finishing blow, grant +10 ATK
            if(!target.isAlive()){
                grantArcaneBlastKillBonus();
                System.out.printf(" ELIMINATED | ATK: %d -> %d (+10)%n",
                this.getAttack() - 10,
                this.getAttack());
            } else {
                System.out.printf(" (survived)%n");
            }
        }
    }

    private void grantArcaneBlastKillBonus(){
        arcaneBlastBonusAtk += 10;
        setAttack(this.getAttack() + 10);
    }

    public int getArcaneBlastBonusAtk(){
        return arcaneBlastBonusAtk;
    }

    @Override
    public String getSkillName(){
        return "Arcane Blast";
    }

    public static void printClassInfo(){
        System.out.println("  +------------------------------------+");
        System.out.println("  | WIZARD                             |");
        System.out.println("  | HP: 200  ATK: 50  DEF: 10  SPD: 20 |");
        System.out.println("  | Skill: Arcane Blast (Cooldown: 3)  |");
        System.out.println("  | Deal BasicAttack to ALL enemies.   |");
        System.out.println("  | +10 ATK per kill (lasts level).    |");
        System.out.println("  +------------------------------------+");
    }
}