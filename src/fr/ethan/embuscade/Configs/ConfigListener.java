package fr.ethan.embuscade.Configs;

import fr.ethan.embuscade.Mains.EditorRunnable;
import fr.ethan.embuscade.GameElements.BonusChest;
import fr.ethan.embuscade.GameElements.WorldManager;
import fr.ethan.embuscade.Mains.Embuscade;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ConfigListener implements Listener {
    private static Embuscade plugin = Embuscade.plugin;
    private double limitModifier;
    private ItemStack itemInHand = new ItemStack(Material.AIR);
    public static final BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public static String gameName;
    public static EditorRunnable editorRunnable = EditorRunnable.editorRunnable;

    public ConfigListener(String name) {
        gameName = name;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static BlockFace yawToFace (float yaw){
        return axis[Math.round(yaw / 90f) & 0x3];
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block blck = event.getBlock();
        BonusChest lc = new BonusChest("luckychest-[" + Math.round(blck.getLocation().getX()) + "," + Math.round(blck.getLocation().getY()) + "," + Math.round(blck.getLocation().getZ()) + "]", blck.getLocation(), blck.getLocation().getBlock().getBlockData().getAsString());

        if(blck.getType() == Material.STONE) {
            event.setCancelled(false);
        }
        else if(blck.getType() == Material.CHEST) {
            if(itemInHand.getItemMeta().getDisplayName().startsWith("LuckyChests ")) {
                event.setCancelled(true);
            }
            else if(editorRunnable.containsName(editorRunnable.luckychests, lc.getName())) {
                editorRunnable.toggleChest(player, blck);
            }
        }
        else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block blck = event.getBlockPlaced();

        if(blck.getType() == Material.CHEST) {
            editorRunnable.toggleChest(player, blck);
        }
        else if(blck.getType() == Material.STONE) {
            event.setCancelled(false);
        }
        else {
            event.setCancelled(true);
        }
    }

    //CHEST SETTER
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block blck = event.getClickedBlock();
        itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() != Material.AIR) {
            if(itemInHand.getItemMeta().getDisplayName().startsWith("LuckyChests ")) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    editorRunnable.toggleChest(player, blck);
                }
            }
            else if (itemInHand.getItemMeta().getDisplayName().startsWith("Border Mover ")) {
                if (event.getAction() == Action.LEFT_CLICK_AIR) {
                    limitModifier = -1;
                } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                    limitModifier = 1;
                }

                if ((yawToFace(player.getLocation().getYaw()) == BlockFace.NORTH) || (yawToFace(player.getLocation().getYaw()) == BlockFace.SOUTH)) {
                    // z
                    WorldManager.moveBorders(player, gameName, 'z', limitModifier);
                } else if ((yawToFace(player.getLocation().getYaw()) == BlockFace.EAST) || (yawToFace(player.getLocation().getYaw()) == BlockFace.WEST)) {
                    // x
                    WorldManager.moveBorders(player, gameName, 'x', limitModifier);
                }
            }
        }
    }
}
