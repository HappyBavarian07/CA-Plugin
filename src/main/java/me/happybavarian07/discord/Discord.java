package me.happybavarian07.discord;

import me.happybavarian07.API.StartUpLogger;
import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.main.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class Discord extends ListenerAdapter implements Listener {
    private final TextChannel consoleLogChannel;
    private final TextChannel minecraftChatChannel;
    // Plugin Variables
    private final CAPluginMain plugin;
    private final FileConfiguration config;
    private final DiscordVerify discordVerify;
    // Discord Variables
    private JDABuilder builder;
    private JDA bot;
    private Handler consoleHandler;
    private boolean started;

    // Other Variables
    public Discord(CAPluginMain plugin, String botToken, Activity customActivity) {
        this.started = false;
        this.plugin = plugin;
        StartUpLogger logger = plugin.getStartUpLogger();
        this.config = plugin.getDiscordFileConfig();
        logger.coloredSpacer(ChatColor.GREEN).coloredMessage(ChatColor.GOLD, "Trying to connect to the Discord Server!");
        try {
            this.builder = JDABuilder.createDefault(botToken).setActivity(customActivity);
            builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
            builder.disableCache(CacheFlag.ACTIVITY);
            builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER));
            builder.setChunkingFilter(ChunkingFilter.NONE);
            builder.setLargeThreshold(50);
            builder.setBulkDeleteSplittingEnabled(false);
            this.bot = builder.build();
            if (!bot.getStatus().equals(JDA.Status.CONNECTED)) {
                bot.awaitReady();
            }
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
        this.discordVerify = new DiscordVerify(bot);
        this.consoleLogChannel = this.bot.getTextChannelById(Objects.requireNonNull(config.getString("Channels.ConsoleLogChannel")));
        this.minecraftChatChannel = this.bot.getTextChannelById(Objects.requireNonNull(config.getString("Channels.MinecraftChatChannel")));
        register();
        logger.coloredMessage(ChatColor.GOLD, "Successfully connected to the Server! Bot online!").coloredSpacer(ChatColor.GREEN);
    }

    public boolean isVerified(Member member) {
        return member.getRoles().stream().filter(role -> role.getName().equals("Verified")).findAny().orElse(null) != null;
    }

    public JDABuilder getBuilder() {
        return builder;
    }

    public boolean hasStarted() {
        return started;
    }

    public JDA getBot() {
        return bot;
    }

    public TextChannel getConsoleLogChannel() {
        return consoleLogChannel;
    }

    public TextChannel getMinecraftChatChannel() {
        return minecraftChatChannel;
    }

    public void register() {
        try {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            bot.addEventListener(this);
            this.consoleHandler = new Handler() {
                @Override
                public void publish(LogRecord record) {
                    try {
                        Date date = Calendar.getInstance().getTime();
                        if (!(consoleLogChannel == null)) {
                            if (record.getThrown() != null) {
                                EmbedBuilder messageBuilder = new EmbedBuilder();
                                messageBuilder.setAuthor("Server");
                                messageBuilder.setColor(0x22ff2a);
                                messageBuilder.setDescription("[" + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "] [" +
                                        record.getLevel() + "]: " + record.getThrown());
                                minecraftChatChannel.sendMessage(messageBuilder.build()).queue();
                            } else if (record.getMessage() != null) {
                                EmbedBuilder messageBuilder = new EmbedBuilder();
                                messageBuilder.setAuthor("Server");
                                messageBuilder.setColor(0x22ff2a);
                                messageBuilder.setDescription("[" + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "] [" +
                                        record.getLevel() + "]: " + record.getMessage());
                                minecraftChatChannel.sendMessage(messageBuilder.build()).queue();
                            }
                        }
                    } catch (NoClassDefFoundError ignored) {
                    }
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() throws SecurityException {
                }
            };
            this.started = true;
        } catch (Exception ignored) {
        }
    }

    public Handler getConsoleHandler() {
        return this.consoleHandler;
    }

    @EventHandler
    public void onChat(PlayerChatEvent e) {
        if (!transferMinecraftChat()) return;
        try {
            EmbedBuilder messageBuilder = new EmbedBuilder();
            messageBuilder.setAuthor(e.getPlayer().getName());
            messageBuilder.setThumbnail("https://mc-heads.net/avatar/" + e.getPlayer().getUniqueId() + "/96/helm.png");
            messageBuilder.setDescription(Utils.format(e.getPlayer(), e.getMessage(), CAPluginMain.getPrefix()));
            minecraftChatChannel.sendMessage(messageBuilder.build()).queue();
        } catch (NoClassDefFoundError ignored) {
        }
    }

    public boolean logConsole() {
        return this.config.getBoolean("Settings.LogConsole");
    }

    public boolean transferMinecraftChat() {
        return this.config.getBoolean("Settings.MinecraftChatToDiscord");
    }

    public boolean transferDiscordChat() {
        return this.config.getBoolean("Settings.DiscordChatToMinecraft");
    }

    public boolean logStartAndStop() {
        return this.config.getBoolean("Settings.LogStartAndStop");
    }

    public boolean logJoinAndQuit() {
        return this.config.getBoolean("Settings.LogJoinAndQuit");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        try {
            if (!logJoinAndQuit()) return;

            EmbedBuilder messageBuilder = new EmbedBuilder();
            messageBuilder.setAuthor(e.getPlayer().getName());
            messageBuilder.setColor(Color.GREEN);
            messageBuilder.setDescription(":green_square: **" + e.getPlayer().getName() + "** joined the Server");
            minecraftChatChannel.sendMessage(messageBuilder.build()).complete(true);
        } catch (NoClassDefFoundError | RateLimitedException ignored) {
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        try {
            if (!logJoinAndQuit()) return;

            EmbedBuilder messageBuilder = new EmbedBuilder();
            messageBuilder.setAuthor(e.getPlayer().getName());
            messageBuilder.setColor(Color.RED);
            messageBuilder.setDescription(":red_square: **" + e.getPlayer().getName() + "** left the Server");
            minecraftChatChannel.sendMessage(messageBuilder.build()).complete(true);
        } catch (NoClassDefFoundError | RateLimitedException ignored) {
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" "); //!link args args args
        if(args.length == 0) return;
        if (args[0].equalsIgnoreCase("!link")) {//!link <Name>
            try {
                if (event.getAuthor().isBot() || event.getAuthor().isFake() || event.isWebhookMessage()) return;
                if (event.getMember().getRoles().stream().filter(role -> role.getName().equals("Verified")).findAny().orElse(null) != null) {
                    event.getChannel().sendMessage(":x: **|** Error! " + event.getAuthor().getAsMention() + ", you are already verified!").queue();
                    return;
                }
                if (args.length != 2) {
                    event.getChannel().sendMessage(":x: **|** Error! You need to specify a player!").queue();
                    return;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    event.getChannel().sendMessage(":x: **|** Error! Target is not online!").queue();
                    return;
                }
                String randomcode = new Random().nextInt(799999) + 200000 + "CA";
                discordVerify.uuidCodeMap.put(target.getUniqueId(), randomcode);
                discordVerify.uuidIdMap.put(target.getUniqueId(), event.getAuthor().getId());
                PrivateChannel pchannel = event.getAuthor().openPrivateChannel().complete();
                pchannel.sendMessage("Hey, " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + "!\n" +
                        "Your Verification has been generated!\n" +
                        "Use this Command ingame to verify: ``/verify " + randomcode + "``").queue();
            } catch (Exception ignored) {
            }
        } else {
            try {
                if (event.getAuthor().isBot() || event.getAuthor().isFake() || event.isWebhookMessage()) return;

                if (event.getChannel().equals(minecraftChatChannel) && transferDiscordChat()) {
                    String message = event.getMessage().getContentDisplay();
                    System.out.println("Message: " + message);
                    System.out.println("Event Message: " + event.getMessage());
                    System.out.println("Event Content: " + event.getMessage().getContentDisplay());
                    User user = event.getAuthor();
                    if (event.getGuild().getMember(user) == null) return;
                    if (isVerified(Objects.requireNonNull(event.getGuild().getMember(user)))) {
                        Bukkit.broadcastMessage(Utils.format(null,
                                "&f[&eDiscord &a" + user.getName() + "&f#&b" + user.getDiscriminator() + "&f (&a✔&f)] &3" + message,
                                CAPluginMain.getPrefix()));
                    } else {
                        Bukkit.broadcastMessage(Utils.format(null,
                                "&f[&eDiscord &a" + user.getName() + "&f#&b" + user.getDiscriminator() + "&f (&c❌&f)] &3" + message,
                                CAPluginMain.getPrefix()));
                    }
                }
            } catch (NoClassDefFoundError ignored) {
            }
        }
    }

    public void sendServerStopMessage() {
        if (!logStartAndStop()) return;

        try {
            Bukkit.getLogger().removeHandler(getConsoleHandler());
            Thread.sleep(1000);
            EmbedBuilder messageBuilder = new EmbedBuilder();
            messageBuilder.setAuthor("Server");
            messageBuilder.setColor(Color.RED);
            messageBuilder.setDescription(":red_circle: **Server stopped!** :red_circle:");
            minecraftChatChannel.sendMessage(messageBuilder.build()).queue();
            Thread.sleep(2000);
        } catch (NoClassDefFoundError | InterruptedException ignored) {
        }
    }

    public void sendServerStartMessage() {
        if (!logStartAndStop()) return;

        try {
            /*List<Message> messages;
            if(!(getConsoleLogChannel().getHistory().size() < 1)) {
                if(getConsoleLogChannel().getHistory().size() > 99) {
                    messages = getConsoleLogChannel().getHistory().retrievePast(99).complete();
                    while(messages.size() > 99) {
                        getConsoleLogChannel().deleteMessages(messages).complete(true);
                    }
                }
                messages = getConsoleLogChannel().getHistory().retrievePast(getConsoleLogChannel().getHistory().size()).complete();
                getConsoleLogChannel().deleteMessages(messages).complete(true);
            }*/
            EmbedBuilder messageBuilder = new EmbedBuilder();
            messageBuilder.setAuthor("Server");
            messageBuilder.setColor(0x22ff2a);
            messageBuilder.setDescription(":green_circle: **Server started!** :green_circle:");
            minecraftChatChannel.sendMessage(messageBuilder.build()).queue();
        } catch (NoClassDefFoundError ignored) {
        }
    }
}
