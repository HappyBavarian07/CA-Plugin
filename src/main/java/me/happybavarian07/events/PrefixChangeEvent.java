package me.happybavarian07.events;

import me.happybavarian07.main.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PrefixChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Prefix newPrefix;
    private final Prefix oldPrefix;
    private final Player player;

    public PrefixChangeEvent(Prefix newPrefix, Prefix oldPrefix, Player player) {
        this.newPrefix = newPrefix;
        this.oldPrefix = oldPrefix;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Prefix getNewPrefix() {
        return newPrefix;
    }

    public Prefix getOldPrefix() {
        return oldPrefix;
    }

    public void setNewPrefix(Prefix newPrefix) {
        this.newPrefix = newPrefix;
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
