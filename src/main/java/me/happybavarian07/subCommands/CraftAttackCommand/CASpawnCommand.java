package me.happybavarian07.subCommands.CraftAttackCommand;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 15:55
 */

import me.happybavarian07.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CASpawnCommand extends SubCommand {
    @Override
    public boolean onCommand(Player player, String[] args) {
        FileConfiguration spawnconfig = plugin.spawnconfig;
        World world = Bukkit.getWorld(Objects.requireNonNull(spawnconfig.getString("CraftAttack.Spawn.World")));
        if (world == null) {
            player.sendMessage("§cNo CraftAttack World found! §4Please contact an Admin!");
            return true;
        }
        double x = spawnconfig.getDouble("CraftAttack.Spawn.X");
        double y = spawnconfig.getDouble("CraftAttack.Spawn.Y");
        double Z = spawnconfig.getDouble("CraftAttack.Spawn.Z");
        float yaw = (float) spawnconfig.getDouble("CraftAttack.Spawn.Yaw");
        float pitch = (float) spawnconfig.getDouble("CraftAttack.Spawn.Pitch");
        Location spawnloc = new Location(world, x, y, Z, yaw, pitch);
        if (args.length == 0) {
            if (!player.getWorld().equals(world)) {
                player.removeMetadata("SpawnTeleportAnfrage", plugin);
                player.teleport(spawnloc);
                player.sendMessage("§aSending you to Spawn!");
                return true;
            }
            player.setMetadata("SpawnTeleportAnfrage", new FixedMetadataValue(plugin, true));
            player.sendMessage("§6Bitte bewege dich 5 Sekunden lang nicht sonst wird der Teleport abgebrochen!");
            plugin.spawnvorgang = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.removeMetadata("SpawnTeleportAnfrage", plugin);
                player.teleport(spawnloc);
                player.sendMessage("§aSending you to Spawn!");
            }, 100);
            return true;
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(lgm.getMessage("Player.PlayerIsNull", player).replace("%target_name%", args[0]));
                return true;
            }

            target.teleport(spawnloc);
            target.sendMessage("§aSending you to Spawn!");
            player.sendMessage("§aSending " + target.getName() + " to Spawn!");
        } else {
            return false;
        }
        return true;
    }

    @Override
    public String name() {
        return "spawn";
    }

    @Override
    public String info() {
        return "Teleports you to the CA Spawn";
    }

    @Override
    public String[] aliases() {
        return new String[] {"caspawn"};
    }

    @Override
    public Map<Integer, String[]> subArgs() {
        Map<Integer, String[]> subArgs = new HashMap<>();
        String[] playerNames = new String[Bukkit.getOnlinePlayers().size()];
        int count = 0;
        for(Player player : Bukkit.getOnlinePlayers()) {
            playerNames[count] = player.getName();
            count++;
        }
        subArgs.put(1, playerNames);
        return subArgs;
    }

    @Override
    public String syntax() {
        return "/ca spawn [Player]";
    }

    @Override
    public String permission() {
        return "ca.player.spawn";
    }
}
