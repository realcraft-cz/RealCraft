package realcraft.bukkit.mapmanager.maps;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocation;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocationArea;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.*;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapRaces extends Map {

	private MapDataString type = new MapDataString("type");
	private MapDataInteger rounds = new MapDataInteger("rounds");
	private MapDataList<MapDataLocationSpawn> spawns = new MapDataList<>("spawns",MapDataLocationSpawn.class,20,20);
	private MapDataList<MapDataLocationArea> checkpoints = new MapDataList<>("checkpoints",MapDataLocationArea.class,2,Integer.MAX_VALUE);
	private MapDataLocationArea barrier = new MapDataLocationArea("barrier");

	public MapRaces(int id){
		super(id);
	}

	public MapRaces(User user){
		super(user,MapType.RACES);
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(type);
		data.addProperty(rounds);
		data.addProperty(spawns);
		data.addProperty(barrier);
		data.addProperty(checkpoints);
		return data;
	}

	@Override
	public void loadData(MapData data){
		type.loadData(data);
		rounds.loadData(data);
		spawns.loadData(data);
		barrier.loadData(data);
		checkpoints.loadData(data);
	}

	@Override
	public void updateScoreboard(MapScoreboard scoreboard){
		scoreboard.addLine("§fType: §f"+type.getValue());
		scoreboard.addLine("§fRounds: §f"+rounds.getValue());
		scoreboard.addLine("");
		scoreboard.addLine("§fSpectator: "+this.getSpectator().getValidColor()+(this.getSpectator().isValid() ? 1 : 0));
		scoreboard.addLine("§fSpawns: "+spawns.getValidColor()+spawns.size());
		scoreboard.addLine("§fBarrier: "+barrier.getValidColor()+(barrier.isValid() ? 1 : 0));
		scoreboard.addLine("§fCheckpoints: "+checkpoints.getValidColor()+checkpoints.size());
	}

	@Override
	public void updateRenderer(MapRenderer renderer){
		for(MapDataLocationSpawn location : spawns.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.YELLOW+"Spawn"));
		}
		for(MapDataLocationArea area : checkpoints.getValues()){
			renderer.addEntry(new MapRendererLocationArea(area,ChatColor.AQUA+"Checkpoint"));
		}
		if(barrier.isValid()) renderer.addEntry(new MapRendererLocationArea(barrier,ChatColor.RED+"Barrier"));
	}

	@Override
	public boolean isValid(){
		return (spawns.isValid() && checkpoints.isValid() && this.getSpectator().isValid() && barrier.isValid());
	}

	@Override
	public void performCommand(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Nastaveni dat");
			player.sendMessage("§6/map data "+ChatColor.GRAY+"spectator §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.LIGHT_PURPLE+"type §e<type>");
			player.sendMessage("§6/map data "+ChatColor.LIGHT_PURPLE+"rounds §e<rounds>");
			player.sendMessage("§6/map data "+ChatColor.YELLOW+"spawn §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.RED+"barrier §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.AQUA+"checkpoint §7[<remove>]");
			return;
		}
		String subcommand = args[0].toLowerCase();
		args = Arrays.copyOfRange(args,1,args.length);
		if(subcommand.equals("type")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				player.sendMessage("§6/map data "+ChatColor.LIGHT_PURPLE+"type §e<type>");
				return;
			}
			MapRaceType team = MapRaceType.getByName(args[0]);
			if(team == null){
				player.sendMessage("§cNeplatny typ.");
				player.sendMessage("§7Types: "+StringUtils.join(MapRaceType.values(),", ").toUpperCase());
				return;
			}
			type.setValue(team.toString());
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("rounds")){
			int amount;
			try {
				amount = Integer.valueOf(args[0]);
			} catch (NumberFormatException e){
				player.sendMessage("§cZadej cele cislo.");
				return;
			}
			rounds.setValue(amount);
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("spawn")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				spawns.remove(new MapDataLocationSpawn(player.getLocation()));
				this.save();
				return;
			}
			spawns.add(new MapDataLocationSpawn(player.getLocation()));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("barrier")){
			WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
			Region region;
			try {
				region = worldEdit.getSession(player).getSelection(worldEdit.getSession(player).getSelectionWorld());
			} catch (NullPointerException | IncompleteRegionException e){
				player.sendMessage("§cNejprve oznac 2 pozice pomoci WorldEdit.");
				return;
			}
			Location locMin = new Location(player.getWorld(),region.getMinimumPoint().getX(),region.getMinimumPoint().getY(),region.getMinimumPoint().getZ());
			Location locMax = new Location(player.getWorld(),region.getMaximumPoint().getX(),region.getMaximumPoint().getY(),region.getMaximumPoint().getZ());
			if(!this.getRegion().isLocationInside(locMin) || !this.getRegion().isLocationInside(locMax)){
				player.sendMessage("§cOznacene pozice nejsou uvnitr mapy.");
				return;
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				barrier = new MapDataLocationArea("barrier");
				this.save();
				return;
			}
			barrier = new MapDataLocationArea("barrier",locMin,locMax);
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("checkpoint")){
			WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
			Region region;
			try {
				region = worldEdit.getSession(player).getSelection(worldEdit.getSession(player).getSelectionWorld());
			} catch (NullPointerException | IncompleteRegionException e){
				player.sendMessage("§cNejprve oznac 2 pozice pomoci WorldEdit.");
				return;
			}
			Location locMin = new Location(player.getWorld(),region.getMinimumPoint().getX(),region.getMinimumPoint().getY(),region.getMinimumPoint().getZ());
			Location locMax = new Location(player.getWorld(),region.getMaximumPoint().getX(),region.getMaximumPoint().getY(),region.getMaximumPoint().getZ());
			if(!this.getRegion().isLocationInside(locMin) || !this.getRegion().isLocationInside(locMax)){
				player.sendMessage("§cOznacene pozice nejsou uvnitr mapy.");
				return;
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				checkpoints.remove(new MapDataLocationArea(locMin,locMax));
				this.save();
				return;
			}
			checkpoints.add(new MapDataLocationArea(locMin,locMax));
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
		if(args.length == 0 || (args.length == 1 && "type".startsWith(args[0].toLowerCase()))) list.add("type");
		if(args.length == 0 || (args.length == 1 && "rounds".startsWith(args[0].toLowerCase()))) list.add("rounds");
		if(args.length == 0 || (args.length == 1 && "spawn".startsWith(args[0].toLowerCase()))) list.add("spawn");
		if(args.length == 0 || (args.length == 1 && "barrier".startsWith(args[0].toLowerCase()))) list.add("barrier");
		if(args.length == 0 || (args.length == 1 && "checkpoint".startsWith(args[0].toLowerCase()))) list.add("checkpoint");
		return list;
	}

	private enum MapRaceType {
		RUN, HORSE, BOAT;

		public static MapRaceType getByName(String name){
			try {
				return MapRaceType.valueOf(name.toUpperCase());
			} catch(IllegalArgumentException e){
			}
			return null;
		}

		public String toString(){
			return this.name().toLowerCase();
		}
	}
}