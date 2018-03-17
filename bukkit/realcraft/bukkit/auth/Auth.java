package realcraft.bukkit.auth;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.playermanazer.PlayerManazer.PlayerInfo;
import realcraft.bukkit.utils.Title;

public class Auth implements Listener, PluginMessageListener {
	RealCraft plugin;

	double x,y,z;
	float yaw,pitch;

	boolean enabled = true;

	public Auth(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin,"RealCraftAuth",this);
		x = plugin.config.getDouble("authspawn.x");
		y = plugin.config.getDouble("authspawn.y");
		z = plugin.config.getDouble("authspawn.z");
		yaw = (float)plugin.config.getDouble("authspawn.yaw");
		pitch = (float)plugin.config.getDouble("authspawn.pitch");
	}

	public void onReload(){
	}

	public Location getServerSpawn(){
		return new Location(plugin.getServer().getWorld("world") ,x, y, z, yaw, pitch);
	}

	@EventHandler(priority=EventPriority.LOWEST,ignoreCancelled = true)
	public void onPlayerLogin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		movePlayerToSpawn(player,false);
	}

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event){
		if(!enabled) return;
		Player player = event.getPlayer();
		if(plugin.playermanazer.getPlayerInfo(player).isLogged() == false){
			movePlayerToSpawn(player,false);
		}
	}

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	public void onPlayerBlockPlace(BlockPlaceEvent event){
		if(!enabled) return;
		Player player = event.getPlayer();
		if(plugin.playermanazer.getPlayerInfo(player).isLogged() == false){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	public void onPlayerBlockDamage(BlockDamageEvent event){
		if(!enabled) return;
		Player player = event.getPlayer();
		if(plugin.playermanazer.getPlayerInfo(player).isLogged() == false){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		if(!enabled) return;
		Player player = event.getPlayer();
		if(plugin.playermanazer.getPlayerInfo(player).isLogged() == false){
			event.setCancelled(true);
		}
	}

	public void movePlayerToSpawn(final Player player,boolean loggedin){
		player.teleport(new Location(plugin.getServer().getWorld("world") ,x, y, z, yaw, pitch));
		if(plugin.playermanazer.getPlayerInfo(player).isLogged() == false){
			for(Player player2 : plugin.getServer().getOnlinePlayers()) player.hidePlayer(player2);
			Title.showTitle(player,"§3RealCraft.cz",1,30,1);
			if(plugin.playermanazer.getPlayerInfo(player).getId() != 0){
				Title.showSubTitle(player,"§6Prihlas se pomoci /login <heslo>",1,30,1);
			} else {
				Title.showSubTitle(player,"§6Registruj se /register <heslo> <heslo>",1,30,1);
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,Integer.MAX_VALUE,1,false,false),true);
		} else {
			for(Player player2 : plugin.getServer().getOnlinePlayers()) player.showPlayer(player2);
			for(PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
			if(loggedin == false){
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
					@Override
					public void run(){
						AuthLoginEvent callevent = new AuthLoginEvent(player);
						Bukkit.getServer().getPluginManager().callEvent(callevent);
					}
				},20);
			}
		}
	}


	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message){
		if(!channel.equals("RealCraftAuth")) return;
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if(subchannel.equals("LoggedIn")){
			UUID uuid = UUID.fromString(in.readUTF());
			if(uuid != null){
				player = plugin.getServer().getPlayer(uuid);
				if(player != null){
					plugin.playermanazer.getPlayerInfo(player).setLogged(true);
					AuthLoginEvent callevent = new AuthLoginEvent(player);
					Bukkit.getServer().getPluginManager().callEvent(callevent);
					movePlayerToSpawn(player,true);
					if(plugin.serverName.equalsIgnoreCase("lobby")){
						Title.showTitle(player,"",0,0,0);
						Title.showSubTitle(player,"",0,0,0);
					}
				}
			}
		}
		else if(subchannel.equals("LoggedOut")){
			UUID uuid = UUID.fromString(in.readUTF());
			if(uuid != null){
				PlayerInfo info = plugin.playermanazer.getPlayerInfo(uuid);
				if(info != null){
					plugin.playermanazer.getPlayerInfo(uuid).setLogged(false);
					player = plugin.getServer().getPlayer(uuid);
					if(player != null){
						movePlayerToSpawn(player,false);
					}
				}
			}
		}
	}
}