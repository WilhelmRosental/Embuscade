package fr.ethan.embuscade.Configs;

import fr.ethan.embuscade.Mains.EditorRunnable;
import fr.ethan.embuscade.GameElements.BonusChest;
import fr.ethan.embuscade.Mains.Embuscade;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.bukkit.Location.deserialize;

public class ChestConfig {
    private static Embuscade plugin = Embuscade.plugin;
    public static EditorRunnable editorRunnable = EditorRunnable.editorRunnable;

    public static void chestsConfigClear(String gameName) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + gameName, "chests_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        System.out.println(file); //TODO : tests

        config.set("luckychests" , null);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveChestsConfig(String gameName) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + gameName, "chests_config.yml");

        System.out.println(file);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        //List<LuckyChest> luckychests = editorRunnable.luckychests;
        for(BonusChest lc : editorRunnable.luckychests) {
            System.out.println("test : "+lc.getName());
        }


        Location loc;

        for(BonusChest chest : editorRunnable.luckychests){
            loc = (Location) chest.getLoc();
            BlockData blckData = chest.getLoc().getBlock().getBlockData();
            config.set("luckychests." + chest.getName() + ".Position", loc.serialize());
            config.set("luckychests." + chest.getName() + ".BlockSpawnData", blckData.getAsString());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<BonusChest> loadChests(String gameName) {
        ArrayList<BonusChest> luckyChests = new ArrayList<BonusChest>();
        //Location locate;

        for(BonusChest lc : luckyChests) {
            System.out.println(lc.getName());
        }

        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + gameName, "chests_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        System.out.println(file);

        for(String str : config.getConfigurationSection("luckychests").getKeys(false)) {

        //for(String str : config.getString("luckychests")){
            //locate = deserialize(config.getConfigurationSection("luckychests." + str + ".Position").getValues(false));
            luckyChests.add(new BonusChest(str ,
                    deserialize(config.getConfigurationSection("luckychests." + str + ".Position").getValues(false)),
                    config.getString("luckychests." + str + ".BlockSpawnData")));
        }

        return luckyChests;
    }

}
