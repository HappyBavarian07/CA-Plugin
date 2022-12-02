package me.happybavarian07.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class AllCommandsCompleter implements TabCompleter {

    List<String> debugargs = new ArrayList<>();
    List<String> prefixargs = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        List<String> worlds = new ArrayList<>();
        if (cmd.getName().equalsIgnoreCase("debug")) {
            if (debugargs.isEmpty()) {
                debugargs.add("WorldCheck");
                debugargs.add("rulesactivated");
                debugargs.add("prefixes");
            }

            List<String> result = new ArrayList<String>();
            if (args.length == 1) {
                for (String a : debugargs) {
                    if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(a);
                }
                return result;
            }
            if (args.length > 1) {
                return null;
            }
            return debugargs;
        }
        // TODO Noch ein paar Bug fixes machen und dann das update hochladen ohne das Feature mit CA DEv anzukündigen
        if (cmd.getName().equalsIgnoreCase("prefix")) {
            if (prefixargs.isEmpty()) {
                prefixargs.addAll(CAPluginMain.getPlugin().getPrefixList().keySet());
                prefixargs.remove("CAPluginDev");
            }

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : prefixargs) {
                    if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(a);
                }
                return result;
            }
            if (args.length > 1) {
                return null;
            }
            return prefixargs;
        }
        return null;
    }
}
