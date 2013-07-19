package com.Chipmunk9998.Spectate.api;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.server.v1_6_R2.EntityPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.Chipmunk9998.Spectate.PlayerState;
import com.Chipmunk9998.Spectate.Spectate;

public class SpectateManager {

	private static Spectate plugin;
	private static int spectateTask = -1;

	private static ArrayList<Player> isSpectating = new ArrayList<Player>();
	private static ArrayList<Player> isBeingSpectated = new ArrayList<Player>();
	private static HashMap<Player, ArrayList<Player>> spectators = new HashMap<Player, ArrayList<Player>>();
	private static HashMap<Player, Player> target = new HashMap<Player, Player>();

	public static ArrayList<String> isClick = new ArrayList<String>();

	private static HashMap<String, Integer> playerMode = new HashMap<String, Integer>();
	private static HashMap<String, Integer> playerAngle = new HashMap<String, Integer>();
	
	private static ArrayList<String> isScanning = new ArrayList<String>();
	private static HashMap<String, Integer> scanTask = new HashMap<String, Integer>();

	private static HashMap<Player, PlayerState> states = new HashMap<Player, PlayerState>();

	private static void updateSpectators() {

		spectateTask = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			public void run() {

				for (Player p : plugin.getServer().getOnlinePlayers()) {

					if (isSpectating(p)) {

						if (getSpectateAngle(p) == 1) {

							if (roundTwoDecimals(p.getLocation().getX()) != roundTwoDecimals(getTarget(p).getLocation().getX()) || roundTwoDecimals(p.getLocation().getY()) != roundTwoDecimals(getTarget(p).getLocation().getY()) || roundTwoDecimals(p.getLocation().getZ()) != roundTwoDecimals(getTarget(p).getLocation().getZ()) || roundTwoDecimals(p.getLocation().getYaw()) != roundTwoDecimals(getTarget(p).getLocation().getYaw()) || roundTwoDecimals(p.getLocation().getPitch()) != roundTwoDecimals(getTarget(p).getLocation().getPitch())) {

								p.teleport(getTarget(p));

							}

						}else {

							if (getSpectateAngle(p) != 4) {

								p.teleport(getSpectateLocation(p));

							}

						}

						p.getInventory().setContents(getTarget(p).getInventory().getContents());
						p.getInventory().setArmorContents(getTarget(p).getInventory().getArmorContents());

						if (getTarget(p).getHealth() > 0) {

							p.setHealth(getTarget(p).getHealth());

						}else {

							p.setHealth(1);

						}
						
						p.setLevel(getTarget(p).getLevel());
						p.setExp(getTarget(p).getExp());

						p.getInventory().setHeldItemSlot(getTarget(p).getInventory().getHeldItemSlot());

					}

				}

			}

		}, 0L, 1L);

	}

	public static void startSpectateTask() {

		if (spectateTask == -1) {

			updateSpectators();

		}

	}

	public static void stopSpectateTask() {

		if (spectateTask != -1) {

			plugin.getServer().getScheduler().cancelTask(spectateTask);
			spectateTask = -1;

		}

	}

	public static void startSpectating(Player p, Player target) {

		if (isSpectating(p)) {

			setBeingSpectated(getTarget(p), false);
			p.showPlayer(getTarget(p));
			removeSpectator(getTarget(p), p);

		}

		setTarget(p, target);
		addSpectator(target, p);

		String playerListName = p.getPlayerListName();

		for (Player player1 : plugin.getServer().getOnlinePlayers()) {

			player1.hidePlayer(p);

		}

		if (getSpectateAngle(p) == 1) {

			p.hidePlayer(target);

		}else {

			p.showPlayer(target);

		}

		p.setPlayerListName(playerListName);

		p.setGameMode(target.getGameMode());
		p.setFoodLevel(target.getFoodLevel());
		
		setExperienceCooldown(p, Integer.MAX_VALUE);

		setSpectating(p, true);
		setBeingSpectated(target, true);

		p.sendMessage(ChatColor.GRAY + "You are now spectating " + target.getName() + ".");

	}

	public static void stopSpectating(Player p, boolean loadState) {

		setSpectating(p, false);
		setBeingSpectated(getTarget(p), false);

		removeSpectator(getTarget(p), p);
		
		if (isScanning(p)) {
			
			stopScanning(p);
			
		}

		if (loadState) {

			loadPlayerState(p);

		}
		
		setExperienceCooldown(p, 0);

		p.showPlayer(getTarget(p));

	}

	public static boolean scrollRight(Player p, ArrayList<Player> playerList) {

		SpectateScrollEvent event = new SpectateScrollEvent(p, playerList, ScrollDirection.RIGHT);
		plugin.getServer().getPluginManager().callEvent(event);

		playerList = event.getSpectateList();

		playerList.remove(p);

		if (playerList.size() == 0) {

			return false;

		}

		int scrollToIndex = 1;

		if (getScrollNumber(p, playerList) == playerList.size()) {

			scrollToIndex = 1;

		}else {

			scrollToIndex = getScrollNumber(p, playerList) + 1;

		}

		startSpectating(p, playerList.get(scrollToIndex - 1));

		return true;

	}

	public static boolean scrollLeft(Player p, ArrayList<Player> playerList) {

		SpectateScrollEvent event = new SpectateScrollEvent(p, playerList, ScrollDirection.LEFT);
		plugin.getServer().getPluginManager().callEvent(event);

		playerList = event.getSpectateList();

		playerList.remove(p);

		if (playerList.size() == 0) {

			return false;

		}

		int scrollToIndex = 1;

		if (getScrollNumber(p, playerList) == 1) {

			scrollToIndex = playerList.size();

		}else {

			scrollToIndex = getScrollNumber(p, playerList) - 1;

		}

		startSpectating(p, playerList.get(scrollToIndex - 1));

		return true;

	}

	public static int getScrollNumber(Player p, ArrayList<Player> playerList) {

		if (!isSpectating(p)) {

			return 1;

		}

		if (!playerList.contains(getTarget(p))) {

			return 1;

		}

		playerList.remove(p);

		return playerList.indexOf(getTarget(p)) + 1;

	}

	public static void setSpectateMode(Player p, int newMode) {

		playerMode.put(p.getName(), newMode);

	}

	public static int getSpectateMode(Player p) {

		if (playerMode.get(p.getName()) == null) {

			return 1;

		}

		return playerMode.get(p.getName());

	}

	public static void setSpectateAngle(Player p, int newAngle) {

		if (SpectateManager.isSpectating(p)) {

			if (newAngle == 1) {

				p.hidePlayer(SpectateManager.getTarget(p));

			}else {

				p.showPlayer(SpectateManager.getTarget(p));

			}

			if (newAngle == 4) {

				p.teleport(SpectateManager.getTarget(p));

			}

		}

		playerAngle.put(p.getName(), newAngle);

	}

	public static int getSpectateAngle(Player p) {

		if (playerAngle.get(p.getName()) == null) {

			return 1;

		}

		return playerAngle.get(p.getName());

	}

	public static void startScanning(final Player p, int interval) {
		
		isScanning.add(p.getName());

		scanTask.put(p.getName(), plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			public void run() {

				scrollRight(p, getSpectateablePlayers());

			}

		}, 0, 20 * interval));

	}
	
	public static void stopScanning(Player p) {
		
		plugin.getServer().getScheduler().cancelTask(scanTask.get(p.getName()));
		isScanning.remove(p.getName());
		
	}
	
	public static boolean isScanning(Player p) {
		
		if (isScanning.contains(p.getName())) {
			
			return true;
			
		}
		
		return false;
		
	}

	public static ArrayList<Player> getSpectateablePlayers() {

		ArrayList<Player> spectateablePlayers = new ArrayList<Player>();

		for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {

			if (onlinePlayers.isDead()) {

				continue;

			}

			if (isSpectating.contains(onlinePlayers.getName())) {

				continue;

			}

			/*

			if (plugin.config.getBoolean("cantspectate Permission Enabled?")) {

				if (onlinePlayers.hasPermission("spectate.cantspectate")) {

					continue;

				}

			}

			 */

			spectateablePlayers.add(onlinePlayers);

		}

		return spectateablePlayers;

	}

	private static void setTarget(Player p, Player ptarget) {

		target.put(p, ptarget);

	}

	public static Player getTarget(Player p) {

		return target.get(p);

	}

	public static boolean isSpectating(Player p) {

		return isSpectating.contains(p);

	}

	public static boolean isBeingSpectated(Player p) {

		return isBeingSpectated.contains(p);

	}

	private static void setBeingSpectated(Player p, boolean beingSpectated) {

		if (beingSpectated) {

			if (isBeingSpectated.contains(p)) {

				return;

			}

			isBeingSpectated.add(p);

		}else {

			isBeingSpectated.remove(p);

		}

	}

	private static void addSpectator(Player p, Player spectator) {

		if (spectators.get(p) == null) {

			ArrayList<Player> newSpectators = new ArrayList<Player>();

			newSpectators.add(spectator);

			spectators.put(p, newSpectators);

		}else {

			spectators.get(p).add(spectator);

		}

	}

	private static void removeSpectator(Player p, Player spectator) {

		if (spectators.get(p) == null) {

			return;

		}else {

			if (spectators.get(p).size() == 1) {

				spectators.remove(p);

			}else {

				spectators.get(p).remove(spectator);

			}

		}

	}

	public static ArrayList<Player> getSpectators(Player p) {

		return spectators.get(p);

	}

	public static ArrayList<Player> getSpectatingPlayers() {

		ArrayList<Player> spectatingPlayers = new ArrayList<Player>();

		for (Player p : plugin.getServer().getOnlinePlayers()) {

			if (isSpectating(p)) {

				spectatingPlayers.add(p);

			}

		}

		return spectatingPlayers;

	}

	private static void setSpectating(Player p, boolean spectating) {

		if (spectating) {

			if (isSpectating.contains(p)) {

				return;

			}

			isSpectating.add(p);

		}else {

			isSpectating.remove(p);

		}

	}

	public static void clickEnable(final Player player) {

		if (!isClick.contains(player.getName())) {

			isClick.add(player.getName());

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				public void run() {

					isClick.remove(player.getName());

				}

			}, 5L);

		}

	}

	public static Location getSpectateLocation(Player p) {

		Location playerLoc = getTarget(p).getLocation();

		double currentSubtraction = 0;
		Location previousLoc = playerLoc;

		while (currentSubtraction <= 5)  {

			playerLoc = getTarget(p).getLocation();

			Vector v = getTarget(p).getLocation().getDirection().normalize();
			v.multiply(currentSubtraction);

			if (getSpectateAngle(p) == 2) {

				playerLoc.subtract(v);

			}else if (getSpectateAngle(p) == 3) {

				playerLoc.add(v);

				if (playerLoc.getYaw() < -180) {

					playerLoc.setYaw(playerLoc.getYaw() + 180);

				}else {

					playerLoc.setYaw(playerLoc.getYaw() - 180);

				}

				playerLoc.setPitch(-playerLoc.getPitch());

			}

			Material tempMat = new Location(playerLoc.getWorld(), playerLoc.getX(), playerLoc.getY() + 1.5, playerLoc.getZ()).getBlock().getType();

			if (tempMat != Material.AIR && tempMat != Material.WATER && tempMat != Material.STATIONARY_WATER) {

				return previousLoc;

			}

			previousLoc = playerLoc;

			currentSubtraction += 0.5;

		}

		return playerLoc;

	}

	public static PlayerState getPlayerState(Player p) {

		return states.get(p);

	}
	
	//vanish them
	//teleport to same world as target
	//save state
	
	//restore inventory
	//teleport them back to original world

	public static void savePlayerState(Player p) {

		PlayerState playerstate = new PlayerState(p);
		states.put(p, playerstate);

	}

	public static void loadPlayerState(Player toPlayer) {

		loadPlayerState(toPlayer, toPlayer);

	}

	public static void loadPlayerState(Player fromState, Player toPlayer) {

		PlayerState state = getPlayerState(fromState);
		
		toPlayer.getInventory().setContents(state.inventory);
		toPlayer.getInventory().setArmorContents(state.armor);
		toPlayer.setFoodLevel(state.hunger);
		toPlayer.setHealth(state.health);
		toPlayer.setLevel(state.level);
		toPlayer.setExp(state.exp);
		toPlayer.getInventory().setHeldItemSlot(state.slot);
		toPlayer.setGameMode(state.mode);
		
		toPlayer.teleport(state.location);

		for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {

			if (!getVanishedFromList(fromState).contains(onlinePlayers)) {

				onlinePlayers.showPlayer(fromState);

			}

		}

		states.remove(fromState);

	}

	public static ArrayList<Player> getVanishedFromList(Player p) {

		return getPlayerState(p).vanishedFrom;

	}

	public static void setPlugin(Spectate pl) {

		SpectateManager.plugin = pl;

	}

	public static void setExperienceCooldown(Player p, int cooldown) {

		CraftPlayer craft = (CraftPlayer) p;
		EntityPlayer entity = (EntityPlayer) craft.getHandle();
		entity.bv = cooldown;

	}

	public static double roundTwoDecimals(double d) {

		try {

			DecimalFormat twoDForm = new DecimalFormat("#.##");
			return Double.valueOf(twoDForm.format(d));

		} catch (NumberFormatException e) {

			return d;

		}

	}

}
