package com.Chipmunk9998.Spectate;

import com.Chipmunk9998.Spectate.api.SpectateManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Spectate extends JavaPlugin {
	
	//TODO: (IN THE FUTURE - DON'T HAVE TIME FOR THIS RIGHT NOW) Control command
	
	private static SpectateManager Manager;
	
	public boolean cantspectate_permission_enabled = false;
	public boolean disable_commands = false;
	
	public void onEnable() {
		
		Manager = new SpectateManager(this);
		
		boolean convertcantspectate = false;
		boolean convertdisable = false;
		
		File configFile = new File(getDataFolder(), "config.yml");
		
		if (configFile.exists()) {
			
			FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
			
			if (config.get("cantspectate Permission Enabled?") != null) {
				
				convertcantspectate = config.getBoolean("cantspectate Permission Enabled?");
				convertdisable = config.getBoolean("Disable commands while spectating?");
				
				config.set("cantspectate Permission Enabled?", null);
				config.set("Disable commands while spectating?", null);
				
				try {
					
					config.save(configFile);
					
				} catch (IOException e) {
					
					e.printStackTrace();
					
				}
				
			}
			
		}
		
		loadConfig();
		
		if (convertcantspectate || convertdisable) {
			
			File configFile1 = new File(getDataFolder(), "config.yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(configFile1);
			config.set("cantspectate-permission-enabled", convertcantspectate);
			config.set("disable-commands-while-spectating", convertdisable);
			
			try {
				
				config.save(configFile1);
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			
			loadConfig();
			
		}
		
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

        return getServer().getPluginManager().getPlugin("Multiverse-Inventories") != null && getServer().getPluginManager().getPlugin("Multiverse-Inventories").isEnabled();

    }
	
	public void loadConfig() {

		saveDefaultConfig();

		File configFile = new File(getDataFolder(), "config.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

		cantspectate_permission_enabled = config.getBoolean("cantspectate-permission-enabled");
		disable_commands = config.getBoolean("disable-commands-while-spectating");

	}

}
