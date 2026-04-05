package entities;

import actions.BasicAttack;
import actions.ItemAction;
import actions.SpecialSkillAction;
import interfaces.IAction;
import interfaces.ICombatant;
import interfaces.IItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Player extends Combatant{
    private int skillCooldown;
    private final List<IItem> inventory;
    protected Player(String name, int hp, int attack, int defense, int speed, List<IItem> items){
        super(name, hp, attack, defense, speed);
        this.inventory = new ArrayList<>(items);
        this.skillCooldown = 0;
    }

    public List<IItem> getInventory(){
        return Collections.unmodifiableList(inventory);
    }

    public boolean hasItemsLeft(){
        return !inventory.isEmpty();
    }

    public void useItem(int index, List<ICombatant> enemies){
        if(index < 0 || index >= inventory.size()){
            throw new IndexOutOfBoundsException("Invalid item index: " + index);
        }
        IItem item = inventory.remove(index);
        item.use(this, enemies);
    }

    // Special Skill Cooldown

    public int getSkillCooldown(){
        return skillCooldown;
    }

    /**
     * Alias for getSkillCooldown() – used by Boundary/GUI layers.
     */
    public int getSpecialSkillCooldown(){
        return skillCooldown;
    }

    public boolean isSkillReady(){
        return skillCooldown == 0;
    }

    public void startSkillCooldown(){
        skillCooldown = 3;
    }

    public void decrementCooldown(){
        if(skillCooldown > 0){
            skillCooldown--;
        }
    }

    // Special skill
    public void useSpecialSkill(List<ICombatant> targets){
        executeSkillEffect(targets);
        startSkillCooldown();
    }

    public abstract void executeSkillEffect(List<ICombatant> targets);

    public abstract String getSkillName();

    /**
     * Returns the list of available actions for this player.
     * Used by InputHandler and GUI to present choices.
     */
    public List<IAction> getActions(){
        List<IAction> actions = new ArrayList<>();
        actions.add(new BasicAttack());
        actions.add(new SpecialSkillAction());
        actions.add(new ItemAction(0));  // ItemAction with index 0 as default
        return actions;
    }

}