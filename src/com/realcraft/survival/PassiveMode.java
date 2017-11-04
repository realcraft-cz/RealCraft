package com.realcraft.survival;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.realcraft.RealCraft;
import com.realcraft.chat.ChatCommandSpy;

public class PassiveMode implements Listener {

	private static final int TRIGGER_TIMEOUT = 60*1000;
	private static HashMap<Player,PlayerPassiveMode> passives = new HashMap<Player,PlayerPassiveMode>();
	//private Scoreboard scoreboard;

	public PassiveMode(){
		/*scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Team team = scoreboard.getTeam("teamVisible");
		if(team == null){
			team = scoreboard.registerNewTeam("teamVisible");
			team.setCanSeeFriendlyInvisibles(true);
		}*/
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		/*ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.HIGH,PacketType.Play.Server.ENTITY_METADATA){
			@Override
			public void onPacketSending(PacketEvent event){
				if(event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA){
					if(event.getPlayer().getEntityId() == event.getPacket().getIntegers().read(0)){
						List<WrappedWatchableObject> watchableObjectList = event.getPacket().getWatchableCollectionModifier().read(0);
						for(WrappedWatchableObject metadata : watchableObjectList){
							if(metadata.getIndex() == 0){
								byte value = (byte)metadata.getValue();
								if(value >= 30) value -= 30;
								else if(value == -96) value = -128;
								metadata.setValue(value);
							}
						}
						event.getPacket().getWatchableCollectionModifier().write(0,watchableObjectList);
					}
					if(PassiveMode.getPassiveMode(event.getPlayer()).isEnabled()){
						if(!((CraftPlayer)event.getPlayer()).getHandle().isInvisible()){
							((CraftPlayer)event.getPlayer()).getHandle().setInvisible(true);
						}
					}
				}
			}
		});*/
	}

	public static PlayerPassiveMode getPassiveMode(Player player){
		if(!passives.containsKey(player)) passives.put(player,new PlayerPassiveMode(player));
		return passives.get(player);
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		passives.put(event.getPlayer(),new PlayerPassiveMode(event.getPlayer()));
		/*event.getPlayer().setScoreboard(scoreboard);
		Team team = scoreboard.getPlayerTeam(event.getPlayer());
        if(team == null){
        	scoreboard.getTeam("teamVisible").addPlayer(event.getPlayer());
        }*/
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		/*event.getPlayer().setScoreboard(scoreboard);
		Team team = scoreboard.getPlayerTeam(event.getPlayer());
        if(team == null){
        	scoreboard.getTeam("teamVisible").addPlayer(event.getPlayer());
        }*/
	}

	@EventHandler
	public void PlayerToggleFlightEvent(PlayerToggleFlightEvent event){
		if(!PassiveMode.getPassiveMode(event.getPlayer()).isEnabled()){
			if(event.isFlying() && event.getPlayer().getGameMode() == GameMode.SURVIVAL){
				event.setCancelled(true);
				event.getPlayer().setFlying(false);
				event.getPlayer().setAllowFlight(false);
				event.getPlayer().sendMessage("§6[§7PassiveMode§6] §cBez zapnuteho passive modu nelze letat.");
			}
		}
	}

	@EventHandler(priority=EventPriority.LOW)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1).toLowerCase();
		if(command.equalsIgnoreCase("pvp") || command.equalsIgnoreCase("passive") || command.equalsIgnoreCase("pasive") || command.equalsIgnoreCase("passivemod") || command.equalsIgnoreCase("passivemode")){
			try {
				PassiveMode.getPassiveMode(player).toggle();
				if(PassiveMode.getPassiveMode(player).isEnabled()){
					player.sendMessage("§6[§7PassiveMode§6] §aZapnuto§7 (hra bez boje)");
				} else {
					player.sendMessage("§6[§7PassiveMode§6] §cVypnuto§7 (boj mezi neprateli)");
				}
				player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
			} catch (PassiveModeTimeoutException e){
				player.sendMessage("§6[§7PassiveMode§6] §cPrikaz muzete znovu pouzit za "+(((PassiveMode.getPassiveMode(player).getLastTrigger()+TRIGGER_TIMEOUT)-System.currentTimeMillis())/1000)+" sekund.");
				player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			} catch (PassiveModeRangeException e){
				player.sendMessage("§6[§7PassiveMode§6] §cPrikaz muzete pouzit, pokud v dosahu neni zadny nepritel.");
				player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			} catch (PassiveModeFlyingException e){
				player.sendMessage("§6[§7PassiveMode§6] §cPrikaz nemuzete pouzit pri letani.");
				player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
			}
			ChatCommandSpy.sendCommandMessage(player,command);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player){
			if(event.getDamager() instanceof Player){
				Player player = (Player) event.getEntity();
				Player killer = (Player) event.getDamager();
				if(PassiveMode.getPassiveMode(player).isEnabled() || PassiveMode.getPassiveMode(killer).isEnabled()){
					event.setCancelled(true);
				}
			}
			else if(event.getDamager() instanceof Arrow){
				Arrow arrow = (Arrow)event.getDamager();
				if(arrow.getShooter() instanceof Player){
					Player player = (Player) event.getEntity();
					Player killer = (Player) arrow.getShooter();
					if(PassiveMode.getPassiveMode(player).isEnabled() || PassiveMode.getPassiveMode(killer).isEnabled()){
						event.setCancelled(true);
						player.setFireTicks(0);
					}
				}
			}
		}
	}

	private static class PlayerPassiveMode {

		private Player player;
		private boolean enabled = false;
		private long lastTrigger = 0;

		public PlayerPassiveMode(Player player){
			this.player = player;
		}

		public boolean isEnabled(){
			return enabled;
		}

		public long getLastTrigger(){
			return lastTrigger;
		}

		public void toggle() throws PassiveModeTimeoutException, PassiveModeRangeException, PassiveModeFlyingException {
			if(lastTrigger+TRIGGER_TIMEOUT >= System.currentTimeMillis()) throw new PassiveModeTimeoutException();
			if(enabled && player.isFlying()) throw new PassiveModeFlyingException();
			if(!enabled){
				for(Entity entity : player.getNearbyEntities(3*16,3*16,3*16)){
					if(entity instanceof Player){
						if(!PassiveMode.getPassiveMode((Player)entity).isEnabled()) throw new PassiveModeRangeException();
					}
				}
			}
			enabled = !enabled;
			lastTrigger = System.currentTimeMillis();
			if(enabled){
				//((CraftPlayer)player).getHandle().setInvisible(true);
			} else {
				//((CraftPlayer)player).getHandle().setInvisible(false);
				if(player.getAllowFlight()) player.setAllowFlight(false);
			}
		}
	}

	@SuppressWarnings("serial")
	private static class PassiveModeTimeoutException extends Exception {
		public PassiveModeTimeoutException(){
			super();
		}
	}

	@SuppressWarnings("serial")
	private static class PassiveModeRangeException extends Exception {
		public PassiveModeRangeException(){
			super();
		}
	}

	@SuppressWarnings("serial")
	private static class PassiveModeFlyingException extends Exception {
		public PassiveModeFlyingException(){
			super();
		}
	}
}