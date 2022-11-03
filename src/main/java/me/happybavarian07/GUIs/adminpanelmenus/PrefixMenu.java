package me.happybavarian07.GUIs.adminpanelmenus;/*
 * @Author HappyBavarian07
 * @Date 31.10.2022 | 10:20
 */

import de.happybavarian07.adminpanel.main.AdminPanelMain;
import de.happybavarian07.adminpanel.menusystem.PaginatedMenu;
import de.happybavarian07.adminpanel.menusystem.PlayerMenuUtility;
import de.happybavarian07.adminpanel.menusystem.menu.AdminPanelStartMenu;
import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.main.Prefix;
import me.happybavarian07.main.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PrefixMenu extends PaginatedMenu {
    private final LanguageManager lgm;

    public PrefixMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        lgm = CAPluginMain.getPlugin().getLanguageManager();
    }

    @Override
    public String getMenuName() {
        return lgm.getMenuTitle("PrefixMenu", playerMenuUtility.getOwner());
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        Map<String, Prefix> prefixListTemp = new HashMap<>(CAPluginMain.getPlugin().getPrefixList());
        Map<Integer, Prefix> prefixList = new HashMap<>();
        int count = 0;
        for (String prefix : prefixListTemp.keySet()) {
            prefixList.put(count, prefixListTemp.get(prefix));
            count++;
        }
        if (item == null) {
            return;
        }
        de.happybavarian07.adminpanel.main.LanguageManager templgm = AdminPanelMain.getPlugin().getLanguageManager();

        if (item.equals(templgm.getItem("General.Close", null, false))) {
            player.closeInventory();
            return;
        } else if (item.equals(templgm.getItem("General.Left", null, false))) {
            if (page == 0) {
                player.sendMessage(templgm.getMessage("Player.General.AlreadyOnFirstPage", player, true));
            } else {
                page = page - 1;
                super.open();
            }
            return;
        } else if (item.equals(templgm.getItem("General.Right", null, false))) {
            if (!((index + 1) >= prefixList.size())) {
                page = page + 1;
                super.open();
            } else {
                player.sendMessage(templgm.getMessage("Player.General.AlreadyOnLastPage", player, true));
            }
            return;
        } else if (item.equals(templgm.getItem("General.Refresh", null, false))) {
            super.open();
            return;
        }

        Prefix prefixFromItem = CAPluginMain.getPlugin().prefixFromMaterial(item.getType());

        if (prefixFromItem == null) return;

        if (player.getWorld().getName().equals(CAPluginMain.getPlugin().getConfig().getString("CA.world.CraftAttack_World"))) {
            Utils.setPlayerPrefix(player, prefixFromItem, true);
        } else {
            player.sendMessage("§cDu kannst deinen Prefix nur setzen wenn du in der Craft Attack Welt bist!");
        }
    }

    @Override
    public void setMenuItems() {
        Player player = playerMenuUtility.getOwner();
        addMenuBorder();
        /*inventory.setItem(getSlot("PrefixItems.Offline", 3), lgm.getItem("PrefixItems.Offline", player, false));
        inventory.setItem(getSlot("PrefixItems.Online", 5), lgm.getItem("PrefixItems.Online", player, false));
        inventory.setItem(getSlot("PrefixItems.Redstone", 23), lgm.getItem("PrefixItems.Redstone", player, false));
        inventory.setItem(getSlot("PrefixItems.Live", 15), lgm.getItem("PrefixItems.Live", player, false));
        inventory.setItem(getSlot("PrefixItems.Afk", 11), lgm.getItem("PrefixItems.Afk", player, false));
        inventory.setItem(getSlot("PrefixItems.Record", 21), lgm.getItem("PrefixItems.Record", player, false));
        inventory.setItem(getSlot("PrefixItems.InfoItem", 13), lgm.getItem("PrefixItems.InfoItem", player, false));*/

        //The thing you will be looping through to place items
        Map<String, Prefix> prefixListTemp = new HashMap<>(CAPluginMain.getPlugin().getPrefixList());
        Map<Integer, Prefix> prefixList = new HashMap<>();
        int count = 0;
        for (String prefix : prefixListTemp.keySet()) {
            prefixList.put(count, prefixListTemp.get(prefix));
            count++;
        }

        ///////////////////////////////////// Pagination loop template
        if (prefixList != null && !prefixList.isEmpty()) {
            for (int i = 0; i < super.maxItemsPerPage; i++) {
                index = super.maxItemsPerPage * page + i;
                if (index >= prefixList.size()) break;
                if (prefixList.get(index) != null) {
                    ///////////////////////////

                    Prefix prefix = prefixList.get(index);
                    if(prefix.getMenuMaterial().equals(Material.BARRIER)) continue;
                    ItemStack item = new ItemStack(prefix.getMenuMaterial());
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName(prefix.getConfigName());
                    meta.setLore(Arrays.asList(
                            Utils.formatChatMessage(player, "&3Prefix: &7" + prefix.getInGamePrefix(), true, true),
                            Utils.formatChatMessage(player, "&3Suffix: &7" + prefix.getInGameSuffix(), true, true),
                            Utils.formatChatMessage(player, "&3Format: &7" + prefix.getFormat(player), true, true)
                    ));
                    item.setItemMeta(meta);
                    inventory.addItem(item);

                    ////////////////////////
                }
            }
        }
        ////////////////////////
    }
}
