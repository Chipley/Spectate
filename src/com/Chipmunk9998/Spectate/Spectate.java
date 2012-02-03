package com.Chipmunk9998.Spectate;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.Packet20NamedEntitySpawn;

public class Spectate extends JavaPlugin {
	
	FileConfiguration conf;
	
	private final SpectateListener Listener = new SpectateListener(this);
	final SpectateCommandExecutor CommandExecutor = new SpectateCommandExecutor(this);
	  
	public void onDisable() {
		
		for (Player players : getServer().getOnlinePlayers()) {
			
			if (CommandExecutor.isSpectating.get(players) != null) {
				
				if (CommandExecutor.isSpectating.get(players)) {
					
					players.sendMessage("§7You were forced to stop spectating because of a server reload.");
					
					players.teleport(CommandExecutor.origLocation.get(players));
					CommandExecutor.isSpectating.put(players, false);
					CommandExecutor.isBeingSpectated.put(CommandExecutor.target.get(players), false);
					
					for (Player playp : players.getWorld().getPlayers()) {
					
						visible(players, playp);
						
					}
					
					getNative(players).netServerHandler.sendPacket(new Packet29DestroyEntity(players.getEntityId()));
					visible(CommandExecutor.target.get(players), players);
					
				}
				
			}
			
		}
		
		System.out.println("Spectate is disabled!");
	    
	}
	  
	public void onEnable() {

		  getServer().getPluginManager().registerEvents(Listener, this);
		  
		  PluginDescriptionFile pdfFile = this.getDescription();
		    
		  System.out.println(pdfFile.getName() + " v" + pdfFile.getVersion() + " enabled!");
		    
		  conf = getConfig();
			  
		  if (conf.get("Enable Permissions?") == null) {
				
			conf.set("Enable Permissions?", false);
			saveConfig();
				
		  }
		  
		  getCommand("spectate").setExecutor(CommandExecutor);
		  getCommand("spectateoff").setExecutor(CommandExecutor);
	}
	  
	  public static net.minecraft.server.Entity getNative(Entity e) {
		  
	        return ((CraftEntity) e).getHandle();
	        
	    }
	  
	    public EntityPlayer getNative(Player p) {
	    	
	        return ((EntityPlayer) getNative((Entity) p));
	        
	    }
	    
	    public void visible(Player p1, Player p2) {
	    	
	    	CraftPlayer unHide = (CraftPlayer) p1;
	    	CraftPlayer unHideFrom = (CraftPlayer) p2;
	    	
	    	unHideFrom.getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(unHide.getHandle()));
	    	
	    }
}