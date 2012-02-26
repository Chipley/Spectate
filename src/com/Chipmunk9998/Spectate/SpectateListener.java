package com.Chipmunk9998.Spectate;

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

				if (event.getPlayer().getHealth() > 0) {

					plugin.CommandExecutor.spectator.get(event.getPlayer()).setHealth(event.getPlayer().getHealth());

				}else {

					plugin.CommandExecutor.spectator.get(event.getPlayer()).setHealth(1);

				}

				for (Player p : plugin.getServer().getOnlinePlayers()) {

					p.hidePlayer(plugin.CommandExecutor.spectator.get(event.getPlayer()));

				}

				plugin.CommandExecutor.spectator.get(event.getPlayer()).hidePlayer(event.getPlayer());
				event.getPlayer().hidePlayer(plugin.CommandExecutor.spectator.get(event.getPlayer()));

			}

		}

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {

				event.getPlayer().teleport(plugin.CommandExecutor.target.get(event.getPlayer()));

				for (Player p : plugin.getServer().getOnlinePlayers()) {

					p.hidePlayer(event.getPlayer());

				}

				event.getPlayer().hidePlayer(plugin.CommandExecutor.target.get(event.getPlayer()));
				plugin.CommandExecutor.target.get(event.getPlayer()).hidePlayer(event.getPlayer());

			}

		}

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {

				event.getPlayer().setFoodLevel(plugin.CommandExecutor.target.get(event.getPlayer()).getFoodLevel());

				event.getPlayer().teleport(plugin.CommandExecutor.origLocation.get(event.getPlayer()));
				plugin.CommandExecutor.isSpectating.put(event.getPlayer(), false);
				event.getPlayer().getInventory().clear();
				event.getPlayer().getInventory().setContents(plugin.CommandExecutor.senderInv.get(event.getPlayer()));
				event.getPlayer().getInventory().setArmorContents(plugin.CommandExecutor.senderArm.get(event.getPlayer()));
				event.getPlayer().setHealth(plugin.CommandExecutor.senderHealth.get(event.getPlayer()));
				event.getPlayer().setFoodLevel(plugin.CommandExecutor.senderHunger.get(event.getPlayer()));

			}
		}

		if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer())) {

				plugin.CommandExecutor.spectator.get(event.getPlayer()).sendMessage("§7You were forced to stop spectating because the person who you were spectating disconnected.");

				plugin.CommandExecutor.spectator.get(event.getPlayer()).teleport(plugin.CommandExecutor.origLocation.get(plugin.CommandExecutor.spectator.get(event.getPlayer())));
				plugin.CommandExecutor.isSpectating.put(plugin.CommandExecutor.spectator.get(event.getPlayer()), false);
				plugin.CommandExecutor.isBeingSpectated.put(event.getPlayer(), false);
				plugin.CommandExecutor.spectator.get(event.getPlayer()).getInventory().clear();
				plugin.CommandExecutor.spectator.get(event.getPlayer()).getInventory().setContents(plugin.CommandExecutor.senderInv.get(plugin.CommandExecutor.spectator.get(event.getPlayer())));
				plugin.CommandExecutor.spectator.get(event.getPlayer()).getInventory().setArmorContents(plugin.CommandExecutor.senderArm.get(plugin.CommandExecutor.spectator.get(event.getPlayer())));
				plugin.CommandExecutor.spectator.get(event.getPlayer()).setHealth(plugin.CommandExecutor.senderHealth.get(plugin.CommandExecutor.spectator.get(event.getPlayer())));
				plugin.CommandExecutor.spectator.get(event.getPlayer()).setFoodLevel(plugin.CommandExecutor.senderHunger.get(plugin.CommandExecutor.spectator.get(event.getPlayer())));

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

		if (event instanceof Player) {

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

		if (event instanceof PlayerDeathEvent) {

			Player pla = (Player)event.getEntity();

			if (plugin.CommandExecutor.isBeingSpectated.get(pla) != null) {

				if (plugin.CommandExecutor.isBeingSpectated.get(pla)) {

					plugin.CommandExecutor.spectator.get(pla).sendMessage("§7You were forced to stop spectating because the person who you were spectating died.");

					plugin.CommandExecutor.spectator.get(pla).teleport(plugin.CommandExecutor.origLocation.get(plugin.CommandExecutor.spectator.get(pla)));
					plugin.CommandExecutor.isSpectating.put(plugin.CommandExecutor.spectator.get(pla), false);
					plugin.CommandExecutor.isBeingSpectated.put(pla, false);
					plugin.CommandExecutor.spectator.get(pla).getInventory().clear();
					plugin.CommandExecutor.spectator.get(pla).getInventory().setContents(plugin.CommandExecutor.senderInv.get(plugin.CommandExecutor.spectator.get(pla)));
					plugin.CommandExecutor.spectator.get(pla).getInventory().setArmorContents(plugin.CommandExecutor.senderArm.get(plugin.CommandExecutor.spectator.get(pla)));
					plugin.CommandExecutor.spectator.get(pla).setHealth(plugin.CommandExecutor.senderHealth.get(plugin.CommandExecutor.spectator.get(pla)));
					plugin.CommandExecutor.spectator.get(pla).setFoodLevel(plugin.CommandExecutor.senderHunger.get(plugin.CommandExecutor.spectator.get(pla)));

					for (Player playp : plugin.getServer().getOnlinePlayers()) {

						plugin.CommandExecutor.spectator.get(pla).showPlayer(playp);

					}

				}

			}

		}

	}

}
