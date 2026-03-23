package entities;

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

    public void useItem(int index, List<interfaces.ICombatant> enemies){
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
    public void useSpecialSkill(List<interfaces.ICombatant> targets){
        executeSkillEffect(targets);
        startSkillCooldown();
    }

    public abstract void executeSkillEffect(List<interfaces.ICombatant> targets);


    public abstract String getSkillName();

}