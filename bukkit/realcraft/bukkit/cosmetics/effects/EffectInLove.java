package realcraft.bukkit.cosmetics.effects;

import org.bukkit.entity.Player;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics2.utils.UtilParticles;
import realcraft.bukkit.utils.Particles;

public class EffectInLove extends Effect {

	public EffectInLove(CosmeticType type){
		super(type);
	}

	@Override
	public void update(Player player){
		UtilParticles.display(Particles.HEART, 0.5f, 0.5f, 0.5f, player.getLocation().add(0, 1, 0), 1);
	}
}