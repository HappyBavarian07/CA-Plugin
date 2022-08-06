package me.happybavarian07.listeners;/*
 * @Author HappyBavarian07
 * @Date 06.08.2022 | 11:39
 */

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.main.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.UnknownFormatConversionException;

public class MoreCraftAttackEvents implements Listener {
    private final CAPluginMain plugin;

    public MoreCraftAttackEvents(CAPluginMain plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String MessageSplitter = plugin.getLanguageManager().getMessage("Chat.Splitter", player);
        String PlayerPrefix = Utils.getPrefixFromConfig(player).getInGamePrefix();
        String PlayerSuffix = Utils.getPrefixFromConfig(player).getInGameSuffix();
        try {
            if (player.getWorld().getName().equals(plugin.getConfig().getString("CA.world.CraftAttack_World"))) {
                e.setMessage(Utils.formatChatMessage(e.getPlayer(), e.getMessage(),
                        player.hasPermission("ca.chat.color"),
                        player.hasPermission("ca.chat.placeholders")));
                e.setFormat(PlayerPrefix + player.getDisplayName() + PlayerSuffix + MessageSplitter + e.getMessage());
            }
        } catch (UnknownFormatConversionException ignored) {
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isOp()) {
            plugin.addStaff(e.getPlayer().getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (plugin.isStaff(e.getPlayer().getName())) {
            plugin.removeStaff(e.getPlayer().getName());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        if (e.getPlayer().getWorld().getName().equals(plugin.getConfig().getString("CA.world.CraftAttack_World"))) {
            if (e.getPlayer().hasMetadata("SpawnTeleportAnfrage")) {
                if (Bukkit.getScheduler().getPendingTasks().contains(plugin.spawnvorgang)) {
                    Bukkit.getScheduler().cancelTask(plugin.spawnvorgang.getTaskId());
                }
                e.getPlayer().sendMessage("§cSpawn Teleport abgebrochen, da du dich bewegt hast!");
                e.getPlayer().removeMetadata("SpawnTeleportAnfrage", plugin);
            }
            e.getPlayer().getItemInHand();
            if (e.getPlayer().getItemInHand().hasItemMeta() && Objects.requireNonNull(e.getPlayer().getItemInHand().getItemMeta()).getDisplayName().equals("§1R§2e§3m§4o§5t§6e §7C§8o§9n§at§br§co§el") && e.getPlayer().getItemInHand().getType() == Material.LEVER) {
                if (e.getPlayer().hasPermission("ca.admin.troll")) {
                    if (Objects.requireNonNull(e.getPlayer().getItemInHand().getItemMeta().getLore()).isEmpty()) {
                        return;
                    }
                    Player target = Bukkit.getPlayer(Objects.requireNonNull(Objects.requireNonNull(e.getPlayer().getItemInHand().getItemMeta()).getLore()).get(0).replace("§4Target locked: §5", ""));
                    assert target != null;
                    if (!plugin.isinSpawn(target.getLocation(), e.getPlayer().getLocation(), 5)) {
                        e.getPlayer().sendMessage("§aDein Target ist nicht in deiner Reichweite!");
                        return;
                    }
                    PacketContainer packet1 = plugin.pman.createPacket(PacketType.Play.Server.POSITION);
                    packet1.getIntegers().write(0, target.getEntityId());
                    packet1.getDoubles().write(0, e.getPlayer().getLocation().getX());
                    packet1.getDoubles().write(1, e.getPlayer().getLocation().getY());
                    packet1.getDoubles().write(2, e.getPlayer().getLocation().getZ() + 1);
                    packet1.getFloat().write(0, e.getPlayer().getLocation().getYaw());
                    packet1.getFloat().write(1, e.getPlayer().getLocation().getPitch());
                    packet1.getModifier().writeDefaults();
                    packet1.getCombatEvents().writeDefaults();
                    packet1.getHands().writeDefaults();

                    try {
                        plugin.pman.sendServerPacket(target, packet1);
                    } catch (InvocationTargetException e1) {
                        throw new RuntimeException("Cannot send packet " + packet1, e1);
                    }
                    //e.setCancelled(true);
                    return;
                }
            }
            Location blockloc = e.getPlayer().getLocation().add(0, -1, 0);
            Location spawnmiddleloc;
            if (plugin.spawnconfig == null || plugin.spawnconfig.getString("CraftAttack.Spawn.World") == null) {
                return;
            } else {
                World world = Bukkit.getWorld(plugin.spawnconfig.getString("CraftAttack.Spawn.World"));
                if (world == null) return;
                double x = plugin.spawnconfig.getDouble("CraftAttack.Spawn.X");
                double y = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Y");
                double Z = plugin.spawnconfig.getDouble("CraftAttack.Spawn.Z");
                spawnmiddleloc = new Location(world, x, y, Z);
            }
            if (plugin.isinSpawn((blockloc.add(0, 1, 0)), spawnmiddleloc, plugin.getConfig().getDouble("CA.settings.Spawn.Radius")) && blockloc.add(0, -1, 0).getBlock().getType().equals(Material.GOLD_BLOCK)) {
                if ((e.getPlayer().getGameMode() == GameMode.SPECTATOR) || (e.getPlayer().getGameMode() == GameMode.CREATIVE)) {
                    return;
                }
                e.getPlayer().setMetadata("CraftAttackPluginSpawnElytra", new FixedMetadataValue(plugin, true));
                e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().setY(1).multiply(1));
                plugin.repeattaskmoveevent = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                    int time = plugin.getConfig().getInt("CA.settings.Flytime");

                    @Override
                    public void run() {
                        if (time >= 0 && !e.getPlayer().isOnGround() && e.getPlayer().hasMetadata("CraftAttackPluginSpawnElytra")) {
                            if (time < 60) {
                                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§4§lFlytime: " + (time) + " Seconds left"));
                            } else {
                                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lFlytime: " + (time / 60) + " Minutes left"));
                            }
                            time--;
                        } else {
                            e.getPlayer().removeMetadata("CraftAttackPluginSpawnElytra", plugin);
                            Bukkit.getScheduler().cancelTask(plugin.repeattaskmoveevent);
                            time = plugin.getConfig().getInt("CA.settings.Flytime");
                        }
                    }
                }, 20L, 20L);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onIact(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (e.getPlayer().getItemInHand().getType() == Material.FIREWORK_ROCKET) {
                if (e.getPlayer().hasMetadata("CraftAttackPluginSpawnElytra")) {
                    e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(2));
                    e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 100, (float) 1.0);
                    int i = 0;
                    while (!Objects.requireNonNull(e.getPlayer().getInventory().getItem(i)).isSimilar(e.getItem())) {
                        i++;
                    }
                    Objects.requireNonNull(e.getPlayer().getInventory().getItem(i)).setAmount(Objects.requireNonNull(e.getPlayer().getInventory().getItem(i)).getAmount() - 1);
                }
            }
        }
    }
}
