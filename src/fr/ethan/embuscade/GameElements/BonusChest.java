package fr.ethan.embuscade.GameElements;

import org.bukkit.Location;

public class BonusChest {
    public String blockData;
    public String name;
    public Location loc;

    public BonusChest(String name, Location loc, String blockData) {
        this.name = name;
        this.loc = loc;
        this.blockData = blockData;
    }

    public Location getLoc() {
        return loc;
    }

    public String getBlockData() {
        return blockData;
    }

    public String getName() {
        return name;
    }
}
