package me.happybavarian07;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 14:43
 */

import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.main.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CommandManager {
    protected final ArrayList<SubCommand> commands = new ArrayList<>();
    protected final CAPluginMain plugin = CAPluginMain.getPlugin();
    protected final LanguageManager lgm = plugin.getLanguageManager();
    protected List<String> commandArgs = new ArrayList<>();
    protected List<String> commandSubArgs = new ArrayList<>();

    public abstract String getCommandName();

    public abstract String getCommandInfo();

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

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        //System.out.println("Test 2");
        commandArgs.clear();
        commandSubArgs.clear();
        for (SubCommand sub : this.getSubCommands()) {
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
            //System.out.println("Test SubArgs 1");
            int count = 0;
            for (SubCommand sub : this.getSubCommands()) {
                if (sub.subArgs() == null || sub.aliases() == null || sub.subArgs().get(args.length - 1) == null) {
                    if (count >= this.getSubCommands().size()) {
                        return Utils.emptyList();
                    } else {
                        continue;
                    }
                }
                if (sub.subArgs().get(args.length - 1) == null) {
                    if (count >= this.getSubCommands().size()) {
                        return Utils.emptyList();
                    } else {
                        continue;
                    }
                }
                String[] subArgs;
                int length = (subArgs = sub.subArgs().get(args.length - 1)).length;
                if (length == 0) {
                    if (count >= this.getSubCommands().size()) {
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
                                //System.out.println("Test SubArgs 2 (args)");
                                List<String> result1 = new ArrayList<>();
                                for (String a : commandSubArgs) {
                                    if (a.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                                        result1.add(a);
                                }
                                return result1;
                            }
                        }
                        if (count >= this.getSubCommands().size()) {
                            return Utils.emptyList();
                        }
                    }
                } else {
                    if (sender.hasPermission(sub.permission())) {
                        if (sender.hasPermission(sub.permission())) {
                            commandSubArgs.addAll(Arrays.asList(subArgs).subList(0, length));
                        }
                        //System.out.println("Test SubArgs 2 (name)");
                        List<String> result1 = new ArrayList<>();
                        for (String a : commandSubArgs) {
                            if (a.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                                result1.add(a);
                        }
                        return result1;
                    }
                }
                count++;
            }
            return Utils.emptyList();
        }
        return Utils.emptyList();
    }

    public abstract void setup();

    public List<SubCommand> getSubCommands() {
        return commands;
    }

    protected SubCommand getSub(String name) {
        for (SubCommand sub : getSubCommands()) {
            if (sub.name().equalsIgnoreCase(name)) {
                return sub;
            }

            String[] aliases;
            int length = (aliases = sub.aliases()).length;

            for (int i = 0; i < length; i++) {
                String alias = aliases[i];
                if (name.equalsIgnoreCase(alias)) {
                    return sub;
                }
            }
        }
        return null;
    }

    private String format(String in, SubCommand cmd) {
        return in.replace("%usage%", cmd.syntax())
                .replace("%description%", cmd.info())
                .replace("%name%", cmd.name())
                .replace("%permission%", cmd.permission())
                .replace("%aliases%", Arrays.toString(cmd.aliases()));
    }
}
