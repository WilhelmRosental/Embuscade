package fr.ethan.embuscade.GameElements;

import fr.ethan.embuscade.Enums.Role;
import fr.ethan.embuscade.Mains.Embuscade;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class EmbuscadePlayer {
    private Team team;
    private Embuscade plugin;
    private Player player;
    private Role role;

    public EmbuscadePlayer(Player player, Embuscade plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return this.role;
    }

    public boolean hasRole() {
        return this.role != null;
    }

    public static void clearPotionEffects(Player p) {
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
    }

    public static void heal(Player p) {
        p.setFoodLevel(20);
        p.setHealth(20);
    }

    public Player getPlayer() {
        return player;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public boolean hasTeam(){
        return team != null;
    }
}