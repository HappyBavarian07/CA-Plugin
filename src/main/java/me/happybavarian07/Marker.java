package me.happybavarian07;

import me.happybavarian07.main.CAPluginMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Marker {

    private final Location loc;
    private final Player player;
    private final String name;
    private final File file;

    public Marker(Location loc, Player player, String markerName) {
        this.loc = loc;
        this.player = player;
        this.name = markerName.replace(".yml", "");
        this.file = new File(CAPluginMain.getPlugin().getDataFolder() + "/Markers/" + markerName + ".yml");
    }

    public Marker(Location loc, String playername, String markerName) {
        this.loc = loc;
        this.player = Bukkit.getPlayer(playername);
        this.name = markerName.replace(".yml", "");
        this.file = new File(CAPluginMain.getPlugin().getDataFolder() + "/Markers/" + markerName + ".yml");
    }

    public Marker(World world, double x, double y, double z, float yaw, float pitch, Player player, String markerName) {
        this.loc = new Location(world, x, y, z, yaw, pitch);
        this.player = player;
        this.name = markerName.replace(".yml", "");
        this.file = new File(CAPluginMain.getPlugin().getDataFolder() + "/Markers/" + markerName + ".yml");
    }

    public Marker(World world, double x, double y, double z, float yaw, float pitch, String playername, String markerName) {
        this.loc = new Location(world, x, y, z, yaw, pitch);
        this.player = Bukkit.getPlayer(playername);
        this.name = markerName.replace(".yml", "");
        this.file = new File(CAPluginMain.getPlugin().getDataFolder() + "/Markers/" + markerName + ".yml");
    }

    public Marker(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.loc = new Location(
                Bukkit.getWorld(config.getString("Marker.World")),
                config.getDouble("Marker.X"),
                config.getDouble("Marker.Y"),
                config.getDouble("Marker.Z"),
                (float) config.getDouble("Marker.Yaw"),
                (float) config.getDouble("Marker.Pitch")
        );
        this.player = Bukkit.getPlayer(UUID.fromString(config.getString("Marker.Player.UUID")));
        this.name = file.getName().replace(".yml", "");
        this.file = file;
    }

    public Player getPlayer() {
        return player;
    }

    public Double getX() {
        return loc.getX();
    }

    public Double getY() {
        return loc.getY();
    }

    public Double getZ() {
        return loc.getZ();
    }

    public Float getYaw() {
        return loc.getYaw();
    }

    public Float getPitch() {
        return loc.getPitch();
    }

    public World getWorld() {
        return loc.getWorld();
    }

    public String getName() {
        return name;
    }

    public File getFile() { return file; }

    public UUID getUUIDFromFile() {
        return UUID.fromString(loadFileConfig().getString("Marker.Player.UUID"));
    }

    public void createMarkerFile() {
        try {
            if (!markerExists())
                file.createNewFile();

            setDefaults();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean removeMarkerFile() {
        if(markerExists())
            return file.delete();

        return false;
    }

    private void setDefaults() {
        FileConfiguration config = loadFileConfig();
        config.set("Marker.World", loc.getWorld().getName());
        config.set("Marker.X", loc.getX());
        config.set("Marker.Y", loc.getY());
        config.set("Marker.Z", loc.getZ());
        config.set("Marker.Yaw", loc.getX());
        config.set("Marker.Pitch", loc.getX());
        config.set("Marker.Player.Name", player.getName());
        config.set("Marker.Player.UUID", player.getUniqueId().toString());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean markerExists() {
        return file != null && file.exists() && loadFileConfig().contains("Marker.World");
    }

    public boolean UUIDEquals(UUID uuid) {
        return this.getUUIDFromFile().equals(uuid);
    }

    private FileConfiguration loadFileConfig() {
        return YamlConfiguration.loadConfiguration(this.file);
    }
}
