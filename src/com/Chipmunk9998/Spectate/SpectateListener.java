package com.Chipmunk9998.Spectate;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SpectateListener implements Listener {

	public Spectate plugin;
	
	public Map<Player, Boolean> quitAndSpectating = new HashMap<Player, Boolean>();
	
	int varx;
	int vayr;
	int varz;
	
	public SpectateListener(Spectate plugin) {

		this.plugin = plugin;

	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		    	
		    	if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer()) != null) {
		    		
		    		if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer())) {
		    	
		    			plugin.CommandExecutor.spectator.get(event.getPlayer()).getPlayer().teleport(plugin.CommandExecutor.target.get(plugin.CommandExecutor.spectator.get(event.getPlayer())));
		    			
		    			plugin.CommandExecutor.spectator.get(event.getPlayer()).getInventory().clear();
		    			plugin.CommandExecutor.spectator.get(event.getPlayer()).getInventory().setContents(event.getPlayer().getInventory().getContents());
		    			plugin.CommandExecutor.spectator.get(event.getPlayer()).getInventory().setArmorContents(event.getPlayer().getInventory().getArmorContents());
		    			
		    			for (Player p : plugin.CommandExecutor.spectator.get(event.getPlayer()).getWorld().getPlayers()) {
    		    			
            				plugin.getNative(p).netServerHandler.sendPacket(new Packet29DestroyEntity(plugin.CommandExecutor.spectator.get(event.getPlayer()).getEntityId()));
            		                
            			}
            		    		
            		    	plugin.getNative(plugin.CommandExecutor.spectator.get(event.getPlayer())).netServerHandler.sendPacket(new Packet29DestroyEntity(event.getPlayer().getEntityId()));
		    		
		    		}
		    		
		    	}else {
		    		
		    		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {
		    			
		    			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {
		    				
		    				event.getPlayer().teleport(plugin.CommandExecutor.target.get(event.getPlayer()));
		    				
		    				for (Player p : event.getPlayer().getWorld().getPlayers()) {
	    		    			
	            				plugin.getNative(p).netServerHandler.sendPacket(new Packet29DestroyEntity(event.getPlayer().getEntityId()));
	            		                
	            			}
	            		    		
	            		    	plugin.getNative(event.getPlayer()).netServerHandler.sendPacket(new Packet29DestroyEntity(plugin.CommandExecutor.target.get(event.getPlayer()).getEntityId()));
		    				
		    			}
		    			
		    		}
		    		
		    	}
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName()) != null) {
		
			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName())) {
				
				event.getPlayer().teleport(plugin.CommandExecutor.origLocation.get(event.getPlayer()));
				plugin.CommandExecutor.isSpectating.put(event.getPlayer(), false);
				
			}
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		
		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {
			
			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {
				
				event.setCancelled(true);
				
			}
			
		}
		
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		
		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {
			
			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {
				
				event.setCancelled(true);
				
			}
			
		}
		
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		
		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {
			
			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {
			
				event.setCancelled(true);
			
			}
			
		}
		
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {
			
			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {
			
				event.setCancelled(true);
			
			}
			
		}
		
	}
	
	@EventHandler
	public void onEnitityDamage(EntityDamageEvent event) {
		
		if (event.getEntity() instanceof Player) {
			
			Player pla = (Player)event.getEntity();
			
			if (plugin.CommandExecutor.isSpectating.get(pla) != null) {
				
				if (plugin.CommandExecutor.isSpectating.get(pla)) {
			
					event.setCancelled(true);
			
				}
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		
		    if (event.getEntity() instanceof PlayerDeathEvent) {
		    	
		    	Player pla = (Player)event.getEntity();
		    	
					if (plugin.CommandExecutor.isBeingSpectated.get(pla) != null) {
						
						if (plugin.CommandExecutor.isBeingSpectated.get(pla)) {
							
							plugin.CommandExecutor.spectator.get(pla).sendMessage("§7You were forced to stop spectating because the person who you were spectating died.");
							
							plugin.CommandExecutor.spectator.get(pla).teleport(plugin.CommandExecutor.origLocation.get(plugin.CommandExecutor.spectator.get(pla)));
							plugin.CommandExecutor.isSpectating.put(plugin.CommandExecutor.spectator.get(pla), false);
							plugin.CommandExecutor.isBeingSpectated.put(pla, false);
							
							for (Player playp : plugin.CommandExecutor.spectator.get(pla).getWorld().getPlayers()) {
							
								plugin.visible(plugin.CommandExecutor.spectator.get(pla), playp);
								
							}
							
							plugin.getNative(plugin.CommandExecutor.spectator.get(pla)).netServerHandler.sendPacket(new Packet29DestroyEntity(plugin.CommandExecutor.spectator.get(pla).getEntityId()));
							plugin.visible(plugin.CommandExecutor.target.get(plugin.CommandExecutor.spectator.get(pla)), plugin.CommandExecutor.spectator.get(pla));
							
						}
						
					}
		    }
	
	}
	
}
