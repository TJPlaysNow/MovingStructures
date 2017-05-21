package com.pzg.www.movingstructure.main.objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Block {
	
	public World world;
	public double x, y, z;
	public Material material;
	public Byte data;
	
	Block(World world, double x, double y, double z, Material material, Byte data) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.material = material;
		this.data = data;
	}
	
	@SuppressWarnings("deprecation")
	public Block(org.bukkit.block.Block block) {
		this.world = block.getWorld();
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
		this.material = block.getType();
		this.data = block.getData();
	}

	public Block(Location location, Material material, Byte data) {
		this.world = location.getWorld();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.material = material;
		this.data = data;
	}

	public Location getLocation() {
		return new Location(world, x, y, z);
	}
	
	public World getWorld() {
		return world;
	}
	
	public Material getType() {
		return material;
	}
	
	public Byte getData() {
		return data;
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