package com.Chipmunk9998.Spectate;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class SpectateExtras {

	public Spectate plugin;

	public SpectateExtras(Spectate plugin) {

		this.plugin = plugin;

	}


	public void spectateOn(final Player player, final Player target) {

		if (plugin.CommandExecutor.isSpectating.get(player) != null) {

			if (plugin.CommandExecutor.isSpectating.get(player)) {

				plugin.SpectateExtras.spectateOff(player);

			}

		}

		player.sendMessage("§7You are now spectating " + target.getName() + ".");
		plugin.CommandExecutor.origLocation.put(player, player.getLocation());
		plugin.CommandExecutor.isSpectating.put(player, true);
		plugin.CommandExecutor.isBeingSpectated.put(target, true);

		if (plugin.CommandExecutor.spectator.get(target) == null) {

			plugin.CommandExecutor.spectator.put(target, player.getName());

		}else {

			plugin.CommandExecutor.spectator.put(target, plugin.CommandExecutor.spectator.get(target) + "," + player.getName());

		}


		plugin.CommandExecutor.target.put(player, target);
		player.getPlayer().teleport(plugin.CommandExecutor.target.get(player));
		plugin.CommandExecutor.senderInv.put(player, player.getInventory().getContents());
		plugin.CommandExecutor.senderArm.put(player, player.getInventory().getArmorContents());
		plugin.CommandExecutor.senderHunger.put(player, player.getFoodLevel());
		plugin.CommandExecutor.senderHealth.put(player, player.getHealth());

		player.getInventory().clear();
		player.getInventory().setContents(target.getInventory().getContents());
		player.getInventory().setArmorContents(target.getInventory().getArmorContents());

		for (Player player1 : plugin.getServer().getOnlinePlayers()) {

			player1.hidePlayer(player);

		}

		target.hidePlayer(player);
		player.hidePlayer(target);

	}


	public void spectateOff(final Player player) {

		plugin.CommandExecutor.isSpectating.put(player, false);

		player.getInventory().clear();
		player.getInventory().setContents(plugin.CommandExecutor.senderInv.get(player));
		player.getInventory().setArmorContents(plugin.CommandExecutor.senderArm.get(player));

		player.teleport(plugin.CommandExecutor.origLocation.get(player));

		player.setHealth(plugin.CommandExecutor.senderHealth.get(player));
		player.setFoodLevel(plugin.CommandExecutor.senderHunger.get(player));
		
		player.setFireTicks(0);

		String[] spectators = plugin.CommandExecutor.spectator.get(plugin.CommandExecutor.target.get(player)).split(",");

		plugin.CommandExecutor.spectator.put(plugin.CommandExecutor.target.get(player), null);

		if (spectators.length > 1) {

			for (String players : spectators) {

				if (!players.equals(player.getName())) {

					if (plugin.CommandExecutor.spectator.get(plugin.CommandExecutor.target.get(player)) == null) {

						plugin.CommandExecutor.spectator.put(plugin.CommandExecutor.target.get(player), players);

					}else {

						plugin.CommandExecutor.spectator.put(plugin.CommandExecutor.target.get(player), plugin.CommandExecutor.spectator.get(plugin.CommandExecutor.target.get(player)) + "," + players);

					}

				}

			}

		}else {

			plugin.CommandExecutor.isBeingSpectated.put(plugin.CommandExecutor.target.get(player), false);

		}

		for (Player p : plugin.getServer().getOnlinePlayers()) {

			p.showPlayer(player);

		}

		player.showPlayer(plugin.CommandExecutor.target.get(player));
		plugin.CommandExecutor.target.get(player).showPlayer(player);

	}


	public void scrollLeft(Player player) {

		if (plugin.getServer().getOnlinePlayers().length > 2) {

			ArrayList<Player> spectateablePlayers = new ArrayList<Player>();

			for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {

				if (!onlinePlayers.isDead() && onlinePlayers != player) {

					if (plugin.CommandExecutor.isSpectating.get(onlinePlayers) != null) {

						if (plugin.CommandExecutor.isSpectating.get(onlinePlayers)) {

							continue;

						}

					}

					if (plugin.conf.getBoolean("canspectate Permission Enabled?") == true) {

						if (!onlinePlayers.hasPermission("spectate.cantspectate")) {

							continue;

						}

					}

					spectateablePlayers.add(onlinePlayers);

				}

			}

			Player[] players = spectateablePlayers.toArray(new Player[spectateablePlayers.size()]);

			int tempPlayerNumber = 0;

			for (Player p : players) {

				if (plugin.CommandExecutor.target.get(player) == p) {

					break;

				}

				tempPlayerNumber++;

			}
			
			plugin.CommandExecutor.playerNumber.put(player, tempPlayerNumber);

			if (tempPlayerNumber == 0) {
				
				if (players[players.length - 1] == plugin.CommandExecutor.target.get(player)) {
					
					return;
					
				}

				plugin.SpectateExtras.spectateOn(player, players[players.length - 1]);
				plugin.Listener.clickEnable(player);
				return;

			}
			
			if (players[tempPlayerNumber - 1] == plugin.CommandExecutor.target.get(player)) {
				
				return;
				
			}

			plugin.SpectateExtras.spectateOn(player, players[tempPlayerNumber - 1]);
			plugin.Listener.clickEnable(player);

		}

	}
	
	
	public void scrollRight(Player player) {

		ArrayList<Player> spectateablePlayers = new ArrayList<Player>();

		for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {

			if (!onlinePlayers.isDead() && onlinePlayers != player) {

				if (plugin.CommandExecutor.isSpectating.get(onlinePlayers) != null) {

					if (plugin.CommandExecutor.isSpectating.get(onlinePlayers)) {

						continue;

					}

				}

				if (plugin.conf.getBoolean("canspectate Permission Enabled?") == true) {

					if (!onlinePlayers.hasPermission("spectate.cantspectate")) {

						continue;

					}

				}

				spectateablePlayers.add(onlinePlayers);

			}

		}

		Player[] players = spectateablePlayers.toArray(new Player[spectateablePlayers.size()]);

		int tempPlayerNumber = 0;

		for (Player p : players) {

			if (plugin.CommandExecutor.target.get(player) == p) {

				break;

			}

			tempPlayerNumber++;

		}
		
		plugin.CommandExecutor.playerNumber.put(player, tempPlayerNumber);

		if (tempPlayerNumber == players.length - 1) {
			
			if (players[0] == plugin.CommandExecutor.target.get(player)) {
				
				return;
				
			}

			plugin.SpectateExtras.spectateOn(player, players[0]);
			plugin.Listener.clickEnable(player);
			return;

		}
		
		if (players[tempPlayerNumber + 1] == plugin.CommandExecutor.target.get(player)) {
			
			return;
			
		}

		plugin.SpectateExtras.spectateOn(player, players[tempPlayerNumber + 1]);
		plugin.Listener.clickEnable(player);

	}



	public void spectateScan(int interval) {

		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			public void run() {



			}

		}, 0L, interval * 20L);

	}

}
