package com.realcraft.lobby;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.EnderChest;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.realcraft.RealCraft;
import com.realcraft.auth.AuthLoginEvent;
import com.realcraft.playermanazer.PlayerManazer;
import com.realcraft.utils.FireworkUtil;
import com.realcraft.utils.Glow;
import com.realcraft.utils.LocationUtil;
import com.realcraft.utils.Particles;
import com.realcraft.utils.Title;
import com.utils.UtilParticles;

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
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.TileEntityChest;
import net.minecraft.server.v1_12_R1.TileEntityEnderChest;
import ru.beykerykt.lightapi.LightAPI;

public class LobbyMystery implements Listener, Runnable {
	RealCraft plugin;

	private static final int NPC_ID = 601;
	private static final String SMITH_INVNAME = "Ukovat magicky klic";
	private static final int SMITH_PRICE = 100;

	private NPCRegistry npcRegistry;
	private LobbyMysterySmith smith;

	private File chestsFile;
	private FileConfiguration chestsConfig;

	HashMap<Location,LobbyChest> chests = new HashMap<Location,LobbyChest>();

	boolean debug = false;

	public LobbyMystery(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,20,20);
		chestsFile = new File(RealCraft.getInstance().getDataFolder()+"/mystery/"+plugin.serverName+".yml");
		if(chestsFile.exists()){
			chestsConfig = new YamlConfiguration();
			try {
				chestsConfig.load(chestsFile);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		this.loadChest();
		LobbyMystery.this.loadSmith();
	}

	public void onReload(){
	}

	@EventHandler
	public void AuthLoginEvent(AuthLoginEvent event){
		Player player = event.getPlayer();
		this.loadPlayerKeys(player);
	}

	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(PlayerManazer.getPlayerInfo(player).isLogged()) this.loadPlayerKeys(player);
	}

	@EventHandler
	public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event){
		if(event.getFrom().getName().equalsIgnoreCase("world")){
			event.getPlayer().getInventory().remove(Material.TRIPWIRE_HOOK);
		}
		else if(event.getPlayer().getWorld().getName().equalsIgnoreCase("world")){
			this.loadPlayerKeys(event.getPlayer());
		}
	}

	public void givePlayerKey(Player player){
		this.givePlayerKey(player,1);
	}

	public void givePlayerKey(Player player,int amount){
		PlayerManazer.getPlayerInfo(player).givePlayerKeys(amount);
		this.loadPlayerKeys(player);
		player.getInventory().setHeldItemSlot(8);
	}

	public void removePlayerKey(Player player){
		PlayerManazer.getPlayerInfo(player).removePlayerKeys(1);
		this.loadPlayerKeys(player);
	}

	public void loadPlayerKeys(Player player){
		if(player.getWorld().getName().equalsIgnoreCase("world")){
			int keys = PlayerManazer.getPlayerInfo(player).getLobbyKeys();
			player.getInventory().remove(Material.TRIPWIRE_HOOK);
			if(keys > 0){
				if(keys > 64) keys = 64;
				ItemStack chestKey = getChestKey();
				chestKey.setAmount(keys);
				player.getInventory().setItem(8,chestKey);
			}
		}
	}

	public ItemStack getChestKey(){
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§fTimto klicem muzes otevrit");
		lore.add("§fmagickou truhlu a ziskat");
		lore.add("§fspoustu zajimavych veci.");
		Glow glow = new Glow(255);
		ItemStack chestKey = new ItemStack(Material.TRIPWIRE_HOOK,1);
		ItemMeta meta = chestKey.getItemMeta();
		meta.setDisplayName("§b§l"+"Klic k truhle");
		meta.setLore(lore);
		meta.addEnchant(glow,10,true);
		chestKey.setItemMeta(meta);
		return chestKey;
	}

	public int getRandomNumber(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	@EventHandler(ignoreCancelled = true)
	public void PlayerDropItemEvent(PlayerDropItemEvent event){
		if(event.getItemDrop().getItemStack().getType() == Material.TRIPWIRE_HOOK && event.getItemDrop().getItemStack().hasItemMeta() && event.getItemDrop().getItemStack().getItemMeta().hasDisplayName() && event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§b§l"+"Klic k truhle")){
			PlayerManazer.getPlayerInfo(event.getPlayer()).removePlayerKeys(event.getItemDrop().getItemStack().getAmount());
		}
	}

	@EventHandler
    public void EntityPickupItemEvent(EntityPickupItemEvent event){
		if(event.getEntity() instanceof Player){
			Player player = (Player)event.getEntity();
			if(event.getItem().getItemStack().getType() == Material.TRIPWIRE_HOOK && event.getItem().getItemStack().hasItemMeta() && event.getItem().getItemStack().getItemMeta().hasDisplayName() && event.getItem().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§b§l"+"Klic k truhle")){
				PlayerManazer.getPlayerInfo(player).givePlayerKeys(event.getItem().getItemStack().getAmount());
			}
		}
	}

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			ItemStack item = event.getCurrentItem();
			if(item != null && item.getType() == Material.TRIPWIRE_HOOK && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§b§l"+"Klic k truhle")){
				event.setCancelled(true);
			}
			else if(event.getInventory().getName().equalsIgnoreCase(SMITH_INVNAME)){
				if(event.getWhoClicked() instanceof Player && ((Player)event.getWhoClicked()).getWorld().getName().equalsIgnoreCase("world")){
					event.setCancelled(true);
					Player player = (Player) event.getWhoClicked();
					if(event.getRawSlot() >= 0 && event.getRawSlot() < 6*9){
						if(item.getType() == Material.EMERALD_BLOCK){
							smith.buyKey(player);
						}
						else if(item.getType() == Material.REDSTONE_BLOCK){
							player.closeInventory();
							player.playSound(player.getLocation(),Sound.ENTITY_VILLAGER_AMBIENT,1f,1f);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void InventoryCloseEvent(InventoryCloseEvent event){
		if(event.getPlayer() instanceof Player){
			if(event.getInventory().getName().equalsIgnoreCase(SMITH_INVNAME)){
				Player player = (Player) event.getPlayer();
				player.playSound(player.getLocation(),Sound.ENTITY_VILLAGER_AMBIENT,1f,1f);
			}
		}
	}

	@Override
	public void run(){
		for(Entry<Location,LobbyChest> entry: chests.entrySet()){
			entry.getValue().run();
		}
		if(smith != null) smith.updateSkin();
	}

	private void loadChest(){
		Location location = LocationUtil.getConfigLocation(chestsConfig,"chest");
		new LobbyChest(location);
	}

	private void loadSmith(){
		try {
			this.npcRegistry = CitizensAPI.createAnonymousNPCRegistry(SimpleNPCDataStore.create(new YamlStorage(new File(RealCraft.getInstance().getDataFolder()+"/citizens.tmp.yml"))));
			Location location = LocationUtil.getConfigLocation(chestsConfig,"smith");
			smith = new LobbyMysterySmith(location);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void PlayerInteractEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			Block block = event.getClickedBlock();
			if(block != null && block.getType() == Material.CHEST){
				LobbyChest chest = chests.get(block.getLocation());
				if(chest != null){
					chest.onPlayerOpen(event.getPlayer());
					event.setCancelled(true);
				}
			}
			else if(block != null && block.getType() == Material.ENDER_CHEST){
				LobbyChest chest = chests.get(block.getLocation());
				if(chest != null){
					if(event.getPlayer().getInventory().getItemInMainHand() != null && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.TRIPWIRE_HOOK) chest.onPlayerOpen(event.getPlayer());
					else {
						event.getPlayer().playSound(block.getLocation(),Sound.ENTITY_ITEM_BREAK,1f,1f);
						Title.showActionTitle(event.getPlayer(),"§c\u2716 §fNemas v ruce zadny klic §c\u2716",3*20);
					}
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent event){
		LobbyChest chest = chests.get(event.getBlock().getLocation());
		if(chest != null){
			event.setCancelled(true);
		}
	}

	private class LobbyChest {
		Location location;
		boolean active = false;
		Hologram hologram;
		Hologram hologramReward;

		public LobbyChest(Location location){
			this.location = location;
			this.show();
			LightAPI.createLight(location.clone().add(0.0,1.0,0.0),15,false);
			Location loc = this.location.clone();
			loc.setYaw(0);
			chests.put(loc,this);
		}

		public void run(){
			UtilParticles.display(Particles.FIREWORKS_SPARK, 4d, 3d, 4d,location.clone().add(0,3.0,0),10);
			if(hologram == null){
				hologram = HologramsAPI.createHologram(plugin,location.clone().add(0.5,1.5,0.5));
				hologram.insertTextLine(0,"§bMagicka truhla");
			}
			if(hologramReward == null){
				hologramReward = HologramsAPI.createHologram(plugin,location.clone().add(0.5,2.0,0.5));
			}
		}

		public void show(){
			this.active = true;
			this.location.getBlock().setType(Material.ENDER_CHEST);
			BlockState state = this.location.getBlock().getState();
			state.setData(new EnderChest(LocationUtil.yawToFace(this.location.getYaw())));
			state.update();
		}

		public void onPlayerOpen(final Player player){
			if(!active) return;
			this.active = false;
			Particles.FIREWORKS_SPARK.display(0.3f,0.3f,0.3f,0.1f,32,location.clone().add(0.5,0.5,0.5),64);
			changeChestState(location,true);
			removePlayerKey(player);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
				@Override
				public void run(){
					String reward = plugin.lobby.lobbycosmetics.giveRandomReward(player);
					player.sendMessage("§e[Doplnky]§r Objevil jsi "+reward);
					hologramReward.insertTextLine(0,"§b§l"+player.getName()+"§r objevil "+reward);
					FireworkUtil.spawnFirework(location.clone().add(0.5,2.0,0.5),FireworkEffect.Type.BALL,true);
					FireworkUtil.spawnFirework(location.clone().add(0.5,2.0,0.5),FireworkEffect.Type.BALL,true);
				}
			},10);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
				@Override
				public void run(){
					changeChestState(location,false);
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
						@Override
						public void run(){
							active = true;
							hologramReward.clearLines();
						}
					},10);
				}
			},60);
		}
	}

	public void changeChestState(Location location,boolean open){
		net.minecraft.server.v1_12_R1.World world = ((CraftWorld) location.getWorld()).getHandle();
		BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
		if(location.getBlock().getType() == Material.ENDER_CHEST){
			TileEntityEnderChest tileChest = (TileEntityEnderChest) world.getTileEntity(position);
			world.playBlockAction(position, tileChest.getBlock(), 1, open ? 1 : 0);
		} else {
			TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);
			world.playBlockAction(position, tileChest.getBlock(), 1, open ? 1 : 0);
		}
	}

	@EventHandler
	public void NPCRightClickEvent(NPCRightClickEvent event){
		if(event.getNPC().getId() == NPC_ID){
			smith.onPlayerClick(event.getClicker());
		}
	}

	@EventHandler
	public void NPCLeftClickEvent(NPCLeftClickEvent event){
		if(event.getNPC().getId() == NPC_ID){
			smith.onPlayerClick(event.getClicker());
		}
	}

	@EventHandler
	public void NPCSpawnEvent(NPCSpawnEvent event){
		if(event.getNPC().getId() == NPC_ID){
			if(smith != null) smith.updateSkin();
		}
	}

	@EventHandler
	public void ChunkLoadEvent(ChunkLoadEvent event){
		if(smith != null){
			if(smith.isInChunk(event.getChunk())){
				smith.spawn();
			}
		}
	}

	@EventHandler
	public void ChunkUnloadEvent(ChunkUnloadEvent event){
		if(smith != null){
			if(smith.isInChunk(event.getChunk())){
				smith.remove();
			}
		}
	}

	private class LobbyMysterySmith {
		NPC npc;
		Location location;
		SkinnableEntity skinnable = null;

		public LobbyMysterySmith(Location location){
			this.location = location;
			this.npc = npcRegistry.createNPC(EntityType.PLAYER,UUID.fromString("00000000-0000-0000-0000-00000000000"+NPC_ID),NPC_ID,"");
			npc.setName("§f§lKovar");
			npc.setProtected(true);

			LookClose look = npc.getTrait(LookClose.class);
			look.setRange(3);
			look.toggle();

			this.spawn();
			LightAPI.createLight(location.clone().add(0.0,1.0,0.0),15,false);
		}

		public boolean isInChunk(Chunk chunk){
			return (location.getBlockX() >> 4 == chunk.getX() && location.getBlockZ() >> 4 == chunk.getZ());
		}

		public void onPlayerClick(Player player){
			this.openBuyMenu(player);
		}

		public void spawn(){
			npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA,"eyJ0aW1lc3RhbXAiOjE1MDYxNzg1MDU0MTcsInByb2ZpbGVJZCI6ImFkMWM2Yjk1YTA5ODRmNTE4MWJhOTgyMzY0OTllM2JkIiwicHJvZmlsZU5hbWUiOiJGdXJrYW5iejAwIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kNDg5NTA1N2RkYTdiNWUxMzc1OWM5YTM5OTI3YjFkMzE0MTQ2ZTEwNzM0ZWEzMWI3OWVkZGZmN2Q0OWVmODIifX19");
			npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA,"TICH2/vz3kWC1aJ+9BXqIraDLOz8sjBc+utJ3XhiAhqVRikpnRnWJ2VV2eZoILnx4oLMvJELQPLGu02uoH878JMevm/h7a5jTEoDxDmApniYLaW1XTHC2VoR+np4DvssGtDFTM1X9gaNvJP6w9JGIk1Qrto54qo0e5jFRjKFFyNKq92mMkXWSvQJ9rgB+NgLDDW9LHGzpwQAEar2i8k9v1NN6ejzFcUtbrz3qp4a+OCHO4F847XrH3vFQPyNM3ejztO8/R2+zx4KfhqImS4URsWhbG6usRvB5zYfw71RCd0fDVyzp0CiX4+7S4W7CzTHx/X8M5QQFaafEiDyipyrGsNZgYTeRbZ/X3qHWtWAFA5tYkovS9yq6fQpnRwLa1qzfio3+V4Xr/hIpQBF8l942pSlFApfcFf6+GfBkPgOVpY2UMR4iiu8iauWYD6I8EKLxE7gc6o83Dj4pbul6xPTwPBVVPk2j4AbHujosfgCMxclH4/ebUhuWY0zWHwo4u0f6MwYtTf4GtP6Rmktc6XhQFEPbGLSfvBHAk/ZfBqNpNRq1MfP3M4XEdbgUapTFnXSPnMoNT8vKzsMIe50NHRNIUrCX9A1sy5tBCEgW+yNykrCzRAyj5PmmPM7wSnHW4PvXln9gZLKqNpB/RjVinCa03OL7k6CDJ+GVSQfVcQMBAw=");
			npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST,false);
			npc.spawn(location);
		}

		public void remove(){
			npc.despawn(DespawnReason.PLUGIN);
			skinnable = null;
		}

		public void updateSkin(){
			npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA,"eyJ0aW1lc3RhbXAiOjE1MDYxNzg1MDU0MTcsInByb2ZpbGVJZCI6ImFkMWM2Yjk1YTA5ODRmNTE4MWJhOTgyMzY0OTllM2JkIiwicHJvZmlsZU5hbWUiOiJGdXJrYW5iejAwIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kNDg5NTA1N2RkYTdiNWUxMzc1OWM5YTM5OTI3YjFkMzE0MTQ2ZTEwNzM0ZWEzMWI3OWVkZGZmN2Q0OWVmODIifX19");
			npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA,"TICH2/vz3kWC1aJ+9BXqIraDLOz8sjBc+utJ3XhiAhqVRikpnRnWJ2VV2eZoILnx4oLMvJELQPLGu02uoH878JMevm/h7a5jTEoDxDmApniYLaW1XTHC2VoR+np4DvssGtDFTM1X9gaNvJP6w9JGIk1Qrto54qo0e5jFRjKFFyNKq92mMkXWSvQJ9rgB+NgLDDW9LHGzpwQAEar2i8k9v1NN6ejzFcUtbrz3qp4a+OCHO4F847XrH3vFQPyNM3ejztO8/R2+zx4KfhqImS4URsWhbG6usRvB5zYfw71RCd0fDVyzp0CiX4+7S4W7CzTHx/X8M5QQFaafEiDyipyrGsNZgYTeRbZ/X3qHWtWAFA5tYkovS9yq6fQpnRwLa1qzfio3+V4Xr/hIpQBF8l942pSlFApfcFf6+GfBkPgOVpY2UMR4iiu8iauWYD6I8EKLxE7gc6o83Dj4pbul6xPTwPBVVPk2j4AbHujosfgCMxclH4/ebUhuWY0zWHwo4u0f6MwYtTf4GtP6Rmktc6XhQFEPbGLSfvBHAk/ZfBqNpNRq1MfP3M4XEdbgUapTFnXSPnMoNT8vKzsMIe50NHRNIUrCX9A1sy5tBCEgW+yNykrCzRAyj5PmmPM7wSnHW4PvXln9gZLKqNpB/RjVinCa03OL7k6CDJ+GVSQfVcQMBAw=");
			npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST,false);
			SkinnableEntity skinnableTmp = (SkinnableEntity) npc.getEntity();
			if(skinnableTmp != null && skinnable == null){
				skinnable = skinnableTmp;
				skinnable.setSkinName("steve",false);
			}
			else if(skinnableTmp == null) skinnable = null;
			Equipment equip = npc.getTrait(Equipment.class);
			equip.set(Equipment.EquipmentSlot.HAND,new ItemStack(Material.IRON_PICKAXE,1));
		}

		private void openBuyMenu(Player player){
			Inventory inventory = Bukkit.createInventory(null,6*9,SMITH_INVNAME);
			ItemStack item;
			ItemMeta meta;
			ArrayList<String> lore;

			inventory.setItem(13,LobbyMystery.this.getChestKey());

			item = new ItemStack(Material.EMERALD_BLOCK);
			meta = item.getItemMeta();
			meta.setDisplayName("§a§lUkovat");
			lore = new ArrayList<String>();
			lore.add("§7Cena: §a"+SMITH_PRICE+" coins");
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
			player.playSound(player.getLocation(),Sound.ENTITY_VILLAGER_TRADING,1f,1f);
		}

		public void buyKey(Player player){
			if(PlayerManazer.getPlayerInfo(player).getCoins() < SMITH_PRICE){
				player.sendMessage("§cNemas dostatek coinu.");
				player.playSound(player.getLocation(),Sound.ENTITY_VILLAGER_NO,1f,1f);
				return;
			}
			PlayerManazer.getPlayerInfo(player).giveCoins(-SMITH_PRICE);
			LobbyMystery.this.givePlayerKey(player);
			location.getWorld().playSound(location,Sound.BLOCK_ANVIL_USE,1f,1f);
			location.getWorld().playSound(location,Sound.ENTITY_VILLAGER_YES,1f,1f);
			player.closeInventory();
		}
	}
}