package me.marplayz.manhunt.listeners;

import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.states.GameState;
import me.marplayz.manhunt.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DragonListener implements Listener {

	private final GameManager gameManager;

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
		winner.sendTitle(ChatColor.GREEN + "You won!", ChatColor.BLUE + "", 0, 60, 20);
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!Team.hasTeam(player)) {
				return;
			}
			if (Team.getTeam(player).getName() != null && Team.getTeam(player).getName().equalsIgnoreCase("Hunter")) {
				player.sendTitle(ChatColor.RED + "You lost!", ChatColor.BLUE + "Better luck next time", 0, 60, 20);
			}
		}
		gameManager.setGameState(GameState.WON);
	}
}
