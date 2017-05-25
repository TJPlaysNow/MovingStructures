package com.pzg.www.movingstructure.main.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

import com.pzg.www.api.config.Config;
import com.pzg.www.movingstructure.main.PluginMain;

public class Structure {
	
	protected String name;
	protected Config config;
	protected List<Block> blocks = new ArrayList<Block>();
	protected List<MovingBlock> movingBlocks = new ArrayList<MovingBlock>();
	protected ArmorStand aHolder;
	protected Location center;
	protected World world;
	protected StructureState state;
	
	@SuppressWarnings("deprecation")
	public Structure(Config config) {
		this.config = config;
		int blockAmmount = config.getConfig().getInt("Blocks.Ammount");
		for (int i = 0; i < blockAmmount; i++) {
			World world = Bukkit.getWorld(config.getConfig().getString("Block." + i + ".Location.World"));
			this.world = world;
			double x = config.getConfig().getDouble("Block." + i + ".Location.X");
			double y = config.getConfig().getDouble("Block." + i + ".Location.Y");
			double z = config.getConfig().getDouble("Block." + i + ".Location.Z");
			Material material = Material.getMaterial(config.getConfig().getString("Block." + i + ".Material"));
			byte data = (byte) Byte.toUnsignedInt((byte) config.getConfig().getInt("Block." + i + ".Data"));
			world.getBlockAt(new Location(world, x, y, z)).setType(material);
			world.getBlockAt(new Location(world, x, y, z)).setData(data);
			Block block = new Block(world, x, y, z, material, data);
			blocks.add(block);
		}
		
		World world = Bukkit.getWorld(config.getConfig().getString("Center.Location.World"));
		int x = config.getConfig().getInt("Center.Location.X");
		int y = config.getConfig().getInt("Center.Location.Y");
		int z = config.getConfig().getInt("Center.Location.Z");
		float yaw = config.getConfig().getInt("Center.Location.yaw");
		float pitch = config.getConfig().getInt("Center.Location.pitch");
		
		center = new Location(world, x, y, z, yaw, pitch);
		
		aHolder = center.getWorld().spawn(center, ArmorStand.class);
		aHolder.setCustomNameVisible(false);
		aHolder.setVisible(false);
		aHolder.setCollidable(false);
		aHolder.setRemoveWhenFarAway(false);
		
		name = config.getConfig().getString("Name");
		state = StructureState.Build;
	}
	
	public Structure(String name, World world, Block... blocks) {
		for (Block block : blocks) {
			this.blocks.add(block);
		}
		this.world = world;
		this.name = name;
		config = new Config("plugins/StructureMover/Structures", name + ".yml", PluginMain.plugin);
		state = StructureState.Build;
	}
	
	public Structure(String name, World world) {
		this.name = name;
		config = new Config("plugins/StructureMover/Structures", name + ".yml", PluginMain.plugin);
		state = StructureState.Build;
		this.world = world;aHolder = world.spawn(world.getSpawnLocation(), ArmorStand.class);
		aHolder.setCustomNameVisible(false);
		aHolder.setVisible(false);
		aHolder.setCollidable(false);
		aHolder.setRemoveWhenFarAway(false);
	}
	
	public void addBlock(Block block) {
		blocks.add(block);
	}
	
	public void removeBlock(Block block) {
		blocks.remove(block);
	}
	
	public Block getBlock(int blockId) {
		return blocks.get(blockId);
	}
	
	public List<Block> getBlocks() {
		return blocks;
	}
	
	public String getName() {
		return name;
	}
	
	public void setCenter(Location center) {
		this.center = center;
	}
	
	public Location getCenter() {
		return center;
	}
	
	public boolean clearLists() {
		blocks.clear();
		movingBlocks.clear();
		if (state == StructureState.Moving) {
			setState(StructureState.Build);
		}
		if (blocks.isEmpty() && movingBlocks.isEmpty() && aHolder.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	public Config saveConfig() {
		config.getConfig().set("Blocks.Ammount", blocks.size());
		config.getConfig().set("Name", name);
		config.saveConfig();
		for (int i = 0; i < blocks.size(); i++) {
			Block block = blocks.get(i);
			
			config.getConfig().set("Block." + i + ".Location.World", block.getWorld().getName());
			config.getConfig().set("Block." + i + ".Location.X", block.getLocation().getX());
			config.getConfig().set("Block." + i + ".Location.Y", block.getLocation().getY());
			config.getConfig().set("Block." + i + ".Location.Z", block.getLocation().getZ());
			
			config.getConfig().set("Block." + i + ".Material", block.getType().toString());
			config.getConfig().set("Block." + i + ".Data", block.getData());
			
			config.saveConfig();
		}
		
		config.getConfig().set("Center.Location.World", center.getWorld().getName());
		config.getConfig().set("Center.Location.X", center.getX());
		config.getConfig().set("Center.Location.Y", center.getY());
		config.getConfig().set("Center.Location.Z", center.getZ());
		config.getConfig().set("Center.Location.yaw", center.getYaw());
		config.getConfig().set("Center.Location.pitch", center.getPitch());
		config.saveConfig();
		
		return config;
	}
	
	public void move(double addX, double addY, double addZ, float addYaw, float addPitch) {
		center.add(addX, addY, addZ);
		center.setYaw(center.getYaw() + addYaw);
		center.setPitch(center.getPitch() + addPitch);
		aHolder.teleport(center);
	}
	
	public void setState(StructureState state) {
		if (state == StructureState.Build) {
			if (this.state == StructureState.Build) {
				return;
			}
			for (MovingBlock block : movingBlocks) {
				aHolder.removePassenger(block.getHold());
				Block mBlock = new Block(block);
				block.destroy();
				blocks.add(mBlock);
			}
			movingBlocks.clear();
		} else if (state == StructureState.Moving) {
			if (this.state == StructureState.Moving) {
				return;
			}
			aHolder.teleport(center);
			for (Block block : blocks) {
				MovingBlock mBlock = new MovingBlock(block);
				movingBlocks.add(mBlock);
			}
			blocks.clear();
			for (MovingBlock block : movingBlocks) {
				aHolder.addPassenger(block.getHold());
			}
		}
	}
}