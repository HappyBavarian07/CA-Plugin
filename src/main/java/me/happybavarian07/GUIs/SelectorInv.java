package me.happybavarian07.GUIs;

import de.slikey.effectlib.effect.DnaEffect;
import de.slikey.effectlib.effect.VortexEffect;
import de.slikey.effectlib.effect.WarpEffect;
import me.happybavarian07.events.LobbyTeleportEvent;
import me.happybavarian07.main.Utils;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SelectorInv implements Listener {

	int task;
	CAPluginMain plugin;
	FileConfiguration cfg;
	public static List<Inventory> lobbyinvs = new ArrayList<>();

	public SelectorInv(CAPluginMain main, FileConfiguration config) {
		this.cfg = config;
		this.plugin = main;
	}

	public static void initializeWorldItems(Inventory inv, Player player) {
		if(inv.getContents() != null) {
			for(int i = 0; i < inv.getSize() ; i++) {
				if(inv.getItem(i) == null) {
					inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " "));
				}
			}
			inv.setItem(13, createGuiItem(Material.GRASS_BLOCK,
					Utils.format(player, "&5Craft&6Attack&11", CAPluginMain.getPrefix()), // Name
					Utils.format(player, "&7Players: &7(&b%ca_onlineplayers%&7/&b%ca_players%&7)", CAPluginMain.getPrefix()),
					Utils.format(player, "&2Hier kommst du zur CraftAttack Welt!", CAPluginMain.getPrefix()), // Lore...
					Utils.format(player, "&4Natürlich nur wenn du ein Teilnehmer bist!", CAPluginMain.getPrefix())));
			inv.setItem(22, createGuiItem(Material.BEACON,
					Utils.format(player, "&4Random Lobby", CAPluginMain.getPrefix()), // Name
					Utils.format( player, "&7Players: (&b%ca_playersinlobby%&7/&b" + Bukkit.getOnlinePlayers().size() + "&7)", CAPluginMain.getPrefix()),
					Utils.format(player, "&2Teleportieren dich zu einer Random Lobby", CAPluginMain.getPrefix()))); // Lore
		}
	}

	// Nice little method to create a gui item with a custom name, and description
	protected static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();

		// Set the name of the item
		assert meta != null;
		meta.setDisplayName(name);

		// Set the lore of the item
		meta.setLore(Arrays.asList(lore));

		item.setItemMeta(meta);

		return item;
	}

	public static void openSelectorInv(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 45, "§5§lSelector");
		initializeWorldItems(inventory, player);
		lobbyinvs.add(inventory);
		player.openInventory(inventory);
	}

	DnaEffect dan;
	VortexEffect vortex;

	// Check for clicks on items
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(final InventoryClickEvent e) {
		if(!lobbyinvs.contains(e.getInventory())) return;

		e.setCancelled(true);

		final ItemStack clickedItem = e.getCurrentItem();

		// verify current item is not null
		if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

		Player player = (Player) e.getWhoClicked();
		LobbyTeleportEvent lobbyTeleportEvent;
		if(clickedItem.getType() == Material.GRASS_BLOCK) {
			if(clickedItem.getItemMeta().getDisplayName().equals(Utils.format(null, "&5Craft&6Attack&11", CAPluginMain.getPrefix()))) {
				World world = plugin.getCraftAttackWorld();
				if (world == null)
					world = new WorldCreator(Objects.requireNonNull(plugin.spawnconfig.getString("CraftAttack.Spawn.World"))).createWorld();

				lobbyTeleportEvent = new LobbyTeleportEvent(player, player.getLocation(), world.getSpawnLocation(), "CraftAttack1", Utils.format(player, "&4Sending you to the Craft Attack World!", CAPluginMain.getPrefix()));
				Bukkit.getPluginManager().callEvent(lobbyTeleportEvent);
				if(!lobbyTeleportEvent.isCancelled()) {
					player.sendMessage(lobbyTeleportEvent.getMessage());
					playTPEffect(lobbyTeleportEvent.getPlayer(), lobbyTeleportEvent.getStartlocation(), lobbyTeleportEvent.getEndlocation());
					player.setMetadata("teleportanimationstoppedmoving", new FixedMetadataValue(plugin, true));
					player.closeInventory();
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						player.removeMetadata("teleportanimationstoppedmoving", plugin);
						player.setAllowFlight(false);
						player.setFlying(false);
					}, 2500);
				}
			}
		}
		if(clickedItem.getType() == Material.BEACON) {
			if(clickedItem.getItemMeta().getDisplayName().equals(Utils.format(player, "&4Random Lobby", CAPluginMain.getPrefix()))) {
				World randomLobby = Utils.randomLobby();
				lobbyTeleportEvent = new LobbyTeleportEvent(player, player.getLocation(), randomLobby.getSpawnLocation(), "RandomLobby", Utils.format(player, "&4Sending you to a Random Lobby!", CAPluginMain.getPrefix()));
				Bukkit.getPluginManager().callEvent(lobbyTeleportEvent);
				if(!lobbyTeleportEvent.isCancelled()) {
					player.sendMessage(lobbyTeleportEvent.getMessage());
					playTPEffect(lobbyTeleportEvent.getPlayer(), lobbyTeleportEvent.getStartlocation(), lobbyTeleportEvent.getEndlocation());
					player.setMetadata("teleportanimationstoppedmoving", new FixedMetadataValue(plugin, true));
					player.closeInventory();
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						player.removeMetadata("teleportanimationstoppedmoving", plugin);
						player.setAllowFlight(false);
						player.setFlying(false);
					}, 2500);
				}
			}
		}
	}

	@EventHandler
	public void onLobbyTP(LobbyTeleportEvent e) {
		if(e.getLocName().equals("RandomLobby")) {
			e.setMessage("&aSending you to a &1R&2a&3n&4d&5o&6m &7L&8o&9b&ab&by!");
		} else if (e.getLocName().equals("CraftAttack1")) {
			e.setMessage("&aSending you to: &5Craft&6Attack&11!");
		} else {
			e.setCancelled(true);
		}
	}

	public void playTPEffect(Player player, Location startLocation, Location endlocation) {
		dan = new DnaEffect(CAPluginMain.em);
		vortex = new VortexEffect(CAPluginMain.em);
		startLocation.setPitch(-90);
		vortex.setLocation(startLocation);
		vortex.setLocation(startLocation);
		dan.setLocation(startLocation);
		dan.start();
		vortex.start();
		Location tploc = player.getLocation();
		double hoehebisende = tploc.getY() + 12;
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if(player.getLocation().getY() > hoehebisende) {
				player.teleport(endlocation);
				player.setAllowFlight(false);
				player.setFlying(false);
				Bukkit.getScheduler().cancelTask(task);
				dan.cancel();
				WarpEffect warp = new WarpEffect(CAPluginMain.em);
				warp.setLocation(player.getLocation());
				warp.particle = Particle.CRIT;
				warp.particleCount = 100;
				warp.autoOrient = true;
				warp.start();
			} else {
				tploc.setY(tploc.getY() + 0.09d);
				player.teleport(tploc);
				player.setAllowFlight(true);
				player.setFlying(false);
			}
		},0L, 1L);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		World world = e.getPlayer().getWorld();
		if(plugin.getConfig().getStringList("Lobby-Worlds").contains(world.getName())) {
			if(dan == null) return;
			if(dan.isDone()) return;
			if(!e.getPlayer().hasMetadata("teleportanimationstoppedmoving")) return;

			e.setCancelled(true);
		}
	}

	// Cancel dragging in our inventory
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(final InventoryDragEvent e) {
		if(lobbyinvs.contains(e.getInventory())) {
			e.setCancelled(true);
		}
	}
}