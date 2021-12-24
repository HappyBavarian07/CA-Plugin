package me.happybavarian07.subCommands.MarkerCommands;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 17:19
 */

import me.happybavarian07.Marker;
import me.happybavarian07.PaginatedList;
import me.happybavarian07.SubCommand;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class MarkerListCommand extends SubCommand {
    @Override
    public boolean onCommand(Player player, String[] args) {
        if (args.length != 1) {
            return false;
        }
        File[] allmarkerlist = new File(CAPluginMain.getPlugin().getDataFolder() + File.separator + "Markers").listFiles();
        List<Marker> playerMarkers = new ArrayList<>();
        assert allmarkerlist != null;
        if (allmarkerlist.length == 0) {
            player.sendMessage(lgm.getMessage("Player.Marker.NoMarkersFound", player));
            return true;
        }
        for (File file : allmarkerlist) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (UUID.fromString(Objects.requireNonNull(config.getString("Marker.Player.UUID"))).equals(player.getUniqueId())) {
                playerMarkers.add(new Marker(file));
            }
        }

        if (playerMarkers.size() == 0) {
            player.sendMessage(lgm.getMessage("Player.Marker.NoMarkersFound", player));
            return true;
        }
        try {
            int page = Integer.parseInt(args[0]);
            PaginatedList<Marker> helpMessages = new PaginatedList<>(playerMarkers).maxItemsPerPage(10).sort();
            if (!helpMessages.containsPage(page)) {
                player.sendMessage(lgm.getMessage("Player.Marker.List.PageDoesNotExist", player));
                return true;
            }
            player.sendMessage(lgm.getMessage("Player.Marker.List.Header", player)
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf(helpMessages.getMaxPage())));
            for (Marker marker : helpMessages.getPage(page)) {
                player.sendMessage(format(lgm.getMessage("Player.Marker.List.Format", player), marker));
            }
            player.sendMessage(lgm.getMessage("Player.Marker.List.Footer", player)
                    .replace("%page%", String.valueOf(page))
                    .replace("%max_page%", String.valueOf(helpMessages.getMaxPage())));
        } catch (NumberFormatException e) {
            player.sendMessage(lgm.getMessage("Player.Commands.NotANumber", player));
            return true;
        } catch (PaginatedList.ListNotSortedException e2) {
            e2.printStackTrace();
            return true;
        }
        return true;
    }

    @Override
    public String name() {
        return "list";
    }

    @Override
    public String info() {
        return "Lists all Markers";
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
        return "/marker list <Page>";
    }

    @Override
    public String permission() {
        return "ca.player.marker";
    }

    private String format(String in, Marker marker) {
        return in.replace("%name%", marker.getName())
                .replace("%file%", marker.getFile().getPath())
                .replace("%world%", marker.getWorld().getName())
                .replace("%x%", String.valueOf(marker.getX().intValue()))
                .replace("%y%", String.valueOf(marker.getY().intValue()))
                .replace("%z%", String.valueOf(marker.getZ().intValue()))
                .replace("%yaw%", String.valueOf(marker.getYaw()))
                .replace("%pitch%", String.valueOf(marker.getPitch()));
    }
}
