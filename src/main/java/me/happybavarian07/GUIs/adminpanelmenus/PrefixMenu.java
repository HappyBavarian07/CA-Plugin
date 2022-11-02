package me.happybavarian07.GUIs.adminpanelmenus;/*
 * @Author HappyBavarian07
 * @Date 31.10.2022 | 10:20
 */

import de.happybavarian07.adminpanel.menusystem.Menu;
import de.happybavarian07.adminpanel.menusystem.PlayerMenuUtility;
import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.main.Prefix;
import me.happybavarian07.main.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PrefixMenu extends Menu {
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
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        Prefix prefixFromItem = Prefix.fromMaterial(item.getType());

        String noPerms = lgm.getMessage("Player.NoPermissions", player, true);

        if(prefixFromItem == null) return;

        if (player.getWorld().getName().equals(CAPluginMain.getPlugin().getConfig().getString("CA.world.CraftAttack_World"))) {
            Utils.setPlayerPrefix(player, prefixFromItem);
        } else {
            player.sendMessage("§cDu kannst deinen Prefix nur setzen wenn du in der Craft Attack Welt bist!");
        }
    }

    @Override
    public void setMenuItems() {
        Player player = playerMenuUtility.getOwner();
        setFillerGlass();
        inventory.setItem(getSlot("PrefixItems.Offline", 3), lgm.getItem("PrefixItems.Offline", player, false));
        inventory.setItem(getSlot("PrefixItems.Online", 5), lgm.getItem("PrefixItems.Online", player, false));
        inventory.setItem(getSlot("PrefixItems.Redstone", 23), lgm.getItem("PrefixItems.Redstone", player, false));
        inventory.setItem(getSlot("PrefixItems.Live", 15), lgm.getItem("PrefixItems.Live", player, false));
        inventory.setItem(getSlot("PrefixItems.Afk", 11), lgm.getItem("PrefixItems.Afk", player, false));
        inventory.setItem(getSlot("PrefixItems.Record", 21), lgm.getItem("PrefixItems.Record", player, false));
        inventory.setItem(getSlot("PrefixItems.InfoItem", 13), lgm.getItem("PrefixItems.InfoItem", player, false));
    }
}
