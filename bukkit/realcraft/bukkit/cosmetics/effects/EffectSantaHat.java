package realcraft.bukkit.cosmetics.effects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics2.utils.MathUtils;
import realcraft.bukkit.cosmetics2.utils.UtilParticles;

public class EffectSantaHat extends Effect {

	public EffectSantaHat(CosmeticType type){
		super(type);
	}

	@Override
	public void update(Player player){
		Location location = player.getEyeLocation().add(0, 0.3, 0);
		float radius = 0.25f;
		drawCircle(radius + 0.1f, -0.05f, location, false);
		for (int i = 0; i < 5; i++) {
			double x = MathUtils.randomDouble(-0.05, 0.05);
			double z = MathUtils.randomDouble(-0.05, 0.05);
			location.add(x, 0.46f, z);
			UtilParticles.display(255, 255, 255, location);
			location.subtract(x, 0.46f, z);
		}
		for (float f = 0; f <= 0.4f; f += 0.1f) {
			if (radius >= 0) {
				drawCircle(radius, f, location, true);
				radius -= 0.09f;
			}
		}
	}

	private void drawCircle(float radius, float height, Location location, boolean red) {
		for (int i = 0; i < 12; i++) {
			double inc = (2 * Math.PI) / 12;
			float angle = (float) (i * inc);
			float x = MathUtils.cos(angle) * radius;
			float z = MathUtils.sin(angle) * radius;
			location.add(x, height, z);
			UtilParticles.display(255, red ? 0 : 255, red ? 0 : 255, location);
			location.subtract(x, height, z);
		}
	}
}