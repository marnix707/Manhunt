package me.marplayz.manhunt.tasks;

import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.manager.GameState;
import me.marplayz.manhunt.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HunterStartCountdownTask extends BukkitRunnable {

	private GameManager gameManager;

	public HunterStartCountdownTask(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	private final String prefix = ManhuntPlugin.prefix;

	public BossBar barHunterCountdown = Bukkit.createBossBar(ChatColor.GOLD + "" + ChatColor.BOLD + "HUNTERS LEAVE IN", BarColor.RED, BarStyle.SOLID);
	String StartMessageHunterLeft = prefix + ChatColor.RED + "Hunters left!";

	int timeLeft = GameManager.hunterStartCountdown;

	@Override
	public void run() {
		if(gameManager.getGameState().equals(GameState.LOBBY)){
			barHunterCountdown.removeAll();
			cancel();
			return;
		}
		timeLeft--;
		if (timeLeft <= 0) {
			cancel();
			barHunterCountdown.removeAll();
			Bukkit.broadcastMessage(StartMessageHunterLeft);
			for (Player players : Bukkit.getOnlinePlayers()) {
				if (Team.getTeam(players) != null && Team.getTeam(players).getName().equals("Hunter")) {
					players.sendTitle(ChatColor.GREEN + "GO!", "", 0, 20, 0);
				}
			}
			return;
		}

		barHunterCountdown.setProgress(timeLeft / (double) GameManager.hunterStartCountdown);
		barHunterCountdown.setVisible(true);
		for (Player players : Bukkit.getOnlinePlayers()) {
			barHunterCountdown.addPlayer(players);
			if (Team.getTeam(players) != null && Team.getTeam(players).getName().equals("Hunter")) {
				players.sendTitle(ChatColor.RED + "Please Wait " + timeLeft + " Seconds.", "", 0, 25, 0);
			}
		}
	}
}
