package me.happybavarian07.commandmanagement;
/*
 * @Author HappyBavarian07
 * @Date 05.10.2021 | 17:28
 */

import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.language.Placeholder;
import me.happybavarian07.language.PlaceholderType;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CommandData
public abstract class SubCommand {
    protected CAPluginMain plugin = CAPluginMain.getPlugin();
    protected LanguageManager lgm = plugin.getLanguageManager();
    protected CommandManagerRegistry registry = plugin.getCommandManagerRegistry();
    protected String mainCommandName = "";
    /*
    /<command> <subcommand> args[0] args[1]
     */

    public SubCommand(String mainCommandName) {
        this.mainCommandName = mainCommandName;
    }

    public boolean isPlayerRequired() {
        if (!this.getClass().isAnnotationPresent(CommandData.class)) return false;
        if(registry.isPlayerRequired(registry.getCommandManager(mainCommandName))) return true;
        return this.getClass().getAnnotation(CommandData.class).playerRequired();
    }

    public boolean isOpRequired() {
        if (!this.getClass().isAnnotationPresent(CommandData.class)) return false;
        if(registry.isOpRequired(registry.getCommandManager(mainCommandName))) return true;
        return this.getClass().getAnnotation(CommandData.class).opRequired();
    }

    public abstract boolean onPlayerCommand(Player player, String[] args);

    public abstract boolean onConsoleCommand(ConsoleCommandSender sender, String[] args);

    public abstract String name();

    public abstract String info();

    public abstract String[] aliases();

    public abstract Map<Integer, String[]> subArgs();

    public abstract String syntax();

    public abstract String permission();

    protected String format(String in, SubCommand cmd) {
        Map<String, Placeholder> placeholders = new HashMap<>();
        placeholders.put("%usage%", new Placeholder("%usage%", cmd.syntax(), PlaceholderType.ALL));
        placeholders.put("%description%", new Placeholder("%description%", cmd.info(), PlaceholderType.ALL));
        placeholders.put("%name%", new Placeholder("%name%", cmd.name(), PlaceholderType.ALL));
        placeholders.put("%permission%", new Placeholder("%permission%", cmd.permission(), PlaceholderType.ALL));
        placeholders.put("%aliases%", new Placeholder("%aliases%", cmd.aliases(), PlaceholderType.ALL));
        placeholders.put("%player_required%", new Placeholder("%player_required%", cmd.isPlayerRequired(), PlaceholderType.ALL));

        return lgm.replacePlaceholders(in, placeholders);
    }
}
