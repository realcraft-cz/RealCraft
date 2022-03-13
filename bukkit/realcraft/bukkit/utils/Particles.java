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
	BLOCK_CRACK        	(Particle.BLOCK_CRACK),
	BLOCK_DUST         	(Particle.BLOCK_DUST),
	BUBBLE_COLUMN_UP   	(Particle.BUBBLE_COLUMN_UP),
	BUBBLE_POP         	(Particle.BUBBLE_POP),
	CLOUD              	(Particle.CLOUD),
	CRIT               	(Particle.CRIT),
	CRIT_MAGIC         	(Particle.CRIT_MAGIC),
	CURRENT_DOWN       	(Particle.CURRENT_DOWN),
	DAMAGE_INDICATOR   	(Particle.DAMAGE_INDICATOR),
	DOLPHIN            	(Particle.DOLPHIN),
	DRAGON_BREATH      	(Particle.DRAGON_BREATH),
	DRIP_LAVA          	(Particle.DRIP_LAVA),
	DRIP_WATER         	(Particle.DRIP_WATER),
	ENCHANTMENT_TABLE  	(Particle.ENCHANTMENT_TABLE),
	END_ROD            	(Particle.END_ROD),
	EXPLOSION_HUGE     	(Particle.EXPLOSION_HUGE),
	EXPLOSION_LARGE    	(Particle.EXPLOSION_LARGE),
	EXPLOSION_NORMAL   	(Particle.EXPLOSION_NORMAL),
	FALLING_DUST       	(Particle.FALLING_DUST),
	FIREWORKS_SPARK    	(Particle.FIREWORKS_SPARK),
	FLAME              	(Particle.FLAME),
	HEART              	(Particle.HEART),
	ITEM_CRACK         	(Particle.ITEM_CRACK),
	LAVA               	(Particle.LAVA),
	LEGACY_BLOCK_CRACK 	(Particle.LEGACY_BLOCK_CRACK),
	LEGACY_BLOCK_DUST  	(Particle.LEGACY_BLOCK_DUST),
	LEGACY_FALLING_DUST	(Particle.LEGACY_FALLING_DUST),
	MOB_APPEARANCE     	(Particle.MOB_APPEARANCE),
	NAUTILUS           	(Particle.NAUTILUS),
	NOTE               	(Particle.NOTE),
	PORTAL             	(Particle.PORTAL),
	REDSTONE           	(Particle.REDSTONE),
	SLIME              	(Particle.SLIME),
	SMOKE_LARGE        	(Particle.SMOKE_LARGE),
	SMOKE_NORMAL       	(Particle.SMOKE_NORMAL),
	SNOW_SHOVEL        	(Particle.SNOW_SHOVEL),
	SNOWBALL           	(Particle.SNOWBALL),
	SPELL              	(Particle.SPELL),
	SPELL_INSTANT      	(Particle.SPELL_INSTANT),
	SPELL_MOB          	(Particle.SPELL_MOB),
	SPELL_MOB_AMBIENT  	(Particle.SPELL_MOB_AMBIENT),
	SPELL_WITCH        	(Particle.SPELL_WITCH),
	SPIT               	(Particle.SPIT),
	SQUID_INK          	(Particle.SQUID_INK),
	SUSPENDED          	(Particle.SUSPENDED),
	SUSPENDED_DEPTH    	(Particle.SUSPENDED_DEPTH),
	SWEEP_ATTACK       	(Particle.SWEEP_ATTACK),
	TOTEM              	(Particle.TOTEM),
	TOWN_AURA          	(Particle.TOWN_AURA),
	VILLAGER_ANGRY     	(Particle.VILLAGER_ANGRY),
	VILLAGER_HAPPY     	(Particle.VILLAGER_HAPPY),
	WATER_BUBBLE       	(Particle.WATER_BUBBLE),
	WATER_DROP         	(Particle.WATER_DROP),
	WATER_SPLASH       	(Particle.WATER_SPLASH),
	WATER_WAKE         	(Particle.WATER_WAKE);

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