package com.realcraft.lobby;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;
import com.realcraft.RealCraft;
import com.realcraft.auth.AuthLoginEvent;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.utils.ItemUtil;
import com.realcraft.utils.LocationUtil;
import com.realcraft.utils.Particles;
import com.realcraft.utils.Particles.OrdinaryColor;
import com.realcraft.utils.RandomUtil;
import com.realcraft.utils.Title;

import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.PathEntity;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;

public class LobbyPokemons implements Listener {

	RealCraft plugin;
	private static final String POKEMONS = "pokemons";
	private static final String POKEMONS_USERS = "pokemons_users";
	private static final String invName = "Pokemon";
	private static final String invBuyName = "Pokemon > Koupit";
	private static final int PRICE = 500;
	private static ItemStack item = null;
	private ArrayList<LobbyPokemonType> pokemonTypes = new ArrayList<LobbyPokemonType>();
	private HashMap<Player,LobbyPokemon> pokemons = new HashMap<Player,LobbyPokemon>();
	private HashMap<Player,Integer> playerPage = new HashMap<Player,Integer>();
	private HashMap<Player,LobbyPokemonType> playerBuying = new HashMap<Player,LobbyPokemonType>();
	private HashMap<Player,HashMap<Integer,Boolean>> playerPokemons = new HashMap<Player,HashMap<Integer,Boolean>>();

	public LobbyPokemons(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		this.loadPokemons();
	}

	public void onDisable(){
		for(LobbyPokemon pokemon : pokemons.values()){
			pokemon.remove();
		}
	}

	private void loadPokemons(){
		ResultSet rs = RealCraft.getInstance().db.query("SELECT pokemon_id,pokemon_name,pokemon_url FROM "+POKEMONS);
		try {
			while(rs.next()){
				pokemonTypes.add(new LobbyPokemonType(rs.getInt("pokemon_id"),rs.getString("pokemon_name"),rs.getString("pokemon_url")));
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	private void loadPlayerPokemons(Player player){
		ResultSet rs = RealCraft.getInstance().db.query("SELECT pokemon_id FROM "+POKEMONS_USERS+" WHERE user_id = '"+PlayerManazer.getPlayerInfo(player).getId()+"'");
		try {
			HashMap<Integer,Boolean> types = new HashMap<Integer,Boolean>();
			while(rs.next()){
				rs.getInt("pokemon_id");
				types.put(rs.getInt("pokemon_id"),true);
			}
			playerPokemons.put(player,types);
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	private void buyPokemon(Player player,LobbyPokemonType type){
		RealCraft.getInstance().db.insert("INSERT INTO "+POKEMONS_USERS+" (user_id,pokemon_id) VALUES('"+PlayerManazer.getPlayerInfo(player).getId()+"','"+type.getId()+"')");
		playerPokemons.get(player).put(type.getId(),true);
		player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
	}

	private boolean hasPlayerPokemon(Player player,LobbyPokemonType type){
		return (playerPokemons.containsKey(player) && playerPokemons.get(player).containsKey(type.getId()));
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
		this.loadPlayerPokemons(player);
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
		ItemStack item = player.getInventory().getItemInMainHand();
		if(player.getWorld().getName().equalsIgnoreCase("world") && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(this.getItem().getItemMeta().getDisplayName()) && (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
			event.setCancelled(true);
			if(PlayerManazer.getPlayerInfo(player).isLogged()){
				this.openMenu(player,1);
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
				ItemStack item = event.getCurrentItem();
				if(event.getRawSlot() >= 0 && event.getRawSlot() < 6*9){
					if(item.getType() != Material.AIR && item.hasItemMeta()){
						if(item.getType() == Material.PAPER){
							if(event.getRawSlot() == 45) this.openMenu(player,playerPage.get(player)-1);
							else if(event.getRawSlot() == 53) this.openMenu(player,playerPage.get(player)+1);
							player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
						} else {
							for(LobbyPokemonType type : pokemonTypes){
								if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§e"+type.getName())){
									if(this.hasPlayerPokemon(player,type)){
										this.createPokemon(player,type);
										player.closeInventory();
									} else {
										this.openBuyMenu(player,type);
										player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
									}
									break;
								}
							}
						}
					}
				}
			}
		}
		else if(event.getInventory().getName().equalsIgnoreCase(invBuyName)){
			if(event.getWhoClicked() instanceof Player && ((Player)event.getWhoClicked()).getWorld().getName().equalsIgnoreCase("world")){
				event.setCancelled(true);
				Player player = (Player) event.getWhoClicked();
				ItemStack item = event.getCurrentItem();
				if(event.getRawSlot() >= 0 && event.getRawSlot() < 6*9){
					if(item.getType() == Material.EMERALD_BLOCK){
						this.buyPokemon(player,playerBuying.get(player));
						this.openMenu(player,playerPage.get(player));
					}
					else if(item.getType() == Material.REDSTONE_BLOCK){
						this.openMenu(player,playerPage.get(player));
						player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
					}
				}
			}

		}
		else if(event.getWhoClicked() instanceof Player && ((Player)event.getWhoClicked()).getWorld().getName().equalsIgnoreCase("world") && event.getSlotType() == SlotType.QUICKBAR && event.getCurrentItem().getType() == Material.ENCHANTMENT_TABLE){
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			if(PlayerManazer.getPlayerInfo(player).isLogged()){
				this.openMenu(player,1);
			}
		}
	}

	private void openMenu(Player player,int page){
		this.removePokemon(player);
		playerPage.put(player,page);
		Inventory inventory = Bukkit.createInventory(null,6*9,invName);
		ItemStack item;
		ItemMeta meta;

		for(int i=0;i<5*9;i++){
			int index = i+((page-1)*(5*9));
			if(pokemonTypes.size() > index){
				LobbyPokemonType type = pokemonTypes.get(index);
				if(type != null){
					inventory.setItem(i,type.getListItemStack(player));
				}
			}
		}

		int maxPage = (int)Math.ceil(pokemonTypes.size()/(5*9.0));
		if(page > 1){
			item = new ItemStack(Material.PAPER);
			meta = item.getItemMeta();
			meta.setDisplayName("§6§lPredchozi");
			item.setItemMeta(meta);
			inventory.setItem(45,item);
		}
		if(page < maxPage){
			item = new ItemStack(Material.PAPER);
			meta = item.getItemMeta();
			meta.setDisplayName("§6§lDalsi");
			item.setItemMeta(meta);
			inventory.setItem(53,item);
		}

		player.openInventory(inventory);
	}

	private void openBuyMenu(Player player,LobbyPokemonType type){
		playerBuying.put(player,type);
		Inventory inventory = Bukkit.createInventory(null,6*9,invBuyName);
		ItemStack item;
		ItemMeta meta;
		ArrayList<String> lore;

		inventory.setItem(13,type.getBuyItemStack());

		item = new ItemStack(Material.EMERALD_BLOCK);
		meta = item.getItemMeta();
		meta.setDisplayName("§a§lKoupit");
		lore = new ArrayList<String>();
		lore.add("§7Cena: §a"+PRICE+" coins");
		lore.add("§7Klikni pro zakoupeni");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inventory.setItem(19,item);
		inventory.setItem(20,item);
		inventory.setItem(21,item);
		inventory.setItem(28,item);
		inventory.setItem(29,item);
		inventory.setItem(30,item);
		inventory.setItem(37,item);
		inventory.setItem(38,item);
		inventory.setItem(39,item);

		item = new ItemStack(Material.REDSTONE_BLOCK);
		meta = item.getItemMeta();
		meta.setDisplayName("§c§lZrusit");
		lore = new ArrayList<String>();
		lore.add("§7Klikni pro zruseni");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inventory.setItem(23,item);
		inventory.setItem(24,item);
		inventory.setItem(25,item);
		inventory.setItem(32,item);
		inventory.setItem(33,item);
		inventory.setItem(34,item);
		inventory.setItem(41,item);
		inventory.setItem(42,item);
		inventory.setItem(43,item);

		player.openInventory(inventory);
	}

	private void createPokemon(Player player,LobbyPokemonType type){
		if(pokemons.containsKey(player)) this.removePokemon(player);
		else pokemons.put(player,new LobbyPokemon(player,type));
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
		private LobbyPokemonMode mode = LobbyPokemonMode.FRIENDLY;
		private int tick = 0;
		private boolean warnSound = false;
		private long followTimeout = 0;
		private long sitTimeout = 0;
		private long leftClickTimeout = 0;
		private long rightClickTimeout = 0;

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

		public Entity getEntity(){
			return entity;
		}

		public boolean canFight(){
			return (state != LobbyPokemonState.SITTING && mode == LobbyPokemonMode.HOSTILE && followTimeout < System.currentTimeMillis());
		}

		public boolean canFriend(){
			return (state != LobbyPokemonState.SITTING && mode == LobbyPokemonMode.FRIENDLY && followTimeout < System.currentTimeMillis());
		}

		private void create(){
			Location location = player.getLocation();
			location.setPitch(0f);
			entity = player.getWorld().spawnEntity(location,EntityType.ZOMBIE);
			entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_VEX_CHARGE,1f,1f);
			if(entity != null){
				((Zombie)entity).setSilent(true);
				((Zombie)entity).setBaby(true);
				((Zombie)entity).getEquipment().clear();
				((Zombie)entity).getEquipment().setHelmet(type.getItemStack());
				((Zombie)entity).getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
				((Zombie)entity).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,Integer.MAX_VALUE,1));
				((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(0);
				this.clearPathfinders(entity);
			}
			else LobbyPokemons.this.removePokemon(player);
		}

		private void run(){
			if(entity == null || entity.isDead()){
				LobbyPokemons.this.removePokemon(player);
				return;
			}
			tick ++;
			if(tick == 3) tick = 0;
			if(state == LobbyPokemonState.ATTACK){
				LobbyPokemon target = this.getNearestTarget(LobbyPokemonMode.HOSTILE);
				if(target != null && this.canFight() && target.getEntity().getLocation().distanceSquared(entity.getLocation()) <= 10*10){
					double distance = entity.getLocation().distanceSquared(target.getEntity().getLocation());
					if(this.getDistanceToOwner() > 10*10){
						followTimeout = System.currentTimeMillis()+3000;
						this.followOwner();
						return;
					}
					if(distance > 3*3 && warnSound == false && RandomUtil.getRandomBoolean()){
						warnSound = true;
						entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_GHAST_WARN,0.3f,1f);
					}
					if(tick%2 == 0 && distance <= 3*3){
						switch(RandomUtil.getRandomInteger(0,1)){
							case 0: Particles.LAVA.display(0.2f,0f,0.2f,0f,2,entity.getLocation().add(0f,1f,0f),64);
							break;
							case 1: Particles.CLOUD.display(0.2f,0f,0.2f,0f,2,entity.getLocation().add(0f,1f,0f),64);
							break;
						}
					}
					Location targetLocation = target.getEntity().getLocation();
					try {
						double speed = (distance > 3*3 ? 1.0 : 0.5);
						((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
						PathEntity path;
						path = ((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(targetLocation.getX(),targetLocation.getY(),targetLocation.getZ());
						if(path != null){
							((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(path,speed);
							((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
						}
					} catch (Exception exception){
					}
					if(tick == 0 && distance <= 3*3){
						if(entity.isOnGround()) entity.setVelocity(entity.getVelocity().add(new Vector(RandomUtil.getRandomDouble(-0.2,0.2),RandomUtil.getRandomDouble(0.3,0.6),RandomUtil.getRandomDouble(-0.2,0.2))));
						switch(RandomUtil.getRandomInteger(0,2)){
							case 0: entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_GHAST_HURT,0.3f,1f);
						}
						if(RandomUtil.getRandomBoolean()) entity.getWorld().spawnParticle(Particle.SWEEP_ATTACK,entity.getLocation().add(0,1.0,0),1);
					}
					else if(distance <= 3*3 && RandomUtil.getRandomBoolean()){
						switch(RandomUtil.getRandomInteger(0,2)){
							case 0: entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_PLAYER_ATTACK_SWEEP,0.5f,1f);
							break;
							case 1: entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK,0.5f,1f);
							break;
							case 2: entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_PLAYER_ATTACK_STRONG,0.5f,1f);
							break;
						}
					}
				} else {
					warnSound = false;
					state = LobbyPokemonState.FOLLOW;
				}
			}
			else if(state == LobbyPokemonState.FRIEND){
				LobbyPokemon target = this.getNearestTarget(LobbyPokemonMode.FRIENDLY);
				if(target != null && this.canFriend() && target.getEntity().getLocation().distanceSquared(entity.getLocation()) <= 5*5){
					double distance = entity.getLocation().distanceSquared(target.getEntity().getLocation());
					if(this.getDistanceToOwner() > 5*5){
						followTimeout = System.currentTimeMillis()+2000;
						this.followOwner();
						return;
					}
					if(tick == 0){
						Location targetLocation = target.getEntity().getLocation();
						if(distance <= 2*2) targetLocation.add(RandomUtil.getRandomDouble(-2.0,2.0),0,RandomUtil.getRandomDouble(-2.0,2.0));
						try {
							double speed = (distance > 2*2 ? 1.0 : 0.5);
							((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
							PathEntity path;
							path = ((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(targetLocation.getX(),targetLocation.getY(),targetLocation.getZ());
							if(path != null){
								((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(path,speed);
								((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
							}
						} catch (Exception exception){
						}
					}
					if(tick == 0 && distance <= 2*2){
						if(RandomUtil.getRandomBoolean()) Particles.HEART.display(0f,0f,0f,0,1,entity.getLocation().add(0f,1f,0f),64);
					}
				} else {
					state = LobbyPokemonState.FOLLOW;
				}
			}
			else if(state == LobbyPokemonState.FOLLOW){
				LobbyPokemon target = this.getNearestTarget(mode);
				if(target != null && this.canFight() && target.getEntity().getLocation().distanceSquared(entity.getLocation()) <= 10*10){
					state = LobbyPokemonState.ATTACK;
				}
				else if(target != null && this.canFriend() && target.getEntity().getLocation().distanceSquared(entity.getLocation()) <= 5*5){
					state = LobbyPokemonState.FRIEND;
				}
				else this.followOwner();
			}
			else if(state == LobbyPokemonState.SITTING){
				if(this.getDistanceToOwner() < 3*3){
					Location lookLocation = entity.getLocation().clone().setDirection(player.getLocation().subtract(entity.getLocation()).toVector());
					if(lookLocation.getPitch() < -45) lookLocation.setPitch(-45);
					else if(lookLocation.getPitch() > 45) lookLocation.setPitch(45);
					entity.teleport(lookLocation);
				} else {
					if(entity.getLocation().getPitch() > 1 || entity.getLocation().getPitch() < -1){
						Location lookLocation = entity.getLocation().clone();
						lookLocation.setPitch(0);
						entity.teleport(lookLocation);
					}
				}
			}
		}

		private double getDistanceToOwner(){
			return player.getLocation().distanceSquared(entity.getLocation());
		}

		private LobbyPokemon getNearestTarget(LobbyPokemonMode mode){
			LobbyPokemon target = null;
			double distance = Integer.MAX_VALUE;
			double tmpDist = 0;
			for(LobbyPokemon pokemon : pokemons.values()){
				if((mode == LobbyPokemonMode.HOSTILE && pokemon.canFight()) || (mode == LobbyPokemonMode.FRIENDLY && pokemon.canFriend())){
					tmpDist = pokemon.getEntity().getLocation().distanceSquared(entity.getLocation());
					if(pokemon != this && tmpDist < distance){
						target = pokemon;
						distance = tmpDist;
					}
				}
			}
			return target;
		}

		private void followOwner(){
			Location targetLocation = player.getLocation();
			state = LobbyPokemonState.FOLLOW;
			try {
				double speed = 1D;
				double distance = this.getDistanceToOwner();
				if(distance > 2*2){
					((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
					PathEntity path;
					path = ((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(targetLocation.getX(),targetLocation.getY(),targetLocation.getZ());
					if(distance > 30*30 && player.isOnGround()){
						((CraftEntity)entity).getHandle().setLocation(targetLocation.getBlockX(),targetLocation.getBlockY(),targetLocation.getBlockZ(),0,0);
					}
					if(path != null){
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

		private void clearPathfinders(org.bukkit.entity.Entity entity){
			net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
			try {
				Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
				bField.setAccessible(true);
				Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
				cField.setAccessible(true);
				bField.set(((EntityInsentient) nmsEntity).goalSelector,Sets.newLinkedHashSet());
				bField.set(((EntityInsentient) nmsEntity).targetSelector,Sets.newLinkedHashSet());
				cField.set(((EntityInsentient) nmsEntity).goalSelector,Sets.newLinkedHashSet());
				cField.set(((EntityInsentient) nmsEntity).targetSelector,Sets.newLinkedHashSet());
			} catch (Exception e){
				e.printStackTrace();
			}
		}

		private void effect(){
			if(entity == null || entity.isDead()){
				LobbyPokemons.this.removePokemon(player);
				return;
			}
			if(state != LobbyPokemonState.SITTING){
				Particles.SNOW_SHOVEL.display(0.1f,0f,0.1f,0f,4,entity.getLocation().add(0,0.7,0),64);
				if(mode == LobbyPokemonMode.HOSTILE) Particles.SPELL_MOB.display(new OrdinaryColor(170,0,0),entity.getLocation().add(0,0.7,0),64);
			}
		}

		@EventHandler
		private void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
			if(event.getHand().equals(EquipmentSlot.HAND) && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR && event.getRightClicked().equals(entity)){
				event.setCancelled(true);
				if(rightClickTimeout < System.currentTimeMillis()) this.rightClick(event.getPlayer());
			}
		}

		@EventHandler
		public void PlayerInteractEvent(PlayerInteractEvent event){
			if(event.getHand().equals(EquipmentSlot.HAND) && event.getItem() == null && LocationUtil.isPlayerLookingAt(event.getPlayer(),entity.getLocation().clone().add(0,1.0,0))){
				event.setCancelled(true);
				if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
					if(rightClickTimeout < System.currentTimeMillis()) this.rightClick(event.getPlayer());
				}
				else if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
					if(leftClickTimeout < System.currentTimeMillis()) this.leftClick(event.getPlayer());
				}
			}
		}

		@EventHandler
		public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
			if(event.getEntity().equals(entity)){
				event.setCancelled(true);
				if(leftClickTimeout < System.currentTimeMillis()) this.leftClick(event.getDamager());
			}
		}

		@EventHandler
		public void EntityDamageEvent(EntityDamageEvent event){
			if(event.getEntity().equals(entity)){
				event.setCancelled(true);
			}
		}

		private void rightClick(Entity damager){
			if(damager instanceof Player && damager.equals(player) && this.getDistanceToOwner() < 3*3 && sitTimeout < System.currentTimeMillis()){
				rightClickTimeout = System.currentTimeMillis()+100;
				if(state == LobbyPokemonState.FOLLOW){
					state = LobbyPokemonState.SITTING;
					sitTimeout = System.currentTimeMillis()+500;
					((Zombie)entity).setAI(false);
					entity.setGravity(false);
					entity.setVelocity(new Vector(0,0,0));
					entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_BAT_HURT,0.2f,1f);
					Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
						public void run(){
							((CraftEntity)entity).getHandle().setLocation(entity.getLocation().getX(),entity.getLocation().getY()-0.7,entity.getLocation().getZ(),0,0);
						}
					});
				}
				else if(state == LobbyPokemonState.SITTING){
					state = LobbyPokemonState.FOLLOW;
					sitTimeout = System.currentTimeMillis()+500;
					((Zombie)entity).setAI(true);
					entity.setGravity(true);
					entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_BAT_HURT,0.2f,1f);
					Bukkit.getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
						public void run(){
							((CraftEntity)entity).getHandle().setLocation(entity.getLocation().getX(),entity.getLocation().getY()+1.0,entity.getLocation().getZ(),0,0);
							entity.setVelocity(new Vector(0,0,0));
						}
					});
				}
			}
		}

		private void leftClick(Entity damager){
			if(damager instanceof Player && damager.equals(player) && this.getDistanceToOwner() < 3*3){
				leftClickTimeout = System.currentTimeMillis()+100;
				if(state != LobbyPokemonState.SITTING){
					state = LobbyPokemonState.FOLLOW;
					mode = (mode == LobbyPokemonMode.HOSTILE ? LobbyPokemonMode.FRIENDLY : LobbyPokemonMode.HOSTILE);
					Title.showTitle(player," ",0.2,2,0.2);
					Title.showSubTitle(player,(mode == LobbyPokemonMode.HOSTILE ? "§cUtocny mod" : "§aPratelsky mod"),0.2,2,0.2);
					if(mode == LobbyPokemonMode.HOSTILE){
						for(int i=0;i<8;i++){
							Bukkit.getScheduler().runTaskLaterAsynchronously(RealCraft.getInstance(),new Runnable(){
								@Override
								public void run(){
									Particles.REDSTONE.display(new OrdinaryColor(170,0,0),entity.getLocation().add(RandomUtil.getRandomDouble(-0.4,0.4),RandomUtil.getRandomDouble(0.7,1.2),RandomUtil.getRandomDouble(-0.4,0.4)),64);
									Particles.REDSTONE.display(new OrdinaryColor(170,0,0),entity.getLocation().add(RandomUtil.getRandomDouble(-0.4,0.4),RandomUtil.getRandomDouble(0.7,1.2),RandomUtil.getRandomDouble(-0.4,0.4)),64);
								}
							},i);
						}
					}
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
		FOLLOW, ATTACK, FRIEND, SITTING;
	}

	private enum LobbyPokemonMode {
		HOSTILE, FRIENDLY;
	}

	private class LobbyPokemonType {

		private int id;
		private String name;
		private ItemStack itemStack;

		private LobbyPokemonType(int id,String name,String url){
			this.id = id;
			this.name = name;
			this.itemStack = ItemUtil.getHead("§e"+name,url);
		}

		public int getId(){
			return id;
		}

		private String getName(){
			return name;
		}

		public ItemStack getItemStack(){
			return itemStack;
		}

		@SuppressWarnings("deprecation")
		public ItemStack getListItemStack(Player player){
			ItemStack item;
			ItemMeta meta;
			ArrayList<String> lore;
			if(LobbyPokemons.this.hasPlayerPokemon(player,this)){
				item = this.getItemStack().clone();
				meta = item.getItemMeta();
				lore = new ArrayList<String>();
				lore.add("§7Klikni pro spawnuti");
				meta.setLore(lore);
				item.setItemMeta(meta);
			} else {
				item = new ItemStack(Material.INK_SACK,1,(short)0,(byte)8);
				meta = item.getItemMeta();
				meta.setDisplayName("§e"+this.getName());
				lore = new ArrayList<String>();
				lore.add("§7Cena: §a"+PRICE+" coins");
				lore.add("§7Klikni pro zakoupeni");
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			return item;
		}

		public ItemStack getBuyItemStack(){
			ItemStack item;
			ItemMeta meta;
			ArrayList<String> lore;
			item = this.getItemStack().clone();
			meta = item.getItemMeta();
			meta.setDisplayName("§e"+this.getName());
			lore = new ArrayList<String>();
			lore.add("§7Cena: §a"+PRICE+" coins");
			lore.add("§7Klikni pro zakoupeni");
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}
	}
}