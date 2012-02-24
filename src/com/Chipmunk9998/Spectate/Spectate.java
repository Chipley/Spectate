package com.Chipmunk9998.Spectate;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Spectate extends JavaPlugin {

	FileConfiguration conf;

	private final SpectateListener Listener = new SpectateListener(this);
	final SpectateCommandExecutor CommandExecutor = new SpectateCommandExecutor(this);

	public void onDisable() {

		for (Player players : getServer().getOnlinePlayers()) {

			if (CommandExecutor.isSpectating.get(players) != null) {

				if (CommandExecutor.isSpectating.get(players)) {

					players.sendMessage("§7You were forced to stop spectating because of a server reload.");

					players.teleport(CommandExecutor.origLocation.get(players));
					CommandExecutor.isSpectating.put(players, false);
					CommandExecutor.isBeingSpectated.put(CommandExecutor.target.get(players), false);
					players.getInventory().clear();
					players.getInventory().setContents(CommandExecutor.senderInv.get(players));
					players.getInventory().setArmorContents(CommandExecutor.senderArm.get(players));
					players.setHealth(CommandExecutor.senderHealth.get(players));
					players.getPlayer().setFoodLevel(CommandExecutor.senderHunger.get(players));

					for (Player playp : players.getWorld().getPlayers()) {

						players.showPlayer(playp);

					}

				}

			}

		}

		System.out.println("Spectate is disabled!");

	}

	public void onEnable() {

		getServer().getPluginManager().registerEvents(Listener, this);

		PluginDescriptionFile pdfFile = this.getDescription();

		System.out.println(pdfFile.getName() + " v" + pdfFile.getVersion() + " enabled!");

		conf = getConfig();

		if (conf.get("Enable Permissions?") == null) {

			conf.set("Enable Permissions?", true);
			saveConfig();

		}

		getCommand("spectate").setExecutor(CommandExecutor);
		getCommand("spectateoff").setExecutor(CommandExecutor);
	}
}