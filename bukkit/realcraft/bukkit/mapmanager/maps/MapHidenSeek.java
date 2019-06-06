package realcraft.bukkit.mapmanager.maps;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocation;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.MapData;
import realcraft.bukkit.mapmanager.map.data.MapDataLocationSpawn;
import realcraft.bukkit.mapmanager.map.data.MapDataMap;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapHidenSeek extends Map {

	private MapDataMap<MapDataLocationSpawn> spawns = new MapDataMap<>("spawns",MapDataLocationSpawn.class,2,2);

	public MapHidenSeek(int id){
		super(id);
	}

	public MapHidenSeek(User user){
		super(user,MapType.HIDENSEEK);
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
		for(java.util.Map.Entry<String,MapDataLocationSpawn> entry : spawns.getValues().entrySet()){
			renderer.addEntry(new MapRendererLocation(entry.getValue(),ChatColor.YELLOW+"Spawn",MapTeam.getByName(entry.getKey()).getColor()+entry.getKey().toUpperCase()));
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
			player.sendMessage("§6/map data "+ChatColor.YELLOW+"spawn §b<team> §7[<remove>]");
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
		if(args.length >= 1 && "spawn".equals(args[0].toLowerCase())){
			for(MapTeam team : MapTeam.values()){
				if(args.length == 1 || team.toString().startsWith(args[1].toLowerCase())) list.add(team.toString().toUpperCase());
			}
		}
		return list;
	}

	private enum MapTeam {
		HIDERS, SEEKERS;

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
				case HIDERS: return ChatColor.AQUA;
				case SEEKERS: return ChatColor.RED;
			}
			return ChatColor.WHITE;
		}
	}
}