package com.Chipmunk9998.Spectate;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpectateCommandExecutor implements CommandExecutor{
	
	public Spectate plugin;
	
	public Map<Player, Boolean> isSpectating = new HashMap<Player, Boolean>();
	public Map<Player, Boolean> isBeingSpectated = new HashMap<Player, Boolean>();
	public Map<Player, Player> spectator = new HashMap<Player, Player>();
	public Map<Player, Player> target = new HashMap<Player, Player>();
	public Map<Player, ItemStack[]> senderInv = new HashMap<Player, ItemStack[]>();
	public Map<Player, ItemStack[]> senderArm = new HashMap<Player, ItemStack[]>();
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
			  
		  	if (cmd.getName().equalsIgnoreCase("spectate")) {
		  		
		  		if (isSpectating.get(cmdsender) != null) {
					  
					  if (isSpectating.get(cmdsender)) {
						  
						  cmdsender.sendMessage("§7You are currently spectating someone. Type /spectateoff to stop.");
						  return true;
						  
					  }
					  
				  }
		  		
                if (args.length > 0) {
                	
                		cmdtarget = args[0];
                		targetPlayer = Bukkit.getPlayer(cmdtarget);
                		
                		if (args[0].equalsIgnoreCase("herobrine")) {
                			
                			cmdsender.sendMessage("§7You can't watch Herobrine, only he can watch you ;)");
                			return true;
                			
                		}
                		
                		if (targetPlayer != null) {
                		
                    		target.put(cmdsender, targetPlayer);
                			
                		if (cmdsender.getName() == targetPlayer.getName()) {
                			
                			cmdsender.sendMessage("§7You're already spectating yourself...");
                			return true;
                			
                		}
                		
                		if (isBeingSpectated.get(targetPlayer) != null) {
                			
                			if (isBeingSpectated.get(targetPlayer)) {
                				
                				cmdsender.sendMessage("§7Someone is currently spectating them.");
                				
                			}
                			
                		}
                			
                				
                			cmdsender.sendMessage("§7You are now spectating " + targetPlayer.getName());
                			origLocation.put(cmdsender, cmdsender.getLocation());
                			isSpectating.put(cmdsender, true);
                			isBeingSpectated.put(targetPlayer, true);
                			spectator.put(targetPlayer, cmdsender);
                			cmdsender.getPlayer().teleport(target.get(cmdsender));
                			senderInv.put(cmdsender, cmdsender.getInventory().getContents());
                			senderArm.put(cmdsender, cmdsender.getInventory().getArmorContents());
                			cmdsender.getInventory().clear();
                			cmdsender.getInventory().setContents(targetPlayer.getInventory().getContents());
                			cmdsender.getInventory().setArmorContents(targetPlayer.getInventory().getArmorContents());
                			    	
                			    for (Player p : cmdsender.getWorld().getPlayers()) {
                		    			
                        			plugin.getNative(p).netServerHandler.sendPacket(new Packet29DestroyEntity(cmdsender.getEntityId()));
                        		                
                        		}
                        		    		
                        		    plugin.getNative(cmdsender).netServerHandler.sendPacket(new Packet29DestroyEntity(targetPlayer.getEntityId()));
        		    		
                			return true;
                    
                		}
                		
                			cmdsender.sendMessage("§cError: Player is not online§f");
                			return true;
                			
                	}
                
    		  	cmdsender.sendMessage("§cError: No player target§f");
    		    return true;
		  	}
		    
		    if (cmd.getName().equalsIgnoreCase("spectateoff")) {
    			
    			if (isSpectating.get(cmdsender) != null) {
    				
    				if (isSpectating.get(cmdsender)) {
    				
    				cmdsender.teleport(origLocation.get(cmdsender));
    				isSpectating.put(cmdsender, false);
    				isBeingSpectated.put(target.get(cmdsender), false);
    				cmdsender.getInventory().clear();
        			cmdsender.getInventory().setContents(senderInv.get(cmdsender));
        			cmdsender.getInventory().setArmorContents(senderArm.get(cmdsender));
    				
    				for (Player playp : cmdsender.getWorld().getPlayers()) {
    				
    					plugin.visible(cmdsender, playp);
    					
    				}
    				
    				plugin.getNative(cmdsender).netServerHandler.sendPacket(new Packet29DestroyEntity(cmdsender.getEntityId()));
    				plugin.visible(target.get(cmdsender), cmdsender);
    				
    				return true;
    				
    				}
    				
    			}
    				
    			cmdsender.sendMessage("§7You are currently not spectating anyone§f");
    			return true;
    		
		  	}
		    
		    return true;
}

}
