package me.happybavarian07.subCommands.CraftAttackCommand;
/*
 * @Author HappyBavarian07
 * @Date 05.10.2021 | 17:53
 */

import me.happybavarian07.PaginatedList;
import me.happybavarian07.SubCommand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HelpCommand extends SubCommand {
    @Override
    public boolean onCommand(Player player, String[] args) {
        if (args.length != 1) {
            return false;
        }
        try {
            int page = Integer.parseInt(args[0]);
            PaginatedList<SubCommand> messages = new PaginatedList<>(plugin.getCommandManagerRegistry().getSubCommands("craftattack"));
            messages.maxItemsPerPage(10).sort();
            if (!messages.containsPage(page)) {
                player.sendMessage(lgm.getMessage("Player.Commands.HelpPageDoesNotExist", player));
                return true;
            }
            player.sendMessage(lgm.getMessage("Player.Commands.HelpMessages.Header", player)
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf(messages.getMaxPage())));
            for (SubCommand s : messages.getPage(page)) {
                if(player.hasPermission(s.permission())) {
                    player.sendMessage(format(lgm.getMessage("Player.Commands.HelpMessages.Format", player), s));
                }
            }
            player.sendMessage(lgm.getMessage("Player.Commands.HelpMessages.Footer", player)
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
        return "/ca help <Page>";
    }

    @Override
    public String permission() {
        return "";
    }
}
