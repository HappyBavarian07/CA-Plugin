package me.happybavarian07.subCommands.DebugCommands;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 16:44
 */

import me.happybavarian07.commandmanagement.CommandData;
import me.happybavarian07.commandmanagement.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CommandData()
public class RulesActivatedCommand extends SubCommand {
    public RulesActivatedCommand(String mainCommandName) {
        super(mainCommandName);
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] args) {
        if(args.length != 0) {
            return false;
        }
        if (plugin.isRulesActivated()) {
            player.sendMessage("§3Rules are currently: §aenabled");
        } else {
            player.sendMessage("§3Rules are currently: §cdisabled");
        }
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender sender, String[] args) {
        if(args.length != 0) {
            return false;
        }
        if (plugin.isRulesActivated()) {
            sender.sendMessage("§3Rules are currently: §aenabled");
        } else {
            sender.sendMessage("§3Rules are currently: §cdisabled");
        }
        return true;
    }

    @Override
    public String name() {
        return "rulesactivated";
    }

    @Override
    public String info() {
        return "The Command for Rules Activated Debug";
    }

    @Override
    public String[] aliases() {
        return new String[] {"rules"};
    }

    @Override
    public Map<Integer, String[]> subArgs() {
        return new HashMap<>();
    }

    @Override
    public String syntax() {
        return "/debug rules";
    }

    @Override
    public String permission() {
        return "ca.admin.debug";
    }
}
