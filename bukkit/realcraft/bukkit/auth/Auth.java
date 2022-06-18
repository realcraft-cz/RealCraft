package realcraft.bukkit.auth;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import realcraft.bukkit.RealCraft;
import realcraft.bukkit.sockets.SocketData;
import realcraft.bukkit.sockets.SocketDataEvent;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.Title;
import realcraft.share.ServerType;
import realcraft.share.users.User;

public class Auth implements Listener {
	RealCraft plugin;

	double x,y,z;
	float yaw,pitch;

	boolean enabled = true;

	public Auth(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
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
		if(!Users.getUser(player).isLogged()){
			movePlayerToSpawn(player,false);
		}
	}

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	public void onPlayerBlockPlace(BlockPlaceEvent event){
		if(!enabled) return;
		Player player = event.getPlayer();
		if(!Users.getUser(player).isLogged()){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	public void onPlayerBlockDamage(BlockDamageEvent event){
		if(!enabled) return;
		Player player = event.getPlayer();
		if(!Users.getUser(player).isLogged()){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.NORMAL,ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		if(!enabled) return;
		Player player = event.getPlayer();
		if(!Users.getUser(player).isLogged()){
			event.setCancelled(true);
		}
	}

	public void movePlayerToSpawn(final Player player,boolean loggedin){
		player.teleport(new Location(plugin.getServer().getWorld("world") ,x, y, z, yaw, pitch));
		if(!Users.getUser(player).isLogged()){
			for(Player player2 : plugin.getServer().getOnlinePlayers()) player.hidePlayer(player2);
			Title.showTitle(player,"§3RealCraft.cz",1,30,1);
			if(Users.getUser(player).isRegistered()){
				Title.showSubTitle(player,"§6Prihlas se pomoci /login <heslo>",1,30,1);
			} else {
				Title.showSubTitle(player,"§6Registruj se /register <heslo> <heslo>",1,30,1);
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,Integer.MAX_VALUE,1,false,false),true);
		} else {
			for(Player player2 : plugin.getServer().getOnlinePlayers()) player.showPlayer(player2);
			for(PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
			if(loggedin == false){
				Bukkit.getScheduler().runTaskLater(plugin,new Runnable(){
					@Override
					public void run(){
						AuthLoginEvent callevent = new AuthLoginEvent(player);
						Bukkit.getServer().getPluginManager().callEvent(callevent);
					}
				},20);
			}
		}
	}

	@EventHandler
	public void SocketDataEvent(SocketDataEvent event){
		SocketData data = event.getData();
		if(data.getChannel().equalsIgnoreCase(Users.CHANNEL_BUNGEE_LOGIN)){
			User user = Users.getUser(data.getInt("id"));
			Player player = Users.getPlayer(user);
			if(player != null){
				AuthLoginEvent callevent = new AuthLoginEvent(player);
				Bukkit.getServer().getPluginManager().callEvent(callevent);
				movePlayerToSpawn(player,true);
				if(RealCraft.getServerType() == ServerType.LOBBY){
					player.resetTitle();
				}
			}
		}
	}
}