package com.Chipmunk9998.Spectate;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Spectate extends JavaPlugin {

	FileConfiguration conf;

	public SpectateListener Listener = new SpectateListener(this);
	public SpectateCommandExecutor CommandExecutor = new SpectateCommandExecutor(this);
	public SpectateExtras SpectateExtras = new SpectateExtras(this);

	public void onEnable() {

		getServer().getPluginManager().registerEvents(Listener, this);
		
		conf = getConfig();
		
		if (conf.get("canspectate Permission Enabled?") == null) {
			
			conf.set("canspectate Permission Enabled?", false);
			
		}
		
		saveConfig();

		PluginDescriptionFile pdfFile = this.getDescription();

		System.out.println("[" + pdfFile.getName() + "] " + pdfFile.getName() + " v" + pdfFile.getVersion() + " enabled!");
		
		Listener.updatePlayer();

		getCommand("spectate").setExecutor(CommandExecutor);
		getCommand("spectateoff").setExecutor(CommandExecutor);
		getCommand("spec").setExecutor(CommandExecutor);
		getCommand("specoff").setExecutor(CommandExecutor);
		getCommand("spectatemode").setExecutor(CommandExecutor);
		getCommand("specmode").setExecutor(CommandExecutor);
		
	}
	
	public void onDisable() {

		for (Player players : getServer().getOnlinePlayers()) {

			if (CommandExecutor.isSpectating.get(players) != null) {

				if (CommandExecutor.isSpectating.get(players)) {

					players.sendMessage("§7You were forced to stop spectating because of a server reload.");

					SpectateExtras.spectateOff(players);

				}

			}

		}

		System.out.println("Spectate is disabled!");

	}
	
}