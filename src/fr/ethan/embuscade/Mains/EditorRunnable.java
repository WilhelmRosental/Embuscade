package fr.ethan.embuscade.Mains;

import fr.ethan.embuscade.Configs.ChestConfig;
import fr.ethan.embuscade.Configs.ConfigListener;
import fr.ethan.embuscade.Configs.UtilsConfig;
import fr.ethan.embuscade.GameElements.BonusChest;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditorRunnable {
    String gameName;
    public BukkitTask editor;
    private static Embuscade plugin = Embuscade.plugin;
    public List<BonusChest> luckychests;
    ConfigListener cl;
    public static EditorRunnable editorRunnable;
    public static double a = 0;

    public EditorRunnable(String gameName) {
        editorRunnable = this;
        this.gameName = gameName;
        cl = new ConfigListener(gameName);

        if(UtilsConfig.isEmpty(gameName, "luckychests", "chests")) {
            this.luckychests = new ArrayList<BonusChest>();
        } else {
            this.luckychests = ChestConfig.loadChests(gameName);
        }

        seeChests(this.luckychests);
        run();
    }

    public void run() {
        editor = new BukkitRunnable() {
            @Override
            public void run() {
                seeChestsParticles(a);
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void cancelEditor() {
        //cancel listener
        HandlerList.unregisterAll(cl);

        //save
        ChestConfig.chestsConfigClear(gameName);
        ChestConfig.saveChestsConfig(gameName);

        unseeChests();
        clearChests();

        //cancel runnable
        editor.cancel();
    }

    public String getGameName() {
        return this.gameName;
    }

    //CHESTS
    public void toggleChest(Player p, Block blck) {
        File file = new File(plugin.getDataFolder() + File.separator + "games" + File.separator + gameName, "game_config.yml");

        if(file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            Location loc = blck.getLocation();
            BonusChest lc = new BonusChest("luckychest-[" + Math.round(loc.getX()) + "," + Math.round(loc.getY()) + "," + Math.round(loc.getZ()) + "]", blck.getLocation(), loc.getBlock().getBlockData().getAsString());

            System.out.println("AVANT :");
            printChests();

            if(this.luckychests.isEmpty() || !containsName(this.luckychests, lc.getName())) {
                p.sendMessage(ChatColor.GREEN + "Coffre ajouté en ["+Math.round(blck.getLocation().getX()) + "," + Math.round(blck.getLocation().getY()) + "," + Math.round(blck.getLocation().getZ())+"]");
                this.luckychests.add(lc);
            }
            else { //On supprime
                p.sendMessage(ChatColor.RED + "Coffre retiré en ["+Math.round(blck.getLocation().getX()) + "," + Math.round(blck.getLocation().getY()) + "," + Math.round(blck.getLocation().getZ())+"]");
                this.luckychests.removeIf(lcs -> lc.getName().equals(lcs.getName()));
            }

            System.out.println("APRES :");
            printChests();
            System.out.println("=======================");

            this.unseeChests();
            this.seeChests(this.luckychests);
        }
    }

    public void printChests() {
        for(BonusChest lc : luckychests) {
            System.out.println(lc.getName());
        }
    }

    public boolean containsName(final List<BonusChest> list, final String name){
        return list.stream().filter(o -> o.getName().equals(name)).findFirst().isPresent();
    }

    public void clearChests() {
        this.luckychests.clear();
    }

    public List<BonusChest> getChests() {
        return this.luckychests;
    }

    public void seeChests(List<BonusChest> luckychestsToSee) {
        for(BonusChest lc : luckychestsToSee) {
            if(lc.getLoc().getBlock().getType() == Material.AIR) {
                BlockData blckData = Bukkit.createBlockData(lc.getBlockData());
                lc.getLoc().getBlock().setBlockData(blckData);
            }
        }
    }

    public void seeChestsParticles(double a) {

        String upDown = "up";

        int height = 2;
        double yIncrement = 0.1;

        //double a = 0;

        double x = 0;
        double y = 0;
        double z = 0;

        double radius = 0.8;

        x = Math.cos(a) * radius;
        z = Math.sin(a) * radius;

        this.a = a + 1;

        plugin.getServer().broadcastMessage(String.valueOf(a));

        if(upDown.equals("up"))
        {
            if(y >= height)
            {
                upDown = "down";
                y -= yIncrement;
            }
            else
            {
                y += yIncrement;
            }
        }
        else
        {
            if(y <= 0)
            {
                upDown = "up";
                y += yIncrement;
            }
            else
            {
                y -= yIncrement;
            }
        }

        if(a >= 360){ this.a = 0;} //reset a to stop it getting too large

        //on spawn sur tous les coffres
        for(BonusChest lc : luckychests) {
            Location loc = lc.getLoc();

            //lc.getLoc().getWorld().spawnParticle(Particle.DRAGON_BREATH,lc.getLoc(),1);
            //lc.getLoc().getWorld().spawnParticle(Particle.PORTAL,lc.getLoc(),1);

            lc.getLoc().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, (float) (loc.getX() + x), (float) (loc.getY() + y), (float) (loc.getZ() + z), 0, 0, 0, 0, 1, null, false);
        }
    }

    public void unseeChests() {

    }
}
