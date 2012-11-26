package com.Chipmunk9998.Spectate;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

public class SpectateCommandExecutor implements CommandExecutor {

	public Spectate plugin;

	public HashMap<String, Boolean> isSpectating = new HashMap<String, Boolean>();
	public HashMap<String, Boolean> isBeingSpectated = new HashMap<String, Boolean>();
	public HashMap<String, String> spectator = new HashMap<String, String>();
	public HashMap<String, String> target = new HashMap<String, String>();
	public HashMap<String, ItemStack[]> senderInv = new HashMap<String, ItemStack[]>();
	public HashMap<String, ItemStack[]> senderArm = new HashMap<String, ItemStack[]>();
	public HashMap<String, Integer> senderHunger = new HashMap<String, Integer>();
	public HashMap<String, Integer> senderHealth = new HashMap<String, Integer>();
	public HashMap<String, Location> origLocation = new HashMap<String, Location>();
	public HashMap<String, String> mode = new HashMap<String, String>();
	public HashMap<String, Integer> playerNumber = new HashMap<String, Integer>();

	public HashMap<String, Boolean> isScanning = new HashMap<String, Boolean>();
	public HashMap<String, Boolean> isInv = new HashMap<String, Boolean>();

	public HashMap<String, Integer> taskId = new HashMap<String, Integer>();

	public HashMap<String, Boolean> isClick = new HashMap<String, Boolean>();

	public SpectateCommandExecutor(Spectate plugin) {

		this.plugin = plugin;

	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (!(sender instanceof Player)) {

			return true;

		}

		Player cmdsender = (Player) sender;

		if (!cmdsender.hasPermission("spectate.use")) {

			cmdsender.sendMessage("§cYou do not have permission to spectate.");
			return true;

		}

		if (cmd.getName().equalsIgnoreCase("spectate") || cmd.getName().equalsIgnoreCase("spec")) {

			if (args.length > 0) {

				if (args[0].equalsIgnoreCase("off")) {

					if (isSpectating.get(cmdsender.getName()) != null) {

						if (isSpectating.get(cmdsender.getName())) {

							cmdsender.sendMessage("§7You have stopped spectating " + target.get(cmdsender.getName()) + ".");

							SpectateAPI.spectateOff(cmdsender);
							if(Bukkit.getServer().getPluginManager().getPlugin("VanishNoPacket") != null) {
								try {
									VanishNoPacket.toggleVanishSilent(cmdsender);
								} catch (VanishNotLoadedException e) {
									e.printStackTrace();
								}
								try {
									VanishNoPacket.toggleVanishSilent(cmdsender);
								} catch (VanishNotLoadedException e) {
									e.printStackTrace();
								}
							}
							if (plugin.CommandExecutor.isScanning.get(cmdsender.getName()) != null) {

								if (plugin.CommandExecutor.isScanning.get(cmdsender.getName())) {

									plugin.CommandExecutor.isScanning.put(cmdsender.getName(), false);

									plugin.getServer().getScheduler().cancelTask(plugin.CommandExecutor.taskId.get(cmdsender.getName()));

								}

							}

							return true;

						}

					}

					cmdsender.sendMessage("§7You are not currently spectating anyone.");
					return true;

				}

				if (args[0].equalsIgnoreCase("mode")) {

					if (args.length < 2) {

						cmdsender.sendMessage("§cError: You must enter the mode type.");
						return true;

					}

					if (args[1].equalsIgnoreCase("1") || args[1].equalsIgnoreCase("default")) {

						if (mode.get(cmdsender.getName()) == null || mode.get(cmdsender.getName()).equals("1")) {

							cmdsender.sendMessage("§7You are already in this mode.");
							return true;

						}

						mode.put(cmdsender.getName(), "1");
						cmdsender.sendMessage("§7You are now using the default spectate mode.");
						return true;

					}

					if (args[1].equalsIgnoreCase("2") || args[1].equalsIgnoreCase("scroll")) {

						if (mode.get(cmdsender.getName()) == null) {

							mode.put(cmdsender.getName(), "2");
							cmdsender.sendMessage("§7You are now using the scroll spectate mode.");
							return true;

						}

						if (mode.get(cmdsender.getName()).equals("2")) {

							cmdsender.sendMessage("§7You are already in this mode.");
							return true;

						}

						mode.put(cmdsender.getName(), "2");
						cmdsender.sendMessage("§7You are now using the scroll spectate mode.");
						return true;

					}

					return true;

				}

				if (args[0].equalsIgnoreCase("scan")) {

					if (isScanning.get(cmdsender.getName()) != null) {

						if (isScanning.get(cmdsender.getName())) {

							cmdsender.sendMessage("§cError: You are already scanning.");
							return true;

						}

					}

					int interval = 0;

					if (args.length < 2) {

						cmdsender.sendMessage("§cError: You must enter an interval.");
						return true;

					}

					try {

						interval = Integer.parseInt(args[1]);

					}catch (NumberFormatException e) {

						cmdsender.sendMessage("§cError: " + args[1] + " is not a number.");
						return true;

					}

					if (interval <= 0) {

						cmdsender.sendMessage("§cError: Interval must be greater than 0.");
						return true;

					}

					ArrayList<Player> spectateablePlayers = SpectateAPI.getSpectateablePlayers();

					spectateablePlayers.remove(cmdsender);

					Player[] specPlayers = spectateablePlayers.toArray(new Player[spectateablePlayers.size()]);

					try {

						if (isSpectating.get(cmdsender.getName()) == null || !isSpectating.get(cmdsender.getName())) {

							SpectateAPI.spectateOn(cmdsender, specPlayers[0]);

						}

					}catch (ArrayIndexOutOfBoundsException e) {

						cmdsender.sendMessage("§cError: There is nobody to spectate.");
						return true;

					}

					isScanning.put(cmdsender.getName(), true);
					SpectateAPI.spectateScan(interval, cmdsender);
					return true;

				}

				if (args[0].equalsIgnoreCase("inv")) {

					if (isSpectating.get(cmdsender.getName()) != null) {

						if (isSpectating.get(cmdsender.getName())) {

							cmdsender.sendMessage("§cError: You can not change this setting while spectating.");
							return true;

						}

					}

					if (args.length == 2) {

						if (args[0].equals("on")) {

							isInv.put(cmdsender.getName(), true);
							cmdsender.sendMessage(ChatColor.GRAY + "Spectate inventory turned on.");
							return true;

						}else if (args[0].equals("off")) {

							isInv.put(cmdsender.getName(), false);
							cmdsender.sendMessage(ChatColor.GRAY + "Spectate inventory turned off.");
							return true;

						}

					}else {

						if (isInv.get(cmdsender.getName()) == null) {

							isInv.put(cmdsender.getName(), false);
							cmdsender.sendMessage(ChatColor.GRAY + "Spectate inventory turned off.");
							return true;

						}else {

							isInv.put(cmdsender.getName(), !isInv.get(sender.getName()));

							if (isInv.get(cmdsender.getName())) {

								cmdsender.sendMessage(ChatColor.GRAY + "Spectate inventory turned on.");
								return true;

							}else {

								cmdsender.sendMessage(ChatColor.GRAY + "Spectate inventory turned off.");
								return true;

							}

						}

					}

					return true;

				}

				Player targetPlayer = plugin.getServer().getPlayer(args[0]);

				if (targetPlayer != null) {

					if (cmdsender.getName() == targetPlayer.getName()) {

						cmdsender.sendMessage("§7Did you really just try to spectate yourself?");
						return true;

					}

					if (isSpectating.get(cmdsender.getName()) != null) {

						if (isSpectating.get(cmdsender.getName())) {

							if (targetPlayer.getName().equals(target.get(cmdsender.getName()))) {

								cmdsender.sendMessage("§7You are already spectating them.");
								return true;

							}

						}

					}

					if (isSpectating.get(targetPlayer.getName()) != null) {

						if (isSpectating.get(targetPlayer.getName())) {

							cmdsender.sendMessage("§7They are currently spectating someone.");
							return true;

						}

					}

					if (targetPlayer.isDead()) {

						cmdsender.sendMessage("§7They are currently dead.");
						return true;

					}

					if (plugin.conf.getBoolean("canspectate Permission Enabled?") == true) {

						if (targetPlayer.hasPermission("spectate.cantspectate")) {

							cmdsender.sendMessage("§7They can not be spectated.");
							return true;

						}

					}

					SpectateAPI.spectateOn(cmdsender, targetPlayer);
					return true;

				}

				if (args[0].equalsIgnoreCase("herobrine")) {

					cmdsender.sendMessage("§7You can't watch Herobrine, only he can watch you ;)");
					return true;

				}

				cmdsender.sendMessage("§cError: Player is not online§f");
				return true;

			}

			if (isSpectating.get(cmdsender.getName()) != null) {

				if (isSpectating.get(cmdsender.getName())) {

					cmdsender.sendMessage("§7You have stopped spectating " + target.get(cmdsender.getName()) + ".");

					SpectateAPI.spectateOff(cmdsender);

					if (plugin.CommandExecutor.isScanning.get(cmdsender.getName()) != null) {

						if (plugin.CommandExecutor.isScanning.get(cmdsender.getName())) {

							plugin.CommandExecutor.isScanning.put(cmdsender.getName(), false);

							plugin.getServer().getScheduler().cancelTask(plugin.CommandExecutor.taskId.get(cmdsender.getName()));

						}

					}

					return true;

				}

			}

			if (plugin.CommandExecutor.mode.get(cmdsender.getName()) != null) {

				if (plugin.CommandExecutor.mode.get(cmdsender.getName()).equals("2")) {

					ArrayList<Player> spectateablePlayers = SpectateAPI.getSpectateablePlayers();

					spectateablePlayers.remove(cmdsender);

					Player[] players = spectateablePlayers.toArray(new Player[spectateablePlayers.size()]);

					try {

						if (isSpectating.get(cmdsender.getName()) != null) {

							if (isSpectating.get(cmdsender.getName())) {

								if (target.get(cmdsender.getName()) != null) {

									if (players[0].getName().equals(target.get(cmdsender.getName()))) {

										cmdsender.sendMessage("§cError: You are already spectating this player.");
										return true;

									}

								}

							}

						}

						SpectateAPI.spectateOn(cmdsender, players[0]);
						playerNumber.put(cmdsender.getName(), 0);
						return true;

					}catch (ArrayIndexOutOfBoundsException e) {

						cmdsender.sendMessage("§7There is nobody to spectate.");
						return true;

					}

				}

			}

			cmdsender.sendMessage("§cError: No player target§f");
			return true;

		}

		return true;

	}

}
