package me.happybavarian07.subCommands.WorldCommands;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 16:20
 */

import me.happybavarian07.commandmanagement.CommandData;
import me.happybavarian07.commandmanagement.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

@CommandData(playerRequired = true)
public class TeleportWorldCommand extends SubCommand {
    public TeleportWorldCommand(String mainCommandName) {
        super(mainCommandName);
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] args) {
        if(args.length != 1) {
            return false;
        }
        World world = Bukkit.getWorld(args[0]);
        if (world != null) {
            player.teleport(world.getSpawnLocation());
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender sender, String[] args) {
        return false;
    }

    @Override
    public String name() {
        return "teleport";
    }

    @Override
    public String info() {
        return "Teleports you to the Worlds Spawn";
    }

    @Override
    public String[] aliases() {
        return new String[] {"tp"};
    }

    @Override
    public Map<Integer, String[]> subArgs() {
        Map<Integer, String[]> subArgs = new HashMap<>();
        List<String> worldNames = new ArrayList<>();
        for(File file : Bukkit.getServer().getWorldContainer().listFiles()) {
            if(file.isFile()) continue;
            if(file.getName().equals("plugins") && file.getName().equals("logs") && file.getName().equals("crash-reports")) continue;
            if(Bukkit.getWorld(file.getName()) != null) continue;

            // Adding all World Files from not loaded Worlds
            worldNames.add(file.getName());
        }
        subArgs.put(1, worldNames.toArray(new String[0]));
        return subArgs;
    }

    @Override
    public String syntax() {
        return "/world tp <World>";
    }

    @Override
    public String permission() {
        return "ca.admin.world.teleport";
    }
}
