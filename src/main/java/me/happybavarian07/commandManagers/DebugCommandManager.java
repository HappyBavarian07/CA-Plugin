package me.happybavarian07.commandManagers;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 16:33
 */

import me.happybavarian07.commandmanagement.CommandData;
import me.happybavarian07.commandmanagement.CommandManager;
import me.happybavarian07.commandmanagement.SubCommand;
import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.subCommands.DebugCommands.PrefixesCommand;
import me.happybavarian07.subCommands.DebugCommands.RulesActivatedCommand;
import me.happybavarian07.subCommands.DebugCommands.WorldCheckCommand;
import me.happybavarian07.subCommands.HelpCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandData()
public class DebugCommandManager extends CommandManager {
    @Override
    public String getCommandName() {
        return "debug";
    }

    @Override
    public String getCommandUsage() {
        return "/debug <SubCommand> (/debug help <Page>)";
    }

    @Override
    public String getCommandInfo() {
        return "The Debug Command";
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return CAPluginMain.getPlugin();
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getCommandPermission() {
        return "";
    }

    @Override
    public void setup() {
        commands.add(new WorldCheckCommand(getCommandName()));
        commands.add(new RulesActivatedCommand(getCommandName()));
        commands.add(new PrefixesCommand(getCommandName()));
        commands.add(new HelpCommand(getCommandName()));
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        return super.onCommand(player, args);
    }

    @Override
    public boolean onCommand(ConsoleCommandSender sender, String[] args) {
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
