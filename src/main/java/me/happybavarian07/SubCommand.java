package me.happybavarian07;
/*
 * @Author HappyBavarian07
 * @Date 05.10.2021 | 17:28
 */

import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;

public abstract class SubCommand {
    protected CAPluginMain plugin = CAPluginMain.getPlugin();
    protected LanguageManager lgm = plugin.getLanguageManager();
    /*
    /<command> <subcommand> args[0] args[1]
     */

    public SubCommand() {

    }

    public abstract boolean onCommand(Player player, String[] args);

    public abstract String name();

    public abstract String info();

    public abstract String[] aliases();

    public abstract Map<Integer, String[]> subArgs();

    public abstract String syntax();

    public abstract String permission();

    protected String format(String in, SubCommand cmd) {
        return in.replace("%usage%", cmd.syntax())
                .replace("%description%", cmd.info())
                .replace("%name%", cmd.name())
                .replace("%permission%", cmd.permission())
                .replace("%aliases%", Arrays.toString(cmd.aliases()));
    }
}
