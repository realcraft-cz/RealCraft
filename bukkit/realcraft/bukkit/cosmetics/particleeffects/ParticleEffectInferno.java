package realcraft.bukkit.cosmetics.particleeffects;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import realcraft.bukkit.cosmetics.utils.UtilParticles;

public class ParticleEffectInferno extends ParticleEffect {

	HashMap<String,PlayerEffect> playersEffect = new HashMap<String,PlayerEffect>();

	public ParticleEffectInferno(ParticleEffectType type){
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

		float[] height = {0, 0, 2, 2};
	    boolean[] up = {true, false, true, false};
	    int[] steps = {0, 0, 0, 0};

		public PlayerEffect(Player player){
			this.player = player;
		}

		public void onUpdate(boolean moving){
			for (int i = 0; i < 4; i++) {
	            if (up[i]) {
	                if (height[i] < 2)
	                    height[i] += 0.05;
	                else
	                    up[i] = false;
	            } else {
	                if (height[i] > 0)
	                    height[i] -= 0.05;
	                else
	                    up[i] = true;
	            }
	            double inc = (2 * Math.PI) / 100;
	            double angle = steps[i] * inc + ((i + 1) % 2 == 0 ? 45 : 0);
	            Vector v = new Vector();
	            v.setX(Math.cos(angle) * 1.1);
	            v.setZ(Math.sin(angle) * 1.1);
	            try {
	                UtilParticles.display(getType().getEffect(), 0.15f, 0.15f, 0.15f, player.getLocation().clone().add(v).add(0, height[i], 0), 4);
	            } catch (Exception exc) {

	            }
	            if (i == 0 || i == 3)
	                steps[i] -= 4;
	            else
	                steps[i] += 4;
	        }
		}
	}
}