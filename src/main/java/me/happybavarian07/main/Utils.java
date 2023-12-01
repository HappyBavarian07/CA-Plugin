package me.happybavarian07.main;

import me.clip.placeholderapi.PlaceholderAPI;
import me.happybavarian07.events.PrefixChangeEvent;
import me.happybavarian07.mysql.MySQLHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Utils {

    private static CAPluginMain plugin;
    private static FileConfiguration prefixConfig;
    private static MySQLHandler mySQLHandler;
    private static Map<UUID, BukkitRunnable> tablistRunnables;

    public Utils(CAPluginMain plugin, FileConfiguration prefixconfig) {
        Utils.plugin = plugin;
        Utils.prefixConfig = prefixconfig;
        Utils.mySQLHandler = plugin.getMySQLHandler();
        tablistRunnables = new HashMap<>();
    }

    public static CAPluginMain getPlugin() {
        return plugin;
    }

    public static FileConfiguration getPrefixConfig() {
        return prefixConfig;
    }

    /**
     * Sends Empty Messages to a specific Player
     * as often as the Integer lines is.
     *
     * @param lines  Lines to clear
     * @param player Player for message
     */
    public static void sendEmptyMessage(int lines, Player player) {
        for (int i = 0; i < lines; i++) {
            player.sendMessage("");
        }
    }

    /**
     * Announce an Message to the Server
     *
     * @param message Message to announce
     */
    public static void annouce(String message) {
        message = format(null, message, CAPluginMain.getPrefix());

        Bukkit.broadcastMessage(message);
    }

    /**
     * Announce an Message with an ChatColor to the Server
     *
     * @param message Message to announce
     * @param color   Color fo the Message
     */
    public static void coloredAnnouce(String message, ChatColor color) {
        Bukkit.broadcastMessage(color + message);
    }

    /**
     * Announce an Message with an ChatColor to the Console
     *
     * @param message Message to announce
     * @param color   Color fo the Message
     */
    public static void coloredAnnouceConsole(String message, ChatColor color) {
        Bukkit.getConsoleSender().sendMessage(color + message);
    }

    /**
     * Announce an Message to the Console
     *
     * @param message Message to announce for the Console
     */
    public static void annouceConsole(String message) {
        message = format(null, message, CAPluginMain.getPrefix());

        Bukkit.getConsoleSender().sendMessage(message);
    }

    /**
     * Replace all Color Code Characters such as &1, &2, ...
     *
     * @param message Message to set Playerholders for
     * @return Formatted String with Placeholders
     */
    public static String format(Player player, String message, String prefix) {
        return PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', message.replace("%prefix%", prefix)));
    }

    /**
     * Get Infos from a Player in a String List
     * Die Wörter in "" sind die Keys
     * "name" = Name: Name of the Player
     * "displayname" = DisplayName: The Name that is shown ingame
     * "customname" = CustomName: An Custom Name
     * "address" = Address: Ip Address
     * "health" = Health: Health
     * "maxhealth" = MaxHealth: MaxHealth
     * "worldname" = Worldname: Name of the World the Player is in
     * "playerlistname" = PlayerListName: Name of the Player in the Tablist
     * "entityid" = EntityID (not UUID): EntityID
     * "gamemode" = GameMode Name: GameMode Name
     * "uuid" = UUID
     * "clientviewdistance" = Client View Distance
     * "allowflight" = Allow Flight
     * "level" = Level
     * "exp" = Experience
     * "flyspeed" = Flyspeed
     *
     * @param player To get the Infos from
     * @return List<String> with the Infos</String>
     */
    public static Map<String, String> getPlayerInfos(Player player) {
        Map<String, String> infos = new HashMap<>();
        if (player == null) return null;
        // 0
        infos.put("name", player.getName());
        // 1
        infos.put("displayname", player.getDisplayName());
        // 2
        infos.put("customname", player.getCustomName());
        // 3
        infos.put("address", player.getAddress().toString());
        // 4
        infos.put("health", String.valueOf(player.getHealth()));
        // 5
        infos.put("maxhealth", String.valueOf(player.getMaxHealth()));
        // 6
        infos.put("worldname", player.getWorld().getName());
        // 7
        infos.put("playerlistname", player.getPlayerListName());
        // 8
        infos.put("entityid", String.valueOf(player.getEntityId()));
        // 9
        infos.put("gamemode", player.getGameMode().toString());
        // 10
        infos.put("uuid", player.getUniqueId().toString());
        // 11
        infos.put("clientviewdistance", String.valueOf(player.getClientViewDistance()));
        // 12
        infos.put("allowflight", String.valueOf(player.getAllowFlight()));
        // 14
        infos.put("level", String.valueOf(player.getLevel()));
        // 15
        infos.put("exp", String.valueOf(player.getExp()));
        // 16
        infos.put("flyspeed", String.valueOf(player.getFlySpeed()));
        return infos;
    }

    public static void setPlayerPrefix(Player player, Prefix prefix, boolean statusMessage) {
        boolean isDev = player.getUniqueId().equals(UUID.fromString("0c069d0e-5778-4d51-8929-6b2f69b475c0"));
        if(prefix == null) throw new NullPointerException("Prefix is null! Either use an Existing Prefix or create a new one in the prefixes.yml file using the scheme at the Top!");
        if (prefix.getConfigName().equals("CAPluginDev") && !isDev) {
            prefix = plugin.getPrefix("Standard");
        }
        PrefixChangeEvent prefixChangeEvent = new PrefixChangeEvent(prefix, getPrefixFromConfig(player), player);
        Bukkit.getPluginManager().callEvent(prefixChangeEvent);
        if (!prefixChangeEvent.isCancelled()) {
            World playerworld = prefixChangeEvent.getPlayer().getWorld();
            World craftattackworld = plugin.getCraftAttackWorld();
            if (!playerworld.equals(craftattackworld)) return;
            String MessageSplitter = CAPluginMain.getPlugin().getLanguageManager().getMessage("Chat.Splitter", null, false);
            String PlayerPrefix = prefixChangeEvent.getNewPrefix().getInGamePrefix();
            String PlayerSuffix = prefixChangeEvent.getNewPrefix().getInGameSuffix();
            if (player.hasPermission("ca.admin.orga")) {
                PlayerSuffix = Utils.format(player, " &f[&4Orga&f]", CAPluginMain.getPrefix());
            }
            if (prefix.getConfigName().equals("CAPluginDev") && isDev) {
                player.setCustomName(prefix.getInGamePrefix() + player.getName());
                player.setCustomNameVisible(true);
            }

            if (statusMessage)
                prefixChangeEvent.getPlayer().sendMessage(format(prefixChangeEvent.getPlayer(), "&3Status: &f" + prefixChangeEvent.getNewPrefix().getConfigName() + "&3 gesetzt!\n" +
                                "   Chatformat: " + PlayerPrefix + "%player_name%" + PlayerSuffix + MessageSplitter + "<Message>\n" +
                                "   Tablistformat: " + PlayerPrefix + "%player_name%" + PlayerSuffix,
                        CAPluginMain.getPrefix()));
            savePrefixToConfig(isDev ? prefix : prefixChangeEvent.getNewPrefix(), prefixChangeEvent.getPlayer().getUniqueId());
            loadTablistForPlayer(prefixChangeEvent.getPlayer(), true);
        }
    }

    public static Prefix getPrefixFromConfig(Player player) {
        if (plugin.getConfig().getStringList("Lobby-Worlds").contains(player.getWorld().getName()))
            return plugin.getPrefix("Lobby");
        return mySQLHandler.getPrefix(player.getUniqueId().toString());

        /*if(!prefixConfig.contains(player.getUniqueId().toString())) return Prefix.STANDARD;

        try {
            return Prefix.valueOf(prefixConfig.getString(player.getUniqueId().toString()).toUpperCase());
        } catch (NullPointerException e) {
            return Prefix.STANDARD;
        }*/
    }

    private static void savePrefixToConfig(Prefix prefix, UUID uuid) {
        // EMPTY, STANDARD, OFFLINE, ONLINE, REDSTONE, LIVE, RECORD, CAM, etc.
        mySQLHandler.setPrefix(uuid.toString(), prefix);
        /*try {
            prefixConfig.set(uuid.toString(), prefix.getConfigName());
            prefixConfig.save(plugin.getPrefixFile());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static String getTablistHeaderAsString(List<String> headers, Player player) {
        String end = "";
        int count = 0;
        for (String s : headers) {
            if (count >= headers.size()) {
                end = end + s;
            } else {
                end = end + (s + "\n§r");
            }
            count++;
        }
        return Utils.format(player, end, CAPluginMain.getPrefix());
    }

    public static String getTablistFooterAsString(List<String> footers, Player player) {
        String end = "";
        int count = 0;
        for (String s : footers) {
            if (count >= footers.size()) {
                end = end + s;
            } else {
                end = end + (s + "\n§r");
            }
            count++;
        }
        return Utils.format(player, end, CAPluginMain.getPrefix());
    }

    public static void loadTablistForPlayer(Player player, boolean headerandfooter) {
        if (headerandfooter) {
            String tablistHeader = getTablistHeaderAsString(plugin.getConfig().getStringList("CA.settings.Tablist.Header"), player);
            String tablistFooter = getTablistFooterAsString(plugin.getConfig().getStringList("CA.settings.Tablist.Footer"), player);
            player.setPlayerListHeader(tablistHeader);
            player.setPlayerListFooter(tablistFooter);
        }
        String prefix = getPrefixFromConfig(player).getInGamePrefix();
        String suffix = getPrefixFromConfig(player).getInGameSuffix();
        if (player.hasPermission("ca.admin.orga")) {
            suffix = Utils.format(player, " &f[&4Orga&f]", CAPluginMain.getPrefix());
        }
        player.setPlayerListName(prefix + player.getDisplayName() + suffix);
    }

    public static World randomLobby() {
        World lobbyWorld = null;
        List<String> worlds = plugin.getConfig().getStringList("Lobby-Worlds");
        Random rand = new Random();

        if (worlds.size() == 0) {
            return Bukkit.getWorlds().get(0);
        }

        while (lobbyWorld == null) {
            try {
                String randomWorldName = worlds.get(rand.nextInt(worlds.size()));
                if (randomWorldName.equals("") || Bukkit.getWorld(randomWorldName) == null) {
                    lobbyWorld = Bukkit.getWorlds().get(0);
                    break;
                }
                lobbyWorld = Bukkit.getWorld(randomWorldName);
            } catch (Exception e) {
                e.printStackTrace();
                lobbyWorld = Bukkit.getWorlds().get(0);
                break;
            }
        }
        return lobbyWorld;
    }

    public static List<String> emptyList() {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("");
        return list;
    }

    public static String formatChatMessage(Player player, String message, boolean colors, boolean placeholders) {
        if (colors && placeholders) {
            return PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', message));
        } else if (colors) {
            return ChatColor.translateAlternateColorCodes('&', message);
        } else {
            return PlaceholderAPI.setPlaceholders(player, message);
        }
    }
}
