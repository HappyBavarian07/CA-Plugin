package me.happybavarian07.listeners;/*
 * @Author HappyBavarian07
 * @Date 06.08.2022 | 11:39
 */

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UnknownFormatConversionException;

public class MoreCraftAttackEvents implements Listener {
    private final CAPluginMain plugin;
    private final Map<Player, BukkitTask> elytraTaskList;

    public MoreCraftAttackEvents(CAPluginMain plugin) {
        this.plugin = plugin;
        elytraTaskList = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String MessageSplitter = plugin.getLanguageManager().getMessage("Chat.Splitter", player, false);
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
        Player player = e.getPlayer();
        if (player.getWorld().getName().equals(plugin.getConfig().getString("CA.world.CraftAttack_World"))) {
            if (player.hasMetadata("SpawnTeleportAnfrage")) {
                if (Bukkit.getScheduler().getPendingTasks().contains(plugin.spawnvorgang.get(player))) {
                    plugin.spawnvorgang.get(player).cancel();
                }
                player.sendMessage("§cSpawn Teleport abgebrochen, da du dich bewegt hast!");
                player.removeMetadata("SpawnTeleportAnfrage", plugin);
            }
            player.getItemInHand();
            /*if (player.getItemInHand().hasItemMeta() && Objects.requireNonNull(player.getItemInHand().getItemMeta()).getDisplayName().equals("§1R§2e§3m§4o§5t§6e §7C§8o§9n§at§br§co§el") && player.getItemInHand().getType() == Material.LEVER) {
                if (player.hasPermission("ca.admin.troll")) {
                    if (Objects.requireNonNull(player.getItemInHand().getItemMeta().getLore()).isEmpty()) {
                        return;
                    }
                    Player target = Bukkit.getPlayer(Objects.requireNonNull(Objects.requireNonNull(player.getItemInHand().getItemMeta()).getLore()).get(0).replace("§4Target locked: §5", ""));
                    assert target != null;
                    if (!plugin.isinSpawn(target.getLocation(), player.getLocation(), 5)) {
                        player.sendMessage("§aDein Target ist nicht in deiner Reichweite!");
                        return;
                    }
                    PacketContainer packet1 = plugin.pman.createPacket(PacketType.Play.Server.POSITION);
                    packet1.getIntegers().write(0, target.getEntityId());
                    packet1.getDoubles().write(0, player.getLocation().getX());
                    packet1.getDoubles().write(1, player.getLocation().getY());
                    packet1.getDoubles().write(2, player.getLocation().getZ() + 1);
                    packet1.getFloat().write(0, player.getLocation().getYaw());
                    packet1.getFloat().write(1, player.getLocation().getPitch());
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
            }*/
            Location blockloc = player.getLocation().add(0, -1, 0);
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
            if (plugin.isinSpawn((blockloc.add(0, 1, 0)), spawnmiddleloc, plugin.getConfig().getDouble("CA.settings.Spawn.Radius")) &&
                    blockloc.add(0, -1, 0).getBlock().getType().equals(Material.GOLD_BLOCK)) {
                if ((player.getGameMode() == GameMode.SPECTATOR) || (player.getGameMode() == GameMode.CREATIVE)) {
                    return;
                }
                player.setMetadata("CraftAttackPluginSpawnElytra", new FixedMetadataValue(plugin, true));
                player.setVelocity(player.getLocation().getDirection().setY(1).multiply(1));
                BukkitTask task = new BukkitRunnable() {
                    private final Player target = player;
                    int minutes = plugin.getConfig().getInt("CA.settings.Flytime")-1;
                    int seconds = 59;

                    @Override
                    public void run() {
                        if (target.isOnGround() || !target.hasMetadata("CraftAttackPluginSpawnElytra")) {
                            target.removeMetadata("CraftAttackPluginSpawnElytra", plugin);
                            cancel();
                            minutes = plugin.getConfig().getInt("CA.settings.Flytime");
                            return;
                        }
                        this.target.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent(plugin.getLanguageManager().getMessage("Player.FlytimeMessage", target, true)
                                        .replace("%minutes%", String.valueOf(minutes)).replace("%seconds%", String.valueOf(seconds))));
                        if (seconds == 0) {
                            if (minutes == 0) {
                                target.removeMetadata("CraftAttackPluginSpawnElytra", plugin);
                                cancel();
                                return;
                            }
                            minutes--;
                            seconds = 60;
                        } else {
                            seconds--;
                        }
                    }
                }.runTaskTimer(plugin, 20L, 20L);
                elytraTaskList.put(player, task);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onIact(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        if (!(player.getItemInHand().getType() == Material.FIREWORK_ROCKET)) return;
        if (!player.hasMetadata("CraftAttackPluginSpawnElytra")) return;
        player.setVelocity(player.getLocation().getDirection().multiply(2));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 100, (float) 1.0);
        for (ItemStack item : e.getPlayer().getInventory()) {
            if (item == null) continue;
            if (!(item.getType() == Material.FIREWORK_ROCKET)) continue;
            item.setAmount(item.getAmount() - 1);
            return;
        }
    }
}
