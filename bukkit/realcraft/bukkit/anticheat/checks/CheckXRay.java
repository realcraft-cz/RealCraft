package realcraft.bukkit.anticheat.checks;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.database.DB;
import realcraft.bukkit.users.Users;
import realcraft.share.users.User;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckXRay extends Check {

	private static final String XRAY_BLOCK_BREAKS = "xray_block_breaks";

	private HashMap<Material,ProtectedBlock> blocks = new HashMap<>();
	private HashMap<BlockLocation,Material> placedBlocks = new HashMap<>();
	private ArrayList<PlayerBlockBreak> breakBlocks = new ArrayList<>();

	public CheckXRay(){
		super(CheckType.XRAY);
		register(Material.COAL_ORE,			8,		132);
		register(Material.IRON_ORE,			50,		68);
		register(Material.LAPIS_ORE,		90,		34);
		register(Material.GOLD_ORE,			90,		34);
		register(Material.DIAMOND_ORE,		100,	16);
		register(Material.REDSTONE_ORE,		50,		16);
		register(Material.EMERALD_ORE,		100,	33);
	}

	private void register(Material type,int value,int maxHeight){
		blocks.put(type,new ProtectedBlock(type,value,maxHeight));
	}

	private ProtectedBlock getProtectedBlock(Material type){
		return blocks.get(type);
	}

	@Override
	public void run(){
		ArrayList<PlayerBlockBreak> blocksTmp = new ArrayList<>(breakBlocks);
		Bukkit.getScheduler().runTaskAsynchronously(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				for(PlayerBlockBreak block : blocksTmp){
					DB.update("INSERT INTO "+XRAY_BLOCK_BREAKS+" (user_id,type,created) VALUES(?,?,?)",
							block.getUser().getId(),
							block.getType().toString(),
							block.getCreated()
					);
				}
			}
		});
		breakBlocks.clear();
	}

	@EventHandler(ignoreCancelled=true,priority=EventPriority.HIGH)
	public void BlockBreakEvent(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.CREATIVE) return;
		if(player.getWorld().getEnvironment() != Environment.NORMAL) return;

		Block block = event.getBlock();
		ProtectedBlock pBlock = this.getProtectedBlock(block.getType());
		if(pBlock == null) return;
		if(block.getLocation().getBlockY() > pBlock.getMaxHeight()) return;
		if(placedBlocks.containsKey(new BlockLocation(block.getLocation()))) return;

		breakBlocks.add(new PlayerBlockBreak(Users.getUser(player),block.getType()));
	}

	@EventHandler(ignoreCancelled=true,priority=EventPriority.HIGH)
	public void BlockPlaceEvent(BlockPlaceEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.CREATIVE) return;
		if(player.getWorld().getEnvironment() != Environment.NORMAL) return;

		Block block = event.getBlock();
		ProtectedBlock pBlock = this.getProtectedBlock(block.getType());
		if(pBlock == null) return;
		if(block.getLocation().getBlockY() > pBlock.getMaxHeight()) return;

		placedBlocks.put(new BlockLocation(block.getLocation()),block.getType());
	}

	private class ProtectedBlock {

		private Material type;
		private int maxHeight;

		public ProtectedBlock(Material type,int value,int maxHeight){
			this.type = type;
			this.maxHeight = maxHeight;
		}

		public Material getType(){
			return type;
		}

		public int getMaxHeight(){
			return maxHeight;
		}
	}

	public static class BlockLocation {

		private Location location;

		public BlockLocation(Location location){
			this.location = location;
		}

		public Location getLocation(){
			return location;
		}

		@Override
		public boolean equals(Object object){
			if(object instanceof BlockLocation){
				BlockLocation toCompare = (BlockLocation) object;
				return (toCompare.getLocation().getBlockX() == this.getLocation().getBlockX() &&
						toCompare.getLocation().getBlockY() == this.getLocation().getBlockY() &&
						toCompare.getLocation().getBlockZ() == this.getLocation().getBlockZ()
				);
			}
			return false;
		}
	}

	public static class PlayerBlockBreak {

		private User user;
		private Material type;
		private int created;

		public PlayerBlockBreak(User user,Material type){
			this.user = user;
			this.type = type;
			this.created = (int)(System.currentTimeMillis()/1000);
		}

		public User getUser(){
			return user;
		}

		public Material getType(){
			return type;
		}

		public int getCreated(){
			return created;
		}
	}
}