package com.Chipmunk9998.Spectate;

import java.text.DecimalFormat;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SpectateListener implements Listener {

	public Spectate plugin;

	public SpectateListener(Spectate plugin) {

		this.plugin = plugin;

	}

	public void updatePlayer() {

		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			public void run() {

				for (Player p : plugin.getServer().getOnlinePlayers()) {

					if (plugin.CommandExecutor.isSpectating.get(p) != null) {

						if (plugin.CommandExecutor.isSpectating.get(p)) {

							if (roundTwoDecimals(p.getLocation().getX()) != roundTwoDecimals(plugin.CommandExecutor.target.get(p).getLocation().getX()) || roundTwoDecimals(p.getLocation().getY()) != roundTwoDecimals(plugin.CommandExecutor.target.get(p).getLocation().getY()) || roundTwoDecimals(p.getLocation().getZ()) != roundTwoDecimals(plugin.CommandExecutor.target.get(p).getLocation().getZ()) || roundTwoDecimals(p.getLocation().getYaw()) != roundTwoDecimals(plugin.CommandExecutor.target.get(p).getLocation().getYaw()) || roundTwoDecimals(p.getLocation().getPitch()) != roundTwoDecimals(plugin.CommandExecutor.target.get(p).getLocation().getPitch())) {

								Location loc = new Location(p.getLocation().getWorld(), plugin.CommandExecutor.target.get(p).getLocation().getX(), plugin.CommandExecutor.target.get(p).getLocation().getY(), plugin.CommandExecutor.target.get(p).getLocation().getZ());

								loc.setYaw(plugin.CommandExecutor.target.get(p).getLocation().getYaw());

								loc.setPitch(plugin.CommandExecutor.target.get(p).getLocation().getPitch());

								p.teleport(loc);

							}

							if (p.getInventory() != plugin.CommandExecutor.target.get(p).getInventory()) {

								p.getInventory().setContents(plugin.CommandExecutor.target.get(p).getInventory().getContents());
								p.getInventory().setArmorContents(plugin.CommandExecutor.target.get(p).getInventory().getArmorContents());

							}

							if (plugin.CommandExecutor.target.get(p).getHealth() > 0) {

								p.setHealth(plugin.CommandExecutor.target.get(p).getHealth());

							}else {

								p.setHealth(1);

							}

							p.setFoodLevel(plugin.CommandExecutor.target.get(p).getFoodLevel());

						}

					}

				}

			}

		}, 0L, 1L);

	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {

		if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer())) {

				String[] spectators = plugin.CommandExecutor.spectator.get(event.getPlayer()).split(",");

				for (String player : spectators) {

					Player p = plugin.getServer().getPlayer(player);

					p.teleport(event.getPlayer());

					for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {

						onlinePlayers.hidePlayer(p);

					}

					p.hidePlayer(event.getPlayer());
					event.getPlayer().hidePlayer(p);

				}

			}

		}

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {

				event.getPlayer().teleport(plugin.CommandExecutor.target.get(event.getPlayer()));

			}

		}

	}


	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {

				plugin.SpectateOff.spectateOff(event.getPlayer());

			}
		}

		if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer())) {

				String[] spectators = plugin.CommandExecutor.spectator.get(event.getPlayer()).split(",");

				for (String player : spectators) {

					Player p = plugin.getServer().getPlayer(player);

					p.sendMessage("§7You were forced to stop spectating because the person you were spectating disconnected.");

					plugin.SpectateOff.spectateOff(p);

				}

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

			Player target = (Player)event.getEntity();

			if (plugin.CommandExecutor.isSpectating.get(target) != null) {

				if (plugin.CommandExecutor.isSpectating.get(target)) {

					event.setCancelled(true);

				}

			}

		}

		if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

			EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

			if (event1.getDamager() instanceof Player) {

				Player damager = (Player)event1.getDamager();

				if (plugin.CommandExecutor.isSpectating.get(damager) != null) {

					if (plugin.CommandExecutor.isSpectating.get(damager)) {

						event.setCancelled(true);

					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {

		if (event instanceof PlayerDeathEvent) {

			Player pla = (Player)event.getEntity();

			if (plugin.CommandExecutor.isBeingSpectated.get(pla) != null) {

				if (plugin.CommandExecutor.isBeingSpectated.get(pla)) {

					String[] spectators = plugin.CommandExecutor.spectator.get(pla).split(",");

					for (String player : spectators) {

						Player p = plugin.getServer().getPlayer(player);

						p.sendMessage("§7You were forced to stop spectating because the person you were spectating died.");

						plugin.SpectateOff.spectateOff(p);

					}

				}

			}

		}

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {

				event.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void onPlayerRegen(EntityRegainHealthEvent event) {

		if (event.getEntity() instanceof Player) {

			Player p = (Player) event.getEntity();

			if (plugin.CommandExecutor.isSpectating.get(p) != null) {

				if (plugin.CommandExecutor.isSpectating.get(p)) {

					event.setCancelled(true);

				}

			}

		}

	}

	double roundTwoDecimals(double d) {

		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));

	}

}
