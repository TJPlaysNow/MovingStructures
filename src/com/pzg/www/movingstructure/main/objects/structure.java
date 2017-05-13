package com.pzg.www.movingstructure.main.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Shulker;

public class structure {
	
	protected String name;
	
	protected List<Block> blocks = new ArrayList<Block>();
	protected List<FallingBlock> fBlocks = new ArrayList<FallingBlock>();
	protected List<Shulker> sBlocks = new ArrayList<Shulker>();
	protected Location center;
	protected ArmorStand centerStand;
	
	protected StructureState state;
	
	public structure(String name, Block... blocks) {
		for (Block block : blocks) {
			this.blocks.add(block);
		}
		this.name = name;
	}
	
	public structure(String name) {
		this.name = name;
	}
	
	public void addBlock(Block block) {
		blocks.add(block);
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