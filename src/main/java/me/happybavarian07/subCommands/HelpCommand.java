package me.happybavarian07.subCommands;
/*
 * @Author HappyBavarian07
 * @Date 05.10.2021 | 17:53
 */

import me.happybavarian07.commandmanagement.PaginatedList;
import me.happybavarian07.commandmanagement.SubCommand;
import me.happybavarian07.language.PlaceholderType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HelpCommand extends SubCommand {
    public HelpCommand(String mainCommandName) {
        super(mainCommandName);
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] args) {
        if (args.length != 1) {
            return false;
        }
        try {
            int page = Integer.parseInt(args[0]);
            PaginatedList<SubCommand> messages = new PaginatedList<>(plugin.getCommandManagerRegistry().getSubCommands(mainCommandName));
            messages.maxItemsPerPage(10).sort();
            lgm.addPlaceholder(PlaceholderType.MESSAGE, "%page%", page, false);
            if (!messages.containsPage(page)) {
                player.sendMessage(lgm.getMessage("Player.Commands.HelpPageDoesNotExist", player, true));
                return true;
            }
            lgm.addPlaceholder(PlaceholderType.MESSAGE, "%max_page%", messages.getMaxPage(), false);
            player.sendMessage(lgm.getMessage("Player.Commands.HelpMessages.Header", player, false));
            for (SubCommand s : messages.getPage(page)) {
                if (player.hasPermission(s.permission())) {
                    player.sendMessage(format(lgm.getMessage("Player.Commands.HelpMessages.Format", player, false), s));
                }
            }
            player.sendMessage(lgm.getMessage("Player.Commands.HelpMessages.Footer", player, true));
        } catch (NumberFormatException e) {
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
        if (args.length != 1) {
            return false;
        }
        try {
            int page = Integer.parseInt(args[0]);
            PaginatedList<SubCommand> messages = new PaginatedList<>(plugin.getCommandManagerRegistry().getSubCommands(mainCommandName));
            messages.maxItemsPerPage(10).sort();
            lgm.addPlaceholder(PlaceholderType.MESSAGE, "%page%", page, false);
            if (!messages.containsPage(page)) {
                sender.sendMessage(lgm.getMessage("Player.Commands.HelpPageDoesNotExist", null, true));
                return true;
            }
            lgm.addPlaceholder(PlaceholderType.MESSAGE, "%max_page%", messages.getMaxPage(), false);
            sender.sendMessage(lgm.getMessage("Player.Commands.HelpMessages.Header", null, false));
            for (SubCommand s : messages.getPage(page)) {
                if (sender.hasPermission(s.permission()) && !isPlayerRequired()) {
                    sender.sendMessage(format(lgm.getMessage("Player.Commands.HelpMessages.Format", null, false), s));
                }
            }
            sender.sendMessage(lgm.getMessage("Player.Commands.HelpMessages.Footer", null, true));
        } catch (NumberFormatException e) {
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
        return "help";
    }

    @Override
    public String info() {
        return "The Help Command";
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
        return "/" + mainCommandName + " help <Page>";
    }

    @Override
    public String permission() {
        return "";
    }
}
