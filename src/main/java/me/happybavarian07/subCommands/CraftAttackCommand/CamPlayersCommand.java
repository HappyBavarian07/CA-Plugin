package me.happybavarian07.subCommands.CraftAttackCommand;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 15:31
 */

import me.happybavarian07.commandmanagement.PaginatedList;
import me.happybavarian07.commandmanagement.CommandData;
import me.happybavarian07.commandmanagement.SubCommand;
import me.happybavarian07.language.PlaceholderType;
import me.happybavarian07.main.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandData()
public class CamPlayersCommand extends SubCommand {
    public CamPlayersCommand(String mainCommandName) {
        super(mainCommandName);
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] args) {
        if (args.length == 0) {
            return false;
        }
        try {
            int page = Integer.parseInt(args[0]);
            PaginatedList<String> messages = new PaginatedList<>(plugin.getConfig().getStringList("CA.world.Cam_Players"));
            messages.maxItemsPerPage(lgm.getCustomObject("Player.CamPlayerListCommand.MaxItemsPerPage", null, 3, false)).sort();
            lgm.addPlaceholder(PlaceholderType.MESSAGE, "%page%", page, false);
            if (!messages.containsPage(page)) {
                player.sendMessage(lgm.getMessage("Player.Commands.HelpPageDoesNotExist", player, true));
                return true;
            }
            lgm.addPlaceholder(PlaceholderType.MESSAGE, "%max_page%", messages.getMaxPage(), false);
            player.sendMessage(lgm.getMessage("Player.CamPlayerListCommand.Header", player, false));
            for (String playerName : messages.getPage(page)) {
                Player tempTarget = Bukkit.getPlayer(playerName);
                if (tempTarget != null) {
                    lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%count%", messages.getListOfThings().indexOf(playerName) + 1, false);
                    lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%status%", (tempTarget.isOnline() ? "  |  &a&lOnline" : "  |  &4&lOffline"), false);
                    lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%current_prefix%", Utils.getPrefixFromConfig(tempTarget).getConfigName(), false);
                    lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%current_world%", tempTarget.getWorld().getName(), false);
                    if (!plugin.getConfig().getBoolean("BetterElytraSystem.enabled"))
                    lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%elytra_fly%", (tempTarget.hasMetadata("CraftAttackPluginSpawnElytra") ? "On" : "Off"), false);
                    List<String> tempMessages = lgm.getMessageList("Player.CamPlayerListCommand.Format", tempTarget, true);
                    //System.out.println("Temp Messages Cam: " + tempMessages);
                    for (String message : tempMessages) {
                        tempTarget.sendMessage(message);
                    }
                } else {
                    player.sendMessage("§7Player '" + playerName + "' is not online or does not exist.");
                }
            }
            player.sendMessage(lgm.getMessage("Player.CamPlayerListCommand.Footer", player, true));
        } catch (NumberFormatException e) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%count%", -1, false);
                lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%status%", (target.isOnline() ? "  |  &a&lOnline" : "  |  &4&lOffline"), false);
                lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%current_prefix%", Utils.getPrefixFromConfig(target).getConfigName(), false);
                lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%current_world%", target.getWorld().getName(), false);
                if (!plugin.getConfig().getBoolean("BetterElytraSystem.enabled"))
                lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%elytra_fly%", (target.hasMetadata("CraftAttackPluginSpawnElytra") ? "On" : "Off"), false);
                List<String> messages = lgm.getMessageList("Player.CamPlayerListCommand.Format", target, true);
                for (String message : messages) {
                    target.sendMessage(message);
                }
                return true;
            }
            player.sendMessage(lgm.getMessage("Player.Commands.NotANumber", player, true));
            return true;
        } catch (PaginatedList.ListNotSortedException e2) {
            e2.printStackTrace();
            return true;
        }
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }
        try {
            int page = Integer.parseInt(args[0]);
            PaginatedList<String> messages = new PaginatedList<>(plugin.getConfig().getStringList("CA.world.Cam_Players"));
            messages.maxItemsPerPage(lgm.getCustomObject("Player.CamPlayerListCommand.MaxItemsPerPage", null, 3, false)).sort();
            lgm.addPlaceholder(PlaceholderType.MESSAGE, "%page%", page, false);
            if (!messages.containsPage(page)) {
                sender.sendMessage(lgm.getMessage("Player.Commands.HelpPageDoesNotExist", null, true));
                return true;
            }
            lgm.addPlaceholder(PlaceholderType.MESSAGE, "%max_page%", messages.getMaxPage(), false);
            sender.sendMessage(lgm.getMessage("Player.CamPlayerListCommand.Header", null, false));
            for (String playerName : messages.getPage(page)) {
                Player tempTarget = Bukkit.getPlayer(playerName);
                if (tempTarget != null) {
                    lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%count%", messages.getListOfThings().indexOf(playerName) + 1, false);
                    lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%status%", (tempTarget.isOnline() ? "  |  &a&lOnline" : "  |  &4&lOffline"), false);
                    lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%current_prefix%", Utils.getPrefixFromConfig(tempTarget).getConfigName(), false);
                    lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%current_world%", tempTarget.getWorld().getName(), false);
                    if (!plugin.getConfig().getBoolean("BetterElytraSystem.enabled"))
                    lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%elytra_fly%", (tempTarget.hasMetadata("CraftAttackPluginSpawnElytra") ? "On" : "Off"), false);
                    List<String> tempMessages = lgm.getMessageList("Player.CamPlayerListCommand.Format", tempTarget, true);
                    for (String message : tempMessages) {
                        tempTarget.sendMessage(message);
                    }
                } else {
                    sender.sendMessage("§7Player '" + playerName + "' is not online or does not exist.");
                }
            }
            sender.sendMessage(lgm.getMessage("Player.CamPlayerListCommand.Footer", null, true));
        } catch (NumberFormatException e) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%count%", -1, false);
                lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%status%", (target.isOnline() ? "  |  &a&lOnline" : "  |  &4&lOffline"), false);
                lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%current_prefix%", Utils.getPrefixFromConfig(target).getConfigName(), false);
                lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%current_world%", target.getWorld().getName(), false);
                if (!plugin.getConfig().getBoolean("BetterElytraSystem.enabled"))
                lgm.addPlaceholder(PlaceholderType.MESSAGELIST, "%elytra_fly%", (target.hasMetadata("CraftAttackPluginSpawnElytra") ? "On" : "Off"), false);
                List<String> messages = lgm.getMessageList("Messages.Player.CamPlayerListCommand.Format", target, true);
                for (String message : messages) {
                    target.sendMessage(message);
                }
                return true;
            }
            sender.sendMessage(lgm.getMessage("Player.Commands.NotANumber", null, true));
            return true;
        } catch (PaginatedList.ListNotSortedException e2) {
            e2.printStackTrace();
            return true;
        }
        return true;
    }

    @Override
    public String name() {
        return "CamPlayers";
    }

    @Override
    public String info() {
        return "The Cam Players List Command";
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }

    @Override
    public Map<Integer, String[]> subArgs() {
        return new HashMap<>();
    }

    @Override
    public String syntax() {
        return "/ca CamPlayers <Page>";
    }

    @Override
    public String permission() {
        return "ca.admin.control";
    }
}
