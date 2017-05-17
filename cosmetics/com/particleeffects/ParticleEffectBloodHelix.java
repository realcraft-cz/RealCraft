package com.particleeffects;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.realcraft.utils.Particles;
import com.utils.UtilParticles;

public class ParticleEffectBloodHelix extends ParticleEffect {

	HashMap<String,PlayerEffect> playersEffect = new HashMap<String,PlayerEffect>();

	public ParticleEffectBloodHelix(ParticleEffectType type){
		super(type);
	}

	@Override
	public void onUpdate(Player player,boolean moving){
		this.getPlayerEffect(player).onUpdate(moving);
	}

	public PlayerEffect getPlayerEffect(Player player){
		PlayerEffect effect;
		if(!playersEffect.containsKey(player.getName())){
			effect = new PlayerEffect(player);
			playersEffect.put(player.getName(),effect);
		}
		else effect = playersEffect.get(player.getName());
		return effect;
	}

	private class PlayerEffect {
		Player player;

		double i = 0;

		public PlayerEffect(Player player){
			this.player = player;
		}

		public void onUpdate(boolean moving){
			Location location = player.getLocation();
	        Location location2 = location.clone();
	        double radius = 1.1d;
	        double radius2 = 1.1d;
	        double particles = 100;

	        for (int step = 0; step < 100; step += 4) {
	            double inc = (2 * Math.PI) / particles;
	            double angle = step * inc + i;
	            Vector v = new Vector();
	            v.setX(Math.cos(angle) * radius);
	            v.setZ(Math.sin(angle) * radius);
	            UtilParticles.display(Particles.REDSTONE, location.add(v));
	            location.subtract(v);
	            location.add(0, 0.12d, 0);
	            radius -= 0.044f;
	        }
	        for (int step = 0; step < 100; step += 4) {
	            double inc = (2 * Math.PI) / particles;
	            double angle = step * inc + i + 3.5;
	            Vector v = new Vector();
	            v.setX(Math.cos(angle) * radius2);
	            v.setZ(Math.sin(angle) * radius2);
	            UtilParticles.display(Particles.REDSTONE, location2.add(v));
	            location2.subtract(v);
	            location2.add(0, 0.12d, 0);
	            radius2 -= 0.044f;
	        }
	        i += 0.05;
		}
	}
}