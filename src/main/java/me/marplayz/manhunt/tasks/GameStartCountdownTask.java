package me.marplayz.manhunt.tasks;

import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.states.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartCountdownTask extends BukkitRunnable {

	private GameManager gameManager;

	public GameStartCountdownTask(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	private final int startCountdown = ManhuntPlugin.startCountdownConfig;
	public int timeLeft = startCountdown;

	public BossBar barStartCountdown = Bukkit.createBossBar(ChatColor.GOLD + "" + ChatColor.BOLD + "MANHUNT STARTS IN", BarColor.PURPLE, BarStyle.SOLID);

	@Override
	public void run() {
		if(gameManager.getGameState().equals(GameState.LOBBY)){
			barStartCountdown.removeAll();
			cancel();
			return;
		}
		timeLeft--;
		if (timeLeft <= 0) {
			barStartCountdown.removeAll();
			for (Player players : Bukkit.getOnlinePlayers()) {
				players.playSound(players.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 1);
			}
			gameManager.setGameState(GameState.ACTIVE);
			cancel();
			return;
		}
		barStartCountdown.setProgress(timeLeft / (double) startCountdown);
		barStartCountdown.setVisible(true);
		for (Player players : Bukkit.getOnlinePlayers()) {
			players.sendTitle(ChatColor.GOLD + "Match Starting in " + timeLeft + " Seconds.", ChatColor.BLUE + "Good Luck!", 0, 25, 0);
			barStartCountdown.addPlayer(players);
			players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 0.5F);

		}

	}
}


