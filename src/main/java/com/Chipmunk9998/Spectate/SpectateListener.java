package com.Chipmunk9998.Spectate;

import com.Chipmunk9998.Spectate.api.ScrollDirection;
import com.Chipmunk9998.Spectate.api.SpectateMode;
import com.Chipmunk9998.Spectate.api.SpectateScrollEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class SpectateListener implements Listener {

    Spectate plugin;

    public SpectateListener(Spectate plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (Player p : Spectate.getAPI().getSpectatingPlayers()) {
            event.getPlayer().hidePlayer(p);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Spectate.getAPI().isSpectating(event.getPlayer())) {
            Spectate.getAPI().stopSpectating(event.getPlayer(), true);
        } else if (Spectate.getAPI().isBeingSpectated(event.getPlayer())) {
            for (Player p : Spectate.getAPI().getSpectators(event.getPlayer())) {
                if (Spectate.getAPI().getSpectateMode(p) == SpectateMode.SCROLL || Spectate.getAPI().isScanning(p)) {
                    SpectateScrollEvent scrollEvent = new SpectateScrollEvent(p, Spectate.getAPI().getSpectateablePlayers(), ScrollDirection.RIGHT);
                    Bukkit.getServer().getPluginManager().callEvent(scrollEvent);
                    ArrayList<Player> playerList = scrollEvent.getSpectateList();
                    playerList.remove(p);
                    playerList.remove(event.getPlayer());
                    p.sendMessage(ChatColor.GRAY + "The person you were previously spectating has disconnected.");
                    if (!Spectate.getAPI().scrollRight(p, playerList)) {
                        Spectate.getAPI().stopSpectating(p, true);
                        p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because there is nobody left to spectate.");
                    }
                } else {
                    Spectate.getAPI().stopSpectating(p, true);
                    p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because the person you were spectating disconnected.");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Spectate.getAPI().isBeingSpectated(event.getEntity())) {
            for (Player p : Spectate.getAPI().getSpectators(event.getEntity())) {
                if (Spectate.getAPI().getSpectateMode(p) == SpectateMode.SCROLL || Spectate.getAPI().isScanning(p)) {
                    SpectateScrollEvent scrollEvent = new SpectateScrollEvent(p, Spectate.getAPI().getSpectateablePlayers(), ScrollDirection.RIGHT);
                    Bukkit.getServer().getPluginManager().callEvent(scrollEvent);
                    ArrayList<Player> playerList = scrollEvent.getSpectateList();
                    playerList.remove(p);
                    playerList.remove(event.getEntity());
                    p.sendMessage(ChatColor.GRAY + "The person you were previously spectating has died.");
                    if (!Spectate.getAPI().scrollRight(p, playerList)) {
                        Spectate.getAPI().stopSpectating(p, true);
                        p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because there is nobody left to spectate.");
                    }
                } else {
                    Spectate.getAPI().stopSpectating(p, true);
                    p.sendMessage(ChatColor.GRAY + "You were forced to stop spectating because the person you were spectating died.");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getEntity();
        if (Spectate.getAPI().isSpectating(p)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (Spectate.getAPI().isSpectating((Player)event.getDamager())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (Spectate.getAPI().isSpectating(event.getPlayer())) {
            if (Spectate.getAPI().isReadyForNextScroll(event.getPlayer())) {
                if (Spectate.getAPI().getSpectateMode(event.getPlayer()) == SpectateMode.SCROLL) {
                    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        if (Bukkit.getServer().getOnlinePlayers().size() > 2) {
                            Spectate.getAPI().scrollLeft(event.getPlayer(), Spectate.getAPI().getSpectateablePlayers());
                            Spectate.getAPI().disableScroll(event.getPlayer(), 5);
                        }
                    } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if (Bukkit.getServer().getOnlinePlayers().size() > 2) {
                            Spectate.getAPI().scrollRight(event.getPlayer(), Spectate.getAPI().getSpectateablePlayers());
                            Spectate.getAPI().disableScroll(event.getPlayer(), 5);
                        }
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (Spectate.getAPI().isSpectating(event.getPlayer())) {
            if (Spectate.getAPI().isReadyForNextScroll(event.getPlayer())) {
                if (Spectate.getAPI().getSpectateMode(event.getPlayer()) == SpectateMode.SCROLL) {
                    if (Bukkit.getServer().getOnlinePlayers().size() > 2) {
                        Spectate.getAPI().scrollRight(event.getPlayer(), Spectate.getAPI().getSpectateablePlayers());
                        Spectate.getAPI().disableScroll(event.getPlayer(), 5);
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    //TODO: Throwable objects don't work, arrows do.
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            if (Spectate.getAPI().isBeingSpectated((Player)event.getEntity().getShooter())) {
                Location fromLocation = event.getEntity().getLocation();
                Vector toLocation = fromLocation.clone().getDirection().multiply(0.5);
                Location finalLoc = fromLocation.clone().add(toLocation);
                event.getEntity().teleport(finalLoc);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!event.isCancelled()) {
                if (Spectate.getAPI().isBeingSpectated(player)) {
                    for (Player p : Spectate.getAPI().getSpectators(player)) {
                        p.setFoodLevel(event.getFoodLevel());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (!event.isCancelled()) {
            if (Spectate.getAPI().isBeingSpectated(event.getPlayer())) {
                for (Player p : Spectate.getAPI().getSpectators(event.getPlayer())) {
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
        if (Spectate.getAPI().isBeingSpectated(p)) {
            for (Player spectators : Spectate.getAPI().getSpectators(p)) {
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
        if (Spectate.getAPI().isBeingSpectated(p)) {
            for (Player spectators : Spectate.getAPI().getSpectators(p)) {
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
        if (Spectate.getAPI().isSpectating(p)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (Spectate.getAPI().isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (Spectate.getAPI().isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Spectate.getAPI().isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Spectate.getAPI().isSpectating(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (Spectate.getAPI().isSpectating(p)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        if (Spectate.getAPI().isSpectating(event.getPlayer())) {
            Spectate.getAPI().getTarget(event.getPlayer()).giveExp(event.getAmount());
            event.setAmount(0);
        }
    }

    @EventHandler
    public void onMobTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Monster) {
            if (event.getTarget() instanceof Player) {
                if (Spectate.getAPI().isSpectating(((Player)event.getTarget()))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (Spectate.getAPI().isSpectating(event.getPlayer())) {
            if (plugin.disable_commands) {
                if (!event.getMessage().startsWith("/spectate") && !event.getMessage().startsWith("/spec")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You can not execute this command while spectating.");
                }
            }
        }
    }

}
