package me.happybavarian07.main;

public enum Prefix {
    // EMPTY, STANDARD, OFFLINE, ONLINE, REDSTONE, LIVE, RECORD, CAM
    EMPTY("", "", "Empty"),
    LOBBY(CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Lobby.Prefix", null),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Lobby.Suffix", null),
            "Lobby"),
    STANDARD(CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Standard.Prefix", null),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Standard.Suffix", null),
            "Standard"),
    OFFLINE(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Offline.Prefix", null),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Offline.Suffix", null),
            "Offline"),
    ONLINE(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Online.Prefix", null),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Online.Suffix", null),
            "Online"),
    REDSTONE(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Redstone.Prefix", null),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Redstone.Suffix", null),
            "Redstone"),
    LIVE(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Live.Prefix", null),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Live.Suffix", null),
            "Live"),
    AFK(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Afk.Prefix", null),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Afk.Suffix", null),
            "Afk"),
    RECORD(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Record.Prefix", null),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Record.Suffix", null),
            "Record"),
    CAM(
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Cam.Prefix", null),
            CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Cam.Suffix", null),
            "Cam");

    private final String inGamePrefix;
    private final String inGameSuffix;
    private final String prefixConfigName;

    Prefix(String inGamePrefix, String inGameSuffix, String prefixConfigName) {
        this.inGamePrefix = inGamePrefix;
        this.prefixConfigName = prefixConfigName;
        this.inGameSuffix = inGameSuffix;
    }

    public String getInGamePrefix() { return inGamePrefix; }

    public String getConfigName() { return prefixConfigName; }

    public String getInGameSuffix() { return inGameSuffix; }

    public static Integer getPrefixCount() {
        int count = 0;
        for(Prefix ignored : values()) {
            count += 1;
        }
        return count;
    }
}
