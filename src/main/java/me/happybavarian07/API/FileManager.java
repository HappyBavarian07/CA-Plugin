package me.happybavarian07.API;

import me.happybavarian07.main.CAPluginMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManager {
	
	CAPluginMain plugin;
	Logger log;
	
	public FileManager(CAPluginMain plugin) {
		this.plugin = plugin;
		this.log = plugin.getLogger();
	}
	
	public File createFile(String path, String name, String fileending) {
		File f;
		if(path.equals("")) {
			f = new File(plugin.getDataFolder(), name + "." + fileending);
		} else {
			f = new File(plugin.getDataFolder() + "/" + path, name + "." + fileending);
			f.getParentFile().mkdir();
		}
		if(!f.exists()) {
			try {
				log.log(Level.FINE, "Saved " + f + " successfully!");
				f.createNewFile();
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not save File " + f, e);
			}
		} else {
			log.log(Level.WARNING, "File " + f + " already exists!");
			return f;
		}
		return f;
	}
	
	public void saveConfig(String path, String name, String fileending) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(getFile(path, name, fileending));
		File file = getFile(path, name, fileending);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public File getFile(String path, String name, String fileending) {
		File f;
		if(path == "") {
			f = new File(plugin.getDataFolder(), name + "." + fileending);
		} else {
			f = new File(plugin.getDataFolder() + "/" + path, name + "." + fileending);
		}
		if(f.exists()) {
			log.log(Level.FINE, "Saved " + f + " successfully!");
			return f;
		}
		return null;
	}
	
	public FileConfiguration getConfig(String path, String name, String fileending) {
		File f;
		if(path == "") {
			f = new File(plugin.getDataFolder(), name + "." + fileending);
		} else {
			f = new File(plugin.getDataFolder() + "/" + path, name + "." + fileending);
		}
		FileConfiguration cfg;
		if(f.exists()) {
			cfg = YamlConfiguration.loadConfiguration(f);
			log.log(Level.FINE, "Loaded FileConfig for File " + f + " successfully!");
			return cfg;
		}
		return null;
	}
	public void deleteFile(String path, String name, String fileending) {
		File f;
		if(path == "") {
			f = new File(plugin.getDataFolder(), name + "." + fileending);
		} else {
			f = new File(plugin.getDataFolder() + "/" + path, name + "." + fileending);
		}
		if(f.exists()) {
			log.log(Level.FINE, "Deleted " + f + " successfully!");
			f.delete();
			return;
		} else {
			log.log(Level.WARNING, "File " + f + " doesn't exists!");
		}
		return;
	}
	
	public File[] listFiles(String path) {
		String fullpath = plugin.getDataFolder() + path;
		File folder = new File(fullpath);
		File[] files = new File[folder.listFiles().length];
		for(int i = 0; i < folder.listFiles().length; i++) {
			files[i] = folder.listFiles()[i];
		}
		if(files.length > 0) {
			return files;
		}
		return null;
	}

	public String[] listFileNames(String path) {
		String fullpath = plugin.getDataFolder() + path;
		File folder = new File(fullpath);
		String[] files = new String[folder.listFiles().length];
		for(int i = 0; i < folder.listFiles().length; i++) {
			files[i] = folder.listFiles()[i].getName();
		}
		if(files.length > 0) {
			return files;
		}
		return null;
	}
}
