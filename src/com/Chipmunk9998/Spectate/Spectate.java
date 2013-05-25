package com.Chipmunk9998.Spectate;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.Chipmunk9998.Spectate.api.SpectateManager;

public class Spectate extends JavaPlugin {
	
	//TODO: Scanning
	//TODO: First person/Third person/Third person front/Free roam
	//TODO: Control command
	//TODO: Fix inventory compatibility (Multiverse Inventories, Mob Arena)
	//TODO: Fix players that log in being able to see spectating players (just check in join event)
	//TODO: Config
	
	public void onEnable() {
		
		SpectateManager.setPlugin(this);
		
		getServer().getPluginManager().registerEvents(new SpectateListener(this), this);
		getCommand("spectate").setExecutor(new SpectateCommandExecutor(this));
		SpectateManager.startSpectateTask();
		
	}
	
	public void onDisable() {
		
		for (Player p : SpectateManager.getSpectatingPlayers()) {
			
			SpectateManager.stopSpectating(p, true);
			p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because of a server reload.");
			
		}
		
		SpectateManager.stopSpectateTask();
		
	}

}
