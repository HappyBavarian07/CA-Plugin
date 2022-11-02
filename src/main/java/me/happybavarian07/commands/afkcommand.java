package me.happybavarian07.commands;

import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.main.Prefix;
import me.happybavarian07.main.Utils;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class afkcommand implements CommandExecutor, Listener {
	private final CAPluginMain plugin = CAPluginMain.getPlugin();
	private final LanguageManager lgm = plugin.getLanguageManager();
	private static final Map<Player, Boolean> afkplayers = new HashMap<>();
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if(args.length == 1) {
			if(player.hasPermission("ca.afk.self")) {
				if(args[0].equalsIgnoreCase("on")) {
					if(!afkplayers.containsKey(player)) {
						afkplayers.put(player, true);
						player.sendMessage(lgm.getMessage("Player.Afk.AfkOn", player, false));
						player.setCanPickupItems(false);
						Utils.setPlayerPrefix(player, Prefix.AFK);
					} else {
						player.sendMessage(lgm.getMessage("Player.Afk.AlreadyAfk", player, false));
					}
					return true;
				} else {
					if(args[0].equalsIgnoreCase("off")) {
						if(afkplayers.containsKey(player)) {
							player.sendMessage(lgm.getMessage("Player.Afk.AfkOff", player, false));
							afkplayers.remove(player);
							player.setCanPickupItems(true);
							Utils.setPlayerPrefix(player, Prefix.STANDARD);
						} else {
							player.sendMessage(lgm.getMessage("Player.Afk.NotAfk", player, false));
						}
						return true;
					}
				}
			} else {
				player.sendMessage(lgm.getMessage("Player.NoPermissions", player, false));
				return true;
			}
		}
		if(args.length == 2) {
			if(player.hasPermission("ca.afk.other")) {
				try {
					Player target = player.getServer().getPlayerExact(args[0]);
					if(args[1].equalsIgnoreCase("on")) {
						if(!afkplayers.containsKey(target)) {
							afkplayers.put(target, true);
							player.sendMessage(lgm.getMessage("Player.Afk.AfkOnOther", player, false));
							target.sendMessage(lgm.getMessage("Player.Afk.AfkOnOtherTarget", player, false));
							Utils.setPlayerPrefix(target, Prefix.AFK);
						} else {
							player.sendMessage(lgm.getMessage("Player.Afk.AlreadyAfkOther", player, false));
						}
					} else {
						if(args[1].equalsIgnoreCase("off")) {
							if(afkplayers.containsKey(target)) {
								player.sendMessage(lgm.getMessage("Player.Afk.AfkOffOther", player, false));
								target.sendMessage(lgm.getMessage("Player.Afk.AfkOffOtherTarget", player, false));
								afkplayers.remove(target);
								Utils.setPlayerPrefix(target, Prefix.STANDARD);
							} else {
								player.sendMessage(lgm.getMessage("Player.Afk.NotAfkOther", player, false));
							}
						}
					}
				} catch (NullPointerException e) {
					player.sendMessage(lgm.getMessage("Player.PlayerIsNull", player, false));
				}
			} else {
				player.sendMessage(lgm.getMessage("Player.NoPermissions", player, false));
			}
		}
		
		return true;
	}

	public static Map<Player, Boolean> getAfkplayers() {
		return afkplayers;
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(afkplayers.containsKey(p)) {
			p.sendMessage(lgm.getMessage("Player.Afk.MoveWhileAfk", p, false));
			e.setCancelled(true);
		}
	}
}
