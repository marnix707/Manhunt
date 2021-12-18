package me.marplayz.manhunt.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CustomConfigs {

	private static File file;
	private static FileConfiguration customFile;


	//Finds or generate Starting kits config
	public static void setup() {
		file = new File(Bukkit.getServer().getPluginManager().getPlugin("Manhunt").getDataFolder(), "itemConfig.yml");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				//no error biatch
			}
		}
		customFile = YamlConfiguration.loadConfiguration(file);
	}

	public static FileConfiguration get() {
		return customFile;
	}

	public static void saveCustomConfig(){
		try{
			customFile.save(file);
		} catch (IOException e){
			System.out.println("Could not save custom file");
		}
	}

	public static void reloadConfigs(){
		customFile = YamlConfiguration.loadConfiguration(file);
	}
}
