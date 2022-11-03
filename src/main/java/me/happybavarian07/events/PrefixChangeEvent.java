package me.happybavarian07.events;

import me.happybavarian07.main.Prefix;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PrefixChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Prefix oldPrefix;
    private final Player player;
    private boolean cancelled;
    private Prefix prefix;

    public PrefixChangeEvent(Prefix prefix, Prefix oldPrefix, Player player) {
        this.prefix = prefix;
        this.oldPrefix = oldPrefix;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Prefix getNewPrefix() {
        return prefix;
    }

    public void setNewPrefix(Prefix prefix) {
        this.prefix = prefix;
    }

    public Prefix getOldPrefix() {
        return oldPrefix;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
