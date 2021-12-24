package me.happybavarian07.subCommands.CraftAttackCommand;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 15:15
 */

import de.happybavarian07.main.AdminPanelMain;
import de.happybavarian07.utils.PluginUtils;
import me.happybavarian07.SubCommand;
import me.happybavarian07.main.Prefix;
import me.happybavarian07.main.Utils;
import net.dv8tion.jda.api.JDA;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReloadCommand extends SubCommand {
    @Override
    public boolean onCommand(Player player, String[] args) {
        if (args.length != 1) {
            return false;
        }
        if (args[0].equalsIgnoreCase("Config")) {
            // Reload the Config
            plugin.reloadConfig();
            plugin.getInfoConfig().reloadConfig();
            plugin.getRuleConfig().reloadConfig();
            plugin.getDiscordConfig().reloadConfig();
            lgm.reloadLanguages(player, true);
            player.sendMessage(lgm.getMessage("Plugin.ReloadedConfigSuccessfully", player));
        }
        if (args[0].equalsIgnoreCase("Tablist")) {
            //Reload Tablist
            try {
                plugin.getPrefixConfig().load(plugin.getPrefixFile());
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            for (Player player2 : Bukkit.getOnlinePlayers()) {
                if (player2.getWorld().equals(plugin.getCraftAttackWorld())) {
                    Utils.setPlayerPrefix(player2, Utils.getPrefixFromConfig(player2) != null ? Utils.getPrefixFromConfig(player2) : Prefix.EMPTY);
                    Utils.loadTablist(player2, true);
                }
            }
            player.sendMessage(lgm.getMessage("Plugin.ReloadedTablistSuccessfully", player));
        }
        if (args[0].equalsIgnoreCase("Plugin")) {
            // Reload the Plugin
            Bukkit.broadcastMessage("             §4+---------------------------------+");
            Bukkit.broadcastMessage("             §4|   §4§l§nRestarting CraftAttack Plugin!§r   §4|");
            Bukkit.broadcastMessage("             §4+---------------------------------+");
            try {
                for (Player player2 : Bukkit.getOnlinePlayers()) {
                    player2.removeMetadata("CraftAttackPluginSpawnElytra", plugin);
                    player2.removeMetadata("SpawnTeleportAnfrage", plugin);
                    player2.removeMetadata("CamAccountScoreboardTag", plugin);
                }
                plugin.getDiscord().getBot().shutdown();
                AdminPanelMain.getAPI().reloadPlugin(plugin);
            } catch (NoClassDefFoundError ignored) {
            }
            Bukkit.broadcastMessage("                 §a+--------------------------+");
            Bukkit.broadcastMessage("                 §a|                 §e§l§nDone!§r                §a|");
            Bukkit.broadcastMessage("                 §a+--------------------------+");
            player.sendMessage(lgm.getMessage("Plugin.ReloadedPluginSuccessfully", player));
        }
        return true;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String info() {
        return "The Reload Command";
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }

    @Override
    public Map<Integer, String[]> subArgs() {
        Map<Integer, String[]> subArgs = new HashMap<>();
        subArgs.put(1, new String[]{"Config", "Tablist", "Plugin"});
        return subArgs;
    }

    @Override
    public String syntax() {
        return "/ca reload <Config|Tablist|Plugin>";
    }

    @Override
    public String permission() {
        return "ca.admin.control";
    }
}
