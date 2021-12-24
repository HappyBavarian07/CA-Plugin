package me.happybavarian07.listeners;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.DnaEffect;
import de.slikey.effectlib.effect.HelixEffect;
import me.happybavarian07.GUIs.SelectorInv;
import me.happybavarian07.main.Utils;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class LobbyEventListener implements Listener, CommandExecutor {
    /*
    Listenes for all lobby Events: Break, Place, ...
     */

    public HelixEffect effect;
    CAPluginMain plugin;
    FileConfiguration config;

    public LobbyEventListener(CAPluginMain plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        World world = p.getWorld();
        if(config.getStringList("Lobby-Worlds").contains(world.getName())) {
            if(!isBuilding(p.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        World world = e.getEntity().getWorld();
        if(config.getStringList("Lobby-Worlds").contains(world.getName())) {
            e.setCancelled(true);
            e.getEntity().setHealth(e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            e.setFoodLevel(20);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        World world = p.getWorld();
        if(config.getStringList("Lobby-Worlds").contains(world.getName())) {
            if(!isBuilding(p.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        World world = e.getEntity().getWorld();
        if(config.getStringList("Lobby-Worlds").contains(world.getName())) {
            e.setCancelled(true);
            if(e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {

            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        World world = e.getPlayer().getWorld();
        if(config.getStringList("Lobby-Worlds").contains(world.getName())) {
            e.setMessage(Utils.format(e.getPlayer(), e.getMessage(), CAPluginMain.getPrefix()));
            e.setFormat(Utils.getPrefixFromConfig(e.getPlayer()).getInGamePrefix() + e.getPlayer().getName() + Utils.getPrefixFromConfig(e.getPlayer()).getInGameSuffix() +
                    CAPluginMain.getPlugin().getLanguageManager().getMessage("Chat.Splitter", null) + e.getMessage());
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        World world = e.getPlayer().getWorld();
        if(config.getStringList("Lobby-Worlds").contains(world.getName())) {
            e.getPlayer().setHealth(e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            e.getPlayer().setFoodLevel(20);
            if(isBuilding(e.getPlayer().getUniqueId())) {
                removeBuilder(e.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        World world = e.getPlayer().getWorld();
        if(config.getStringList("Lobby-Worlds").contains(world.getName())) {
            if(!isBuilding(e.getPlayer().getUniqueId())) {
                if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.ENDER_CHEST) {
                    EffectManager em = new EffectManager(plugin);
                    effect = new HelixEffect(em);
                    effect.particle = Particle.DRAGON_BREATH;
                    effect.type = EffectType.REPEATING;
                    effect.radius = 10.5f;
                    Location loc = (e.getPlayer().getLocation().add(0, 15, 0));
                    effect.setLocation(loc);
                    e.setCancelled(true);
                    effect.start();
                    effect.duration = 30*1000;
                    SelectorInv.openSelectorInv(e.getPlayer());
                    return;
                }
                if(e.getClickedBlock() != null) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("build")) {
            if(sender instanceof Player) {
                Player p = (Player) sender;
                if(!p.hasPermission("ca.admin.lobby.build")) {
                    p.sendMessage(CAPluginMain.getPlugin().getLanguageManager().getMessage("Player.NoPermissions", p));
                    return true;
                }
                if(!isBuilding(p.getUniqueId())) {
                    addBuilder(p.getUniqueId());
                    p.sendMessage(CAPluginMain.getPlugin().getLanguageManager().getMessage("Player.Lobby.CanBuildNow", p));
                } else {
                    removeBuilder(p.getUniqueId());
                    p.sendMessage(CAPluginMain.getPlugin().getLanguageManager().getMessage("Player.Lobby.CanNoLongerBuild", p));
                }
            }
        }
        return true;
    }

    public void addBuilder(UUID builder) {
        if(isBuilding(builder)) return;

        getPlayerFromUUID(builder).setMetadata("builder", new FixedMetadataValue(CAPluginMain.getPlugin(), true));
    }
    public void removeBuilder(UUID builder) {
        if(!isBuilding(builder)) return;

        getPlayerFromUUID(builder).removeMetadata("builder", CAPluginMain.getPlugin());
    }
    public boolean isBuilding(UUID builder) {
        return getPlayerFromUUID(builder).hasMetadata("builder");
    }

    private Player getPlayerFromUUID(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }
}
