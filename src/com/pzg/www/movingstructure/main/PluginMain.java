package com.pzg.www.movingstructure.main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.pzg.www.api.config.Config;
import com.pzg.www.movingstructure.main.objects.Structure;
import com.pzg.www.movingstructure.main.objects.StructureState;

public class PluginMain extends JavaPlugin {
	
	public static Logger logger;
	public static Plugin plugin;
	public static Config config;
	public static List<Structure> structures;
	
	@Override
	public void onEnable() {
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
}