package com.realcraft.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;

public class LocationUtil {

	public static Location getConfigLocation(FileConfiguration config,String path){
		double x = (config.getDouble(path+".x"));
		double y = (config.getDouble(path+".y"));
		double z = (config.getDouble(path+".z"));
		float yaw = (float)(config.getDouble(path+".yaw"));
		float pitch = (float)(config.getDouble(path+".pitch"));
		World world = Bukkit.getServer().getWorld(config.getString(path+".world"));
		return new Location(world,x,y,z,yaw,pitch);
	}

	public static float normalAngle(float angle) {
        while (angle <= -180) angle += 360;
        while (angle > 180) angle -= 360;
        return angle;
    }

	public static BlockFace yawToFace (float yaw) {
        return yawToFace(yaw, true);
    }
    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        yaw = normalAngle(yaw);
        if (useSubCardinalDirections) {
            switch ((int) yaw) {
            case 0 : return BlockFace.NORTH;
            case 45 : return BlockFace.NORTH_EAST;
            case 90 : return BlockFace.EAST;
            case 135 : return BlockFace.SOUTH_EAST;
            case 180 : return BlockFace.SOUTH;
            case 225 : return BlockFace.SOUTH_WEST;
            case 270 : return BlockFace.WEST;
            case 315 : return BlockFace.NORTH_WEST;
            }
            //Let's apply angle differences
            if (yaw >= -22.5 && yaw < 22.5) {
                return BlockFace.NORTH;
            } else if (yaw >= 22.5 && yaw < 67.5) {
                return BlockFace.NORTH_EAST;
            } else if (yaw >= 67.5 && yaw < 112.5) {
                return BlockFace.EAST;
            } else if (yaw >= 112.5 && yaw < 157.5) {
                return BlockFace.SOUTH_EAST;
            } else if (yaw >= -67.5 && yaw < -22.5) {
                return BlockFace.NORTH_WEST;
            } else if (yaw >= -112.5 && yaw < -67.5) {
                return BlockFace.WEST;
            } else if (yaw >= -157.5 && yaw < -112.5) {
                return BlockFace.SOUTH_WEST;
            } else {
                return BlockFace.SOUTH;
            }
        } else {
            switch ((int) yaw) {
            case 0 : return BlockFace.NORTH;
            case 90 : return BlockFace.EAST;
            case 180 : return BlockFace.SOUTH;
            case 270 : return BlockFace.WEST;
            }
            //Let's apply angle differences
            if (yaw >= -45 && yaw < 45) {
                return BlockFace.NORTH;
            } else if (yaw >= 45 && yaw < 135) {
                return BlockFace.EAST;
            } else if (yaw >= -135 && yaw < -45) {
                return BlockFace.WEST;
            } else {
                return BlockFace.SOUTH;
            }
        }
    }
    public static float faceToYaw(BlockFace face){
    	switch(face){
    	case NORTH: return 0f;
    	case EAST: return 90f;
    	case SOUTH: return 180f;
    	case WEST: return 270f;
		default: return 0f;
    	}
    }
}