package com.realcraft.minihry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.realcraft.RealCraft;

public class GamesPlayersCount implements Listener, Runnable {
	RealCraft plugin;
	BlockNumbers blockNumbers;

	Set<String> worlds;
	List<GameCounter> gamecounters = new ArrayList<GameCounter>();

	boolean enabled = false;

	public GamesPlayersCount(RealCraft realcraft){
		plugin = realcraft;
		if(plugin.config.getBoolean("gamesplayerscount.enabled")){
			enabled = true;
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
			plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,10*20,5*20);
			blockNumbers = new BlockNumbers(plugin);
			worlds = plugin.config.getKeys("gamesplayerscount.worlds");
			if(worlds.size() > 0){
				for(String world : worlds){
					Location location = new Location(
						plugin.getServer().getWorld(world),
						plugin.config.getDouble("gamesplayerscount.worlds."+world+".x"),
						plugin.config.getDouble("gamesplayerscount.worlds."+world+".y"),
						plugin.config.getDouble("gamesplayerscount.worlds."+world+".z")
					);
					gamecounters.add(new GameCounter(plugin.getServer().getWorld(world),location,plugin.config.getInt("gamesplayerscount.worlds."+world+".r")));
				}
			}
		}
	}

	public void onReload(){
		enabled = false;
	}

	@Override
	public void run(){
		if(gamecounters.size() > 0){
			for(final GameCounter gamecounter : gamecounters){
				plugin.getServer().getScheduler().runTask(plugin,new Runnable(){
					@Override
					public void run(){
						gamecounter.update();
					}
				});
			}
		}
	}

	private class GameCounter {
		World world;
		World gameWorld;
		Location center;
		int rotation;

		public GameCounter(World world,Location center,int rotation){
			this.world = plugin.getServer().getWorld("world");
			this.gameWorld = world;
			this.center = center;
			this.rotation = rotation;
			clearRegion();
			pasteNumber(0);
		}

		public void update(){
			int count = 0;
			if(gameWorld != null){
				for(Player player : gameWorld.getPlayers()){
					if(player.getGameMode() == GameMode.SURVIVAL) count ++;
				}
			}
			clearRegion();
			pasteNumber(count);
		}

		private void pasteNumber(int number){
			if(number < 10){
				if(rotation == 0) blockNumbers.pasteNumber(number,new Location(world,center.getBlockX()-2,center.getBlockY()-7,center.getBlockZ()),rotation);
				else if(rotation == 90) blockNumbers.pasteNumber(number,new Location(world,center.getBlockX(),center.getBlockY()-7,center.getBlockZ()-2),rotation);
				else if(rotation == 180) blockNumbers.pasteNumber(number,new Location(world,center.getBlockX()-2,center.getBlockY()-7,center.getBlockZ()),rotation);
				else if(rotation == 270) blockNumbers.pasteNumber(number,new Location(world,center.getBlockX(),center.getBlockY()-7,center.getBlockZ()-2),rotation);
			} else {
				int d1 = (int)Math.floor(number/10.0);
				int d2 = number%10;
				if(rotation == 0){
					blockNumbers.pasteNumber(d1,new Location(world,center.getBlockX()+1,center.getBlockY()-7,center.getBlockZ()),rotation);
					blockNumbers.pasteNumber(d2,new Location(world,center.getBlockX()-5,center.getBlockY()-7,center.getBlockZ()),rotation);
				}
				else if(rotation == 90){
					blockNumbers.pasteNumber(d1,new Location(world,center.getBlockX(),center.getBlockY()-7,center.getBlockZ()+1),rotation);
					blockNumbers.pasteNumber(d2,new Location(world,center.getBlockX(),center.getBlockY()-7,center.getBlockZ()-5),rotation);
				}
				else if(rotation == 180){
					blockNumbers.pasteNumber(d1,new Location(world,center.getBlockX()-5,center.getBlockY()-7,center.getBlockZ()),rotation);
					blockNumbers.pasteNumber(d2,new Location(world,center.getBlockX()+1,center.getBlockY()-7,center.getBlockZ()),rotation);
				}
				else if(rotation == 270){
					blockNumbers.pasteNumber(d1,new Location(world,center.getBlockX(),center.getBlockY()-7,center.getBlockZ()-5),rotation);
					blockNumbers.pasteNumber(d2,new Location(world,center.getBlockX(),center.getBlockY()-7,center.getBlockZ()+1),rotation);
				}
			}
		}

		private void clearRegion(){
			int minX,maxX,minZ,maxZ;
			int minY = center.getBlockY()-7,maxY = center.getBlockY();
			if(rotation == 0 || rotation == 180){
				minX = center.getBlockX()-5;
				maxX = center.getBlockX()+5;
				minZ = center.getBlockZ();
				maxZ = center.getBlockZ();
			} else {
				minX = center.getBlockX();
				maxX = center.getBlockX();
				minZ = center.getBlockZ()-5;
				maxZ = center.getBlockZ()+5;
			}
			for(int x=minX;x<=maxX;x++){
				for(int y=minY;y<=maxY;y++){
					for(int z=minZ;z<=maxZ;z++){
						world.getBlockAt(x,y,z).setType(Material.AIR);
					}
				}
			}
		}
	}
}