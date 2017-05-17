package com.anticheat.utils;

import org.bukkit.Location;

public class SimpleLocation {

	private int x, y, z;

	public SimpleLocation(Location l)
	{
		this(l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	public SimpleLocation(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getZ()
	{
		return z;
	}

}