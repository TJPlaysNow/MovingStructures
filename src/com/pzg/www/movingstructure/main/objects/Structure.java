package com.pzg.www.movingstructure.main.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Shulker;

import com.pzg.www.api.config.Config;
import com.pzg.www.movingstructure.main.PluginMain;

public class Structure {
	
	protected String name;
	protected Config config;
	protected List<Block> blocks = new ArrayList<Block>();
	protected List<FallingBlock> fBlocks = new ArrayList<FallingBlock>();
	protected List<Shulker> sBlocks = new ArrayList<Shulker>();
	protected Location center;
	protected ArmorStand centerStand;
	
	protected StructureState state;
	
	@SuppressWarnings("deprecation")
	public Structure(Config config) {
		this.config = config;
		int blockAmmount = config.getConfig().getInt("Blocks.Ammount");
		for (int i = 0; i < blockAmmount; i++) {
			World world = Bukkit.getWorld(config.getConfig().getString("Block." + i + ".Location.World"));
			double x = config.getConfig().getDouble("Block." + i + ".Location.X");
			double y = config.getConfig().getDouble("Block." + i + ".Location.Y");
			double z = config.getConfig().getDouble("Block." + i + ".Location.Z");
			Material material = Material.getMaterial(config.getConfig().getString("Block." + i + ".Material"));
			byte data = (byte) Byte.toUnsignedInt((byte) config.getConfig().getInt("Block." + i + ".Data"));
			world.getBlockAt(new Location(world, x, y, z)).setType(material);
			world.getBlockAt(new Location(world, x, y, z)).setData(data);
			Block block = world.getBlockAt(new Location(world, x, y, z));
			blocks.add(block);
		}
		state = StructureState.Build;
	}
	
	public Structure(String name, Block... blocks) {
		for (Block block : blocks) {
			this.blocks.add(block);
		}
		this.name = name;
		config = new Config("plugins/StructureMover/Structures", name + ".yml", PluginMain.plugin);
		state = StructureState.Build;
	}
	
	public Structure(String name) {
		this.name = name;
		config = new Config("plugins/StructureMover/Structures", name + ".yml", PluginMain.plugin);
		state = StructureState.Build;
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
	
	@SuppressWarnings("deprecation")
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
		blocks.clear();
		return config;
	}
	
	public void refreshCenter() {
		World world = blocks.get(1).getWorld();
		List<Integer> allX = new ArrayList<Integer>();
		List<Integer> allY = new ArrayList<Integer>();
		List<Integer> allZ = new ArrayList<Integer>();
		for (Block block : blocks) {
			allX.add(block.getX());
			allY.add(block.getY());
			allZ.add(block.getZ());
		}
		int addedX = 0;
		for (int x : allX) {
			addedX = addedX + x;
		}
		int addedY = 0;
		for (int y : allY) {
			addedY = addedY + y;
		}
		int addedZ = 0;
		for (int z : allZ) {
			addedZ = addedZ + z;
		}
		int finalX = addedX / allX.size();
		int finalY = addedY / allY.size();
		int finalZ = addedZ / allZ.size();
		center = new Location(world, finalX, finalY, finalZ);
	}
	
	public Location getCenter() {
		return center;
	}
	
	@SuppressWarnings("deprecation")
	public void setState(StructureState state) {
		if (state == StructureState.Build) {
			if (this.state == StructureState.Build) {
				return;
			}
			for (FallingBlock fBlock : fBlocks) {
				Block block = fBlock.getWorld().getBlockAt(fBlock.getLocation());
				block.setType(fBlock.getMaterial());
				block.setData(fBlock.getBlockData());
				blocks.add(block);
				fBlocks.remove(fBlock);
				fBlock.remove();
			}
			for (Shulker sBlock : sBlocks) {
				sBlocks.remove(sBlock);
				sBlock.remove();
			}
			centerStand.remove();
		}
		if (state == StructureState.Moving) {
			if (this.state == StructureState.Moving) {
				return;
			}
			for (Block block : blocks) {
				FallingBlock fBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
				Shulker sBlock = block.getWorld().spawn(block.getLocation(), Shulker.class);
				sBlock.setAI(false);
				sBlock.setSilent(true);
				fBlock.setGravity(false);
				sBlock.addPassenger(fBlock);
				sBlocks.add(sBlock);
				fBlocks.add(fBlock);
				blocks.remove(block);
				block.setType(Material.AIR);
			}
			centerStand = getCenter().getWorld().spawn(getCenter(), ArmorStand.class);
			centerStand.setAI(false);
			centerStand.setCollidable(false);
			centerStand.setGravity(false);
			centerStand.setVisible(false);
			for (Shulker sBlock : sBlocks) {
				centerStand.addPassenger(sBlock);
			}
		}
	}
}