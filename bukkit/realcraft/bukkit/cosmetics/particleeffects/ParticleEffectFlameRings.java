package realcraft.bukkit.cosmetics.particleeffects;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import realcraft.bukkit.cosmetics.utils.MathUtils;
import realcraft.bukkit.cosmetics.utils.UtilParticles;

public class ParticleEffectFlameRings extends ParticleEffect {

	HashMap<String,PlayerEffect> playersEffect = new HashMap<String,PlayerEffect>();

	public ParticleEffectFlameRings(ParticleEffectType type){
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

		float step = 0;

		public PlayerEffect(Player player){
			this.player = player;
		}

		public void onUpdate(boolean moving){
			for (int i = 0; i < 2; i++) {
	            double inc = (2 * Math.PI) / 100;
	            double toAdd = 0;
	            if (i == 1)
	                toAdd = 3.5;
	            double angle = step * inc + toAdd;
	            Vector v = new Vector();
	            v.setX(Math.cos(angle));
	            v.setZ(Math.sin(angle));
	            if (i == 0) {
	                MathUtils.rotateAroundAxisZ(v, 180);
	            } else {
	                MathUtils.rotateAroundAxisZ(v, 90);
	            }
	            UtilParticles.display(getType().getEffect(), player.getLocation().clone().add(0, 1, 0).add(v));
	        }
	        step += 3;
		}
	}
}