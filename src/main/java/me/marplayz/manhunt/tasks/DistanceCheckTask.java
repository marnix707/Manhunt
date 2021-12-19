package me.marplayz.manhunt.tasks;

import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.manager.GameState;
import me.marplayz.manhunt.util.Team;
import me.marplayz.manhunt.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DistanceCheckTask extends BukkitRunnable {
	private GameManager gameManager;

	public DistanceCheckTask(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public BossBar distanceCountdownBar = Bukkit.createBossBar(ChatColor.BLUE + "" + ChatColor.BOLD + "DISTANCE CHECK IN", BarColor.GREEN, BarStyle.SOLID);

	private final String prefix = ManhuntPlugin.prefix;

	private final int maxTimeLeft = ManhuntPlugin.distanceTimer * 60;
	private int timeLeft = maxTimeLeft;


	public void run() {
		if(gameManager.getGameState().equals(GameState.LOBBY)){
			distanceCountdownBar.removeAll();
			cancel();
			return;
		}

		if (timeLeft <= 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (Team.getTeam(p) != null && Team.getTeam(p).getName().equals("Runner")) {
					distanceCheck(p);
				}
			}
			//Count again
			timeLeft = maxTimeLeft;
		}
		//If timer still not 0 run this code every second
		distanceCountdownBar.setProgress(timeLeft / (double) maxTimeLeft);
		distanceCountdownBar.setVisible(true);
		for (Player players : Bukkit.getOnlinePlayers()) {
			distanceCountdownBar.addPlayer(players);
		}
		timeLeft--;
	}

	public void distanceCheck(Player runner) {
		double distance = getDistance(runner);
		String distanceMessage = prefix + ChatColor.GOLD + maxTimeLeft/(60*20) + " minutes have passed. Distance is " + (int) distance + "";
		String distanceMessageWorld = prefix + ChatColor.GOLD + "You and the hunter are not in the same world.";
		String distanceMessageWorldHunter = prefix + ChatColor.GOLD + "You and the runner are not in the same world.";


		//Runner Message
		if (distance > -1) {
			runner.sendMessage(distanceMessage);
		} else {
			runner.sendMessage(distanceMessageWorld);
		}

		//Hunter Message
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (gameManager.getPlugin().getConfig().getString("distance-check-hunter").equalsIgnoreCase("true") && Team.getTeam(p) != null && Team.getTeam(p).getName().equals("Hunter")) {
				if (distance > -1) {
					p.sendMessage(distanceMessage);
				} else {
					p.sendMessage(distanceMessageWorldHunter);
				}
			}
		}
	}

	public double getDistance(Player runner) {
		double distance = 0;

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (Team.getTeam(p) != null && Team.getTeam(p).getName().equals("Hunter")) {
				Player hunter = p.getPlayer();
				if (runner.getLocation().getWorld().getEnvironment() == hunter.getLocation().getWorld().getEnvironment()) {
					distance = runner.getLocation().distance(hunter.getLocation());
				} else {
					distance = -1;
					break;
				}
			}
		}
		return distance;
	}
}

