package realcraft.bukkit.cosmetics.effects;

import org.bukkit.entity.Player;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.utils.Particles;

public class EffectEnchanted extends Effect {

	public EffectEnchanted(CosmeticType type){
		super(type);
	}

	@Override
	public void update(Player player){
		Particles.ENCHANTMENT_TABLE.display(0.5f,0.5f,0.5f,0f,8,player.getLocation().clone().add(0.0,1.5,0.0),64);
	}
}