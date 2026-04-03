package combatarena.entity;

import combatarena.item.Item;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract player — Person 1 owns the full implementation.
 * Stub here so BattleEngine can reference Player-specific features.
 */
public abstract class Player extends Combatant {

    protected List<Item> inventory;
    protected int specialSkillCooldown;

    public Player(String name, int hp, int attack, int defense, int speed) {
        super(name, hp, attack, defense, speed);
        this.inventory = new ArrayList<>();
        this.specialSkillCooldown = 0;
    }

    // ── Inventory ────────────────────────────────────────────────────

    public List<Item> getInventory() {
        return inventory;
    }

    public void addItem(Item item) {
        if (inventory.size() < 2) {
            inventory.add(item);
        }
    }

    public boolean hasItems() {
        return !inventory.isEmpty();
    }

    public void removeItem(Item item) {
        inventory.remove(item);
    }

    // ── Special Skill ────────────────────────────────────────────────

    public int getSpecialSkillCooldown() {
        return specialSkillCooldown;
    }

    public void setSpecialSkillCooldown(int cd) {
        this.specialSkillCooldown = cd;
    }

    public void decrementCooldown() {
        if (specialSkillCooldown > 0) {
            specialSkillCooldown--;
        }
    }

    public boolean isSkillReady() {
        return specialSkillCooldown == 0;
    }

    /**
     * Execute the full special skill (with cooldown management).
     * Person 2's SpecialSkillAction will call this.
     */
    public abstract void useSpecialSkill(List<ICombatant> targets);

    /**
     * Execute only the skill *effect* — no cooldown change.
     * Used by PowerStone (Person 3).
     */
    public abstract void executeSkillEffect(List<ICombatant> targets);
}
