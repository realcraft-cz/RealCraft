package realcraft.bukkit.mapmanager.map;

import org.bukkit.ChatColor;
import realcraft.share.ServerType;

public enum MapType {

	BEDWARS, HIDENSEEK, BLOCKPARTY, RAGEMODE, PAINTBALL, DOMINATE, FIGHTS, RACES;

	public static ServerType getByName(String name){
		return ServerType.valueOf(name.toUpperCase());
	}

	public String toString(){
		return this.name().toLowerCase();
	}

	public int getId(){
		switch(this){
			case BEDWARS: return 3;
			case HIDENSEEK: return 4;
			case BLOCKPARTY: return 5;
			case RAGEMODE: return 6;
			case PAINTBALL: return 7;
			case DOMINATE: return 10;
			case RACES: return 12;
		}
		return 0;
	}

	public String getName(){
		switch(this){
			case BEDWARS: return "BedWars";
			case HIDENSEEK: return "Hide & Seek";
			case BLOCKPARTY: return "BlockParty";
			case RAGEMODE: return "RageMode";
			case PAINTBALL: return "Paintball";
			case DOMINATE: return "Dominate";
			case FIGHTS: return "Fights";
			case RACES: return "Races";
		}
		return "unknown";
	}

	public String getColor(){
		switch(this){
			case BEDWARS: return ChatColor.RED.toString();
			case HIDENSEEK: return ChatColor.BLUE.toString();
			case BLOCKPARTY: return ChatColor.LIGHT_PURPLE.toString();
			case RAGEMODE: return ChatColor.RED.toString();
			case PAINTBALL: return ChatColor.GOLD.toString();
			case DOMINATE: return ChatColor.YELLOW.toString();
			case FIGHTS: return ChatColor.AQUA.toString();
			case RACES: return ChatColor.DARK_AQUA.toString();
		}
		return ChatColor.WHITE.toString();
	}
}