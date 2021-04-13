package fr.ethan.embuscade.Mains;

import fr.ethan.embuscade.GameElements.GameCycle;
import fr.ethan.embuscade.Utils.Broadcast;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Embuscade extends JavaPlugin {
    public static Embuscade plugin;
    public static String worldLobby;
    public static GameCycle gamecycle;

    public static boolean isEditor = false;
    public HashMap<Player, EditorRunnable> playerInEditor = new HashMap<Player, EditorRunnable>();

    //Liste de tuple (Joueur , partie en editor)
    public static ArrayList<HashMap<Player,String>> editor = new ArrayList<HashMap<Player,String>>();

    String[] permissions = new String[]{
            "em.*",

            //commandes usuelles
            "em.give",
            "em.tpg",
            "em.tpw",
            "em.gamelist",

            //game
            "em.game.*",
            "em.game.skip",
            "em.game.cancel",
            "em.game.set",
            "em.game.rem",

            //config
            "em.config.*",
            "em.config.editor",
            "em.config.set"
    };

    @Override
    public void onEnable() {
        plugin = this;
        PluginManager pm = Bukkit.getPluginManager();

        //on initialise les commandes
        getCommand("em").setExecutor(new Commands());

        for(int j = 0; j < permissions.length; j++) {
            Permission p = new Permission(permissions[j]);
            pm.addPermission(p);
        }

        //config.yml
        plugin.saveDefaultConfig();

        File games = new File(plugin.getDataFolder() + File.separator + "games");
        File worlds = new File(plugin.getDataFolder() + File.separator + "worlds");
        File userCaches = new File(plugin.getDataFolder() + File.separator + "usercaches");

        //games directory
        if(!games.exists()) { games.mkdirs(); }
        //worlds directory
        if(!worlds.exists()) { games.mkdirs(); }
        //usercaches directory
        if(!userCaches.exists()) { userCaches.mkdirs(); }

        GlobalListener gem = new GlobalListener();

        if(!(Arrays.asList(games.list()).isEmpty())) {
            startGame();
        } else {
            Broadcast.errorLog("Aucune partie n'est configurée. Essayez " + ChatColor.YELLOW + "/help embuscade" + ChatColor.RED + ".");
        }

        //games = null;
        //userCaches = null;
    }

    public static void startGame() {
        gamecycle = new GameCycle(null);
    }

    public static void tpToLobby(Player player) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (config.getString("world-lobby") == null) {
            player.sendMessage(ChatColor.RED + "Il n'y a pas de monde configuré en tant que lobby.");
        } else {
            try {
                worldLobby = config.getString("world-lobby");
                Location spawnpoint = plugin.getServer().getWorld(worldLobby).getSpawnLocation();
                player.teleport(spawnpoint);
            } catch (NullPointerException e) {
                player.sendMessage(ChatColor.RED + "Le monde défini comme lobby n'existe pas.");
            }
        }
    }
}
