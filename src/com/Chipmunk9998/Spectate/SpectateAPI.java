package com.Chipmunk9998.Spectate;

import java.util.ArrayList;

import net.minecraft.server.v1_5_R1.Packet16BlockItemSwitch;

import org.bukkit.craftbukkit.v1_5_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class SpectateAPI {

	private static Spectate plugin;


	public static void spectateOn(Player player, Player target) {

		if (getPlugin().CommandExecutor.isSpectating.contains(player.getName())) {

			spectateOff(player);

		}

		if (!plugin.CommandExecutor.isControlling.contains(target.getName())) {

			player.sendMessage("§7You are now spectating " + target.getName() + ".");

		}else {

			plugin.CommandExecutor.mode.remove(player.getName());
			plugin.CommandExecutor.isInv.remove(player.getName());

		}
		
		if (plugin.CommandExecutor.playerAngle.get(player.getName()) == null) {
			
			plugin.CommandExecutor.playerAngle.put(player.getName(), 1);
			
		}

		getPlugin().CommandExecutor.origLocation.put(player.getName(), player.getLocation());
		getPlugin().CommandExecutor.isSpectating.add(player.getName());
		getPlugin().CommandExecutor.isBeingSpectated.add(target.getName());

		if (getPlugin().CommandExecutor.spectator.get(target.getName()) == null) {

			getPlugin().CommandExecutor.spectator.put(target.getName(), player.getName());

		}else {

			getPlugin().CommandExecutor.spectator.put(target.getName(), getPlugin().CommandExecutor.spectator.get(target.getName()) + "," + player.getName());

		}


		getPlugin().CommandExecutor.target.put(player.getName(), target.getName());
		getPlugin().CommandExecutor.senderHunger.put(player.getName(), player.getFoodLevel());
		getPlugin().CommandExecutor.senderHealth.put(player.getName(), player.getHealth());

		if (getPlugin().CommandExecutor.isInv.get(player.getName()) == null || getPlugin().CommandExecutor.isInv.get(player.getName())) {

			getPlugin().CommandExecutor.senderInv.put(player.getName(), player.getInventory().getContents());
			getPlugin().CommandExecutor.senderArm.put(player.getName(), player.getInventory().getArmorContents());

			player.getInventory().clear();
			player.getInventory().setContents(target.getInventory().getContents());
			player.getInventory().setArmorContents(target.getInventory().getArmorContents());

		}

		getPlugin().CommandExecutor.senderSlot.put(player.getName(), player.getInventory().getHeldItemSlot());

		((CraftPlayer)player).getHandle().inventory.itemInHandIndex = target.getInventory().getHeldItemSlot();
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(new Packet16BlockItemSwitch(target.getInventory().getHeldItemSlot()));

		ArrayList<Player> spectateablePlayers = getSpectateablePlayers();

		spectateablePlayers.remove(player);

		int tempPlayerNumber = 0;

		for (Player p : spectateablePlayers) {

			if (getPlugin().CommandExecutor.target.get(player.getName()).equals(p.getName())) {

				break;

			}

			tempPlayerNumber++;

		}

		getPlugin().CommandExecutor.playerNumber.put(player.getName(), tempPlayerNumber);

		if (!plugin.CommandExecutor.isControlling.contains(target.getName())) {

			for (Player player1 : getPlugin().getServer().getOnlinePlayers()) {

				player1.hidePlayer(player);

			}

		}

		if (plugin.CommandExecutor.playerAngle.get(player.getName()) == 1) {

			player.hidePlayer(target);

		}

		if (plugin.CommandExecutor.playerAngle.get(player.getName()) == 2 || plugin.CommandExecutor.playerAngle.get(player.getName()) == 3) {

			player.teleport(plugin.Listener.getThirdPersonLocation(target, (plugin.CommandExecutor.playerAngle.get(player.getName()) == 2 ? false : true)));

		}else {

			player.getPlayer().teleport(getPlugin().getServer().getPlayer(getPlugin().CommandExecutor.target.get(player.getName())));

		}

	}


	public static void spectateOff(Player player) {

		getPlugin().CommandExecutor.isSpectating.remove(player.getName());

		if (getPlugin().CommandExecutor.isInv.get(player.getName()) == null || getPlugin().CommandExecutor.isInv.get(player.getName())) {

			player.getInventory().clear();
			player.getInventory().setContents(getPlugin().CommandExecutor.senderInv.get(player.getName()));
			player.getInventory().setArmorContents(getPlugin().CommandExecutor.senderArm.get(player.getName()));

		}

		player.teleport(getPlugin().CommandExecutor.origLocation.get(player.getName()));

		player.setHealth(getPlugin().CommandExecutor.senderHealth.get(player.getName()));
		player.setFoodLevel(getPlugin().CommandExecutor.senderHunger.get(player.getName()));

		player.setFireTicks(0);

		((CraftPlayer)player).getHandle().inventory.itemInHandIndex = getPlugin().CommandExecutor.senderSlot.get(player.getName());
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(new Packet16BlockItemSwitch(getPlugin().CommandExecutor.senderSlot.get(player.getName())));

		String[] spectators = getPlugin().CommandExecutor.spectator.get(getPlugin().CommandExecutor.target.get(player.getName())).split(",");

		getPlugin().CommandExecutor.spectator.put(getPlugin().CommandExecutor.target.get(player.getName()), null);

		if (spectators.length > 1) {

			for (String players : spectators) {

				if (!players.equals(player.getName())) {

					if (getPlugin().CommandExecutor.spectator.get(getPlugin().CommandExecutor.target.get(player.getName())) == null) {

						getPlugin().CommandExecutor.spectator.put(getPlugin().CommandExecutor.target.get(player.getName()), players);

					}else {

						getPlugin().CommandExecutor.spectator.put(getPlugin().CommandExecutor.target.get(player.getName()), getPlugin().CommandExecutor.spectator.get(getPlugin().CommandExecutor.target.get(player.getName())) + "," + players);

					}

				}

			}

		}else {

			getPlugin().CommandExecutor.isBeingSpectated.remove(getPlugin().CommandExecutor.target.get(player.getName()));

		}

		for (Player p : getPlugin().getServer().getOnlinePlayers()) {

			p.showPlayer(player);

		}

		player.showPlayer(getPlugin().getServer().getPlayer(getPlugin().CommandExecutor.target.get(player.getName())));

		getPlugin().CommandExecutor.senderInv.remove(player.getName());
		getPlugin().CommandExecutor.senderArm.remove(player.getName());
		getPlugin().CommandExecutor.origLocation.remove(player.getName());
		getPlugin().CommandExecutor.senderHealth.remove(player.getName());
		getPlugin().CommandExecutor.senderHunger.remove(player.getName());
		getPlugin().CommandExecutor.target.remove(player.getName());

	}


	public static void scrollLeft(Player player) {

		if (getPlugin().getServer().getOnlinePlayers().length > 2) {

			ArrayList<Player> spectateablePlayers = getSpectateablePlayers();

			spectateablePlayers.remove(player);

			Player[] players = spectateablePlayers.toArray(new Player[spectateablePlayers.size()]);

			int tempPlayerNumber = 0;

			for (Player p : players) {

				if (getPlugin().CommandExecutor.target.get(player.getName()).equals(p.getName())) {

					break;

				}

				tempPlayerNumber++;

			}

			getPlugin().CommandExecutor.playerNumber.put(player.getName(), tempPlayerNumber);

			if (tempPlayerNumber == 0) {

				if (players[players.length - 1].getName().equals(getPlugin().CommandExecutor.target.get(player.getName()))) {

					return;

				}

				spectateOn(player, players[players.length - 1]);
				getPlugin().Listener.clickEnable(player);
				return;

			}

			if (players[tempPlayerNumber - 1].getName().equals(getPlugin().CommandExecutor.target.get(player.getName()))) {

				return;

			}

			spectateOn(player, players[tempPlayerNumber - 1]);
			getPlugin().Listener.clickEnable(player);

		}

	}


	public static void scrollRight(Player player) {

		if (getPlugin().getServer().getOnlinePlayers().length > 2) {

			ArrayList<Player> spectateablePlayers = getSpectateablePlayers();

			spectateablePlayers.remove(player);

			Player[] players = spectateablePlayers.toArray(new Player[spectateablePlayers.size()]);

			int tempPlayerNumber = 0;

			for (Player p : players) {

				if (getPlugin().CommandExecutor.target.get(player.getName()).equals(p.getName())) {

					break;

				}

				tempPlayerNumber++;

			}

			getPlugin().CommandExecutor.playerNumber.put(player.getName(), tempPlayerNumber);

			if (tempPlayerNumber == players.length - 1) {

				if (players[0].getName().equals(getPlugin().CommandExecutor.target.get(player.getName()))) {

					return;

				}

				spectateOn(player, players[0]);
				getPlugin().Listener.clickEnable(player);
				return;

			}

			if (players[tempPlayerNumber + 1].getName().equals(getPlugin().CommandExecutor.target.get(player.getName()))) {

				return;

			}

			spectateOn(player, players[tempPlayerNumber + 1]);
			getPlugin().Listener.clickEnable(player);

		}

	}


	public static void spectateScan(int interval, final Player player) {

		getPlugin().CommandExecutor.taskId.put(player.getName(), getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(getPlugin(), new Runnable() {

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

		for (Player onlinePlayers : getPlugin().getServer().getOnlinePlayers()) {

			if (onlinePlayers.isDead()) {

				continue;

			}

			if (getPlugin().CommandExecutor.isSpectating.contains(onlinePlayers.getName())) {

				continue;

			}

			if (getPlugin().conf.getBoolean("canspectate Permission Enabled?")) {

				if (onlinePlayers.hasPermission("spectate.cantspectate")) {

					continue;

				}

			}

			spectateablePlayers.add(onlinePlayers);

		}

		return spectateablePlayers;

	}

	public static void setMode(String player, String mode) {

		getPlugin().CommandExecutor.mode.put(player, mode);

	}


	public static void setPlugin(Spectate plugin) {

		SpectateAPI.plugin = plugin;

	}


	public static Spectate getPlugin() {

		return plugin;

	}

}
