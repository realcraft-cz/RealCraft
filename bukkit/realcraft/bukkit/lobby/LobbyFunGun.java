package realcraft.bukkit.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.auth.AuthLoginEvent;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.Particles;
import realcraft.bukkit.utils.RandomUtil;

import java.util.HashMap;

public class LobbyFunGun implements Listener {

	private static final int FIRE_TIMEOUT = 2000;
	private HashMap<Player,LobbyFunGunPlayer> players = new HashMap<>();

	public LobbyFunGun(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	private LobbyFunGunPlayer getFunGunPlayer(Player player){
		if(!players.containsKey(player)) players.put(player,new LobbyFunGunPlayer());
		return players.get(player);
	}

	private ItemStack getFunGunItem(Player player){
		ItemStack item = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§lFunGun§r - "+this.getFunGunPlayer(player).getType().getName());
		item.setItemMeta(meta);
		return item;
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Player player = event.getPlayer();
		player.getInventory().setItem(1,this.getFunGunItem(player));
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(Users.getUser(player).isLogged() && player.getWorld().getName().equalsIgnoreCase("world")){
			player.getInventory().setItem(1,this.getFunGunItem(player));
		}
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		Player player = event.getPlayer();
		if(event.getFrom().getName().equalsIgnoreCase("world")){
			player.getInventory().remove(this.getFunGunItem(player));
		}
		else if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			player.getInventory().setItem(1,this.getFunGunItem(player));
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getInventory().getItemInMainHand().getType() != Material.BLAZE_ROD) return;
		event.setCancelled(true);
		if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
			this.switchFunGun(player);
		}
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			this.fireFunGun(player);
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			Player player = (Player)event.getWhoClicked();
			ItemStack item = event.getCurrentItem();
			if(event.getClick() == ClickType.NUMBER_KEY) item = player.getInventory().getItem(event.getHotbarButton());
			if(item != null && item.isSimilar(this.getFunGunItem(player))){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void InventoryDragEvent(InventoryDragEvent event){
		if(event.getWhoClicked() instanceof Player && event.getOldCursor().isSimilar(this.getFunGunItem((Player)event.getWhoClicked()))){
			event.setCancelled(true);
		}
	}

	private void fireFunGun(Player player){
		if(this.getFunGunPlayer(player).getLastFired()+FIRE_TIMEOUT < System.currentTimeMillis()){
			if(this.getFunGunPlayer(player).getType() == LobbyFunGunType.MEOW){
				for(int i=0;i<3;i++){
					Snowball snowball = (Snowball) player.getWorld().spawnEntity(player.getEyeLocation(),EntityType.SNOWBALL);
					snowball.setShooter(player);
					snowball.setCustomName("fungun");
					snowball.setCustomNameVisible(false);
					Vector vector = player.getLocation().getDirection().clone();
					vector.setX(vector.getX() + RandomUtil.getRandomDouble(-0.01,0.01));
					vector.setY(vector.getY() + RandomUtil.getRandomDouble(-0.01,0.01));
					vector.setZ(vector.getZ() + RandomUtil.getRandomDouble(-0.01,0.01));
					snowball.setVelocity(vector.multiply(1.5));
				}
			}
			else if(this.getFunGunPlayer(player).getType() == LobbyFunGunType.SHULKER){
				ShulkerBullet spit = (ShulkerBullet)player.getWorld().spawnEntity(player.getEyeLocation(),EntityType.SHULKER_BULLET);
				spit.setShooter(player);
				Vector vector = player.getLocation().getDirection().clone();
				vector.setX(vector.getX()+RandomUtil.getRandomDouble(-0.01,0.01));
				vector.setY(vector.getY()+RandomUtil.getRandomDouble(-0.01,0.01));
				vector.setZ(vector.getZ()+RandomUtil.getRandomDouble(-0.01,0.01));
				spit.setVelocity(vector.multiply(1.5));
			}
			this.getFunGunPlayer(player).setLastFired(System.currentTimeMillis());
		}
	}

	private void switchFunGun(Player player){
		this.getFunGunPlayer(player).setType(this.getFunGunPlayer(player).getType().getNext());
		player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,2f);
		player.getInventory().setItem(1,this.getFunGunItem(player));
	}

	@EventHandler
	public void ProjectileHitEvent(ProjectileHitEvent event){
		Projectile projectile = event.getEntity();
		if(projectile.getType() == EntityType.SNOWBALL && projectile.getWorld().getName().equalsIgnoreCase("world") && projectile.getCustomName() != null && projectile.getCustomName().equalsIgnoreCase("fungun")){
			Location location = projectile.getLocation();
			event.getEntity().remove();
			Particles.LAVA.display(1.0f,1f,1.0f,0f,3,location,128);
			Particles.HEART.display(0.8f,0.8f,0.8f,0f,4,location,128);
			location.getWorld().playSound(location,Sound.ENTITY_CAT_AMBIENT,0.1f,1.0f);
			location.getWorld().playSound(location,Sound.ENTITY_CAT_AMBIENT,0.1f,1.0f);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Snowball){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		if(event.getItemDrop().getItemStack().getType() == Material.BLAZE_ROD){
			event.setCancelled(true);
		}
	}

	private enum LobbyFunGunType {
		MEOW, SHULKER;

		public String getName(){
			switch(this){
				case MEOW: return "§cMeow";
				case SHULKER: return "§3Shulker";
			}
			return null;
		}

		public LobbyFunGunType getNext(){
			if(this == MEOW) return SHULKER;
			return MEOW;
		}
	}

	private class LobbyFunGunPlayer {

		private LobbyFunGunType type = LobbyFunGunType.MEOW;
		private long lastFired = 0;

		public LobbyFunGunPlayer(){
		}

		public LobbyFunGunType getType(){
			return type;
		}

		public void setType(LobbyFunGunType type){
			this.type = type;
		}

		public long getLastFired(){
			return lastFired;
		}

		public void setLastFired(long lastFired){
			this.lastFired = lastFired;
		}
	}
}