package realcraft.bukkit.cosmetics.effects;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics2.utils.UtilParticles;
import realcraft.bukkit.utils.Particles;

import java.util.HashMap;

public class EffectGreenSparks extends Effect {

	HashMap<Player,PlayerEffect> playersEffect = new HashMap<>();

	public EffectGreenSparks(CosmeticType type){
		super(type);
	}

	@Override
	public void update(Player player){
		this.getPlayerEffect(player).update();
	}

	public PlayerEffect getPlayerEffect(Player player){
		if(!playersEffect.containsKey(player)) playersEffect.put(player,new PlayerEffect(player));
		return playersEffect.get(player);
	}

	private class PlayerEffect {
		Player player;

		boolean up;
		float height;
		int step;

		public PlayerEffect(Player player){
			this.player = player;
		}

		public void update(){
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
			UtilParticles.display(Particles.VILLAGER_HAPPY, player.getLocation().clone().add(v).add(0, height, 0));
			step += 4;
		}
	}
}