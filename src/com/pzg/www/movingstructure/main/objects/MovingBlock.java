package com.pzg.www.movingstructure.main.objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Shulker;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class MovingBlock  {
	
	protected Shulker sBlock;
	protected FallingBlock fBlock;
	protected ArmorStand aBlock;
	protected Position pos;
	
	public MovingBlock(Block block) {
		block.getWorld().getBlockAt(block.getLocation()).setType(Material.AIR);
		
		aBlock = block.getWorld().spawn(block.getLocation(), ArmorStand.class);
		aBlock.setCustomNameVisible(false);
		aBlock.setVisible(false);
		aBlock.setCollidable(false);
		aBlock.setRemoveWhenFarAway(false);
		
		fBlock = block.getWorld().spawn(block.getLocation(), FallingBlock.class);
		fBlock.setGravity(false);
		fBlock.setVelocity(new Vector(0, 0, 0));
		fBlock.setTicksLived(1);
		
//		fBlock.setMetadata("TileID", new FixedMetadataValue(PluginMain.plugin, block.getType().getId()));
//		fBlock.setMetadata("Data", new FixedMetadataValue(PluginMain.plugin, block.data.intValue()));
		
		sBlock = block.getWorld().spawn(block.getLocation(), Shulker.class);
		sBlock.setAI(false);
		sBlock.setSilent(true);
		sBlock.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 1, true));
		
		aBlock.addPassenger(fBlock);
		aBlock.addPassenger(sBlock);
		
		pos = new Position(block.getLocation());
	}
	
	public ArmorStand getHold() {
		return aBlock;
	}
	
	public Material getType() {
		return fBlock.getMaterial();
	}
	
	public Position getPosition() {
		return pos;
	}
	
	public Position move(double x, double y, double z) {
		Location newLoc = pos.addPosition(x, y, z);
		aBlock.teleport(newLoc);
		return pos;
	}
	
	@SuppressWarnings("deprecation")
	public Byte getData() {
		return fBlock.getBlockData();
	}
	
	public void destroy() {
		sBlock.remove();
		fBlock.remove();
		aBlock.remove();
	}
}