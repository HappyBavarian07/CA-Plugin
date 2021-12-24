package me.happybavarian07.subCommands.WorldCommands;/*
 * @Author HappyBavarian07
 * @Date 09.11.2021 | 16:20
 */

import me.happybavarian07.SubCommand;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class ImportWorldCommand extends SubCommand {
    @Override
    public boolean onCommand(Player player, String[] args) {
        if(args.length != 1) {
            return false;
        }
        String worldToImportName = args[0];
        int count = 0;
        for (File file : Objects.requireNonNull(Bukkit.getServer().getWorldContainer().listFiles())) {
            if (file.isFile()) continue;
            if (CAPluginMain.getPlugin().getConfig().getStringList("FileBlacklist").contains(Objects.requireNonNull(Bukkit.getServer().getWorldContainer().listFiles())[count].getName())) continue;
            if (Bukkit.getWorld(file.getName()) != null) continue;

            // The World Name is the Name of the File that equals to that
            count++;
            if(worldToImportName.equals(file.getName())) {
                WorldCreator.name(worldToImportName).createWorld();
                World worldToImport = Bukkit.getWorld(worldToImportName);
                player.teleport(worldToImport.getSpawnLocation());
                player.sendMessage("§3Importet World §f" + worldToImport.getName() + "§3 successfully!");
                return true;
            }
        }
        player.sendMessage("§4No World with this Name found!");
        return true;
    }

    @Override
    public String name() {
        return "import";
    }

    @Override
    public String info() {
        return "Imports the Given World";
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }

    @Override
    public Map<Integer, String[]> subArgs() {
        Map<Integer, String[]> subArgs = new HashMap<>();
        List<String> worldNames = new ArrayList<>();
        for(File file : Bukkit.getServer().getWorldContainer().listFiles()) {
            if(file.isFile()) continue;
            if(file.getName().equals("plugins") && file.getName().equals("logs") && file.getName().equals("crash-reports")) continue;
            if(Bukkit.getWorld(file.getName()) == null) continue;

            // Adding all World Files from not loaded Worlds
            worldNames.add(file.getName());
        }
        subArgs.put(1, worldNames.toArray(new String[0]));
        return subArgs;
    }

    @Override
    public String syntax() {
        return "/world import <FileName>";
    }

    @Override
    public String permission() {
        return "ca.admin.world.import";
    }
}
