package realcraft.bukkit.cosmetics.effects;

import org.bukkit.entity.Player;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;
import realcraft.bukkit.cosmetics.utils.MathUtils;
import realcraft.bukkit.utils.Particles;

public class EffectMusic extends Effect {

	public EffectMusic(CosmeticType type){
		super(type);
	}

	@Override
	public void update(Player player){
		for (int i = 0; i < 5; i++) {
			Particles.NOTE.display(player.getLocation().add(MathUtils.randomDouble(-1.5, 1.5),MathUtils.randomDouble(0, 2.5), MathUtils.randomDouble(-1.5, 1.5)),1);
		}
	}
}