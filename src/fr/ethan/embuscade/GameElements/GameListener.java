package fr.ethan.embuscade.GameElements;

import fr.ethan.embuscade.Enums.GameState;
import fr.ethan.embuscade.Mains.Embuscade;

import fr.ethan.embuscade.Utils.Broadcast;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.*;

public class GameListener implements Listener {
    private Embuscade plugin = Embuscade.plugin;
    
    public GameListener(){
       this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    //GESTION DES EVENEMENTS BASIQUES DE GAMEPLAY
    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent event) {
    	Player player = null;
    	RegainReason reason = event.getRegainReason();
    	if(event.getEntity() instanceof Player) {
    		player = Bukkit.getPlayerExact(event.getEntity().getName());
    	}
    	else {
    		return;
    	}
    	//si joueur est en jeu et raison est satiete
        if(Embuscade.gamecycle.state == GameState.GAME) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLvlChange(FoodLevelChangeEvent event){
    	Player player = null;
    	if(event.getEntity() instanceof Player) {
    		player = Bukkit.getPlayerExact(event.getEntity().getName());
    	}
    	else {
    		return;
    	}
    	//si joueur est en jeu et foodlevel est au max
        if(Embuscade.gamecycle.state == GameState.GAME) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
    	Player player = event.getPlayer();
    	//si joueur est en jeu
    	if(true/*joueur est en jeu*/) {
    		event.setCancelled(true);
    	}
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
    	Player player = null;
    	if(event.getEntity() instanceof Player) {
    		player = Bukkit.getPlayerExact(event.getEntity().getName());
    	}
    	else {
    		return;
    	}
    	//si joueur est en jeu
    	if(true/*joueur est en jeu*/) {
    		event.setCancelled(true);
    	}
    }
    
    //GESTION DES INTERRACTIONS JOUEURS
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();//entité ayant frappé
        Entity ent = event.getEntity();//entité ayant été frappée

        if(damager instanceof Player && ent instanceof Player) {//si les deux entités sont des joeurs on vérifie qu'elles sont dans une partie
            if(Embuscade.gamecycle.state != null && Embuscade.gamecycle != null) {
                if(!(Embuscade.gamecycle.seekers == null) && !(Embuscade.gamecycle.hiders == null)) {
                    if(Embuscade.gamecycle.seekers.hasMember(Bukkit.getPlayerExact(event.getDamager().getName())) && Embuscade.gamecycle.hiders.hasMember(Bukkit.getPlayerExact(event.getEntity().getName())) && Embuscade.gamecycle.state == GameState.GAME && Embuscade.gamecycle.getTime() <= Embuscade.gamecycle.gameTime - 60){//si c'est le cas on notifie au joueur frappé qu'il a été trouvé et au joueur frappant qu'il l'a trouvé ainsi qu'au reste des joueurs
                        Bukkit.getPlayerExact(event.getEntity().getName()).sendMessage(ChatColor.YELLOW + "Tu as été trouvé par "+ ChatColor.RED + event.getDamager().getName());
                        Bukkit.getPlayerExact(event.getDamager().getName()).sendMessage(ChatColor.YELLOW + "Tu as trouvé "+ ChatColor.BLUE + event.getEntity().getName());
                        Broadcast.broadcaster(ChatColor.BLUE + "" + Bukkit.getPlayerExact(event.getEntity().getName()).getName() + ChatColor.YELLOW + " as été trouvé par " + ChatColor.RED + Bukkit.getPlayerExact(event.getDamager().getName()).getName() + ChatColor.YELLOW + ".");

                        Embuscade.gamecycle.hiders.removePlayer(Bukkit.getPlayerExact(ent.getName()));

                        if(Embuscade.gamecycle.hiders.isEmpty()) {//si tous les hiders on été trouvés on le notifie aux joueurs, on vide les listes restantes et on arrete le chronomètre
                            Broadcast.broadcaster(ChatColor.YELLOW + " Tous les hiders ont été trouvé, fin de la partie !");
                            Embuscade.gamecycle.cancelGame();
                        } else {
                            Embuscade.tpToLobby(Bukkit.getPlayerExact(ent.getName()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

    }
    
    //GESTION DES MOUVEMENTS ET DES POSITIONS
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
    	
    }
    
    //GESTION DES MORTS ET DES RESPAWN
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Entity ent = event.getEntity();
        Player killer = getKiller(event.getDeathMessage());
        Player player = null;
        if(ent instanceof Player) {
            player = Bukkit.getPlayerExact(ent.getName());
        }

        event.setDeathMessage(null);

        if((killer != null && Embuscade.gamecycle.playerList.contains(killer) ) && (Embuscade.gamecycle.playerList.contains(player))) {
            if(Embuscade.gamecycle.hiders.hasMember(killer)) {
                player.sendMessage(ChatColor.YELLOW + "Tu as été tué par " + ChatColor.BLUE + killer.getName());
                Broadcast.broadcaster(ChatColor.YELLOW + player.getName() + ChatColor.RED + " as été tué par "  + ChatColor.BLUE + killer.getName());

                Embuscade.gamecycle.seekers.removePlayer(player);

                if(Embuscade.gamecycle.seekers.isEmpty()) {
                    Broadcast.broadcaster(ChatColor.YELLOW + "Tous les " + ChatColor.RED + "seekers" + ChatColor.YELLOW + "ont été éliminés, Les " + ChatColor.BLUE + "hiders" + ChatColor.YELLOW + " gagnent la partie.");
                    Embuscade.gamecycle.cancelGame();
                }
            }
            else if(Embuscade.gamecycle.seekers.hasMember(killer)) {
                player.sendMessage(ChatColor.YELLOW + "Tu as été tué par " + ChatColor.RED + killer.getName());
                Broadcast.broadcaster(ChatColor.YELLOW + player.getName() + ChatColor.BLUE + " as été tué par "  + ChatColor.RED + killer.getName());

                Embuscade.gamecycle.hiders.removePlayer(player);

                if(Embuscade.gamecycle.hiders.isEmpty()) {
                    Broadcast.broadcaster(ChatColor.YELLOW + "Tous les" + ChatColor.BLUE + "hiders" + ChatColor.YELLOW + "ont été éliminés, Les " + ChatColor.RED + "seekers" + ChatColor.YELLOW + " gagnent la partie.");
                    Embuscade.gamecycle.cancelGame();
                }
            }
        }
        else {
            return;
        }
    }

    public Player getKiller(String msg) { //TODO : à revoir
        String buffer = "";
        for(int i = 0;i < msg.length();i++) {
            buffer += msg.charAt(i);
            if(msg.charAt(i) == ' ') {
                buffer = "";
            }
        }
        return Bukkit.getPlayerExact(buffer);
    }

    @EventHandler
    public void onRespawn(EntityResurrectEvent event) {
    	
    }
}
