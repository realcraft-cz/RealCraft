package realcraft.bukkit.survival;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.chat.ChatCommandSpy;

import java.util.HashMap;

public class PassiveMode implements Listener {

	private static final int TRIGGER_TIMEOUT = 60*1000;
	private static HashMap<Player,PlayerPassiveMode> passives = new HashMap<Player,PlayerPassiveMode>();

	public PassiveMode(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	public static PlayerPassiveMode getPassiveMode(Player player){
		if(!passives.containsKey(player)) passives.put(player,new PlayerPassiveMode(player));
		return passives.get(player);
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event){
		passives.put(event.getPlayer(),new PlayerPassiveMode(event.getPlayer()));
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

	@EventHandler
	public void PlayerBucketEmptyEvent(PlayerBucketEmptyEvent event){
		if(event.getBucket() == Material.LAVA_BUCKET){
			for(Entity entity : event.getPlayer().getWorld().getNearbyEntities(event.getPlayer().getLocation(),16,16,16)){
				if(entity.getEntityId() != event.getPlayer().getEntityId() && entity.getType() == EntityType.PLAYER && PassiveMode.getPassiveMode((Player)entity).isEnabled()){
					event.getPlayer().sendMessage("§6[§7PassiveMode§6] §cNemuzes polozit lavu v blizkosti hrace.");
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void PlayerFishEvent(PlayerFishEvent event){
		if(event.getCaught() != null && event.getCaught().getType() == EntityType.PLAYER){
			Player player = (Player) event.getCaught();
			if(PassiveMode.getPassiveMode(player).isEnabled()){
				event.setCancelled(true);
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
			if(!enabled){
				if(player.getAllowFlight()) player.setAllowFlight(false);
			}
		}
	}

	private static class PassiveModeTimeoutException extends Exception {
	}

	private static class PassiveModeRangeException extends Exception {
	}

	private static class PassiveModeFlyingException extends Exception {
	}
}