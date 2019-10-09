package realcraft.bukkit.test;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.others.AbstractCommand;
import realcraft.share.utils.RandomUtil;

import java.util.Random;

public class FallingTest extends AbstractCommand implements Listener {

	private static final Material[] TYPES = new Material[]{
			Material.STONE,
			Material.DIRT,
			Material.GRASS_BLOCK,
			Material.COBBLESTONE,
			Material.BIRCH_LOG
	};

	public FallingTest() {
		super("falling");
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@Override
	public void perform(Player player,String[] args){
		Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(),new Runnable() {
			@Override
			public void run(){
				Location location = player.getLocation().add(RandomUtil.getRandomInteger(-15,15),32,RandomUtil.getRandomInteger(-15,15));
				location.setX(location.getBlockX()+0.5);
				location.setZ(location.getBlockZ()+0.5);
				Material type = null;
				while(type == null || !type.isBlock()){
					type = FallingTest.this.getRandom(TYPES);
				}

				FallingBlock fallblock = location.getWorld().spawnFallingBlock(location,type,(byte)0);
				fallblock.setDropItem(false);
			}
		},1,1);
	}

	public Material getRandom(Material[] array) {
		int rnd = new Random().nextInt(array.length);
		return array[rnd];
	}
}