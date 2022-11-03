package me.happybavarian07.commands;/*
 * @Author HappyBavarian07
 * @Date 26.04.2022 | 19:33
 */

import me.happybavarian07.main.Utils;
import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.language.PlaceholderType;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PerPlayerLanguageCommand implements CommandExecutor, TabCompleter {
    private final LanguageManager lgm;
    private final List<String> completerArgs = new ArrayList<>();

    public PerPlayerLanguageCommand() {
        this.lgm = CAPluginMain.getPlugin().getLanguageManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(lgm.getMessage("Console.ExecutesPlayerCommand", null, true));
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (!sender.hasPermission("ca.admin.perplayerlang")) {
                sender.sendMessage(lgm.getMessage("Player.NoPermissions", (Player) sender, true));
                return true;
            }
            UUID playerUUID = player.getUniqueId();
            String langName = args[0];
            lgm.addPlaceholder(PlaceholderType.MESSAGE, "%langname%", langName, true);
            if (langName.equals("default")) {
                lgm.getPlhandler().removePlayerLanguage(playerUUID);
                player.sendMessage(lgm.getMessage("Player.PerPlayerLang.PerPlayerLangSet", player, true));
                return true;
            }
            if (lgm.getLang(langName, false) == null) {
                player.sendMessage(lgm.getMessage("Player.PerPlayerLang.NotAValidLanguage", player, true));
                return true;
            }
            lgm.getPlhandler().setPlayerLanguage(playerUUID, langName);
            player.sendMessage(lgm.getMessage("Player.PerPlayerLang.PerPlayerLangSet", player, true));
        } else if (args.length == 2) {
            if (!sender.hasPermission("ca.admin.perplayerlangother")) {
                sender.sendMessage(lgm.getMessage("Player.NoPermissions", (Player) sender, true));
                return true;
            }
            String langName = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            lgm.addPlaceholder(PlaceholderType.MESSAGE, "%langname%", langName, true);
            lgm.addPlaceholder(PlaceholderType.MESSAGE, "%target%", args[1], false);
            if (target == null) {
                player.sendMessage(lgm.getMessage("Player.PlayerIsNull", player, true));
                return true;
            }
            UUID playerUUID = target.getUniqueId();
            if (langName.equals("default")) {
                lgm.getPlhandler().removePlayerLanguage(playerUUID);
                player.sendMessage(lgm.getMessage("Player.PerPlayerLang.PerPlayerLangSetOther", player, true));
                return true;
            }
            if (lgm.getLang(langName, false) == null) {
                player.sendMessage(lgm.getMessage("Player.PerPlayerLang.NotAValidLanguage", player, true));
                return true;
            }
            lgm.getPlhandler().setPlayerLanguage(playerUUID, langName);
            player.sendMessage(lgm.getMessage("Player.PerPlayerLang.PerPlayerLangSetOther", player, true));
        } else {
            player.sendMessage(Utils.formatChatMessage(null, CAPluginMain.getPrefix() + "&c Usage: &6" + command.getUsage(), true, false));
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("perplayerlang")) {
            if (!sender.hasPermission("AdminPanel.PerPlayerLang")) return new ArrayList<>();
            completerArgs.clear();
            completerArgs.add("default");
            completerArgs.addAll(lgm.getRegisteredLanguages().keySet());

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : completerArgs) {
                    if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(a);
                }
                return result;
            }

            if (args.length > 1) {
                return null;
            }
            return completerArgs;
        }
        return new ArrayList<>();
    }
}
