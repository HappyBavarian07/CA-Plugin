package me.happybavarian07.API;

import me.happybavarian07.discord.Discord;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;

public class StartUpLogger {

    private final String SPACER_FORMAT = "+------------------------------------------------------------------+";
    private final String MESSAGE_FORMAT = "|------------------------------------------------------------------|";

    private final ConsoleCommandSender sender = Bukkit.getConsoleSender();
    private final CAPluginMain plugin = CAPluginMain.getPlugin();
    private final Discord discord = plugin.getDiscord();

    public StartUpLogger spacer() {
        sender.sendMessage(SPACER_FORMAT);
        if(discord != null && discord.hasStarted() && discord.logConsole())
            discord.getConsoleLogChannel().sendMessage(SPACER_FORMAT).queue();
        return this;
    }

    public StartUpLogger coloredSpacer(ChatColor color) {
        sender.sendMessage(color + SPACER_FORMAT);
        if(discord != null && discord.hasStarted() && discord.logConsole()) {
            discord.getConsoleLogChannel().sendMessage(color + SPACER_FORMAT).queue();
        }
        return this;
    }

    public StartUpLogger emptySpacer() {
        sender.sendMessage("");
        if(discord != null && discord.hasStarted() && discord.logConsole())
            discord.getConsoleLogChannel().sendMessage("").queue();
        return this;
    }

    public StartUpLogger message(String message) {
        sender.sendMessage(getMessageWithFormat(message));
        if(discord != null && discord.hasStarted() && discord.logConsole())
            discord.getConsoleLogChannel().sendMessage(getMessageWithFormat(message)).queue();
        return this;
    }

    public StartUpLogger coloredMessage(ChatColor color, String message) {
        sender.sendMessage(color + getMessageWithFormat(message));
        if(discord != null && discord.hasStarted() && discord.logConsole())
            discord.getConsoleLogChannel().sendMessage(color + getMessageWithFormat(message)).queue();
        return this;
    }

    public StartUpLogger rawMessage(String message) {
        sender.sendMessage(message);
        if(discord != null && discord.hasStarted() && discord.logConsole())
            discord.getConsoleLogChannel().sendMessage(message).queue();
        return this;
    }

    public StartUpLogger messages(String... messages) {
        for(String message : messages) {
            sender.sendMessage(getMessageWithFormat(message));
            if(discord != null && discord.hasStarted() && discord.logConsole())
                discord.getConsoleLogChannel().sendMessage(message).queue();
        }
        return this;
    }

    public StartUpLogger rawMessages(String... messages) {
        sender.sendMessage(messages);
        if(discord != null && discord.hasStarted() && discord.logConsole())
            discord.getConsoleLogChannel().sendMessage(Arrays.toString(messages)).queue();
        return this;
    }

    private String getMessageWithFormat(String message) {
        final int messageSpacerLength = MESSAGE_FORMAT.length();
        final int messageLength = message
                .replaceAll("§([a-fA-F0-9]|r|l|m|n|o|k)", "")
                .length() ;

        // Return the default message if it is too long for the actual spacer
        if(messageLength > messageSpacerLength - 2) return message;

        final int partLength = (messageSpacerLength - messageLength) / 2;

        final String startPart = MESSAGE_FORMAT.substring(0, partLength);
        final String endPart = MESSAGE_FORMAT.substring(messageSpacerLength - partLength, messageSpacerLength);

        return startPart + message + endPart;
    }

    public static StartUpLogger create() {
        return new StartUpLogger();
    }

}