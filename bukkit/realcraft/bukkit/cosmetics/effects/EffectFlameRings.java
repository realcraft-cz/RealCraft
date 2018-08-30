package realcraft.bukkit.cosmetics.effects;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics.utils.MathUtils;
import realcraft.bukkit.cosmetics.utils.UtilParticles;
import realcraft.bukkit.utils.Particles;

import java.util.HashMap;

public class EffectFlameRings extends Effect {

	HashMap<Player,PlayerEffect> playersEffect = new HashMap<>();

	public EffectFlameRings(CosmeticType type){
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

		float step = 0;

		public PlayerEffect(Player player){
			this.player = player;
		}

		public void update(){
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
				UtilParticles.display(Particles.FLAME, player.getLocation().clone().add(0, 1, 0).add(v));
			}
			step += 3;
		}
	}
}