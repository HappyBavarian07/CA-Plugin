package me.happybavarian07.GUIs.adminpanelmenus;/*
 * @Author HappyBavarian07
 * @Date 31.10.2022 | 10:20
 */

import de.happybavarian07.adminpanel.menusystem.Menu;
import de.happybavarian07.adminpanel.menusystem.PlayerMenuUtility;
import me.happybavarian07.language.LanguageManager;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SelectorMenu extends Menu {
    private final LanguageManager lgm;
    public SelectorMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        lgm = CAPluginMain.getPlugin().getLanguageManager();
    }
    // TODO FERTIG MACHEN (UNDER DEVELOPMENT)

    @Override
    public String getMenuName() {
        return lgm.getMenuTitle("SelectorMenu", playerMenuUtility.getOwner());
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

    }

    @Override
    public void setMenuItems() {
        setFillerGlass();
    }
}
