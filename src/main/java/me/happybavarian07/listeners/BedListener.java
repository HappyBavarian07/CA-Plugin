package me.happybavarian07.listeners;

import de.happybavarian07.adminpanel.main.AdminPanelMain;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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
        if (e.getPlayer().getWorld().getName().equals(CAPluginMain.getPlugin().getConfig().getString("CA.world.CraftAttack_World"))) {
            List<Player> players = new ArrayList<>();
            e.getPlayer().setMetadata("BedEntered", new FixedMetadataValue(CAPluginMain.getPlugin(), true));
            for (Player onlineplayer : Bukkit.getOnlinePlayers()) {
                if (onlineplayer.hasMetadata("BedEntered") && onlineplayer.getWorld().getName().equals(CAPluginMain.getPlugin().getConfig().getString("CA.world.CraftAttack_World"))) {
//					if(!onlineplayers.get(i).getScoreboardTags().contains("CamAccountScoreboardTag")) {
                    players.add(onlineplayer);
//					}
                }
            }
            Bukkit.getScheduler().runTaskLater(CAPluginMain.getPlugin(), () -> {
                if (!e.getPlayer().hasMetadata("BedEntered")) {
                    return;
                }
                FileConfiguration pConfig = CAPluginMain.getPlugin().getConfig();
                if (e.getPlayer().getWorld().getTime() > 13000 && e.getPlayer().getWorld().getTime() < 22000) {
                    if (!AdminPanelMain.getPlugin().chatmute.containsKey(e.getPlayer())) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getWorld().equals(e.getPlayer().getWorld()) && pConfig.getBoolean("CA.world.Sleep-System.messageEnabled"))
                                p.sendMessage("§6" + e.getPlayer().getName() + " entered bed! §1[§5" + players.size() + "§1/§5" + (Bukkit.getOnlinePlayers().size() / 2) + "§1] §6Players are sleeping!\n §7");
                        }
                    }
                    if ((Bukkit.getOnlinePlayers().size() / pConfig.getDouble("CA.world.Sleep-System.count", 2)) <= players.size()) {
                        e.getPlayer().removeMetadata("BedEntered", CAPluginMain.getPlugin());
                        e.getPlayer().getWorld().setTime(1000);
                        Random rnd = new Random();
                        int max = 4;
                        switch (rnd.nextInt(max)) {
                            case 0: {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (p.getWorld().equals(e.getPlayer().getWorld()))
                                        p.sendMessage("§6Good Morning...");
                                }
                                break;
                            }
                            case 1: {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (p.getWorld().equals(e.getPlayer().getWorld()))
                                        p.sendMessage("§6Wake up wake up it's morning!");
                                }
                                break;
                            }
                            case 2: {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (p.getWorld().equals(e.getPlayer().getWorld()))
                                        p.sendMessage("§6Get out of bed there is a lot to do!");
                                }
                                break;
                            }
                            case 3: {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (p.getWorld().equals(e.getPlayer().getWorld()))
                                        p.sendMessage("§4Watch out, there's a creeper!");
                                }
                                Bukkit.getScheduler().runTaskLater(CAPluginMain.getPlugin(), () -> {
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (p.getWorld().equals(e.getPlayer().getWorld()))
                                            p.sendMessage("§aAll good was just a test");
                                    }
                                }, 80L);
                                break;
                            }
                            case 4: {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (p.getWorld().equals(e.getPlayer().getWorld()))
                                        p.sendMessage("§6wakey wakey!");
                                }
                                break;
                            }
                            default:
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (p.getWorld().equals(e.getPlayer().getWorld()))
                                        p.sendMessage("§6Give me something to eat, I'm hungry after sleeping!");
                                }
                                break;
                        }
                    }
                }
            }, 20L);
        }
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent e) {
        if (e.getPlayer().getWorld().getName().equals(CAPluginMain.getPlugin().getConfig().getString("CA.world.CraftAttack_World"))) {
            List<Player> players = new ArrayList<>();
            e.getPlayer().removeMetadata("BedEntered", CAPluginMain.getPlugin());
            FileConfiguration pConfig = CAPluginMain.getPlugin().getConfig();
            for (Player onlineplayer : Bukkit.getOnlinePlayers()) {
                if (onlineplayer.hasMetadata("BedEntered") && onlineplayer.getWorld().getName().equals(CAPluginMain.getPlugin().getConfig().getString("CA.world.CraftAttack_World"))) {
//					if(!onlineplayers.get(i).getScoreboardTags().contains("CamAccountScoreboardTag")) {
                    players.add(onlineplayer);
//					}
                }
            }
            if (e.getPlayer().getWorld().getTime() > 13000 && e.getPlayer().getWorld().getTime() < 22000) {
                if (!AdminPanelMain.getPlugin().chatmute.containsKey(e.getPlayer())) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getWorld().equals(e.getPlayer().getWorld()) && pConfig.getBoolean("CA.world.Sleep-System.messageEnabled"))
                            p.sendMessage("§6" + e.getPlayer().getName() + " left bed! §1[§5" + players.size() + "§1/§5" + (Bukkit.getOnlinePlayers().size() / 2) + "§1] §6Players are sleeping!\n §7");
                    }
                }
            }
        }
    }
}
