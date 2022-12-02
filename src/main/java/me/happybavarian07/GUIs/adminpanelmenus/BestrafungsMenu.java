package me.happybavarian07.GUIs.adminpanelmenus;/*
 * @Author HappyBavarian07
 * @Date 13.11.2022 | 12:49
 */

import me.happybavarian07.language.PlaceholderType;
import de.happybavarian07.adminpanel.menusystem.PaginatedMenu;
import de.happybavarian07.adminpanel.menusystem.PlayerMenuUtility;
import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.main.Bestrafung;
import me.happybavarian07.main.BestrafungsManager;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.text.SimpleDateFormat;
import java.util.*;

public class BestrafungsMenu extends PaginatedMenu {
    private final UUID targetUUID;
    private final BestrafungsManager bestrafungsManager;
    private final Map<ItemStack, Bestrafung> itemsToWarnings = new HashMap<>();

    public BestrafungsMenu(PlayerMenuUtility playerMenuUtility, UUID targetUUID) {
        super(playerMenuUtility);
        this.targetUUID = targetUUID;
        this.bestrafungsManager = CAPluginMain.getPlugin().getBestrafungsManager();
        setOpeningPermission("ca.admin.bestrafungen.menu");
    }

    @Override
    public String getMenuName() {
        return "Bestrafungs Menu";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    /* Permissions:
                (Use the Warn Button)
              ca.admin.bestrafungen.use:
                default: true
                (Open Warn Menu)
              ca.admin.bestrafungen.menu:
                default: true
     */
    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();
        List<Bestrafung> warnings = new ArrayList<>(bestrafungsManager.getBestrafungen(targetUUID));

        String noPerms = lgm.getMessage("Player.General.NoPermissions", player, true);

        if (item.equals(CAPluginMain.getPlugin().getLanguageManager().getItem("BestrafungsMenu.AddBestrafung", Bukkit.getPlayer(targetUUID), false))) {
            if (!player.hasPermission("ca.admin.bestrafungen.use")) {
                player.sendMessage(noPerms);
                return;
            }
            new AddBestrafungMenu(playerMenuUtility, targetUUID).open();
        } else if (item.getType().equals(CAPluginMain.getPlugin().getLanguageManager().getItem("BestrafungsMenu.BestrafungsItem", Bukkit.getPlayer(targetUUID), false).getType())) {
            if (!player.hasPermission("ca.admin.bestrafungen.use")) {
                player.sendMessage(noPerms);
                return;
            }
            bestrafungsManager.removeBestrafung(targetUUID, itemsToWarnings.get(item).getBestrafungsCount(), true);
            player.sendMessage("Bestrafung entfernt!");
            player.closeInventory();
            //player.sendMessage("Warning: " + itemsToWarnings.getOrDefault(item, new Bestrafung(player.getUniqueId(), -1, -1, PotionEffectType.BAD_OMEN, -12, 10)).toString());
        } else if (item.equals(lgm.getItem("General.Close", null, false))) {
            player.closeInventory();
        } else if (item.equals(lgm.getItem("General.Left", null, false))) {
            if (page == 0) {
                player.sendMessage(lgm.getMessage("Player.General.AlreadyOnFirstPage", player, true));
            } else {
                page = page - 1;
                super.open();
            }
        } else if (item.equals(lgm.getItem("General.Right", null, false))) {
            if (!((index + 1) >= warnings.size())) {
                page = page + 1;
                super.open();
            } else {
                player.sendMessage(lgm.getMessage("Player.General.AlreadyOnLastPage", player, true));
            }
        } else if (item.equals(lgm.getItem("General.Refresh", null, false))) {
            if (!player.hasPermission("AdminPanel.Button.refresh")) {
                player.sendMessage(noPerms);
                return;
            }
            super.open();
        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();
        LanguageManager lgm2 = CAPluginMain.getPlugin().getLanguageManager();
        //The thing you will be looping through to place items
        List<Bestrafung> bestrafungen = new ArrayList<>(bestrafungsManager.getBestrafungen(targetUUID));
        System.out.println("Bestrafungen: " + bestrafungen);

        ///////////////////////////////////// Pagination loop template
        if (bestrafungen != null && !bestrafungen.isEmpty()) {
            for (int i = 0; i < super.maxItemsPerPage; i++) {
                index = super.maxItemsPerPage * page + i;
                if (index >= bestrafungen.size()) break;
                if (bestrafungen.get(index) != null) {
                    ///////////////////////////
                    // TODO Finish
                    Bestrafung bestrafung = bestrafungen.get(index);
                    Player player = Bukkit.getPlayer(targetUUID);
                    try {
                        lgm2.addPlaceholder(PlaceholderType.ITEM, "%count%", bestrafung.getBestrafungsCount(), false);
                        lgm2.addPlaceholder(PlaceholderType.ITEM, "%amplifier%", bestrafung.getAmplifier(), false);
                        lgm2.addPlaceholder(PlaceholderType.ITEM, "%potionEffectType%", bestrafung.getPotionEffectType().getName(), false);
                        lgm2.addPlaceholder(PlaceholderType.ITEM, "%expirationDate%", longToFormattedDate(bestrafung.getExpirationDate(), "yyyy/MM/dd HH:mm:ss"), false);
                        lgm2.addPlaceholder(PlaceholderType.ITEM, "%creationDate%", longToFormattedDate(bestrafung.getCreationDate(), "yyyy/MM/dd HH:mm:ss"), false);
                        ItemStack item = lgm2.getItem("BestrafungsMenu.BestrafungsItem", player, false);
                        inventory.addItem(item);
                        itemsToWarnings.put(item, bestrafung);
                    } catch (NumberFormatException e) {
                        System.out.println("Bestrafung #" + bestrafung.getBestrafungsCount() + " from Player " + player.getName() + " threw a Error. Check if the Creation-/Expiration Date contains any characters that are not numbers!");
                    }
                    ////////////////////////
                }
            }
        }
        inventory.setItem(getSlot("BestrafungsMenu.AddBestrafung", 47), CAPluginMain.getPlugin().getLanguageManager().getItem("BestrafungsMenu.AddBestrafung", playerMenuUtility.getOwner(), false));
        ////////////////////////
    }

    public String longToFormattedDate(long dateInLongFormat, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date tempDate = new Date(dateInLongFormat);
        return sdf.format(tempDate);
    }
}
