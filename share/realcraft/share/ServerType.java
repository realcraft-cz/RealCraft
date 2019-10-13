package realcraft.share;

import net.md_5.bungee.api.ChatColor;

public enum ServerType {
	BUNGEE, LOBBY, SURVIVAL, CREATIVE, BEDWARS, HIDENSEEK, BLOCKPARTY, RAGEMODE, PAINTBALL, DOMINATE, FIGHTS, RACES, MAPS, FALLING;

	public static ServerType getByName(String name){
		return ServerType.valueOf(name.toUpperCase());
	}

	public String toString(){
		return this.name().toLowerCase();
	}

	public String getName(){
		switch(this){
			case BUNGEE: return "Bungee";
			case LOBBY: return "Lobby";
			case SURVIVAL: return "Survival";
			case CREATIVE: return "Creative";
			case BEDWARS: return "BedWars";
			case HIDENSEEK: return "Hide & Seek";
			case BLOCKPARTY: return "BlockParty";
			case RAGEMODE: return "RageMode";
			case PAINTBALL: return "Paintball";
			case DOMINATE: return "Dominate";
			case FIGHTS: return "Fights";
			case RACES: return "Races";
			case MAPS: return "Maps";
			case FALLING: return "Falling";
		}
		return "unknown";
	}

	public String getColor(){
		switch(this){
			case BUNGEE: return ChatColor.WHITE.toString();
			case LOBBY: return ChatColor.YELLOW.toString();
			case SURVIVAL: return ChatColor.AQUA.toString();
			case CREATIVE: return ChatColor.GREEN.toString();
			case BEDWARS: return ChatColor.RED.toString();
			case HIDENSEEK: return ChatColor.BLUE.toString();
			case BLOCKPARTY: return ChatColor.LIGHT_PURPLE.toString();
			case RAGEMODE: return ChatColor.RED.toString();
			case PAINTBALL: return ChatColor.GOLD.toString();
			case DOMINATE: return ChatColor.YELLOW.toString();
			case FIGHTS: return ChatColor.AQUA.toString();
			case RACES: return ChatColor.DARK_AQUA.toString();
			case MAPS: return ChatColor.GRAY.toString();
			case FALLING: return ChatColor.YELLOW.toString();
		}
		return ChatColor.WHITE.toString();
	}

	public int getPortOrder(){
		switch(this){
			case BUNGEE: return 100;
			case LOBBY: return 0;
			case SURVIVAL: return 1;
			case CREATIVE: return 2;
			case BEDWARS: return 3;
			case HIDENSEEK: return 4;
			case BLOCKPARTY: return 5;
			case RAGEMODE: return 6;
			case PAINTBALL: return 7;
			case DOMINATE: return 10;
			case FIGHTS: return 11;
			case RACES: return 12;
			case MAPS: return 8;
			case FALLING: return 13;
		}
		return 0;
	}
}