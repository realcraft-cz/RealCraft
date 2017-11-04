package com.realcraft.lobby;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import com.realcraft.RealCraft;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.utils.FireworkUtil;
import com.realcraft.utils.LocationUtil;
import com.realcraft.utils.Title;

public class LobbyAutoParkour implements Listener, Runnable {
	RealCraft plugin;

	private FileConfiguration parkourConfig;

	Location plateLocation = null;
	Vector[] arenaBounds = new Vector[2];
	HashMap<Player,ParkourPlayer> players = new HashMap<Player,ParkourPlayer>();

	static Random random = new Random();

	static int levels = 3;
	static int jumpsPerLevel = 10;

	public LobbyAutoParkour(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,4,4);
		this.loadParkour();
	}

	public void loadParkour(){
		File parkourFile = new File(RealCraft.getInstance().getDataFolder()+"/parkours/"+plugin.serverName+".yml");
		if(parkourFile.exists()){
			parkourConfig = new YamlConfiguration();
			try {
				parkourConfig.load(parkourFile);
			} catch (Exception e){
				e.printStackTrace();
			}
			plateLocation = LocationUtil.getConfigLocation(parkourConfig,"plate");
			arenaBounds[0] = LocationUtil.getConfigLocation(parkourConfig,"arena.locMin").toVector();
			arenaBounds[1] = LocationUtil.getConfigLocation(parkourConfig,"arena.locMax").toVector();
		}
	}

	public void onReload(){
	}

	public void onDisable(){
		for(Entry<Player,ParkourPlayer> entry: players.entrySet()){
			entry.getValue().clear();
		}
	}

	@Override
	public void run(){
		for(Entry<Player,ParkourPlayer> entry: players.entrySet()){
			if(entry.getValue().getJumps() != 0) entry.getValue().setTime(entry.getValue().getTime()-1);
			if(entry.getValue().getTime() == -1){
				entry.getValue().clearBase();
			}
			else if(entry.getValue().getTime() == -10){
				this.cancelPlayerParkour(entry.getKey());
			} else {
				float exp = entry.getValue().getTime()/(entry.getValue().getMaxTime()+0f);
				if(exp < 0) exp = 0;
				else if(exp > 1) exp = 1;
				entry.getKey().setExp(exp);
			}
		}
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(this.isPlayerInParkour(player)){
			this.cancelPlayerParkour(player);
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.PHYSICAL && plateLocation != null){
			Block block = event.getClickedBlock();
			if(block != null && (block.getType() == Material.STONE_PLATE || block.getType() == Material.WOOD_PLATE)
					&& block.getLocation().getBlockX() == plateLocation.getBlockX() && block.getLocation().getBlockY() == plateLocation.getBlockY() && block.getLocation().getBlockZ() == plateLocation.getBlockZ()){
				Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
					@Override
					public void run(){
						LobbyAutoParkour.this.startPlayerParkour(event.getPlayer());
					}
				},10);
			}
		}
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(this.isPlayerInParkour(player)){
			if(player.getLocation().getBlockY()+1 < this.getPlayerParkour(player).getDestination().getBlockY()){
				this.cancelPlayerParkour(player);
			} else {
				Location location = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
				Location destination = this.getPlayerParkour(player).getDestination();
				if(location.getBlockX() == destination.getBlockX() && location.getBlockY() == destination.getBlockY() && location.getBlockZ() == destination.getBlockZ()){
					this.createNextStep(player,destination);
				}
			}
		}
	}

	public void startPlayerParkour(Player player){
		if(!this.isPlayerInParkour(player)){
			players.put(player,new ParkourPlayer(player));
			player.setFlying(false);
			player.setAllowFlight(false);
			player.setWalkSpeed(0.3f);
			Location random = this.chooseRandomStartLocation();
			this.createNextStep(player,random);
			Location teleport = random.clone();
			teleport.setPitch(player.getLocation().getPitch());
			teleport.setYaw(player.getLocation().getYaw());
			player.teleport(teleport.add(0.5,1,0.5));
			player.getWorld().playSound(teleport,Sound.ENTITY_ENDERMEN_TELEPORT,1f,1f);
		}
	}

	public void cancelPlayerParkour(Player player){
		this.getPlayerParkour(player).clear();
		this.getPlayerParkour(player).cancel();
		players.remove(player);
	}

	public void createNextStep(Player player,Location baseLocation){
		this.getPlayerParkour(player).clear();
		Location location = this.chooseRandomStep(baseLocation,1);
		this.getPlayerParkour(player).setLocations(baseLocation,location);
	}

	public Location chooseRandomStartLocation(){
		int x = this.getRandomInteger(arenaBounds[0].getBlockX(),arenaBounds[1].getBlockX());
		int y = this.getRandomInteger(arenaBounds[0].getBlockY(),arenaBounds[1].getBlockY());
		int z = this.getRandomInteger(arenaBounds[0].getBlockZ(),arenaBounds[1].getBlockZ());
		Location location = new Location(plateLocation.getWorld(),x,y,z);
		if(location.getBlock().getType() != Material.AIR
		|| location.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR
		|| location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() != Material.AIR) return this.chooseRandomStartLocation();
		return location;
	}

	public Location chooseRandomStep(Location baseLocation,int step){
		if(step > 100) return baseLocation;
		int x = this.getRandomInteger(-4,4);
		int y = (random.nextBoolean() ? this.getRandomInteger(0,1) : this.getRandomInteger(-1,1));
		int z = this.getRandomInteger(-4,4);
		Location location = baseLocation.clone().add(x,y,z);
		if(!location.toVector().isInAABB(arenaBounds[0],arenaBounds[1])) return this.chooseRandomStep(baseLocation,step+1);
		if(location.distance(baseLocation) > 5 || location.distance(baseLocation) <= 2
		|| location.getBlock().getType() != Material.AIR
		|| location.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR
		|| location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() != Material.AIR) return this.chooseRandomStep(baseLocation,step+1);
		return location;
	}

	public boolean isPlayerInParkour(Player player){
		return players.containsKey(player);
	}

	public ParkourPlayer getPlayerParkour(Player player){
		return players.get(player);
	}

	private class ParkourPlayer {
		private Player player;
		private Location base;
		private Location destination;
		private DyeColor color;
		private int maxTime;
		private int time;
		private int level;
		private int jumps;

		public ParkourPlayer(Player player){
			this.player = player;
			this.color = LobbyAutoParkour.getRandomColor();
			this.time = 15;
			this.maxTime = 15;
			this.level = 1;
			this.jumps = -1;
			Title.showTitle(player,"Parkour Challenge",0,5.2,0.6);
		}

		@SuppressWarnings("deprecation")
		public void setLocations(Location base,Location destination){
			this.base = base;
			this.destination = destination;
			this.base.getBlock().setType(Material.STAINED_CLAY);
			this.base.getBlock().setData(color.getWoolData());
			this.destination.getBlock().setType(Material.WOOL);
			this.destination.getBlock().setData(color.getWoolData());
			this.time = this.maxTime;
			this.jumps ++;
			player.setExp((this.time/(this.maxTime+0f)));
			player.playSound(destination,Sound.ENTITY_ITEM_PICKUP,1f,1.5f);
			if(this.jumps == LobbyAutoParkour.jumpsPerLevel){
				this.jumps = 0;
				this.maxTime -= 5;
				this.time = this.maxTime;
				this.level ++;
				FireworkUtil.spawnFirework(base.clone().add(0.5,1.0,0.5),FireworkEffect.Type.BALL,color.getColor(),true,false);

				int coins = 0;
				if(this.level-1 == 1) coins = 15;
				else if(this.level-1 == 2) coins = 30;
				else if(this.level-1 == 3) coins = 50;
				int reward = PlayerManazer.getPlayerInfo(player).giveCoins(coins);

				String title = "";
				if(this.level <= LobbyAutoParkour.levels) title = "Level "+(this.level-1);
				else title = ChatColor.GREEN+"Parkour Master";
				Title.showTitle(player,title,0,5.2,0.6);
				if(this.level <= LobbyAutoParkour.levels) Title.showActionTitle(player,""+ChatColor.YELLOW+(this.jumps)+"/"+LobbyAutoParkour.jumpsPerLevel);
				player.playSound(destination,Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
				if(this.level-1 == LobbyAutoParkour.levels) cancelPlayerParkour(player);

				final String finalTitle = title;
				Bukkit.getScheduler().runTaskLater(RealCraft.getInstance(),new Runnable(){
					public void run(){
						PlayerManazer.getPlayerInfo(player).runCoinsEffect(finalTitle,reward);
					}
				},20);
			}
			else Title.showActionTitle(player,""+ChatColor.YELLOW+this.jumps+"/"+LobbyAutoParkour.jumpsPerLevel);
		}

		public Location getBase(){
			return this.base;
		}

		public Location getDestination(){
			return this.destination;
		}

		public int getTime(){
			return this.time;
		}

		public void setTime(int time){
			this.time = time;
		}

		public int getJumps(){
			return this.jumps;
		}

		public int getMaxTime(){
			return this.maxTime;
		}

		public void clear(){
			if(this.getBase() != null) this.getBase().getBlock().setType(Material.AIR);
			if(this.getDestination() != null) this.getDestination().getBlock().setType(Material.AIR);
		}

		public void clearBase(){
			if(this.getBase() != null) this.getBase().getBlock().setType(Material.AIR);
			player.playSound(player.getLocation(),Sound.ENTITY_ENDERDRAGON_FLAP,1,2);
		}

		public void cancel(){
			if(this.level < 4) player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			player.setExp(0);
			player.setWalkSpeed(0.2f);
		}
	}

	public int getRandomInteger(int min,int max){
		return random.nextInt((max - min) + 1) + min;
	}

	public double getRandomDouble(double min,double max){
		return min+Math.random()*(max-min);
	}

	static private DyeColor colors[] = null;

	public static DyeColor getRandomColor(){
		if(colors == null){
			colors = new DyeColor[]{
				DyeColor.BLUE,
				DyeColor.CYAN,
				DyeColor.GREEN,
				DyeColor.LIGHT_BLUE,
				DyeColor.LIME,
				DyeColor.MAGENTA,
				DyeColor.ORANGE,
				DyeColor.PINK,
				DyeColor.PURPLE,
				DyeColor.RED,
				DyeColor.YELLOW
			};
		}
		return colors[random.nextInt(colors.length)];
	}
}