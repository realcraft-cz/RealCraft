package realcraft.bukkit.cosmetics.particleeffects;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import realcraft.bukkit.cosmetics.utils.UtilParticles;

public class ParticleEffectFrostLord extends ParticleEffect {

	HashMap<String,PlayerEffect> playersEffect = new HashMap<String,PlayerEffect>();

	public ParticleEffectFrostLord(ParticleEffectType type){
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

		int step = 0;
	    float stepY = 0;
	    float radius = 1.5f;

		public PlayerEffect(Player player){
			this.player = player;
		}

		public void onUpdate(boolean moving){
			for (int i = 0; i < 6; i++) {
	            Location location = player.getLocation();
	            double inc = (2 * Math.PI) / 100;
	            double angle = step * inc + stepY + i;
	            Vector v = new Vector();
	            v.setX(Math.cos(angle) * radius);
	            v.setZ(Math.sin(angle) * radius);
	            UtilParticles.display(getType().getEffect(), location.add(v).add(0, stepY, 0));
	            location.subtract(v).subtract(0, stepY, 0);
	            if (stepY < 3) {
	                radius -= 0.022;
	                stepY += 0.045;
	            } else {
	                stepY = 0;
	                step = 0;
	                radius = 1.5f;
	                player.playSound(player.getLocation(), Sound.BLOCK_SNOW_BREAK, .5f, 1.5f);
	                UtilParticles.display(getType().getEffect(), location.clone().add(0, 3, 0), 48, 0.3f);
	            }
	        }
		}
	}
}