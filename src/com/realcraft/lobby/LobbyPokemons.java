package com.realcraft.lobby;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Sets;
import com.realcraft.RealCraft;
import com.realcraft.auth.AuthLoginEvent;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.utils.ItemUtil;
import com.realcraft.utils.Particles;
import com.realcraft.utils.RandomUtil;

import net.minecraft.server.v1_11_R1.EntityInsentient;
import net.minecraft.server.v1_11_R1.PathEntity;
import net.minecraft.server.v1_11_R1.PathfinderGoalSelector;

public class LobbyPokemons implements Listener {

	RealCraft plugin;
	private static final String invName = "Pokemon";
	private static ItemStack item = null;
	private HashMap<Player,LobbyPokemon> pokemons = new HashMap<Player,LobbyPokemon>();

	public LobbyPokemons(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	public void onDisable(){
		for(LobbyPokemon pokemon : pokemons.values()){
			pokemon.remove();
		}
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItem(){
		if(item == null){
			item = new ItemStack(Material.MONSTER_EGG,1,(short)0,(byte)98);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§e§l"+invName);
			item.setItemMeta(meta);
		}
		return item;
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Player player = event.getPlayer();
		player.getInventory().setItem(5,this.getItem());
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(PlayerManazer.getPlayerInfo(player).isLogged() && player.getWorld().getName().equalsIgnoreCase("world")){
			player.getInventory().setItem(5,this.getItem());
		}
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event){
		this.removePokemon(event.getPlayer());
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		if(event.getFrom().getName().equalsIgnoreCase("world")){
			event.getPlayer().getInventory().remove(this.getItem().getType());
			this.removePokemon(event.getPlayer());
		}
		else if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
				@Override
				public void run(){
					event.getPlayer().getInventory().setItem(5,LobbyPokemons.this.getItem());
				}
			},20);
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equalsIgnoreCase("world") && player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(this.getItem().getItemMeta().getDisplayName()) && (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
			event.setCancelled(true);
			if(PlayerManazer.getPlayerInfo(player).isLogged()){
				this.openMenu(player);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world") && event.getItemDrop().getItemStack().getType() == Material.MONSTER_EGG){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getInventory().getName().equalsIgnoreCase(invName)){
			if(event.getWhoClicked() instanceof Player && ((Player)event.getWhoClicked()).getWorld().getName().equalsIgnoreCase("world")){
				event.setCancelled(true);
				Player player = (Player) event.getWhoClicked();
			}
		}
		else if(event.getWhoClicked() instanceof Player && ((Player)event.getWhoClicked()).getWorld().getName().equalsIgnoreCase("world") && event.getSlotType() == SlotType.QUICKBAR && event.getCurrentItem().getType() == Material.ENCHANTMENT_TABLE){
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			if(PlayerManazer.getPlayerInfo(player).isLogged()){
				this.openMenu(player);
			}
		}
	}

	@EventHandler
	public void InventoryCloseEvent(InventoryCloseEvent event){
		if(event.getInventory().getName().equalsIgnoreCase(invName)){
			if(event.getPlayer() instanceof Player && ((Player)event.getPlayer()).getWorld().getName().equalsIgnoreCase("world")){
				Player player = (Player) event.getPlayer();
			}
		}
	}

	private void openMenu(Player player){
		this.createPokemon(player);
	}

	private void createPokemon(Player player){
		if(pokemons.containsKey(player)) this.removePokemon(player);
		else pokemons.put(player,new LobbyPokemon(player,LobbyPokemonType.values()[RandomUtil.getRandomInteger(0,LobbyPokemonType.values().length-1)]));
	}

	private void removePokemon(Player player){
		if(pokemons.containsKey(player)){
			pokemons.get(player).remove();
			pokemons.remove(player);
		}
	}

	private class LobbyPokemon implements Listener {

		private Player player;
		private LobbyPokemonType type;
		private Entity entity;
		private BukkitTask taskMove;
		private BukkitTask taskEffect;
		private LobbyPokemonState state = LobbyPokemonState.FOLLOW;

		public LobbyPokemon(Player player,LobbyPokemonType type){
			this.player = player;
			this.type = type;
			Bukkit.getServer().getPluginManager().registerEvents(this,RealCraft.getInstance());
			taskMove = Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					LobbyPokemon.this.run();
				}
			},5,5);
			taskEffect = Bukkit.getScheduler().runTaskTimerAsynchronously(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					LobbyPokemon.this.effect();
				}
			},3,3);
			this.create();
		}

		private void create(){
			Location location = player.getLocation();
			location.setPitch(0f);
			entity = player.getWorld().spawnEntity(location,EntityType.ZOMBIE);
			if(entity != null){
				((Zombie)entity).setSilent(true);
				((Zombie)entity).setBaby(true);
				((Zombie)entity).getEquipment().setHelmet(type.getItemStack());
				((Zombie)entity).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,Integer.MAX_VALUE,1));
			}
			else this.remove();
		}

		private void run(){
			if(state == LobbyPokemonState.FOLLOW){
				Location targetLocation = player.getLocation();
				try {
					double speed = 1D;
					int distance = (int) player.getLocation().distance(entity.getLocation());
					if(distance > 2.0){
						((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
						PathEntity path;
						path = ((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(targetLocation.getX(),targetLocation.getY(),targetLocation.getZ());
						if(distance > 20 && player.isOnGround()){
							((CraftEntity)entity).getHandle().setLocation(targetLocation.getBlockX(),targetLocation.getBlockY(),targetLocation.getBlockZ(),0,0);
						}
						if (path != null){
							((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(path,speed);
							((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
						}
					} else {
						((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(0);
						this.clearPathfinders(entity);
					}
				} catch (Exception exception){
				}
			}
		}

		private void clearPathfinders(org.bukkit.entity.Entity entity) {
			net.minecraft.server.v1_11_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
			try {
				Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
				bField.setAccessible(true);
				Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
				cField.setAccessible(true);
				bField.set(((EntityInsentient) nmsEntity).goalSelector, Sets.newLinkedHashSet());
				bField.set(((EntityInsentient) nmsEntity).targetSelector, Sets.newLinkedHashSet());
				cField.set(((EntityInsentient) nmsEntity).goalSelector, Sets.newLinkedHashSet());
				cField.set(((EntityInsentient) nmsEntity).targetSelector, Sets.newLinkedHashSet());
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}

		private void effect(){
			if(state == LobbyPokemonState.FOLLOW) Particles.SNOW_SHOVEL.display(0.1f,0f,0.1f,0,4,entity.getLocation().clone().add(0,0.7,0),64);
		}

		@EventHandler
		private void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
			if(event.getHand().equals(EquipmentSlot.HAND) && event.getRightClicked().equals(entity)){
				event.setCancelled(true);
				this.interactByEntity(event.getPlayer());
			}
		}

		@EventHandler
		public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
			if(event.getEntity().equals(entity)){
				event.setCancelled(true);
				this.interactByEntity(event.getDamager());
			}
		}

		private void interactByEntity(Entity damager){
			if(damager instanceof Player && damager.equals(player)){
				if(state == LobbyPokemonState.FOLLOW){
					state = LobbyPokemonState.SITTING;
					entity.setGravity(false);
					Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
						public void run(){
							((CraftEntity)entity).getHandle().setLocation(entity.getLocation().getX(),entity.getLocation().getY()-0.7,entity.getLocation().getZ(),0,0);
						}
					});
				} else {
					state = LobbyPokemonState.FOLLOW;
					entity.setGravity(true);
					Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
						public void run(){
							((CraftEntity)entity).getHandle().setLocation(entity.getLocation().getX(),entity.getLocation().getY()+1.0,entity.getLocation().getZ(),0,0);
						}
					});
				}
			}
		}

		public void remove(){
			HandlerList.unregisterAll(this);
			if(taskMove != null) taskMove.cancel();
			if(taskEffect != null) taskEffect.cancel();
			if(entity != null) entity.remove();
		}
	}

	private enum LobbyPokemonState {
		FOLLOW, SITTING;
	}

	private enum LobbyPokemonType {
		MAGIKARP("Magikarp","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmY1OGZiN2NiZjlmOGRjZmMzYmM5ZDYxYzdjYjViMjI5YmY0OWRiMTEwMTMzNmZmZGMyZDA4N2MwYjk0MTYyIn19fQ=="),
		SQUIRTLE("Squirtle","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjUzZWJjOTc2Y2I2NzcxZjNlOTUxMTdiMzI2ODQyZmY3ODEyYzc0MGJlY2U5NmJiODg1ODM0NmQ4NDEifX19"),
		UNFEZANT("Unfezant","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFmZDFjODNhZjdlN2U1MjIxZWZiMWY0MTQ5ZjdkMTZmNTk4MGEyNTFmMGE1ZDcxYWJlMzY2OTAyMjhhIn19fQ=="),
		HYDREIGON("Hydreigon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM5NzVhYWFkMmRiYzMxN2UzNzg3YmRlYmFiOWZiMWViNDUyNmIzODJmY2NkZmViMTgxMzM5YjIxNTRmYmEzIn19fQ=="),
		EELEKTROSS("Eelektross","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmU1ZWY2MzRjN2VlOTczY2IwNGZlNDFlMWRiYjJmMDYyYjEyYzA3MjYxNDNkM2JmMjMyYjIzODFmMjRiIn19fQ=="),
		SWANNA("Swanna","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGM2MTJkNTQzMzJlY2RhYTQzOGYyMWY3YWZkNTQ0M2U5MTM1NWNkMWM2ODQ0ZjY4YTU3YmVjNmE5M2MzZmExIn19fQ=="),
		MAGMAR("Magmar","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY0NDY2MGU1NGNjMWZlMzE1YTk5Yjk0ZTE5OTExNWM1NGNkNzdjYmY3YzZhZWYyNDcwZGJlZjRmNjhmMzI3In19fQ=="),
		LIEPARD("Liepard","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2U4NTI0ZjZhYzc2MjQ4OTViY2EyM2FlN2Q2Nzc3ZGE1YWMxYWQwZDcyNmJmNGU1Njg0Y2E2ZmRiYzI5MjliIn19fQ=="),
		LILLIGANT("Lilligant","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNlMWZhYTk5M2E0N2JkYTliYzdkZTBjNjkzY2E2YzgyNzI2NjI2YmQyNWE3YzA2NGQ3YWY3Nzk2MzZhIn19fQ=="),
		BISHARP("Bisharp","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVmZTg3NzA0MmRlMzAyZjg4ZGI3ZGUyYWMwN2NlY2RkM2NiOGI3NzFkNGMwNTVhMzcyMzAzMzIxNWQ1YyJ9fX0="),
		GYARADOS("Gyarados","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFiOTNhZjY2OGNiODNlMzc5ZTllZGJjZGM0NTMyZjEyOTRmOTBjYjEzZGU2YTU4MmVmYWI4NzY5NmMzNmRkIn19fQ=="),
		BEARTIC("Beartic","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I2MDhlZDQ1MjM4MjVhNjFmNGJhYWI4OTZlMzhlYmRiYjgzZWUxNDlkNDQwYjlhNGUxMmJjOWVmZmI0YSJ9fX0="),
		SCRAFTY("Scrafty","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmU1MTk1OThjMzc2ZGI1MWMyZGRkMzM4NzgyOWQwNWMzNTY5YTBjN2YxOWM1MDFmZGM5Njc1Njc2MWVkMSJ9fX0="),
		DARMANITAN("Darmanitan","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWViZWZmYTQ2MzU1NzU4Nzk1YTE1MzYzOWZjMTQxMWZkZmRkOTFlYzEzYzEyNjZjZTZiODc1ODVlMmZjMSJ9fX0="),
		AXEW("Axew","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDE3Y2MxY2I4NDkyNDkzNTQ4YzkwZDcxNGMyM2U4ZTcxYTFmY2QwZDQ3YTQzYzExNDk5ZDJjMmJjNDIyIn19fQ=="),
		NURSEJOY("Nurse Joy","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjNkZTM4YTFjZWVhNmQ5NDkzZGYxOWE4YjU1YmIyMzg3MTFjZDVkYTRmNDM1ZDJlYzAyNjM3NmQ4NzQ2NDcifX19"),
		PATRAT("Patrat","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmJmZTRhNTliMTY0NTQ4NzMyZmQ1Yjc1NGYyNjY0MTE5NjlhMmMyZmViMDhhODliNDBhMTI5MzI0NGFiZWMifX19"),
		THROH("Throh","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlmM2FjYjkzN2VlYTVmZjQ0N2U0ZDQ1MzA4NjM1ZjZhYzc5MjNiMGE1MDRjY2QyYzhmNjcxODUzYjJlZGMifX19"),
		SAWK("Sawk","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWVmOWMxZDVmM2JiNGIxOTcyM2JkZDg1ZjIxOTY3NWRhMGZjOWRlYzVkOGFiMmU5NGEzZDlmY2FiMmQ1NzYifX19"),
		ZEBSTRIKA("Zebstrika","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRlOGE0N2U1NTI5NGVhZTY2ZTI1MDI3NGJhYTE1YzExNTU0YzA2MjRiNjMzY2MxZDE3ODc4NzVlNGIxMjYifX19"),
		CHARMELEON("Charmeleon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDMxNzExZjMzNjY1YjNlMWU5OWVkOGY1ZjUwYTYzZTNmNmRlYzcyMWFmMjM5MWUzNGY4M2UxNWNlMjdhZiJ9fX0="),
		SYLVEON("Sylveon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDc3YTEyMmU2NjI4NmJlODUwNGU3Mjk3OWI0NzkxMmJiZTY5MTM2YzJlYWM2MWJhYTJiNzYzMWQ3ZTkyNmIifX19"),
		LEAFEON("Leafeon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc5ZGFhMjFmOWVlZWI2ZGM3ZjY1NmIwNTVkNmFjMzA5MGIzYzU4NmNiZTQxMWI5MWZiOTgyOTg1MGRhN2M4NSJ9fX0="),
		GLACEON("Glaceon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGRjNTNiNzUzZGVlMWFmOTE4MTljZjI5OWJiNDRlZTk2ODI5MzYxMTQ5YTg4N2IwMWFkOTc0MWNjNzhiM2UifX19"),
		UMBREON("Umbreon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVhOGY2NzcyMmJlZjA5M2M2N2NjZTE0NTg3ZDY3YjM3NWUyN2E4MmZhNzc3YTg4MjE4YmExMWFmOWMxM2IifX19"),
		ESPEON("Espeon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNjMzc1MTAyYmE0MTkxNjI5N2Q3MjQ1MmNjNDgyYzc1Mjg1Yjk4ZTQzZGI2N2VlNWY0OTkyYWVhMDQzZTJiMSJ9fX0="),
		FLAREON("Flareon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTM2NTg3MmViZWE1ZWE5ZDE4MDQ5YWIxY2RiOGY1ODZmNDI5ZTc4NDYxMGEzN2ZiZmI2NmI2ZGM2MzcyIn19fQ=="),
		VAPOREON("Vaporeon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFiNzllZTZiNjFjMTFlNmExMjliMTljNzdiZDMwN2E0ODJmZWM1YWIzNjNjNjZhYjFmMWU0MjY1ZDMyNzU5In19fQ=="),
		JOLTEON("Jolteon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODBkOGM0ODUzMzI2ZjAzNWIwMTA1ZWQ2OTgwMWE5MDljYTBiNzJlMDgxZmFjMDQ3N2MxZmU1NDc3MDI0YTUifX19"),
		PIKACHU("Pikachu","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZhNjk1YjU5NjE4YjM2MTZiNmFhYTkxMGM1YTEwMTQ2MTk1ZWRkNjM2N2QyNWU5Mzk5YTc0Y2VlZjk2NiJ9fX0="),
		COBALION("Cobalion","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTFjNTM2YzhmYmE1OTZhZTk3ZWE1MGQ2ODNmMWViYjg5NWRkZjY2MmFkY2VkYTkxNjkwYmM1OTdkMzg0MyJ9fX0="),
		CATERPIE("Caterpie","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGFhMjUzZmFkZDg5N2E2YTE5YWFkMzk1OWM0NGZiNGNlYWM1YThjYTU4OGYxMGU1MmVjOGNmYmI0MTQ0YzZkIn19fQ=="),
		XERNEAS("Xerneas","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzMxMjNmNTk1OWNlOGQ4MjEwZjY3MmFhNTQ5MWI2YjUwYjk3ZjI3ZTNhODQ2ZDU1ZDM1MmJjMmY0YzllYiJ9fX0="),
		DELPHOX("Delphox","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I1NWM2NGI1NTVjN2Y4NjU0YzU1Yzc3OTNhN2UzOWFiZjVlZTRkOGNiN2FmOThhOGYxOTdkYWFmYjZhMGRhIn19fQ=="),
		CHESNAUGHT("Chesnaught","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGY4NmFlYzIzZjNhODQ3ODJhZGUzZTUzYmFmN2I4YmYyYjNhNTExM2UyNDg3MmNlMmRkYmYzMTFmOThkNjEyZCJ9fX0="),
		KELDEO("Keldeo","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzc0ZWIxZDU5MmQ2MmU5MmMxZTZiNzc3NDI4MTBlMzJmZDQ1MGY3OWJlZjlhOWVmOWQ1NjRmM2NjYjI5OTAifX19"),
		KYUREM("Kyurem","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmI5ZjgyNWRkN2M5ZDU4YWMyMjBiYzk0MjgyNTE3Y2UzOWVhOTA1MGUxN2E4M2U0OTJkM2FhMWZiOThlZGQ4MSJ9fX0="),
		ZEKROM("Zekrom","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGZlN2UxMzQ2YWZmMjUzMjE2ZWUwY2UxNDRmNmMzZDY2NGQwZDFkYzZkOWY2ZGI0NzE4M2NjNjc5Y2UwNDMifX19"),
		RESHIRAM("Reshiram","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZmMzNkZTg4NzZlM2NkZDg5YWU4MTgzNWYzYWZmYzk0NmJjNDk4MzkzYzM2NDRjZmEwNGI2YTZjODlkMmZkIn19fQ=="),
		SCRAGGY("Scraggy","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFiZTM0ZTFlNTQyMTg0NWM0Yzk3Y2I5YTllZjg5ZjJmZGNjYzkyYjFlMmQ0ZDlhYmIxMzIzMzllOTAifX19"),
		SAMUROTT("Samurott","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzc2OGJkMjVhMjM5MWJhOWQyN2ZmNmU2NmVmYzhlMzQ2ZDE3NjRiODViNDdiM2M4MWU3NDQ4MWVjZTIyZmYifX19"),
		EMBOAR("Emboar","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzcxYWE1ZjAxMTQ0MzlkOTE4ZjllYjJlYTc4M2QzYTk2Zjc5MTkyNzY3ZDA1NWZjY2EzMWViNmZiNTExNGFmMiJ9fX0="),
		SERPERIOR("Serperior","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQ1NzljMzE1ZjE2ZGNjOWI0OWQyN2JhMWQ2YTBmMzM3M2RlYTlkZWViZGE0MzYxMGMxYTcxM2VjODg0Y2QifX19"),
		VICTINI("Victini","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQxNDg1NGM4NjRmN2NiMWI0YjUyNTA5YTJmNDJlOTNiMmNhZGFlZGQyODlmYjIxZGRlYWNlNmQ4NzdkNTkifX19"),
		SHAYMIN("Shaymin","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY1OGJmOWY4MTYzN2QzNjRlYWU3MTAzN2FhMGE1YzRjM2E0NmIyMTY5N2E2YmRkMWQxZjY1M2Y1YSJ9fX0="),
		DARKRAI("Darkrai","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDRlZTdlZDNmNmVkZGMxMDE3YWI4Y2I1NTgzZTE3ZmI3Mjc5ZDY1NmE5ZGEwYzI4MzhkYjM2ZDIxN2QzOSJ9fX0="),
		GIRATINA("Giratina","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE2ZTI5NTBlNzhhYzBkMWIxOWFiYWM5ZDY2YmQ0ZGViMGRjNTlhYzQ0NGYxODQxZThhN2RlODMxNmVhYWIyNCJ9fX0="),
		PALKIA("Palkia","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWNhODk0MThlY2VmMTZmNWU0ODliYjI4NzRiZmIyYjBiMzExODRjNDE0NGIzZTc4ZTUzNGVkYmEzNTY4OTIxMSJ9fX0="),
		DIALGA("Dialga","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWFhZWQ4NGVhNGMzZTA2YWJhMzkyYTM1MTU1NWU0ZTk0Mjk3MTY2YmFlZWQ1MTRjOTI3OTE4ZTU2NGU2NTgzNCJ9fX0="),
		EMPOLEON("Empoleon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDI2MjFkNzY2YzRlNjlmODU5MjhiZTRjZWRhMGI5OTZlOTVmNWEyMGZlOTYyMzJiZDAyZWQ0Mjc1MGQifX19"),
		PIPLUP("Piplup","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmEzY2U3Y2FlODM1YjlkNjc3YTY3NTNlMjVjNjg4OTY2YWI2NzAyMTliNmE1Nzg4OGQyZWY3ZDI4MzNkZGI2OCJ9fX0="),
		INFERNAPE("Infernape","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBkYzhhZjljYzY4ZmYxZjJkN2I0YzY4MDc1MWYyMGRkY2MyMGYxNjYzZWNjOTAyYjVkMmI0ZjdiNzRkMWY2In19fQ=="),
		TORTERRA("Torterra","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmRjNTcxYTVlODI4NWRjOGYyZmI2MWM5MThmYTQ3OWUyNzc5Yzg2ZDE2Yzk4MjUxOWRkNzUxY2NlNTAifX19"),
		DEOXYS("Deoxys","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDU5YjI4NGIzOTczN2QzMjQ5MzU3MjhhNzcxZWQxZWRiYmJlMzRhMjk4YWY2ZmMxN2JmMDdjMjczNWY0OCJ9fX0="),
		RAYQUAZA("Rayquaza","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2EzZWI1OTc3ZDdkMmRmN2NmMDZiZTE3ZTFmNmQwZWVkNWJiYzViYTM0MzM4Y2YyYmJiODk4NGE1ZDVhYiJ9fX0="),
		GROUDON("Groudon","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg0NjVlODZmZmRhYjhlYmY3YjhjZDNhZmY1ZDQ0ZjNhM2M5YmUxODhlZTcxNjZlYjU0MGRjNjhmMTliYjgifX19"),
		KYOGRE("Kyogre","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFlOTdlNjI3Y2FmMzEzY2Q5YmY4ZGRlZDQxNzUzZTIyYTdmNDM4MWQxM2UzZTYyMmExNmMwYTA0Nzg1NjM2YyJ9fX0="),
		LATIOS("Latios","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE3ZjQ1OTQ3YzliMjc1M2U1OTM0NTZiODdjOGNmZGFkYjA4YzdiOWE2N2M3NTM1ZDlkMzc5NGNhNmUzNmEifX19"),
		LATIAS("Latias","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2E3MzhjZjU0ZWNiYThhYmZlOGZkYmIyNTQwNzc5ODg5MTIyZWExYTcxZjZjNzBkNDJlZDRlMThlZWQ0YmEifX19"),
		SALAMENCE("Salamence","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWU2NDhhNmU2OTg4N2QzMjgxODgyNzBmNjY1NTI1YmEzOTllMzQ0ODc1NzE1ODliOGYzZjU2OTY0MThlZmMifX19"),
		BLAZIKEN("Blaziken","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVkYzVhYzljOTQ0N2UzYTdhOTI2ZmJjNTRkY2Y2NmQ1ZTM3M2I0OTIxMDgzYTFmZmYwNzQyMzk1YzkyYyJ9fX0="),
		SCEPTILE("Sceptile","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY4YjZhY2ZjOGM3MThiNzc1NzY5NDc2YjM4ZjJjM2MwNzJkZDMwZWQyYTM1YTI4MGMwZDNkOGYzYzRlMTgifX19"),
		HOOH("Ho-Oh","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWViZmJmZTdhNGZjY2JmNTY2YzljZjQ5ZGU1NmU3ODRiZjY0MjFjODZhMzUyNGFhZjU0YjY1Njg5NDJkIn19fQ=="),
		LUGIA("Lugia","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjY5ZjRhY2JkY2YzMjU5M2EwYTljOTdlZmJkZGMwMWZiYTFhMzFhNDFiZWI5ZGIxMzU1NTEzOTM4NmZiMzM3In19fQ=="),
		SUICUNE("Suicune","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjIyYmM5NWFmMDU1N2E1OTQwNDYyMDI1ZjI1M2U5NDk0Y2ZjYzU2YzVmZjQwNWUxODgwNWQxMzNhODdlZmQyIn19fQ=="),
		ENTEI("Entei","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE0YWVlM2Y1MmU4MjcxODViOWI5ODJmNWZhNjU0ZmNiZGRhMzY1NzI2MWNlN2I1MzE0YzFiMjU3NmE4YTg1In19fQ=="),
		RAIKOU("Raikou","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU5YzgxMWMzNGQzY2VlNGQ1MTM4MzE3Zjc1M2NlMmU4ZGQxYjdiYWRlODhiY2RiYmI1ZDc0ZjVhMjFhODI4ZCJ9fX0="),
		TYPHLOSION("Typhlosion","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2ZlMTRhY2NlOTA2OWY2NWVkYjM0ZTNhZDMyZjRkMzM4MTE2ZjcxZWE3YTg0MWU2YzU2NDM2MjhjMzlmMWI3In19fQ=="),
		MEGANIUM("Meganium","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFiNjhmNjNjNTVhZDNhZWIxNjE2N2EyZjk4ODk0YzE1ZWI4ZWFmMmMzNWE5M2JlYzRhNzczZDY0Y2E5YmFmIn19fQ=="),
		MEWTWO("Mewtwo","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQyYzRiNDgxZjMyN2Y0NDAyMmJhYjM5M2E0MTg4NzRiM2Y0NWFjZmMzYmRmMDYwOWE4ODk0NDRiMzQ2In19fQ=="),
		DRAGONITE("Dragonite","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE1MTY0ZGNlZGY4OGViMjY2YzY3NWJmZDc1YzU2N2MzN2IzNmIyN2UwNjQ2OWYzYTQ0Y2UyNjk3ZWQxNTQifX19"),
		BLASTOISE("Blastoise","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFkZjJiYjkxZjQzOTBhYWNmYTJjM2FhYmZlM2RlMDI2OWY0ZWI4YjhmMmRmZGJhMmVmYThjYWZjOTNkZGQifX19"),
		VENUSAUR("Venusaur","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjczMzFjNTMxNDVjNmIxNzY2YzVlNGFkN2Q5ODI0YjI4ZmE4ZmVlMjc3NTMzYzhmNDUxZjljNTA3MDIyOGE0MiJ9fX0="),
		CHARIZARD("Charizard","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODkzN2ZiYTBiMWU5ODg1ZmI0YTg0YzkxNTA1MTNkZWU4YjIxN2NkMDRmMTQwZDI1MDVjYWI4YWUzOWI1ZDQifX19"),
		LUCARIO("Lucario","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjlkODM2NjU5MmQ5ZTJiYTg0Y2Y1MjEwMmY3MjM5N2Y3Y2NkMjg2YmFjNjIxMzNjMGE3MTA5MWZlYmUifX19"),
		FERALIGATR("Feraligatr","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWY5MTllN2E1NWY5NWMyN2NmOTk1YjdhNWEzY2RlYzIyYWI5OTdmOGRmZmQ0MTQxZWEwOGRmNjZjNjBjZDVkIn19fQ=="),
		PIDGEOT("Pidgeot","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjZjOTZhZWY2NTU4ZjI5YjI0N2JjOGUzOGQ5MzIwNjE0M2YxMzE0NDc1YzVmY2QxMWUyZWZjYzVkYjU1ZTg1In19fQ=="),
		PIDGEY("Pidgey","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDE2ZjU5NWU4ZjY3OTFiYzE1NDY1OWE4OTc2ZjZhOGZmZDk4NDdjZjc1YTJiZjYzOTkyZTNhNjU1ZTAifX19"),
		METAPOD("Metapod","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFlZWUyYWNlOGI0YTg5NTcyYmQxYTU3ZDQ3ZmMxOTI3Yjg5YWJkNjBjYzc5Y2I4Yzc3ZmFhNzQ1ODE0NGUifX19"),
		BUTTERFREE("Butterfree","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWUyYTUzYzI3ZjcyZmY4NDc5NTI0NWJhZDIzMjk4ZDhhNTlhMDYxM2RlZmJlZDYyNjM1M2ZjNjZhOTViIn19fQ=="),
		WARTORTLE("Wartortle","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZkMTZlYmZkYmM1MWY5Mzk4ZTMzODM1Y2VlMGM2NjRhMDgxNDJlZTc5ZjhmZmM1N2Q2YjdlYjUxOGVmIn19fQ=="),
		BULBASAUR("Bulbasaur","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTNlODZhOGE0MjMxZDFjZWU4MzcxNGViNWU5MzljNmQzMDc4ZWE2ODMyYmY5M2ViYTY2ZDEyZGMyNWVhOTVhIn19fQ=="),
		IVYSAUR("Ivysaur","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzk5ZWM5NDNiNDhjNmY4MmYzMmFjZDllODYyNjU0NmRlODQxNmNjZTRkYTQxY2JhYTAyYzY5ZmVlZmJlYSJ9fX0="),
		SWAMPERT("Swampert","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjdmYmQzNjY3ZDMyNThjM2MyYTI5MTQ5N2Y0MjdhYjJiM2NlYWE1ZGYwZWZmNjJlZGMzMjE5ZGNkNzE1NzAifX19"),
		GROWLITHE("Growlithe","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE1MjEzZDM4NTI2OGFkM2JkMTc5ZTYxM2YxZmFjOTlmYTgzOTI4MzFmYzlmNmYxMGRiNTk5Y2Y1OWNlZmZiIn19fQ=="),
		GRENINJA("Greninja","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDkyZmQyNjRjZmMwMmY1OGNjYTdhZGYwZmE2OThhYWY4ZWYyMzM5YjJlZTQ5N2MzYmNmZjc0ZWI5YWViYTkxMiJ9fX0="),
		CHARMANDER("Charmander","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTM4OTkyZmE3MWQ1ZDk4Nzg5ZDUwNjFkZGQ2OGUyYjdhZjllZmMyNTNiMzllMWIzNDYzNDNkNzc4OWY4ZGMifX19"),
		CHESPIN("Chespin","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzU2YWRmODVjZDhiODg2ZWM3NWI3MmQ3NjEyZTViNmQyZmQ3ZDUyZTY1NzMxNmNiNjZmNmQ5ZDY4MjY5MzVhMiJ9fX0="),
		TERRAKION("Terrakion","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRmYjVlNmY4YzgyNjc2NzliNzgyODBkNWExMGNkNDEyMmU1MGE5N2JlMjlmYTBmNGYxYzYxZmZkM2ZkYSJ9fX0="),
		VIRIZION("Virizion","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRiZjNhOGVlOTkxOGU5YzFjMDc5YWQ2OTYzZTg0YWU4MjQyN2NmNGVhMjBmZGM2MmFhMWQ2NDBjZWJhIn19fQ=="),
		NOIVERN("Noivern","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjNlODdjYmJhMjcwNTlmNWU4YzM0ZjU5OWMyNWFhYjk0MjIwNjNlYWJhODAyYzMyNzc2YjNkODBhYWQ3NGY2OSJ9fX0="),
		JIGGLYPUFF("Jigglypuff","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE2ZjEyNjIxZTUzNjM1OTViYzZkNjhmYTE4NWNlZGZjZWFhZGEzZDgyYjYwYzEzZmRjNGEwMzI2OSJ9fX0="),
		GROVYLE("Grovyle","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzU1Y2FlNDkxM2U5N2Y0OTgzOGQ0YThkZGY3MTFmOTU5OGQ1NjJiY2I1OGUzOWYzZDQxYzYwZDNiZTcyMSJ9fX0="),
		GABITE("Gabite","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ4MzJjZTJlNjVhYzE5NjQ4MmFmZTQ2ZGZmY2ZkODUyOWJjNDc3OWNjYjdlOWE1MmRmYTVjYmRhMTQ0ZDVjIn19fQ=="),
		ARTICUNO("Articuno","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ0Yjk4NjdkZWRlOTNlOGYyMjZmZjkxYjc3ZDdhM2NjYWYzZjZiMWVmNWY0ODZjZTYyZDExZTk0MyJ9fX0="),
		LUXRAY("Luxray","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MDUxZmMyOGZjZmRiZWZiNTQzYWQ3OGEyYjI1NGIyNTRkZDZmMTcxYzczNDZiNDZhNDZkZDM5MjNmIn19fQ=="),
		ARCANINE("Arcanine","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQzMGJkYTE5YzQ3YmM3OTFiZTExZjVjNzRiY2JkODNlZmZjNjA2ZDI5MWJiNGQzNjk4OGI3NjZmNmM2In19fQ=="),
		MIGHTYENA("Mightyena","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDc1NWRlODVjNmIzNzYyMDZmODAxMWY5Y2RmNTk0MTRhZGUyMDFmZTQzNDliZTBlYTE1YTc4OTdlNzAxNGZhIn19fQ=="),
		EEVEE("Eevee","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTA0NGU5ZDE5YmVmNDc5MzNhZmY0MmJjZTRiNDU4ZjQzMTMxNTA5MGQ2MTNmNTRiNmU3OTVkYTU5ZGI5ZDBkZSJ9fX0="),
		VULPIX("Vulpix","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTJiNzY0YTczMTdlOTAxYzdiZDhjMjQ4Y2QxMzg3ZTZhZjZiYzgzMzViODlkOTIzZjYxOGY4ZmViYmZiOTUifX19"),
		GENGAR("Gengar","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQyOGJjYjJhZDYyNTY3ZTFiZDBkNGRhYzZmNDczZmU5ZGUxNzVkYjExNzQyMjE0NGM0NjU3NWZmNWUxIn19fQ=="),
		RAICHU("Raichu","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJmNTIzZjJiZDkwYjNmZjE5NDQ1MTViNmEzMjQzMzhhYWQ0N2VhMWYyY2U5M2Y4MmQ1NTY0YzRjOWFkZTcxIn19fQ=="),
		BEEDRILL("Beedrill","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWM0OGNhMWNlNDQ3YzFkYWEzOTliNGRlNjNiYjE5MDY2N2Y4ODdjYWY2ZTNlOGVkNTM3ZjA5MGE1ZmI2NGI4In19fQ=="),
		CUBCHOO("Cubchoo","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTRmNDUxNTIzZGQ2NmM0ZTg5MmFlNTlhYTc5ZTlkZGNjNTI5MDQ1NDdmNWRmNWY2ODMxMDhkZGQ0MjJmZWMifX19"),
		BIDOOF("Bidoof","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdhNWU1MjE4M2U0MWIyOGRlNDFkOTAzODg4M2QzOTlkYzU4N2Q0ZWIyMzBlNjk2ZDhmNmJlNmQzZTU3Y2YifX19"),
		BUIZEL("Buizel","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjQ0MDk3MmYxZGNiMjQ0ODcyZDJmMTAyNmRhY2ViOTRkYWRiOTg1MWNhNTkzMmUxNTE1NGZmZTdlM2JlOCJ9fX0="),
		ARCEUS("Arceus","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2M3ZWFkZTcyNmIzOTFmN2YzYWI1ZDhiNWNmYzczNzY1NThiYWE4ODVkZTIyOWQ2ZGNiZDZiNjRlYzg5YWE3MCJ9fX0="),
		FLAAFFY("Flaaffy","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmEyMTM3ZjM4NDRiMDMxNDMyZTMyMzYzMTdkNTU1M2ZiMjQ3ZWZlNzJlZTY4NmI4NTljZGNjNGYxOWUyYzMifX19"),
		DEINO("Deino","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI5YTY3Yzc5MDVkMWFlN2M4NjUzZjZhNmU5ZjU0OTE5Zjg5MjZkMzY3MTQyM2E1ZmFmYWU2Yzk1YjkyOTgifX19"),
		MUK("Muk","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWFlNTY4ZWU1OTc4MzQ5YWRjNjNhNWJmMzdmMDgyZWY1NTEyYmIyNjRjZGI3NTk4ZWZlY2Q3MWY0MmQxMyJ9fX0="),
		OSHAWOTT("Oshawott","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjJiMWIzNmIyOTg1OTdjZGEyNmY3MjY1MmNhZjg0ZTBlN2RkZmFiNTRkZmY2ZjUyNTkzNzE3NDNlMjU4NSJ9fX0="),
		CRESSELIA("Cresselia","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMThjOGRhNWEyYTc3M2NlNGY1ZjUyOTY3NGMyZGY1MDVlNmZiOGU4NWQ3MTM5OWIxZjU2NjQwYmViMmZkZTcifX19"),
		YVELTAL("Yveltal","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTVhZTY0Yzg3ZGU0NTFmZjExMjgyNTE0OTM1MzdlYWUzZWQ1MzYyOTgwYWFjZDU5MWNiNWUxMmI1Y2Y3YTI1NyJ9fX0="),
		WAILORD("Wailord","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDIxMTQyMTcyNDI0YjIxMGIxN2E5Y2EyZjQ0OWE0NDQ5NTE4NGFkZjgzYzk2NGQzODVmYTc1OGExMjAifX19"),
		MOLTRES("Moltres","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmNmMDIyYTg5ZWYyMWZhZGEyNmQ5ZjY0OGUxNWNkZjQzZjJlZGI3NDk3MWY0NDIzY2ViNWFjNGEzNDNhNWYifX19"),
		MEW("Mew","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzVjZDNjNzJkY2M3ZWVkY2ZmY2IyMjIxYjM4YjViOGFjNDcwNWZmZGM0NTc0NjFhODE2NTM4ODc0YjRjZiJ9fX0="),
		ZYGARDE("Zygarde","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA1NGEwMTlmNDVkN2FhNjE5ZGQ1YmUxZDRlNjhjNzljMGRmYWNiMjYwNjgxNDM5YzdiNDEzODY5YzhkYzcifX19"),
		ZAPDOS("Zapdos","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDlhNjZmM2QyNThkOTI3ZjdlNDgxODE0OGJhYzY3YjIzZTc5MjRhOTNiODlmM2M5NmI4NzU0YmZjYjQ4Zjc1In19fQ=="),
		MANAPHY("Manaphy","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzcxZjJmMWQ1ZTRmZWFlNmY4OTM4MTZhOGNjNzg5MTU1MzY2NzQ3MjY0ZjlhMzZlZmM3MTNiYjhmOWMzZDYifX19"),
		KOFFING("Koffing","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjE3NmRlYzQ5YTkzMTA5NmEwOWIyMmFkZDA0MDJhYjJjN2Y0ODk4NzcxMTA5MWQwMThlMDJiNGJiMWU1NyJ9fX0="),
		OMANYTE("Omanyte","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWM3MzhmY2I2OWM0OGVmNjBkNjU0ZGE0YzJjNDkzYzc1YjdjMjlmYmY4ZDgzNmJkZjVmOThiY2FiOGJhIn19fQ=="),
		CUBONE("Cubone","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E0ZmE3MWFkMjhjZDFlMWI3ZWE5MzU4MTczMDgyNWNiYTk2Y2FjM2NkM2IxYmM3MmE5N2VhNTRkZTUzNCJ9fX0="),
		VOLTORB("Voltorb","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJmM2Y5Y2NhNzdjNzI1MjE3ZTQ1YWQ0ZWVlZWZmYTA1NjVmODJiODY2YWM2Nzk5OWI0M2MzYTk3MzExNjI4YyJ9fX0="),
		ELECTRODE("Electrode","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWVlZmUxMTkxNTc5OTU3YzgzMjUwYThjZThmZWZkNTVmNGQ3NmM1MGQ4MTA5NGM5MjA5ODk1ZjRiZDYwMCJ9fX0="),
		WEEDLE("Weedle","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjI5NjU5ZDExZTJkNGYzMGMzZTU5NDdhMWZjOTMyMWE4ZDljMTA1ZWQ3MmU5MjdhNTBjYjNlOGQ3MjkxNTMzIn19fQ=="),
		KAKUNA("Kakuna","eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWE5YTgwMWYxMTljNjMxYTljOWZhMDQ3YTJjMjViYzBiNmNiZjkwODIzN2Q3NGNiMWE0MTA4NTEwN2M1OTcifX19");

		private ItemStack itemStack;

		private LobbyPokemonType(String name,String url){
			this.itemStack = ItemUtil.getHead(name,url);
		}

		public String toPermission(){
			return "cosmetics.pokemons."+this.name().toLowerCase();
		}

		public ItemStack getItemStack(){
			return itemStack;
		}
	}
}