package combatarena.entity;

import combatarena.effect.StunEffect;
import java.util.List;

/**
 * Concrete Warrior — Stats: HP:260, ATK:40, DEF:20, SPD:30
 * Special skill: Shield Bash — deal BasicAttack damage to one enemy
 * and apply a 2-turn Stun.
 */
public class Warrior extends Player {

    public Warrior() {
        super("Warrior", 260, 40, 20, 30);
    }

    @Override
    public void useSpecialSkill(List<ICombatant> targets) {
        executeSkillEffect(targets);
        setSpecialSkillCooldown(3);
    }

    @Override
    public void executeSkillEffect(List<ICombatant> targets) {
        if (!targets.isEmpty()) {
            ICombatant target = targets.get(0);
            int damage = Math.max(0, this.getAttack() - target.getDefense());
            target.takeDamage(damage);
            if (target.isAlive()) {
                target.applyStatusEffect(new StunEffect(2));
            }
        }
    }
}
