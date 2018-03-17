package realcraft.bukkit.cosmetics.particleeffects;

import org.bukkit.entity.Player;

import realcraft.bukkit.cosmetics.utils.MathUtils;
import realcraft.bukkit.cosmetics.utils.UtilParticles;
import realcraft.bukkit.utils.Particles;

public class ParticleEffectEnchanted extends ParticleEffect {

	public ParticleEffectEnchanted(ParticleEffectType type){
		super(type);
	}

	@Override
	public void onUpdate(Player player,boolean moving){
		UtilParticles.display(Particles.ENCHANTMENT_TABLE, player.getLocation().add(0, MathUtils.randomDouble(0.1, 2), 0), 60, 8f);
	}
}