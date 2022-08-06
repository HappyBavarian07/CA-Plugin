package me.happybavarian07.mysql;

import me.happybavarian07.main.CAPluginMain;
import me.happybavarian07.main.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLHandler {

    private final CAPluginMain plugin;
    private final boolean mySQLDisabled;
    private MySQL mySQL;
    private Connection connection;
    private File configFile;
    private FileConfiguration config;

    public MySQLHandler(boolean mySQLdisabled) {
        this.plugin = CAPluginMain.getPlugin();
        this.mySQLDisabled = mySQLdisabled;
        //System.out.println("Boolean MySQL: " + mySQLdisabled + "!!!!!!!!!!!!!" + !mySQLdisabled);
        if (mySQLdisabled) {
            configFile = new File(plugin.getDataFolder() + "/savedData.yml");
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            config = YamlConfiguration.loadConfiguration(configFile);
        } else {
            this.mySQL = new MySQL();
            mySQL.readData();
            mySQL.connect();
            this.connection = mySQL.getConnection();
            this.createTable();
        }
    }

    public boolean useMySQL() {
        return !mySQLDisabled;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public void createTable() {
        if (useMySQL()) {
            if (!mySQL.isConnected()) return;
            try {
                PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + mySQL.getTablePrefix() +
                        "playerdata(NAME VARCHAR(20), UUID VARCHAR(100) PRIMARY KEY, Prefix VARCHAR(20), DiscordVerified BOOLEAN, discordID VARCHAR(100))");
                ps.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public Prefix getPrefix(String uuid) {
        if (useMySQL()) {
            if (!hasData(uuid))
                insert((Player) Bukkit.getOfflinePlayer(UUID.fromString(uuid)), Prefix.EMPTY, false, "");
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT Prefix FROM " + mySQL.getTablePrefix() + "playerdata WHERE UUID = ?");
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    return Prefix.valueOf(rs.getString("Prefix"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            if (!hasData(uuid)) {
                insert((Player) Bukkit.getOfflinePlayer(UUID.fromString(uuid)), Prefix.EMPTY, false, "");
                return Prefix.EMPTY;
            }
            return Prefix.valueOf(config.getString("Players." + uuid + ".prefix"));
        }
        return null;
    }

    public String getDiscordID(String uuid) {
        if (useMySQL()) {
            if (!hasData(uuid))
                insert((Player) Bukkit.getOfflinePlayer(UUID.fromString(uuid)), getPrefix(uuid), false, "");
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT discordID FROM " + mySQL.getTablePrefix() + "playerdata WHERE UUID = ?");
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    return rs.getString("discordID");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            if (!hasData(uuid)) {
                insert((Player) Bukkit.getOfflinePlayer(UUID.fromString(uuid)), Prefix.EMPTY, false, "");
                return "";
            }
            return config.getString("Players." + uuid + ".discordID");
        }
        return null;
    }

    public boolean isVerified(String uuid) {
        if (useMySQL()) {
            if (!hasData(uuid))
                insert((Player) Bukkit.getOfflinePlayer(UUID.fromString(uuid)), Prefix.EMPTY, false, "");
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT DiscordVerified FROM " + mySQL.getTablePrefix() + "playerdata WHERE UUID = ?");
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    return rs.getBoolean("DiscordVerified");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            if (!hasData(uuid)) {
                insert((Player) Bukkit.getOfflinePlayer(UUID.fromString(uuid)), Prefix.EMPTY, false, "");
                return false;
            }
            return config.getBoolean("Players." + uuid + ".discordVerified");
        }
        return false;
    }

    public void insert(Player player, Prefix prefix, Boolean verified, String verifiedID) {
        if (useMySQL()) {
            try {
                String uuid = player.getUniqueId().toString();
                if (hasData(uuid)) {
                    PreparedStatement ps = connection.prepareStatement("UPDATE " + mySQL.getTablePrefix() + "playerdata SET NAME = ?, UUID = ?, Prefix = ?, DiscordVerified = ?, discordID = ? WHERE UUID = ?");
                    ps.setString(1, player.getName());
                    ps.setString(2, uuid);
                    ps.setString(3, prefix.toString());
                    ps.setBoolean(4, verified);
                    ps.setString(5, verifiedID);
                    ps.setString(6, uuid);
                    ps.executeUpdate();
                } else {
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO " + mySQL.getTablePrefix() + "playerdata(NAME,UUID,Prefix,DiscordVerified,discordID) VALUES (?,?,?,?,?)");
                    ps.setString(1, player.getName());
                    ps.setString(2, uuid);
                    ps.setString(3, prefix.toString());
                    ps.setBoolean(4, verified);
                    ps.setString(5, verifiedID);
                    ps.executeUpdate();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            String uuid = player.getUniqueId().toString();
            config.set("Players." + uuid + ".name", player.getName());
            config.set("Players." + uuid + ".uuid", player.getUniqueId().toString());
            config.set("Players." + uuid + ".prefix", prefix.toString());
            config.set("Players." + uuid + ".discordID", verifiedID);
            config.set("Players." + uuid + ".discordVerified", verified);
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasData(String uuid) {
        if (useMySQL()) {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT UUID FROM " + mySQL.getTablePrefix() + "playerdata WHERE UUID = ?");
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                return rs.next();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            return config.getConfigurationSection("Players." + uuid) != null;
        }
        return false;
    }

    public void setPrefix(String uuid, Prefix prefix) {
        if (useMySQL()) {
            if (!hasData(uuid)) insert((Player) Bukkit.getOfflinePlayer(UUID.fromString(uuid)), prefix, false, "");
            try {
                PreparedStatement ps = connection.prepareStatement("UPDATE " + mySQL.getTablePrefix() + "playerdata SET Prefix = ? WHERE UUID = ?");
                ps.setString(1, prefix.toString());
                ps.setString(2, uuid);
                ps.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            if (!hasData(uuid)) {
                insert((Player) Bukkit.getOfflinePlayer(UUID.fromString(uuid)), Prefix.EMPTY, false, "");
            }
            config.set("Players." + uuid + ".prefix", prefix.toString());
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setVerified(String uuid, Boolean verified, String discordID) {
        if (useMySQL()) {
            if (!hasData(uuid))
                insert((Player) Bukkit.getOfflinePlayer(UUID.fromString(uuid)), Prefix.EMPTY, false, "");
            try {
                PreparedStatement ps = connection.prepareStatement("UPDATE " + mySQL.getTablePrefix() + "playerdata SET DiscordVerified = ?, discordID = ? WHERE UUID = ?");
                ps.setBoolean(1, verified);
                ps.setString(2, discordID);
                ps.setString(3, uuid);
                ps.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            if (!hasData(uuid)) {
                insert((Player) Bukkit.getOfflinePlayer(UUID.fromString(uuid)), Prefix.EMPTY, false, "");
            }
            config.set("Players." + uuid + ".discordID", discordID);
            config.set("Players." + uuid + ".discordVerified", verified);
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
