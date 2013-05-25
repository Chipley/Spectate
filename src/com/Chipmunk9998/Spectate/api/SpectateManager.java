package com.Chipmunk9998.Spectate.api;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

	private static HashMap<String, Integer> mode = new HashMap<String, Integer>();
	//private static HashMap<String, Integer> playerAngle = new HashMap<String, Integer>();
	//private static HashMap<String, Boolean> isScanning = new HashMap<String, Boolean>();

	private static HashMap<Player, PlayerState> states = new HashMap<Player, PlayerState>();

	private static void updateSpectators() {

		spectateTask = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			public void run() {

				for (Player p : plugin.getServer().getOnlinePlayers()) {

					if (isSpectating(p)) {

						if (roundTwoDecimals(p.getLocation().getX()) != roundTwoDecimals(getTarget(p).getLocation().getX()) || roundTwoDecimals(p.getLocation().getY()) != roundTwoDecimals(getTarget(p).getLocation().getY()) || roundTwoDecimals(p.getLocation().getZ()) != roundTwoDecimals(getTarget(p).getLocation().getZ()) || roundTwoDecimals(p.getLocation().getYaw()) != roundTwoDecimals(getTarget(p).getLocation().getYaw()) || roundTwoDecimals(p.getLocation().getPitch()) != roundTwoDecimals(getTarget(p).getLocation().getPitch())) {

							Location loc = new Location(getTarget(p).getWorld(), getTarget(p).getLocation().getX(), getTarget(p).getLocation().getY(), getTarget(p).getLocation().getZ(), getTarget(p).getLocation().getYaw(), getTarget(p).getLocation().getPitch());
							p.teleport(loc);

						}

						p.getInventory().setContents(getTarget(p).getInventory().getContents());
						p.getInventory().setArmorContents(getTarget(p).getInventory().getArmorContents());

						if (getTarget(p).getHealth() > 0) {

							p.setHealth(getTarget(p).getHealth());

						}else {

							p.setHealth(1);

						}

						p.setFoodLevel(getTarget(p).getFoodLevel());

						p.setLevel(0);
						p.setExp(0);
						p.setTotalExperience(getTarget(p).getTotalExperience());

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

		p.hidePlayer(target);

		p.setPlayerListName(playerListName);

		setSpectating(p, true);
		setBeingSpectated(target, true);

		p.sendMessage(ChatColor.GRAY + "You are now spectating " + target.getName() + ".");

	}

	public static void stopSpectating(Player p, boolean loadState) {

		setSpectating(p, false);
		setBeingSpectated(getTarget(p), false);

		removeSpectator(getTarget(p), p);

		if (loadState) {

			loadPlayerState(p);

		}

		for (Player onlinePlayers : plugin.getServer().getOnlinePlayers()) {

			if (!getVanishedFromList(p).contains(onlinePlayers)) {

				onlinePlayers.showPlayer(p);

			}

		}

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

		int number = 0;

		for (Player players : playerList) {

			number++;

			if (players.getName().equals(getTarget(p).getName())) {

				break;

			}

		}

		return playerList.indexOf(getTarget(p)) + 1;

	}

	public static void setSpectateMode(Player p, int newMode) {

		mode.put(p.getName(), newMode);

	}

	public static int getSpectateMode(Player p) {

		if (mode.get(p.getName()) == null) {

			return 1;

		}

		return mode.get(p.getName());

	}

	public static void setSpectateAngle(Player p, String mode) {



	}

	public static void getSpectateAngle(Player p) {



	}

	public static void startScanning(Player p, int interval) {



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

	public static PlayerState getPlayerState(Player p) {

		return states.get(p);

	}

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
		toPlayer.setTotalExperience(state.xp);
		toPlayer.getInventory().setHeldItemSlot(state.slot);
		toPlayer.teleport(state.location);

		states.remove(fromState);

	}

	public static ArrayList<Player> getVanishedFromList(Player p) {

		return getPlayerState(p).vanishedFrom;

	}

	public static void setPlugin(Spectate pl) {

		SpectateManager.plugin = pl;

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
