package com.pzg.www.movingstructure.main;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.pzg.www.api.config.Config;

public class PluginMain extends JavaPlugin {
	
	public static Logger logger;
	public static Plugin plugin;
	public static Config config;
	public static List<Config> structures;
	
	@Override
	public void onEnable() {
		logger = getLogger();
		plugin = this;
		config = new Config("plugins/StructureMover", "config.yml", plugin);
		for (int i = 0; i <= config.getConfig().getInt("structures.ammount"); i++) {
			String fileName = config.getConfig().getString("structure.file." + i);
			Config structure = new Config("plugins/StructureMover/Structures", fileName + ".yml", plugin);
			structures.add(structure);
		}
	}
	
}