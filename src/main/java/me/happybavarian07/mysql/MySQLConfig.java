package me.happybavarian07.mysql;

import me.happybavarian07.main.CAPluginMain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MySQLConfig {

    private final CAPluginMain plugin;
    private Properties properties = null;
    private File propertiesFile = null;

    public MySQLConfig() {
        this.plugin = CAPluginMain.getPlugin();
        // saves/initializes the Config
        saveDefaultConfig();
    }

    public void reloadConfig() {
        try {
            properties.load(new FileInputStream(this.propertiesFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties() {
        return this.properties;
    }

    public File getPropertiesFile() {
        return propertiesFile;
    }

    public void saveConfig() {
        try {
            properties.store(new FileOutputStream(propertiesFile), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefaultConfig() {
        if (this.propertiesFile == null)
            this.propertiesFile = new File(this.plugin.getDataFolder(), "mysql.properties");

        if (!this.propertiesFile.exists()) {
            this.plugin.saveResource("mysql.properties", false);
        }
        this.properties = new Properties();
        try {
            properties.load(new FileInputStream(this.propertiesFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
