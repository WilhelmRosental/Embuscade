package fr.ethan.embuscade.Enums;

import fr.ethan.embuscade.GameElements.EmbuscadePlayer;
import org.bukkit.entity.Player;

public enum Role {

    TRAPPER("Piègeur"),
    RUNNER("Coureur"),
    DEFENDER("Défenseur"), //TODO : voir pour un autre nom ?

    //SEEKERS
    DEMOLISHER("Démolisseur"),
    SCOUT("Éclaireur"),
    HUNTER("Chasseur");

    private String name;

    Role(String name) { this.name = name; }

    public void equip(EmbuscadePlayer cachecachePlayer){
        Player player = cachecachePlayer.getPlayer();
        EmbuscadePlayer.heal(player);
    }
}
