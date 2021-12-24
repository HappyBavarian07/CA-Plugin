package me.happybavarian07.GUIs.prefixes;

import me.happybavarian07.main.CAPluginMain;
import me.rockyhawk.commandpanels.api.Panel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import java.io.File;

public class PrefixInventory implements Listener {
    private final CAPluginMain plugin;
    private final Panel prefixPanel;
    private final File panelFile;
    private final FileConfiguration panelConfig;
    private final String panelName;

    // TODO Prefix Panel Fertig! Danach mit Commandpanels adden + command: /prefix menu
    public PrefixInventory(CAPluginMain plugin, PrefixConfig panelConfig, String panelName) {
        this.plugin = plugin;
        this.panelFile = panelConfig.getConfigFile();
        this.panelConfig = panelConfig.getConfig();
        this.panelName = panelName;
        this.prefixPanel = new Panel(this.panelFile, this.panelName);
    }

    public Panel getPrefixPanel() {
        return prefixPanel;
    }

    public File getPanelFile() {
        return panelFile;
    }

    public FileConfiguration getPanelConfig() {
        return panelConfig;
    }

    public String getPanelName() {
        return panelName;
    }
}
