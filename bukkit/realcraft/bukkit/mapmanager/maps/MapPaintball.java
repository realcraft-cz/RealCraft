package realcraft.bukkit.mapmanager.maps;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.mapmanager.map.Map;
import realcraft.bukkit.mapmanager.map.MapRenderer;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocation;
import realcraft.bukkit.mapmanager.map.MapRenderer.MapRendererLocationArea;
import realcraft.bukkit.mapmanager.map.MapScoreboard;
import realcraft.bukkit.mapmanager.map.MapType;
import realcraft.bukkit.mapmanager.map.data.*;
import realcraft.bukkit.utils.MaterialUtil;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapPaintball extends Map implements Listener {

	private MapDataMap<MapDataLocationSpawn> spawns = new MapDataMap<>("spawns",MapDataLocationSpawn.class,2,2);
	private MapDataList<MapDataLocationBlock> drops = new MapDataList<>("drops",MapDataLocationBlock.class);
	private MapDataList<PaintballSpeed> speeds = new MapDataList<>("speeds",PaintballSpeed.class);
	private MapDataList<PaintballJumpArea> jumps = new MapDataList<>("jumps",PaintballJumpArea.class);
	private MapDataList<MapDataLocationBlock> machineguns = new MapDataList<>("machineguns",MapDataLocationBlock.class);

	public MapPaintball(int id){
		super(id);
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public MapPaintball(User user){
		super(user,MapType.PAINTBALL);
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@Override
	public MapData getData(){
		MapData data = new MapData();
		data.addProperty(spawns);
		data.addProperty(drops);
		data.addProperty(speeds);
		data.addProperty(jumps);
		data.addProperty(machineguns);
		return data;
	}

	@Override
	public void loadData(MapData data){
		spawns.loadData(data);
		drops.loadData(data);
		jumps.loadData(data);
		speeds.loadData(data);
		machineguns.loadData(data);
	}

	@Override
	public void updateScoreboard(MapScoreboard scoreboard){
		scoreboard.addLine("§fSpectator: "+this.getSpectator().getValidColor()+(this.getSpectator().isValid() ? 1 : 0));
		scoreboard.addLine("§fSpawns: "+spawns.getValidColor()+spawns.size());
		scoreboard.addLine("§fDrops: "+drops.getValidColor()+drops.size());
		scoreboard.addLine("§fSpeeds: "+speeds.getValidColor()+speeds.size());
		scoreboard.addLine("§fJumps: "+jumps.getValidColor()+jumps.size());
		scoreboard.addLine("§fMachineGuns: "+machineguns.getValidColor()+machineguns.size());
	}

	@Override
	public void updateRenderer(MapRenderer renderer){
		for(java.util.Map.Entry<String,MapDataLocationSpawn> entry : spawns.getValues().entrySet()){
			renderer.addEntry(new MapRendererLocation(entry.getValue(),ChatColor.YELLOW+"Spawn",MapTeam.getByName(entry.getKey()).getColor()+entry.getKey().toUpperCase()));
		}
		for(MapDataLocationBlock location : drops.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.DARK_AQUA+"Drop"));
		}
		for(PaintballSpeed location : speeds.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.GOLD+"Speed",location.getDuration()+" ticks"));
		}
		for(PaintballJumpArea area : jumps.getValues()){
			renderer.addEntry(new MapRendererLocationArea(area,ChatColor.GREEN+"Jump",""+area.getForce()));
		}
		for(MapDataLocationBlock location : machineguns.getValues()){
			renderer.addEntry(new MapRendererLocation(location,ChatColor.DARK_GREEN+"MachineGun"));
		}
	}

	@Override
	public boolean isValid(){
		return (spawns.isValid() && drops.isValid() && speeds.isValid() && jumps.isValid() && machineguns.isValid() && this.getSpectator().isValid());
	}

	@Override
	public void performCommand(Player player,String[] args){
		if(args.length == 0){
			player.sendMessage("Nastaveni dat");
			player.sendMessage("§6/map data "+ChatColor.GRAY+"spectator §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.YELLOW+"spawn §b<team> §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.DARK_AQUA+"drop §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.GOLD+"speed §e<duration> §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.GREEN+"jump §e<force> §7[<remove>]");
			player.sendMessage("§6/map data "+ChatColor.DARK_GREEN+"machinegun §7[<remove>]");
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
		else if(subcommand.equals("drop")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				drops.remove(new MapDataLocationBlock(player.getLocation()));
				this.save();
				return;
			}
			drops.add(new MapDataLocationBlock(player.getLocation()));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("speed")){
			if(args.length == 0){
				player.sendMessage("§6/map data "+ChatColor.GOLD+"speed §e<duration> §7[<remove>]");
				return;
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				speeds.remove(new PaintballSpeed(0,player.getLocation()));
				this.save();
				return;
			}
			int time;
			try {
				time = Integer.valueOf(args[0]);
			} catch (NumberFormatException e){
				player.sendMessage("§cZadej cele cislo.");
				return;
			}
			speeds.add(new PaintballSpeed(time,player.getLocation()));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("jump")){
			if(args.length == 0){
				player.sendMessage("§6/map data "+ChatColor.GREEN+"jump §e<force> §7[<remove>]");
				return;
			}
			WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
			Region region;
			try {
				region = worldEdit.getSession(player).getSelection(worldEdit.getSession(player).getSelectionWorld());
			} catch (IncompleteRegionException e){
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
				jumps.remove(new PaintballJumpArea(1,locMin,locMax));
				this.save();
				return;
			}
			double force;
			try {
				force = Double.valueOf(args[0]);
			} catch (NumberFormatException e){
				player.sendMessage("§cZadej realne cislo.");
				return;
			}
			jumps.add(new PaintballJumpArea(force,locMin,locMax));
			player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
			this.save();
		}
		else if(subcommand.equals("machinegun")){
			if(args.length == 1 && args[0].equalsIgnoreCase("remove")){
				machineguns.remove(new MapDataLocationBlock(player.getLocation()));
				this.save();
				return;
			}
			machineguns.add(new MapDataLocationBlock(player.getLocation()));
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
		if(args.length == 0 || (args.length == 1 && "drop".startsWith(args[0].toLowerCase()))) list.add("drop");
		if(args.length == 0 || (args.length == 1 && "speed".startsWith(args[0].toLowerCase()))) list.add("speed");
		if(args.length == 0 || (args.length == 1 && "jump".startsWith(args[0].toLowerCase()))) list.add("jump");
		if(args.length == 0 || (args.length == 1 && "machinegun".startsWith(args[0].toLowerCase()))) list.add("machinegun");
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

	public static class PaintballSpeed extends MapDataLocationBlock {

		private int duration;

		public PaintballSpeed(int duration,Location location){
			super(location);
			this.duration = duration;
		}

		public PaintballSpeed(JsonElement element){
			super(element);
			this.duration = element.getAsJsonObject().get("duration").getAsInt();
		}

		public int getDuration(){
			return duration;
		}

		@Override
		public JsonObject getData(){
			JsonObject json = super.getData();
			json.addProperty("duration",this.getDuration());
			return json;
		}
	}

	public static class PaintballJumpArea extends MapDataLocationArea {

		private double force;

		public PaintballJumpArea(double force,Location locFrom,Location locTo){
			super(locFrom,locTo);
			this.force = force;
		}

		public PaintballJumpArea(JsonElement element){
			super(element);
			this.force = element.getAsJsonObject().get("force").getAsDouble();
		}

		public double getForce(){
			return force;
		}

		@Override
		public JsonObject getData(){
			JsonObject json = super.getData();
			json.addProperty("force",this.getForce());
			return json;
		}
	}

	private static final int JUMP_TIMEOUT = 500;
	private static HashMap<Player,Long> jumpPlayers = new HashMap<>();

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		if(event.getPlayer().getVelocity().getY() > 0.0001){
			if(!jumpPlayers.containsKey(event.getPlayer()) || jumpPlayers.get(event.getPlayer())+JUMP_TIMEOUT < System.currentTimeMillis()){
				for(PaintballJumpArea area : jumps.getValues()){
					if(this.isPlayerAtJump(event.getPlayer(),area)){
						event.getPlayer().setVelocity(event.getPlayer().getVelocity().setY(area.getForce()));
						jumpPlayers.put(event.getPlayer(),System.currentTimeMillis());
						break;
					}
				}
			}
		}
	}

	private boolean isPlayerAtJump(Player player,PaintballJumpArea area){
		Location location = player.getLocation();
		if(location.getBlockX() >= area.getMinLocation().getLocation().getBlockX() && location.getBlockX() <= area.getMaxLocation().getLocation().getBlockX()
				&& location.getBlockY() >= area.getMinLocation().getLocation().getBlockY() && location.getBlockY() <= area.getMaxLocation().getLocation().getBlockY()
				&& location.getBlockZ() >= area.getMinLocation().getLocation().getBlockZ() && location.getBlockZ() <= area.getMaxLocation().getLocation().getBlockZ()) return true;
		return false;
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.PHYSICAL){
			Block block = event.getClickedBlock();
			if(block != null && MaterialUtil.isPressurePlate(block.getType())){
				for(PaintballSpeed speed : speeds.getValues()){
					if(block.getLocation().getBlockX() == speed.getLocation().getBlockX() && block.getLocation().getBlockY() == speed.getLocation().getBlockY() && block.getLocation().getBlockZ() == speed.getLocation().getBlockZ()){
						event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,speed.getDuration(),2));
					}
				}
			}
		}
	}
}