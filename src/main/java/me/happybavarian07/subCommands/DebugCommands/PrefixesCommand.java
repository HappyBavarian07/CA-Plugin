package me.happybavarian07.subCommands.DebugCommands;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 16:46
 */

import me.happybavarian07.PaginatedList;
import me.happybavarian07.SubCommand;
import me.happybavarian07.main.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrefixesCommand extends SubCommand {
    @Override
    public boolean onCommand(Player player, String[] args) {
        if (args.length != 1) {
            return false;
        }
        try {
            int page = Integer.parseInt(args[0]);
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            PaginatedList<Player> messages = new PaginatedList<>(onlinePlayers).maxItemsPerPage(10).sort();
            if (!messages.containsPage(page)) {
                player.sendMessage(lgm.getMessage("Player.Debug.Prefixes.PageDoesNotExist", player));
                return true;
            }
            player.sendMessage(lgm.getMessage("Player.Debug.Prefixes.Header", player)
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf(messages.getMaxPage())));
            for (Player online : messages.getPage(page)) {
                player.sendMessage(format(lgm.getMessage("Player.Debug.Prefixes.Format", online), online));
            }
            player.sendMessage(lgm.getMessage("Player.Debug.Prefixes.Footer", player)
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf(messages.getMaxPage())));
        } catch (NumberFormatException e) {
            player.sendMessage(lgm.getMessage("Player.Commands.NotANumber", player));
            return true;
        } catch (PaginatedList.ListNotSortedException e2) {
            e2.printStackTrace();
            return true;
        }
        return true;
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
