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
    // Sub Commands
    List<String> commandArgs = new ArrayList<>();
    List<String> commandSubArgs = new ArrayList<>();

    @Override
    public String getCommandName() {
        return "craftattack";
    }

    @Override
    public String getCommandInfo() {
        return "The Main Craft Attack Command";
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
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        SubCommand target = this.getSub(args[0]);

        if (target == null) {
            player.sendMessage(lgm.getMessage("Player.Commands.InvalidSubCommand", player));
            return true;
        }

        if (!player.hasPermission(target.permission())) {
            player.sendMessage(format(lgm.getMessage("Player.NoPermissions", player), target));
            return true;
        }

        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(args));
        arrayList.remove(0);
        String[] updatedArgs = new String[arrayList.size()];
        int count = 0;
        for (String s : arrayList) {
            updatedArgs[count] = s;
            count++;
        }
        try {
            boolean callResult = target.onCommand(player, updatedArgs);
            if (!callResult) {
                player.sendMessage(format(lgm.getMessage("Player.Commands.UsageMessage", player), target));
            }
        } catch (Exception e) {
            player.sendMessage(format(lgm.getMessage("Player.Commands.ErrorPerformingSubCommand", player), target));
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        commandArgs.clear();
        commandSubArgs.clear();
        for (SubCommand sub : this.commands) {
            String[] aliases;
            int length = (aliases = sub.aliases()).length;
            if (sender.hasPermission(sub.permission())) {
                commandArgs.add(sub.name());
                commandArgs.addAll(Arrays.asList(aliases).subList(0, length));
            }
        }

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : commandArgs) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
            }
            return result;
        }
        if (args.length > 1) {
            int count = 0;
            for (SubCommand sub : this.commands) {
                if (sub.subArgs() == null || sub.aliases() == null || sub.subArgs().get(args.length - 1) == null) {
                    if (count >= this.commands.size()) {
                        return Utils.emptyList();
                    } else {
                        continue;
                    }
                }
                if (sub.subArgs().get(args.length - 1) == null) {
                    if (count >= this.commands.size()) {
                        return Utils.emptyList();
                    } else {
                        continue;
                    }
                }
                String[] subArgs;
                int length = (subArgs = sub.subArgs().get(args.length - 1)).length;
                if (length == 0) {
                    if (count >= this.commands.size()) {
                        return Utils.emptyList();
                    } else {
                        continue;
                    }
                }
                if (!sub.name().equals(args[0])) {
                    for (String s : sub.aliases()) {
                        if (s.equals(args[0])) {
                            if (sender.hasPermission(sub.permission())) {
                                if (sender.hasPermission(sub.permission())) {
                                    commandSubArgs.addAll(Arrays.asList(subArgs).subList(0, length));
                                }
                                return commandSubArgs;
                            }
                        }
                        if (count >= this.commands.size()) {
                            return Utils.emptyList();
                        }
                    }
                } else {
                    if (sender.hasPermission(sub.permission())) {
                        if (sender.hasPermission(sub.permission())) {
                            commandSubArgs.addAll(Arrays.asList(subArgs).subList(0, length));
                        }
                        return commandSubArgs;
                    }
                }
                count++;
            }
            return Utils.emptyList();
        }
        return Utils.emptyList();
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
