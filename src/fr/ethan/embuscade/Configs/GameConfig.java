package fr.ethan.embuscade.Configs;

import fr.ethan.embuscade.Mains.EditorRunnable;
import fr.ethan.embuscade.GameElements.WorldManager;
import fr.ethan.embuscade.Mains.Embuscade;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.Location.deserialize;

public class GameConfig {
    private static Embuscade plugin = Embuscade.plugin;
    public static FileConfiguration config;
    public static EditorRunnable editorRunnable;

    public static void createConfig(Player p, Embuscade plugin, String dirName) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + dirName, "game_config.yml");

        if (!file.exists()) {
                if(plugin.getResource("game_config.yml") == null) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        FileUtils.copyToFile(plugin.getResource("game_config.yml"), new File(plugin.getDataFolder() + File.separator + "games" + File.separator + dirName + File.separator + "game_config.yml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        } else {
            p.sendMessage(ChatColor.RED + "Cette partie existe déjà.");
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static String[] listGame() {
        File dir = new File(plugin.getDataFolder() + File.separator + "games");
        String[] gameConfigs = dir.list();

        return gameConfigs;
    }

    public static void printListGame(String[] gameList, Player p) {
        String inlineGameList = "";
        for(int i = 0; i < gameList.length; i++) {
            inlineGameList = inlineGameList + gameList[i];
            if(i != gameList.length - 1){ inlineGameList = inlineGameList + ", "; }
        }
        p.sendMessage(inlineGameList);
    }

    public static void setGame(Player player, String name) {
        File gameConfigFile = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, "game_config.yml");

        if(!gameConfigFile.exists()) {
            GameConfig.createConfig(player,plugin,name);
        } else {
            player.sendMessage(ChatColor.RED + "La partie " + ChatColor.YELLOW + name + ChatColor.RED + " existe déjà.");
        }

        FileConfiguration gameConfig = YamlConfiguration.loadConfiguration(gameConfigFile);

        gameConfig.set("spawn-position",player.getLocation().serialize());

        try {
            gameConfig.save(gameConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendMessage(ChatColor.GREEN + "La partie " + ChatColor.YELLOW + name + ChatColor.GREEN + " a été créée.");
    }

    public static void spawnGame(Player player, String name) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, "game_config.yml");

        if(!file.exists()) {
            player.sendMessage(ChatColor.RED + "La partie " + ChatColor.YELLOW + name + ChatColor.RED + " n'existe pas.");
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            if ((Arrays.asList(Bukkit.getWorldContainer().list())).contains(config.getString("spawn-position.world"))) {
                World w = Bukkit.createWorld(new WorldCreator(config.getString("spawn-position.world")));
                Location loc = deserialize(config.getConfigurationSection("spawn-position").getValues(false));
                player.teleport(loc);

                player.sendMessage(ChatColor.GREEN + "Tu as été téléporté à la partie " + ChatColor.YELLOW + name + ChatColor.GREEN + ".");
            } else {
                player.sendMessage(ChatColor.RED + "Cette partie est sur un monde qui n'existe pas.");
            }
        }
    }

    public static String getWorld(String name) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, "game_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.getString("spawn-position.world");
    }

    public static void setLimits(Player player, String name, Double value) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, "game_config.yml");

        if(file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.set("limits", value);

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            WorldManager.setBorders(name);
        } else {
            player.sendMessage(ChatColor.RED + "Cette partie n'existe pas.");
        }
    }

    public static void setTime(Player player, String name, int value) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, "game_config.yml");

        if(!file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.set("time", value);

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            player.sendMessage(ChatColor.GREEN + "Durée de la partie " + ChatColor.YELLOW + name + ChatColor.GREEN + " configurée sur " + ChatColor.YELLOW + value + ChatColor.GREEN + ".");
        } else {
            player.sendMessage(ChatColor.RED + "Cette partie n'existe pas.");
        }
    }

    public static void removeGame(Player p, String name) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name);

        if (file.exists()) {
            String[]entries = file.list();
            for(String s : entries) {
                File currentFile = new File(file.getPath(),s);
                currentFile.delete();
            }
            file.delete();
            p.sendMessage(ChatColor.GREEN + "La partie " + ChatColor.YELLOW + name + ChatColor.GREEN + " a été supprimée.");
        } else {
            p.sendMessage(ChatColor.RED + "La partie " + ChatColor.YELLOW + name + ChatColor.GREEN + " n'existe pas.");
        }
    }

    public static void giveItemEditor(Player player, String name) {
        ArrayList<ItemStack> isList = new ArrayList<ItemStack>();

        ItemStack luckyChests = new ItemStack(Material.BLAZE_ROD);
        ItemMeta im = luckyChests.getItemMeta();
        im.setDisplayName("LuckyChests " + name);
        luckyChests.setItemMeta(im);
        isList.add(luckyChests);

        ItemStack borderMove = new ItemStack(Material.STICK);
        im = borderMove.getItemMeta();
        im.setDisplayName("Border Mover " + name);
        borderMove.setItemMeta(im);
        isList.add(borderMove);

        ItemStack borderSize = new ItemStack(Material.BOWL);
        im = borderSize.getItemMeta();
        im.setDisplayName("Border Sizer " + name);
        borderSize.setItemMeta(im);
        isList.add(borderSize);

        ItemStack stone = new ItemStack(Material.STONE);
        ItemStack chest = new ItemStack(Material.CHEST);
        isList.add(stone);
        isList.add(chest);

        for(ItemStack is : isList) {
            player.getInventory().addItem(is);
        }
    }

    //Open/Close Editor
    public static void openEditor(Player player, String name) {
        //TODO : revoir conditionnel
        if(!plugin.playerInEditor.containsKey(player)) {
            player.getInventory().clear();
            editorRunnable = new EditorRunnable(name);
            plugin.playerInEditor.put(player,editorRunnable);

            GameConfig.spawnGame(player,name);

            GameConfig.giveItemEditor(player,name);
            Embuscade.isEditor = true;
            WorldManager.setBorders(name);
            player.setGameMode(GameMode.CREATIVE);
            //TODO : Afficher le spawn ?
        }
        else {
            player.sendMessage(ChatColor.RED + "Vous êtes déjà dans l'éditeur de la partie " + ChatColor.YELLOW + (plugin.playerInEditor.get(player)).getGameName() + ChatColor.RED + ".");
        }
    }

    public static void closeEditor(Player player, String name) {
        //TODO : revoir conditionnel
        if(plugin.playerInEditor.containsKey(player) && (plugin.playerInEditor.containsValue(plugin.playerInEditor.get(player))) ) {
            plugin.playerInEditor.remove(player);
            editorRunnable.cancelEditor();

            Embuscade.tpToLobby(player);

            player.getInventory().clear();
            Embuscade.isEditor = false;
            WorldManager.removeBorders(name); //TODO : il lisait la spawn-position au lieu de limit center, à tester

            Bukkit.getServer().unloadWorld(getWorld(name),false);
        }
        else if(!plugin.playerInEditor.containsKey(player) || !(plugin.playerInEditor.containsValue(plugin.playerInEditor.get(player))) ) {
            player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans l'éditeur de cette partie.");
        }
    }
}