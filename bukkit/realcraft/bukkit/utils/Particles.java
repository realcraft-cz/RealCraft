package realcraft.bukkit.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public enum Particles {
	BLOCK_CRACK        	(Particle.BLOCK),
	BLOCK_DUST         	(Particle.DUST),
	BUBBLE_COLUMN_UP   	(Particle.BUBBLE_COLUMN_UP),
	BUBBLE_POP         	(Particle.BUBBLE_POP),
	CLOUD              	(Particle.CLOUD),
	CRIT               	(Particle.CRIT),
	CRIT_MAGIC         	(Particle.ENCHANTED_HIT),
	CURRENT_DOWN       	(Particle.CURRENT_DOWN),
	DAMAGE_INDICATOR   	(Particle.DAMAGE_INDICATOR),
	DOLPHIN            	(Particle.DOLPHIN),
	DRAGON_BREATH      	(Particle.DRAGON_BREATH),
	DRIP_LAVA          	(Particle.DRIPPING_LAVA),
	DRIP_WATER         	(Particle.DRIPPING_WATER),
	ENCHANTMENT_TABLE  	(Particle.ENCHANT),
	END_ROD            	(Particle.END_ROD),
	EXPLOSION_HUGE     	(Particle.EXPLOSION_EMITTER),
	EXPLOSION    	(Particle.EXPLOSION),
	EXPLOSION_NORMAL   	(Particle.EXPLOSION),
	FALLING_DUST       	(Particle.FALLING_DUST),
	FIREWORKS_SPARK    	(Particle.FIREWORK),
	FLAME              	(Particle.FLAME),
	HEART              	(Particle.HEART),
	ITEM_CRACK         	(Particle.ITEM),
	LAVA               	(Particle.LAVA),
	NAUTILUS           	(Particle.NAUTILUS),
	NOTE               	(Particle.NOTE),
	PORTAL             	(Particle.PORTAL),
	REDSTONE           	(Particle.DUST),
	SLIME              	(Particle.ITEM_SLIME),
	SMOKE_LARGE        	(Particle.LARGE_SMOKE),
	SMOKE_NORMAL       	(Particle.SMOKE),
	SNOW_SHOVEL        	(Particle.ITEM_SNOWBALL),
	SNOWBALL           	(Particle.ITEM_SNOWBALL),
	SPELL              	(Particle.EFFECT),
	SPELL_MOB_AMBIENT  	(Particle.INSTANT_EFFECT),
	SPELL_WITCH        	(Particle.WITCH),
	SPIT               	(Particle.SPIT),
	SQUID_INK          	(Particle.SQUID_INK),
	SWEEP_ATTACK       	(Particle.SWEEP_ATTACK),
	VILLAGER_ANGRY     	(Particle.ANGRY_VILLAGER),
	VILLAGER_HAPPY     	(Particle.HAPPY_VILLAGER);

	private Particle particle;

	private Particles(Particle particle){
		this.particle = particle;
	}

	public void display(Location center,int amount){
		center.getWorld().spawnParticle(particle,center,amount,0,0,0,0);
	}

	public void display(Location center,int amount, Player... players){
		for(Player player : players) player.spawnParticle(particle,center,amount,0,0,0,0);
	}

	public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range){
		center.getWorld().spawnParticle(particle,center,amount,offsetX,offsetY,offsetZ,speed);
	}

	public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players){
		for(Player player : players) player.spawnParticle(particle,center,amount,offsetX,offsetY,offsetZ,speed);
	}

	public void display(Vector direction, float speed, Location center, double range){
		center.getWorld().spawnParticle(particle,center,1,direction.getX(),direction.getY(),direction.getZ(),speed);
	}

	public void display(Vector direction, float speed, Location center, Player... players){
		for(Player player : players) player.spawnParticle(particle,center,1,direction.getX(),direction.getY(),direction.getZ(),speed);
	}

	public void display(Color color, Location center, double range){
		center.getWorld().spawnParticle(particle,center,1,new DustOptions(color,1));
	}

	public void display(Color color, Location center, Player... players){
		for(Player player : players) player.spawnParticle(particle,center,1,new DustOptions(color,1));
	}

	public void display(BlockData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range){
		center.getWorld().spawnParticle(particle,center,amount,offsetX,offsetY,offsetZ,speed,data);
	}

	public void display(BlockData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players){
		for(Player player : players) player.spawnParticle(particle,center,amount,offsetX,offsetY,offsetZ,speed,data);
	}

	public void display(BlockData data, Vector direction, float speed, Location center, double range){
		center.getWorld().spawnParticle(particle,center,1,direction.getX(),direction.getY(),direction.getZ(),speed,data);
	}

	public void display(BlockData data, Vector direction, float speed, Location center, Player... players){
		for(Player player : players) player.spawnParticle(particle,center,1,direction.getX(),direction.getY(),direction.getZ(),speed,data);
	}

	public void display(ItemStack data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range){
		center.getWorld().spawnParticle(particle,center,amount,offsetX,offsetY,offsetZ,speed,data);
	}

	public void display(ItemStack data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players){
		for(Player player : players) player.spawnParticle(particle,center,amount,offsetX,offsetY,offsetZ,speed,data);
	}

	public void display(ItemStack data, Vector direction, float speed, Location center, double range){
		center.getWorld().spawnParticle(particle,center,1,direction.getX(),direction.getY(),direction.getZ(),speed,data);
	}

	public void display(ItemStack data, Vector direction, float speed, Location center, Player... players){
		for(Player player : players) player.spawnParticle(particle,center,1,direction.getX(),direction.getY(),direction.getZ(),speed,data);
	}

	public void display(DustOptions data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range){
		center.getWorld().spawnParticle(particle,center,amount,offsetX,offsetY,offsetZ,speed,data);
	}

	public void display(DustOptions data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players){
		for(Player player : players) player.spawnParticle(particle,center,amount,offsetX,offsetY,offsetZ,speed,data);
	}

	public void display(DustOptions data, Vector direction, float speed, Location center, double range){
		center.getWorld().spawnParticle(particle,center,1,direction.getX(),direction.getY(),direction.getZ(),speed,data);
	}

	public void display(DustOptions data, Vector direction, float speed, Location center, Player... players){
		for(Player player : players) player.spawnParticle(particle,center,1,direction.getX(),direction.getY(),direction.getZ(),speed,data);
	}
}