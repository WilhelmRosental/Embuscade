package fr.ethan.embuscade.Enums;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum BonusType {

    SUPERSPEED(1, new ItemStack(Material.GLASS_BOTTLE), ChatColor.GREEN + "SuperSpeed", new PotionEffect(PotionEffectType.SPEED,15*20,2), null),
    BLUEWOOL(2, new ItemStack(Material.BLUE_WOOL), ChatColor.BLUE + "Laine Bleue", null, Role.DEFENDER),
    REDWOOL(3, new ItemStack(Material.RED_WOOL), ChatColor.RED + "Laine Rouge", null, Role.RUNNER),
    GREENWOOL(4, new ItemStack(Material.GREEN_WOOL), ChatColor.GREEN + "Laine Verte", null, Role.TRAPPER),
    YELLOWWOOL(5, new ItemStack(Material.YELLOW_WOOL), ChatColor.YELLOW + "Laine Jaune", null, Role.DEMOLISHER),
    PURPLEWOOL(6, new ItemStack(Material.PURPLE_WOOL), ChatColor.DARK_PURPLE + "Laine Violette", null, Role.SCOUT),
    WHITEWOOL(7, new ItemStack(Material.WHITE_WOOL), ChatColor.WHITE + "Laine Blanche", null, Role.HUNTER);

    private int id;
    private String name;
    private PotionEffect potionEffect;
    private ItemStack itemStack;
    private static String teamName;
    private Role role;

    BonusType(int id, ItemStack itemStack, String name, PotionEffect potionEffect, Role role) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        this.role = role;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public static String getTeam() {
        return teamName;
    }
}
