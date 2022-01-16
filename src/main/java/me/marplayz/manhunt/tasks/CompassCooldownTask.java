package me.marplayz.manhunt.tasks;

import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.states.GameState;
import me.marplayz.manhunt.util.Team;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CompassCooldownTask extends BukkitRunnable {

	private GameManager gameManager;

	public CompassCooldownTask(GameManager gameManager) { this.gameManager = gameManager;
	}

	private BaseComponent[] bc;

	@Override
	public void run() {
		if(gameManager.getGameState().equals(GameState.LOBBY)){
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (Team.getTeam(p) != null && Team.getTeam(p).getName().equalsIgnoreCase("Hunter")) {
					//p.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
				}
			}
			cancel();
			return;
		}
		if (gameManager.getTrackerManager().compassCooldown * 10 <= 0) {
			//timeLeft = ManhuntPlugin.compassCooldownConfig * 10;
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (Team.getTeam(p) != null && Team.getTeam(p).getName().equalsIgnoreCase("Hunter")) {
					bc = TextComponent.fromLegacyText(ChatColor.GREEN + "Compass Ready");
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,bc);
					gameManager.getTrackerManager().compassCooldown = gameManager.getPlugin().getConfig().getInt("compass-cooldown-amount") *10;
				}
			}
			cancel();
			return;
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (Team.getTeam(p) != null && Team.getTeam(p).getName().equalsIgnoreCase("Hunter")) {
				bc = TextComponent.fromLegacyText(ChatColor.RED + "Compass back in: " + (double)gameManager.getTrackerManager().compassCooldown/10 + " S");
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, bc);
			}
		}
		gameManager.getTrackerManager().compassCooldown--;
	}
}