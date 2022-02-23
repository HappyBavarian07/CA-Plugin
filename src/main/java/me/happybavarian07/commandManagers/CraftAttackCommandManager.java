package me.happybavarian07.commandManagers;/*
 * @Author HappyBavarian07
 * @Date 05.10.2021 | 17:30
 */

import me.happybavarian07.CommandData;
import me.happybavarian07.CommandManager;
import me.happybavarian07.SubCommand;
import me.happybavarian07.main.Utils;
import me.happybavarian07.subCommands.CraftAttackCommand.*;
import me.happybavarian07.subCommands.DebugCommands.PrefixesCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandData()
public class CraftAttackCommandManager extends CommandManager {

    @Override
    public String getCommandName() {
        return "craftattack";
    }

    @Override
    public String getCommandInfo() {
        return "The Main Craft Attack Command";
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        return super.onCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return super.onTabComplete(sender, command, label, args);
    }

    @Override
    public void setup() {
        commands.add(new ReloadCommand());
        commands.add(new CamPlayersCommand());
        commands.add(new CAPlayersCommand());
        commands.add(new SetSpawnLocationCommand());
        commands.add(new CASpawnCommand());
        commands.add(new SelectorInvCommand());
        commands.add(new HelpCommand());
        commands.add(new UpdateCommand());
    }

    public ArrayList<SubCommand> getSubCommands() {
        return commands;
    }
}
