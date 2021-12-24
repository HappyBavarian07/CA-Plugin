package me.happybavarian07.events;

import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.main.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LobbyTeleportEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final Player player;
    private Location startlocation;
    private Location endlocation;
    private String locName;
    String message;

    public LobbyTeleportEvent(Player player, Location startlocation, Location endlocation, String locName, String message) {
        this.player = player;
        this.startlocation = startlocation;
        this.endlocation = endlocation;
        this.locName = locName;
        this.message = message;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getStartlocation() {
        return startlocation;
    }

    public void setStartlocation(Location startlocation) {
        this.startlocation = startlocation;
    }

    public void setEndlocation(Location endlocation) {
        this.endlocation = endlocation;
    }

    public Location getEndlocation() {
        return endlocation;
    }

    public String getLocName() { return locName; }

    public String getMessage() {
        return Utils.format(player, message, CAPluginMain.getPrefix());
    }

    public void setMessage(String message) {
        this.message = Utils.format(player, message, CAPluginMain.getPrefix());
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }
}
