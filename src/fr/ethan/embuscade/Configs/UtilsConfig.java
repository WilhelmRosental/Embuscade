package fr.ethan.embuscade.Configs;

import fr.ethan.embuscade.Mains.Embuscade;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class UtilsConfig {
    private static Embuscade plugin = Embuscade.plugin;

    public static boolean isEmpty(String name, String path, String fileType) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + name, fileType + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        if(config.getConfigurationSection(path) != null) {
            return false;
        }
        return true;
    }
}
