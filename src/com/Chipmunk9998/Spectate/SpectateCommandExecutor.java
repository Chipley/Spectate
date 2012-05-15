package com.Chipmunk9998.Spectate;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpectateCommandExecutor implements CommandExecutor {

	public Spectate plugin;

	public HashMap<Player, Boolean> isSpectating = new HashMap<Player, Boolean>();
	public HashMap<Player, Boolean> isBeingSpectated = new HashMap<Player, Boolean>();
	public HashMap<Player, String> spectator = new HashMap<Player, String>();
	public HashMap<Player, Player> target = new HashMap<Player, Player>();
	public HashMap<Player, ItemStack[]> senderInv = new HashMap<Player, ItemStack[]>();
	public HashMap<Player, ItemStack[]> senderArm = new HashMap<Player, ItemStack[]>();
	public HashMap<Player, Integer> senderHunger = new HashMap<Player, Integer>();
	public HashMap<Player, Integer> senderHealth = new HashMap<Player, Integer>();
	public HashMap<Player, Location> origLocation = new HashMap<Player, Location>();
	public HashMap<Player, String> mode = new HashMap<Player, String>();
	public HashMap<Player, Integer> playerNumber = new HashMap<Player, Integer>();

	public HashMap<Player, Boolean> isClick = new HashMap<Player, Boolean>();

	String cmdtarget;
	Player targetPlayer;


	public SpectateCommandExecutor(Spectate plugin) {

		this.plugin = plugin;

	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (!(sender instanceof Player)) {

			return true;

		}

		final Player cmdsender = (Player) sender;

		if (!cmdsender.hasPermission("spectate.use")) {

			cmdsender.sendMessage("§cYou do not have permission to spectate.");
			return true;

		}

		if (cmd.getName().equalsIgnoreCase("spectate") || cmd.getName().equalsIgnoreCase("spec")) {

			if (args.length > 0) {

				cmdtarget = args[0];
				targetPlayer = Bukkit.getPlayer(cmdtarget);

				if (args[0].equalsIgnoreCase("herobrine") && targetPlayer == null) {

					cmdsender.sendMessage("§7You can't watch Herobrine, only he can watch you ;)");
					return true;

				}

				if (targetPlayer != null) {

					if (cmdsender.getName() == targetPlayer.getName()) {

						cmdsender.sendMessage("§7Did you really just try to spectate yourself?");
						return true;

					}

					if (isSpectating.get(cmdsender) != null) {

						if (isSpectating.get(cmdsender)) {

							if (targetPlayer.getName() == target.get(cmdsender).getName()) {

								cmdsender.sendMessage("§7You are already spectating them.");
								return true;

							}

						}

					}

					if (isSpectating.get(targetPlayer) != null) {

						if (isSpectating.get(targetPlayer)) {

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

					ArrayList<Player> spectateablePlayers = new ArrayList<Player>();

					for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {

						if (!onlinePlayers.isDead() && onlinePlayers != cmdsender) {

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

					int tempPlayerNumber = 0;

					for (Player p : spectateablePlayers) {

						if (plugin.CommandExecutor.target.get(cmdsender) == p) {

							break;

						}

						tempPlayerNumber++;

					}
					
					plugin.CommandExecutor.playerNumber.put(cmdsender, tempPlayerNumber);
					plugin.SpectateExtras.spectateOn(cmdsender, targetPlayer);
					return true;

				}

				cmdsender.sendMessage("§cError: Player is not online§f");
				return true;

			}else if (plugin.CommandExecutor.mode.get(cmdsender) != null) {

				if (plugin.CommandExecutor.mode.get(cmdsender).equals("2")) {

					ArrayList<Player> spectateablePlayers = new ArrayList<Player>();

					for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {

						if (!onlinePlayers.isDead() && onlinePlayers != cmdsender) {

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

					if (isSpectating.get(cmdsender) != null) {

						if (isSpectating.get(cmdsender)) {

							if (target.get(cmdsender) != null) {

								if (players[0] == target.get(cmdsender)) {

									cmdsender.sendMessage("§cError: You are already spectating this player.");
									return true;

								}

							}

						}

					}

					plugin.SpectateExtras.spectateOn(cmdsender, players[0]);
					playerNumber.put(cmdsender, 0);
					return true;

				}

			}

			cmdsender.sendMessage("§cError: No player target§f");
			return true;

		}

		if (cmd.getName().equalsIgnoreCase("spectateoff") || cmd.getName().equalsIgnoreCase("specoff")) {

			if (isSpectating.get(cmdsender) != null) {

				if (isSpectating.get(cmdsender)) {

					plugin.SpectateExtras.spectateOff(cmdsender);

					return true;

				}

			}

			cmdsender.sendMessage("§7You are currently not spectating anyone§f");
			return true;

		}

		if (cmd.getName().equalsIgnoreCase("spectatemode") || cmd.getName().equalsIgnoreCase("specmode")) {

			if (args.length < 1) {

				cmdsender.sendMessage("§cError: You must enter the mode type.");
				return true;

			}

			if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("default")) {

				if (mode.get(cmdsender) == null) {

					cmdsender.sendMessage("§7You are already in this mode.");
					return true;

				}

				if (mode.get(cmdsender).equals("1")) {

					cmdsender.sendMessage("§7You are already in this mode.");
					return true;

				}

				mode.put(cmdsender, "1");
				cmdsender.sendMessage("§7You are now using the default spectate mode.");
				return true;

			}

			if (args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("scroll")) {

				if (mode.get(cmdsender) == null) {

					mode.put(cmdsender, "2");
					cmdsender.sendMessage("§7You are now using the scroll spectate mode.");
					return true;

				}

				if (mode.get(cmdsender).equals("2")) {

					cmdsender.sendMessage("§7You are already in this mode.");
					return true;

				}

				mode.put(cmdsender, "2");
				cmdsender.sendMessage("§7You are now using the scroll spectate mode.");
				return true;

			}

		}

		//		if (cmd.getName().equalsIgnoreCase("spectatescan") || cmd.getName().equalsIgnoreCase("specscan")) {
		//			
		//			if (args.length == 1) {
		//				
		//				plugin.SpectateExtras.spectateScan(Integer.parseInt(args[0]));
		//				
		//			}
		//			
		//		}

		return true;
	}

}
