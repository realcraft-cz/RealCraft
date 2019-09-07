package realcraft.bukkit.cosmetics.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.utils.Particles;

public class EffectCrushedCandy extends Effect {

	public EffectCrushedCandy(CosmeticType type){
		super(type);
	}

	@Override
	public void update(Player player){
		Location location = player.getLocation();
		location.add(0,0.5,0);
		Particles.ITEM_CRACK.display(new ItemStack(Material.POPPY),0.2f,0.2f,0.2f,0f,4,location,128);
		Particles.ITEM_CRACK.display(new ItemStack(Material.BONE_MEAL),0.2f,0.2f,0.2f,0f,4,location,128);
	}
}