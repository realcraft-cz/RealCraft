package realcraft.bukkit.cosmetics.particleeffects;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import realcraft.bukkit.cosmetics.utils.UtilParticles;

public class ParticleEffectGreenSparks extends ParticleEffect {

	HashMap<String,PlayerEffect> playersEffect = new HashMap<String,PlayerEffect>();

	public ParticleEffectGreenSparks(ParticleEffectType type){
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

		boolean up;
	    float height;
	    int step;

		public PlayerEffect(Player player){
			this.player = player;
		}

		public void onUpdate(boolean moving){
			if (up) {
	            if (height < 2)
	                height += 0.05;
	            else
	                up = false;
	        } else {
	            if (height > 0)
	                height -= 0.05;
	            else
	                up = true;
	        }
	        double inc = (2 * Math.PI) / 100;
	        double angle = step * inc;
	        Vector v = new Vector();
	        v.setX(Math.cos(angle) * 1.1);
	        v.setZ(Math.sin(angle) * 1.1);
	        UtilParticles.display(getType().getEffect(), player.getLocation().clone().add(v).add(0, height, 0));
	        step += 4;
		}
	}
}