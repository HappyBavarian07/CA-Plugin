package me.happybavarian07.listeners;

import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.main.Utils;
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

import java.util.UUID;

public class CraftAttackEvents implements Listener {

    private CAPluginMain plugin;

    public CraftAttackEvents(CAPluginMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String joinmessagewithoutplaceholders = plugin.getLanguageManager().getMessage("Player.Join", player, false);
        boolean isDev = player.getUniqueId().equals(UUID.fromString("0c069d0e-5778-4d51-8929-6b2f69b475c0"));
        String joinmessagewithplaceholders = Utils.format(e.getPlayer(), (isDev ? plugin.getPrefix("CAPluginDev").getInGamePrefix() + player.getName() + "§a ist dem Spiel beigetreten!" : joinmessagewithoutplaceholders), CAPluginMain.getPrefix());

        e.setJoinMessage(joinmessagewithplaceholders);
        if (player.getWorld().getName().equals(plugin.getConfig().getString("CA.world.CraftAttack_World"))) {
            if (plugin.isWorldChangeCheck()) {
                if (plugin.getConfig().getStringList("CA.world.Cam_Players").contains(player.getName())) {
                    if (!player.hasPlayedBefore()) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.setMetadata("CamAccountScoreboardTag", new FixedMetadataValue(plugin, true));
                        World world = Bukkit.getWorld(plugin.spawnconfig.getString("CraftAttack.Spawn.World"));
                        if (world == null) {
                            player.sendMessage("§cNo CraftAttack World found! §4Please contact an Admin!");
                            return;
                        }
                        double x = plugin.spawnconfig.getDouble("CraftAttack.Spawn.X");
                        double y = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Y");
                        double Z = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Z");
                        float yaw = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Yaw");
                        float pitch = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Pitch");
                        Location spawnloc = new Location(world, x, y, Z, yaw, pitch);
                        player.teleport(spawnloc);
                        Utils.setPlayerPrefix(player, plugin.getPrefix("Cam"), true);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt als Cam angemeldet!");
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.setMetadata("CamAccountScoreboardTag", new FixedMetadataValue(plugin, true));
                        Utils.setPlayerPrefix(player, plugin.getPrefix("Cam"), true);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt als Cam angemeldet!");
                    }
                } else if (plugin.getConfig().getStringList("CA.world.CraftAttack_Players").contains(player.getName())) {
                    if (!player.hasPlayedBefore()) {
                        player.removeMetadata("CamAccountScoreboardTag", plugin);
                        World world = Bukkit.getWorld(plugin.spawnconfig.getString("CraftAttack.Spawn.World"));
                        if (world == null) {
                            player.sendMessage("§cNo CraftAttack World found! §4Please contact an Admin!");
                            return;
                        }
                        double x = plugin.spawnconfig.getDouble("CraftAttack.Spawn.X");
                        double y = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Y");
                        double Z = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Z");
                        float yaw = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Yaw");
                        float pitch = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Pitch");
                        Location spawnloc = new Location(world, x, y, Z, yaw, pitch);
                        player.teleport(spawnloc);
                        Utils.setPlayerPrefix(player, Utils.getPrefixFromConfig(player), true);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt angemeldet!");
                    } else {
                        /*if(!player.getWorld().getName().equals(plugin.getConfig().getString("CA.world.CraftAttack_World"))) {

                        }*/
                        player.removeMetadata("CamAccountScoreboardTag", plugin);
                        Utils.setPlayerPrefix(player, Utils.getPrefixFromConfig(player), true);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt angemeldet!");
                    }
                } else {
                    if (plugin.isLobbySystemEnabled()) {
                        player.teleport(Utils.randomLobby().getSpawnLocation());
                    }

                    player.kickPlayer("§cDu wurdest gekickt da du kein offizieler Teilnehmer von Craft Attack bist!\n" + "\n"
                            + "§cFalls du glaubst das ist ein Fehler dann melde dich beim Craft Attack Team!\n" + "\n"
                            + "§cFalls du dich für das nächste CraftAttack registrieren willst dann sage dem Team bescheid!");
                }
            } else {
                if (player.hasPermission("ca.admin.worldcheckerisdis")) {
                    player.sendMessage("§cWorld Change Checker is currently disabled!");
                }
            }
        } else {
            player.removeMetadata("CamAccountScoreboardTag", plugin);
            if (plugin.isLobbySystemEnabled()) {
                Utils.loadTablistForPlayer(player, false);
                player.teleport(Utils.randomLobby().getSpawnLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnWorldchange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getName().equals(plugin.getConfig().getString("CA.world.CraftAttack_World"))) {
            if (plugin.isWorldChangeCheck()) {
                if (plugin.getConfig().getStringList("CA.world.Cam_Players").contains(player.getName())) {
                    if (!player.hasPlayedBefore()) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.setMetadata("CamAccountScoreboardTag", new FixedMetadataValue(plugin, true));
                        World world = Bukkit.getWorld(plugin.spawnconfig.getString("CraftAttack.Spawn.World"));
                        if (world == null) {
                            player.sendMessage("§cNo CraftAttack World found! §4Please contact an Admin!");
                            return;
                        }
                        double x = plugin.spawnconfig.getDouble("CraftAttack.Spawn.X");
                        double y = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Y");
                        double Z = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Z");
                        float yaw = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Yaw");
                        float pitch = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Pitch");
                        Location spawnloc = new Location(world, x, y, Z, yaw, pitch);
                        player.teleport(spawnloc);
                        Utils.setPlayerPrefix(player, plugin.getPrefix("Cam"), true);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt als Cam angemeldet!");
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.setMetadata("CamAccountScoreboardTag", new FixedMetadataValue(plugin, true));
                        Utils.setPlayerPrefix(player, plugin.getPrefix("Cam"), true);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt als Cam angemeldet!");
                    }

                } else if (plugin.getConfig().getStringList("CA.world.CraftAttack_Players").contains(player.getName())) {
                    if (!player.hasPlayedBefore()) {
                        player.removeMetadata("CamAccountScoreboardTag", plugin);
                        World world = Bukkit.getWorld(plugin.spawnconfig.getString("CraftAttack.Spawn.World"));
                        if (world == null) {
                            player.sendMessage("§cNo CraftAttack World found! §4Please contact an Admin!");
                            return;
                        }
                        double x = plugin.spawnconfig.getDouble("CraftAttack.Spawn.X");
                        double y = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Y");
                        double Z = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Z");
                        float yaw = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Yaw");
                        float pitch = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Pitch");
                        Location spawnloc = new Location(world, x, y, Z, yaw, pitch);
                        player.teleport(spawnloc);
                        Utils.setPlayerPrefix(player, Utils.getPrefixFromConfig(player), true);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt angemeldet!");
                    } else {
                        player.removeMetadata("CamAccountScoreboardTag", plugin);
                        Utils.setPlayerPrefix(player, Utils.getPrefixFromConfig(player), true);
                        player.sendMessage(CAPluginMain.getPrefix() + " §2Du bist jetzt in der §5Craft§6Attack§11 §2Welt angemeldet!");
                    }
                } else {
                    if (plugin.isLobbySystemEnabled()) {
                        player.teleport(Utils.randomLobby().getSpawnLocation());
                    }

                    player.kickPlayer("§cDu wurdest gekickt da du kein offizieler Teilnehmer von Craft Attack bist!\n" + "\n"
                            + "§cFalls du glaubst das ist ein Fehler dann melde dich beim Craft Attack Team!\n" + "\n"
                            + "§cFalls du dich für das nächste CraftAttack registrieren willst dann sage dem Team bescheid!");
                }
            } else {
                if (player.hasPermission("ca.admin.*") || player.hasPermission("ca.admin.worldcheckerisdisabled") || player.hasPermission("ca.*")) {
                    player.sendMessage("§cWorld Change Checker is currently disabled!");
                }
            }
        } else {
            player.setGameMode(GameMode.SURVIVAL);
            if(plugin.isLobbySystemEnabled()) {
                Utils.loadTablistForPlayer(player, false);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        boolean isDev = player.getUniqueId().equals(UUID.fromString("0c069d0e-5778-4d51-8929-6b2f69b475c0"));
        String quitmessage = CAPluginMain.getPlugin().getLanguageManager().getMessage("Player.Quit", player, false);
        e.setQuitMessage((isDev ? plugin.getPrefix("CAPluginDev").getInGamePrefix() + player.getName() + "§a hat das Spiel verlassen!" : quitmessage));
        //System.out.println("Message: " + (isDev ? plugin.getPrefix("CAPluginDev").getInGamePrefix() + player.getName() + "§a hat das Spiel verlassen!" : quitmessage));
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(player);
        }
        player.setInvulnerable(false);
        player.setCollidable(true);
        player.removeMetadata("CraftAttackPluginSpawnElytra", plugin);
        player.setGameMode(GameMode.SURVIVAL);
    }
}
