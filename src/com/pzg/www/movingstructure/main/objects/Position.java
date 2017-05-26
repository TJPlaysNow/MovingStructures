package com.pzg.www.movingstructure.main.objects;

import org.bukkit.Location;
import org.bukkit.World;

public class Position {
	
	double x, y, z;
	World world;
	
	public Position(World world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Position(Location location) {
		this.world = location.getWorld();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
	}
	
	public Location addPosition(double x, double y, double z) {
		this.x = x + this.x;
		this.y = y + this.y;
		this.z = z + this.z;
		Location position = new Location(world, this.x, this.y, this.z);
		return position;
	}
	
	public Location getPosition() {
		Location position = new Location(world, x, y, z);
		return position;
	}
	
	public World getWorld() {
		return world;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
}