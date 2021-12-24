package me.happybavarian07.listeners;

import me.happybavarian07.main.CAPluginMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BedListener implements Listener {
	
	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent e) {
		if(e.getPlayer().getWorld().getName().equals(CAPluginMain.getPlugin().getConfig().getString("CA.world.CraftAttack_World"))) {
			List<Player> players = new ArrayList<>();
			List<Player> onlineplayers = new ArrayList<>();
			e.getPlayer().setMetadata("BedEntered", new FixedMetadataValue(CAPluginMain.getPlugin(), true));
			onlineplayers.addAll(Bukkit.getOnlinePlayers());
			for(int i = 0; i < onlineplayers.size(); i++) {

				if(onlineplayers.get(i).hasMetadata("BedEntered") && onlineplayers.get(i).getWorld().getName().equals(CAPluginMain.getPlugin().getConfig().getString("CA.world.CraftAttack_World"))) {
//					if(!onlineplayers.get(i).getScoreboardTags().contains("CamAccountScoreboardTag")) {
						players.add(onlineplayers.get(i));
//					}
				}
			}
			Bukkit.getScheduler().runTaskLater(CAPluginMain.getPlugin(), new Runnable() {
				
				@Override
				public void run() {
					if(!e.getPlayer().hasMetadata("BedEntered")) {
						return;
					}
					if(e.getPlayer().getWorld().getTime() > 13000 && e.getPlayer().getWorld().getTime() < 22000) {
						for(Player p : Bukkit.getOnlinePlayers()) {
							if(p.getWorld().equals(e.getPlayer().getWorld()))
								p.sendMessage("§6" + e.getPlayer().getName() + " entered bed! §1[§5" + players.size() + "§1/§5" + (Bukkit.getOnlinePlayers().size() / 2) + "§1] §6Players are sleeping!\n §7");
						}
						if((Bukkit.getOnlinePlayers().size() / 2) <= players.size()) {
							e.getPlayer().removeMetadata("BedEntered", CAPluginMain.getPlugin());
							e.getPlayer().getWorld().setTime(1000);
							Random rnd = new Random();
							int max = 4;
							switch (rnd.nextInt(max)) {
							case 0: {
								for(Player p : Bukkit.getOnlinePlayers()) {
									if(p.getWorld().equals(e.getPlayer().getWorld()))
									p.sendMessage("§6Good Morning...");
								}
								break;
							}
							case 1: {
								for(Player p : Bukkit.getOnlinePlayers()) {
									if(p.getWorld().equals(e.getPlayer().getWorld()))
									p.sendMessage("§6Wake up wake up it's morning!");
								}
								break;
							}
							case 2: {
								for(Player p : Bukkit.getOnlinePlayers()) {
									if(p.getWorld().equals(e.getPlayer().getWorld()))
									p.sendMessage("§6Get out of bed there is a lot to do!");
								}
								break;
							}
							case 3: {
								for(Player p : Bukkit.getOnlinePlayers()) {
									if(p.getWorld().equals(e.getPlayer().getWorld()))
									p.sendMessage("§4Watch out, there's a creeper!");
								}
								Bukkit.getScheduler().runTaskLater(CAPluginMain.getPlugin(), new Runnable() {
									
									@Override
									public void run() {
										for(Player p : Bukkit.getOnlinePlayers()) {
											if(p.getWorld().equals(e.getPlayer().getWorld()))
											p.sendMessage("§aAll good was just a test");
										}
									}
								}, 80L);
								break;
							}
							case 4: {
								for(Player p : Bukkit.getOnlinePlayers()) {
									if(p.getWorld().equals(e.getPlayer().getWorld()))
									p.sendMessage("§6wakey wakey!");
								}
								break;
							}
							default:
								for(Player p : Bukkit.getOnlinePlayers()) {
									if(p.getWorld().equals(e.getPlayer().getWorld()))
									p.sendMessage("§6Give me something to eat, I'm hungry after sleeping!");
								}
								break;
							}
						}
					}
				}
			}, 20L);
		}
	}

	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent e) {
		if(e.getPlayer().getWorld().getName().equals(CAPluginMain.getPlugin().getConfig().getString("CA.world.CraftAttack_World"))) {
			List<Player> players = new ArrayList<>();
			List<Player> onlineplayers = new ArrayList<>();
			e.getPlayer().removeMetadata("BedEntered", CAPluginMain.getPlugin());
			onlineplayers.addAll(Bukkit.getOnlinePlayers());
			for(int i = 0; i < onlineplayers.size(); i++) {
				
				if(onlineplayers.get(i).hasMetadata("BedEntered") && onlineplayers.get(i).getWorld().getName().equals(CAPluginMain.getPlugin().getConfig().getString("CA.world.CraftAttack_World"))) {
//					if(!onlineplayers.get(i).getScoreboardTags().contains("CamAccountScoreboardTag")) {
						players.add(onlineplayers.get(i));
//					}
				}
			}
			if(e.getPlayer().getWorld().getTime() > 13000 && e.getPlayer().getWorld().getTime() < 22000) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(p.getWorld().equals(e.getPlayer().getWorld()))
					p.sendMessage("§6" + e.getPlayer().getName() + " left bed! §1[§5" + players.size() + "§1/§5" + (Bukkit.getOnlinePlayers().size() / 2) + "§1] §6Players are sleeping!\n §7");
				}
			}
		}
	}
}
