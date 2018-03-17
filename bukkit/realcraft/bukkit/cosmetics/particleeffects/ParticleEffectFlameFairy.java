package realcraft.bukkit.cosmetics.particleeffects;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import realcraft.bukkit.cosmetics.utils.MathUtils;
import realcraft.bukkit.cosmetics.utils.UtilParticles;
import realcraft.bukkit.utils.Particles;

public class ParticleEffectFlameFairy extends ParticleEffect {

	HashMap<String,PlayerEffect> playersEffect = new HashMap<String,PlayerEffect>();

	public ParticleEffectFlameFairy(ParticleEffectType type){
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

		private Vector targetDirection = new Vector(1, 0, 0);

	    private Location currentLocation, targetLocation;

	    public double noMoveTime = 0, movementSpeed = 0.2d;

		public PlayerEffect(Player player){
			this.player = player;
			this.currentLocation = player.getLocation();
	        this.targetLocation = generateNewTarget();
		}

		public void onUpdate(boolean moving){
			if (player.getWorld() != currentLocation.getWorld()
	                || player.getWorld() != targetLocation.getWorld()) {
	            currentLocation = player.getLocation();
	            targetLocation = generateNewTarget();
	        }

	        double distanceBtw = player.getEyeLocation().distance(currentLocation);
	        double distTarget = currentLocation.distance(targetLocation);

	        if (distTarget < 1d || distanceBtw > 3)
	            targetLocation = generateNewTarget();

	        distTarget = currentLocation.distance(targetLocation);

	        if (MathUtils.random.nextDouble() > 0.98)
	            noMoveTime = System.currentTimeMillis() + MathUtils.randomDouble(0, 2000);

	        if (player.getEyeLocation().distance(currentLocation) < 3)
	            movementSpeed = noMoveTime > System.currentTimeMillis() ? Math.max(0, movementSpeed - 0.0075)
	                    : Math.min(0.1, movementSpeed + 0.0075);
	        else {
	            noMoveTime = 0;
	            movementSpeed = Math.min(0.15 + distanceBtw * 0.05, movementSpeed + 0.02);
	        }

	        targetDirection.add(targetLocation.toVector().subtract(currentLocation.toVector()).multiply(0.2));

	        if (targetDirection.length() < 1)
	            movementSpeed = targetDirection.length() * movementSpeed;

	        targetDirection = targetDirection.normalize();

	        if (distTarget > 0.1)
	            currentLocation.add(targetDirection.clone().multiply(movementSpeed));

	        UtilParticles.display(Particles.LAVA, currentLocation);
	        UtilParticles.display(Particles.FLAME, currentLocation);
		}

		private Location generateNewTarget() {
	        return player.getEyeLocation()
	                .add(Math.random() * 6 - 3,
	                        Math.random() * 1.5,
	                        Math.random() * 6 - 3);
	    }
	}
}