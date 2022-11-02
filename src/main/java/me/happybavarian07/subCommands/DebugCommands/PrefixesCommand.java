package me.happybavarian07.subCommands.DebugCommands;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 16:46
 */

import me.happybavarian07.commandmanagement.CommandData;
import me.happybavarian07.commandmanagement.PaginatedList;
import me.happybavarian07.commandmanagement.SubCommand;
import me.happybavarian07.main.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandData()
public class PrefixesCommand extends SubCommand {
    public PrefixesCommand(String mainCommandName) {
        super(mainCommandName);
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }
        try {
            int page = Integer.parseInt(args[0]);
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            PaginatedList<Player> messages = new PaginatedList<>(onlinePlayers).maxItemsPerPage(10).sort();
            if (!messages.containsPage(page)) {
                sender.sendMessage(lgm.getMessage("Player.Debug.Prefixes.PageDoesNotExist", sender instanceof Player ? (Player) sender : null, false));
                return true;
            }
            sender.sendMessage(lgm.getMessage("Player.Debug.Prefixes.Header", sender instanceof Player ? (Player) sender : null, false)
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf(messages.getMaxPage())));
            for (Player online : messages.getPage(page)) {
                sender.sendMessage(format(lgm.getMessage("Player.Debug.Prefixes.Format", online, false), online));
            }
            sender.sendMessage(lgm.getMessage("Player.Debug.Prefixes.Footer", sender instanceof Player ? (Player) sender : null, false)
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf(messages.getMaxPage())));
        } catch (NumberFormatException e) {
            sender.sendMessage(lgm.getMessage("Player.Commands.NotANumber", sender instanceof Player ? (Player) sender : null, false));
            return true;
        } catch (PaginatedList.ListNotSortedException e2) {
            e2.printStackTrace();
            return true;
        }
        return true;
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] args) {
        return onCommand(player, args);
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender sender, String[] args) {
        return onCommand(sender, args);
    }

    @Override
    public String name() {
        return "prefixes";
    }

    @Override
    public String info() {
        return "The Command that lists all Players Prefixes";
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
        return "/debug prefixes <Page>";
    }

    @Override
    public String permission() {
        return "ca.admin.debug";
    }

    private String format(String in, Player player) {
        return in.replace("%ingame_prefix%", Utils.getPrefixFromConfig(player).getInGamePrefix())
                .replace("%ingame_suffix%", Utils.getPrefixFromConfig(player).getInGameSuffix())
                .replace("%config_name%", Utils.getPrefixFromConfig(player).getConfigName());
    }
}
