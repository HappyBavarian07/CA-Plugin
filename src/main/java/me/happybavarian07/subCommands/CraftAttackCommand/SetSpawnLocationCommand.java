package me.happybavarian07.subCommands.CraftAttackCommand;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 15:37
 */

import me.happybavarian07.commandmanagement.SubCommand;
import me.happybavarian07.commandmanagement.CommandData;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@CommandData(playerRequired = true)
public class SetSpawnLocationCommand extends SubCommand {
    public SetSpawnLocationCommand(String mainCommandName) {
        super(mainCommandName);
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] args) {
        if (args.length != 0) {
            return false;
        }
        FileConfiguration spawnconfig = plugin.spawnconfig;
        if (player.hasPermission("ca.admin.setspawnloc")) {
            spawnconfig.set("CraftAttack.Spawn.World", Objects.requireNonNull(player.getLocation().getWorld()).getName());
            spawnconfig.set("CraftAttack.Spawn.X", player.getLocation().getX());
            spawnconfig.set("CraftAttack.Spawn.Y", player.getLocation().getY());
            spawnconfig.set("CraftAttack.Spawn.Z", player.getLocation().getZ());
            spawnconfig.set("CraftAttack.Spawn.Yaw", player.getLocation().getYaw());
            spawnconfig.set("CraftAttack.Spawn.Pitch", player.getLocation().getPitch());
            try {
                spawnconfig.save(plugin.fm.getFile("", "spawnloc", "yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.sendMessage(lgm.getMessage("Player.CraftAttackSpawnSet", player, false));
        }
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender sender, String[] args) {
        return false;
    }

    @Override
    public String name() {
        return "setspawnlocation";
    }

    @Override
    public String info() {
        return "Sets the Spawn Location";
    }

    @Override
    public String[] aliases() {
        return new String[] {"setspawnloc", "setspawn", "ssl"};
    }

    @Override
    public Map<Integer, String[]> subArgs() {
        return new HashMap<>();
    }

    @Override
    public String syntax() {
        return "/ca ssl";
    }

    @Override
    public String permission() {
        return "ca.admin.setspawnloc";
    }
}
