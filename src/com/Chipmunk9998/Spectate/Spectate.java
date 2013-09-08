package com.Chipmunk9998.Spectate;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.Chipmunk9998.Spectate.api.SpectateManager;

public class Spectate extends JavaPlugin {
	
	//TODO: Control command
	//TODO: Fix inventory compatibility (Multiverse Inventories, Mob Arena)
	//TODO: Config
	
	static SpectateManager Manager;
	
	public void onEnable() {
		
		Manager = new SpectateManager(this);
		
		getServer().getPluginManager().registerEvents(new SpectateListener(), this);
		getCommand("spectate").setExecutor(new SpectateCommandExecutor());
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

}
