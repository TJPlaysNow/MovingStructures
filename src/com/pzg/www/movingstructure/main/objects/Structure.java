package com.pzg.www.movingstructure.main.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Shulker;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.pzg.www.api.config.Config;
import com.pzg.www.movingstructure.main.PluginMain;

public class Structure {
	
	protected String name;
	protected Config config;
	protected List<Block> blocks = new ArrayList<Block>();
	protected List<FallingBlock> fBlocks = new ArrayList<FallingBlock>();
	protected List<Shulker> sBlocks = new ArrayList<Shulker>();
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
		
		World world = config.getConfig().getInt("Center.Location.World");
		int x = config.getConfig().getInt("Center.Location.X");
		int y = config.getConfig().getInt("Center.Location.Y");
		int z = config.getConfig().getInt("Center.Location.Z");
		float yaw = config.getConfig().getInt("Center.Location.yaw");
		float pitch = config.getConfig().getInt("Center.Location.pitch");
		
		center = new Location(world, x, y, z)
		
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
		this.world = world;
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
			
			config.getConfig().set("Center.Location.World", center.getWorld());
			config.getConfig().set("Center.Location.X", center.getX());
			config.getConfig().set("Center.Location.Y", center.getY());
			config.getConfig().set("Center.Location.Z", center.getZ());
			config.getConfig().set("Center.Location.yaw", center.getYaw());
			config.getConfig().set("Center.Location.pitch", center.getPitch());
			
			config.saveConfig();
		}
		blocks.clear();
		return config;
	}
	
	public void move(double addX, double addY, double addZ, float addYaw, float addPitch) {
		center.add(addX, addY, addZ);
		center.setYaw(center.getYaw() + addYaw);
		center.setPitch(center.getPitch() + addPitch);
		
		
// 		for (ArmorStand a : aHolder) {
// 			a.teleport(a.getLocation().add(new Location(world, addX, addY, addZ, addYaw, addPitch)));
// 		}
	}
	
//	public void refreshCenter() {
//		List<Double> allX = new ArrayList<Double>();
//		List<Double> allY = new ArrayList<Double>();
//		List<Double> allZ = new ArrayList<Double>();
//		for (Block block : blocks) {
//			allX.add(block.getX());
//			allY.add(block.getY());
//			allZ.add(block.getZ());
//		}
//		double addedX = 0;
//		for (double x : allX) {
//			addedX = addedX + x;
//		}
//		double addedY = 0;
//		for (double y : allY) {
//			addedY = addedY + y;
//		}
//		double addedZ = 0;
//		for (double z : allZ) {
//			addedZ = addedZ + z;
//		}
//		double finalX = addedX / allX.size();
//		double finalY = addedY / allY.size();
//		double finalZ = addedZ / allZ.size();
//		center = new Location(world, finalX, finalY, finalZ);
//	}
	
//	public Location getCenter() {
//		return center;
//	}
	
	@SuppressWarnings("deprecation")
	public void setState(StructureState state) {
		if (state == StructureState.Build) {
			if (this.state == StructureState.Build) {
				return;
			}
			for (ArmorStand armor : aHolder) {
				for (Entity e : armor.getPassengers()) {
					if (e.getType().equals(EntityType.FALLING_BLOCK)) {
						FallingBlock f = (FallingBlock) e;
						Block b = new Block(f.getLocation(), f.getMaterial(), f.getBlockData());
						blocks.add(b);
						fBlocks.remove(f);
						f.remove();
					} else if (e.getType().equals(EntityType.SHULKER)) {
						Shulker s = (Shulker) e;
						sBlocks.remove(s);
						s.remove();
					}
				}
				aHolder = null;
			}
			aHolder.clear();
			this.state = state;
		} else if (state == StructureState.Moving) {
			if (this.state == StructureState.Moving) {
				return;
			}
			
			ArmorStand center = center.getWorld().spawn(center, ArmorStand.class);
			armor.setVisible(false);
			armor.setCollidable(false);
			armor.setCustomNameVisible(false);
			armor.setRemoveWhenFarAway(false);
			
			for (Block block : blocks) {
				block.getWorld().getBlockAt(block.getLocation()).setType(Material.AIR);
				
				FallingBlock fBlock = block.getWorld().spawn(block.getLocation(), FallingBlock.class);
				fBlock.setGravity(false);
				fBlock.setVelocity(new Vector(0, 0, 0));
				fBlock.setTicksLived(-1);
				fBlock.setMetadata("TileID", new FixedMetadataValue(PluginMain.plugin, block.getType().getId()));
				fBlock.setMetadata("Data", new FixedMetadataValue(PluginMain.plugin, block.data.intValue()));
				
				Shulker sBlock = block.getWorld().spawn(block.getLocation(), Shulker.class);
				sBlock.setAI(false);
				sBlock.setSilent(true);
				sBlock.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 1, true));
				
				armor.addPassenger(fBlock);
				armor.addPassenger(sBlock);
				
				fBlocks.add(fBlock);
				sBlocks.add(sBlock);
			}
			
			aHolder = armor;
			blocks.clear();
			this.state = state;
		}
	}
}
