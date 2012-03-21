package com.Chipmunk9998.Spectate;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Spectate extends JavaPlugin {

	FileConfiguration conf;

	public SpectateListener Listener = new SpectateListener(this);
	public SpectateCommandExecutor CommandExecutor = new SpectateCommandExecutor(this);
	public SpectateOff SpectateOff = new SpectateOff(this);

	public void onDisable() {

		for (Player players : getServer().getOnlinePlayers()) {

			if (CommandExecutor.isSpectating.get(players) != null) {

				if (CommandExecutor.isSpectating.get(players)) {

					players.sendMessage("§7You were forced to stop spectating because of a server reload.");

					SpectateOff.spectateOff(players);

				}

			}

		}

		System.out.println("Spectate is disabled!");

	}

	public void onEnable() {

		getServer().getPluginManager().registerEvents(Listener, this);

		PluginDescriptionFile pdfFile = this.getDescription();

		System.out.println("[" + pdfFile.getName() + "] " + " v" + pdfFile.getVersion() + " enabled!");

		conf = getConfig();

		if (conf.get("Enable Permissions?") == null) {

			conf.set("Enable Permissions?", true);
			saveConfig();

		}
		
		Listener.updatePitchAndYaw();

		getCommand("spectate").setExecutor(CommandExecutor);
		getCommand("spectateoff").setExecutor(CommandExecutor);
		getCommand("spec").setExecutor(CommandExecutor);
		getCommand("specoff").setExecutor(CommandExecutor);
	}
}