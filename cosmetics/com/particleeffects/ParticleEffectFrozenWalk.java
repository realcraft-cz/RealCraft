package com.particleeffects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.realcraft.utils.Particles;

public class ParticleEffectFrozenWalk extends ParticleEffect {

	public ParticleEffectFrozenWalk(ParticleEffectType type){
		super(type);
	}

	@Override
	public void onUpdate(Player player,boolean moving){
		Vector vectorLeft = getLeftVector(player.getLocation()).normalize().multiply(0.15);
        Vector vectorRight = getRightVector(player.getLocation()).normalize().multiply(0.15);
        Location locationLeft = player.getLocation().add(vectorLeft);
        Location locationRight = player.getLocation().add(vectorRight);
        locationLeft.setY(player.getLocation().getY());
        locationRight.setY(player.getLocation().getY());

        Particles.ITEM_CRACK.display(new Particles.ItemData(Material.SNOW, (byte) 0), 0, 0, 0, 0f, 0, locationLeft, 32);
        Particles.ITEM_CRACK.display(new Particles.ItemData(Material.SNOW, (byte) 0), 0, 0, 0, 0f, 0, locationRight, 32);
	}

	public static Vector getLeftVector(Location loc) {
        final float newX = (float) (loc.getX() + (1 * Math.cos(Math.toRadians(loc.getYaw() + 0))));
        final float newZ = (float) (loc.getZ() + (1 * Math.sin(Math.toRadians(loc.getYaw() + 0))));

        return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
    }

    public static Vector getRightVector(Location loc) {
        final float newX = (float) (loc.getX() + (-1 * Math.cos(Math.toRadians(loc.getYaw() + 0))));
        final float newZ = (float) (loc.getZ() + (-1 * Math.sin(Math.toRadians(loc.getYaw() + 0))));

        return new Vector(newX - loc.getX(), 0, newZ - loc.getZ());
    }
}