package fr.ethan.embuscade.Enums;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.Random;

public class Bonus {

    private BonusType bonusType;

    public Bonus(BonusType bonusType) {
        this.bonusType = bonusType;
    }

    public Bonus(BonusType bonusType, Team team) {
        for(BonusType bonus : BonusType.values())
            if(new Random().nextInt(3) == 1 && (BonusType.getTeam().equals(team.getName()))) {
                this.bonusType = bonus;
                break;
            }
    }

    public ItemStack generate() {
        return bonusType.getItemStack();
    }

    public void giveBonus(Player player) {

    }
}