package me.happybavarian07.subCommands.MarkerCommands;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 17:09
 */

import me.happybavarian07.Marker;
import me.happybavarian07.commandmanagement.SubCommand;
import me.happybavarian07.events.RemoveCraftAttackMarkerEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MarkerRemoveCommand extends SubCommand {
    public MarkerRemoveCommand(String mainCommandName) {
        super(mainCommandName);
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] args) {
        if(args.length != 1) {
            return false;
        }
        File playermarker = null;
        for(File file : Objects.requireNonNull(new File(plugin.getDataFolder() + File.separator + "Markers").listFiles())) {
            if (file.getName().replace(".yml", "").equals(args[0])) {
                playermarker = file;
            }
        }
        if(Objects.requireNonNull(new File(plugin.getDataFolder() + File.separator + "Markers").listFiles()).length == 0) {
            player.sendMessage(lgm.getMessage("Player.Marker.NoMarkersFound", player, false).replace("%marker%", args[0]));
            return true;
        }
        if(playermarker == null) {
            player.sendMessage(lgm.getMessage("Player.Marker.MarkerDoesntExists", player, false).replace("%marker%", args[0]));
            return true;
        }

        RemoveCraftAttackMarkerEvent removeMarkerEvent = new RemoveCraftAttackMarkerEvent(new Marker(playermarker), player);
        Bukkit.getPluginManager().callEvent(removeMarkerEvent);
        if (!removeMarkerEvent.isCancelled()) {
            if (UUID.fromString(Objects.requireNonNull(YamlConfiguration.loadConfiguration(playermarker).getString("Marker.Player.UUID"))).equals(player.getUniqueId())) {
                playermarker.delete();
                player.sendMessage(lgm.getMessage("Player.Marker.MarkerRemoved", player, false).replace("%marker%", args[0]));
                return true;
            }
        }
        player.sendMessage(lgm.getMessage("Player.Marker.MarkerDoesntExists", player, false).replace("%marker%", args[0]));
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender sender, String[] args) {
        return false;
    }

    @Override
    public String name() {
        return "remove";
    }

    @Override
    public String info() {
        return "Removes a Marker";
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }

    @Override
    public Map<Integer, String[]> subArgs() {
        return new HashMap<>();
    }

    @Override
    public String syntax() {
        return "/marker remove <Name>";
    }

    @Override
    public String permission() {
        return "ca.player.marker";
    }
}
