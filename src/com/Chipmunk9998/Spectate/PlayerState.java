package com.Chipmunk9998.Spectate;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerState {

	public Player player;
	public ItemStack[] inventory;
	public ItemStack[] armor;
	public int hunger;
	public double health;
	public int level;
	public float exp;
	public int slot;
	public GameMode mode;
	public Location location;

	public ArrayList<Player> vanishedFrom = new ArrayList<Player>();

	public PlayerState(Player p) {

		player = p;
		inventory = p.getInventory().getContents();
		armor = p.getInventory().getArmorContents();
		hunger = p.getFoodLevel();
		health = p.getHealth();
		level = p.getLevel();
		exp = p.getExp();
		slot = p.getInventory().getHeldItemSlot();
		mode = p.getGameMode();
		location = p.getLocation();

		for (Player players : p.getWorld().getPlayers()) {

			if (players != p ) {

				if (!players.canSee(p)) {

					vanishedFrom.add(players);

				}

			}

		}

	}

}
