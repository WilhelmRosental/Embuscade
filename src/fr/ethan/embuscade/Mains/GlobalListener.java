package fr.ethan.embuscade.Mains;

import fr.ethan.embuscade.Enums.GameState;
import fr.ethan.embuscade.GameElements.EmbuscadePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Iterator;

public class GlobalListener implements Listener {
    private Embuscade plugin = Embuscade.plugin;

    public GlobalListener() {
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    //GESTION DES CONNEXIONS / DECONNEXIONS
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        if (p.getGameMode() != GameMode.ADVENTURE) {
            p.setGameMode(GameMode.ADVENTURE);
        }

        event.setJoinMessage(null);
        //TODO : générer un message aléatoire cool

        if (Embuscade.gamecycle != null && Embuscade.gamecycle.state != null) {
            if (!(Embuscade.gamecycle.lobbyPlayers.contains(p))) {
                Embuscade.gamecycle.lobbyPlayers.add(p);
                if(Embuscade.gamecycle.state == GameState.LOBBY) {
                    p.sendMessage("Une partie est sur le point de commencer.");
                } else if(Embuscade.gamecycle.state == GameState.GAME) {
                    p.sendMessage("Une partie est déjà en cours.");
                }

                if(Embuscade.gamecycle.lobbyPlayers.size() >= 2 && !Embuscade.gamecycle.started){
                    Embuscade.gamecycle.startLobby();
                }
            }
            Embuscade.tpToLobby(p);
            //TODO : on téléporte au lobby du config.yml sinon le point de spawn du monde par défaut ?
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        event.setQuitMessage(null);
        //TODO : générer un message aléatoire cool

        if(Embuscade.gamecycle != null) {
            if (Embuscade.gamecycle.playerList.contains(p)) {
                Embuscade.gamecycle.removeOfPlayerList(p);
                if(Embuscade.gamecycle.playerList.size() < 2) {
                    Embuscade.gamecycle.cancelGame();
                }
                //TODO : malus de points
            } else if(Embuscade.gamecycle.lobbyPlayers.contains(p)) {
                Embuscade.gamecycle.lobbyPlayers.remove(p);
            }
        }
        EmbuscadePlayer.clearPotionEffects(p);
        p.getInventory().clear();
    }

    //GESTION DES DAMAGES
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        //TODO : à revoir avec le système playerList / lobbyPlayers (bug)
        if(Embuscade.gamecycle.lobbyPlayers.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    //CHAT MANAGER
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        String msg = event.getMessage();

        if(Embuscade.gamecycle != null && Embuscade.gamecycle.hiders.hasMember(player)){
            event.setCancelled(true);
            Embuscade.gamecycle.hiders.sendTeamMessage(ChatColor.BLUE + player.getName() + ChatColor.RESET + " : " + msg);
        }
        else if(Embuscade.gamecycle != null && Embuscade.gamecycle.seekers.hasMember(player)){
            event.setCancelled(true);
            Embuscade.gamecycle.seekers.sendTeamMessage(ChatColor.RED + player.getName() + ChatColor.RESET + " : " + msg);
        }
        else if(Embuscade.gamecycle == null  || (!(Embuscade.gamecycle.seekers.hasMember(player)) && !(Embuscade.gamecycle.hiders.hasMember(player)))){
            event.setCancelled(true);
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!(Embuscade.gamecycle.seekers.hasMember(p)) && !(Embuscade.gamecycle.hiders.hasMember(p))) {
                    p.sendMessage(ChatColor.ITALIC + "" + ChatColor.GRAY + "(Lobby) " + ChatColor.RESET + player.getName() + " : " + msg);
                }
            }
        }
    }

    //PROTECTION PLANTATIONS
    @EventHandler
    public void soilChangePlayer(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void soilChangeEntity(EntityInteractEvent event) {
        if (event.getEntityType() != EntityType.PLAYER && event.getBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        Iterator<Block> blockList = event.blockList().iterator();
        while (blockList.hasNext()) {
            Block block = blockList.next();
            if (block.getType() == Material.FARMLAND) {
                blockList.remove();
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> blockList = event.blockList().iterator();
        while (blockList.hasNext()) {
            Block block = blockList.next();
            if (block.getType() == Material.FARMLAND) {
                blockList.remove();
            }
        }
    }
}