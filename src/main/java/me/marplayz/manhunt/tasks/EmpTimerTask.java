package me.marplayz.manhunt.tasks;

import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.listeners.EMPListener;
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

public class EmpTimerTask extends BukkitRunnable {

	private final GameManager gameManager;
	private EMPListener EMPListener;

	public EmpTimerTask(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	private final int empTimer = ManhuntPlugin.empTimerConfig;
	private int timeLeft = empTimer;

	BossBar empTimerBar = Bukkit.createBossBar(ChatColor.GOLD + "" + ChatColor.BOLD + "Compass Back In", BarColor.RED, BarStyle.SOLID);

	String prefix = ManhuntPlugin.prefix;

	@Override
	public void run() {
		if(gameManager.getGameState().equals(GameState.LOBBY)){
			empTimerBar.removeAll();
			cancel();
			return;
		}
		timeLeft--;

		if (timeLeft <= 0) {
			cancel();
			gameManager.getItemListener().empToggle = false;
			empTimerBar.removeAll();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (Team.getTeam(player).getName().equals("Hunter")) {
					player.sendMessage(prefix + ChatColor.GREEN + "Your compass is working again!");
				}
			}
			return;
		}
		gameManager.getItemListener().empToggle = true;
		empTimerBar.setProgress(timeLeft / (double) empTimer);
		empTimerBar.setVisible(true);
		for (Player players : Bukkit.getOnlinePlayers()) {
			empTimerBar.addPlayer(players);
		}
	}
}

