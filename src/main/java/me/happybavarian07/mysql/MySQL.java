package me.happybavarian07.mysql;

import me.happybavarian07.API.StartUpLogger;
import me.happybavarian07.main.Utils;
import me.happybavarian07.main.CAPluginMain;
import org.bukkit.ChatColor;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQL {
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private String tablePrefix;

    private final MySQLConfig mySQLConfig;
    private final File mySQLFile;
    private final Properties properties;
    private Connection connection;

    private final StartUpLogger logger;

    // TODO MySQL Craft Attack Plugin mit den Settings der Spieler (Prefixe, ...)
    public MySQL() {
        this.logger = CAPluginMain.getPlugin().getStartUpLogger();
        this.mySQLConfig = new MySQLConfig();
        this.properties = mySQLConfig.getProperties();
        this.mySQLFile = mySQLConfig.getPropertiesFile();
    }

    public void readData() {
        logger.coloredSpacer(ChatColor.GREEN).message(getPrefix() + format("&aAttempting to Read Data!"));
        this.host = properties.getProperty("Host"); // Get Host
        logger.message(getPrefix() + format("&aHost: ") + getHost());
        this.port = properties.getProperty("Port"); // Get Port
        logger.message(getPrefix() + format("&aPort: ") + getPort());
        this.database = properties.getProperty("Database"); // Get Database
        logger.message(getPrefix() + format("&aDatabase: ") + getDatabase());
        this.username = properties.getProperty("Username"); // Get Username
        logger.message(getPrefix() + format("&aUsername: ") + getUsername());
        this.password = properties.getProperty("Password"); // Get Password
        this.tablePrefix = properties.getProperty("TablePrefix"); // Get TablePrefix
        logger.message(getPrefix() + format("&aTablePrefix: ") + getTablePrefix());
        logger.message(getPrefix() + format("&aFinished! Connecting...")).coloredSpacer(ChatColor.GREEN);
    }

    public void connect() {
        if(this.isConnected()) return;

        try {
            logger.coloredSpacer(ChatColor.GOLD).message(getPrefix() + format("&aAttempting to Connect!"));
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            logger.message(getPrefix() + format("&aConnected!")).coloredSpacer(ChatColor.GOLD);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void disconnect() {
        if(!this.isConnected()) return;
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean isConnected() {
        return this.connection != null;
    }

    public String getPassword() {
        return password;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public File getMySQLFile() {
        return mySQLFile;
    }

    public Properties getProperties() {
        return properties;
    }

    public MySQLConfig getMySQLConfig() {
        return mySQLConfig;
    }

    public String getPort() {
        return port;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public String getUsername() {
        return username;
    }

    public String getPrefix() {
        return Utils.format(null, properties.getProperty("Prefix") + " ", "");
    }

    public String format(String message) {
        return Utils.format(null, message, getPrefix());
    }
}
