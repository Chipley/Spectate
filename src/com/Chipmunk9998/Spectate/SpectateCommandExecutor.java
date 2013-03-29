package com.Chipmunk9998.Spectate;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpectateCommandExecutor implements CommandExecutor {

	public Spectate plugin;

	public ArrayList<String> isSpectating = new ArrayList<String>();
	public ArrayList<String> isBeingSpectated = new ArrayList<String>();
	public HashMap<String, String> spectator = new HashMap<String, String>();
	public HashMap<String, String> target = new HashMap<String, String>();
	public HashMap<String, ItemStack[]> senderInv = new HashMap<String, ItemStack[]>();
	public HashMap<String, ItemStack[]> senderArm = new HashMap<String, ItemStack[]>();
	public HashMap<String, Integer> senderHunger = new HashMap<String, Integer>();
	public HashMap<String, Integer> senderHealth = new HashMap<String, Integer>();
	public HashMap<String, Integer> senderXP = new HashMap<String, Integer>();
	public HashMap<String, Integer> senderSlot = new HashMap<String, Integer>();
	public HashMap<String, Location> origLocation = new HashMap<String, Location>();
	public HashMap<String, String> mode = new HashMap<String, String>();
	public HashMap<String, Integer> playerNumber = new HashMap<String, Integer>();
	
	public HashMap<String, Integer> playerAngle = new HashMap<String, Integer>();

	public HashMap<String, Boolean> isScanning = new HashMap<String, Boolean>();
	public HashMap<String, Boolean> isInv = new HashMap<String, Boolean>();

	public HashMap<String, Integer> taskId = new HashMap<String, Integer>();

	public HashMap<String, Boolean> isClick = new HashMap<String, Boolean>();
	
	public ArrayList<String> isControlling = new ArrayList<String>();

	public SpectateCommandExecutor(Spectate plugin) {

		this.plugin = plugin;

	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (!(sender instanceof Player)) {

			return true;

		}

		Player cmdsender = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("spectate") || cmd.getName().equalsIgnoreCase("spec")) {

			if (args.length > 0) {

				if (args[0].equalsIgnoreCase("off")) {

					if (!cmdsender.hasPermission("spectate.off")) {

						cmdsender.sendMessage("§cYou do not have permission.");
						return true;

					}

					if (isSpectating.contains(cmdsender.getName())) {

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

					cmdsender.sendMessage("§7You are not currently spectating anyone.");
					return true;

				}

				if (args[0].equalsIgnoreCase("mode")) {

					if (!cmdsender.hasPermission("spectate.mode")) {

						cmdsender.sendMessage("§cYou do not have permission.");
						return true;

					}

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

					if (!cmdsender.hasPermission("spectate.scan")) {

						cmdsender.sendMessage("§cYou do not have permission.");
						return true;

					}

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

						if (!isSpectating.contains(cmdsender.getName())) {

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

				if (args[0].equalsIgnoreCase("angle")) {

					if (!cmdsender.hasPermission("spectate.angle")) {

						cmdsender.sendMessage("§cYou do not have permission.");
						return true;

					}
					
					if (playerAngle.get(cmdsender.getName()) == null) {
						
						playerAngle.put(cmdsender.getName(), 1);
						
					}

					if (args.length == 2) {

						if (args[1].equalsIgnoreCase("firstperson")) {

							if (playerAngle.get(cmdsender.getName()) == 1) {

								cmdsender.sendMessage("§cError: You are already in first person mode.");

							}

							playerAngle.put(cmdsender.getName(), 1);

							if (isSpectating.contains(cmdsender.getName())) {

								cmdsender.hidePlayer(plugin.getServer().getPlayer(target.get(cmdsender.getName())));

							}

							cmdsender.sendMessage(ChatColor.GRAY + "You are now in first person mode.");
							return true;

						}else if (args[1].equalsIgnoreCase("thirdperson")) {

							if (playerAngle.get(cmdsender.getName()) == 2) {

								cmdsender.sendMessage("§cError: You are already in third person mode.");

							}

							playerAngle.put(cmdsender.getName(), 2);
							
							if (isSpectating.contains(cmdsender.getName())) {

								cmdsender.showPlayer(plugin.getServer().getPlayer(target.get(cmdsender.getName())));

							}
							
							cmdsender.sendMessage(ChatColor.GRAY + "You are now in third person mode.");
							return true;

						}else {

							cmdsender.sendMessage(ChatColor.RED + "Error: Unknown angle.");
							return true;

						}
						
						/*
						
						else if (args[1].equalsIgnoreCase("thirdpersonfront")) {

							if (playerAngle.get(cmdsender.getName()) == 3) {

								cmdsender.sendMessage("§cError: You are already in third person front mode.");

							}

							playerAngle.put(cmdsender.getName(), 3);
							
							if (isSpectating.contains(cmdsender.getName())) {

								cmdsender.showPlayer(plugin.getServer().getPlayer(target.get(cmdsender.getName())));

							}
							
							cmdsender.sendMessage(ChatColor.GRAY + "You are now in third person front mode.");
							return true;

						}
						
						*/

					}else {
						
						if (playerAngle.get(cmdsender.getName()) == 1) {

							playerAngle.put(cmdsender.getName(), 2);
							
							if (isSpectating.contains(cmdsender.getName())) {

								cmdsender.showPlayer(plugin.getServer().getPlayer(target.get(cmdsender.getName())));

							}
							
							cmdsender.sendMessage(ChatColor.GRAY + "You are now in third person mode.");
							return true;

						}else if (playerAngle.get(cmdsender.getName()) == 2) {

							playerAngle.put(cmdsender.getName(), 1);
							
							if (isSpectating.contains(cmdsender.getName())) {

								cmdsender.hidePlayer(plugin.getServer().getPlayer(target.get(cmdsender.getName())));

							}
							
							cmdsender.sendMessage(ChatColor.GRAY + "You are now in first person mode.");
							return true;

						}
						
						/*

						if (playerAngle.get(cmdsender.getName()) == 1) {

							playerAngle.put(cmdsender.getName(), 2);
							
							if (isSpectating.contains(cmdsender.getName())) {

								cmdsender.showPlayer(plugin.getServer().getPlayer(target.get(cmdsender.getName())));

							}
							
							cmdsender.sendMessage(ChatColor.GRAY + "You are now in third person mode.");
							return true;

						}else if (playerAngle.get(cmdsender.getName()) == 2) {

							playerAngle.put(cmdsender.getName(), 3);
							
							if (isSpectating.contains(cmdsender.getName())) {

								cmdsender.showPlayer(plugin.getServer().getPlayer(target.get(cmdsender.getName())));

							}
							
							cmdsender.sendMessage(ChatColor.GRAY + "You are now in third person front mode.");
							return true;

						}else if (playerAngle.get(cmdsender.getName()) == 3) {

							playerAngle.put(cmdsender.getName(), 1);
							
							if (isSpectating.contains(cmdsender.getName())) {

								cmdsender.hidePlayer(plugin.getServer().getPlayer(target.get(cmdsender.getName())));

							}
							
							cmdsender.sendMessage(ChatColor.GRAY + "You are now in first person mode.");
							return true;

						}
						
						*/

					}

				}

				if (args[0].equalsIgnoreCase("inv")) {

					if (!cmdsender.hasPermission("spectate.inv")) {

						cmdsender.sendMessage("§cYou do not have permission.");
						return true;

					}

					if (isSpectating.contains(cmdsender.getName())) {

						cmdsender.sendMessage("§cError: You can not change this setting while spectating.");
						return true;

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

				if (args[0].equalsIgnoreCase("help")) {

					if (!cmdsender.hasPermission("spectate.help")) {

						cmdsender.sendMessage("§cYou do not have permission.");
						return true;

					}

					cmdsender.sendMessage(ChatColor.RED + "Commands for Spectate:");
					cmdsender.sendMessage(ChatColor.RED + "/spectate [PlayerName]: " + ChatColor.GRAY + "Puts you into spectate mode and lets you see what the target sees.");
					cmdsender.sendMessage(ChatColor.RED + "/spectate off : " + ChatColor.GRAY + "Takes you out of spectate mode.");
					cmdsender.sendMessage(ChatColor.RED + "/spectate mode [1 | default]: " + ChatColor.GRAY + "Puts you into the default spectate mode.");
					cmdsender.sendMessage(ChatColor.RED + "/spectate mode [2 | scroll]: " + ChatColor.GRAY + "Puts you into scroll style mode with left click and right click controls.");
					cmdsender.sendMessage(ChatColor.RED + "/spectate inv [on/off] : " + ChatColor.GRAY + "Toggles whether or not your inventory will be modified while spectating.");
					cmdsender.sendMessage(ChatColor.RED + "/spectate help : " + ChatColor.GRAY + "Shows this help page.");
					return true;

				}

				if (!cmdsender.hasPermission("spectate.on")) {

					cmdsender.sendMessage("§cYou do not have permission.");
					return true;

				}

				Player targetPlayer = plugin.getServer().getPlayer(args[0]);

				if (targetPlayer != null) {

					if (cmdsender.getName() == targetPlayer.getName()) {

						cmdsender.sendMessage("§7Did you really just try to spectate yourself?");
						return true;

					}

					if (isSpectating.contains(cmdsender.getName())) {

						if (targetPlayer.getName().equals(target.get(cmdsender.getName()))) {

							cmdsender.sendMessage("§7You are already spectating them.");
							return true;

						}

					}

					if (isSpectating.contains(targetPlayer.getName())) {

						cmdsender.sendMessage("§7They are currently spectating someone.");
						return true;

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

			if (isSpectating.contains(cmdsender.getName())) {

				if (!cmdsender.hasPermission("spectate.off")) {

					cmdsender.sendMessage("§cYou do not have permission.");
					return true;

				}

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

			if (plugin.CommandExecutor.mode.get(cmdsender.getName()) != null) {

				if (plugin.CommandExecutor.mode.get(cmdsender.getName()).equals("2")) {

					if (!cmdsender.hasPermission("spectate.on")) {

						cmdsender.sendMessage("§cYou do not have permission.");
						return true;

					}

					ArrayList<Player> spectateablePlayers = SpectateAPI.getSpectateablePlayers();

					spectateablePlayers.remove(cmdsender);

					Player[] players = spectateablePlayers.toArray(new Player[spectateablePlayers.size()]);

					try {

						if (isSpectating.contains(cmdsender.getName())) {

							if (target.get(cmdsender.getName()) != null) {

								if (players[0].getName().equals(target.get(cmdsender.getName()))) {

									cmdsender.sendMessage("§cError: You are already spectating this player.");
									return true;

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

			if (!cmdsender.hasPermission("spectate.help")) {

				cmdsender.sendMessage("§cYou do not have permission.");
				return true;

			}

			cmdsender.sendMessage(ChatColor.RED + "Commands for Spectate:");
			cmdsender.sendMessage(ChatColor.RED + "/spectate [PlayerName]: " + ChatColor.GRAY + "Puts you into spectate mode and lets you see what the target sees.");
			cmdsender.sendMessage(ChatColor.RED + "/spectate off : " + ChatColor.GRAY + "Takes you out of spectate mode.");
			cmdsender.sendMessage(ChatColor.RED + "/spectate mode [1 | default]: " + ChatColor.GRAY + "Puts you into the default spectate mode.");
			cmdsender.sendMessage(ChatColor.RED + "/spectate mode [2 | scroll]: " + ChatColor.GRAY + "Puts you into scroll style mode with left click and right click controls.");
			cmdsender.sendMessage(ChatColor.RED + "/spectate inv [on/off] : " + ChatColor.GRAY + "Toggles whether or not your inventory will be modified while spectating.");
			cmdsender.sendMessage(ChatColor.RED + "/spectate help : " + ChatColor.GRAY + "Shows this help page.");

			return true;

		}
		
		if (cmd.getName().equalsIgnoreCase("control")) {
			
			if (!cmdsender.hasPermission("spectate.control")) {

				cmdsender.sendMessage("§cYou do not have permission.");
				return true;

			}
			
			if (args.length > 0) {
				
				if (args[0].equalsIgnoreCase("off")) {

					if (!cmdsender.hasPermission("spectate.off")) {

						cmdsender.sendMessage("§cYou do not have permission.");
						return true;

					}

					if (isControlling.contains(cmdsender.getName())) {

						cmdsender.sendMessage("§7You have stopped controlling " + spectator.get(cmdsender.getName()) + ".");
						SpectateAPI.spectateOff(plugin.getServer().getPlayer(spectator.get(cmdsender.getName())));
						return true;

					}

					cmdsender.sendMessage("§7You are not currently controlling anyone.");
					return true;

				}
				
				Player targetPlayer = plugin.getServer().getPlayer(args[0]);

				if (targetPlayer != null) {

					if (cmdsender.getName() == targetPlayer.getName()) {

						cmdsender.sendMessage("§7Did you really just try to control yourself?");
						return true;

					}

					if (isControlling.contains(cmdsender.getName())) {

						if (targetPlayer.getName().equals(target.get(targetPlayer.getName()))) {

							cmdsender.sendMessage("§7You are already controlling them.");
							return true;

						}

					}

					if (isSpectating.contains(targetPlayer.getName())) {
						
						if (isControlling.contains(target.get(targetPlayer.getName()))) {
							
							cmdsender.sendMessage("§7They are currently being controlled by someone.");
							return true;
							
						}

						cmdsender.sendMessage("§7They are currently spectating someone.");
						return true;

					}
					
					if (isControlling.contains(targetPlayer.getName())) {
						
						cmdsender.sendMessage("§7They are currently controlling someone.");
						return true;
						
					}

					if (targetPlayer.isDead()) {

						cmdsender.sendMessage("§7They are currently dead.");
						return true;

					}

					if (plugin.conf.getBoolean("canspectate Permission Enabled?") == true) {

						if (targetPlayer.hasPermission("spectate.cantspectate")) {

							cmdsender.sendMessage("§7They can not be controlled.");
							return true;

						}

					}
					
					isControlling.add(targetPlayer.getName());
					SpectateAPI.spectateOn(targetPlayer, cmdsender);
					return true;

				}

				if (args[0].equalsIgnoreCase("herobrine")) {

					cmdsender.sendMessage("§7You can't control Herobrine silly.");
					return true;

				}

				cmdsender.sendMessage("§cError: Player is not online§f");
				return true;
				
			}
			
		}

		return true;

	}

}
