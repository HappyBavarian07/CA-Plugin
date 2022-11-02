package me.happybavarian07.subCommands.DebugCommands;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 16:41
 */

import me.happybavarian07.commandmanagement.CommandData;
import me.happybavarian07.commandmanagement.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CommandData()
public class WorldCheckCommand extends SubCommand {
    public WorldCheckCommand(String mainCommandName) {
        super(mainCommandName);
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] args) {
        if(args.length != 0) {
            return false;
        }
        if (plugin.isWorldChangeCheck()) {
            player.sendMessage("§3World Checking for Craft Attack is currently: §aenabled");
        } else {
            player.sendMessage("§aWorld Checking for Craft Attack is currently: §cdisabled");
        }
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender sender, String[] args) {
        if(args.length != 0) {
            return false;
        }
        if (plugin.isWorldChangeCheck()) {
            sender.sendMessage("§3World Checking for Craft Attack is currently: §aenabled");
        } else {
            sender.sendMessage("§aWorld Checking for Craft Attack is currently: §cdisabled");
        }
        return true;
    }

    @Override
    public String name() {
        return "worldchangecheck";
    }

    @Override
    public String info() {
        return "The Command for World change Check Debug";
    }

    @Override
    public String[] aliases() {
        return new String[] {"worldcheck"};
    }

    @Override
    public Map<Integer, String[]> subArgs() {
        return new HashMap<>();
    }

    @Override
    public String syntax() {
        return "/debug worldcheck";
    }

    @Override
    public String permission() {
        return "ca.admin.debug";
    }
}
