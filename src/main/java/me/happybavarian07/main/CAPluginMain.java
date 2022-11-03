package me.happybavarian07.main;

import de.happybavarian07.adminpanel.main.AdminPanelMain;
import de.happybavarian07.adminpanel.utils.VersionComparator;
import de.slikey.effectlib.EffectManager;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import me.happybavarian07.API.CraftAttackExtension;
import me.happybavarian07.API.FileManager;
import me.happybavarian07.API.StartUpLogger;
import me.happybavarian07.GUIs.SelectorInv;
import me.happybavarian07.GUIs.adminpanelmenus.PrefixMenu;
import me.happybavarian07.GUIs.prefixes.PrefixConfig;
import me.happybavarian07.GUIs.prefixes.PrefixInventory;
import me.happybavarian07.commandManagers.CraftAttackCommandManager;
import me.happybavarian07.commandManagers.DebugCommandManager;
import me.happybavarian07.commandManagers.MarkerCommandManager;
import me.happybavarian07.commandManagers.WorldCommandManager;
import me.happybavarian07.commandmanagement.CommandManagerRegistry;
import me.happybavarian07.commands.afkcommand;
import me.happybavarian07.configs.DiscordConfig;
import me.happybavarian07.configs.InfoConfig;
import me.happybavarian07.configs.RuleConfig;
import me.happybavarian07.configupdater.ConfigUpdater;
import me.happybavarian07.discord.Discord;
import me.happybavarian07.language.LanguageFile;
import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.language.OldLanguageFileUpdater;
import me.happybavarian07.language.PerPlayerLanguageHandler;
import me.happybavarian07.listeners.*;
import me.happybavarian07.mysql.MySQLHandler;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CAPluginMain extends JavaPlugin implements Listener {

    public static EffectManager em;
    private static CAPluginMain plugin;
    private static String prefix;
    //public ProtocolManager pman;
    public File prefixFile;
    public FileConfiguration spawnconfig;
    public FileManager fm;
    public Material waehrung;
    public Map<Player, BukkitTask> spawnvorgang;
    List<String> staff = new ArrayList<>();
    StartUpLogger logger;
    PluginManager pm;
    ItemStack playerheaditem;
    private Discord discord;
    private boolean rulesActivated;
    private boolean worldChangeCheck;
    private RuleConfig ruleConfig;
    private InfoConfig infoConfig;
    private World craftAttackWorld;
    private DiscordConfig discordConfig;
    private MySQLHandler mySQLHandler;
    private PrefixInventory prefixInventory;
    private LanguageManager languageManager;
    private boolean discordEnabled;
    private CommandManagerRegistry commandManagerRegistry;
    private PluginFileLogger fileLogger;
    private de.happybavarian07.adminpanel.utils.NewUpdater updater;
    private OldLanguageFileUpdater langFileUpdater;
    private boolean languageManagerEnabled;
    private FileConfiguration dataYML;
    private boolean lobbySystemEnabled;
    // <ConfigName, Prefix>
    private Map<String, Prefix> prefixList;
    private FileConfiguration prefixConfig;

    public static CAPluginMain getPlugin() {
        return plugin;
    }

    public void setPlugin(CAPluginMain plugin) {
        CAPluginMain.plugin = plugin;
    }

    public static String getPrefix() {
        return prefix;
    }

    public boolean isLobbySystemEnabled() {
        return lobbySystemEnabled;
    }

    public void setLobbySystemEnabled(boolean lobbySystemEnabled) {
        this.lobbySystemEnabled = lobbySystemEnabled;
    }

    public FileManager getFileMan() {
        return fm;
    }

    public MySQLHandler getMySQLHandler() {
        return mySQLHandler;
    }

    public PrefixInventory getPrefixInventory() {
        return prefixInventory;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public CommandManagerRegistry getCommandManagerRegistry() {
        return commandManagerRegistry;
    }

    public RuleConfig getRuleConfig() {
        return ruleConfig;
    }

    public InfoConfig getInfoConfig() {
        return infoConfig;
    }

    public PluginFileLogger getFileLogger() {
        return fileLogger;
    }

    public de.happybavarian07.adminpanel.utils.NewUpdater getUpdater() {
        return updater;
    }

    public OldLanguageFileUpdater getLangFileUpdater() {
        return langFileUpdater;
    }

    public String getPluginVersion(Plugin plugin) {
        return plugin.getDescription().getVersion();
    }

    public void reloadPrefixConfig() {
        if(this.prefixFile == null)
            this.prefixFile = new File(getDataFolder(), "prefixes.yml");

        this.prefixConfig = YamlConfiguration.loadConfiguration(this.prefixFile);

        InputStream defaultStream = getResource("prefixes.yml");
        if(defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.prefixConfig.setDefaults(defaultConfig);
        }
    }

    public void onEnable() {
        prefixList = new HashMap<>();
        spawnvorgang = new HashMap<>();

        setPlugin(this);
        logger = new StartUpLogger();
        saveDefaultConfig();
        this.mySQLHandler = new MySQLHandler(getConfig().getBoolean("CA.settings.MySQL.disabled", false));
        setLobbySystemEnabled(getConfig().getBoolean("LobbySystemEnabled", true));
        setupDiscord();
        if (Bukkit.getPluginManager().isPluginEnabled("EffectLib")) {
            em = new EffectManager(getPlugin());
        }
        waehrung = Material.DIAMOND;
        infoConfig = new InfoConfig(this);
        //pman = ProtocolLibrary.getProtocolManager();
        rulesActivated = getConfig().getBoolean("CA.rules.activate");
        worldChangeCheck = getConfig().getBoolean("CA.world.Access_Check");
        prefix = Utils.format(null, getConfig().getString("CA.PluginPrefix", "[CA-Plugin]"), "");
        languageManager = new LanguageManager(this, new File(getDataFolder(), "/languages"), prefix);
        langFileUpdater = new OldLanguageFileUpdater();
        if (Bukkit.getPluginManager().isPluginEnabled("Admin-Panel")) {
            updater = new de.happybavarian07.adminpanel.utils.NewUpdater(AdminPanelMain.getPlugin(), 98642, "CA-Plugin-%version%.jar", this,
                    "https://github.com/HappyBavarian07/CA-Plugin/releases/latest/download/CA-Plugin-" + getPluginVersion(this) + ".jar", true);
        } else {
            logger.spacer().coloredMessage(ChatColor.RED, "The Plugin Admin-Panel is not installed and because of that the Updater can't function!");
        }
        commandManagerRegistry = new CommandManagerRegistry(this);
        fileLogger = new PluginFileLogger();
        if (!fileLogger.getLogFile().exists()) {
            logger.spacer().message("§c§lCreating plugin.log file!§r");
            fileLogger.createLogFile();
            logger.message("§e§lDone!§r");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("CommandPanels"))
            this.prefixInventory = new PrefixInventory(getPlugin(), new PrefixConfig(getPlugin()), "Prefix_Panel");


        fm = new FileManager(this);
        pm = this.getServer().getPluginManager();

        /*
         * Creating Configs
         */
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            logger.message("§a§lInitialized PlaceHolderAPI!");
        } else {
            logger.coloredSpacer(ChatColor.RED);
            logger.message("§4§lCould not find PlaceholderAPI!!");
            logger.message("§4§lPlugin can not work without it!");
            logger.coloredSpacer(ChatColor.RED);
            pm.disablePlugin(this);
        }
        logger.coloredSpacer(ChatColor.BLUE).message("§6Creating and initializing Configs:§r").spacer();
        try {
            ConfigUpdater.update(this, "config.yml", new File(this.getDataFolder() + "/config.yml"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fm.createFile("", "spawnloc", "yml");
        fm.createFile("", "prefixes", "yml");
        prefixConfig = fm.getConfig("", "prefixes", "yml");
        if (!prefixConfig.contains("Prefixes"))
            prefixConfig.createSection("Prefixes");
        prefixConfig.options().header("The Prefix Format is the following:" + "\n" +
                "<PrefixName>:" + "\n" +
                "  prefix: '<Prefix>'" + "\n" +
                "  suffix: '<Suffix>'" + "\n" +
                "  configName: '<ConfigName>'" + "\n" +
                "  menuMaterial: '<Material>'");
        for (String prefixName : prefixConfig.getConfigurationSection("Prefixes").getKeys(false)) {
            ConfigurationSection section;
            if ((section = prefixConfig.getConfigurationSection("Prefixes." + prefixName)) != null) {
                if (prefixList.containsKey(section.getString("configName"))) continue;
                Prefix prefix = new Prefix(
                        Utils.formatChatMessage(null, section.getString("prefix"), true, true),
                        Utils.formatChatMessage(null, section.getString("suffix"), true, true),
                        section.getString("configName"),
                        section.getString("menuMaterial") == null ? Material.BARRIER : Material.matchMaterial(section.getString("menuMaterial")));
                prefixList.put(prefix.getConfigName(), prefix);
            }
        }
        //fm.createFile("", "FakePlates", "yml");
        //fm.createFile("", "ChestLog", "yml");
        spawnconfig = fm.getConfig("", "spawnloc", "yml");
        prefixFile = fm.getFile("", "prefixes", "yml");
        new Utils(getPlugin(), prefixConfig);
        new CraftAttackExtension().register();
        logger.message("§e§lDone§r").emptySpacer().spacer();

        /*
         * Crafting Recipes
         */
        logger.message("Initializing Crafting Recipes:§r").spacer();
        logger.message("§eInvisible Item Frames§r");
        getServer().addRecipe(getInvisibleItemFramesRecipe());
        if (!getConfig().getBoolean("CA.settings.PlayerHeads.Killing") || getConfig().getBoolean("CA.settings.PlayerHeads.Command")) {
            logger.message("§ePlayer Heads§r");
            playerheaditem = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = playerheaditem.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§7Empty Player Head");
            List<String> lore = new ArrayList<>();
            lore.add("§4Nun musst du nur noch 64 Diamanten in einer der ersten Slots halten,");
            lore.add("§4den Player Skull in der hand haben und /skull <name> eingeben!");
            meta.setLore(lore);
            playerheaditem.setItemMeta(meta);
            getServer().addRecipe(getPlayerHeadRecipe());
        }
        logger.message("§e§lDone§r").emptySpacer();
        /*
         * Register Commands
         */
        registercommands(logger);
        /*
         * Register Events
         */
        registerevents(logger, pm);
        /*
         * Save Default Configs
         */
        logger.message("§2Saving default Config:§r");
        saveDefaultConfig();
        setCraftAttackWorld(Bukkit.getWorld(getConfig().getString("CA.world.CraftAttack_World", "world")));
        ruleConfig = new RuleConfig(this);
        try {
            prefixConfig.save(prefixFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         * Starting Elytra Listener
         */
        logger.coloredSpacer(ChatColor.BLUE).message("§4Starting Elytra Event Listener§r").coloredSpacer(ChatColor.BLUE);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getInventory().getChestplate() != null && online.getInventory().getChestplate().getType() == Material.ELYTRA) {
                    return;
                }
                if (online.getWorld().getName().equals(getConfig().getString("CA.world.CraftAttack_World"))) {
                    if ((online.getGameMode() == GameMode.SPECTATOR) || (online.getGameMode() == GameMode.CREATIVE)) {
                        return;
                    }
                    if (online.isOnGround()) {
                        online.setGliding(false);
                        online.setAllowFlight(false);
                    }
                    if (online.hasMetadata("CraftAttackPluginSpawnElytra")) {
                        online.setGliding(true);
                        online.setAllowFlight(true);
                        online.setFallDistance(0);
                    } else if (!online.hasMetadata("CraftAttackPluginSpawnElytra")) {
                        online.setGliding(false);
                        online.setAllowFlight(false);
                        online.setFlying(false);
                    }
                }
            }
        }, 0L, 1L);
        getCommandManagerRegistry().register(new CraftAttackCommandManager());
        getCommandManagerRegistry().register(new DebugCommandManager());
        getCommandManagerRegistry().register(new WorldCommandManager());
        getCommandManagerRegistry().register(new MarkerCommandManager());

        dataYML = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/data.yml"));
        languageManager.setPlhandler(new PerPlayerLanguageHandler(languageManager, new File(getDataFolder() + "/data.yml"), dataYML));

        LanguageFile deLang = new LanguageFile(this, "de");
        LanguageFile enLang = new LanguageFile(this, "en");
        languageManager.addLanguagesToList(true);
        languageManager.addLang(deLang, deLang.getLangName());
        languageManager.addLang(enLang, enLang.getLangName());
        languageManager.setCurrentLang(languageManager.getLang(getConfig().getString("CA.settings.language"), true), true);
        setLanguageManagerEnabled(languageManager != null && languageManager.getCurrentLang() != null);
        if (isLanguageManagerEnabled()) {
            getServer().getConsoleSender().sendMessage(languageManager.getMessage("Plugin.Enabled", null, false));
        } else {
            getServer().getConsoleSender().sendMessage("[CA-Plugin] enabled!");
        }
        prefixList.put("Empty", new Prefix("", "", "Empty", Material.BARRIER));
        prefixList.put("Lobby", new Prefix(CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Lobby.Prefix", null, false),
                CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Lobby.Suffix", null, false),
                "Lobby", Material.BARRIER));
        prefixList.put("Standard", new Prefix(CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Standard.Prefix", null, false),
                CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Standard.Suffix", null, false),
                "Standard", Material.BARRIER));
        prefixList.put("Cam", new Prefix(CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Cam.Prefix", null, false),
                CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Cam.Suffix", null, false),
                "Cam", Material.BARRIER));
        prefixList.put("Afk", new Prefix(CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Afk.Prefix", null, false),
                CAPluginMain.getPlugin().getLanguageManager().getMessage("Prefix.Afk.Suffix", null, false),
                "Afk", Material.BARRIER));
        //System.out.println("Prefix List: " + prefixList);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(getCraftAttackWorld())) {
                    Utils.setPlayerPrefix(player, Utils.getPrefixFromConfig(player) != null ? Utils.getPrefixFromConfig(player) : plugin.getPrefix("Empty"), false);
                    Utils.loadTablist(player, true);
                }
            }
        }, 300L);
        if (getConfig().getBoolean("CA.settings.Updater.AutomaticLanguageFileUpdating")) {
            languageManager.reloadLanguages(null, false);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Admin-Panel")) {
            updater.setVersionComparator(VersionComparator.SEMATIC_VERSION);
            if (plugin.getConfig().getBoolean("CA.settings.Updater.checkForUpdates")) {
                updater.checkForUpdates(true);
                if (updater.updateAvailable()) {
                    updater.downloadLatestUpdate(plugin.getConfig().getBoolean("CA.settings.Updater.automaticReplace"), plugin.getConfig().getBoolean("Plugin.Updater.downloadPluginUpdate"), true);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        updater.checkForUpdates(false);
                        if (updater.updateAvailable()) {
                            updater.downloadLatestUpdate(plugin.getConfig().getBoolean("CA.settings.Updater.automaticReplace"), plugin.getConfig().getBoolean("Plugin.Updater.downloadPluginUpdate"), true);
                        }
                    }
                }.runTaskTimer(plugin, (plugin.getConfig().getLong("CA.settings.Updater.UpdateCheckTime") * 60 * 20), (plugin.getConfig().getLong("CA.settings.Updater.UpdateCheckTime") * 60 * 20));
            }
        }
    }

    public void reloadData() {
        saveResource("data.yml", false);
        dataYML = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/data.yml"));
    }

    public Prefix getPrefix(String configName) {
        int count = 0;
        for (String key : prefixList.keySet()) {
            count++;
            if (configName.equalsIgnoreCase(key)) {
                //System.out.println("Key: " + key);
                //System.out.println("Value: " + prefixList.get(key));
                //System.out.println("Count: " + count);
                return prefixList.get(key);
            }
        }
        return null;
    }

    public Prefix prefixFromMaterial(Material menuMaterial) {
        for (Prefix prefix : prefixList.values()) {
            if (prefix.getMenuMaterial().equals(menuMaterial)) return prefix;
        }
        return null;
    }

    public boolean isLanguageManagerEnabled() {
        return languageManagerEnabled;
    }

    private void setLanguageManagerEnabled(boolean languageManagerEnabled) {
        this.languageManagerEnabled = languageManagerEnabled;
    }

    public World getCraftAttackWorld() {
        return craftAttackWorld;
    }

    public void setCraftAttackWorld(World craftAttackWorld) {
        this.craftAttackWorld = craftAttackWorld;
    }

    public FileConfiguration getChestLogCfg() {
        return fm.getConfig("", "ChestLog", "yml");
    }

    public File getChestLogFile() {
        return fm.getFile("", "ChestLog", "yml");
    }

    public void addStaff(String name) {
        staff.add(name);
    }

    public boolean isStaff(String name) {
        return staff.contains(name);
    }

    public void removeStaff(String name) {
        staff.remove(name);
    }

    public int getStaffCount() {
        return staff.size();
    }

    public void setupDiscord() {
        this.discordConfig = new DiscordConfig(getPlugin());
        FileConfiguration tempconfig = getDiscordFileConfig();
        setDiscordEnabled(tempconfig.getBoolean("Settings.enabled", false));
        if (isDiscordEnabled()) {
            this.discord = new Discord(getPlugin(), tempconfig.getString("Bot.Token"), Activity.playing(tempconfig.getString("Bot.Activity", "Minecraft CraftAttack")));
            discord.sendServerStartMessage();
        }
    }

    public boolean isDiscordEnabled() {
        return discordEnabled;
    }

    public void setDiscordEnabled(boolean discordEnabled) {
        this.discordEnabled = discordEnabled;
    }

    public Discord getDiscord() {
        return discord;
    }

    public FileConfiguration getDiscordFileConfig() {
        return discordConfig.getConfig();
    }

    public File getDiscordFile() {
        return discordConfig.getConfigFile();
    }

    public DiscordConfig getDiscordConfig() {
        return discordConfig;
    }

    public void registercommands(StartUpLogger logger) {
        logger.coloredSpacer(ChatColor.BLUE);
        logger.message("§4Register Commands:§r");
        AllCommandsCompleter ccom = new AllCommandsCompleter();
        Objects.requireNonNull(getCommand("build")).setExecutor(new LobbyEventListener(getPlugin()));
        logger.message("§6/afk§r");
        Objects.requireNonNull(getCommand("afk")).setExecutor(new afkcommand());
        logger.message("§6/prefix§r");
        Objects.requireNonNull(getCommand("prefix")).setTabCompleter(ccom);
        //Objects.requireNonNull(getCommand("troll")).setExecutor(new TrollIOpenCommand());
        //Objects.requireNonNull(getCommand("trollvanish")).setExecutor(new TrollVanishCommand());
        logger.message("§e§lDone§r").spacer();
        logger.emptySpacer();
    }

    public void registerevents(StartUpLogger logger, PluginManager pm2) {
        logger.spacer().message("§4Register Events:§r");
        logger.message("§6Wichtige Kontroll Events:§r");
        logger.message("(World Change, JoinEvent, Elytra, ...)");
        pm2.registerEvents(new CraftAttackEvents(this), this);
        pm2.registerEvents(new PlayerManager(this), this);
        pm2.registerEvents(new MoreCraftAttackEvents(this), this);
        logger.message("§6Afk Events§r");
        pm2.registerEvents(new afkcommand(), this);
        logger.message("§6Bed Events§r");
        pm2.registerEvents(new BedListener(), this);
        //pm2.registerEvents(new TrollItemsGUI(this, getConfig()), this);
        if (isLobbySystemEnabled())
            pm2.registerEvents(new LobbyEventListener(getPlugin()), this);
        pm2.registerEvents(new SelectorInv(this, this.getConfig()), this);
        logger.message("§e§lDone§r").coloredSpacer(ChatColor.BLUE).emptySpacer().coloredSpacer(ChatColor.BLUE);
    }

    public boolean isRulesActivated() {
        return rulesActivated;
    }

    public boolean isWorldChangeCheck() {
        return worldChangeCheck;
    }

    public void onDisable() {
        HandlerList.unregisterAll(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(this.getName())));
        ruleConfig = null;
        fm = null;
        waehrung = null;
        infoConfig = null;
        pm = null;

        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Player online2 : Bukkit.getOnlinePlayers()) {
                p.showPlayer(online2);
            }
            new LobbyEventListener(getPlugin()).removeBuilder(p.getUniqueId());
        }
        logger.emptySpacer().emptySpacer().emptySpacer();
        logger.coloredSpacer(ChatColor.RED).message(prefix + " wurde erfolgreich entladen!").coloredSpacer(ChatColor.RED);
        logger.emptySpacer().emptySpacer().emptySpacer();
        logger = null;
        FileConfiguration tempconfig = getDiscordFileConfig();
        if (tempconfig.getBoolean("Settings.enabled")) {
            discord.sendServerStopMessage();
            this.discord.getBuilder().removeEventListeners(this.discord);
            discord.getBot().shutdown();
            discord = null;
        }
        if (isLanguageManagerEnabled()) {
            getServer().getConsoleSender().sendMessage(languageManager.getMessage("Plugin.Disabled", null, false));
        } else {
            getServer().getConsoleSender().sendMessage("[CA-Plugin] disabled!");
        }
        languageManager = null;
        setPlugin(null);
    }

    public ShapedRecipe getInvisibleItemFramesRecipe() {
        ItemStack item = new ItemStack(Material.ITEM_FRAME);
        NBTItem nbti = new NBTItem(item);
        nbti.mergeCompound(new NBTContainer("{EntityTag:{Invisible:1b}}"));
        ItemStack enditem = nbti.getItem();
        enditem.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        ItemMeta meta = enditem.getItemMeta();
        assert meta != null;
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName("§2Invisible Item Frame");
        enditem.setItemMeta(meta);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "InvisibleItemFramesRecipe"), enditem);
        recipe.shape("GGG", "GLG", "GGG");
        recipe.setIngredient('G', Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(getConfig().getString("CA.world.CraftingRecipes.InvisibItemFrames.Rand")).toUpperCase())));
        recipe.setIngredient('L', Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(getConfig().getString("CA.world.CraftingRecipes.InvisibItemFrames.Mitte")).toUpperCase())));

        return recipe;
    }

    public ShapedRecipe getPlayerHeadRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "PlayerHeadRecipe"), playerheaditem);
        recipe.shape("CCC", "CBC", "ADA");
        recipe.setIngredient('A', Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(getConfig().getString("CA.world.CraftingRecipes.Player_heads.Zutat1")).toUpperCase())));
        recipe.setIngredient('B', Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(getConfig().getString("CA.world.CraftingRecipes.Player_heads.Zutat3")).toUpperCase())));
        recipe.setIngredient('C', Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(getConfig().getString("CA.world.CraftingRecipes.Player_heads.Zutat2")).toUpperCase())));
        recipe.setIngredient('D', Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(getConfig().getString("CA.world.CraftingRecipes.Player_heads.Zutat4")).toUpperCase())));
        return recipe;
    }

    public boolean isinSpawn(Location block, Location plotMiddel, double radius) {
        return plotMiddel.distance(block) < radius;
    }

    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("skull")) {
                if (player.hasPermission("ca.player.skull")) {
                    if (getConfig().getBoolean("CA.settings.Heads.Killing") || !getConfig().getBoolean("CA.settings.Heads.Command")) {
                        player.sendMessage("§4The Method to get Player Heads is Player Killing at the Moment!");
                        return true;
                    }
                    if (args.length == 1) {
                        ItemStack fee = new ItemStack(waehrung);
                        fee.setAmount(64);
                        if (player.getInventory().contains(fee)) {
                            if (player.getItemInHand().equals(playerheaditem)) {
                                player.getInventory().removeItem(fee);
                                ItemStack head = player.getItemInHand();
                                SkullMeta meta = (SkullMeta) head.getItemMeta();
                                assert meta != null;
                                meta.setDisplayName("§6" + args[0]);
                                meta.setOwner(args[0]);
                                meta.setLore(null);
                                head.setItemMeta(meta);
                                player.setItemInHand(head);
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 80, (float) 1.0);
                                player.sendMessage("§aGiving you the Skull of §6" + args[0]);
                                return true;
                            } else {
                                player.sendMessage("§cDu musst einen gecrafteten Spieler Kopf in der Hand halten!");
                            }
                        } else {
                            for (ItemStack is : player.getInventory()) {
                                if (is != null && is.getType() == waehrung) {
                                    player.sendMessage("§cDu brauchst 64x Diamanten! Du hast: " + is.getAmount() + "x!");
                                    return true;
                                }
                            }
                            player.sendMessage("§cDu brauchst 64x Diamanten! Du hast: 0x!");
                            return true;
                        }
                    } else {
                        player.sendMessage(languageManager.getMessage("Player." + (args.length < 1 ? "TooFewArguments" : "TooManyArguments"), player, false));
                    }
                }
            }
            if (cmd.getName().equalsIgnoreCase("cainfos")) {
                if (args.length == 0) {
                    if (player.hasPermission("ca.infos.read")) {
                        List<String> infolist = infoConfig.getConfig().getStringList("Infos");
                        if (infolist.size() > 0) {
                            player.sendMessage("§2Infos:");
                            player.sendMessage("§2--------------------------------------");
                            for (int i = 0; i < infolist.size(); i++) {
                                String info = infolist.get(i);
                                player.sendMessage("§6" + (i + 1) + ". §r" + info.replace('&', '§'));
                                player.sendMessage("§2--------------------------------------");
                            }
                        }
                    }
                } else {
                    player.sendMessage("§cZu viele / wenige Argumente!");
                }
            }
            if (cmd.getName().equals("rules")) {
                if (rulesActivated) {
                    if (player.hasPermission("ca.rules.read")) {
                        List<String> rules = ruleConfig.getConfig().getStringList("rules");
                        if (rules.size() > 0) {
                            player.sendMessage("§2Regeln:");
                            player.sendMessage("§2--------------------------------------");
                            for (int i = 0; i < rules.size(); i++) {
                                String rule = rules.get(i);
                                player.sendMessage("§6" + (i + 1) + ". " + rule.replace('&', '§'));
                                player.sendMessage("§2--------------------------------------");
                            }
                        } else {
                            if (player.hasPermission("ca.rules.notdefinedmessage")) {
                                player.sendMessage(prefix + " §4Regeln m§ssen definiert werden in der rules.yml Datei");
                            } else {
                                player.sendMessage(prefix + " §4Wenn dir jetzt die Regeln nicht angezeigt werden dann sage einem Admin bescheid denn dann sind die Regeln nicht definiert worden!");
                            }
                        }
                    } else {
                        player.sendMessage(prefix + " §4Du hast keine Rechte dazu!");
                    }
                } else {
                    if (player.hasPermission("ca.rules.fehler.activate") && !rulesActivated) {
                        player.sendMessage(prefix + " §4Du musst erst die Regeln aktivieren in der Config");
                    } else
                        player.sendMessage(prefix + " §4Du hast keine Rechte dazu! Wenn du glaubst das ist ein Fehler dann gehe zu einem Admin");
                }
            }
            if (cmd.getName().equals("prefix")) {
                if (player.hasPermission("ca.prefix")) {
                    if (player.getWorld().getName().equals(getConfig().getString("CA.world.CraftAttack_World"))) {
                        if (args.length == 1) {
                            // neuer Prefix Code
                            try {
                                Prefix prefix = plugin.getPrefix(args[0]);
                                Utils.setPlayerPrefix(player, prefix, true);
                                return true;
                            } catch (NullPointerException | IllegalStateException e) {
                                Utils.setPlayerPrefix(player, plugin.getPrefix("Standard"), true);
                                return true;
                            }
                        } else if (args.length == 0) {
                            /*CommandPanels & Admin-Panel*/
                            /*TRUE & FALSE*/
                            if (Bukkit.getPluginManager().isPluginEnabled("CommandPanels") &&
                                    !Bukkit.getPluginManager().isPluginEnabled("Admin-Panel")) {
                                getPrefixInventory().getPrefixPanel().open(player, PanelPosition.Top);
                            }/*FALSE & TRUE*/ else if (!Bukkit.getPluginManager().isPluginEnabled("CommandPanels") &&
                                    Bukkit.getPluginManager().isPluginEnabled("Admin-Panel")) {
                                new PrefixMenu(AdminPanelMain.getAPI().getPlayerMenuUtility(player)).open();
                            }/*TRUE & TRUE*/ else if (Bukkit.getPluginManager().isPluginEnabled("CommandPanels") &&
                                    Bukkit.getPluginManager().isPluginEnabled("Admin-Panel")) {
                                new PrefixMenu(AdminPanelMain.getAPI().getPlayerMenuUtility(player)).open();
                            }/*FALSE & FALSE*/ else if (!Bukkit.getPluginManager().isPluginEnabled("CommandPanels") &&
                                    Bukkit.getPluginManager().isPluginEnabled("Admin-Panel")) {
                                player.sendMessage("§cNo Prefix Panel found! Install one of these two Plugins: Command-Panels (Rockyhawk), Admin-Panel (HappyBavarian07)");
                            }
                        }
                    } else {
                        player.sendMessage("§cDu kannst deinen Prefix nur setzen wenn du in der Craft Attack Welt bist!");
                    }
                }
            }
        } else {
            sender.sendMessage(languageManager.getMessage("Console.ExecutesPlayerCommand", null, false));
        }
        return true;
    }

    public FileConfiguration getPrefixConfig() {
        return prefixConfig;
    }

    public File getPrefixFile() {
        return prefixFile;
    }

    public StartUpLogger getStartUpLogger() {
        return logger;
    }

    public Map<String, Prefix> getPrefixList() {
        return prefixList;
    }
}
