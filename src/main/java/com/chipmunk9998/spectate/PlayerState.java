package com.chipmunk9998.spectate;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerState {

    public Player player;
    public ItemStack[] inventory;
    public ItemStack[] armor;
    public int hunger;
    public double maxHealth;
    public double health;
    public int level;
    public float exp;
    public int slot;
    public boolean allowFlight;
    public boolean isFlying;
    public GameMode mode;
    public Location location;

    public Collection<PotionEffect> potions;

    public ArrayList<Player> vanishedFrom = new ArrayList<Player>();

    public PlayerState(Player p) {
        player = p;
        inventory = p.getInventory().getContents();
        armor = p.getInventory().getArmorContents();
        hunger = p.getFoodLevel();
        maxHealth = p.getMaxHealth();
        health = p.getHealth();
        level = p.getLevel();
        exp = p.getExp();
        slot = p.getInventory().getHeldItemSlot();
        allowFlight = p.getAllowFlight();
        isFlying = p.isFlying();
        mode = p.getGameMode();
        location = p.getLocation();
        potions = p.getActivePotionEffects();
        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            if (players != p) {
                if (!players.canSee(p)) {
                    vanishedFrom.add(players);
                }
            }
        }
    }

}
