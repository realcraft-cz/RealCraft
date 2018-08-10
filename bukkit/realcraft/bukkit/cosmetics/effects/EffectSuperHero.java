package realcraft.bukkit.cosmetics.effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics2.utils.UtilParticles;
import realcraft.bukkit.utils.Particles;

import java.util.HashMap;

public class EffectSuperHero extends Effect implements Listener {

	HashMap<Player,Boolean> isMoving = new HashMap<>();

	boolean x = true;

	private boolean[][] shape = {
			{x, x, x, x, x,},
			{x, x, x, x, x,},
			{x, x, x, x, x,},
			{x, x, x, x, x,},
			{x, x, x, x, x,},
			{x, x, x, x, x,},
			{x, x, x, x, x,},
			{x, x, x, x, x,},
	};

	public EffectSuperHero(CosmeticType type){
		super(type);
		Bukkit.getPluginManager().registerEvents(this,RealCraft.getInstance());
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event){
		if(this.isRunning(event.getPlayer())){
			if((event.getFrom().getX() != event.getTo().getX() || event.getFrom().getY() != event.getTo().getY() || event.getFrom().getZ() != event.getTo().getZ())){
				setMoving(event.getPlayer(),true);
			} else {
				setMoving(event.getPlayer(),false);
			}
		}
	}

	public boolean isMoving(Player player){
		return (isMoving.containsKey(player) && isMoving.get(player) == true);
	}

	public void setMoving(Player player,boolean moving){
		isMoving.put(player,moving);
	}

	@Override
	public void update(Player player){
		Location location = player.getLocation();
		double space = 0.2;
		double defX = location.getX() - (space * shape[0].length / 2) + space / 2;
		double x = defX;
		double defY = location.getY() + 1.5;
		double y = defY;
		double angle = -((location.getYaw() + 180) / 60);
		angle += (location.getYaw() < -180 ? 3.25 : 2.985);
		for (int i = 0; i < shape.length; i++) {
			for (int j = 0; j < shape[i].length; j++) {
				if (shape[i][j]) {
					Location target = location.clone();
					target.setX(x);
					target.setY(y);

					Vector v = target.toVector().subtract(location.toVector());
					Vector v2 = getBackVector(location);
					v = rotateAroundAxisY(v, angle);
					double iT = ((double) i) / 10;
					v2.setY(0).multiply(-0.2 - iT);

					Location loc = location.clone();

					loc.add(v);
					loc.add(v2);
					if (isMoving(player))
						loc.setY(defY);

					for (int k = 0; k < 3; k++)
						UtilParticles.display(255, 0, 0, loc);
					loc.subtract(v2);
					loc.subtract(v);
				}
				x += space;
			}
			y -= space;
			x = defX;
		}
		UtilParticles.display(Particles.CLOUD, 0.15F, 0.1f, 0.15f, player.getLocation(), 4);
	}

	private Vector rotateAroundAxisY(Vector v, double angle) {
		double x, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		x = v.getX() * cos + v.getZ() * sin;
		z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}

	private Vector getBackVector(Location loc) {
		final float newZ = (float) (loc.getZ() + (1 * Math.sin(Math.toRadians(loc.getYaw() + 90 * 1))));
		final float newX = (float) (loc.getX() + (1 * Math.cos(Math.toRadians(loc.getYaw() + 90 * 1))));
		return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
	}
}