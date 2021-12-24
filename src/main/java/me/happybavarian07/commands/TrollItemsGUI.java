package me.happybavarian07.commands;

import me.happybavarian07.API.FileManager;
import me.happybavarian07.main.CAPluginMain;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.EnumUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrollItemsGUI implements Listener {
	
	static CAPluginMain plugin;
	
	static int playerheadtask;
	
	FileConfiguration cfg;
	public static List<Inventory> trollinvs = new ArrayList<>();
	public static List<Inventory> playerselectorinvs = new ArrayList<>();
	
	public TrollItemsGUI(CAPluginMain main, FileConfiguration config) {
		
		this.cfg = config;
		TrollItemsGUI.plugin = main;
	}
	
	public static void initializeTrollItems(Inventory inv, Player target) {
		if(inv.getContents() != null) {
			for(int i = 0; i < inv.getSize() ; i++) {
				if(inv.getItem(i) == null) {
					inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " "));
				}
			}
			inv.setItem(13, createGuiItem(Material.LEVER, "§1R§2e§3m§4o§5t§6e §7C§8o§9n§at§br§co§el", ""));
			inv.setItem(21, createGuiItem(Material.BOW, "§4Punch Bow", "§4Das Item gibt dir einen Punch 1000 Bogen!", "§6Mit dem du " + target.getName() + " ins Jenseits schicken kannst"));
			inv.getItem(21).addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1000);
			inv.setItem(22, createGuiItem(Material.STICK, "§4Bye Bye", "Gibt dir einen Knochback 1000 Stick!", "§6Mit dem du " + target.getName() + " ins Jenseits schicken kannst"));
			inv.getItem(22).addUnsafeEnchantment(Enchantment.KNOCKBACK, 1000);
			inv.setItem(23, createGuiItem(Material.ELYTRA, "§4Elytra", "§4Deaktiviert das Elytra System f§r den den du mit diesem Item anklickst!"));
			inv.getItem(23).addUnsafeEnchantment(Enchantment.LUCK, -1);
			inv.setItem(32, createGuiItem(Material.STONE_PRESSURE_PLATE, "§4Fake §5Sound §6Plate", "Configure this plate and play the Sound", "on steping on that plate."));
		}
	}
	
	public static void initializePlayerSelector(Inventory inv) {
		if(inv.getContents() != null) {
			for(int i = 0; i < inv.getSize() ; i++) {
				if(inv.getItem(i) == null) {
					inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " "));
				}
			}
			ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
			
			if(Bukkit.getOnlinePlayers().size() > 54) return;
			
			OfflinePlayer[] players = Bukkit.getOfflinePlayers();
			
			playerheadtask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
				for(int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
					OfflinePlayer p = players[i];
					if(p.hasPlayedBefore() && p.isOnline()) {
						SkullMeta meta = (SkullMeta) playerhead.getItemMeta();
						meta.setOwner(p.getName());
						meta.setDisplayName("§4" + p.getName());
						playerhead.setItemMeta(meta);
						inv.setItem(i, playerhead);
					}
				}
			}, 0L, 1L);
		}
	}
	
	// Nice little method to create a gui item with a custom name, and description
	protected static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();
		
		// Set the name of the item
		meta.setDisplayName(name);
		
		// Set the lore of the item
		meta.setLore(Arrays.asList(lore));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	// You can open the inventory with this
	public static void openTrollInv(final HumanEntity ent, Player target) {
		Inventory trollinventory = Bukkit.createInventory(null, 45, "§c§lTr§a§lo§e§ll§d§ll §6§lG§5§lU§b§lI§r " + target.getName());
		initializeTrollItems(trollinventory, target);
		trollinvs.add(trollinventory);
		ent.openInventory(trollinventory);
	}
	
	public static void openPlayerSelector(HumanEntity ent) {
		Inventory inventory = Bukkit.createInventory(null, 54, "§4§lPlayer §5§lSelector");
		initializePlayerSelector(inventory);
		playerselectorinvs.add(inventory);
		ent.openInventory(inventory);
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		if(playerselectorinvs.contains(e.getInventory())) {
			Bukkit.getScheduler().cancelTask(playerheadtask);
		}
	}
	
	// Check for clicks on items
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e) throws InterruptedException {
		if (!trollinvs.contains(e.getInventory()) && !playerselectorinvs.contains(e.getInventory())) return;
		
		e.setCancelled(true);
		
		final ItemStack clickedItem = e.getCurrentItem();
		
		// verify current item is not null
		if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
		
		Player p = (Player) e.getWhoClicked();
		// Using slots click is a best option for your inventory click's
		if(e.getRawSlot() == 13 && clickedItem.getType() != Material.PLAYER_HEAD) {
			ItemStack item = new ItemStack(Material.LEVER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§1R§2e§3m§4o§5t§6e §7C§8o§9n§at§br§co§el");
			item.setItemMeta(meta);
			item.addUnsafeEnchantment(Enchantment.LUCK, -1);
			p.getInventory().addItem(item);
			p.closeInventory();
		}
		if(e.getRawSlot() == 23 && clickedItem.getType() != Material.PLAYER_HEAD) {
			ItemStack Elytra = new ItemStack(Material.ACACIA_BOAT);
			ItemMeta ElytraMeta = Elytra.getItemMeta();
			ElytraMeta.setDisplayName("§4§l§nDisable the Elytra!");
			Elytra.addUnsafeEnchantment(Enchantment.LUCK, -1);
			Elytra.setItemMeta(ElytraMeta);
			p.getInventory().addItem(Elytra);
			p.closeInventory();
		}
		if(e.getRawSlot() == 22 && clickedItem.getType() != Material.PLAYER_HEAD) {
			// Knockback stick
			ItemStack kstick = new ItemStack(Material.STICK);
			kstick.getItemMeta().setDisplayName("§4Knowback §5Stick");
			kstick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1000);
			kstick.addUnsafeEnchantment(Enchantment.LUCK, -1);
			kstick.setDurability((short) 5);
			List<String> lore = new ArrayList<>();
			lore.add("§4Schlage deine Gegner ins Jenseints!");
			kstick.getItemMeta().setLore(lore);
			p.getInventory().addItem(kstick);
			p.closeInventory();
		}
		if(e.getRawSlot() == 21 && clickedItem.getType() != Material.PLAYER_HEAD) {
			// Knockback Bow
			ItemStack kbow = new ItemStack(Material.BOW);
			kbow.getItemMeta().setDisplayName("§4Knockback §5Bow");
			kbow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1000);
			kbow.addUnsafeEnchantment(Enchantment.LUCK, -1);
			kbow.setDurability((short) 3);
			List<String> lore = new ArrayList<>();
			lore.add("§4Schieße deine Gegner ins Jenseints!");
			kbow.getItemMeta().setLore(lore);
			p.getInventory().addItem(kbow);
			p.getInventory().addItem(new ItemStack(Material.ARROW, 64));
			p.closeInventory();
		}
		/*if(e.getRawSlot() == 32 && clickedItem.getType() != Material.PLAYER_HEAD) {
			p.sendMessage("§4§l§nType the Sound in the Chat and hold the Item in your hand:");
			Utils.sendEmptyMessage(10, p);
			ItemStack item = new ItemStack(Material.STONE_PRESSURE_PLATE);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§4Fake §5Sound §6Plate");
			List<String> lore = new ArrayList<>();
			lore.add("Hold this Item in your Hand and type the Sound in the Chat!");
			meta.setLore(lore);
			item.setItemMeta(meta);
			p.getInventory().addItem(item);
			sendSoundMessage(p);
			p.closeInventory();
		}*/
		if(clickedItem.getType() == Material.PLAYER_HEAD) {
			Player target = Bukkit.getPlayerExact(clickedItem.getItemMeta().getDisplayName().substring(2));
			p.closeInventory();
			openTrollInv(p, target);
		}
	}

	public void sendSoundMessage(Player player) {
		TextComponent sound = new TextComponent();
		sound.setText("§r[§e§lSound§r]");
		sound.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "sound=<soundname>"));
		sound.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§4Click to set the Sound of the Plate").create()));
		player.spigot().sendMessage(sound);
		player.setMetadata("PressurePlateSound", new FixedMetadataValue(CAPluginMain.getPlugin(), true));
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onIact(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_AIR) {
			if(e.getItem().getItemMeta().getDisplayName().equals("§4§l§nDisable the Elytra!") && e.getItem().getType() == Material.ACACIA_BOAT) {
				if(e.getItem().getItemMeta().hasLore()) {
					if(e.getPlayer().hasPermission("ca.admin.troll") && !e.getPlayer().hasMetadata("TrollItemsGUI_ElytraCheck")) {
						if(!e.getPlayer().getName().equals(e.getItem().getItemMeta().getLore().get(2).substring(11))) {
							e.getPlayer().sendMessage(CAPluginMain.getPlugin().getLanguageManager().getMessage("Player.Troll.Staff.NotTheOwner", e.getPlayer()));
							for(Player p : Bukkit.getOnlinePlayers()) {
								if(p.isOp()) {
									p.sendMessage(CAPluginMain.getPlugin().getLanguageManager().getMessage("Player.Troll.Staff.Admin.TryedToUseStaff", p).replace("%stuffowner%" , e.getItem().getItemMeta().getLore().get(2).substring(11)));
								}
							}
							return;
						}
						Player target = Bukkit.getPlayer(e.getPlayer().getItemInHand().getItemMeta().getLore().get(0).replace("§4Target locked: §5", ""));
						if(target != null) {
							if(target.hasMetadata("CraftAttackPluginSpawnElytra")) {
								target.removeMetadata("CraftAttackPluginSpawnElytra", plugin);
								target.setFallDistance(0);
							} else {
								if(!target.isOnGround() || e.getPlayer().hasPermission("ca.admin.troll.elytra.onground")) {
									target.setMetadata("CraftAttackPluginSpawnElytra", new FixedMetadataValue(plugin, true));
									target.setFallDistance(0);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onIactAtEntity(PlayerInteractAtEntityEvent e) {
		if(e.getRightClicked() instanceof Player) {
			if(e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§1R§2e§3m§4o§5t§6e §7C§8o§9n§at§br§co§el") && e.getPlayer().getItemInHand().getType() == Material.LEVER) {
				if(!e.getPlayer().getItemInHand().getItemMeta().hasLore()) {
					if(e.getPlayer().hasPermission("ca.admin.troll")) {
						List<String> lore = new ArrayList<String>();
						lore.add(0, "§4Target locked: §5" + e.getRightClicked().getName());
						lore.add(1, "§6Put it into your Main Hand to activate and then Move!");
						lore.add(2, "Your name: " + e.getPlayer().getName());
						ItemMeta meta = e.getPlayer().getItemInHand().getItemMeta();
						meta.setLore(lore);
						e.getPlayer().getItemInHand().setItemMeta(meta);
						e.getPlayer().sendMessage(CAPluginMain.getPlugin().getLanguageManager().getMessage("Player.Troll.Staff.TargetLocked", e.getPlayer()).replace("%target_name%", e.getRightClicked().getName()));
						return;
					}
				}
			}
			if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§4§l§nDisable the Elytra!") && e.getPlayer().getItemInHand().getType() == Material.ACACIA_BOAT) {
				if(!e.getPlayer().getItemInHand().getItemMeta().hasLore()) {
					if(e.getPlayer().hasPermission("ca.admin.troll")) {
						e.getPlayer().setMetadata("TrollItemsGUI_ElytraCheck", new FixedMetadataValue(plugin, true));
						List<String> lore = new ArrayList<>();
						lore.add(0, "§4Target locked: §5" + e.getRightClicked().getName());
						lore.add(1, "§6Click to activate / deactivate Fly for the Player!");
						lore.add(2, "Your name: " + e.getPlayer().getName());
						ItemMeta meta = e.getPlayer().getItemInHand().getItemMeta();
						meta.setLore(lore);
						e.getPlayer().getItemInHand().setItemMeta(meta);
						e.getPlayer().sendMessage(CAPluginMain.getPlugin().getLanguageManager().getMessage("Player.Troll.Staff.TargetLocked", e.getPlayer())
								.replace("%target_name%", e.getRightClicked().getName()));
						e.getPlayer().removeMetadata("TrollItemsGUI_ElytraCheck", plugin);
					}
				}
			}
		}
	}

	//@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if(!e.getPlayer().hasPermission("ca.admin.troll")) return;
		if(e.getPlayer().getItemInHand() == null) return;
		if(!e.getPlayer().getItemInHand().hasItemMeta() && !e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§4Fake §5Sound §6Plate")) return;

		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		if(e.getMessage().startsWith("sound=") && p.hasMetadata("PressurePlateSound")) {
			e.setCancelled(true);
			if(!EnumUtils.isValidEnum(Sound.class, e.getMessage().substring(6))) {
				p.sendMessage("§4Der Sound Name §6\"" + e.getMessage().substring(6) + "\"§4 ist kein Sound!");
				p.removeMetadata("PressurePlateSound", CAPluginMain.getPlugin());
				return;
			}
			Sound sound = Sound.valueOf(e.getMessage().substring(6));
			p.removeMetadata("PressurePlateSound", CAPluginMain.getPlugin());
			lore.add("§4Sound: §6" + sound);
			lore.add("§4Player: " + p.getName());
			meta.setLore(lore);
			item.setItemMeta(meta);
			p.sendMessage("§aSound for that Pressure Plate is now §c" + sound);
		}
	}

	//@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent e) throws IOException {
		if(e.getBlock().getType() == Material.STONE_PRESSURE_PLATE) {
			if(e.getPlayer().hasPermission("ca.admin.troll") && e.getItemInHand().getItemMeta().getDisplayName().equals("§4Fake §5Sound §6Plate")) {
				if(e.getItemInHand().getItemMeta().getLore().get(0).startsWith("§4Sound: §6")) {
					if(e.getPlayer().getName().equals(e.getItemInHand().getItemMeta().getLore().get(1).substring(10))) {
						Location blockloc = e.getBlock().getLocation();
						FileManager fm = CAPluginMain.getPlugin().getFileMan();
						FileConfiguration config = fm.getConfig("", "FakePlates", "yml");
						int plateanzahl = config.getInt("FakePlates." + e.getPlayer().getName() + ".Amount");
						e.getPlayer().sendMessage("§aSuccessfully placed and registered this Fake Sound Plate!");
						if(config.getConfigurationSection("FakePlates." + e.getPlayer().getName()) != null && config.getConfigurationSection("FakePlates." + e.getPlayer().getName()).contains("Plate_" + (plateanzahl+1))) return;

						config.set("FakePlates." + e.getPlayer().getName() + ".Plate_" + plateanzahl + ".loc", blockloc);
						config.set("FakePlates." + e.getPlayer().getName() + ".Plate_" + plateanzahl + ".sound", e.getItemInHand().getItemMeta().getLore().get(0).substring(11));
						config.set("FakePlates." + e.getPlayer().getName() + ".Plate_" + plateanzahl + ".placedby", e.getPlayer().getName());
						config.set("FakePlates." + e.getPlayer().getName() + ".Amount", plateanzahl + 1);
						config.save(fm.getFile("", "FakePlates", "yml"));
					} else {
						e.setCancelled(true);
					}
				} else {
					e.setCancelled(true);
					e.getPlayer().sendMessage("§4That Pressure plate has no Sound configured!");
				}
			} else {
				if(e.getItemInHand().getItemMeta().getDisplayName().equals("§4Fake §5Sound §6Plate")) {
					e.setCancelled(true);
				}
			}
		}
	}

	//@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e) {
		if(e.getBlock().getType() == Material.STONE_PRESSURE_PLATE) {
			Location blockloc = e.getBlock().getLocation();
			FileManager fm = CAPluginMain.getPlugin().getFileMan();
			FileConfiguration config = fm.getConfig("", "FakePlates", "yml");
			if(!config.contains("FakePlates." + e.getPlayer().getName() + ".Plate_0.loc")) {
				e.getPlayer().sendMessage("§1T§2e§3s§4t");
				return;
			}

			config.getConfigurationSection("FakePlates." + e.getPlayer().getName()).getKeys(false).forEach(plate -> {
				if(plate.equals("Amount")) return;

				Location loc = config.getLocation("FakePlates." + e.getPlayer().getName() + "." + plate + ".loc");
				Sound sound = Sound.valueOf(config.getString("FakePlates." + e.getPlayer().getName() + "." + plate + ".sound"));
				OfflinePlayer placedby = Bukkit.getOfflinePlayer(config.getString("FakePlates." + e.getPlayer().getName() + "." + plate + ".placedby"));
				int amount = config.getInt("FakePlates." + e.getPlayer().getName() + ".Amount");
				if(loc.equals(blockloc)) {
					if(!e.getPlayer().hasPermission("ca.admin.troll")) return;

					e.getBlock().getDrops().clear();
					e.setDropItems(false);
					ItemStack newplate = new ItemStack(Material.STONE_PRESSURE_PLATE);
					ItemMeta meta = newplate.getItemMeta();
					List<String> lore = new ArrayList<>();
					meta.setDisplayName("§4Fake §5Sound §6Plate");
					lore.add("§4Sound: §6" + sound);
					lore.add("§4Player: " + placedby.getName());
					meta.setLore(lore);
					newplate.setItemMeta(meta);
					config.set("FakePlates." + e.getPlayer().getName() + "." + plate, null);
					config.set("FakePlates." + e.getPlayer().getName() + ".Amount", (config.getInt("FakePlates." + e.getPlayer().getName() + ".Amount")-1));
					e.getPlayer().getWorld().dropItem(blockloc, newplate);
					e.getPlayer().sendMessage("§cYou destroyed this Fake Plate");
					e.getPlayer().sendMessage(plate.toString());
					e.getPlayer().sendMessage(loc.toString());
					try {
						config.save(fm.getFile("", "FakePlates", "yml"));
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
			});
		}
	}

	//@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent e) {
		Location loc = e.getPlayer().getLocation();
		if(loc.getBlock().getType() == Material.STONE_PRESSURE_PLATE) {
			FileManager fm = CAPluginMain.getPlugin().getFileMan();
			FileConfiguration config = fm.getConfig("", "FakePlates", "yml");
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(!config.contains("FakePlates." + player.getName() + ".Plate_0.loc")) continue;

				config.getConfigurationSection("FakePlates." + player.getName()).getKeys(false).forEach(plate -> {
					if (plate.equals("Amount")) return;

					Location loc2 = config.getLocation("FakePlates." + player.getName() + "." + plate + ".loc");
					Sound sound = Sound.valueOf(config.getString("FakePlates." + player.getName() + "." + plate + ".sound"));
					OfflinePlayer placedby = Bukkit.getOfflinePlayer(config.getString("FakePlates." + player.getName() + "." + plate + ".placedby"));
					int amount = config.getInt("FakePlates." + player.getName() + ".Amount");
					if (loc.equals(loc2)) {
						e.getPlayer().playSound(loc, sound, 100, 1.0F);
					}
				});
			}
		}
	}

	// Cancel dragging in our inventory
	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if(trollinvs.contains(e.getInventory()) || playerselectorinvs.contains(e.getInventory())) {
			e.setCancelled(true);
		}
	}
}