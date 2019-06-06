package realcraft.bukkit.mapmanager.maps;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocation;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.*;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapBedWars extends Map {

	private MapDataMap<MapDataLocationSpawn> spawns = new MapDataMap<>("spawns",MapDataLocationSpawn.class,4,4);
	private MapDataMap<MapDataLocationBlock> beds = new MapDataMap<>("beds",MapDataLocationBlock.class,4,4);
	private MapDataList<MapDataLocationSpawn> traders = new MapDataList<>("traders",MapDataLocationSpawn.class,4,8);
	private MapDataList<MapDataLocationBlock> bronze = new MapDataList<>("bronze",MapDataLocationBlock.class,4,8);
	private MapDataList<MapDataLocationBlock> iron = new MapDataList<>("iron",MapDataLocationBlock.class,4,8);
	private MapDataList<MapDataLocationBlock> gold = new MapDataList<>("gold",MapDataLocationBlock.class,4,5);

	public MapBedWars(int id){
		super(id);
	}

	public MapBedWars(User user){
		super(user,MapType.BEDWARS);
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		data.addProperty(beds);
		data.addProperty(traders);
		data.addProperty(bronze);
		data.addProperty(iron);
		data.addProperty(gold);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
		beds.loadData(data);
		traders.loadData(data);
		bronze.loadData(data);
		iron.loadData(data);
		gold.loadData(data);
	}

	@Override
	public void updateScoreboard(MapScoreboard scoreboard){
		scoreboard.addLine("§fSpectator: "+this.getSpectator().getValidColor()+(this.getSpectator().isValid() ? 1 : 0));
		scoreboard.addLine("§fSpawns: "+spawns.getValidColor()+spawns.size());
		scoreboard.addLine("§fBeds: "+beds.getValidColor()+beds.size());
		scoreboard.addLine("§fTraders: "+traders.getValidColor()+traders.size());
		scoreboard.addLine("");
		scoreboard.addLine("§fBronze: "+bronze.getValidColor()+bronze.size());
		scoreboard.addLine("§fIron: "+iron.getValidColor()+iron.size());
		scoreboard.addLine("§fGold: "+gold.getValidColor()+gold.size());
	}

	@Override
	public void updateRenderer(MapRenderer renderer){
		for(java.util.Map.Entry<String,MapDataLocationSpawn> entry : spawns.getValues().entrySet()){
			renderer.addEntry(new MapRendererLocation(entry.getValue(),ChatColor.YELLOW+"Spawn",MapTeam.getByName(entry.getKey()).getColor()+entry.getKey().toUpperCase()));
		}
		for(java.util.Map.Entry<String,MapDataLocationBlock> entry : beds.getValues().entrySet()){
			renderer.addEntry(new MapRendererLocation(entry.getValue(),ChatColor.LIGHT_PURPLE+"Bed",MapTeam.getByName(entry.getKey()).getColor()+entry.getKey().toUpperCase()));
		}
		for(MapDataLocationSpawn location : traders.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.WHITE+"Trader"));
		}
		for(MapDataLocationBlock location : bronze.getValues()){
			renderer.addEntry(new MapRendererLocation(location,Material.BRICK,ChatColor.DARK_RED+"Bronze"));
		}
		for(MapDataLocationBlock location : iron.getValues()){
			renderer.addEntry(new MapRendererLocation(location,Material.IRON_INGOT,ChatColor.GRAY+"Iron"));
		}
		for(MapDataLocationBlock location : gold.getValues()){
			renderer.addEntry(new MapRendererLocation(location,Material.GOLD_INGOT,ChatColor.GOLD+"Gold"));
		}
	}

	@Override
	public boolean isValid(){
		return (spawns.isValid() && beds.isValid() && traders.isValid() && bronze.isValid() && iron.isValid() && gold.isValid() && this.getSpectator().isValid());
	}

	@Override
	public void performCommand(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Nastaveni dat");
			player.sendMessage("§6/map data "+ChatColor.GRAY+"spectator §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.YELLOW+"spawn §b<team> §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.LIGHT_PURPLE+"bed §b<team> §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.WHITE+"trader §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.DARK_RED+"bronze §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.GRAY+"iron §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.GOLD+"gold §7[<remove>]");
			return;
		}
		String subcommand = args[0].toLowerCase();
		args = Arrays.copyOfRange(args,1,args.length);
		if(subcommand.equals("spawn")){
			if(args.length == 0){
				player.sendMessage("§6/map data "+ChatColor.YELLOW+"spawn §b<team> §7[<remove>]");
				return;
			}
			MapTeam team = MapTeam.getByName(args[0]);
			if(team == null){
				player.sendMessage("§cNeplatny tym.");
				player.sendMessage("§7Teams: "+StringUtils.join(MapTeam.values(),", ").toUpperCase());
				return;
			}
			if(args.length == 2 && args[1].equalsIgnoreCase("remove")){
				spawns.remove(team.toString());
				this.save();
				return;
			}
			spawns.add(team.toString(),new MapDataLocationSpawn(player.getLocation()));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("bed")){
			if(args.length == 0){
				player.sendMessage("§6/map data "+ChatColor.LIGHT_PURPLE+"bed §b<team> §7[<remove>]");
				return;
			}
			MapTeam team = MapTeam.getByName(args[0]);
			if(team == null){
				player.sendMessage("§cNeplatny tym.");
				player.sendMessage("§7Teams: "+StringUtils.join(MapTeam.values(),", ").toUpperCase());
				return;
			}
			if(args.length == 2 && args[1].equalsIgnoreCase("remove")){
				beds.remove(team.toString());
				this.save();
				return;
			}
			beds.add(team.toString(),new MapDataLocationBlock(player.getLocation()));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("trader")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				traders.remove(new MapDataLocationSpawn(player.getLocation()));
				this.save();
				return;
			}
			traders.add(new MapDataLocationSpawn(player.getLocation()));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("bronze")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				bronze.remove(new MapDataLocationBlock(player.getLocation()));
				this.save();
				return;
			}
			bronze.add(new MapDataLocationBlock(player.getLocation()));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("iron")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				iron.remove(new MapDataLocationBlock(player.getLocation()));
				this.save();
				return;
			}
			iron.add(new MapDataLocationBlock(player.getLocation()));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("gold")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				gold.remove(new MapDataLocationBlock(player.getLocation()));
				this.save();
				return;
			}
			gold.add(new MapDataLocationBlock(player.getLocation()));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("spectator")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				this.getSpectator().setLocation(null);
				this.save();
				return;
			}
			this.getSpectator().setLocation(player.getLocation());
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
	}

	@Override
	public List<String> tabCompleter(Player player,String[] args){
		ArrayList<String> list = new ArrayList<>();
		if(args.length == 0 || (args.length == 1 && "spectator".startsWith(args[0].toLowerCase()))) list.add("spectator");
		if(args.length == 0 || (args.length == 1 && "spawn".startsWith(args[0].toLowerCase()))) list.add("spawn");
		if(args.length == 0 || (args.length == 1 && "bed".startsWith(args[0].toLowerCase()))) list.add("bed");
		if(args.length == 0 || (args.length == 1 && "trader".startsWith(args[0].toLowerCase()))) list.add("trader");
		if(args.length == 0 || (args.length == 1 && "bronze".startsWith(args[0].toLowerCase()))) list.add("bronze");
		if(args.length == 0 || (args.length == 1 && "iron".startsWith(args[0].toLowerCase()))) list.add("iron");
		if(args.length == 0 || (args.length == 1 && "gold".startsWith(args[0].toLowerCase()))) list.add("gold");
		if(args.length >= 1 && ("spawn".equals(args[0].toLowerCase()) || "bed".equals(args[0].toLowerCase()))){
			for(MapTeam team : MapTeam.values()){
				if(args.length == 1 || team.toString().startsWith(args[1].toLowerCase())) list.add(team.toString().toUpperCase());
			}
		}
		return list;
	}

	private enum MapTeam {
		RED, BLUE, YELLOW, GREEN;

		public static MapTeam getByName(String name){
			try {
				return MapTeam.valueOf(name.toUpperCase());
			} catch(IllegalArgumentException e){
			}
			return null;
		}

		public String toString(){
			return this.name().toLowerCase();
		}

		public ChatColor getColor(){
			switch(this){
				case RED: return ChatColor.RED;
				case BLUE: return ChatColor.BLUE;
				case YELLOW: return ChatColor.YELLOW;
				case GREEN: return ChatColor.GREEN;
			}
			return ChatColor.WHITE;
		}
	}
}