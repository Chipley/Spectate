package com.Chipmunk9998.Spectate;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.Chipmunk9998.Spectate.api.ScrollDirection;
import com.Chipmunk9998.Spectate.api.SpectateManager;
import com.Chipmunk9998.Spectate.api.SpectateScrollEvent;

public class SpectateListener implements Listener {

	Spectate plugin;

	public SpectateListener(Spectate plugin) {

		this.plugin = plugin;

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		for (Player p : SpectateManager.getSpectatingPlayers()) {

			event.getPlayer().hidePlayer(p);

		}

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		if (SpectateManager.isSpectating(event.getPlayer())) {

			SpectateManager.stopSpectating(event.getPlayer(), true);
			return;

		}else if (SpectateManager.isBeingSpectated(event.getPlayer())) {

			for (Player p : SpectateManager.getSpectators(event.getPlayer())) {

				if (SpectateManager.getSpectateMode(event.getPlayer()) == 2) {

					SpectateScrollEvent scrollEvent = new SpectateScrollEvent(p, SpectateManager.getSpectateablePlayers(), ScrollDirection.LEFT);
					plugin.getServer().getPluginManager().callEvent(event);

					ArrayList<Player> playerList = scrollEvent.getSpectateList();

					p.sendMessage(ChatColor.GRAY + "The person you were previously spectating has disconnected.");

					if (!SpectateManager.scrollRight(event.getPlayer(), playerList)) {

						p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because there is nobody left to spectate.");

					}

				}else {

					SpectateManager.stopSpectating(p, true);
					p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because the person you were spectating disconnected.");

				}

			}

			return;

		}

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {

		if (SpectateManager.isBeingSpectated(event.getEntity())) {

			for (Player p : SpectateManager.getSpectators(event.getEntity())) {

				if (SpectateManager.getSpectateMode(event.getEntity()) == 2) {

					SpectateScrollEvent scrollEvent = new SpectateScrollEvent(p, SpectateManager.getSpectateablePlayers(), ScrollDirection.LEFT);
					plugin.getServer().getPluginManager().callEvent(event);

					ArrayList<Player> playerList = scrollEvent.getSpectateList();

					p.sendMessage(ChatColor.GRAY + "The person you were previously spectating has died.");

					if (!SpectateManager.scrollRight(event.getEntity(), playerList)) {

						p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because there is nobody left to spectate.");

					}

				}else {

					SpectateManager.stopSpectating(p, true);
					p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because the person you were spectating died.");

				}

			}

			return;

		}

	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {

		if (event instanceof EntityDamageByEntityEvent) {

			EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) event;

			if (event1.getDamager() instanceof Player) {

				if (SpectateManager.isSpectating((Player)event1.getDamager())) {

					event.setCancelled(true);
					return;

				}

			}

		}

		if (!(event.getEntity() instanceof Player)) {

			return;

		}

		Player p = (Player) event.getEntity();

		if (SpectateManager.isSpectating(p)) {

			event.setCancelled(true);
			return;

		}

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (SpectateManager.isSpectating(event.getPlayer())) {

			if (!SpectateManager.isClick.contains(event.getPlayer().getName())) {

				if (SpectateManager.getSpectateMode(event.getPlayer()) == 2) {

					if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {

						if (plugin.getServer().getOnlinePlayers().length > 2) {

							SpectateManager.scrollLeft(event.getPlayer(), SpectateManager.getSpectateablePlayers());
							SpectateManager.clickEnable(event.getPlayer());

						}

					}else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

						if (plugin.getServer().getOnlinePlayers().length > 2) {

							SpectateManager.scrollRight(event.getPlayer(), SpectateManager.getSpectateablePlayers());
							SpectateManager.clickEnable(event.getPlayer());

						}

					}

				}

			}

			event.setCancelled(true);
			return;

		}

	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

		if (SpectateManager.isSpectating(event.getPlayer())) {

			if (!SpectateManager.isClick.contains(event.getPlayer().getName())) {

				if (SpectateManager.getSpectateMode(event.getPlayer()) == 2) {

					if (plugin.getServer().getOnlinePlayers().length > 2) {

						SpectateManager.scrollRight(event.getPlayer(), SpectateManager.getSpectateablePlayers());
						SpectateManager.clickEnable(event.getPlayer());

					}

				}

			}

			event.setCancelled(true);
			return;

		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {

		if (event.getEntity() instanceof Player) {

			Player player = (Player) event.getEntity();

			if (!event.isCancelled()) {

				if (SpectateManager.isBeingSpectated(player)) {

					for (Player p : SpectateManager.getSpectators(player)) {

						p.setFoodLevel(event.getFoodLevel());

					}

				}

			}

		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {

		if (!event.isCancelled()) {

			if (SpectateManager.isBeingSpectated(event.getPlayer())) {

				for (Player p : SpectateManager.getSpectators(event.getPlayer())) {

					p.setGameMode(event.getNewGameMode());

				}

			}

		}

	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {

		if (!(event.getPlayer() instanceof Player)) {

			return;

		}

		Player p = (Player) event.getPlayer();

		if (SpectateManager.isBeingSpectated(p)) {

			for (Player spectators : SpectateManager.getSpectators(p)) {

				spectators.openInventory(event.getInventory());

			}

		}

	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {

		if (!(event.getPlayer() instanceof Player)) {

			return;

		}

		Player p = (Player) event.getPlayer();

		if (SpectateManager.isBeingSpectated(p)) {

			for (Player spectators : SpectateManager.getSpectators(p)) {

				spectators.closeInventory();

			}

		}

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		if (!(event.getWhoClicked() instanceof Player)) {

			return;

		}

		Player p = (Player) event.getWhoClicked();

		if (SpectateManager.isSpectating(p)) {

			event.setCancelled(true);

		}

	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {

		if (SpectateManager.isSpectating(event.getPlayer())) {

			event.setCancelled(true);

		}

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		if (SpectateManager.isSpectating(event.getPlayer())) {

			event.setCancelled(true);

		}

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		if (SpectateManager.isSpectating(event.getPlayer())) {

			event.setCancelled(true);

		}

	}

	@EventHandler
	public void onPlayerRegen(EntityRegainHealthEvent event) {

		if (event.getEntity() instanceof Player) {

			Player p = (Player) event.getEntity();

			if (SpectateManager.isSpectating(p)) {

				event.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void onMobTarget(EntityTargetEvent event) {

		if (event.getEntity() instanceof Monster) {

			if (event.getTarget() instanceof Player) {

				if (SpectateManager.isSpectating(((Player)event.getTarget()))) {

					event.setCancelled(true);

				}

			}

		}

	}

	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {

		//check the option for disabling commands in the config and cancel it here if the command isn't /spectate

	}

}
