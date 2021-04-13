package fr.ethan.embuscade.GameElements;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {
	private GameCycle game;
	private List<Player> members;

	public List<Player> role1; // DEFENDER ou HUNTER
    public List<Player> role2; // TRAPPER ou DEMOLISHER
    public List<Player> role3; // RUNNER ou SCOUT

    public static String name;

    public Team(GameCycle game) {
        this.game = game;
        this.members = new ArrayList<>();
    }

    public boolean hasMember(Player p){
        return members.contains(p);
    }
    
    public boolean isNameVisible() {
    	boolean a = false;
    	for(Player p : members) {
    		if(p.isCustomNameVisible()) {
    			a = true;
    		}
    	}
    	return a;
    }
    
    public boolean isEmpty() {
    	return members.isEmpty();
    }
    
    public void addPlayer(Player p) {
    	this.members.add(p);
    }
    
    public void removePlayer(Player p) {
    	this.members.remove(p);
    	//TODO : à corriger : le registeredPlayers est vide encore
    	//game.getCacheCachePlayer(p).setTeam(null);
    }

    public void sendTeamMessage(String msg) {
        for(Player p : members) {
            p.sendMessage(msg);
        }
    }
    
    public void setNameVisible(boolean y) {
    	for(Player p : members) {
    		p.setCustomNameVisible(y);
    	}
    }

    public void printMembers() { //TODO : à remodifier pour autre utilisation
        for(Player p : members) {
            System.out.println(p.getName());
        }
    }

    public String getName() {
    	return name;
    }

    public List<Player> getMembers() {
        return members;
    }
}