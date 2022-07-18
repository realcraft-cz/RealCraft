package realcraft.bukkit.test;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import realcraft.bukkit.others.AbstractCommand;

public class FallingTest extends AbstractCommand implements Listener {

	public FallingTest() {
		super("falling");
	}

	@Override
	public void perform(Player player,String[] args){
		Material[] blocks = new Material[]{
			Material.MANGROVE_LEAVES,
			Material.MANGROVE_ROOTS,
			Material.AZALEA_LEAVES,
			Material.FLOWERING_AZALEA_LEAVES,

			Material.MUD,
			Material.MANGROVE_LOG,

			Material.AMETHYST_BLOCK,
			Material.CALCITE,
			Material.COPPER_ORE,
			Material.DRIPSTONE_BLOCK,
			Material.POINTED_DRIPSTONE,
			Material.POWDER_SNOW,
			Material.TUFF,
			Material.BEE_NEST,
		};
		Location location = player.getLocation();
		location.add(2, 2, 2);

		for (Material block : blocks) {
			try {
				FallingBlock fallblock = location.getWorld().spawnFallingBlock(location, Bukkit.createBlockData(block));
				fallblock.setDropItem(false);
			} catch (Exception e) {
				player.sendMessage("§cError: §f" + block);
			}
		}
	}
}
