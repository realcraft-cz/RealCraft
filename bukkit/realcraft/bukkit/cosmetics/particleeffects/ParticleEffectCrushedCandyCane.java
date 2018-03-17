package realcraft.bukkit.cosmetics.particleeffects;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import realcraft.bukkit.utils.Particles;

public class ParticleEffectCrushedCandyCane extends ParticleEffect {

	HashMap<String,PlayerEffect> playersEffect = new HashMap<String,PlayerEffect>();

	public ParticleEffectCrushedCandyCane(ParticleEffectType type){
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

		private int step;

	    private Random random = new Random();

		public PlayerEffect(Player player){
			this.player = player;
		}

		public void onUpdate(boolean moving){
			if (step > 360)
	            step = 0;
	        Location center = player.getEyeLocation().add(0, 0.6, 0);
	        double inc = (2 * Math.PI) / 20;
	        double angle = step * inc;
	        double x = Math.cos(angle) * 1.1f;
	        double z = Math.sin(angle) * 1.1f;
	        center.add(x, 0, z);
	        for (int i = 0; i < 15; i++)
	            Particles.ITEM_CRACK.display(new Particles.ItemData(Material.INK_SACK, getRandomColor()), 0.2f, 0.2f, 0.2f, 0, 1, center, 128);
	        step++;
		}

		public byte getRandomColor() {
	        float f = random.nextFloat();
	        if (f > 0.98)
	            return (byte) 2;
	        else if (f > 0.49)
	            return (byte) 1;
	        else
	            return (byte) 15;
	    }
	}
}