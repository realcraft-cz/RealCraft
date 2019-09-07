package realcraft.bukkit.cosmetics;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.CosmeticTransactions.CosmeticTransaction;
import realcraft.bukkit.cosmetics.cosmetic.Cosmetic;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticCategory;
import realcraft.bukkit.cosmetics.gadgets.Gadget;
import realcraft.bukkit.utils.*;
import ru.beykerykt.lightapi.LightAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CosmeticCrystals implements Listener, Runnable {

	private static final int CRYSTAL_PRICE = 100;
	private static final String CRYSTAL_INV_NAME = "Lucky Crystal";
	private static final String CRYSTAL_LUCKY_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGI5MmNiNDMzMzNhYTYyMWM3MGVlZjRlYmYyOTliYTQxMmI0NDZmZTEyZTM0MWNjYzU4MmYzMTkyMTg5In19fQ";

	private ArrayList<CosmeticCrystal> crystals = new ArrayList<>();
	private ArrayList<CosmeticHead> heads = new ArrayList<>();

	private FileConfiguration config;
	private Inventory inventory;

	public CosmeticCrystals(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,5*20,5*20);
		this.loadCrystals();
		this.loadHeads();
		CosmeticTransactions.init(heads.size());
		this.update();
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RealCraft.getInstance(),ListenerPriority.NORMAL,PacketType.Play.Client.USE_ENTITY){
			@Override
			public void onPacketReceiving(PacketEvent event){
				Player player = event.getPlayer();
				if(event.getPacketType() == PacketType.Play.Client.USE_ENTITY){
					if(event.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK){
						for(CosmeticCrystal crystal : crystals){
							if(crystal.getEntityId() == event.getPacket().getIntegers().read(0)){
								Bukkit.getServer().getScheduler().runTask(RealCraft.getInstance(),new Runnable(){
									@Override
									public void run(){
										crystal.click(player);
									}
								});
							}
						}
					}
				}
			}
		});
	}

	@Override
	public void run(){
		for(CosmeticHead head : heads){
			head.update(false);
		}
	}

	public void update(){
		ArrayList<CosmeticTransaction> transactions = CosmeticTransactions.getLastTransactions();
		int idx = 0;
		for(CosmeticHead head : heads){
			if(transactions.size() > idx) head.update(transactions.get(idx++));
		}
	}

	private Inventory getInventory(){
		if(inventory == null){
			inventory = Bukkit.createInventory(null,6*9,CRYSTAL_INV_NAME);
			ItemStack item;
			ItemMeta meta;

			item = ItemUtil.getHead("§a§lLucky Crystal",CRYSTAL_LUCKY_TEXTURE);
			meta = item.getItemMeta();
			meta.setLore(ItemUtil.getLores("§7Otevri crystal a ziskej","§7spoustu skvelych doplnku"));
			item.setItemMeta(meta);
			inventory.setItem(13,item);

			item = new ItemStack(Material.EMERALD_BLOCK);
			meta = item.getItemMeta();
			meta.setDisplayName("§a§lOtevrit");
			meta.setLore(ItemUtil.getLores(
					"§7Cena: §a"+CRYSTAL_PRICE+" coins",
					"§7Klikni pro zakoupeni"
			));
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
			meta.setLore(ItemUtil.getLores("§7Klikni pro zruseni"));
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
		}
		return inventory;
	}

	private void openMenu(Player player){
		player.openInventory(this.getInventory());
	}

	private FileConfiguration getConfig(){
		if(config == null){
			File file = new File(RealCraft.getInstance().getDataFolder() + "/cosmetics.yml");
			if(file.exists()){
				config = new YamlConfiguration();
				try {
					config.load(file);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		return config;
	}

	@SuppressWarnings("unchecked")
	private void loadCrystals(){
		List<Map<String, Object>> tmpList = (List<Map<String, Object>>) this.getConfig().get("crystals");
		if(tmpList != null && !tmpList.isEmpty()){
			int idx = 1;
			for(Map<String, Object> entry : tmpList){
				double x = Double.valueOf(entry.get("x").toString());
				double y = Double.valueOf(entry.get("y").toString());
				double z = Double.valueOf(entry.get("z").toString());
				float yaw = Float.valueOf(entry.get("yaw").toString());
				float pitch = Float.valueOf(entry.get("pitch").toString());
				World world = Bukkit.getServer().getWorld(entry.get("world").toString());
				Location location = new Location(world,x,y,z,yaw,pitch);
				crystals.add(new CosmeticCrystal(idx++,location));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadHeads(){
		List<Map<String, Object>> tmpList = (List<Map<String, Object>>) this.getConfig().get("heads");
		if(tmpList != null && !tmpList.isEmpty()){
			for(Map<String, Object> entry : tmpList){
				double x = Double.valueOf(entry.get("x").toString());
				double y = Double.valueOf(entry.get("y").toString());
				double z = Double.valueOf(entry.get("z").toString());
				float yaw = Float.valueOf(entry.get("yaw").toString());
				float pitch = Float.valueOf(entry.get("pitch").toString());
				World world = Bukkit.getServer().getWorld(entry.get("world").toString());
				Location location = new Location(world,x,y,z,yaw,pitch);
				heads.add(new CosmeticHead(location));
			}
		}
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(event.getClickedBlock() != null && MaterialUtil.isShulkerBox(event.getClickedBlock().getType())){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event){
		if(event.getRightClicked().getType() == EntityType.ENDER_CRYSTAL){
			for(CosmeticCrystal crystal : crystals){
				if(crystal.getName().equals(event.getRightClicked().getCustomName())){
					event.setCancelled(true);
					crystal.click(event.getPlayer());
					break;
				}
			}
		}
	}

	@EventHandler
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getEntity().getType() == EntityType.ENDER_CRYSTAL){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event){
		for(CosmeticHead head : heads){
			if(head.getName().equals(event.getRightClicked().getCustomName())){
				event.setCancelled(true);
				return;
			}
		}
		for(CosmeticCrystal crystal : crystals){
			if(crystal.getStand() != null && crystal.getStand().getEntityId() == event.getRightClicked().getEntityId()){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player && event.getView().getTitle().equalsIgnoreCase(CRYSTAL_INV_NAME)){
			event.setCancelled(true);
			Player player = (Player)event.getWhoClicked();
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.EMERALD_BLOCK){
				player.closeInventory();
				CosmeticCrystal crystal = Cosmetics.getCosmeticPlayer(player).getMenuCrystal();
				if(crystal.isOccupied()){
					player.sendMessage("§cLucky Crystal prave nekdo otevira!");
					return;
				}
				if(Cosmetics.getCosmeticPlayer(player).getUser().getCoins() < CRYSTAL_PRICE){
					player.sendMessage("§cNemas dostatek coinu.");
					return;
				}
				crystal.open(player);
			}
			else if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.REDSTONE_BLOCK){
				player.closeInventory();
			}
		}
	}

	@EventHandler
	public void ChunkLoadEvent(ChunkLoadEvent event){
		for(CosmeticCrystal crystal : crystals){
			if(crystal.isInChunk(event.getChunk())){
				crystal.spawn();
			}
		}
		for(CosmeticHead head : heads){
			if(head.isInChunk(event.getChunk())){
				head.spawn();
			}
		}
	}

	@EventHandler
	public void ChunkUnloadEvent(ChunkUnloadEvent event){
		for(CosmeticCrystal crystal : crystals){
			if(crystal.isInChunk(event.getChunk())){
				crystal.remove();
			}
		}
		for(CosmeticHead head : heads){
			if(head.isInChunk(event.getChunk())){
				head.remove();
			}
		}
	}

	public class CosmeticCrystal {

		private int id;
		private Location location;
		private EnderCrystal crystal;
		private Hologram hologramName;
		private Hologram hologramReward;
		private ArmorStand stand;

		private boolean occupied = false;

		public CosmeticCrystal(int id,Location location){
			this.id = id;
			this.location = location;
			this.init();
			this.spawn();
		}

		public String getName(){
			return "crystal"+id;
		}

		public int getEntityId(){
			return (crystal != null ? crystal.getEntityId() : 0);
		}

		public ArmorStand getStand(){
			return stand;
		}

		public boolean isInChunk(Chunk chunk){
			return (location.getBlockX() >> 4 == chunk.getX() && location.getBlockZ() >> 4 == chunk.getZ());
		}

		public boolean isOccupied(){
			return occupied;
		}

		private void init(){
			hologramName = HologramsAPI.createHologram(RealCraft.getInstance(),location.clone().add(0.0,2.0,0.0));
			hologramName.insertTextLine(0,"§a§lLucky Crystal");
			hologramReward = HologramsAPI.createHologram(RealCraft.getInstance(),location.clone().add(0.0,2.0,0.0));
			LightAPI.createLight(location.clone().add(0.0,2.0,0.0),15,false);
		}

		private void spawn(){
			if(occupied) return;
			this.remove();
			hologramReward.clearLines();
			hologramName.clearLines();
			hologramName.insertTextLine(0,"§a§lLucky Crystal");
			crystal = (EnderCrystal)location.getWorld().spawnEntity(location,EntityType.ENDER_CRYSTAL);
			crystal.setCustomName(this.getName());
			crystal.setCustomNameVisible(false);
			crystal.setInvulnerable(true);
			crystal.setShowingBottom(false);
			crystal.setGravity(false);
			stand = (ArmorStand)location.getWorld().spawnEntity(location.clone().add(0.0,-2.0,0.0),EntityType.ARMOR_STAND);
			stand.setCustomName("§7§kABCDEFGHK");
			stand.setCustomNameVisible(false);
			stand.setSmall(true);
			stand.setBasePlate(false);
			stand.setArms(false);
			stand.setVisible(false);
			stand.setGravity(false);
			stand.setInvulnerable(true);
			stand.getEquipment().setHelmet(ItemUtil.getHead(CRYSTAL_LUCKY_TEXTURE));
			for(Entity entity : crystal.getNearbyEntities(3.0,3.0,3.0)){
				if(entity.getType() == EntityType.ENDER_CRYSTAL && crystal.getCustomName().equalsIgnoreCase(entity.getCustomName())){
					entity.remove();
				}
			}
			for(Entity entity : stand.getNearbyEntities(3.0,3.0,3.0)){
				if(entity.getType() == EntityType.ARMOR_STAND && stand.getCustomName().equalsIgnoreCase(entity.getCustomName())){
					entity.remove();
				}
			}
		}

		private void remove(){
			if(crystal != null && !crystal.isDead()){
				crystal.remove();
				stand.remove();
				crystal = null;
				stand = null;
			}
		}

		public void click(Player player){
			if(this.isOccupied()){
				player.sendMessage("§cLucky Crystal prave nekdo otevira!");
				return;
			}
			Cosmetics.getCosmeticPlayer(player).setMenuCrystal(this);
			CosmeticCrystals.this.openMenu(player);
		}

		public void open(Player player){
			occupied = true;
			Cosmetic cosmetic = Cosmetics.getRandomCosmetic(player);
			BukkitRunnable runnable = new BukkitRunnable(){
				private int step = 0;
				@Override
				public void run(){
					if(step == 0){
						crystal.remove();
						crystal = null;
						hologramName.clearLines();
						step = 1;
						Particles.CLOUD.display(0.3f,0.3f,0.3f,0.02f,16,location.clone().add(0.0,0.5,0.0),64);
						location.getWorld().playSound(location,Sound.ENTITY_GENERIC_EXTINGUISH_FIRE,0.5f,1f);
					}
					else if(step == 1){
						stand.teleport(location.clone().add(0,-2.0,0));
						stand.getEquipment().setHelmet(ItemUtil.getHead(CRYSTAL_LUCKY_TEXTURE));
						stand.setCustomNameVisible(true);
						step = 2;
					}
					else if(step == 2){
						if(stand.getLocation().getY() < location.getY()+0.2){
							float progress = (float)(1f-(((location.getY()+0.2)-stand.getLocation().getY())/2f));
							Location loc = stand.getLocation().add(0,0.05,0);
							loc.setYaw(loc.getYaw()+10);
							stand.teleport(loc,PlayerTeleportEvent.TeleportCause.PLUGIN);
							stand.getWorld().playSound(stand.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS,1f,1f+progress);
							stand.getWorld().playSound(stand.getLocation(),Sound.BLOCK_NOTE_BLOCK_PLING,0.3f,1f+progress);
							stand.getWorld().playSound(stand.getLocation(),Sound.ENTITY_CHICKEN_EGG,0.5f,1f+progress);
							Particles.ENCHANTMENT_TABLE.display(0.15f,0.15f,0.15f,0f,2,stand.getLocation().clone().add(0.0,0.8,0.0),64);
						} else {
							FireworkUtil.spawnFirework(location.clone().add(0.0,1.0,0.0),FireworkEffect.Type.BALL,Color.FUCHSIA,false,false);
							step = 3;
						}
					}
					else if(step == 3){
						step = 4;
						CosmeticPlayer cPlayer = Cosmetics.getCosmeticPlayer(player);
						int amount = 1;
						if(cosmetic.getType().getCategory() == CosmeticCategory.GADGET) amount = ((Gadget)cosmetic).getRandomAmount();
						cPlayer.getUser().giveCoins(-CRYSTAL_PRICE);
						cPlayer.addCosmetic(cosmetic.getType(),amount);
						CosmeticTransactions.addTransaction(cPlayer,cosmetic.getType(),amount);
						CosmeticCrystals.this.update();
						hologramReward.insertTextLine(0,"§e"+cosmetic.getType().getCategory().getName());
						hologramReward.insertTextLine(1,cosmetic.getType().getName()+(amount > 1 ? "§r §7("+amount+")" : ""));
						try {
							hologramReward.insertItemLine(2,cosmetic.getItemStack());
						} catch(IllegalArgumentException e){
						}
						Particles.VILLAGER_HAPPY.display(0.7f,0.7f,0.7f,0f,14,location.clone().add(0.0,1.0,0.0),64);
						location.getWorld().playSound(location,Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
						stand.remove();
						stand = null;
					}
					else if(step >= 4){
						step ++;
						if(step == 60){
							this.cancel();
							occupied = false;
							CosmeticCrystal.this.spawn();
						}
					}
				}
			};
			runnable.runTaskTimer(RealCraft.getInstance(),2,2);
		}
	}

	private class CosmeticHead {

		private Location location;
		private ArmorStand stand;
		private Hologram hologram;

		private CosmeticTransaction transaction;
		private ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		private String createdText;

		public CosmeticHead(Location location){
			this.location = location;
			hologram = HologramsAPI.createHologram(RealCraft.getInstance(),location.clone().add(0.0,1.0,0.0));
			hologram.insertTextLine(0,"§r");
			this.spawn();
		}

		public String getName(){
			return "crystalhead";
		}

		public boolean isInChunk(Chunk chunk){
			return (location.getBlockX() >> 4 == chunk.getX() && location.getBlockZ() >> 4 == chunk.getZ());
		}

		private void spawn(){
			this.remove();
			stand = (ArmorStand)location.getWorld().spawnEntity(location.clone().add(0.0,-1.0,0.0),EntityType.ARMOR_STAND);
			stand.setCustomName(this.getName());
			stand.setCustomNameVisible(false);
			stand.setSmall(true);
			stand.setBasePlate(false);
			stand.setArms(false);
			stand.setVisible(false);
			stand.setGravity(false);
			stand.setInvulnerable(true);
			stand.getEquipment().setHelmet(head);
			for(Entity entity : stand.getNearbyEntities(1.5,1.5,1.5)){
				if(entity.getType() == EntityType.ARMOR_STAND){
					entity.remove();
				}
			}
		}

		private void remove(){
			if(stand != null && !stand.isDead()){
				stand.remove();
				stand = null;
			}
		}

		public void update(boolean force){
			if(transaction != null){
				if(force || !DateUtil.lastTime(transaction.getCreated(),true).equalsIgnoreCase(createdText)){
					hologram.clearLines();
					createdText = DateUtil.lastTime(transaction.getCreated(),true);
					hologram.insertTextLine(0,"§a"+transaction.getCPlayer().getUser().getName()+"§7 "+createdText);
					hologram.insertTextLine(1,transaction.getType().getName()+(transaction.getAmount() > 1 ? "§r §7("+transaction.getAmount()+")" : ""));
				}
			}
		}

		public void update(CosmeticTransaction transaction){
			this.transaction = transaction;
			head = ItemUtil.getHead(transaction.getCPlayer().getUser().getSkin().getValue());
			stand.getEquipment().setHelmet(head);
			this.update(true);
		}
	}
}