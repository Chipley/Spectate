package com.Chipmunk9998.Spectate;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerState {

	public Player player;
	public ItemStack[] inventory;
	public ItemStack[] armor;
	public int hunger;
	public int health;
	public int xp;
	public int slot;
	public Location location;

	public ArrayList<Player> vanishedFrom = new ArrayList<Player>();

	public PlayerState(Player p) {

		player = p;
		inventory = p.getInventory().getContents();
		armor = p.getInventory().getArmorContents();
		hunger = p.getFoodLevel();
		health = p.getHealth();
		xp = p.getTotalExperience();
		slot = p.getInventory().getHeldItemSlot();
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
