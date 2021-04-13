package fr.ethan.embuscade.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Broadcast {
    public static void broadcaster(String message) {
        Bukkit.broadcastMessage(message);
    }

    public static void log(String message) { //fonction d'affichage en console
        System.out.println("[Embuscade] " + message);
    }

    public static void errorLog(String msg) { Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[Embuscade] " + ChatColor.RED + "ERREUR: " + ChatColor.RED + msg); }
}