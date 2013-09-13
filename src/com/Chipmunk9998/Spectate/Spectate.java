package com.Chipmunk9998.Spectate;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.Chipmunk9998.Spectate.api.SpectateManager;

public class Spectate extends JavaPlugin {
	
	//TODO: (IN THE FUTURE - DON'T HAVE TIME FOR THIS RIGHT NOW) Control command
	
	static SpectateManager Manager;
	
	public boolean cantspectate_permission_enabled = false;
	public boolean disable_commands = false;
	
	public void onEnable() {
		
		Manager = new SpectateManager(this);
		
		loadConfig();
		
		getServer().getPluginManager().registerEvents(new SpectateListener(this), this);
		getCommand("spectate").setExecutor(new SpectateCommandExecutor(this));
		getAPI().startSpectateTask();
		
	}
	
	public void onDisable() {
		
		for (Player p : getAPI().getSpectatingPlayers()) {
			
			getAPI().stopSpectating(p, true);
			p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because of a server reload.");
			
		}
		
		getAPI().stopSpectateTask();
		
	}
	
	public static SpectateManager getAPI() {
		
		return Manager;
		
	}
	
	public boolean multiverseInvEnabled() {
		
		if (getServer().getPluginManager().getPlugin("Multiverse-Inventories") != null && getServer().getPluginManager().getPlugin("Multiverse-Inventories").isEnabled()) {
			
			return true;
			
		}
		
		return false;
		
	}
	
	public void loadConfig() {

		saveDefaultConfig();

		File tutorialFile = new File(getDataFolder(), "config.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(tutorialFile);

		cantspectate_permission_enabled = config.getBoolean("cantspectate-permission-enabled");
		disable_commands = config.getBoolean("disable-commands-while-spectating");

	}

}
