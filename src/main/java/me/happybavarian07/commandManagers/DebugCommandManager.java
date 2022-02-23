package me.happybavarian07.commandManagers;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 16:33
 */

import me.happybavarian07.CommandData;
import me.happybavarian07.CommandManager;
import me.happybavarian07.SubCommand;
import me.happybavarian07.main.Utils;
import me.happybavarian07.subCommands.DebugCommands.PrefixesCommand;
import me.happybavarian07.subCommands.DebugCommands.RulesActivatedCommand;
import me.happybavarian07.subCommands.DebugCommands.WorldCheckCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandData()
public class DebugCommandManager extends CommandManager {
    // Sub Commands
    List<String> commandArgs = new ArrayList<>();
    List<String> commandSubArgs = new ArrayList<>();

    @Override
    public String getCommandName() {
        return "debug";
    }

    @Override
    public String getCommandInfo() {
        return "The Debug Command";
    }

    @Override
    public void setup() {
        commands.add(new WorldCheckCommand());
        commands.add(new RulesActivatedCommand());
        commands.add(new PrefixesCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        return super.onCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return super.onTabComplete(sender, command, label, args);
    }

    private String format(String in, SubCommand cmd) {
        return in.replace("%usage%", cmd.syntax())
                .replace("%description%", cmd.info())
                .replace("%name%", cmd.name())
                .replace("%permission%", cmd.permission())
                .replace("%aliases%", Arrays.toString(cmd.aliases()));
    }

    public ArrayList<SubCommand> getSubCommands() {
        return commands;
    }
}
