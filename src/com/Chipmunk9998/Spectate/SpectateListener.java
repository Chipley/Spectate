package com.Chipmunk9998.Spectate;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public class SpectateListener implements Listener {

	public Spectate plugin;

	public SpectateListener(Spectate plugin) {

		this.plugin = plugin;

	}

	public void updatePlayer() {

		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			public void run() {

				for (Player p : plugin.getServer().getOnlinePlayers()) {

					if (plugin.CommandExecutor.isSpectating.get(p.getName()) != null) {

						if (plugin.CommandExecutor.isSpectating.get(p.getName())) {

							if (roundTwoDecimals(p.getLocation().getX()) != roundTwoDecimals(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getLocation().getX()) || roundTwoDecimals(p.getLocation().getY()) != roundTwoDecimals(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getLocation().getY()) || roundTwoDecimals(p.getLocation().getZ()) != roundTwoDecimals(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getLocation().getZ()) || roundTwoDecimals(p.getLocation().getYaw()) != roundTwoDecimals(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getLocation().getYaw()) || roundTwoDecimals(p.getLocation().getPitch()) != roundTwoDecimals(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getLocation().getPitch())) {

								Location loc = new Location(p.getLocation().getWorld(), plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getLocation().getX(), plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getLocation().getY(), plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getLocation().getZ());

								loc.setYaw(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getLocation().getYaw());

								loc.setPitch(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getLocation().getPitch());

								p.teleport(loc);

							}

							if (plugin.CommandExecutor.isInv.get(p.getName()) == null || plugin.CommandExecutor.isInv.get(p.getName())) {

								p.getInventory().setContents(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getInventory().getContents());
								p.getInventory().setArmorContents(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getInventory().getArmorContents());

							}

							if (plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getHealth() > 0) {

								p.setHealth(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getHealth());

							}else {

								p.setHealth(1);

							}

							p.setFoodLevel(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())).getFoodLevel());

						}

					}

				}

			}

		}, 0L, 1L);

	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {

		if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer().getName())) {

				String[] spectators = plugin.CommandExecutor.spectator.get(event.getPlayer().getName()).split(",");

				for (String player : spectators) {

					Player p = plugin.getServer().getPlayer(player);

					p.teleport(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(p.getName())));

				}

			}

		}

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName())) {

				event.getPlayer().teleport(plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(event.getPlayer().getName())));

			}

		}

	}


	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName())) {

				plugin.SpectateAPI.spectateOff(event.getPlayer());

				if (plugin.CommandExecutor.isScanning.get(event.getPlayer().getName()) != null) {

					if (plugin.CommandExecutor.isScanning.get(event.getPlayer().getName())) {

						plugin.CommandExecutor.isScanning.put(event.getPlayer().getName(), false);

						plugin.getServer().getScheduler().cancelTask(plugin.CommandExecutor.taskId.get(event.getPlayer().getName()));

					}

				}

			}
		}

		if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer().getName())) {

				String[] spectators = plugin.CommandExecutor.spectator.get(event.getPlayer().getName()).split(",");

				for (String player : spectators) {

					Player p = plugin.getServer().getPlayer(player);

					if ((plugin.CommandExecutor.mode.get(p.getName()) != null || plugin.CommandExecutor.mode.get(p.getName()).equals("2")) || (plugin.CommandExecutor.isScanning.get(p.getName()) != null || plugin.CommandExecutor.isScanning.get(p.getName()))) {

						ArrayList<Player> spectateablePlayers = plugin.SpectateAPI.getSpectateablePlayers();

						spectateablePlayers.remove(event.getPlayer());

						Player[] players = spectateablePlayers.toArray(new Player[spectateablePlayers.size()]);

						try {

							if (plugin.CommandExecutor.playerNumber.get(p.getName()) > players.length - 1) {

								plugin.SpectateAPI.spectateOn(p, players[0]);
								p.sendMessage("§7The person you were previously spectating has disconnected.");

							}else {

								plugin.SpectateAPI.spectateOn(p, players[plugin.CommandExecutor.playerNumber.get(p.getName())]);
								p.sendMessage("§7The person you were previously spectating has disconnected.");

							}

						}catch (ArrayIndexOutOfBoundsException e) {

							p.sendMessage("§7You were forced to stop spectating because there is nobody left to spectate.");
							plugin.SpectateAPI.spectateOff(p);

							if (plugin.CommandExecutor.isScanning.get(p.getName()) != null) {

								if (plugin.CommandExecutor.isScanning.get(p.getName())) {

									plugin.CommandExecutor.isScanning.put(p.getName(), false);

									plugin.getServer().getScheduler().cancelTask(plugin.CommandExecutor.taskId.get(p.getName()));

								}

							}

							return;

						}

						return;

					}

					p.sendMessage("§7You were forced to stop spectating because the person you were spectating disconnected.");
					plugin.SpectateAPI.spectateOff(p);

					if (plugin.CommandExecutor.isScanning.get(p.getName()) != null) {

						if (plugin.CommandExecutor.isScanning.get(p.getName())) {

							plugin.CommandExecutor.isScanning.put(p.getName(), false);

							plugin.getServer().getScheduler().cancelTask(plugin.CommandExecutor.taskId.get(p.getName()));

						}

					}

				}

			}

		}

	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName())) {

				event.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName())) {

				event.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName())) {

				event.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName())) {

				event.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {

		if (event.getEntity() instanceof Player) {

			Player target = (Player)event.getEntity();

			if (plugin.CommandExecutor.isSpectating.get(target.getName()) != null) {

				if (plugin.CommandExecutor.isSpectating.get(target.getName())) {

					if (event.getDamager() instanceof Projectile) {

						EntityDamageByEntityEvent bowDamage = new EntityDamageByEntityEvent(event.getDamager(), plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(target.getName())), event.getCause(), event.getDamage());

						plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(target.getName())).setLastDamageCause(bowDamage);

						plugin.getServer().getPlayer(plugin.CommandExecutor.target.get(target.getName())).damage(event.getDamage());

						event.getDamager().remove();

					}

					event.setCancelled(true);

					return;

				}

			}

		}

		if (event.getDamager() instanceof Player) {

			Player damager = (Player)event.getDamager();

			if (plugin.CommandExecutor.isSpectating.get(damager.getName()) != null) {

				if (plugin.CommandExecutor.isSpectating.get(damager.getName())) {

					event.setCancelled(true);

				}

			}

		}

	}

	@EventHandler
	public void projectileShootEvent(ProjectileLaunchEvent event) {

		if(event.getEntity().getShooter() instanceof Player) {

			Projectile projectile = event.getEntity();

			Player player = (Player)event.getEntity().getShooter();

			if (plugin.CommandExecutor.isBeingSpectated.get(player.getName()) != null) {

				if (plugin.CommandExecutor.isBeingSpectated.get(player.getName())) {

					Location loc = player.getLocation();

					Vector dir = loc.getDirection();

					dir.normalize();

					loc.add(dir.multiply(1));

					loc.setY(loc.getY() + 1.5);

					projectile.teleport(loc);

				}

			}

		}

	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {

		if (event instanceof PlayerDeathEvent) {

			Player pla = (Player)event.getEntity();

			if (plugin.CommandExecutor.isBeingSpectated.get(pla.getName()) != null) {

				if (plugin.CommandExecutor.isBeingSpectated.get(pla.getName())) {

					String[] spectators = plugin.CommandExecutor.spectator.get(pla.getName()).split(",");

					for (String player : spectators) {

						Player p = plugin.getServer().getPlayer(player);

						if ((plugin.CommandExecutor.mode.get(p.getName()) != null && plugin.CommandExecutor.mode.get(p.getName()).equals("2")) || (plugin.CommandExecutor.isScanning.get(p.getName()) != null && plugin.CommandExecutor.isScanning.get(p.getName()))) {

							ArrayList<Player> spectateablePlayers = plugin.SpectateAPI.getSpectateablePlayers();

							spectateablePlayers.remove(p);

							Player[] players = spectateablePlayers.toArray(new Player[spectateablePlayers.size()]);

							try {

								if (plugin.CommandExecutor.playerNumber.get(p.getName()) > players.length - 1) {

									plugin.SpectateAPI.spectateOn(p, players[0]);

								}else {

									plugin.SpectateAPI.spectateOn(p, players[plugin.CommandExecutor.playerNumber.get(p.getName())]);

								}

								p.sendMessage("§7The person you were previously spectating has died.");

							}catch (ArrayIndexOutOfBoundsException e) {

								p.sendMessage("§7You were forced to stop spectating because there is nobody left to spectate.");
								plugin.SpectateAPI.spectateOff(p);
								e.printStackTrace();

							}

							return;

						}

						p.sendMessage("§7You were forced to stop spectating because the person you were spectating died.");

						plugin.SpectateAPI.spectateOff(p);

					}

				}

			}

		}

	}

	@EventHandler
	public void onPlayerAnimation(PlayerAnimationEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName())) {

				if (plugin.CommandExecutor.mode.get(event.getPlayer().getName()) != null) {

					if (plugin.CommandExecutor.mode.get(event.getPlayer().getName()).equals("2")) {

						if (event.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) {

							if (plugin.getServer().getOnlinePlayers().length > 2) {

								if (plugin.CommandExecutor.isClick.get(event.getPlayer().getName()) != null) {

									if (plugin.CommandExecutor.isClick.get(event.getPlayer().getName())) {

										return;

									}

								}

								plugin.SpectateAPI.scrollLeft(event.getPlayer());

							}

						}

					}

				}

			}

		}

	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName())) {

				event.setCancelled(true);

				if (plugin.CommandExecutor.mode.get(event.getPlayer().getName()) != null) {

					if (plugin.CommandExecutor.mode.get(event.getPlayer().getName()).equals("2")) {

						if (plugin.getServer().getOnlinePlayers().length > 2) {

							if (plugin.CommandExecutor.isClick.get(event.getPlayer().getName()) != null) {

								if (plugin.CommandExecutor.isClick.get(event.getPlayer().getName())) {

									return;

								}

							}

							plugin.SpectateAPI.scrollRight(event.getPlayer());

						}

					}

				}

			}

		}

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName())) {

				if (plugin.CommandExecutor.mode.get(event.getPlayer().getName()) != null) {

					if (plugin.CommandExecutor.mode.get(event.getPlayer().getName()).equals("2")) {

						if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

							if (plugin.getServer().getOnlinePlayers().length > 2) {

								if (plugin.CommandExecutor.isClick.get(event.getPlayer().getName()) != null) {

									if (plugin.CommandExecutor.isClick.get(event.getPlayer().getName())) {

										return;

									}

								}

								plugin.SpectateAPI.scrollRight(event.getPlayer());


							}

						}

					}

				}

				event.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void onPlayerRegen(EntityRegainHealthEvent event) {

		if (event.getEntity() instanceof Player) {

			Player p = (Player) event.getEntity();

			if (plugin.CommandExecutor.isSpectating.get(p.getName()) != null) {

				if (plugin.CommandExecutor.isSpectating.get(p.getName())) {

					event.setCancelled(true);

				}

			}

		}

	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {

		if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer().getName())) {

				String[] spectators = plugin.CommandExecutor.spectator.get(event.getPlayer().getName()).split(",");

				for (String player : spectators) {

					Player p = plugin.getServer().getPlayer(player);

					p.openInventory(event.getInventory());

				}

			}

		}

	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {

		if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isBeingSpectated.get(event.getPlayer().getName())) {

				String[] spectators = plugin.CommandExecutor.spectator.get(event.getPlayer().getName()).split(",");

				for (String player : spectators) {

					Player p = plugin.getServer().getPlayer(player);

					p.closeInventory();

				}

			}

		}

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getWhoClicked().getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getWhoClicked().getName())) {

				event.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void onMobTarget(EntityTargetEvent event) {

		if (event.getEntity() instanceof Monster) {

			if (event.getTarget() instanceof Player) {

				if (plugin.CommandExecutor.isSpectating.get(((Player) event.getTarget()).getName()) != null) {

					if (plugin.CommandExecutor.isSpectating.get(((Player) event.getTarget()).getName())) {

						event.setCancelled(true);

					}

				}

			}

		}

	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {

		if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName()) != null) {

			if (plugin.CommandExecutor.isSpectating.get(event.getPlayer().getName())) {

				if (plugin.conf.getBoolean("Disable commands while spectating?")) {

					if (!event.getMessage().startsWith("/spectate") && !event.getMessage().startsWith("/spec")) {

						event.setCancelled(true);
						event.getPlayer().sendMessage("§cYou can not execute this command while spectating.");
						return;

					}

				}

			}

		}

	}


	public void clickEnable(final Player player) {

		if (plugin.CommandExecutor.isClick.get(player.getName()) == null || !plugin.CommandExecutor.isClick.get(player.getName())) {

			plugin.CommandExecutor.isClick.put(player.getName(), true);

		}

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			public void run() {

				plugin.CommandExecutor.isClick.put(player.getName(), false);

			}

		}, 5L);

	}

	double roundTwoDecimals(double d) {

		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));

	}

}
