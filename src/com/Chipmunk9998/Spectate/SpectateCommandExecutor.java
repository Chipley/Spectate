package com.Chipmunk9998.Spectate;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpectateCommandExecutor implements CommandExecutor {

	public Spectate plugin;

	public Map<Player, Boolean> isSpectating = new HashMap<Player, Boolean>();
	public Map<Player, Boolean> isBeingSpectated = new HashMap<Player, Boolean>();
	public Map<Player, String> spectator = new HashMap<Player, String>();
	public Map<Player, Player> target = new HashMap<Player, Player>();
	public Map<Player, ItemStack[]> senderInv = new HashMap<Player, ItemStack[]>();
	public Map<Player, ItemStack[]> senderArm = new HashMap<Player, ItemStack[]>();
	public Map<Player, Integer> senderHunger = new HashMap<Player, Integer>();
	public Map<Player, Integer> senderHealth = new HashMap<Player, Integer>();
	public HashMap<Player, Location> origLocation = new HashMap<Player, Location>();

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

		if (plugin.conf.getBoolean("Enable Permissions?")) {

			if (!cmdsender.isOp()) {

				if (!cmdsender.hasPermission("spectate.use")) {

					cmdsender.sendMessage("§cYou do not have permission to spectate.");
					return true;

				}
			}
		}

		if (cmd.getName().equalsIgnoreCase("spectate") || cmd.getName().equalsIgnoreCase("spec")) {

			if (args.length > 0) {

				cmdtarget = args[0];
				targetPlayer = Bukkit.getPlayer(cmdtarget);

				if (args[0].equalsIgnoreCase("herobrine")) {

					cmdsender.sendMessage("§7You can't watch Herobrine, only he can watch you ;)");
					return true;

				}

				if (targetPlayer != null) {

					if (cmdsender.getName() == targetPlayer.getName()) {

						cmdsender.sendMessage("§7You're already spectating yourself...");
						return true;

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
					
					if (targetPlayer.hasPermission("spectate.cantspectate")) {

						cmdsender.sendMessage("§7They can not be spectated.");
						return true;

					}
					
					if (isSpectating.get(cmdsender) != null) {

						if (isSpectating.get(cmdsender)) {

							plugin.SpectateOff.spectateOff(cmdsender);

						}

					}


					cmdsender.sendMessage("§7You are now spectating " + targetPlayer.getName());
					origLocation.put(cmdsender, cmdsender.getLocation());
					isSpectating.put(cmdsender, true);
					isBeingSpectated.put(targetPlayer, true);
					
					if (spectator.get(targetPlayer) == null) {
					
						spectator.put(targetPlayer, cmdsender.getName());
					
					}else {
						
						spectator.put(targetPlayer, spectator.get(cmdsender) + "," + cmdsender.getName());
						
					}
					
					
					target.put(cmdsender, targetPlayer);
					cmdsender.getPlayer().teleport(target.get(cmdsender));
					senderInv.put(cmdsender, cmdsender.getInventory().getContents());
					senderArm.put(cmdsender, cmdsender.getInventory().getArmorContents());
					senderHunger.put(cmdsender, cmdsender.getFoodLevel());
					senderHealth.put(cmdsender, cmdsender.getHealth());
					cmdsender.getInventory().clear();
					cmdsender.getInventory().setContents(targetPlayer.getInventory().getContents());
					cmdsender.getInventory().setArmorContents(targetPlayer.getInventory().getArmorContents());
					
					for (Player player : plugin.getServer().getOnlinePlayers()) {

						player.hidePlayer(cmdsender);

					}

					targetPlayer.hidePlayer(cmdsender);
					cmdsender.hidePlayer(targetPlayer);

					return true;

				}

				cmdsender.sendMessage("§cError: Player is not online§f");
				return true;

			}

			cmdsender.sendMessage("§cError: No player target§f");
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("spectateoff") || cmd.getName().equalsIgnoreCase("specoff")) {

			if (isSpectating.get(cmdsender) != null) {

				if (isSpectating.get(cmdsender)) {

					plugin.SpectateOff.spectateOff(cmdsender);

					return true;

				}

			}

			cmdsender.sendMessage("§7You are currently not spectating anyone§f");
			return true;

		}

		return true;
	}

}
