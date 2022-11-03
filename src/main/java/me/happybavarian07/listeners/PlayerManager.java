package me.happybavarian07.listeners;

import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.main.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class PlayerManager implements Listener {

    CAPluginMain plugin;

    public PlayerManager(CAPluginMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.getEntity().removeMetadata("CraftAttackPluginSpawnElytra", plugin);
        if (plugin.getConfig().getBoolean("CA.settings.Heads.Killing") || !plugin.getConfig().getBoolean("CA.settings.Heads.Command")) {
            if (e.getEntity().getKiller() == null || !e.getEntity().getLastDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                return;
            }
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwner(e.getEntity().getName());
            meta.setDisplayName("§e" + e.getEntity().getName() + "'s Head");
            head.setItemMeta(meta);
            e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), head);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        if (plugin.getConfig().getBoolean("CA.settings.Heads.Killing") || !plugin.getConfig().getBoolean("CA.settings.Heads.Command")) {
            if (e.getEntity().getKiller() == null || !e.getEntity().getLastDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                return;
            }
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwner(e.getEntity().getName());
            meta.setDisplayName("§e" + e.getEntity().getName() + "'s Head");
            head.setItemMeta(meta);
            Random r = new Random();
            if (r.nextInt(3) == 1) {
                e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), head);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (e.getPlayer().getBedSpawnLocation() == null) {
            World world = Bukkit.getWorld(plugin.spawnconfig.getString("CraftAttack.Spawn.World"));
            if (world == null) {
                e.getPlayer().sendMessage("§cNo CraftAttack World found! §4Please contact an Admin!");
                return;
            }
            double x = plugin.spawnconfig.getDouble("CraftAttack.Spawn.X");
            double y = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Y");
            double Z = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Z");
            float yaw = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Yaw");
            float pitch = (float) plugin.spawnconfig.getDouble("CraftAttack.Spawn.Pitch");
            Location spawnloc = new Location(world, x, y, Z, yaw, pitch);
            e.getPlayer().teleport(spawnloc);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        e.getPlayer().removeMetadata("CraftAttackPluginSpawnElytra", plugin);
        // Random lobby World
        if(plugin.isLobbySystemEnabled()) {
            e.getPlayer().teleport(Utils.randomLobby().getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoinForRegister(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        File userdatafile = new File(plugin.getDataFolder() + "/userdata/" + player.getUniqueId() + ".yml");
        if (!player.hasPlayedBefore()) {
            try {
                if (!userdatafile.getParentFile().exists())
                    userdatafile.getParentFile().mkdir();

                userdatafile.createNewFile();

                FileConfiguration config = YamlConfiguration.loadConfiguration(userdatafile);
                config.set("lastAccountName", player.getName());
                config.set("ipAdress", player.getAddress().toString());
                config.set("lastLoginLocation", player.getLocation());
                config.set("bedSpawnLocation", player.getBedSpawnLocation());
                config.save(userdatafile);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(userdatafile);
            config.set("lastAccountName", player.getName());
            config.set("ipAdress", player.getAddress().toString());
            config.set("lastLoginLocation", player.getLocation());
            config.set("bedSpawnLocation", player.getBedSpawnLocation());
            try {
                config.save(userdatafile);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
