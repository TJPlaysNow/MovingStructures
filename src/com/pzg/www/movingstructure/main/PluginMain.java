package com.pzg.www.movingstructure.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.pzg.www.api.config.Config;
import com.pzg.www.api.config.ConfigCreate;
import com.pzg.www.movingstructure.main.objects.Structure;
import com.pzg.www.movingstructure.main.objects.StructureState;

public class PluginMain extends JavaPlugin implements Listener {
	
	public static Logger logger;
	public static Plugin plugin;
	public static Config config;
	public static List<Structure> structures;
	public static boolean firstCreation;
	
	protected static HashMap<Player, String> playerEditingStructure = new HashMap<Player, String>();
	
	@Override
	public void onEnable() {
		logger = getLogger();
		plugin = this;
		structures = new ArrayList<Structure>();
		Bukkit.getPluginManager().registerEvents(this, plugin);
		config = new Config("plugins/StructureMover", "config.yml", new ConfigCreate() {
			@Override
			public void configCreate() {
				firstCreation = true;
			}
		}, plugin);
		if (firstCreation) {
			config.getConfig().set("structures.ammount", 0);
			config.saveConfig();
		}
		for (int i = 0; i <= config.getConfig().getInt("structures.ammount"); i++) {
			if (i <= 0) continue;
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
		if (!structures.isEmpty()) {
			for (Structure structure : structures) {
				counter++;
				structure.setState(StructureState.Build);
				String name = structure.getName();
				names.add(name);
			}
		}
		config.getConfig().set("structures.ammount", counter);
		config.saveConfig();
		for (int i = 0; i <= counter; i++) {
			if (i <= 0) continue;
			config.getConfig().set("structure.file." + i, names.get(i - 1));
			config.saveConfig();
		}
	}
	
	@EventHandler
	public void onBlockPlaced(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		player.sendMessage("Placed block");
		if (playerEditingStructure.containsKey(player)) {
			player.sendMessage(ChatColor.GOLD + "Attempting to build off of " + ChatColor.AQUA + playerEditingStructure.get(player));
			for (Structure s : structures) {
				if (s.getName() == playerEditingStructure.get(player)) {
					player.sendMessage(ChatColor.DARK_GREEN + "You added a new block!");
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
					Structure structure = new Structure(args[0]);
					structures.add(structure);
					player.sendMessage(ChatColor.DARK_GREEN + "You are now creating a structure!");
					playerEditingStructure.put(player, args[0]);
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "[ERROR] " + ChatColor.RESET + "Please use " + ChatColor.GOLD + "/createStructure <StructureName> " + ChatColor.RESET + "to make a structure.");
					return true;
				}
			} else if (label.equalsIgnoreCase("stopEditing")) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.DARK_GREEN + "You are no longer editing the structure.");
					for (Structure structure : structures) {
						if (playerEditingStructure.get(player).equalsIgnoreCase(structure.getName())) {
							structure.saveConfig();
						}
					}
					playerEditingStructure.remove(player);
					return true;
				}
				return true;
			} else if (label.equalsIgnoreCase("listStructures")) {
				player.sendMessage(ChatColor.GOLD + "Structures : ");
				player.sendMessage(ChatColor.GRAY + "-------------");
				if (structures.isEmpty()) {
					player.sendMessage(ChatColor.RED + "There are no structures to be listed!");
				} else {
					for (int i = 0; i <= structures.size(); i++) {
						if (i > 10) break;
						player.sendMessage(ChatColor.GRAY + "- " + ChatColor.GREEN + structures.get(i).getName());
					}
				}
				player.sendMessage(ChatColor.GRAY + "-------------");
				return true;
			} else {
				return false;
			}
		} else if (sender instanceof ConsoleCommandSender) {
			ConsoleCommandSender console = (ConsoleCommandSender) sender;
			if (label.equalsIgnoreCase("listStructures")) {
				console.sendMessage(ChatColor.GOLD + "Structures : ");
				console.sendMessage(ChatColor.GRAY + "-------------");
				if (structures.isEmpty()) {
					console.sendMessage(ChatColor.RED + "There are no structures to be listed!");
				} else {
					for (Structure structure : structures) {
						console.sendMessage(ChatColor.GRAY + "- " + ChatColor.GREEN + structure.getName());
					}
				}
				console.sendMessage(ChatColor.GRAY + "-------------");
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}