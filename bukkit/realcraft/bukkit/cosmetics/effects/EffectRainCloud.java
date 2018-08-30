package realcraft.bukkit.cosmetics.effects;

import org.bukkit.entity.Player;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics.utils.UtilParticles;
import realcraft.bukkit.utils.Particles;

public class EffectRainCloud extends Effect {

	public EffectRainCloud(CosmeticType type){
		super(type);
	}

	@Override
	public void update(Player player){
		UtilParticles.display(Particles.CLOUD, 0.5F, 0.1f, 0.5f, player.getLocation().add(0, 3, 0), 10);
		UtilParticles.display(Particles.DRIP_WATER, 0.25F, 0.05f, 0.25f, player.getLocation().add(0, 3, 0), 1);
	}
}