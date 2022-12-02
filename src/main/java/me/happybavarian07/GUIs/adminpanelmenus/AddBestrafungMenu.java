package me.happybavarian07.GUIs.adminpanelmenus;/*
 * @Author HappyBavarian07
 * @Date 13.11.2022 | 18:34
 */

import de.happybavarian07.adminpanel.main.AdminPanelMain;
import de.happybavarian07.adminpanel.menusystem.Menu;
import de.happybavarian07.adminpanel.menusystem.PlayerMenuUtility;
import de.happybavarian07.adminpanel.utils.Utils;
import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.main.Bestrafung;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AddBestrafungMenu extends Menu implements Listener {
    private final UUID targetUUID;
    private int years = 0;
    private int months = 0;
    private int hours = 0;
    private int days = 0;
    private int minutes = 0;
    private PotionEffectType potionEffectType = PotionEffectType.SLOW;
    private int amplifier = 0;

    public AddBestrafungMenu(PlayerMenuUtility playerMenuUtility, UUID targetUUID) {
        super(playerMenuUtility);
        this.targetUUID = targetUUID;
        setOpeningPermission("ca.admin.bestrafungen.menu");
    }

    @Override
    public String getMenuName() {
        return "Bestrafung hinzufügen";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        String path = "BestrafungsMenu.AddBestrafungsMenu.";

        String noPerms = lgm.getMessage("Player.General.NoPermissions", player, true);

        LanguageManager lgm = CAPluginMain.getPlugin().getLanguageManager();
        if (item == null || !item.hasItemMeta()) return;
        if (item.getType().equals(lgm.getItem(path + "ExpirationDates.Years", player, false).getType()) &&
                item.getItemMeta().getDisplayName().equals(format(player, lgm.getItem(path + "ExpirationDates.Years", player, false).getItemMeta().getDisplayName()))) {
            if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                if (years == 1000) return;
                years += 1;
            } else if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                if (years == 0) return;
                years -= 1;
            }
            super.open();
        } else if (item.getType().equals(lgm.getItem(path + "ExpirationDates.Months", player, false).getType()) &&
                item.getItemMeta().getDisplayName().equals(format(player, lgm.getItem(path + "ExpirationDates.Months", player, false).getItemMeta().getDisplayName()))) {
            if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                if (months == 1000) return;
                months += 1;
            } else if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                if (months == 0) return;
                months -= 1;
            }
            super.open();
        } else if (item.getType().equals(lgm.getItem(path + "ExpirationDates.Days", player, false).getType()) &&
                item.getItemMeta().getDisplayName().equals(format(player, lgm.getItem(path + "ExpirationDates.Days", player, false).getItemMeta().getDisplayName()))) {
            if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                if (days == 1000) return;
                days += 1;
            } else if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                if (days == 0) return;
                days -= 1;
            }
            super.open();
        } else if (item.getType().equals(lgm.getItem(path + "ExpirationDates.Hours", player, false).getType()) &&
                item.getItemMeta().getDisplayName().equals(format(player, lgm.getItem(path + "ExpirationDates.Hours", player, false).getItemMeta().getDisplayName()))) {
            if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                if (hours == 1000) return;
                hours += 1;
            } else if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                if (hours == 0) return;
                hours -= 1;
            }
            super.open();
        } else if (item.getType().equals(lgm.getItem(path + "ExpirationDates.Minutes", player, false).getType()) &&
                item.getItemMeta().getDisplayName().equals(format(player, lgm.getItem(path + "ExpirationDates.Minutes", player, false).getItemMeta().getDisplayName()))) {
            if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                if (minutes == 1000) return;
                minutes += 5;
            } else if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
                if (minutes == 0) return;
                minutes -= 5;
            }
            super.open();
        } else if (item.getType().equals(lgm.getItem(path + "PotionEffectType", player, false).getType()) &&
                item.getItemMeta().getDisplayName().equals(format(player, lgm.getItem(path + "PotionEffectType", player, false).getItemMeta().getDisplayName()))) {
            player.sendMessage("Gib den Typ im Chat ein!");
            player.setMetadata("BestrafungsSystemEffectType", new FixedMetadataValue(CAPluginMain.getPlugin(), true));
            player.closeInventory();
        } else if (item.getType().equals(lgm.getItem(path + "Amplifier", player, false).getType()) &&
                item.getItemMeta().getDisplayName().equals(format(player, lgm.getItem(path + "Amplifier", player, false).getItemMeta().getDisplayName()))) {
            player.sendMessage("Gib den Amplifier im Chat ein!");
            player.setMetadata("BestrafungsSystemAmplifier", new FixedMetadataValue(CAPluginMain.getPlugin(), true));
            player.closeInventory();
        } else if (item.equals(this.lgm.getItem("General.Close", null, false))) {
            player.closeInventory();
        } else if (item.getType().equals(lgm.getItem(path + "Add", player, false).getType()) &&
                item.getItemMeta().getDisplayName().equals(format(player, lgm.getItem(path + "Add", player, false).getItemMeta().getDisplayName()))) {
            if (!player.hasPermission("ca.admin.bestrafungen.use")) {
                player.sendMessage(noPerms);
                return;
            }
            Date bestrafungsEnde = new Date();
            long minutesMillis = TimeUnit.MINUTES.toMillis(minutes);
            long hoursMillis = TimeUnit.HOURS.toMillis(hours);
            long daysMillis = TimeUnit.DAYS.toMillis(days);
            long monthsMillis = months * 2628000000L;
            long yearsMillis = years * 31556952000L;
            bestrafungsEnde.setTime(System.currentTimeMillis() + minutesMillis + hoursMillis + daysMillis + monthsMillis + yearsMillis);
            CAPluginMain.getPlugin().getBestrafungsManager().addBestrafung(targetUUID, new Bestrafung(
                    targetUUID,
                    bestrafungsEnde.getTime(),
                    System.currentTimeMillis(),
                    potionEffectType,
                    CAPluginMain.getPlugin().getBestrafungsManager().getBestrafungsCount(targetUUID, false),
                    amplifier
            ), true);
            years = 0;
            months = 0;
            days = 0;
            hours = 0;
            minutes = 0;
            CAPluginMain.getPlugin().getBestrafungsManager().checkBestrafungen(targetUUID);
            player.sendMessage("Erfolgreich Bestrafung ausgegeben!");
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        setFillerGlass();
        LanguageManager lgm = CAPluginMain.getPlugin().getLanguageManager();
        Player player = playerMenuUtility.getOwner();
        String path = "BestrafungsMenu.AddBestrafungsMenu.";
        ItemStack stack;
        ItemMeta meta;
        List<String> updatedLore = new ArrayList<>();

        // Years
        stack = lgm.getItem(path + "ExpirationDates.Years", player, false);
        meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(format(player, meta.getDisplayName()));
        updatedLore.clear();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            updatedLore.add(format(player, s));
        }
        meta.setLore(updatedLore);
        stack.setItemMeta(meta);
        inventory.setItem(getSlot(path + "ExpirationDates.Years", 2), stack);

        // Months
        stack = lgm.getItem(path + "ExpirationDates.Months", player, false);
        meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(format(player, meta.getDisplayName()));
        updatedLore.clear();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            updatedLore.add(format(player, s));
        }
        meta.setLore(updatedLore);
        stack.setItemMeta(meta);
        inventory.setItem(getSlot(path + "ExpirationDates.Months", 3), stack);

        // Days
        stack = lgm.getItem(path + "ExpirationDates.Days", player, false);
        meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(format(player, meta.getDisplayName()));
        updatedLore.clear();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            updatedLore.add(format(player, s));
        }
        meta.setLore(updatedLore);
        stack.setItemMeta(meta);
        inventory.setItem(getSlot(path + "ExpirationDates.Days", 4), stack);

        // Hours
        stack = lgm.getItem(path + "ExpirationDates.Hours", player, false);
        meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(format(player, meta.getDisplayName()));
        updatedLore.clear();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            updatedLore.add(format(player, s));
        }
        meta.setLore(updatedLore);
        stack.setItemMeta(meta);
        inventory.setItem(getSlot(path + "ExpirationDates.Hours", 5), stack);

        // Minutes
        stack = lgm.getItem(path + "ExpirationDates.Minutes", player, false);
        meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(format(player, meta.getDisplayName()));
        updatedLore.clear();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            updatedLore.add(format(player, s));
        }
        meta.setLore(updatedLore);
        stack.setItemMeta(meta);
        inventory.setItem(getSlot(path + "ExpirationDates.Minutes", 6), stack);

        // Amplifier
        stack = lgm.getItem(path + "Amplifier", player, false);
        meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(format(player, meta.getDisplayName()));
        updatedLore.clear();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            updatedLore.add(format(player, s));
        }
        meta.setLore(updatedLore);
        stack.setItemMeta(meta);
        inventory.setItem(getSlot(path + "Amplifier", 14), stack);

        // PotionEffectType
        stack = lgm.getItem(path + "PotionEffectType", player, false);
        meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(format(player, meta.getDisplayName()));
        updatedLore.clear();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            updatedLore.add(format(player, s));
        }
        meta.setLore(updatedLore);
        stack.setItemMeta(meta);
        inventory.setItem(getSlot(path + "PotionEffectType", 13), stack);

        // Ban
        stack = lgm.getItem(path + "Add", player, false);
        meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(format(player, meta.getDisplayName()));
        updatedLore.clear();
        for (String s : Objects.requireNonNull(meta.getLore())) {
            updatedLore.add(format(player, s));
        }
        meta.setLore(updatedLore);
        stack.setItemMeta(meta);
        inventory.setItem(getSlot(path + "Add", 22), stack);

        // General
        inventory.setItem(getSlot("General.Close", 26), this.lgm.getItem("General.Close", player, false));
    }

    public String format(Player player, String message) {
        return Utils.format(player, message, AdminPanelMain.getPrefix())
                .replace("%time%", years + ":" + months + ":" + days + ":" + hours + ":" + minutes)
                .replace("%years%", String.valueOf(years))
                .replace("%months%", String.valueOf(months))
                .replace("%days%", String.valueOf(days))
                .replace("%hours%", String.valueOf(hours))
                .replace("%minutes%", String.valueOf(minutes))
                .replace("%amplifier%", String.valueOf(amplifier))
                .replace("%potionEffectType%", potionEffectType.getName());
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("BestrafungsSystemAmplifier")) {
            try {
                this.amplifier = Integer.parseInt(event.getMessage());
                player.removeMetadata("BestrafungsSystemAmplifier", plugin);
                player.sendMessage("Amplifier wurde gesetzt (" + amplifier + ")!");
                super.open();
                event.setCancelled(true);
            } catch (NumberFormatException e) {
                player.sendMessage("Das ist keine Nummer!");
            }
        } else if (player.hasMetadata("BestrafungsSystemEffectType")) {
            try {
                this.potionEffectType = PotionEffectType.getByName(event.getMessage().trim());
                player.removeMetadata("BestrafungsSystemEffectType", plugin);
                player.sendMessage("Potion Effect wurde gesetzt (" + potionEffectType.getName() + ")!");
                super.open();
                event.setCancelled(true);
            } catch (NullPointerException e) {
                player.sendMessage("Try again!");
            }
        }
    }
}
