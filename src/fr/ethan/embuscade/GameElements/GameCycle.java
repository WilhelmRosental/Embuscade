package fr.ethan.embuscade.GameElements;

import fr.ethan.embuscade.Configs.GameConfig;
import fr.ethan.embuscade.Enums.GameState;
import fr.ethan.embuscade.Enums.Role;
import fr.ethan.embuscade.Mains.Embuscade;
import fr.ethan.embuscade.Utils.ActionBarAPI.ActionBarAPI;
import fr.ethan.embuscade.Utils.Broadcast;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

public class GameCycle {
    private static Embuscade plugin = Embuscade.plugin;

    public Team hiders;
    public Team seekers;
    public String name;
    public int lobbyTime;
    public int gameTime;
    public int time;
    public BukkitTask lobbyTimer;
    public BukkitTask gameTimer;

    private HashMap<Player, EmbuscadePlayer> registeredPlayer;
    public static List<Player> playerList = new ArrayList<Player>();
    public static List<Player> lobbyPlayers = new ArrayList<Player>();

    private GameListener gl;
    public boolean started;
    private static World map;

    public ArrayList<String> gameList = new ArrayList<String>();
    public GameState state;

    public GameCycle(String initName) {
        //si à l'instanciation on a pas renseigné de nom on lance une partie aléatoire, sinon partie indiquée
        if(initName == null) { name = randomGame(); }
        else { name = initName; }

        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, "game_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        //listener
        gl = new GameListener();

        //teams
        this.hiders = new Team(this);
        this.seekers = new Team(this);

        //temps
        lobbyTime = 120; //TODO : rendre ça automatique ?
        gameTime = config.getInt("time");

        //limits
        Double Limits = config.getDouble("limits");

        /* SOLUTION 1 : LOAD DE LA MAP (+ lightweight)
        *
        *  - à la fin de la partie, on l'unload afin d'annuler toute modification
        *  - createWorld est obligatoire car permet de load la map
        *
         */
        if((Arrays.asList(Bukkit.getWorldContainer().list())).contains(config.getString("spawn-position.world"))) {
            Bukkit.createWorld(new WorldCreator(config.getString("spawn-position.world")));
        }

        map = Bukkit.getWorld(config.getString("spawn-position.world"));

        /* SOLUTION 2 : COPY DE LA MAP (à retester)
        *
        * //on load la map
        * if((Arrays.asList(Bukkit.getWorldContainer().list())).contains(config.getString("spawn-position.world"))) {
        *     Bukkit.createWorld(new WorldCreator(config.getString("spawn-position.world")));
        * }
        * map = Bukkit.getWorld(config.getString("spawn-position.world"));
        *
        * //on créé la map temporaire
        * File tempMap = new File(plugin.getServer().getWorldContainer() ,config.getString("spawn-position.world") + "_temp");
        * tempMap.mkdirs();
        *
        * //on copy la map
        * WorldManager.copyWorld(map.getWorldFolder(),tempMap);
        *
        */

        state = GameState.LOBBY;

        if(lobbyPlayers.size() >= 4 && !started) {
            started = true;
            startLobby();
        }
    }

    public int getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public void startLobby() {
        int cooldown = 10;
        time = lobbyTime + cooldown;

        started = true;

        lobbyTimer = new BukkitRunnable() {
            @Override
            public void run() {
                if (time == 0) {

                    if(lobbyPlayers.size() >= 4) {
                        playerList.addAll(lobbyPlayers);
                        lobbyPlayers.clear();
                    }

                    if(playerList.size() >= 4) {
                        cancel();
                        startGame();
                    }
                }
                ActionBarAPI.sendActionBarToAllPlayers("" + time, -1); //TODO : remplacer par le scoreboard (uniquement sur les joueurs ingame)
                time--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void startGame() {
        state = GameState.GAME;

        int cooldown = 10;
        time = gameTime + cooldown;

        playerList = random(playerList);

        for (Player player : playerList) {
            if (!seekers.hasMember(player) && !hiders.hasMember(player)) {
                if (seekers.getMembers().size() >= hiders.getMembers().size()) {
                    setTeamHiders(player);
                } else {
                    setTeamSeekers(player);

                    player.addPotionEffect((new PotionEffect(PotionEffectType.BLINDNESS, 20 * 60, 1)));
                    player.addPotionEffect((new PotionEffect(PotionEffectType.SLOW, 20 * 60, 100)));
                    player.addPotionEffect((new PotionEffect(PotionEffectType.JUMP, 20 * 60, 200)));
                }
                EmbuscadePlayer.heal(player);
                GameConfig.spawnGame(player, name);
                player.setGameMode(GameMode.CREATIVE);  //TODO : tests
            }
        }
        WorldManager.setDifficulty(name);
        WorldManager.setBorders(name);

        gameTimer = new BukkitRunnable() {
            @Override
            public void run() {
                if(time == 0) {
                    if(!hiders.isEmpty()) {
                        Broadcast.broadcaster("Le temps est écoulé, les " + ChatColor.RED + "hiders" + ChatColor.RESET + " ont gagné la partie.");
                        cancelGame();
                    }
                }
                ActionBarAPI.sendActionBarToAllPlayers(""+time,-1); //TODO : remplacer par le scoreboard
                time--;
            }
        }.runTaskTimer(plugin, 0,20);
    }

    public String randomGame() {
        return Arrays.asList(GameConfig.listGame()).get(new Random().nextInt(Arrays.asList(GameConfig.listGame()).size()));
    }
    
    public static List<Player> random(List<Player> list){
		int min = 0, max = list.size();
        int r = min + (int)(Math.random() * ((max - min) + 1));
        for(int i = 0; i < r; i++) {
            min = 0;
            max = list.size() - 1;
            int nombreAleat = min + (int)(Math.random() * ((max - min) + 1));

            list.add(list.get(nombreAleat));
            list.remove(nombreAleat);
        }
        return list;
	}

    public void setTeamSeekers(Player player) {
        if (seekers.hasMember(player)) {
            player.sendMessage(ChatColor.YELLOW + "Vous êtes déjà dans l'équipe " + ChatColor.RED + "seekers" + ChatColor.YELLOW + ".");
            return;
        }
        if (hiders.getMembers().size() >= seekers.getMembers().size() && seekers.getMembers().size() - hiders.getMembers().size() != 2) {
            if (hiders.hasMember(player)) { hiders.removePlayer(player); }
            seekers.addPlayer(player);
            player.sendMessage(ChatColor.YELLOW + "Vous avez rejoint la team " + ChatColor.RED + "seekers" + ChatColor.YELLOW + ".");

        } else {
            player.sendMessage(ChatColor.RED + "Il y a trop de joueur dans cette équipe.");
        }
    }

    public void setTeamHiders(Player player) {
        if (hiders.hasMember(player)) {
            player.sendMessage(ChatColor.YELLOW + "Vous êtes déjà dans l'équipe " + ChatColor.BLUE + "hiders" + ChatColor.YELLOW + ".");
            return;
        }
        if (hiders.getMembers().size() <= seekers.getMembers().size() && hiders.getMembers().size() - seekers.getMembers().size() != 2) {
            if (seekers.hasMember(player)) { seekers.removePlayer(player); }
            hiders.addPlayer(player);
            player.sendMessage(ChatColor.YELLOW + "Vous avez rejoint la team " + ChatColor.BLUE + "hiders" + ChatColor.YELLOW + ".");
        } else {
            player.sendMessage(ChatColor.RED + "Il y a trop de joueur dans cette équipe");
        }
    }

    public void setRoleSeeker(Player player) {
        if(!(getCacheCachePlayer(player).hasRole())) {
            if(seekers.role1.size() == 0) {
                seekers.role1.add(player);
                getCacheCachePlayer(player).setRole(Role.HUNTER);
            }
            else if(seekers.role2.size() == 0) {
                seekers.role2.add(player);
                getCacheCachePlayer(player).setRole(Role.DEMOLISHER);
            }
            else if(seekers.role3.size() == 0) {
                seekers.role3.add(player);
                getCacheCachePlayer(player).setRole(Role.SCOUT);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Vous avez déjà un rôle.");
        }
    }

    public void setRoleHiders(Player player) {
        if(!(getCacheCachePlayer(player).hasRole())) {
            if(hiders.role1.size() == 0) {
                hiders.role1.add(player);
                getCacheCachePlayer(player).setRole(Role.DEFENDER);
            }
            else if(hiders.role2.size() == 0) {
                hiders.role2.add(player);
                getCacheCachePlayer(player).setRole(Role.TRAPPER);
            }
            else if(hiders.role3.size() == 0) {
                hiders.role3.add(player);
                getCacheCachePlayer(player).setRole(Role.RUNNER);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Vous avez déjà un rôle.");
        }
    }

    public void cancelGame() {
        for(Player p : playerList) {
            EmbuscadePlayer.clearPotionEffects(p);
            Embuscade.tpToLobby(p);
        }

        if(state == GameState.LOBBY) {
            lobbyTimer.cancel();
        } else if(state == GameState.GAME){
            gameTimer.cancel();
            state = GameState.LOBBY;
        }

        HandlerList.unregisterAll(gl);
        WorldManager.removeBorders(name);
        started = false;
        lobbyPlayers.addAll(playerList);
        playerList.clear();

        Bukkit.getServer().unloadWorld(map,false);

        Embuscade.startGame();
    }

    public void registerPlayer(Player player){
        registeredPlayer.put(player, new EmbuscadePlayer(player,plugin));
    }

    public EmbuscadePlayer getCacheCachePlayer(Player player){
        return this.registeredPlayer.get(player);
    }

    public HashMap<Player, EmbuscadePlayer> getRegisteredPlayer() {
        return registeredPlayer;
    }

    public void removeOfPlayerList(Player p) {
        playerList.remove(p);
    }
}
