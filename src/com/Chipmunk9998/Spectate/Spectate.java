package com.Chipmunk9998.Spectate;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Spectate extends JavaPlugin {

	FileConfiguration conf;

	public SpectateListener Listener = new SpectateListener(this);
	public SpectateCommandExecutor CommandExecutor = new SpectateCommandExecutor(this);
	public SpectateAPI SpectateExtras = new SpectateAPI(this);

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
		getCommand("spec").setExecutor(CommandExecutor);
		
	}
	
	public void onDisable() {

		for (Player players : getServer().getOnlinePlayers()) {

			if (CommandExecutor.isSpectating.get(players.getName()) != null) {

				if (CommandExecutor.isSpectating.get(players.getName())) {

					players.sendMessage("§7You were forced to stop spectating because of a server reload.");
					
					if (CommandExecutor.isScrolling.get(players.getName()) != null) {

						if (CommandExecutor.isScrolling.get(players.getName())) {

							CommandExecutor.isScrolling.put(players.getName(), false);
							
							getServer().getScheduler().cancelTask(CommandExecutor.taskId.get(players.getName()));

						}

					}

					SpectateExtras.spectateOff(players);

				}

			}

		}

		System.out.println("Spectate is disabled!");

	}
	
}