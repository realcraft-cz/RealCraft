package realcraft.bukkit.cosmetics.effects;

import org.bukkit.entity.Player;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;

public class EffectEnderAura extends Effect {

	public EffectEnderAura(CosmeticType type){
		super(type);
	}

	@Override
	public void update(Player player){
		player.getWorld().playEffect(player.getLocation().add(0, 1, 0), org.bukkit.Effect.ENDER_SIGNAL, 0);
	}
}