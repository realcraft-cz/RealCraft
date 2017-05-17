package com.particleeffects;

import org.bukkit.entity.Player;

import com.realcraft.utils.Particles;
import com.utils.MathUtils;
import com.utils.UtilParticles;

public class ParticleEffectEnchanted extends ParticleEffect {

	public ParticleEffectEnchanted(ParticleEffectType type){
		super(type);
	}

	@Override
	public void onUpdate(Player player,boolean moving){
		UtilParticles.display(Particles.ENCHANTMENT_TABLE, player.getLocation().add(0, MathUtils.randomDouble(0.1, 2), 0), 60, 8f);
	}
}