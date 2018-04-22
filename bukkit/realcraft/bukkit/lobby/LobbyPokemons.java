package realcraft.bukkit.lobby;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.SimpleNPCDataStore;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.util.YamlStorage;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.LookClose;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.PathEntity;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.auth.AuthLoginEvent;
import realcraft.bukkit.users.Users;
import realcraft.bukkit.utils.ItemUtil;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.Particles;
import realcraft.bukkit.utils.Particles.OrdinaryColor;
import realcraft.bukkit.utils.RandomUtil;
import realcraft.share.database.DB;
import ru.beykerykt.lightapi.LightAPI;

public class LobbyPokemons implements Listener {

	RealCraft plugin;
	private static final String POKEMONS = "pokemons";
	private static final String POKEMONS_USERS = "pokemons_users";
	private static final String invName = "Nabidka pokemonu";
	private static final String invBuyName = "Koupit pokemona";
	private static final String pokedexName = "Pokedex";
	private static final int NPC_ID = 602;
	private static final int PRICE = 500;
	private static ItemStack item = null;

	private NPCRegistry npcRegistry;
	private LobbyPokemonAsh ash;

	private HashMap<Integer,LobbyPokemonType> pokemonTypes = new HashMap<Integer,LobbyPokemonType>();
	private HashMap<Player,LobbyPokemon> pokemons = new HashMap<Player,LobbyPokemon>();
	private HashMap<Player,LobbyPokemonPlayer> players = new HashMap<Player,LobbyPokemonPlayer>();

	public LobbyPokemons(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		this.loadPokemons();
		this.loadAsh();
	}

	public void onDisable(){
		for(LobbyPokemon pokemon : pokemons.values()){
			pokemon.remove();
		}
	}

	private void loadPokemons(){
		ResultSet rs = DB.query("SELECT pokemon_id,pokemon_name,pokemon_url FROM "+POKEMONS);
		try {
			while(rs.next()){
				pokemonTypes.put(rs.getInt("pokemon_id"),new LobbyPokemonType(rs.getInt("pokemon_id"),rs.getString("pokemon_name"),rs.getString("pokemon_url")));
			}
			rs.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	private void loadAsh(){
		try {
			this.npcRegistry = CitizensAPI.createAnonymousNPCRegistry(SimpleNPCDataStore.create(new YamlStorage(new File(RealCraft.getInstance().getDataFolder()+"/citizens.tmp.yml"))));
			Location location = new Location(Bukkit.getWorld("world"),-83.5,65.0,64.5,0.0f,0.0f);
			ash = new LobbyPokemonAsh(location);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public LobbyPokemonPlayer getPokemonPlayer(Player player){
		if(!players.containsKey(player)) players.put(player,new LobbyPokemonPlayer(player));
		return players.get(player);
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItem(){
		if(item == null){
			item = new ItemStack(Material.MONSTER_EGG,1,(short)0,(byte)98);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§e§l"+pokedexName);
			item.setItemMeta(meta);
		}
		return item;
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Player player = event.getPlayer();
		player.getInventory().setItem(6,this.getItem());
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(Users.getUser(player).isLogged() && player.getWorld().getName().equalsIgnoreCase("world")){
			player.getInventory().setItem(6,this.getItem());
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
					event.getPlayer().getInventory().setItem(6,LobbyPokemons.this.getItem());
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
			if(Users.getUser(player).isLogged()){
				this.getPokemonPlayer(player).openPokedex();
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
							if(event.getRawSlot() == 45) ash.openMenu(player,this.getPokemonPlayer(player).getPage()-1);
							else if(event.getRawSlot() == 53) ash.openMenu(player,this.getPokemonPlayer(player).getPage()+1);
							player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
						} else {
							for(LobbyPokemonType type : pokemonTypes.values()){
								if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§e"+type.getName())){
									if(!this.getPokemonPlayer(player).hasPokemon(type)){
										ash.openBuyMenu(player,type);
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
						ash.buyPokemon(player,this.getPokemonPlayer(player).getBuying());
					}
					else if(item.getType() == Material.REDSTONE_BLOCK){
						ash.openMenu(player,this.getPokemonPlayer(player).getPage());
						player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
					}
				}
			}
		}
		else if(event.getInventory().getName().equalsIgnoreCase(pokedexName)){
			if(event.getWhoClicked() instanceof Player && ((Player)event.getWhoClicked()).getWorld().getName().equalsIgnoreCase("world")){
				event.setCancelled(true);
				Player player = (Player) event.getWhoClicked();
				ItemStack item = event.getCurrentItem();
				if(event.getRawSlot() >= 0 && event.getRawSlot() < 6*9){
					if(item.getType() != Material.AIR && item.hasItemMeta()){
						if(item.getType() == Material.PAPER){
							if(event.getRawSlot() == 45) this.getPokemonPlayer(player).openPokedex(this.getPokemonPlayer(player).getPage()-1);
							else if(event.getRawSlot() == 53) this.getPokemonPlayer(player).openPokedex(this.getPokemonPlayer(player).getPage()+1);
							player.playSound(player.getLocation(),Sound.UI_BUTTON_CLICK,1f,1f);
						} else {
							for(LobbyPokemonType type : pokemonTypes.values()){
								if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§e"+type.getName())){
									this.getPokemonPlayer(player).equipPokemon(type);
									break;
								}
							}
						}
					}
				}
			}
		}
		else if(event.getWhoClicked() instanceof Player){
			ItemStack item = event.getCurrentItem();
			if(item != null && item.getType() == this.getItem().getType() && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(this.getItem().getItemMeta().getDisplayName())){
				event.setCancelled(true);
			}
		}
	}

	private void createPokemon(Player player,LobbyPokemonType type){
		if(pokemons.containsKey(player)) this.removePokemon(player);
		pokemons.put(player,new LobbyPokemon(player,type));
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
		private long arrivedTimeout = 0;
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
			location.add(location.getDirection().setY(0).normalize().multiply(1.5));
			entity = player.getWorld().spawnEntity(location,EntityType.ZOMBIE);
			entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_VEX_CHARGE,1f,1f);
			if(entity != null){
				((Zombie)entity).setSilent(true);
				((Zombie)entity).setBaby(true);
				((Zombie)entity).getEquipment().clear();
				((Zombie)entity).getEquipment().setHelmet(type.getItemStack());
				((Zombie)entity).getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
				((Zombie)entity).getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
				((Zombie)entity).getEquipment().setItemInMainHandDropChance(0);
				((Zombie)entity).getEquipment().setItemInOffHandDropChance(0);
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
						switch(RandomUtil.getRandomInteger(0,3)){
							case 0: entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_GHAST_HURT,0.3f,1f);
							case 1: entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_CAT_HISS,0.3f,1f);
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
							double speed = (distance > 2*2 ? 0.7 : 0.5);
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
					if(tick == 0 && distance <= 1*1){
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
					if(distance > 32*32 && player.isOnGround()){
						((CraftEntity)entity).getHandle().setLocation(targetLocation.getBlockX(),targetLocation.getBlockY(),targetLocation.getBlockZ(),0,0);
					}
					if(path != null){
						((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(path,speed);
						((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
						arrivedTimeout = System.currentTimeMillis()+600;
					}
				} else {
					if(arrivedTimeout >= System.currentTimeMillis()){
						((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(0);
						if(tick == 0) entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_GHAST_AMBIENT,1f,2f);
					} else {
						speed = 0.5;
						if(tick == 0 && RandomUtil.getRandomBoolean()){
							((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
							targetLocation.add(RandomUtil.getRandomDouble(-2.0,2.0),0,RandomUtil.getRandomDouble(-2.0,2.0));
							PathEntity path;
							path = ((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(targetLocation.getX(),targetLocation.getY(),targetLocation.getZ());
							if(path != null){
								((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(path,speed);
								((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(speed);
							}
						}
					}
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
				((EntityInsentient) ((CraftEntity)entity).getHandle()).getNavigation().a(0);
				bField.set(((EntityInsentient) nmsEntity).goalSelector,Sets.newLinkedHashSet());
				bField.set(((EntityInsentient) nmsEntity).targetSelector,Sets.newLinkedHashSet());
				cField.set(((EntityInsentient) nmsEntity).goalSelector,Sets.newLinkedHashSet());
				cField.set(((EntityInsentient) nmsEntity).targetSelector,Sets.newLinkedHashSet());
			} catch (Exception e){
				e.printStackTrace();
			}
		}

		private void effect(){
			if(entity == null || entity.isDead()) return;
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

		@EventHandler
		public void ChunkUnloadEvent(ChunkUnloadEvent event){
			for(Entity entity : event.getChunk().getEntities()){
				if(entity.equals(this.getEntity())){
					event.setCancelled(true);
				}
			}
		}

		private void rightClick(Entity damager){
			if(damager instanceof Player && damager.equals(player) && this.getDistanceToOwner() < 3*3 && sitTimeout < System.currentTimeMillis()){
				rightClickTimeout = System.currentTimeMillis()+100;
				if(state == LobbyPokemonState.FOLLOW || state == LobbyPokemonState.FRIEND){
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
					this.clearPathfinders(entity);
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
					this.clearPathfinders(entity);
				}
			}
		}

		private void leftClick(Entity damager){
			if(damager instanceof Player && damager.equals(player) && this.getDistanceToOwner() < 3*3){
				player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ITEM_PICKUP,1f,1f);
				Particles.SPELL_WITCH.display(0.2f,0.2f,0.2f,0.5f,8,entity.getLocation().add(0f,0.9f,0f),64);
				LobbyPokemons.this.removePokemon(player);
				//leftClickTimeout = System.currentTimeMillis()+1000;
			}
			/*if(damager instanceof Player && damager.equals(player) && this.getDistanceToOwner() < 3*3){
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
			}*/
		}

		public void remove(){
			HandlerList.unregisterAll(this);
			if(taskMove != null) taskMove.cancel();
			if(taskEffect != null) taskEffect.cancel();
			if(entity != null) entity.remove();
		}
	}

	private class LobbyPokemonPlayer {

		private Player player;
		private int page = 1;
		private LobbyPokemonType buying;
		private HashMap<Integer,LobbyPokemonType> pokemons = new HashMap<Integer,LobbyPokemonType>();

		public LobbyPokemonPlayer(Player player){
			this.player = player;
			this.loadPlayerPokemons();
		}

		public int getPage(){
			return page;
		}

		public void setPage(int page){
			this.page = page;
		}

		public LobbyPokemonType getBuying(){
			return buying;
		}

		public void setBuying(LobbyPokemonType buying){
			this.buying = buying;
		}

		public boolean hasPokemon(LobbyPokemonType type){
			return pokemons.containsKey(type.getId());
		}

		public void loadPlayerPokemons(){
			ResultSet rs = DB.query("SELECT pokemon_id FROM "+POKEMONS_USERS+" WHERE user_id = '"+Users.getUser(player).getId()+"'");
			try {
				pokemons.clear();
				while(rs.next()){
					int id = rs.getInt("pokemon_id");
					pokemons.put(id,pokemonTypes.get(id));
				}
				rs.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
		}

		public void addPokemon(LobbyPokemonType type){
			pokemons.put(type.getId(),type);
			DB.update("INSERT INTO "+POKEMONS_USERS+" (user_id,pokemon_id) VALUES('"+Users.getUser(player).getId()+"','"+type.getId()+"')");
		}

		public void openPokedex(){
			this.openPokedex(1);
		}

		public void openPokedex(int page){
			LobbyPokemons.this.getPokemonPlayer(player).setPage(page);
			Inventory inventory = Bukkit.createInventory(null,6*9,pokedexName);
			ItemStack item;
			ItemMeta meta;

			ArrayList<LobbyPokemonType> pokemonsTmp = new ArrayList<LobbyPokemonType>(pokemons.values());
			for(int i=0;i<5*9;i++){
				int index = i+((page-1)*(5*9));
				if(pokemonsTmp.size() > index){
					LobbyPokemonType type = pokemonsTmp.get(index);
					if(type != null){
						inventory.setItem(i,type.getPokedexItemStack());
					}
				}
			}

			int maxPage = (int)Math.ceil(pokemonsTmp.size()/(5*9.0));
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

		public void equipPokemon(LobbyPokemonType type){
			if(this.hasPokemon(type)){
				LobbyPokemons.this.createPokemon(player,type);
			}
			player.closeInventory();
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

		public ItemStack getListItemStack(Player player){
			ItemStack item;
			ItemMeta meta;
			ArrayList<String> lore;
			if(LobbyPokemons.this.getPokemonPlayer(player).hasPokemon(this)){
				item = new ItemStack(Material.AIR);
			} else {
				item = this.getItemStack().clone();
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

		public ItemStack getPokedexItemStack(){
			ItemStack item;
			ItemMeta meta;
			ArrayList<String> lore;
			item = this.getItemStack().clone();
			meta = item.getItemMeta();
			lore = new ArrayList<String>();
			lore.add("§7Klikni pro spawnuti");
			meta.setLore(lore);
			item.setItemMeta(meta);
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

	@EventHandler
	public void NPCRightClickEvent(NPCRightClickEvent event){
		if(event.getNPC().getId() == NPC_ID){
			ash.onPlayerClick(event.getClicker());
		}
	}

	@EventHandler
	public void NPCLeftClickEvent(NPCLeftClickEvent event){
		if(event.getNPC().getId() == NPC_ID){
			ash.onPlayerClick(event.getClicker());
		}
	}

	@EventHandler
	public void NPCSpawnEvent(NPCSpawnEvent event){
		if(event.getNPC().getId() == NPC_ID){
			if(ash != null) ash.updateSkin();
		}
	}

	@EventHandler
	public void ChunkLoadEvent(ChunkLoadEvent event){
		if(ash != null){
			if(ash.isInChunk(event.getChunk())){
				ash.spawn();
			}
		}
	}

	@EventHandler
	public void ChunkUnloadEvent(ChunkUnloadEvent event){
		if(ash != null){
			if(ash.isInChunk(event.getChunk())){
				ash.remove();
			}
		}
	}

	private class LobbyPokemonAsh {
		NPC npc;
		Location location;
		SkinnableEntity skinnable = null;

		public LobbyPokemonAsh(Location location){
			this.location = location;
			this.npc = npcRegistry.createNPC(EntityType.PLAYER,UUID.fromString("00000000-0000-0000-0000-00000000000"+NPC_ID),NPC_ID,"");
			npc.setName("§f§lAsh");
			npc.setProtected(true);

			LookClose look = npc.getTrait(LookClose.class);
			look.setRange(3);
			look.toggle();

			this.spawn();
			LightAPI.createLight(location.clone().add(0.0,1.0,0.0),15,false);
			Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),new Runnable(){
				@Override
				public void run(){
					LobbyPokemonAsh.this.updateSkin();
				}
			},20,20);
		}

		public boolean isInChunk(Chunk chunk){
			return (location.getBlockX() >> 4 == chunk.getX() && location.getBlockZ() >> 4 == chunk.getZ());
		}

		public void onPlayerClick(Player player){
			this.openMenu(player);
		}

		public void spawn(){
			npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA,"eyJ0aW1lc3RhbXAiOjE1MDczNzAxNDQ3NzYsInByb2ZpbGVJZCI6ImE5MGI4MmIwNzE4NTQ0ZjU5YmE1MTZkMGY2Nzk2NDkwIiwicHJvZmlsZU5hbWUiOiJJbUZhdFRCSCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY5YWI4YjBmMTlhMWM5OWZlM2FkODZlYTFhMmVhMmJlZWVmYmE4ZTFiOTM0MzMwODc0M2I3YmNiZDgifX19");
			npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA,"kxHR94GVVXcIylOruGoa6wRJoayaHqW4bmGl+YJfAClDUIU6KrX0+o1PIzH2G7xeYkLCx2FiZUAytVH4t+FEE99kduVXqVSSqhq/0A1OcoYAAhTfYrfBXhuygQ8PR0sFlgDUnoJ4b4PbBn/oYvwx0kymNd0Xp3x0S+gmSbSrh+C9kmcX8INKkbXnbz4pH+kkQAr8OerpSU12bxTB3ohULkvD1ujP1Bq8QGxomhtP7NBbvHypfvlweDYoScullCvzeIlzkGvZ88uHTh5PxruO045zx7iwndxGrDbW1SGdV1u5CiCESx8SYHTCOx4JaWkvTE33RfrJ5M8/XYW2TzaYaAOnwo5jYzVA6E/nQpopNLzjSt420AiastcaMW7JUWGXiNB6yi1Dkz/U6TvaLMNHVLmiNQ7zdlfnNO290C+rGfphWlHhTHl1CMH99RFMY01HDm8w+Z36eVOE15wTc9aEthCHJCL47nao//rCdkPVeiXHxD/+ZitqFa7Sd4yC/pQCd5g3AocRZMkEC+JBW1dKXM52dEZBIs6xMXZPhgGiqkRecun4ozKET1QfAWmOQv5dHUCo7EKy4equjVx12uh7h+H06FNsNNbL83os4fOnZGEqAq5y1k77P10OVLhrQ3MdC7xGjqOZQka9Uld614slpmBWdwxvH5sM8fVIrOCjCGM=");
			npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST,false);
			npc.spawn(location);
		}

		public void remove(){
			npc.despawn(DespawnReason.PLUGIN);
			skinnable = null;
		}

		public void updateSkin(){
			npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA,"eyJ0aW1lc3RhbXAiOjE1MDczNzAxNDQ3NzYsInByb2ZpbGVJZCI6ImE5MGI4MmIwNzE4NTQ0ZjU5YmE1MTZkMGY2Nzk2NDkwIiwicHJvZmlsZU5hbWUiOiJJbUZhdFRCSCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY5YWI4YjBmMTlhMWM5OWZlM2FkODZlYTFhMmVhMmJlZWVmYmE4ZTFiOTM0MzMwODc0M2I3YmNiZDgifX19");
			npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA,"kxHR94GVVXcIylOruGoa6wRJoayaHqW4bmGl+YJfAClDUIU6KrX0+o1PIzH2G7xeYkLCx2FiZUAytVH4t+FEE99kduVXqVSSqhq/0A1OcoYAAhTfYrfBXhuygQ8PR0sFlgDUnoJ4b4PbBn/oYvwx0kymNd0Xp3x0S+gmSbSrh+C9kmcX8INKkbXnbz4pH+kkQAr8OerpSU12bxTB3ohULkvD1ujP1Bq8QGxomhtP7NBbvHypfvlweDYoScullCvzeIlzkGvZ88uHTh5PxruO045zx7iwndxGrDbW1SGdV1u5CiCESx8SYHTCOx4JaWkvTE33RfrJ5M8/XYW2TzaYaAOnwo5jYzVA6E/nQpopNLzjSt420AiastcaMW7JUWGXiNB6yi1Dkz/U6TvaLMNHVLmiNQ7zdlfnNO290C+rGfphWlHhTHl1CMH99RFMY01HDm8w+Z36eVOE15wTc9aEthCHJCL47nao//rCdkPVeiXHxD/+ZitqFa7Sd4yC/pQCd5g3AocRZMkEC+JBW1dKXM52dEZBIs6xMXZPhgGiqkRecun4ozKET1QfAWmOQv5dHUCo7EKy4equjVx12uh7h+H06FNsNNbL83os4fOnZGEqAq5y1k77P10OVLhrQ3MdC7xGjqOZQka9Uld614slpmBWdwxvH5sM8fVIrOCjCGM=");
			npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST,false);
			SkinnableEntity skinnableTmp = (SkinnableEntity) npc.getEntity();
			if(skinnableTmp != null && skinnable == null){
				skinnable = skinnableTmp;
				skinnable.setSkinName("steve",false);
			}
			else if(skinnableTmp == null) skinnable = null;
			Equipment equip = npc.getTrait(Equipment.class);
			equip.set(Equipment.EquipmentSlot.HAND,ItemUtil.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJhODUwZmVhYmIwNzM0OWNmZTI0NWIyNmEyNjRlYTM2ZGY3MzMzOGY4NGNkMmVlMzgzM2IxODVlMWUyZTJkOCJ9fX0="));
		}

		public void openMenu(Player player){
			player.playSound(player.getLocation(),Sound.ENTITY_VILLAGER_TRADING,1f,1f);
			this.openMenu(player,1);
		}

		public void openMenu(Player player,int page){
			LobbyPokemons.this.getPokemonPlayer(player).setPage(page);
			Inventory inventory = Bukkit.createInventory(null,6*9,invName);
			ItemStack item;
			ItemMeta meta;

			ArrayList<LobbyPokemonType> pokemonsTmp = new ArrayList<LobbyPokemonType>(pokemonTypes.values());
			for(int i=0;i<5*9;i++){
				int index = i+((page-1)*(5*9));
				if(pokemonsTmp.size() > index){
					LobbyPokemonType type = pokemonsTmp.get(index);
					if(type != null){
						inventory.setItem(i,type.getListItemStack(player));
					}
				}
			}

			int maxPage = (int)Math.ceil(pokemonsTmp.size()/(5*9.0));
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

		public void openBuyMenu(Player player,LobbyPokemonType type){
			LobbyPokemons.this.getPokemonPlayer(player).setBuying(type);
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

		public void buyPokemon(Player player,LobbyPokemonType type){
			if(Users.getUser(player).getCoins() < PRICE){
				player.sendMessage("§cNemas dostatek coinu.");
				player.playSound(player.getLocation(),Sound.ENTITY_VILLAGER_NO,1f,1f);
				return;
			}
			Users.getUser(player).giveCoins(-PRICE);
			LobbyPokemons.this.getPokemonPlayer(player).addPokemon(type);
			player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
			location.getWorld().playSound(location,Sound.ENTITY_VILLAGER_YES,1f,1f);
			ash.openMenu(player,LobbyPokemons.this.getPokemonPlayer(player).getPage());
		}
	}
}