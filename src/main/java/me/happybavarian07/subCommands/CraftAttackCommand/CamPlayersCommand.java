package me.happybavarian07.subCommands.CraftAttackCommand;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 15:31
 */

import me.happybavarian07.SubCommand;
import me.happybavarian07.main.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CamPlayersCommand extends SubCommand {
    @Override
    public boolean onCommand(Player player, String[] args) {
        if (args.length != 0) {
            return false;
        }
        player.sendMessage("§5--------------§6Cam-Craft-§5Attack-§2Players§5----------------");
        int count = 0;
        for (String name : plugin.getConfig().getStringList("CA.world.Cam_Players")) {
            count++;
            Player target = Bukkit.getPlayer(name);
            if (target != null) {
                player.sendMessage("§3" + count + ". §2" + name + (target.isOnline() ? "  |  §a§lOnline" : "  |  §4§lOffline"));
                player.sendMessage("§3    Current_Prefix: §2" + Utils.getPrefixFromConfig(target).getConfigName());
                player.sendMessage("§3    Current_World: §2" + target.getWorld().getName());
                player.sendMessage("§3    Elytra_Fly: §2" + (target.hasMetadata("CraftAttackPluginSpawnElytra") ? "On" : "Off"));
            } else {
                player.sendMessage("§3" + count + ". §2" + name + "§3:  |  §4§lOffline");
            }
        }
        player.sendMessage("§5-----------------------------------------------------");
        return true;
    }

    @Override
    public String name() {
        return "CamPlayers";
    }

    @Override
    public String info() {
        return "The Cam Players List Command";
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
        return "/ca CamPlayers";
    }

    @Override
    public String permission() {
        return "ca.admin.control";
    }
}
