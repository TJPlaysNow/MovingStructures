package com.pzg.www.movingstructure.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.erezbiox1.CommandsAPI.Command;
import com.erezbiox1.CommandsAPI.CommandListener;
import com.erezbiox1.CommandsAPI.CommandManager;
import com.pzg.www.api.config.Config;
import com.pzg.www.api.config.ConfigCreate;
import com.pzg.www.movingstructure.main.objects.Structure;
import com.pzg.www.movingstructure.main.objects.StructureState;

import net.md_5.bungee.api.ChatColor;

public class PluginMain extends JavaPlugin implements Listener, CommandListener {
	
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
		CommandManager.register(this);
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
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (playerEditingStructure.containsKey(player)) {
			player.sendMessage(ChatColor.GOLD + "Attempting to build off of " + ChatColor.AQUA + playerEditingStructure.get(player));
			for (Structure s : structures) {
				if (s.getName() == playerEditingStructure.get(player)) {
					if (s.getBlocks().contains(e.getBlock())) {
						player.sendMessage(ChatColor.DARK_GREEN + "You removed a block!");
						s.removeBlock(e.getBlock());
					}
				} else {
					continue;
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	@Command (name = "structure", arguments = "create *", permission = "structure.create")
	public void createStructure(Player player, String[] args) {
		if (!structures.isEmpty()) {
			player.sendMessage("It's not empty");
			for (Structure s : structures) {
				player.sendMessage("Looping structure " + s.getName());
				if (args[0].equalsIgnoreCase(s.getName())) {
					player.sendMessage(ChatColor.RED + "[ERROR] " + ChatColor.RESET + "Please use a structure name that's not been made.");
					break;
				} else {
					player.sendMessage("It's not empty");
					Structure structure = new Structure(args[0]);
					structures.add(structure);
					player.sendMessage(ChatColor.DARK_GREEN + "You are now creating a structure!");
					playerEditingStructure.put(player, args[0]);
					break;
				}
			}
		} else {
			player.sendMessage("It's empty");
			Structure structure = new Structure(args[0]);
			structures.add(structure);
			player.sendMessage(ChatColor.DARK_GREEN + "You are now creating a structure!");
			playerEditingStructure.put(player, args[0]);
		}
	}

	@Command (name = "structure", arguments = "edit *", permission = "structure.edit")
	public void editStructure(Player player, String[] args) {
		
	}

	@Command (name = "structure", arguments = "delete *", permission = "structure.edit")
	public void deleteStructure(Player player, String[] args) {
		
	}
	
	@Command (name = "structure", arguments = "list", permission = "structure.edit")
	public void listStructure(Player player, String[] args) {
	}
	
	@Command (name = "structure", arguments = "list", permission = "structure.edit")
	public void listStructure(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
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
		} else if (sender instanceof ConsoleCommandSender) {
			ConsoleCommandSender console = (ConsoleCommandSender) sender;
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
		} else {}
	}
	
	@Command (name = "structure", arguments = "stop editing", permission = "structure.edit")
	public void stopEditingStructure(Player player, String[] args) {
		for (Structure structure : structures) {
			if (!playerEditingStructure.isEmpty()) {
				if (playerEditingStructure.get(player).equalsIgnoreCase(structure.getName())) {
					structure.saveConfig();
					player.sendMessage(ChatColor.DARK_GREEN + "You are no longer editing the structure.");
				}
			} else player.sendMessage(ChatColor.RED + "You weren't creating a structure.");
		}
		playerEditingStructure.remove(player);
	}
}