package com.Chipmunk9998.Spectate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Chipmunk9998.Spectate.api.SpectateAngle;
import com.Chipmunk9998.Spectate.api.SpectateMode;

public class SpectateCommandExecutor implements CommandExecutor {

    Spectate plugin;

    public SpectateCommandExecutor(Spectate plugin) {

        this.plugin = plugin;

    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("spectate")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                if (!sender.hasPermission("spectate.help") && !sender.hasPermission("spectate.on") && !sender.hasPermission("spectate.off")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission.");
                    return true;
                }
                showHelp(sender);
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission.");
                    return true;
                }
                plugin.loadConfig();
                sender.sendMessage(ChatColor.GRAY + "Spectate config reloaded.");
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You can't execute this command from the console.");
                return true;
            }
            Player cmdsender = (Player) sender;
            if (args[0].equalsIgnoreCase("off")) {
                if (!cmdsender.hasPermission("spectate.off")) {
                    cmdsender.sendMessage(ChatColor.RED + "You do not have permission.");
                    return true;
                }
                if (!Spectate.getAPI().isSpectating(cmdsender)) {
                    cmdsender.sendMessage(ChatColor.GRAY + "You are not currently spectating anyone.");
                    return true;
                }
                cmdsender.sendMessage(ChatColor.GRAY + "You have stopped spectating " + Spectate.getAPI().getTarget(cmdsender).getName() + ".");
                Spectate.getAPI().stopSpectating(cmdsender, true);
                return true;
            } else if (args[0].equalsIgnoreCase("mode")) {
                if (!cmdsender.hasPermission("spectate.mode")) {
                    cmdsender.sendMessage(ChatColor.RED + "You do not have permission.");
                    return true;
                }
                if (args.length < 2) {
                    cmdsender.sendMessage(ChatColor.RED + "Error: You must enter in a mode.");
                    return true;
                }
                SpectateMode newMode = SpectateMode.DEFAULT;
                if (args[1].equalsIgnoreCase("1") || args[1].equalsIgnoreCase("default")) {
                    if (Spectate.getAPI().getSpectateMode(cmdsender) == SpectateMode.DEFAULT) {
                        cmdsender.sendMessage(ChatColor.RED + "Error: You are already in this mode.");
                    } else {
                        newMode = SpectateMode.DEFAULT;
                        cmdsender.sendMessage(ChatColor.GRAY + "You are now using the default spectate mode.");
                    }
                } else if (args[1].equalsIgnoreCase("2") || args[1].equalsIgnoreCase("scroll")) {
                    if (Spectate.getAPI().getSpectateMode(cmdsender) == SpectateMode.SCROLL) {
                        cmdsender.sendMessage(ChatColor.RED + "Error: You are already in this mode.");
                    } else {
                        newMode = SpectateMode.SCROLL;
                        cmdsender.sendMessage(ChatColor.GRAY + "You are now using the scroll spectate mode.");
                    }
                } else {
                    cmdsender.sendMessage(ChatColor.RED + "Error: Unknown mode \"" + args[1] + "\"");
                    return true;
                }
                Spectate.getAPI().setSpectateMode(cmdsender, newMode);
                return true;
            } else if (args[0].equalsIgnoreCase("angle")) {
                if (!cmdsender.hasPermission("spectate.angle")) {
                    cmdsender.sendMessage(ChatColor.RED + "You do not have permission.");
                    return true;
                }
                if (args.length < 2) {
                    cmdsender.sendMessage(ChatColor.RED + "Error: You must enter in an angle.");
                    return true;
                }
                SpectateAngle newAngle;
                if (args[1].equalsIgnoreCase("1") || args[1].equalsIgnoreCase("firstperson")) {
                    if (Spectate.getAPI().getSpectateAngle(cmdsender) == SpectateAngle.FIRST_PERSON) {
                        cmdsender.sendMessage(ChatColor.RED + "Error: You are already in first person mode.");
                        return true;
                    } else {
                        newAngle = SpectateAngle.FIRST_PERSON;
                        cmdsender.sendMessage(ChatColor.GRAY + "You are now in first person mode.");
                    }
                } else if (args[1].equalsIgnoreCase("2") || args[1].equalsIgnoreCase("thirdperson")) {
                    if (Spectate.getAPI().getSpectateAngle(cmdsender) == SpectateAngle.THIRD_PERSON) {
                        cmdsender.sendMessage(ChatColor.RED + "Error: You are already in third person mode.");
                        return true;
                    } else {
                        newAngle = SpectateAngle.THIRD_PERSON;
                        cmdsender.sendMessage(ChatColor.GRAY + "You are now in third person mode.");
                    }
                } else if (args[1].equalsIgnoreCase("3") || args[1].equalsIgnoreCase("thirdpersonfront")) {
                    if (Spectate.getAPI().getSpectateAngle(cmdsender) == SpectateAngle.THIRD_PERSON_FRONT) {
                        cmdsender.sendMessage(ChatColor.RED + "Error: You are already in third person front mode.");
                        return true;
                    } else {
                        newAngle = SpectateAngle.THIRD_PERSON_FRONT;
                        cmdsender.sendMessage(ChatColor.GRAY + "You are now in third person front mode.");
                    }
                } else if (args[1].equalsIgnoreCase("4") || args[1].equalsIgnoreCase("freeroam")) {
                    if (Spectate.getAPI().getSpectateAngle(cmdsender) == SpectateAngle.FREEROAM) {
                        cmdsender.sendMessage(ChatColor.RED + "Error: You are already in free roam mode.");
                        return true;
                    } else {
                        newAngle = SpectateAngle.FREEROAM;
                        cmdsender.sendMessage(ChatColor.GRAY + "You are now in free roam mode.");
                    }
                } else {
                    cmdsender.sendMessage(ChatColor.RED + "Error: Unknown angle \"" + args[1] + "\"");
                    return true;
                }
                Spectate.getAPI().setSpectateAngle(cmdsender, newAngle);
                return true;
            } else if (args[0].equalsIgnoreCase("scan")) {
                if (!cmdsender.hasPermission("spectate.scan")) {
                    cmdsender.sendMessage(ChatColor.RED + "You do not have permission.");
                    return true;
                }
                if (args.length < 2) {
                    cmdsender.sendMessage(ChatColor.RED + "Error: You must enter in an interval.");
                    return true;
                }
                int interval;
                try {
                    interval = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    cmdsender.sendMessage(ChatColor.RED + "Error: " + args[1] + " is not a number.");
                    return true;
                }
                if (Spectate.getAPI().isScanning(cmdsender)) {
                    Spectate.getAPI().stopScanning(cmdsender);
                }
                if (!Spectate.getAPI().isSpectating(cmdsender)) {
                    Spectate.getAPI().savePlayerState(cmdsender);
                }
                cmdsender.sendMessage(ChatColor.GRAY + "You are now scanning every " + interval + " seconds.");
                Spectate.getAPI().startScanning(cmdsender, interval);
                return true;
            }
            if (!cmdsender.hasPermission("spectate.on")) {
                cmdsender.sendMessage(ChatColor.RED + "You do not have permission.");
                return true;
            }
            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                cmdsender.sendMessage(ChatColor.RED + "Error: Player is not online.");
                return true;
            }
            if (cmdsender.getName().equals(targetPlayer.getName())) {
                cmdsender.sendMessage(ChatColor.GRAY + "Did you really just try to spectate yourself?");
                return true;
            }
            if (plugin.cantspectate_permission_enabled) {
                if (targetPlayer.hasPermission("spectate.cantspectate")) {
                    cmdsender.sendMessage(ChatColor.GRAY + "This person can not be spectated.");
                    return true;
                }
            }
            if (Spectate.getAPI().isSpectating(cmdsender)) {
                if (targetPlayer.getName().equals(Spectate.getAPI().getTarget(cmdsender).getName())) {
                    cmdsender.sendMessage(ChatColor.GRAY + "You are already spectating them.");
                    return true;
                }
            }
            if (Spectate.getAPI().isSpectating(targetPlayer)) {
                cmdsender.sendMessage(ChatColor.GRAY + "They are currently spectating someone.");
                return true;
            }
            if (targetPlayer.isDead()) {
                cmdsender.sendMessage(ChatColor.GRAY + "They are currently dead.");
                return true;
            }
            if (plugin.multiverseInvEnabled() && !cmdsender.getWorld().getName().equals(targetPlayer.getWorld().getName())) {
                if (Spectate.getAPI().isSpectating(cmdsender)) {
                    Spectate.getAPI().stopSpectating(cmdsender, true);
                }
                Spectate.getAPI().savePlayerState(cmdsender);
                Spectate.getAPI().saveMultiInvState(cmdsender, targetPlayer);
            } else {
                if (!Spectate.getAPI().isSpectating(cmdsender)) {
                    Spectate.getAPI().savePlayerState(cmdsender);
                }
            }
            Spectate.getAPI().startSpectating(cmdsender, targetPlayer);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("control")) {
            //TODO: Finish controlling
        }
        return true;
    }

    public void showHelp(CommandSender cmdsender) {
        cmdsender.sendMessage(ChatColor.RED + "Commands for Spectate:");
        cmdsender.sendMessage(ChatColor.RED + "/spectate [PlayerName] : " + ChatColor.GRAY + "Puts you into spectate mode and lets you see what the target sees.");
        cmdsender.sendMessage(ChatColor.RED + "/spectate off : " + ChatColor.GRAY + "Takes you out of spectate mode.");
        cmdsender.sendMessage(ChatColor.RED + "/spectate scan [interval] : " + ChatColor.GRAY + "Starts the scanning mode with the specified interval.");
        cmdsender.sendMessage(ChatColor.RED + "/spectate mode [1 | default] : " + ChatColor.GRAY + "Puts you into the default spectate mode.");
        cmdsender.sendMessage(ChatColor.RED + "/spectate mode [2 | scroll] : " + ChatColor.GRAY + "Puts you into scroll style mode with left click and right click controls.");
        cmdsender.sendMessage(ChatColor.RED + "/spectate reload : " + ChatColor.GRAY + "Reloads the config.");
        cmdsender.sendMessage(ChatColor.RED + "/spectate help : " + ChatColor.GRAY + "Shows this help page.");
    }

}
