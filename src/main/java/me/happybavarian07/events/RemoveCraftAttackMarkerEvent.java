package me.happybavarian07.events;

import me.happybavarian07.Marker;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RemoveCraftAttackMarkerEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final Marker marker;
    private final Player player;

    public RemoveCraftAttackMarkerEvent(Marker marker, Player player) {
        this.marker = marker;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Marker getMarker() {
        return marker;
    }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
