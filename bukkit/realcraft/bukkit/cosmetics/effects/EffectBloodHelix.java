package realcraft.bukkit.cosmetics.effects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics.utils.UtilParticles;

import java.util.HashMap;

public class EffectBloodHelix extends Effect {

	HashMap<Player,PlayerEffect> playersEffect = new HashMap<>();

	public EffectBloodHelix(CosmeticType type){
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

		double i = 0;

		public PlayerEffect(Player player){
			this.player = player;
		}

		public void update(){
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
				UtilParticles.display(255,0,0, location.add(v));
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
				UtilParticles.display(255,0,0, location2.add(v));
				location2.subtract(v);
				location2.add(0, 0.12d, 0);
				radius2 -= 0.044f;
			}
			i += 0.05;
		}
	}
}