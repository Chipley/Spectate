package com.Chipmunk9998.Spectate;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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

							p.getInventory().setContents(plugin.CommandExecutor.target.get(p).getInventory().getContents());
							p.getInventory().setArmorContents(plugin.CommandExecutor.target.get(p).getInventory().getArmorContents());

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

				plugin.SpectateExtras.spectateOff(event.getPlayer());

			}
		}

		if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer())) {

				String[] spectators = plugin.CommandExecutor.spectator.get(event.getPlayer()).split(",");

				for (String player : spectators) {

					Player p = plugin.getServer().getPlayer(player);

					if (plugin.CommandExecutor.mode.get(p) != null) {

						if (plugin.CommandExecutor.mode.get(p).equals("2")) {	

							ArrayList<Player> spectateablePlayers = new ArrayList<Player>();

							for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {

								if (!onlinePlayers.isDead() && onlinePlayers != p && onlinePlayers != event.getPlayer()) {

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

							try {

								if (plugin.CommandExecutor.playerNumber.get(p) > players.length - 1) {

									plugin.SpectateExtras.spectateOn(p, players[0]);
									p.sendMessage("§7The person you were previously spectating has disconnected.");

								}else {

									plugin.SpectateExtras.spectateOn(p, players[plugin.CommandExecutor.playerNumber.get(p)]);
									p.sendMessage("§7The person you were previously spectating has disconnected.");

								}

							}catch (ArrayIndexOutOfBoundsException e) {

								p.sendMessage("§7You were forced to stop spectating because there is nobody left to spectate.");
								plugin.SpectateExtras.spectateOff(p);
								return;

							}

							return;

						}

					}
					
					p.sendMessage("§7You were forced to stop spectating because the person you were spectating disconnected.");
					plugin.SpectateExtras.spectateOff(p);

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

						if (plugin.CommandExecutor.mode.get(p) != null) {

							if (plugin.CommandExecutor.mode.get(p).equals("2")) {

								ArrayList<Player> spectateablePlayers = new ArrayList<Player>();

								for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {

									if (!onlinePlayers.isDead() && onlinePlayers != p) {

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

								try {

									if (plugin.CommandExecutor.playerNumber.get(p) > players.length - 1) {

										plugin.SpectateExtras.spectateOn(p, players[0]);
										p.sendMessage("§7The person you were previously spectating has died.");

									}else {

										plugin.SpectateExtras.spectateOn(p, players[plugin.CommandExecutor.playerNumber.get(p)]);
										p.sendMessage("§7The person you were previously spectating has died.");

									}

								}catch (ArrayIndexOutOfBoundsException e) {

									p.sendMessage("§7You were forced to stop spectating because there is nobody left to spectate.");
									plugin.SpectateExtras.spectateOff(p);
									e.printStackTrace();

								}

								return;

							}

						}
						
						p.sendMessage("§7You were forced to stop spectating because the person you were spectating died.");

						plugin.SpectateExtras.spectateOff(p);

					}

				}

			}

		}

	}

	@EventHandler
	public void onPlayerAnimation(PlayerAnimationEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {

				if (plugin.CommandExecutor.mode.get(event.getPlayer()) != null) {

					if (plugin.CommandExecutor.mode.get(event.getPlayer()).equals("2")) {

						if (event.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) {

							if (plugin.getServer().getOnlinePlayers().length > 2) {

								if (plugin.CommandExecutor.isClick.get(event.getPlayer()) != null) {

									if (plugin.CommandExecutor.isClick.get(event.getPlayer())) {

										return;

									}

								}

								plugin.SpectateExtras.scrollLeft(event.getPlayer());

							}

						}

					}

				}

			}

		}

	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {
				
				event.setCancelled(true);

				if (plugin.CommandExecutor.mode.get(event.getPlayer()) != null) {

					if (plugin.CommandExecutor.mode.get(event.getPlayer()).equals("2")) {

						if (plugin.getServer().getOnlinePlayers().length > 2) {

							if (plugin.CommandExecutor.isClick.get(event.getPlayer()) != null) {

								if (plugin.CommandExecutor.isClick.get(event.getPlayer())) {

									return;

								}

							}

							plugin.SpectateExtras.scrollRight(event.getPlayer());

						}

					}

				}

			}

		}

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer())) {

				if (plugin.CommandExecutor.mode.get(event.getPlayer()) != null) {

					if (plugin.CommandExecutor.mode.get(event.getPlayer()).equals("2")) {

						if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

							if (plugin.getServer().getOnlinePlayers().length > 2) {

								if (plugin.CommandExecutor.isClick.get(event.getPlayer()) != null) {

									if (plugin.CommandExecutor.isClick.get(event.getPlayer())) {

										return;

									}

								}

								plugin.SpectateExtras.scrollRight(event.getPlayer());


							}

						}else {

							event.setCancelled(true);
							return;

						}

					}

				}

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

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {

		if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer())) {

				String[] spectators = plugin.CommandExecutor.spectator.get(event.getPlayer()).split(",");

				for (String player : spectators) {

					Player p = plugin.getServer().getPlayer(player);

					p.openInventory(event.getInventory());

				}

			}

		}

	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {

		if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer()) != null) {

			if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer())) {

				String[] spectators = plugin.CommandExecutor.spectator.get(event.getPlayer()).split(",");

				for (String player : spectators) {

					Player p = plugin.getServer().getPlayer(player);

					p.closeInventory();

				}

			}

		}

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getWhoClicked()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getWhoClicked())) {

				event.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void onMobTarget(EntityTargetEvent event) {

		if (event.getEntity() instanceof Monster) {

			if (event.getTarget() instanceof Player) {

				if (plugin.CommandExecutor.isSpectating.get((Player)event.getTarget()) != null) {

					if (plugin.CommandExecutor.isSpectating.get((Player)event.getTarget())) {

						event.setCancelled(true);

					}

				}

			}

		}

	}


	public void clickEnable(final Player player) {

		if (plugin.CommandExecutor.isClick.get(player) == null || !plugin.CommandExecutor.isClick.get(player)) {

			plugin.CommandExecutor.isClick.put(player, true);

		}

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			public void run() {

				plugin.CommandExecutor.isClick.put(player, false);

			}

		}, 5L);

	}

	double roundTwoDecimals(double d) {

		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));

	}

}
