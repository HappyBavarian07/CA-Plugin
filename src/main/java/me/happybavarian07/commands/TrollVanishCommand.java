package me.happybavarian07.commands;

import me.happybavarian07.main.CAPluginMain;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrollVanishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!p.hasPermission("ca.admin.troll")) {
                p.sendMessage("§rUnknown command. type \"/help\" for help");
                return true;
            }
            if(args.length == 0) {
                // Vanish self
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.canSee(p)) {
                        player.showPlayer(p);
                        p.setCanPickupItems(true);
                        p.setCollidable(true);
                    } else {
                        player.hidePlayer(p);
                        p.setCanPickupItems(false);
                        p.setCollidable(false);
                    }
                }
                p.sendMessage("§1T§2r§3o§4l§5l§6V§7a§8n§9i§as§bh§a toggled!");
            } else if(args.length == 1) {
                // Vanish other
                Player target = Bukkit.getPlayerExact(args[0]);
                if(target == null) {
                    p.sendMessage(CAPluginMain.getPlugin().getLanguageManager().getMessage("Player.PlayerIsNull", p).replace("%target_name%", target.getName()));
                    return true;
                }
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(!player.canSee(target)) {
                        player.showPlayer(target);
                        target.setCanPickupItems(true);
                        target.setCollidable(true);
                    } else {
                        player.hidePlayer(target);
                        target.setCanPickupItems(false);
                        target.setCollidable(false);
                    }
                }
                target.sendMessage("§1T§2r§3o§4l§5l§6V§7a§8n§9i§as§bh§a toggled!");
                p.sendMessage("§1T§2r§3o§4l§5l§6V§7a§8n§9i§as§bh§a toggled for " + target.getName() + "!");
            } else {
                p.sendMessage(args.length > 1 ? "§4Too many Arguments!" : "§4Less Arguments!");
                // Error Message to many Arguments
            }
        } else {
            sender.sendMessage(CAPluginMain.getPlugin().getLanguageManager().getMessage("Console.ExecutesPlayerCommand", null));
        }
        return true;
    }
}
