package com.Chipmunk9998.Spectate.api;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpectateScrollEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player scroller;
    private ArrayList<Player> scrollList;
    private ScrollDirection direction;

    public SpectateScrollEvent(Player scroller, ArrayList<Player> scrollList, ScrollDirection direction) {
        this.scroller = scroller;
        this.scrollList = scrollList;
        this.direction = direction;
    }

    public Player getPlayer() {
        return scroller;
    }

    public ArrayList<Player> getSpectateList() {
        return scrollList;
    }

    public ScrollDirection getDirection() {
        return direction;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
