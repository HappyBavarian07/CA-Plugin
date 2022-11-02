package me.happybavarian07.main;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Prefix {
    // EMPTY, STANDARD, OFFLINE, ONLINE, REDSTONE, LIVE, RECORD, CAM
    EMPTY("", "", "Empty", null),
    LOBBY(CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Lobby.Prefix", null, false),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Lobby.Suffix", null, false),
            "Lobby", null),
    STANDARD(CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Standard.Prefix", null, false),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Standard.Suffix", null, false),
            "Standard", null),
    OFFLINE(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Offline.Prefix", null, false),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Offline.Suffix", null, false),
            "Offline",
            CAPluginMain.getPlugin().getLanguageManager().getItem("PrefixItems.Offline", null, false).getType()),
    ONLINE(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Online.Prefix", null, false),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Online.Suffix", null, false),
            "Online",
            CAPluginMain.getPlugin().getLanguageManager().getItem("PrefixItems.Online", null, false).getType()),
    REDSTONE(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Redstone.Prefix", null, false),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Redstone.Suffix", null, false),
            "Redstone",
            CAPluginMain.getPlugin().getLanguageManager().getItem("PrefixItems.Redstone", null, false).getType()),
    LIVE(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Live.Prefix", null, false),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Live.Suffix", null, false),
            "Live",
            CAPluginMain.getPlugin().getLanguageManager().getItem("PrefixItems.Live", null, false).getType()),
    AFK(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Afk.Prefix", null, false),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Afk.Suffix", null, false),
            "Afk",
            CAPluginMain.getPlugin().getLanguageManager().getItem("PrefixItems.Afk", null, false).getType()),
    RECORD(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Record.Prefix", null, false),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Record.Suffix", null, false),
            "Record",
            CAPluginMain.getPlugin().getLanguageManager().getItem("PrefixItems.Record", null, false).getType()),
    CAM(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Cam.Prefix", null, false),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Cam.Suffix", null, false),
            "Cam", null);

    private final String inGamePrefix;
    private final String inGameSuffix;
    private final String prefixConfigName;
    private final Material menuMaterial;

    Prefix(String inGamePrefix, String inGameSuffix, String prefixConfigName, Material menuMaterial) {
        this.inGamePrefix = inGamePrefix;
        this.prefixConfigName = prefixConfigName;
        this.inGameSuffix = inGameSuffix;
        this.menuMaterial = menuMaterial;
    }

    public static Integer getPrefixCount() {
        int count = 0;
        for (Prefix ignored : values()) {
            count += 1;
        }
        return count;
    }

    public static Prefix fromMaterial(Material material) {
        if (material == null) return null;
        for (Prefix prefix : values()) {
            if (prefix.getMenuMaterial() == null) continue;
            if (!prefix.getMenuMaterial().equals(material)) continue;

            return prefix;
        }
        return null;
    }

    public static Prefix fromItem(ItemStack item) {
        return fromMaterial(item.getType());
    }

    public String getInGamePrefix() {
        return inGamePrefix;
    }

    public String getConfigName() {
        return prefixConfigName;
    }

    public String getInGameSuffix() {
        return inGameSuffix;
    }

    public Material getMenuMaterial() {
        return menuMaterial;
    }
}
