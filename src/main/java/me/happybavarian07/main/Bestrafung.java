package me.happybavarian07.main;/*
 * @Author HappyBavarian07
 * @Date 13.11.2022 | 17:46
 */

import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Bestrafung {
    private final UUID player;
    private final long expirationDate;
    private final long creationDate;
    private final PotionEffectType potionEffectType;
    private int bestrafungsCount;
    private int amplifier;

    public Bestrafung(UUID player, long expirationDate, long creationDate, PotionEffectType potionEffectType, int bestrafungsCount, int amplifier) {
        this.player = player;
        this.expirationDate = expirationDate;
        this.creationDate = creationDate;
        this.potionEffectType = potionEffectType;
        this.bestrafungsCount = bestrafungsCount;
        this.amplifier = amplifier;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public UUID getPlayer() {
        return player;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public PotionEffectType getPotionEffectType() {
        return potionEffectType;
    }

    public int getBestrafungsCount() {
        return bestrafungsCount;
    }

    public void setBestrafungsCount(int bestrafungsCount) {
        this.bestrafungsCount = bestrafungsCount;
    }
}
