package realcraft.bukkit.survival;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import realcraft.bukkit.utils.AbstractCommand;
import realcraft.bukkit.utils.LocationUtil;
import realcraft.bukkit.utils.RandomUtil;

import java.util.HashMap;
import java.util.List;

public class RandomSpawn extends AbstractCommand {

	private static final int RANDOM_LIMIT = 60*1000;
	private static final int RANDOM_SIZE = 5000;
	private HashMap<Player,Long> lastRandomSpawn = new HashMap<Player,Long>();

	public RandomSpawn(){
		super("priroda","nature","warp rs");
	}

	@Override
	public void perform(Player player,String[] args){
		if(lastRandomSpawn.containsKey(player) && lastRandomSpawn.get(player)+RANDOM_LIMIT >= System.currentTimeMillis()){
			player.sendMessage("§cNahodny spawn do prirody muzete znovu pouzit za "+(((lastRandomSpawn.get(player)+RANDOM_LIMIT)-System.currentTimeMillis())/1000)+" sekund.");
			return;
		}
		lastRandomSpawn.put(player,System.currentTimeMillis());
		player.teleport(this.getRandomLocation(Bukkit.getWorld("world")),PlayerTeleportEvent.TeleportCause.PLUGIN);
	}

	@Override
	public List<String> onTabComplete(Player player,String[] args){
		return null;
	}

	public Location getRandomLocation(World world){
		Location location = LocationUtil.getSafeDestination(new Location(world,RandomUtil.getRandomInteger(-RANDOM_SIZE,RANDOM_SIZE),world.getMaxHeight(),RandomUtil.getRandomInteger(-RANDOM_SIZE,RANDOM_SIZE)));
		if(this.isLocationInOcean(location)) location = this.getRandomLocation(world);
		return location;
	}

	public boolean isLocationInOcean(Location location){
		return (
			location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()) == Biome.OCEAN || location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()) == Biome.DEEP_OCEAN ||
			location.getWorld().getBiome(location.getBlockX()+1,location.getBlockZ()) == Biome.OCEAN || location.getWorld().getBiome(location.getBlockX()+1,location.getBlockZ()) == Biome.DEEP_OCEAN ||
			location.getWorld().getBiome(location.getBlockX()-1,location.getBlockZ()) == Biome.OCEAN || location.getWorld().getBiome(location.getBlockX()-1,location.getBlockZ()) == Biome.DEEP_OCEAN ||
			location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()+1) == Biome.OCEAN || location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()+1) == Biome.DEEP_OCEAN ||
			location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()-1) == Biome.OCEAN || location.getWorld().getBiome(location.getBlockX(),location.getBlockZ()-1) == Biome.DEEP_OCEAN
		);
	}
}