package me.marplayz.manhunt.util;

import me.marplayz.manhunt.manager.GameManager;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

import static me.marplayz.manhunt.ManhuntPlugin.prefix;

public class WorldGeneration {

	private final GameManager gameManager;

	public WorldGeneration(GameManager gameManager){
		this.gameManager = gameManager;
	}

	public void createNewGameWorld(CommandSender sender){
		World manhuntWorld = gameManager.getPlugin().getServer().getWorld("Manhunt");
		if(manhuntWorld != null){
			System.out.println("Manhunt world found");
			for(Player p: manhuntWorld.getPlayers()){
				p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			}
			sender.sendMessage(prefix + ChatColor.GREEN + "Unloading previous world");

			for(Chunk c : manhuntWorld.getLoadedChunks()){
				c.unload();
			}
			gameManager.getPlugin().getServer().unloadWorld("Manhunt", true);

			sender.sendMessage(prefix + ChatColor.GREEN + "Deleting previous world");
			deleteDirectory(manhuntWorld.getWorldFolder());

			System.out.println("World deleted");
		}
		sender.sendMessage(prefix + ChatColor.GREEN + "Creating new world... (This may take a while)");
		World NewWorld = gameManager.getPlugin().getServer().createWorld(new WorldCreator("Manhunt"));
		System.out.println("New world created");
		sender.sendMessage(prefix + ChatColor.GREEN + "World created");
	}

	public boolean deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				} //end else
			}
		}
		return( path.delete() );
	}
}
