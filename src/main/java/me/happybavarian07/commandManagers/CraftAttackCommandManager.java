package me.happybavarian07.commandManagers;/*
 * @Author HappyBavarian07
 * @Date 05.10.2021 | 17:30
 */

import me.happybavarian07.commandmanagement.CommandData;
import me.happybavarian07.commandmanagement.CommandManager;
import me.happybavarian07.commandmanagement.SubCommand;
import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.subCommands.CraftAttackCommand.*;
import me.happybavarian07.subCommands.HelpCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

@CommandData()
public class CraftAttackCommandManager extends CommandManager {

    @Override
    public String getCommandName() {
        return "craftattack";
    }

    @Override
    public String getCommandUsage() {
        return "/ca <SubCommand> (/ca help <Page>)";
    }

    @Override
    public String getCommandInfo() {
        return "The Main Craft Attack Command";
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

    @Override
    public void setup() {
        // TODO Maybe bei allen Commands die langen Methoden in eine Methode schreiben auf die onPlayerCommand und onConsoleCommand zugreifen
        commands.add(new ReloadCommand(getCommandName()));
        commands.add(new CamPlayersCommand(getCommandName()));
        commands.add(new CAPlayersCommand(getCommandName()));
        commands.add(new SetSpawnLocationCommand(getCommandName()));
        commands.add(new CASpawnCommand(getCommandName()));
        commands.add(new SelectorInvCommand(getCommandName()));
        commands.add(new HelpCommand(getCommandName()));
        if(Bukkit.getPluginManager().isPluginEnabled("Admin-Panel")) {
            commands.add(new UpdateCommand(getCommandName()));
        }
    }

    @Override
    public List<SubCommand> getSubCommands() {
        return super.getSubCommands();
    }
}
