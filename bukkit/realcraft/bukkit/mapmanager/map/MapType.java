package realcraft.bukkit.mapmanager.map;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import static realcraft.bukkit.mapmanager.map.MapDimension.MapDimensionDefault;

public enum MapType {

	BEDWARS, HIDENSEEK, BLOCKPARTY, RAGEMODE, PAINTBALL, DOMINATE, RACES;

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
			//case FIGHTS: return "Fights";
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
			//case FIGHTS: return ChatColor.AQUA.toString();
			case RACES: return ChatColor.DARK_AQUA.toString();
		}
		return ChatColor.WHITE.toString();
	}

	public Material getMaterial(){
		switch(this){
			case BEDWARS: return Material.RED_BED;
			case HIDENSEEK: return Material.BOOKSHELF;
			case BLOCKPARTY: return Material.MUSIC_DISC_WAIT;
			case RAGEMODE: return Material.BOW;
			case PAINTBALL: return Material.SNOWBALL;
			case DOMINATE: return Material.BEACON;
			//case FIGHTS: return Material.DIAMOND_SWORD;
			case RACES: return Material.SADDLE;
		}
		return Material.AIR;
	}

	public MapDimension getDimension(){
		switch(this){
			case BEDWARS: return new MapDimensionDefault();
			case HIDENSEEK: return new MapDimensionDefault();
			case BLOCKPARTY: return new MapDimension(31,7,31);
			case RAGEMODE: return new MapDimensionDefault();
			case PAINTBALL: return new MapDimensionDefault();
			case DOMINATE: return new MapDimensionDefault();
			case RACES: return new MapDimensionDefault();
		}
		return new MapDimensionDefault();
	}

	public static MapType getByName(String name){
		return MapType.valueOf(name.toUpperCase());
	}

	public static MapType getById(int id){
		for(MapType type : MapType.values()){
			if(type.getId() == id) return type;
		}
		return null;
	}
}