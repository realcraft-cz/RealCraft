package realcraft.bungee;

import net.md_5.bungee.api.ChatColor;

public enum ServerType {
	BUNGEE, LOBBY, SURVIVAL, CREATIVE, PARKOUR, BEDWARS, HIDENSEEK, BLOCKPARTY, RAGEMODE, PAINTBALL, DOMINATE;

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
			case PARKOUR: return "Parkour";
			case BEDWARS: return "BedWars";
			case HIDENSEEK: return "Hide & Seek";
			case BLOCKPARTY: return "BlockParty";
			case RAGEMODE: return "RageMode";
			case PAINTBALL: return "Paintball";
			case DOMINATE: return "Dominate";
		}
		return "unknown";
	}

	public ChatColor getColor(){
		switch(this){
			case BUNGEE: return ChatColor.WHITE;
			case LOBBY: return ChatColor.YELLOW;
			case SURVIVAL: return ChatColor.AQUA;
			case CREATIVE: return ChatColor.GREEN;
			case PARKOUR: return ChatColor.YELLOW;
			case BEDWARS: return ChatColor.RED;
			case HIDENSEEK: return ChatColor.BLUE;
			case BLOCKPARTY: return ChatColor.LIGHT_PURPLE;
			case RAGEMODE: return ChatColor.RED;
			case PAINTBALL: return ChatColor.GOLD;
			case DOMINATE: return ChatColor.YELLOW;
		}
		return ChatColor.WHITE;
	}

	public int getPortOrder(){
		switch(this){
			case BUNGEE: return 100;
			case LOBBY: return 0;
			case SURVIVAL: return 1;
			case CREATIVE: return 2;
			case PARKOUR: return 9;
			case BEDWARS: return 3;
			case HIDENSEEK: return 4;
			case BLOCKPARTY: return 5;
			case RAGEMODE: return 6;
			case PAINTBALL: return 7;
			case DOMINATE: return 10;
		}
		return 0;
	}
}