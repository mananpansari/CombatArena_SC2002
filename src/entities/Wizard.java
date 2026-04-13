package entities;

import interfaces.ICombatant;
import interfaces.IItem;
import java.util.List;

public class Wizard extends Player {

    private static final int wizardHp = 200;
    private static final int wizardAttack = 50;
    private static final int wizardDefense = 10;
    private static final int wizardSpeed = 20;

    private int arcaneBlastBonusAtk = 0;

    public Wizard(List<IItem> items) {
        super("Wizard", wizardHp, wizardAttack, wizardDefense, wizardSpeed, items);
    }

    @Override
    public void executeSkillEffect(List<ICombatant> targets) {
        if (targets == null || targets.isEmpty()) {
            System.out.println("  No targets for Arcane Blast.");
            return;
        }

        System.out.printf("  %s -> Arcane Blast -> All Enemies%n",
                this.getName());

        for (ICombatant target : targets) {
            if (!target.isAlive()) {
                continue; // skip already-dead enemies
            }

            // Damages uses CURRENT attack (may have increased from earlier kills this
            // blast)
            int damage = Math.max(0, this.getAttack() - target.getDefense());
            target.takeDamage(damage);

            System.out.printf("    -> %s  (%d damage, HP: %d -> %d)",
                    target.getName(),
                    damage,
                    target.getHp() + damage,
                    target.getHp());

            // If this kill was the finishing blow, grant +10 ATK
            if (!target.isAlive()) {
                grantArcaneBlastKillBonus();
                System.out.printf(" -> ELIMINATED  (ATK +10 -> %d)%n",
                        this.getAttack());
            } else {
                System.out.printf("%n");
            }
        }
    }

    private void grantArcaneBlastKillBonus() {
        arcaneBlastBonusAtk += 10;
        setAttack(this.getAttack() + 10);
    }

    public int getArcaneBlastBonusAtk() {
        return arcaneBlastBonusAtk;
    }

    @Override
    public String getSkillName() {
        return "Arcane Blast";
    }
}