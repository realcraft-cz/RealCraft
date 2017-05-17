package com.realcraft.lobby;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Chest;
import org.bukkit.material.EnderChest;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.realcraft.RealCraft;
import com.realcraft.auth.AuthLoginEvent;
import com.realcraft.utils.FireworkUtil;
import com.realcraft.utils.LocationUtil;
import com.realcraft.utils.Particles;
import com.realcraft.utils.Title;
import com.utils.UtilParticles;

import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.TileEntityChest;
import net.minecraft.server.v1_11_R1.TileEntityEnderChest;

public class LobbyChests implements Listener, Runnable {
	RealCraft plugin;

	private File chestsFile;
	private FileConfiguration chestsConfig;

	HashMap<Location,LobbyChest> chests = new HashMap<Location,LobbyChest>();

	boolean debug = false;

	public LobbyChests(RealCraft realcraft){
		plugin = realcraft;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,this,20,20);
		debug = plugin.config.getBoolean("lobby.chests.debug");
		this.registerGlow();
		this.loadChests();
		if(!debug && plugin.serverName.equalsIgnoreCase("lobby")) this.firstGenerateChests();
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
		if(plugin.playermanazer.getPlayerInfo(player).isLogged()) this.loadPlayerKeys(player);
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

	public int getPlayerKeys(Player player){
		return plugin.playermanazer.getPlayerInfo(player).getLobbyKeys();
	}

	public int getPlayerKeyFragments(Player player){
		return plugin.playermanazer.getPlayerInfo(player).getLobbyFragments();
	}

	public void givePlayerKeys(Player player){
		plugin.playermanazer.getPlayerInfo(player).givePlayerKeys(1);
		this.loadPlayerKeys(player);
		player.getInventory().setHeldItemSlot(8);
	}

	public int givePlayerFragments(Player player){
		return this.givePlayerFragments(player,0);
	}

	public int givePlayerFragments(Player player,int fragments){
		if(fragments == 0) fragments = this.getRandomNumber(1,3);
		if(plugin.playermanazer.getPlayerInfo(player).getLobbyFragments() == 9 || plugin.playermanazer.getPlayerInfo(player).getLobbyFragments() == 8) fragments = 1;
		else if(plugin.playermanazer.getPlayerInfo(player).getLobbyFragments() == 7) fragments = this.getRandomNumber(1,2);
		plugin.playermanazer.getPlayerInfo(player).givePlayerFragments(fragments);
		return fragments;
	}

	public void removePlayerKey(Player player){
		plugin.playermanazer.getPlayerInfo(player).removePlayerKeys(1);
		removeItems(player.getInventory(),Material.TRIPWIRE_HOOK,1);
	}

	public void resetPlayerFragments(Player player){
		plugin.playermanazer.getPlayerInfo(player).resetPlayerFragments();
	}

	public void removeItems(Inventory inventory,Material type,int amount){
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }

	public void loadPlayerKeys(Player player){
		if(player.getWorld().getName().equalsIgnoreCase("world")){
			int keys = this.getPlayerKeys(player);
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
			plugin.playermanazer.getPlayerInfo(event.getPlayer()).removePlayerKeys(event.getItemDrop().getItemStack().getAmount());
		}
	}

	@EventHandler
    public void PlayerPickupItemEvent(PlayerPickupItemEvent event){
		if(event.getItem().getItemStack().getType() == Material.TRIPWIRE_HOOK && event.getItem().getItemStack().hasItemMeta() && event.getItem().getItemStack().getItemMeta().hasDisplayName() && event.getItem().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§b§l"+"Klic k truhle")){
			plugin.playermanazer.getPlayerInfo(event.getPlayer()).givePlayerKeys(event.getItem().getItemStack().getAmount());
		}
	}

	public void keysEffects(){
		World world = Bukkit.getServer().getWorld("world");
		List<Entity> entities = world.getEntities();
		for(Entity entity : entities){
			if(entity instanceof Item && ((Item) entity).getItemStack().getType() == Material.TRIPWIRE_HOOK){
				Particles.SPELL_WITCH.display(0.4f,0.4f,0.4f,0f,4,entity.getLocation().clone().add(0.0,0.2,0.0),64);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		if(event.isCancelled()) return;
		Player player = event.getPlayer();
		String [] args = event.getMessage().substring(1).split(" ");
		if(args[0].equalsIgnoreCase("savekeys") && player.hasPermission("group.Manazer")){
			int total = 0;
			int beacons = 0;
			ArrayList<HashMap<String, Object>> keysList = new ArrayList<HashMap<String, Object>>();
			FileConfiguration testConfig = new YamlConfiguration();

			Location location = player.getLocation();
			int minX = (int)Math.round(location.getX())-200;
			int minY = 40;
			int minZ = (int)Math.round(location.getZ())-200;
			for(int x = minX; x < minX + 400; x++){
				for(int y = minY; y < minY + 60; y++){
					for(int z = minZ; z < minZ + 400; z++){
						Block block = location.getWorld().getBlockAt(x,y,z);
						if(block.getType() == Material.BEACON){
							beacons ++;
							if(location.getWorld().getBlockAt(x,y-1,z).getType() == Material.CHEST){
								total ++;
								block = location.getWorld().getBlockAt(x,y-1,z);
								Chest chest = (Chest) block.getState().getData();
								Location loc = block.getLocation().clone();
								loc.setYaw(LocationUtil.faceToYaw(chest.getFacing()));
								keysList.add(this.locationToConfig(loc));
								FireworkUtil.spawnFirework(block.getLocation().clone().add(0.5,2.0,0.5),FireworkEffect.Type.BALL,true);
							}
						}
					}
				}
			}
			testConfig.set("keys",keysList);
			try {
				testConfig.save(new File("keysLocations.yml"));
			} catch (IOException e){
				e.printStackTrace();
			}
			player.sendMessage("ChestKeys saved. Total: "+total+" (beacons: "+beacons+")");
			event.setCancelled(true);
		}
	}

	public HashMap<String,Object> locationToConfig(Location location){
		HashMap<String, Object> section = new HashMap<String, Object>();
		section.put("x",(double)Math.round(location.getX()*10)/10);
		section.put("y",(double)Math.round(location.getY()*10)/10);
		section.put("z",(double)Math.round(location.getZ()*10)/10);
		section.put("pitch", 0.0);
		section.put("yaw", (double)Math.round(location.getYaw()*10)/10);
		section.put("world", location.getWorld().getName());
		return section;
	}

	@Override
	public void run(){
		for(Entry<Location,LobbyChest> entry: chests.entrySet()){
			entry.getValue().run();
		}
		this.keysEffects();
	}

	@SuppressWarnings("unchecked")
	private void loadChests(){
		chestsFile = new File(RealCraft.getInstance().getDataFolder()+"/chests/"+plugin.serverName+".yml");
		if(chestsFile.exists()){
			chestsConfig = new YamlConfiguration();
			try {
				chestsConfig.load(chestsFile);
			} catch (Exception e){
				e.printStackTrace();
			}
			List<Map<String, Object>> tempMystery = (List<Map<String, Object>>) chestsConfig.get("mystery");
			if(tempMystery != null && !tempMystery.isEmpty()){
				for(Map<String, Object> spawn : tempMystery){
					double x = Math.round(Double.valueOf(spawn.get("x").toString()));
					double y = Math.round(Double.valueOf(spawn.get("y").toString()));
					double z = Math.round(Double.valueOf(spawn.get("z").toString()));
					float yaw = Float.valueOf(spawn.get("yaw").toString());
					float pitch = Float.valueOf(spawn.get("pitch").toString());
					World world = Bukkit.getServer().getWorld(spawn.get("world").toString());
					new LobbyChest(LobbyChestType.MYSTERY,new Location(world,x,y,z,yaw,pitch));
				}
			}
			List<Map<String, Object>> tempKeys = (List<Map<String, Object>>) chestsConfig.get("keys");
			if(tempKeys != null && !tempKeys.isEmpty()){
				for(Map<String, Object> spawn : tempKeys){
					double x = Math.round(Double.valueOf(spawn.get("x").toString()));
					double y = Math.round(Double.valueOf(spawn.get("y").toString()));
					double z = Math.round(Double.valueOf(spawn.get("z").toString()));
					float yaw = Float.valueOf(spawn.get("yaw").toString());
					float pitch = Float.valueOf(spawn.get("pitch").toString());
					World world = Bukkit.getServer().getWorld(spawn.get("world").toString());
					new LobbyChest(LobbyChestType.KEY,new Location(world,x,y,z,yaw,pitch));
				}
			}
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
		LobbyChestType type;
		boolean active = false;
		Hologram hologram;
		Hologram hologramReward;

		public LobbyChest(LobbyChestType type,Location location){
			this.type = type;
			this.location = location;
			if(debug) this.show();
			else this.hide();
			if(this.type == LobbyChestType.MYSTERY) this.show();
			Location loc = this.location.clone();
			loc.setYaw(0);
			chests.put(loc,this);
		}

		public void run(){
			if(type == LobbyChestType.KEY && this.isActive() && location.getBlock().getType() == Material.CHEST){
				Particles.FIREWORKS_SPARK.display(0.3f,0.5f,0.3f,0.05f,16,location.clone().add(0.5,0.5,0.5),64);
			}
			else if(type == LobbyChestType.MYSTERY){
				UtilParticles.display(Particles.FIREWORKS_SPARK, 4d, 3d, 4d,location.clone().add(0,3.0,0),10);
				if(hologram == null){
					hologram = HologramsAPI.createHologram(plugin,location.clone().add(0.5,1.5,0.5));
					hologram.insertTextLine(0,"§bMagicka truhla");
				}
				if(hologramReward == null){
					hologramReward = HologramsAPI.createHologram(plugin,location.clone().add(0.5,2.0,0.5));
				}
			}
		}

		public boolean isActive(){
			return this.active;
		}

		public LobbyChestType getType(){
			return this.type;
		}

		public void show(){
			this.active = true;
			if(this.type == LobbyChestType.MYSTERY){
				this.location.getBlock().setType(Material.ENDER_CHEST);
				BlockState state = this.location.getBlock().getState();
				state.setData(new EnderChest(LocationUtil.yawToFace(this.location.getYaw())));
				state.update();
			} else {
				this.location.getBlock().setType(Material.CHEST);
				BlockState state = this.location.getBlock().getState();
				state.setData(new Chest(LocationUtil.yawToFace(this.location.getYaw())));
				state.update();
			}
		}

		public void hide(){
			this.active = false;
			this.location.getBlock().setType(Material.AIR);
		}

		public void onPlayerOpen(final Player player){
			if(!active) return;
			if(type == LobbyChestType.KEY){
				this.active = false;
				final int fragmentsFound = givePlayerFragments(player);
				final int fragments = getPlayerKeyFragments(player);
				changeChestState(location,true);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
					@Override
					public void run(){
						hide();
						Particles.SMOKE_NORMAL.display(0.4f,0.6f,0.4f,0f,32,location.clone().add(0.5,0.5,0.5),32);
						Particles.LAVA.display(0.2f,0.4f,0.2f,0f,8,location.clone().add(0.5,0.5,0.5),32);
						Title.showTitle(player," ",0.2,5,0.2);
						if(fragments >= 10){
							givePlayerKeys(player);
							resetPlayerFragments(player);
							player.playSound(location,Sound.ENTITY_PLAYER_LEVELUP,1f,1f);
							String keyText = "klicu";
							if(getPlayerKeys(player) == 1) keyText = "klic";
							else if(getPlayerKeys(player) < 5) keyText = "klice";
							Title.showSubTitle(player,"§fZiskal jsi §a1 klic§f k magicke truhle.",0,5.2,0.6);
							Title.showActionTitle(player,"§fCelkem mas §e"+getPlayerKeys(player)+" "+keyText+"§f k magicke truhle.",5*20);
						} else {
							String fragmentText = "ulomku";
							if(fragmentsFound == 1) fragmentText = "ulomek";
							else if(fragmentsFound < 5) fragmentText = "ulomky";
							player.playSound(location,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
							Title.showSubTitle(player,"§fNasel jsi §e"+fragmentsFound+" "+fragmentText+"§f klice.",0,5.2,0.6);
							Title.showActionTitle(player,"§fCelkem mas §e"+fragments+"/10§f ulomku klice.",5*20);
						}
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable(){
							@Override
							public void run(){
								generateRandomChest();
							}
						},30*20);
					}
				},12);
			}
			else if(type == LobbyChestType.MYSTERY){
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
	}

	public void changeChestState(Location location,boolean open){
		net.minecraft.server.v1_11_R1.World world = ((CraftWorld) location.getWorld()).getHandle();
		BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
		if(location.getBlock().getType() == Material.ENDER_CHEST){
			TileEntityEnderChest tileChest = (TileEntityEnderChest) world.getTileEntity(position);
			world.playBlockAction(position, tileChest.getBlock(), 1, open ? 1 : 0);
		} else {
			TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);
			world.playBlockAction(position, tileChest.getBlock(), 1, open ? 1 : 0);
		}
	}

	public void firstGenerateChests(){
		for(int i=0;i<10;i++) this.generateRandomChest();
	}

	public void generateRandomChest(){
		LobbyChest chest = this.getRandomChest();
		chest.show();
	}

	public LobbyChest getRandomChest(){
		LobbyChest random;
		Random generator = new Random();
		Object [] values = chests.values().toArray();
		random = (LobbyChest) values[generator.nextInt(values.length)];
		if(random.isActive() || random.getType() == LobbyChestType.MYSTERY) random = this.getRandomChest();
		return random;
	}

	private enum LobbyChestType {
		MYSTERY,KEY
	}

	private class Glow extends Enchantment {
		public Glow(int id){
			super(id);
		}

		@Override
		public boolean canEnchantItem(ItemStack arg0){
			return false;
		}

		@Override
		public boolean conflictsWith(Enchantment arg0){
			return false;
		}

		@Override
		public EnchantmentTarget getItemTarget(){
			return null;
		}

		@Override
		public int getMaxLevel(){
			return 0;
		}

		@Override
		public String getName(){
			return null;
		}

		@Override
		public int getStartLevel(){
			return 0;
		}

		@Override
		public boolean isCursed() {
			return false;
		}

		@Override
		public boolean isTreasure() {
			return false;
		}
	}

	public void registerGlow() {
		try {
		    Field f = Enchantment.class.getDeclaredField("acceptingNew");
		    f.setAccessible(true);
		    f.set(null, true);
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
		try {
		    Glow glow = new Glow(255);
		    Enchantment.registerEnchantment(glow);
		}
		catch (IllegalArgumentException e){
		}
		catch(Exception e){
		    e.printStackTrace();
		}
	}
}