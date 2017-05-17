package com.particleeffects;

import org.bukkit.entity.Player;

import com.realcraft.utils.Particles;
import com.utils.UtilParticles;

public class ParticleEffectSnowCloud extends ParticleEffect {

	public ParticleEffectSnowCloud(ParticleEffectType type){
		super(type);
	}

	@Override
	public void onUpdate(Player player,boolean moving){
		UtilParticles.display(Particles.CLOUD, 0.5F, 0.1f, 0.5f, player.getLocation().add(0, 3, 0), 10);
        UtilParticles.display(getType().getEffect(), 0.25F, 0.05f, 0.25f, player.getLocation().add(0, 3, 0), 1);
	}
}