package combatarena.entity;

import java.util.List;

/**
 * Concrete Wizard — Person 1 owns this. Stub for integration testing.
 * Stats: HP:200, ATK:50, DEF:10, SPD:20
 */
public class Wizard extends Player {

    public Wizard() {
        super("Wizard", 200, 50, 10, 20);
    }

    @Override
    public void useSpecialSkill(List<ICombatant> targets) {
        // Arcane Blast — Person 2 implements the real logic.
        executeSkillEffect(targets);
        setSpecialSkillCooldown(3);
    }

    @Override
    public void executeSkillEffect(List<ICombatant> targets) {
        // Arcane Blast effect: damage all enemies, +10 ATK per kill.
        // Placeholder: Person 2 will flesh this out.
        for (ICombatant target : targets) {
            int damage = Math.max(0, this.getAttack() - target.getDefense());
            target.takeDamage(damage);
            if (!target.isAlive()) {
                this.setAttack(this.getAttack() + 10);
            }
        }
    }
}
