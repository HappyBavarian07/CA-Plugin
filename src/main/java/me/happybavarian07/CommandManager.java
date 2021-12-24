package me.happybavarian07;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 14:43
 */

import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandManager {
    protected final ArrayList<SubCommand> commands = new ArrayList<>();
    protected final CAPluginMain plugin = CAPluginMain.getPlugin();
    protected final LanguageManager lgm = plugin.getLanguageManager();

    public abstract String getCommandName();

    public abstract String getCommandInfo();

    public abstract boolean onCommand(CommandSender sender, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args);

    public abstract void setup();

    public abstract List<SubCommand> getSubCommands();

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
}
