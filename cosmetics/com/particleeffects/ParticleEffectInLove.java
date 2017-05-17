package com.particleeffects;

import org.bukkit.entity.Player;

import com.utils.UtilParticles;

public class ParticleEffectInLove extends ParticleEffect {

	public ParticleEffectInLove(ParticleEffectType type){
		super(type);
	}

	@Override
	public void onUpdate(Player player,boolean moving){
		UtilParticles.display(getType().getEffect(), 0.5f, 0.5f, 0.5f, player.getLocation().add(0, 1, 0), 2);
	}
}