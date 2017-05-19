package com.pzg.www.movingstructure.main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.pzg.www.api.config.Config;
import com.pzg.www.movingstructure.main.objects.Structure;
import com.pzg.www.movingstructure.main.objects.StructureState;

public class PluginMain extends JavaPlugin implements Listener {
	
	public static Logger logger;
	public static Plugin plugin;
	public static Config config;
	public static List<Structure> structures;
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		logger = getLogger();
		plugin = this;
		config = new Config("plugins/StructureMover", "config.yml", plugin);
		for (int i = 0; i <= config.getConfig().getInt("structures.ammount"); i++) {
			String fileName = config.getConfig().getString("structure.file." + i);
			Config structure = new Config("plugins/StructureMover/Structures", fileName + ".yml", plugin);
			structures.add(new Structure(structure));
			logger.info(ChatColor.GOLD + "Loaded structure " + ChatColor.GREEN + structure.getConfig().getName() + ChatColor.GOLD + " sucesfully!");
		}
	}
	
	@Override
	public void onDisable() {
		int counter = 0;
		List<String> names = new ArrayList<String>();
		for (Structure structure : structures) {
			counter++;
			structure.setState(StructureState.Build);
			Config structureConfig = structure.saveConfig();
			String name = structureConfig.getConfig().getName();
			names.add(name);
		}
		config.getConfig().set("structures.ammount", counter);
		config.saveConfig();
		for (int i = 0; i <= counter; i++) {
			config.getConfig().set("structure.file." + i, names.get(i));
			config.saveConfig();
		}
	}
	
	@EventHandler
	public void onBlockPlaced(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		if (player.getMetadata("CreatingStructure") == new FixedMetadataValue(plugin, "Making-the-best-structure!")) {
			FixedMetadataValue meta = (FixedMetadataValue) player.getMetadata("SturctureName");
			for (Structure s : structures) {
				if (s.getName() == meta.asString()) {
					s.addBlock(e.getBlock());
				} else {
					continue;
				}
			}
			
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (label.equalsIgnoreCase("createStructure")) {
				if (args.length == 1) {
					if (!structures.isEmpty()) {
						for (Structure s : structures) {
							if (args[0] == s.getName()) {
								player.sendMessage(ChatColor.RED + "[ERROR] " + ChatColor.RESET + "Please use a structure name that's not been made.");
							}
						}
					}
					player.setMetadata("CreatingStructure", new FixedMetadataValue(plugin, "Making-the-best-structure!"));
					player.setMetadata("SturctureName", new FixedMetadataValue(plugin, args[0]));
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "[ERROR] " + ChatColor.RESET + "Please use " + ChatColor.GOLD + "/createStructure <StructureName> " + ChatColor.RESET + "to make a structure.");
					return true;
				}
			} else if (label.equalsIgnoreCase("stopEditing")) {
				if (args.length == 1) {
					player.removeMetadata("CreatingStructure", plugin);
					player.removeMetadata("SturctureName", plugin);
					return true;
				}
				return true;
			} else {
				return false;
			}
		} else if (sender instanceof ConsoleCommandSender) {
//			ConsoleCommandSender console = (ConsoleCommandSender) sender;
			return false;
		} else {
			return false;
		}
	}
}