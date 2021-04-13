package fr.ethan.embuscade.Mains;

import fr.ethan.embuscade.Configs.GameConfig;
import fr.ethan.embuscade.Enums.GameState;
import fr.ethan.embuscade.GameElements.WorldManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class Commands implements TabExecutor {
    private static Embuscade plugin = Embuscade.plugin;


    /*
    *
    *
    * /cc config editor open|close <nom> : lance une instance de configuration pour <nom>
    *
    * /cc config set <param>
    *   /cc config set time : définit le temps par défaut de l'instance de config
    *   /cc config set spawn : définit le point de spawn de l'instance de config
    *
    *
     */

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if(cmd.getName().equalsIgnoreCase("em")) { //tester si partie pas lancé et tester permissions
            switch(args[0]) {
                case "game":
                    if(args.length == 3) {
                        switch(args[1]) {
                            case "set":
                                if(sender.hasPermission("em.game.set") || sender.hasPermission("em.game.*") || sender.hasPermission("em.*")) {
                                    if (sender instanceof Player) {
                                        GameConfig.setGame(Bukkit.getPlayerExact(sender.getName()), args[2]);
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "Seul les Joueurs sont autorisés à exécuter cette commande.");
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Vous n'avez pas l'autorisation d'utiliser " + ChatColor.YELLOW + "/em game set <nom>");
                                }
                                break;
                            case "rem":
                                if(sender.hasPermission("em.game.set") || sender.hasPermission("em.game.*") || sender.hasPermission("em.*")) {
                                    GameConfig.removeGame(Bukkit.getPlayerExact(sender.getName()), args[2]);
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Vous n'avez pas l'autorisation d'utiliser " + ChatColor.YELLOW + "/em game rem <nom>");
                                }
                                break;
                            case "help":
                                //TODO : /cc game help (dans Broadcast)
                                break;
                            default:
                                sender.sendMessage(ChatColor.RED + "Erreur : essayez : " + ChatColor.YELLOW + "/em game help" + ChatColor.RED + ".");
                                break;
                        }
                    }
                    else if(args.length == 2) {
                        switch(args[1]) {
                            case "skip":
                                if(sender.hasPermission("em.game.skip") || sender.hasPermission("em.game.*") || sender.hasPermission("em.*")) {
                                    try {
                                        if (Embuscade.gamecycle.state == GameState.GAME) {
                                            sender.sendMessage(ChatColor.RED + "La partie a déjà commencé.");
                                        } else {
                                            Embuscade.gamecycle.lobbyTimer.cancel();
                                            Embuscade.gamecycle.startGame();
                                        }
                                    } catch(NullPointerException e) {
                                        sender.sendMessage(ChatColor.RED + "Aucune partie n'est en cours.");
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Vous n'avez pas l'autorisation d'utiliser " + ChatColor.YELLOW + "/cc game skip");
                                }
                                break;
                            case "cancel":
                                if(sender.hasPermission("em.game.cancel") || sender.hasPermission("em.game.*") || sender.hasPermission("em.*")) {
                                    try {
                                        Embuscade.gamecycle.cancelGame();
                                    } catch(NullPointerException e) {
                                        sender.sendMessage(ChatColor.RED + "Aucune partie n'est en cours.");
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Vous n'avez pas l'autorisation d'utiliser " + ChatColor.YELLOW + "/em game cancel");
                                }
                                break;
                            default:
                                sender.sendMessage(ChatColor.RED + "Erreur : trop ou pas assez d'arguments. Essayez :\n"
                                                    + ChatColor.YELLOW + "/em game help");
                                break;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Erreur : essayez : " + ChatColor.YELLOW + "/cc game help" + ChatColor.RED + ".");
                    }
                    break;
                case "config":
                    if(args.length == 4) {
                        if(sender.hasPermission("em.config.editor") || sender.hasPermission("em.config.*") || sender.hasPermission("em.*")) {
                            switch(args[1]) {
                                case "editor":
                                    if(args[2].equalsIgnoreCase("close")) { //on quitte l'éditeur
                                        if(Embuscade.isEditor) {
                                            GameConfig.closeEditor(Bukkit.getPlayerExact(sender.getName()),args[3]);
                                        } else {
                                            sender.sendMessage(ChatColor.RED + "L'éditeur n'est pas ouvert.");
                                        }
                                    } else if(args[2].equalsIgnoreCase("open")) {
                                        GameConfig.openEditor(Bukkit.getPlayerExact(sender.getName()),args[3]);
                                    }
                                    break;
                                default:
                                    sender.sendMessage(ChatColor.RED + "Erreur : trop ou pas assez d'arguments. Essayez :\n"
                                                        + ChatColor.YELLOW + "/em config help");
                                    break;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Vous n'avez pas l'autorisation d'utiliser " + ChatColor.YELLOW + "/em editor close|open <nom>");
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "Pas assez ou trop d'arguments. Essayez :\n"
                                            + ChatColor.AQUA + "/em " + ChatColor.GREEN + "config " + ChatColor.DARK_GREEN + "editor " + ChatColor.YELLOW + "<nom_de_partie>");
                    }
                    break;
                case "gamelist":
                    if(sender.hasPermission("em.gamelist") || sender.hasPermission("em.*")) {
                        GameConfig.printListGame(GameConfig.listGame(), Bukkit.getPlayerExact(sender.getName()));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Vous n'avez pas l'autorisation d'utiliser " + ChatColor.YELLOW + "/em gamelist");
                    }
                    break;
                case "tpg":
                    if (sender instanceof Player == true) {
                        if(sender.hasPermission("em.tpg") || sender.hasPermission("em.*")) {
                            GameConfig.spawnGame(Bukkit.getPlayerExact(sender.getName()),args[1]);
                        } else {
                            sender.sendMessage(ChatColor.RED + "Vous n'avez pas l'autorisation d'utiliser " + ChatColor.YELLOW + "/em tpg");
                        }
                    } else if(sender instanceof Player == false) {
                        sender.sendMessage(ChatColor.RED + "Seul les Joueurs sont autorisés à exécuter cette commande.");
                    }
                    break;
                case "tpw":
                    if (sender instanceof Player == true) {
                        if(sender.hasPermission("em.tpw") || sender.hasPermission("em.*")) {
                            World w = Bukkit.getWorld(args[1]);

                            if ((Arrays.asList(Bukkit.getWorldContainer().list())).contains(args[1])) {
                                w = Bukkit.createWorld(new WorldCreator(args[1]));
                                Bukkit.getPlayerExact(sender.getName()).teleport(w.getSpawnLocation());
                            } else {
                                sender.sendMessage(ChatColor.RED + "Ce monde n'existe pas.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Vous n'avez pas l'autorisation d'utiliser " + ChatColor.YELLOW + "/em tpw");
                        }
                    } else if(sender instanceof Player == false) {
                        sender.sendMessage(ChatColor.RED + "Seul les Joueurs sont autorisés à exécuter cette commande.");
                    }
                    break;
                case "give":
                    sender.sendMessage("Pas encore configuré.");
                    break;
                case "lobby":
                    if (sender instanceof Player) {
                        Embuscade.tpToLobby(Bukkit.getPlayerExact(sender.getName()));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Seul les Joueurs sont autorisés à exécuter cette commande.");
                    }
                    break;
                default:
                    break;
            }
        }
        else if(cmd.getName().equalsIgnoreCase("setlobbyspawn")) {
            //TODO : à faire
        }
        else if(cmd.getName().equalsIgnoreCase("setworldlobby")) {
            //TODO : à faire
        }
        else if(cmd.getName().equalsIgnoreCase("settime")) {
            if(args.length != 2) {
                sender.sendMessage("Bonne utilisation : /settime <nom> <valeur>");
                return true;
            } else {
                GameConfig.setTime(Bukkit.getPlayerExact(sender.getName()), args[0], parseInt(args[1]));
            }
        }
        else if(cmd.getName().equalsIgnoreCase("setlimits")) {
            if(args.length != 2) {
                sender.sendMessage("Bonne utilisation : /setlimits <nom> <valeur>");
                return true;
            } else {
                GameConfig.setLimits(Bukkit.getPlayerExact(sender.getName()), args[0], parseDouble(args[1]));
            }
        }
        else if(cmd.getName().equalsIgnoreCase("centerlimits")) {
            WorldManager.setLimitsCenter(Bukkit.getPlayerExact(sender.getName()),args[0]);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("em") && args.length >= 0){
            if(sender instanceof Player){
                Player player = (Player) sender;

                if(args.length == 1) {
                    list.add("lobby");
                    list.add("give");
                    list.add("tpw");
                    list.add("tpg");
                    list.add("gamelist");
                    list.add("game");
                    list.add("config");
                }
                else if(args.length == 2 && (args[0].equalsIgnoreCase("tpg"))) {
                    list.addAll(Arrays.asList(GameConfig.listGame()));
                }
                else if(args.length == 2 && (args[0].equalsIgnoreCase("game"))) {
                    list.add("skip");
                    list.add("cancel");
                    list.add("help");
                    list.add("set");
                    list.add("rem");
                }
                else if(args.length == 2 && (args[0].equalsIgnoreCase("config"))) {
                    list.add("set");
                    list.add("editor");
                }
                else if(args.length == 3 && (args[1].equalsIgnoreCase("editor"))) {
                    list.add("open");
                    list.add("close");
                }
                else if(args.length == 3 && (args[1].equalsIgnoreCase("set"))) {
                    list.add("time");
                    list.add("spawn");
                }
                else if(args.length == 3 && (args[1].equalsIgnoreCase("rem"))) {
                    list.addAll(Arrays.asList(GameConfig.listGame()));
                }
                else if(args.length == 4 && (args[2].equalsIgnoreCase("open")) || (args[2].equalsIgnoreCase("close"))) {
                    list.addAll(Arrays.asList(GameConfig.listGame()));
                }

                return list;
            }
        }
        return list;
    }
}
