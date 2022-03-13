package realcraft.bukkit.mapmanager.maps;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

public class MapDominate extends Map {

	private MapDataMap<MapDataLocationSpawn> spawns = new MapDataMap<>("spawns",MapDataLocationSpawn.class,2,2);
	private MapDataList<MapDataLocationSpawn> kits = new MapDataList<>("kits",MapDataLocationSpawn.class,10,10);
	private MapDataList<MapDataLocationBlock> emeralds = new MapDataList<>("emeralds",MapDataLocationBlock.class,2,4);
	private MapDataList<DominatePoint> beacons = new MapDataList<>("beacons",DominatePoint.class,5,5);

	public MapDominate(int id){
		super(id);
	}

	public MapDominate(User user){
		super(user,MapType.DOMINATE);
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		data.addProperty(kits);
		data.addProperty(emeralds);
		data.addProperty(beacons);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
		kits.loadData(data);
		emeralds.loadData(data);
		beacons.loadData(data);
	}

	@Override
	public void updateScoreboard(MapScoreboard scoreboard){
		scoreboard.addLine("§fSpectator: "+this.getSpectator().getValidColor()+(this.getSpectator().isValid() ? 1 : 0));
		scoreboard.addLine("§fSpawns: "+spawns.getValidColor()+spawns.size());
		scoreboard.addLine("§fKits: "+kits.getValidColor()+kits.size());
		scoreboard.addLine("§fEmeralds: "+emeralds.getValidColor()+emeralds.size());
		scoreboard.addLine("§fBeacons: "+beacons.getValidColor()+beacons.size());
	}

	@Override
	public void updateRenderer(MapRenderer renderer){
		for(java.util.Map.Entry<String,MapDataLocationSpawn> entry : spawns.getValues().entrySet()){
			renderer.addEntry(new MapRendererLocation(entry.getValue(),ChatColor.YELLOW+"Spawn",MapTeam.getByName(entry.getKey()).getColor()+entry.getKey().toUpperCase()));
		}
		for(MapDataLocationSpawn location : kits.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.DARK_AQUA+"Kit"));
		}
		for(MapDataLocationBlock location : emeralds.getValues()){
			renderer.addEntry(new MapRendererLocation(location,Material.EMERALD,ChatColor.GREEN+"Emerald"));
		}
		for(DominatePoint location : beacons.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.LIGHT_PURPLE+"Beacon",location.getName()));
		}
	}

	@Override
	public boolean isValid(){
		return (spawns.isValid() && kits.isValid() && emeralds.isValid() && beacons.isValid() && this.getSpectator().isValid());
	}

	@Override
	public void performCommand(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Nastaveni dat");
			player.sendMessage("§6/map data "+ChatColor.GRAY+"spectator §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.YELLOW+"spawn §b<team> §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.DARK_AQUA+"kit §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.GREEN+"emerald §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.LIGHT_PURPLE+"beacon §e<name> §7[<remove>]");
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
				player.sendMessage("§7Teams: "+String.join(", ", Arrays.toString(MapTeam.values())).toUpperCase());
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
		else if(subcommand.equals("kit")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				kits.remove(new MapDataLocationSpawn(player.getLocation()));
				this.save();
				return;
			}
			kits.add(new MapDataLocationSpawn(player.getLocation()));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("emerald")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				emeralds.remove(new MapDataLocationBlock(player.getLocation()));
				this.save();
				return;
			}
			emeralds.add(new MapDataLocationBlock(player.getLocation()));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("beacon")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				beacons.remove(new DominatePoint(null,player.getLocation()));
				this.save();
				return;
			}
			else if(args.length == 0){
				player.sendMessage("§6/map data "+ChatColor.LIGHT_PURPLE+"beacon §e<name> §7[<remove>]");
				return;
			}
			beacons.add(new DominatePoint(args[0],player.getLocation()));
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
		if(args.length == 0 || (args.length == 1 && "kit".startsWith(args[0].toLowerCase()))) list.add("kit");
		if(args.length == 0 || (args.length == 1 && "emerald".startsWith(args[0].toLowerCase()))) list.add("emerald");
		if(args.length == 0 || (args.length == 1 && "beacon".startsWith(args[0].toLowerCase()))) list.add("beacon");
		if(args.length >= 1 && "spawn".equals(args[0].toLowerCase())){
			for(MapTeam team : MapTeam.values()){
				if(args.length == 1 || team.toString().startsWith(args[1].toLowerCase())) list.add(team.toString().toUpperCase());
			}
		}
		return list;
	}

	private enum MapTeam {
		RED, BLUE;

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
			}
			return ChatColor.WHITE;
		}
	}

	public static class DominatePoint extends MapDataLocationBlock {

		private String name;

		public DominatePoint(String name,Location location){
			super(location);
			this.name = name;
		}

		public DominatePoint(JsonElement element){
			super(element);
			if(element.getAsJsonObject().has("name")) this.name = element.getAsJsonObject().get("name").getAsString();
		}

		public String getName(){
			return name;
		}

		@Override
		public JsonObject getData(){
			JsonObject json = super.getData();
			json.addProperty("name",this.getName());
			return json;
		}
	}
}