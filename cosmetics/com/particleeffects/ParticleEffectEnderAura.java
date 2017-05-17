package com.particleeffects;

import org.bukkit.Effect;
import org.bukkit.entity.Player;

public class ParticleEffectEnderAura extends ParticleEffect {

	public ParticleEffectEnderAura(ParticleEffectType type){
		super(type);
	}

	@Override
	public void onUpdate(Player player,boolean moving){
		player.getWorld().playEffect(player.getLocation().add(0, 1, 0), Effect.ENDER_SIGNAL, 0);
	}
}