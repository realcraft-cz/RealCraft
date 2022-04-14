package realcraft.bukkit.lobby;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.Cosmetics;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticCategory;
import realcraft.bukkit.utils.LocationUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class LobbySpleef implements Listener, Runnable {

	private static final int BLOCK_TIMEOUT = 5000;
	private HashMap<Player,LobbySpleefPlayer> players = new HashMap<>();
	private HashMap<LocationUtil.BlockLocation,LobbySpleefBlock> blocks = new HashMap<>();
	private Location location;
	private int radius;
	private ItemStack item;

	private FileConfiguration config;

	public LobbySpleef(){
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(RealCraft.getInstance(),this,5,5);
		this.location = LocationUtil.getConfigLocation(this.getConfig(),"location");
		this.radius = this.getConfig().getInt("radius");
		this.init();
	}

	@Override
	public void run(){
		ArrayList<LobbySpleefBlock> blocks = this.getBlocks();
		Collections.sort(blocks,new Comparator<LobbySpleefBlock>(){
			@Override
			public int compare(LobbySpleefBlock block1,LobbySpleefBlock block2){
				int compare = Long.compare(block1.getChanged(),block2.getChanged());
				if(compare > 0) return 1;
				else if(compare < 0) return -1;
				return 0;
			}
		});
		for(LobbySpleefBlock block : blocks){
			if(block.getBlock().getType() == Material.AIR && block.getChanged()+BLOCK_TIMEOUT < System.currentTimeMillis()){
				block.getBlock().setType(Material.SNOW_BLOCK);
				break;
			}
		}
	}

	private ItemStack getShovel(){
		if(item == null){
			item = new ItemStack(Material.DIAMOND_SHOVEL);
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			item.setItemMeta(meta);
		}
		return item;
	}

	private void init(){
		Location pos = location.clone();
		double radiusX = this.getRadius()+0.5;
		double radiusZ = this.getRadius()+0.5;
		final double invRadiusX = 1/radiusX;
		final double invRadiusZ = 1/radiusZ;
		final int ceilRadiusX = (int) Math.ceil(radiusX);
		final int ceilRadiusZ = (int) Math.ceil(radiusZ);
		double nextXn = 0;
		forX: for (int x = 0; x <= ceilRadiusX; ++x) {
			final double xn = nextXn;
			nextXn = (x + 1) * invRadiusX;
			double nextZn = 0;
			forZ: for (int z = 0; z <= ceilRadiusZ; ++z) {
				final double zn = nextZn;
				nextZn = (z + 1) * invRadiusZ;
				double distanceSq = lengthSq(xn, zn);
				if (distanceSq > 1) {
					if (z == 0) {
						break forX;
					}
					break forZ;
				}
				this.addBlock(pos.clone().add(x, 0, z));
				this.addBlock(pos.clone().add(-x, 0, z));
				this.addBlock(pos.clone().add(x, 0, -z));
				this.addBlock(pos.clone().add(-x, 0, -z));
			}
		}
	}

	private void addBlock(Location location){
		Block block = location.getBlock();
		block.setType(Material.SNOW_BLOCK);
		LocationUtil.BlockLocation sLocation = new LocationUtil.BlockLocation(block.getLocation());
		blocks.put(sLocation,new LobbySpleefBlock(block));
	}

	private double lengthSq(double x,double z){
		return (x * x) + (z * z);
	}

	private FileConfiguration getConfig(){
		if(config == null){
			File file = new File(RealCraft.getInstance().getDataFolder() + "/spleef.yml");
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

	public Location getLocation(){
		return location;
	}

	public int getRadius(){
		return radius;
	}

	public LobbySpleefPlayer getLobbySpleefPlayer(Player player){
		if(!players.containsKey(player)) players.put(player,new LobbySpleefPlayer());
		return players.get(player);
	}

	public ArrayList<LobbySpleefBlock> getBlocks(){
		return new ArrayList<>(blocks.values());
	}

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.ADVENTURE && !this.getLobbySpleefPlayer(player).isInGame() && this.isLocationInside(player.getLocation())){
			this.joinPlayer(player);
		}
		else if(this.getLobbySpleefPlayer(player).isInGame() && !this.isLocationInside(player.getLocation())){
			this.leavePlayer(player);
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void BlockBreakEvent(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(!this.getLobbySpleefPlayer(player).isInGame()) return;
		if(player.getInventory().getItemInMainHand().getType() != Material.DIAMOND_SHOVEL) return;
		if(event.getBlock().getType() != Material.SNOW_BLOCK) return;
		Block block = event.getBlock();
		LocationUtil.BlockLocation sLocation = new LocationUtil.BlockLocation(block.getLocation());
		if(blocks.containsKey(sLocation)){
			event.setCancelled(false);
			event.setDropItems(false);
			blocks.put(sLocation,new LobbySpleefBlock(block));
		}
	}

	private void joinPlayer(Player player){
		this.getLobbySpleefPlayer(player).setInGame(true);
		player.setGameMode(GameMode.SURVIVAL);
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setGliding(false);
		player.getInventory().setItem(2,this.getShovel());
		player.getInventory().setHeldItemSlot(2);
		Cosmetics.disableCosmetics(player,CosmeticCategory.GADGET);
		Cosmetics.disableCosmetics(player,CosmeticCategory.PET);
		//Cosmetics.disableCosmetics(player,CosmeticCategory.MOUNT);
	}

	private void leavePlayer(Player player){
		player.setGameMode(GameMode.ADVENTURE);
		this.getLobbySpleefPlayer(player).setInGame(false);
		player.getInventory().remove(Material.DIAMOND_SHOVEL);
	}

	private boolean isLocationInside(Location location){
		Location center = this.getLocation().clone();
		center.setY(location.getY());
		return (location.getBlockY() < this.getLocation().getBlockY()+3 && location.getBlockY() > this.getLocation().getBlockY()-10 && center.distance(location) <= this.getRadius()+2);
	}

	private class LobbySpleefPlayer {

		private boolean inGame = false;

		public LobbySpleefPlayer(){
		}

		public boolean isInGame(){
			return inGame;
		}

		public void setInGame(boolean inGame){
			this.inGame = inGame;
		}
	}

	private class LobbySpleefBlock {

		private Block block;
		private long changed;

		public LobbySpleefBlock(Block block){
			this.block = block;
			this.changed = System.currentTimeMillis();
		}

		public Block getBlock(){
			return block;
		}

		public long getChanged(){
			return changed;
		}
	}
}