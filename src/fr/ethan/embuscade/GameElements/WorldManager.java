package fr.ethan.embuscade.GameElements;

import fr.ethan.embuscade.Mains.Embuscade;
import fr.ethan.embuscade.Utils.Broadcast;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.bukkit.Location.deserialize;

public class WorldManager {
    private static Embuscade plugin = Embuscade.plugin;
    public static ArrayList<Entity> chestnames = new ArrayList<Entity>();
    public static FileConfiguration config;

    public static void setDifficulty(String name) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, "game_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        World w = plugin.getServer().getWorld(config.getString("spawn-position.world"));
        w.setDifficulty(Difficulty.PEACEFUL);
    }

    public static void setLimitsCenter(Player player, String name) {
        File gameConfigFile = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, "game_config.yml");

        FileConfiguration gameConfig = YamlConfiguration.loadConfiguration(gameConfigFile);

        gameConfig.set("limits-center",player.getLocation().serialize());

        try {
            gameConfig.save(gameConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void moveBorders(Player player, String name, char axe, Double value) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, "game_config.yml");

        if(file.exists()) {
            if(axe == 'x') {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                config.set("limits-center.x", config.getDouble("limits-center.x") + value);

                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                WorldManager.setBorders(name);
            } else if(axe == 'z') {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                config.set("limits-center.z", config.getDouble("limits-center.z") + value);

                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                WorldManager.setBorders(name);
            } else {
                player.sendMessage(ChatColor.RED + "L'axe renseigné est incorrect.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Cette partie n'existe pas.");
        }
    }

    public static void setBorders(String name) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, "game_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        World w = plugin.getServer().getWorld(config.getString("limits-center.world"));

        try {
            WorldBorder border = w.getWorldBorder();
            border.setCenter(deserialize(config.getConfigurationSection("limits-center").getValues(false)));
            border.setSize(config.getDouble("limits"));
        } catch(NullPointerException e) {
            Broadcast.errorLog("Erreur lors de l'activation des limites.");
        }
    }

    public static void removeBorders(String name) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, "game_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        World w = plugin.getServer().getWorld(config.getString("limits-center.world"));

        WorldBorder border = w.getWorldBorder();
        border.reset();
    }

    //inutilisée
    public static void copyWorld(File source, File target){
        try {
            ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        target.mkdirs();
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            Broadcast.errorLog("Erreur lors de la copy du monde");
        }
    }

    //inutilisée
    public boolean deleteWorld(File path) {
        if(path.exists()) {
            File files[] = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }

    //TODO : à faire
    public static String[] listWorlds() {
        File dir = new File(plugin.getDataFolder() + File.separator + "games");
        return dir.list();
    }

    //TODO : configuration des mondes pour indications
    public static void createWorldConfig(String worldName) {
        File file = new File(plugin.getDataFolder() + File.separator + "worlds",  worldName + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Broadcast.log(ChatColor.RED + "Ce monde a déjà une configuration.");
        }
        config = YamlConfiguration.loadConfiguration(file);
    }
}
