package me.marplayz.manhunt.listeners;

import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.manager.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DragonListener implements Listener {

	private GameManager gameManager;

	public DragonListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	String prefix = ManhuntPlugin.prefix;

	@EventHandler
	public void enderDragonDeath(EntityDeathEvent event) {
		if (!event.getEntity().getType().equals(EntityType.ENDER_DRAGON)) {
			return;
		}
		if (gameManager.getGameState() != GameState.ACTIVE) {
			return;
		}
		Player winner = event.getEntity().getKiller();
		event.getEntity().setSilent(true);
		Bukkit.broadcastMessage(prefix + ChatColor.GOLD + winner.getDisplayName() + ChatColor.GREEN + " Has won the game!");
		gameManager.setGameState(GameState.WON);
	}
}
