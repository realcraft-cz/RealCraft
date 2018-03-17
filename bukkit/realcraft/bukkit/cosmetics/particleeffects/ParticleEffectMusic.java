package realcraft.bukkit.cosmetics.particleeffects;

import java.util.Random;

import org.bukkit.entity.Player;

import realcraft.bukkit.cosmetics.utils.MathUtils;
import realcraft.bukkit.utils.Particles;

public class ParticleEffectMusic extends ParticleEffect {

	public ParticleEffectMusic(ParticleEffectType type){
		super(type);
	}

	@Override
	public void onUpdate(Player player,boolean moving){
		for (int i = 0; i < 12; i++) {
            Random random = new Random();
            int j = random.nextInt(25);
            Particles.ParticleColor particleColor = new Particles.NoteColor(j);
            Particles.NOTE.display(particleColor, player.getLocation().add(MathUtils.randomDouble(-1.5, 1.5),
                    MathUtils.randomDouble(0, 2.5), MathUtils.randomDouble(-1.5, 1.5)), 32);
        }
	}
}