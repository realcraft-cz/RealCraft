package realcraft.bukkit.mapmanager.maps;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocation;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.MapData;
import realcraft.bukkit.mapmanager.map.data.MapDataList;
import realcraft.bukkit.mapmanager.map.data.MapDataLocationSpawn;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapRageMode extends Map {

	private MapDataList<MapDataLocationSpawn> spawns = new MapDataList<>("spawns",MapDataLocationSpawn.class,20,Integer.MAX_VALUE);

	public MapRageMode(int id){
		super(id);
	}

	public MapRageMode(User user){
		super(user,MapType.RAGEMODE);
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
	}

	@Override
	public void updateScoreboard(MapScoreboard scoreboard){
		scoreboard.addLine("§fSpectator: "+this.getSpectator().getValidColor()+(this.getSpectator().isValid() ? 1 : 0));
		scoreboard.addLine("§fSpawns: "+spawns.getValidColor()+spawns.size());
	}

	@Override
	public void updateRenderer(MapRenderer renderer){
		for(MapDataLocationSpawn location : spawns.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.YELLOW+"Spawn"));
		}
	}

	@Override
	public boolean isValid(){
		return (spawns.isValid() && this.getSpectator().isValid());
	}

	@Override
	public void performCommand(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Nastaveni dat");
			player.sendMessage("§6/map data "+ChatColor.GRAY+"spectator §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.YELLOW+"spawn §7[<remove>]");
			return;
		}
		String subcommand = args[0].toLowerCase();
		args = Arrays.copyOfRange(args,1,args.length);
		if(subcommand.equals("spawn")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				spawns.remove(new MapDataLocationSpawn(player.getLocation()));
				this.save();
				return;
			}
			spawns.add(new MapDataLocationSpawn(player.getLocation()));
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
		return list;
	}
}