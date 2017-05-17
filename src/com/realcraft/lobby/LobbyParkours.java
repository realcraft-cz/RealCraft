package com.realcraft.lobby;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.realcraft.RealCraft;
import com.realcraft.utils.FireworkUtil;
import com.realcraft.utils.Particles;
import com.realcraft.utils.Title;

public class LobbyParkours implements Runnable {
	RealCraft plugin;

	ArrayList<LobbyParkour> parkours = new ArrayList<LobbyParkour>();

	public LobbyParkours(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,10,10);

		ArrayList<Vector[]> arenaBounds;

		if(plugin.serverName.equalsIgnoreCase("lobby")){
			arenaBounds = new ArrayList<Vector[]>();
			arenaBounds.add(new Vector[]{new Vector(72,54,11),new Vector(91,77,51)});
			parkours.add(new LobbyParkour(
					new Vector[]{new Vector(72,60,11),new Vector(76,65,16)},
					new Vector[]{new Vector(86,75,47),new Vector(90,78,50)},
					new Location(Bukkit.getServer().getWorld("world"),69.5,63.0,14.5,90f,0f),
					new Location(Bukkit.getServer().getWorld("world"),88.0,77.5,48.0),
					new Location(Bukkit.getServer().getWorld("world"),72.5,65.0,13.5),
					arenaBounds
			));

			arenaBounds = new ArrayList<Vector[]>();
			arenaBounds.add(new Vector[]{new Vector(12,42,-124),new Vector(26,56,-93)});
			arenaBounds.add(new Vector[]{new Vector(25,46,-127),new Vector(37,61,-118)});
			parkours.add(new LobbyParkour(
					new Vector[]{new Vector(19,50,-98),new Vector(26,56,-93)},
					new Vector[]{new Vector(22,58,-127),new Vector(25,62,-121)},
					new Location(Bukkit.getServer().getWorld("world"),27.5,53.0,-94.5,-90f,0f),
					new Location(Bukkit.getServer().getWorld("world"),24.0,59.5,-124.0),
					new Location(Bukkit.getServer().getWorld("world"),25.5,55.0,-94.5),
					arenaBounds
			));
		}
	}

	public void onReload(){
	}

	@Override
	public void run(){
		for(LobbyParkour parkour : parkours){
			parkour.run();
		}
	}

	public boolean isPlayerInParkour(Player player){
		for(LobbyParkour parkour : parkours){
			if(parkour.isPlayerInParkour(player)) return true;
		}
		return false;
	}

	private class LobbyParkour implements Listener {
		private Vector [] startArena;
		private Vector [] finishArena;
		private Location finishTeleport;
		private Location finishFirework;
		private ArrayList<Vector[]> arenaBounds = new ArrayList<Vector[]>();
		private HashMap<Player,ParkourPlayer> players = new HashMap<Player,ParkourPlayer>();
		private ParkourPlayer lastRecord = null;
		Hologram recordHologram;

		public LobbyParkour(Vector [] startArena,Vector [] finishArena,Location finishTeleport,Location finishFirework,Location recordLocation,ArrayList<Vector[]> arenaBounds){
			this.startArena = startArena;
			this.finishArena = finishArena;
			this.finishTeleport = finishTeleport;
			this.finishFirework = finishFirework;
			this.arenaBounds = arenaBounds;
			recordHologram = HologramsAPI.createHologram(plugin,recordLocation);
			plugin.getServer().getPluginManager().registerEvents(this,plugin);
			this.updateRecord();
		}

		public void run(){
			for(Player player : plugin.getServer().getOnlinePlayers()){
				if(this.isPlayerInParkour(player)){
					if(player.getGameMode() != GameMode.ADVENTURE){
						this.cancelPlayerParkour(player);
						return;
					}
					for(PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
					plugin.playermanazer.getPlayerInfo(player).setLastLobbyJump(System.currentTimeMillis()+2000);
					player.setAllowFlight(false);
					players.get(player).addTime();
					Title.showActionTitle(player,"§fCas: §e"+players.get(player).getTimeFormat());
					if(this.isPlayerInFinish(player)){
						this.finishPlayerParkour(player);
					}
					else if(!this.isPlayerInArena(player)){
						this.cancelPlayerParkour(player);
						Title.showActionTitle(player," ");
					}
				}
				else if(this.isPlayerInStart(player)){
					if(player.getGameMode() == GameMode.ADVENTURE) this.startPlayerParkour(player);
				}
			}
		}

		public void updateRecord(){
			recordHologram.clearLines();
			if(lastRecord != null){
				recordHologram.insertTextLine(0,"§b§lREKORD DNE");
				recordHologram.insertTextLine(1,"§f§l"+lastRecord.getName());
				recordHologram.insertTextLine(2,"§e§l"+lastRecord.getTimeFormat());
			}
		}

		@EventHandler
		public void PlayerQuitEvent(PlayerQuitEvent event){
			this.cancelPlayerParkour(event.getPlayer());
		}

		@EventHandler(priority=EventPriority.LOW)
		public void PlayerToggleFlightEvent(PlayerToggleFlightEvent event){
			if(event.getPlayer().getGameMode() != GameMode.CREATIVE && this.isPlayerInParkour(event.getPlayer())) event.setCancelled(true);
		}

		public boolean isPlayerInStart(Player player){
			return player.getLocation().toVector().isInAABB(startArena[0],startArena[1]);
		}

		public boolean isPlayerInFinish(Player player){
			return player.getLocation().toVector().isInAABB(finishArena[0],finishArena[1]);
		}

		public boolean isPlayerInParkour(Player player){
			return players.containsKey(player);
		}

		public boolean isPlayerInArena(Player player){
			boolean inArena = false;
			Vector location = player.getLocation().toVector();
			for(Vector [] arena : arenaBounds){
				if(location.isInAABB(arena[0],arena[1])){
					inArena = true;
					break;
				}
			}
			return inArena;
		}

		public void startPlayerParkour(Player player){
			players.put(player,new ParkourPlayer(player));
			player.setFlying(false);
			player.setAllowFlight(false);
			player.setGameMode(GameMode.ADVENTURE);
			player.getInventory().remove(Material.CHEST);
			plugin.lobby.lobbycosmetics.clearCosmetics(player);
			plugin.playermanazer.getPlayerInfo(player).setLastLobbyJump(System.currentTimeMillis()+2000);
			Title.showActionTitle(player,"§fCas: §e"+players.get(player).getTimeFormat());
		}

		public void finishPlayerParkour(final Player player){
			FireworkUtil.spawnFirework(finishFirework,FireworkEffect.Type.BALL,true);
			FireworkUtil.spawnFirework(finishFirework,FireworkEffect.Type.BALL,false);
			Particles.FIREWORKS_SPARK.display(0.5f,0.5f,0.5f,0.2f,64,finishFirework,64);
			player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
			plugin.lobby.lobbychests.givePlayerFragments(player,2);
			final int fragments = plugin.lobby.lobbychests.getPlayerKeyFragments(player);
			Title.showTitle(player,"§aParkour dokoncen",0.2,7,0.2);
			if(fragments >= 10){
				plugin.lobby.lobbychests.givePlayerKeys(player);
				plugin.lobby.lobbychests.resetPlayerFragments(player);
				Title.showSubTitle(player,"§fZiskal jsi §a1 klic§f k magicke truhle.",0,7.2,0.6);
				Title.showActionTitle(player,"§fCelkovy cas: §e"+players.get(player).getTimeFormat(),7*20);
			} else {
				Title.showSubTitle(player,"§fZiskal jsi §e2 ulomky§f klice.",0,7.2,0.6);
				Title.showActionTitle(player,"§fCelkovy cas: §e"+players.get(player).getTimeFormat(),7*20);
			}
			if(lastRecord == null || lastRecord.getTime() > players.get(player).getTime()){
				lastRecord = players.get(player);
				plugin.getServer().broadcastMessage("§b[Parkour]§r §6"+player.getName()+"§f vyskakal v rekordnim case §e"+lastRecord.getTimeFormat()+"§f.");
				this.updateRecord();
			}
			players.remove(player);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
				@Override
				public void run(){
					player.teleport(finishTeleport,TeleportCause.UNKNOWN);
					cancelPlayerParkour(player);
					player.playSound(player.getLocation(),Sound.ENTITY_BAT_TAKEOFF,0.5f,1);
				}
			},3*20);
		}

		public void cancelPlayerParkour(Player player){
			players.remove(player);
			if(plugin.playermanazer.getPlayerInfo(player).getRank() >= 20){
				player.setAllowFlight(true);
				player.setFlying(false);
			}
			ItemStack chest = new ItemStack(Material.CHEST,1);
			ItemMeta meta = chest.getItemMeta();
			meta.setDisplayName("§e§lDoplnky");
			chest.setItemMeta(meta);
			player.getInventory().setItem(4,chest);
			this.updateRecord();
		}

		private class ParkourPlayer {
			private String name;
			private int time;

			public ParkourPlayer(Player player){
				this.name = player.getName();
			}

			public String getName(){
				return this.name;
			}

			public int getTime(){
				return this.time;
			}

			public void addTime(){
				this.time ++;
			}

			public String getTimeFormat(){
				int timeTmp = this.time/2;
				int minutes = (int)(Math.floor(((double)timeTmp)/60)%60);
				int seconds = timeTmp%60;
				return (minutes < 10 ? "0" : "")+minutes+":"+(seconds < 10 ? "0" : "")+seconds;
			}
		}
	}
}