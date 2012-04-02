package com.Chipmunk9998.Spectate;

import org.bukkit.entity.Player;

public class SpectateOff {
	
	public Spectate plugin;
	
	public SpectateOff(Spectate plugin) {

		this.plugin = plugin;

	}
	
	public void spectateOff(final Player player) {
		
		player.teleport(plugin.CommandExecutor.origLocation.get(player));
		plugin.CommandExecutor.isSpectating.put(player, false);
		player.getInventory().clear();
		player.getInventory().setContents(plugin.CommandExecutor.senderInv.get(player));
		player.getInventory().setArmorContents(plugin.CommandExecutor.senderArm.get(player));
		player.setHealth(plugin.CommandExecutor.senderHealth.get(player));
		player.setFoodLevel(plugin.CommandExecutor.senderHunger.get(player));
		
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
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			public void run() {

				for (Player p : plugin.getServer().getOnlinePlayers()) {

					p.showPlayer(player);

				}
				
				player.showPlayer(plugin.CommandExecutor.target.get(player));
				plugin.CommandExecutor.target.get(player).showPlayer(player);
		
			}
			
		}, 10L);

	}

}
