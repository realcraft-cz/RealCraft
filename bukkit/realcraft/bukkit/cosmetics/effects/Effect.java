package realcraft.bukkit.cosmetics.effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import realcraft.bukkit.RealCraft;
import realcraft.bukkit.cosmetics.cosmetic.Cosmetic;
import realcraft.bukkit.cosmetics.cosmetic.CosmeticType;

public abstract class Effect extends Cosmetic {

	public Effect(CosmeticType type){
		super(type);
		Bukkit.getScheduler().runTaskTimerAsynchronously(RealCraft.getInstance(),new Runnable(){
			@Override
			public void run(){
				for(Player player : Bukkit.getServer().getOnlinePlayers()){
					if(Effect.this.isRunning(player)){
						Effect.this.update(player);
					}
				}
			}
		},0,this.getRepeatDelay());
	}

	@Override
	public void run(Player player){
	}

	@Override
	public void clear(Player player){
	}

	public abstract void update(Player player);

	private int getRepeatDelay(){
		switch(this.getType()){
			case EFFECT_RAINCLOUD: return 1;
			case EFFECT_SNOWCLOUD: return 1;
			case EFFECT_BLOODHELIX: return 1;
			case EFFECT_FLAMERINGS: return 1;
			case EFFECT_INLOVE: return 6;
			case EFFECT_GREENSPARKS: return 1;
			case EFFECT_FROZENWALK: return 1;
			case EFFECT_MUSIC: return 6;
			case EFFECT_ENCHANTED: return 4;
			case EFFECT_CRUSHEDCANDY: return 2;
			case EFFECT_ANGELWINGS: return 2;
			case EFFECT_SUPERHERO: return 2;
			case EFFECT_SANTAHAT: return 2;
			case EFFECT_ENDERAURA: return 1;
		}
		return 1;
	}
}