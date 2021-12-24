package me.happybavarian07.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrollIOpenCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if(s instanceof Player) {
			if(cmd.getName().equalsIgnoreCase("troll")) {
				Player p = (Player) s;
				if(p.hasPermission("ca.admin.troll")) {
					TrollItemsGUI.openPlayerSelector(p);
				} else {
					p.sendMessage("§rUnknown command. type \"/help\" for help");
					return true;
				}
			}
		}
		return true;
	}
}
