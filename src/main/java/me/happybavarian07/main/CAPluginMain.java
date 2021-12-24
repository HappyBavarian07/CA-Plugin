package me.happybavarian07.main;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import de.slikey.effectlib.EffectManager;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import me.happybavarian07.API.CraftAttackExtension;
import me.happybavarian07.API.FileManager;
import me.happybavarian07.API.StartUpLogger;
import me.happybavarian07.CommandManagerRegistry;
import me.happybavarian07.GUIs.SelectorInv;
import me.happybavarian07.GUIs.prefixes.PrefixConfig;
import me.happybavarian07.GUIs.prefixes.PrefixInventory;
import me.happybavarian07.commandManagers.CraftAttackCommandManager;
import me.happybavarian07.commandManagers.DebugCommandManager;
import me.happybavarian07.commandManagers.MarkerCommandManager;
import me.happybavarian07.commandManagers.WorldCommandManager;
import me.happybavarian07.commands.afkcommand;
import me.happybavarian07.configs.DiscordConfig;
import me.happybavarian07.configs.InfoConfig;
import me.happybavarian07.configs.RuleConfig;
import me.happybavarian07.configupdater.ConfigUpdater;
import me.happybavarian07.discord.Discord;
import me.happybavarian07.language.LanguageFile;
import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.language.OldLanguageFileUpdater;
import me.happybavarian07.listeners.BedListener;
import me.happybavarian07.listeners.CraftAttackEvents;
import me.happybavarian07.listeners.LobbyEventListener;
import me.happybavarian07.listeners.PlayerManager;
import me.happybavarian07.mysql.MySQLHandler;
import me.rockyhawk.commandpanels.openpanelsmanager.PanelPosition;
import net.dv8tion.jda.api.entities.Activity;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UnknownFormatConversionException;

public class CAPluginMain extends JavaPlugin implements Listener {

    public static EffectManager em;
    private static CAPluginMain plugin;
    private static String prefix;
    public ProtocolManager pman;
    public File prefixfile;
    public FileConfiguration prefixconfig;
    public FileConfiguration spawnconfig;
    public FileManager fm;
    public Material waehrung;
    public BukkitTask spawnvorgang;
    int repeattaskmoveevent;
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
    private Updater updater;
    private OldLanguageFileUpdater langFileUpdater;

    public static CAPluginMain getPlugin() {
        return plugin;
    }

    public void setPlugin(CAPluginMain plugin) {
        CAPluginMain.plugin = plugin;
    }

    public static String getPrefix() {
        return prefix;
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

    public Updater getUpdater() {
        return updater;
    }

    public OldLanguageFileUpdater getLangFileUpdater() {
        return langFileUpdater;
    }

    public void onEnable() {
        setPlugin(this);
        logger = new StartUpLogger();
        this.mySQLHandler = new MySQLHandler();
        setupDiscord();
        em = new EffectManager(getPlugin());
        waehrung = Material.DIAMOND;
        infoConfig = new InfoConfig(this);
        pman = ProtocolLibrary.getProtocolManager();
        rulesActivated = getConfig().getBoolean("CA.rules.activate");
        worldChangeCheck = getConfig().getBoolean("CA.world.Access_Check");
        languageManager = new LanguageManager(this, new File(getDataFolder(), "/languages"));
        langFileUpdater = new OldLanguageFileUpdater(this);
        commandManagerRegistry = new CommandManagerRegistry(this);
        fileLogger = new PluginFileLogger();
        if (!fileLogger.getLogFile().exists()) {
            logger.spacer().message("§c§lCreating plugin.log file!§r");
            fileLogger.createLogFile();
            logger.message("§e§lDone!§r");
        }
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
        fm.createFile("", "spawnloc", "yml");
        fm.createFile("", "prefixes", "yml");
        //fm.createFile("", "FakePlates", "yml");
        fm.createFile("", "ChestLog", "yml");
        spawnconfig = fm.getConfig("", "spawnloc", "yml");
        prefixconfig = fm.getConfig("", "prefixes", "yml");
        prefixfile = fm.getFile("", "prefixes", "yml");
        new Utils(getPlugin(), prefixconfig);
        new CraftAttackExtension().register();
        prefix = Utils.format(null, getConfig().getString("Plugin.Prefix", "[CA-Plugin]"), "");
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
            prefixconfig.save(prefixfile);
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
                    if (online.hasMetadata("CraftAttackPluginSpawnElytra")) {
                        online.setGliding(true);
                        online.setAllowFlight(true);
                    } else {
                        online.setGliding(false);
                        online.setAllowFlight(false);
                    }
                    online.setFlying(false);
                    online.setFallDistance(0);
                }
            }
        }, 0L, 1L);
        getCommandManagerRegistry().register(new CraftAttackCommandManager());
        getCommandManagerRegistry().register(new DebugCommandManager());
        getCommandManagerRegistry().register(new WorldCommandManager());
        getCommandManagerRegistry().register(new MarkerCommandManager());
        LanguageFile deLang = new LanguageFile(this, "de");
        LanguageFile enLang = new LanguageFile(this, "en");
        languageManager.addLanguagesToList(true);
        languageManager.addLang(deLang, deLang.getLangName());
        languageManager.addLang(enLang, enLang.getLangName());
        languageManager.setCurrentLang(languageManager.getLang(getConfig().getString("CA.settings.language")), true);
        if (languageManager != null && languageManager.getMessage("Plugin.Enabled", null) != null &&
                !languageManager.getMessage("Plugin.Enabled", null).equals("null config") &&
                !languageManager.getMessage("Plugin.Enabled", null).startsWith("null path: Messages.")) {
            getServer().getConsoleSender().sendMessage(languageManager.getMessage("Plugin.Enabled", null));
        } else {
            getServer().getConsoleSender().sendMessage("[CA-Plugin] enabled!");
        }
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(getCraftAttackWorld())) {
                    Utils.setPlayerPrefix(player, Utils.getPrefixFromConfig(player) != null ? Utils.getPrefixFromConfig(player) : Prefix.EMPTY);
                    Utils.loadTablist(player, true);
                }
            }
        }, 50L);
        try {
            ConfigUpdater.update(this, "config.yml", new File(this.getDataFolder() + "/config.yml"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (getConfig().getBoolean("CA.settings.Updater.AutomaticLanguageFileUpdating")) {
            for (LanguageFile langFiles : languageManager.getRegisteredLanguages().values()) {
                if (plugin.getResource("languages/" + langFiles.getLangName() + ".yml") == null) continue;
                File oldFile = langFiles.getLangFile();
                File newFile = new File(langFiles.getLangFile().getParentFile().getPath() + "/" + langFiles.getLangName() + "-new.yml");
                YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(newFile);
                InputStream defaultStream = plugin.getResource("languages/" + langFiles.getLangName() + ".yml");
                if (defaultStream != null) {
                    YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
                    newConfig.setDefaults(defaultConfig);
                }


                newConfig.options().copyDefaults(true);
                try {
                    newConfig.save(newFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                langFileUpdater.updateFile(oldFile, newConfig, langFiles.getLangName());
                newFile.delete();
            }
            languageManager.reloadLanguages(null, false);
        }
        updater = new Updater(getPlugin(), 91800);
        if (getConfig().getBoolean("CA.settings.Updater.checkForUpdates")) {
            updater.checkForUpdates(true);
            if (updater.updateAvailable()) {
                updater.downloadPlugin(getConfig().getBoolean("CA.settings.Updater.automaticReplace"), false, true);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    updater.checkForUpdates(false);
                    if (updater.updateAvailable()) {
                        updater.downloadPlugin(getConfig().getBoolean("CA.settings.Updater.automaticReplace"), false, true);
                    }
                }
            }.runTaskTimer(plugin, (getConfig().getLong("CA.settings.Updater.UpdateCheckTime") * 60 * 20), (getConfig().getLong("CA.settings.Updater.UpdateCheckTime") * 60 * 20));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isOp()) {
            addStaff(e.getPlayer().getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (isStaff(e.getPlayer().getName())) {
            removeStaff(e.getPlayer().getName());
        }
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
        pm2.registerEvents(this, this);
        logger.message("§6Afk Events§r");
        pm2.registerEvents(new afkcommand(), this);
        logger.message("§6Bed Events§r");
        pm2.registerEvents(new BedListener(), this);
        //pm2.registerEvents(new TrollItemsGUI(this, getConfig()), this);
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
        if (languageManager != null && languageManager.getMessage("Plugin.Disabled", null) != null &&
                !languageManager.getMessage("Plugin.Disabled", null).equals("null config") &&
                !languageManager.getMessage("Plugin.Disabled", null).startsWith("null path: Messages.Plugin.")) {
            getServer().getConsoleSender().sendMessage(languageManager.getMessage("Plugin.Disabled", null));
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

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        if (e.getPlayer().getWorld().getName().equals(getConfig().getString("CA.world.CraftAttack_World"))) {
            if (e.getPlayer().hasMetadata("SpawnTeleportAnfrage")) {
                if (Bukkit.getScheduler().getPendingTasks().contains(spawnvorgang)) {
                    Bukkit.getScheduler().cancelTask(spawnvorgang.getTaskId());
                }
                e.getPlayer().sendMessage("§cSpawn Teleport abgebrochen, da du dich bewegt hast!");
                e.getPlayer().removeMetadata("SpawnTeleportAnfrage", plugin);
            }
            e.getPlayer().getItemInHand();
            if (e.getPlayer().getItemInHand().hasItemMeta() && Objects.requireNonNull(e.getPlayer().getItemInHand().getItemMeta()).getDisplayName().equals("§1R§2e§3m§4o§5t§6e §7C§8o§9n§at§br§co§el") && e.getPlayer().getItemInHand().getType() == Material.LEVER) {
                if (e.getPlayer().hasPermission("ca.admin.troll")) {
                    if (Objects.requireNonNull(e.getPlayer().getItemInHand().getItemMeta().getLore()).isEmpty()) {
                        return;
                    }
                    Player target = Bukkit.getPlayer(Objects.requireNonNull(Objects.requireNonNull(e.getPlayer().getItemInHand().getItemMeta()).getLore()).get(0).replace("§4Target locked: §5", ""));
                    assert target != null;
                    if (!isinSpawn(target.getLocation(), e.getPlayer().getLocation(), 5)) {
                        e.getPlayer().sendMessage("§aDein Target ist nicht in deiner Reichweite!");
                        return;
                    }
                    PacketContainer packet1 = pman.createPacket(PacketType.Play.Server.POSITION);
                    packet1.getIntegers().write(0, target.getEntityId());
                    packet1.getDoubles().write(0, e.getPlayer().getLocation().getX());
                    packet1.getDoubles().write(1, e.getPlayer().getLocation().getY());
                    packet1.getDoubles().write(2, e.getPlayer().getLocation().getZ() + 1);
                    packet1.getFloat().write(0, e.getPlayer().getLocation().getYaw());
                    packet1.getFloat().write(1, e.getPlayer().getLocation().getPitch());
                    packet1.getModifier().writeDefaults();
                    packet1.getCombatEvents().writeDefaults();
                    packet1.getHands().writeDefaults();

                    try {
                        pman.sendServerPacket(target, packet1);
                    } catch (InvocationTargetException e1) {
                        throw new RuntimeException("Cannot send packet " + packet1, e1);
                    }
                    //e.setCancelled(true);
                    return;
                }
            }
            Location blockloc = e.getPlayer().getLocation().add(0, -1, 0);
            World world = Bukkit.getWorld(Objects.requireNonNull(spawnconfig.getString("CraftAttack.Spawn.World")));
            if (world == null) return;
            double x = spawnconfig.getDouble("CraftAttack.Spawn.X");
            double y = spawnconfig.getDouble("CraftAttack.Spawn.Y");
            double Z = spawnconfig.getDouble("CraftAttack.Spawn.Z");
            Location spawnmiddleloc = new Location(world, x, y, Z);
            if (isinSpawn((blockloc.add(0, 1, 0)), spawnmiddleloc, getConfig().getDouble("CA.settings.Spawn.Radius")) && blockloc.add(0, -1, 0).getBlock().getType().equals(Material.GOLD_BLOCK)) {
                if ((e.getPlayer().getGameMode() == GameMode.SPECTATOR) || (e.getPlayer().getGameMode() == GameMode.CREATIVE)) {
                    return;
                }
                e.getPlayer().setMetadata("CraftAttackPluginSpawnElytra", new FixedMetadataValue(plugin, true));
                e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().setY(1).multiply(1));
                repeattaskmoveevent = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                    int time = getConfig().getInt("CA.settings.Flytime");

                    @Override
                    public void run() {
                        if (time >= 0 && !e.getPlayer().isOnGround() && e.getPlayer().hasMetadata("CraftAttackPluginSpawnElytra")) {
                            if (time < 60) {
                                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§4§lFlytime: " + (time) + " Seconds left"));
                            } else {
                                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lFlytime: " + (time / 60) + " Minutes left"));
                            }
                            time--;
                        } else {
                            e.getPlayer().removeMetadata("CraftAttackPluginSpawnElytra", plugin);
                            Bukkit.getScheduler().cancelTask(repeattaskmoveevent);
                            time = getConfig().getInt("CA.settings.Flytime");
                        }
                    }
                }, 20L, 20L);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onIact(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (e.getPlayer().getItemInHand().getType() == Material.FIREWORK_ROCKET) {
                if (e.getPlayer().hasMetadata("CraftAttackPluginSpawnElytra")) {
                    e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(2));
                    e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 100, (float) 1.0);
                    int i = 0;
                    while (!Objects.requireNonNull(e.getPlayer().getInventory().getItem(i)).isSimilar(e.getItem())) {
                        i++;
                    }
                    Objects.requireNonNull(e.getPlayer().getInventory().getItem(i)).setAmount(Objects.requireNonNull(e.getPlayer().getInventory().getItem(i)).getAmount() - 1);
                }
            }
        }
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
                        player.sendMessage(languageManager.getMessage("Player." + (args.length < 1 ? "TooFewArguments" : "TooManyArguments"), player));
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
                                Prefix prefix = Prefix.valueOf(args[0].toUpperCase());
                                Utils.setPlayerPrefix(player, prefix);
                                return true;
                            } catch (NullPointerException | IllegalStateException e) {
                                Utils.setPlayerPrefix(player, Prefix.STANDARD);
                                return true;
                            }
                        } else if (args.length == 0) {
                            getPrefixInventory().getPrefixPanel().open(player, PanelPosition.Top);
                        }
                    } else {
                        player.sendMessage("§cDu kannst deinen Prefix nur setzen wenn du in der Craft Attack Welt bist!");
                    }
                }
            }
        } else {
            sender.sendMessage(languageManager.getMessage("Console.ExecutesPlayerCommand", null));
        }
        return true;
    }

    public FileConfiguration getPrefixConfig() {
        return prefixconfig;
    }

    public File getPrefixFile() {
        return prefixfile;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String MessageSplitter = languageManager.getMessage("Chat.Splitter", player);
        String PlayerPrefix = Utils.getPrefixFromConfig(player).getInGamePrefix();
        String PlayerSuffix = Utils.getPrefixFromConfig(player).getInGameSuffix();
        try {
            if (player.getWorld().getName().equals(plugin.getConfig().getString("CA.world.CraftAttack_World"))) {
                e.setMessage(Utils.formatChatMessage(e.getPlayer(), e.getMessage(),
                        player.hasPermission("ca.chat.color"),
                        player.hasPermission("ca.chat.placeholders")));
                e.setFormat(PlayerPrefix + player.getDisplayName() + PlayerSuffix + MessageSplitter + e.getMessage());
            }
        } catch (UnknownFormatConversionException ignored) {
        }
    }

    public StartUpLogger getStartUpLogger() {
        return logger;
    }
}
