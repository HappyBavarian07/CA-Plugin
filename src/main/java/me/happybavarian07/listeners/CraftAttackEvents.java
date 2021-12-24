package me.happybavarian07.listeners;

import me.happybavarian07.main.Prefix;
import me.happybavarian07.main.Utils;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;

public class CraftAttackEvents implements Listener {

    static CAPluginMain plugin;

    public CraftAttackEvents(CAPluginMain plugin) {
        CraftAttackEvents.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String joinmessagewithoutplaceholders = plugin.getLanguageManager().getMessage("Player.Join", player);

        String joinmessagewithplaceholders = Utils.format(e.getPlayer(), joinmessagewithoutplaceholders, CAPluginMain.getPrefix());

        e.setJoinMessage(joinmessagewithplaceholders);
        if (player.getWorld().getName().equals(plugin.getConfig().getString("CA.world.CraftAttack_World"))) {
            if (plugin.isWorldChangeCheck()) {
                if (plugin.getConfig().getStringList("CA.world.Cam_Players").contains(player.getName())) {
                    if(!player.hasPlayedBefore()) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.setMetadata("CamAccountScoreboardTag", new FixedMetadataValue(plugin, true));
                        World world = Bukkit.getWorld(plugin.spawnconfig.getString("CraftAttack.Spawn.World"));
                        if(world == null) { player.sendMessage("§cNo CraftAttack World found! §4Please contact an Admin!"); return; }
                        double x = plugin.spawnconfig.getDouble("CraftAttack.Spawn.X");
                        double y = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Y");
                        double Z = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Z");
                        float yaw = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Yaw");
                        float pitch = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Pitch");
                        Location spawnloc = new Location(world, x, y, Z, yaw, pitch);
                        player.teleport(spawnloc);
                        Utils.setPlayerPrefix(player, Prefix.CAM);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt als Cam angemeldet!");
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.setMetadata("CamAccountScoreboardTag", new FixedMetadataValue(plugin, true));
                        Utils.setPlayerPrefix(player, Prefix.CAM);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt als Cam angemeldet!");
                    }
                } else if (plugin.getConfig().getStringList("CA.world.CraftAttack_Players").contains(player.getName())) {
                    if(!player.hasPlayedBefore()) {
                        player.removeMetadata("CamAccountScoreboardTag", plugin);
                        World world = Bukkit.getWorld(plugin.spawnconfig.getString("CraftAttack.Spawn.World"));
                        if(world == null) { player.sendMessage("§cNo CraftAttack World found! §4Please contact an Admin!"); return; }
                        double x = plugin.spawnconfig.getDouble("CraftAttack.Spawn.X");
                        double y = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Y");
                        double Z = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Z");
                        float yaw = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Yaw");
                        float pitch = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Pitch");
                        Location spawnloc = new Location(world, x, y, Z, yaw, pitch);
                        player.teleport(spawnloc);
                        Utils.setPlayerPrefix(player, Utils.getPrefixFromConfig(player));
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt angemeldet!");
                    } else {
                        player.removeMetadata("CamAccountScoreboardTag", plugin);
                        Utils.setPlayerPrefix(player, Utils.getPrefixFromConfig(player));
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt angemeldet!");
                    }
                } else {
                    player.teleport(Utils.randomLobby().getSpawnLocation());

                    player.kickPlayer("§cDu wurdest gekickt da du kein offizieler Teilnehmer von Craft Attack bist!\n" + "\n"
                            + "§cFalls du glaubst das ist ein Fehler dann melde dich beim Craft Attack Team!\n" + "\n"
                            + "§6Falls du dich f§r das n§chste CraftAttack registrieren willst dann gehe auf unsere Website: §4craftattackwebsite.test");
                }
            } else {
                if (player.hasPermission("ca.admin.worldcheckerisdis")) {
                    player.sendMessage("§cWorld Change Checker is currently disabled!");
                }
            }
        } else {
            player.removeMetadata("CamAccountScoreboardTag", plugin);
            Utils.loadTablist(player, false);
            player.teleport(Utils.randomLobby().getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnWorldchange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getName().equals(plugin.getConfig().getString("CA.world.CraftAttack_World"))) {
            if (plugin.isWorldChangeCheck()) {
                if (plugin.getConfig().getStringList("CA.world.Cam_Players").contains(player.getName())) {
                    if(!player.hasPlayedBefore()) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.setMetadata("CamAccountScoreboardTag", new FixedMetadataValue(plugin, true));
                        World world = Bukkit.getWorld(plugin.spawnconfig.getString("CraftAttack.Spawn.World"));
                        if(world == null) { player.sendMessage("§cNo CraftAttack World found! §4Please contact an Admin!"); return; }
                        double x = plugin.spawnconfig.getDouble("CraftAttack.Spawn.X");
                        double y = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Y");
                        double Z = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Z");
                        float yaw = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Yaw");
                        float pitch = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Pitch");
                        Location spawnloc = new Location(world, x, y, Z, yaw, pitch);
                        player.teleport(spawnloc);
                        Utils.setPlayerPrefix(player, Prefix.CAM);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt als Cam angemeldet!");
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.setMetadata("CamAccountScoreboardTag", new FixedMetadataValue(plugin, true));
                        Utils.setPlayerPrefix(player, Prefix.CAM);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt als Cam angemeldet!");
                    }

                } else if (plugin.getConfig().getStringList("CA.world.CraftAttack_Players").contains(player.getName())) {
                    if(!player.hasPlayedBefore()) {
                        player.removeMetadata("CamAccountScoreboardTag", plugin);
                        World world = Bukkit.getWorld(plugin.spawnconfig.getString("CraftAttack.Spawn.World"));
                        if(world == null) { player.sendMessage("§cNo CraftAttack World found! §4Please contact an Admin!"); return; }
                        double x = plugin.spawnconfig.getDouble("CraftAttack.Spawn.X");
                        double y = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Y");
                        double Z = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Z");
                        float yaw = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Yaw");
                        float pitch = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Pitch");
                        Location spawnloc = new Location(world, x, y, Z, yaw, pitch);
                        player.teleport(spawnloc);
                        Utils.setPlayerPrefix(player, Utils.getPrefixFromConfig(player));
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt angemeldet!");
                    } else {
                        player.removeMetadata("CamAccountScoreboardTag", plugin);
                        Utils.setPlayerPrefix(player, Utils.getPrefixFromConfig(player));
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt angemeldet!");
                    }
                } else {
                    player.teleport(Utils.randomLobby().getSpawnLocation());

                    player.kickPlayer("§cDu wurdest gekickt da du kein offizieler Teilnehmer von Craft Attack bist!\n" + "\n"
                            + "§cFalls du glaubst das ist ein Fehler dann melde dich beim Craft Attack Team!\n" + "\n"
                            + "§6Falls du dich f§r das n§chste CraftAttack registrieren willst dann gehe auf unsere Website: §4craftattackwebsite.test");
                }
            } else {
                if (player.hasPermission("ca.admin.*") || player.hasPermission("ca.admin.worldcheckerisdisabled") || player.hasPermission("ca.*")) {
                    player.sendMessage("§cWorld Change Checker is currently disabled!");
                }
            }
        } else {
            player.setGameMode(GameMode.SURVIVAL);
            Utils.loadTablist(player, false);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String quitmessage = CAPluginMain.getPlugin().getLanguageManager().getMessage("Player.Quit", p);
        e.setQuitMessage(quitmessage);
        p.setPlayerListHeader("");
        p.setPlayerListFooter("");
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.showPlayer(p);
        }
        p.setInvulnerable(false);
        p.setCollidable(true);
        p.removeMetadata("CraftAttackPluginSpawnElytra", plugin);
        p.setGameMode(GameMode.SURVIVAL);
    }
}
