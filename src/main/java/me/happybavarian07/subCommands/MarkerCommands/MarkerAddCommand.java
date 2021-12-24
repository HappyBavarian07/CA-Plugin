package me.happybavarian07.subCommands.MarkerCommands;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 17:05
 */

import me.happybavarian07.Marker;
import me.happybavarian07.SubCommand;
import me.happybavarian07.events.CreateCraftAttackMarkerEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MarkerAddCommand extends SubCommand {
    @Override
    public boolean onCommand(Player player, String[] args) {
        if(args.length != 1) {
            return false;
        }
        Marker marker = new Marker(player.getLocation(), player, args[0].replace("&", "").replace("§", ""));
        if(marker.getFile() != null && marker.getFile().exists()) {
            player.sendMessage(lgm.getMessage("Player.Marker.MarkerAlreadyExists", player).replace("%marker%", marker.getName()));
            return true;
        }
        CreateCraftAttackMarkerEvent markerCreateEvent = new CreateCraftAttackMarkerEvent(marker, player);
        Bukkit.getPluginManager().callEvent(markerCreateEvent);
        if(!markerCreateEvent.isCancelled()) {
            marker.createMarkerFile();
            player.sendMessage(lgm.getMessage("Player.Marker.MarkerSaved", player).replace("%marker%", marker.getName()));
        }
        return true;
    }

    @Override
    public String name() {
        return "add";
    }

    @Override
    public String info() {
        return "Adds a Marker";
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
        return "/marker add <Name>";
    }

    @Override
    public String permission() {
        return "ca.player.marker";
    }
}
