package com.Chipmunk9998.Spectate;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class SpectateAPI {

	private static Spectate plugin;

	public static void spectateOn(final Player player, final Player target) {

		if (plugin.CommandExecutor.isSpectating.get(player.getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(player.getName())) {

				spectateOff(player);

			}

		}

		player.sendMessage("§7You are now spectating " + target.getName() + ".");
		plugin.CommandExecutor.origLocation.put(player.getName(), player.getLocation());
		plugin.CommandExecutor.isSpectating.put(player.getName(), true);
		plugin.CommandExecutor.isBeingSpectated.put(target.getName(), true);

		if (plugin.CommandExecutor.spectator.get(target.getName()) == null) {

			plugin.CommandExecutor.spectator.put(target.getName(), player.getName());

		}else {

			plugin.CommandExecutor.spectator.put(target.getName(), plugin.CommandExecutor.spectator.get(target.getName()) + "," + player.getName());

		}


		plugin.CommandExecutor.target.put(player.getName(), target.getName());
		player.getPlayer().teleport(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(player.getName())));
		plugin.CommandExecutor.senderHunger.put(player.getName(), player.getFoodLevel());
		plugin.CommandExecutor.senderHealth.put(player.getName(), player.getHealth());

		if (plugin.CommandExecutor.isInv.get(player.getName()) == null || plugin.CommandExecutor.isInv.get(player.getName())) {

			plugin.CommandExecutor.senderInv.put(player.getName(), player.getInventory().getContents());
			plugin.CommandExecutor.senderArm.put(player.getName(), player.getInventory().getArmorContents());

			player.getInventory().clear();
			player.getInventory().setContents(target.getInventory().getContents());
			player.getInventory().setArmorContents(target.getInventory().getArmorContents());

		}

		ArrayList<Player> spectateablePlayers = getSpectateablePlayers();

		spectateablePlayers.remove(player);

		int tempPlayerNumber = 0;

		for (Player p : spectateablePlayers) {

			if (plugin.CommandExecutor.target.get(player.getName()).equals(p.getName())) {

				break;

			}

			tempPlayerNumber++;

		}

		plugin.CommandExecutor.playerNumber.put(player.getName(), tempPlayerNumber);

		for (Player player1 : plugin.getServer().getOnlinePlayers()) {

			player1.hidePlayer(player);

		}

		target.hidePlayer(player);
		player.hidePlayer(target);

	}


	public static void spectateOff(final Player player) {

		plugin.CommandExecutor.isSpectating.put(player.getName(), false);

		if (plugin.CommandExecutor.isInv.get(player.getName()) == null || plugin.CommandExecutor.isInv.get(player.getName())) {

			player.getInventory().clear();
			player.getInventory().setContents(plugin.CommandExecutor.senderInv.get(player.getName()));
			player.getInventory().setArmorContents(plugin.CommandExecutor.senderArm.get(player.getName()));

		}

		player.teleport(plugin.CommandExecutor.origLocation.get(player.getName()));

		player.setHealth(plugin.CommandExecutor.senderHealth.get(player.getName()));
		player.setFoodLevel(plugin.CommandExecutor.senderHunger.get(player.getName()));

		player.setFireTicks(0);

		String[] spectators = plugin.CommandExecutor.spectator.get(plugin.CommandExecutor.target.get(player.getName())).split(",");

		plugin.CommandExecutor.spectator.put(plugin.CommandExecutor.target.get(player.getName()), null);

		if (spectators.length > 1) {

			for (String players : spectators) {

				if (!players.equals(player.getName())) {

					if (plugin.CommandExecutor.spectator.get(plugin.CommandExecutor.target.get(player.getName())) == null) {

						plugin.CommandExecutor.spectator.put(plugin.CommandExecutor.target.get(player.getName()), players);

					}else {

						plugin.CommandExecutor.spectator.put(plugin.CommandExecutor.target.get(player.getName()), plugin.CommandExecutor.spectator.get(plugin.CommandExecutor.target.get(player.getName())) + "," + players);

					}

				}

			}

		}else {

			plugin.CommandExecutor.isBeingSpectated.put(plugin.CommandExecutor.target.get(player.getName()), false);

		}

		for (Player p : plugin.getServer().getOnlinePlayers()) {

			p.showPlayer(player);

		}
		
		player.showPlayer(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(player.getName())));

	}


	public static void scrollLeft(Player player) {

		if (plugin.getServer().getOnlinePlayers().length > 2) {

			ArrayList<Player> spectateablePlayers = getSpectateablePlayers();

			spectateablePlayers.remove(player);

			Player[] players = spectateablePlayers.toArray(new Player[spectateablePlayers.size()]);

			int tempPlayerNumber = 0;

			for (Player p : players) {

				if (plugin.CommandExecutor.target.get(player.getName()).equals(p.getName())) {

					break;

				}

				tempPlayerNumber++;

			}

			plugin.CommandExecutor.playerNumber.put(player.getName(), tempPlayerNumber);

			if (tempPlayerNumber == 0) {

				if (players[players.length - 1].getName().equals(plugin.CommandExecutor.target.get(player.getName()))) {

					return;

				}

				spectateOn(player, players[players.length - 1]);
				plugin.Listener.clickEnable(player);
				return;

			}

			if (players[tempPlayerNumber - 1].getName().equals(plugin.CommandExecutor.target.get(player.getName()))) {

				return;

			}

			spectateOn(player, players[tempPlayerNumber - 1]);
			plugin.Listener.clickEnable(player);

		}

	}


	public static void scrollRight(Player player) {

		if (plugin.getServer().getOnlinePlayers().length > 2) {

			ArrayList<Player> spectateablePlayers = getSpectateablePlayers();

			spectateablePlayers.remove(player);

			Player[] players = spectateablePlayers.toArray(new Player[spectateablePlayers.size()]);

			int tempPlayerNumber = 0;

			for (Player p : players) {

				if (plugin.CommandExecutor.target.get(player.getName()).equals(p.getName())) {

					break;

				}

				tempPlayerNumber++;

			}

			plugin.CommandExecutor.playerNumber.put(player.getName(), tempPlayerNumber);

			if (tempPlayerNumber == players.length - 1) {

				if (players[0].getName().equals(plugin.CommandExecutor.target.get(player.getName()))) {

					return;

				}

				spectateOn(player, players[0]);
				plugin.Listener.clickEnable(player);
				return;

			}

			if (players[tempPlayerNumber + 1].getName().equals(plugin.CommandExecutor.target.get(player.getName()))) {

				return;

			}

			spectateOn(player, players[tempPlayerNumber + 1]);
			plugin.Listener.clickEnable(player);

		}

	}


	public static void spectateScan(int interval, final Player player) {

		plugin.CommandExecutor.taskId.put(player.getName(), plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			public void run() {

				ArrayList<Player> spectateablePlayers = getSpectateablePlayers();

				spectateablePlayers.remove(player);

				if (spectateablePlayers.size() > 1) {

					scrollRight(player);

				}

			}

		}, interval * 20L, interval * 20L));

	}


	public static ArrayList<Player> getSpectateablePlayers() {

		ArrayList<Player> spectateablePlayers = new ArrayList<Player>();

		for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {

			if (onlinePlayers.isDead()) {

				continue;

			}

			if (plugin.CommandExecutor.isSpectating.get(onlinePlayers.getName()) != null) {

				if (plugin.CommandExecutor.isSpectating.get(onlinePlayers.getName())) {

					continue;

				}

			}

			if (plugin.conf.getBoolean("canspectate Permission Enabled?")) {

				if (onlinePlayers.hasPermission("spectate.cantspectate")) {

					continue;

				}

			}

			spectateablePlayers.add(onlinePlayers);

		}

		return spectateablePlayers;

	}

}
