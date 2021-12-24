package me.happybavarian07.events;

import me.happybavarian07.Marker;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class ListCraftAttackMarkerEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private List<Marker> markers;
    private final Player player;

    public ListCraftAttackMarkerEvent(List<Marker> markers, Player player) {
        this.markers = markers;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setMarkers(List<Marker> markers) { this.markers = markers; }

    public List<Marker> getMarkers() {
        return markers;
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
