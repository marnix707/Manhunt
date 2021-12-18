package me.marplayz.manhunt.util;

import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.listeners.DeathListener;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import static me.marplayz.manhunt.listeners.DeathListener.runnerDeathsInt;

public class InfoBoard {

	private static ManhuntPlugin plugin;
	private static PlayerManager playerManager;
	private static GameManager gameManager;

	public InfoBoard(ManhuntPlugin passedPlugin) {
		InfoBoard.plugin = plugin;
	}

	public InfoBoard(PlayerManager playerManager) {
		InfoBoard.playerManager = playerManager;
	}

	public InfoBoard(GameManager gameManager) {
		InfoBoard.gameManager = gameManager;
	}



	private static final String hunterKillsName = ChatColor.GOLD + "" + ChatColor.BOLD + "❯❯❯ Manhunt Info ❮❮❮";

	private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
	private static final Scoreboard board = manager.getNewScoreboard();


	//create infoboard
	public final Objective infoBoard = board.registerNewObjective("manhuntInfo", "dummy", hunterKillsName);

	private static int runnerKillsScore = 0;
	private int animation;


	//Scores
	public void updateScoreboard() {
		String runnerKills = ChatColor.BLUE + "     ➤ Kills: " + ChatColor.GREEN + runnerKillsScore;
		//String runnerRewards = ChatColor.BLUE + "  ➤ Rewards: " + ChatColor.GREEN + reward; //playerManager.reward

		//lives
		String heart = "❤";
		String heartEmpty = "♡";
		String livesString = ChatColor.RED + "❤";
		StringBuilder heart_bfr = new StringBuilder();
		StringBuilder heartEmpty_bfr = new StringBuilder();

		if (runnerDeathsInt < ManhuntPlugin.respawnsFinal) {
			for (int k = 0; k < ManhuntPlugin.respawns; k++) {
				heart_bfr.append(heart);
				livesString = ChatColor.RED + heart_bfr.toString();
			}
			for (int d = 0; d < runnerDeathsInt; d++) {
				heart_bfr.append(heartEmpty);
				livesString = ChatColor.RED + heart_bfr.toString();
			}

		} else // empty hearts
			for (int k = 0; k < ManhuntPlugin.respawnsFinal; k++) {
				heartEmpty_bfr.append(heartEmpty);
				livesString = ChatColor.RED + heartEmpty_bfr.toString();
			}
		String runnerRespawns = ChatColor.BLUE + "     ➤ Lives: " + ChatColor.RED + livesString;

		String hunterDeaths = ChatColor.BLUE + "     ➤ Deaths: " + ChatColor.GREEN + DeathListener.hunterDeathsInt;

		//Remove all
		for (String entry : board.getEntries()) {
			board.resetScores(entry);
		}

		//Place all back watch integers
		infoBoard.getScore("").setScore(10);

		switch(gameManager.getGameState()){
			case LOBBY:
			case STOP:
				infoBoard.getScore(ChatColor.GRAY + "   " + ChatColor.ITALIC +  "In lobby").setScore(9);
				break;
			case STARTING:
				infoBoard.getScore(ChatColor.GRAY + "   " + ChatColor.ITALIC + "Match about to start.").setScore(9);
				break;
			case ACTIVE:
				infoBoard.getScore(ChatColor.GRAY + "   " + ChatColor.ITALIC + "Game in progress").setScore(9);
				break;

		}
		infoBoard.getScore(" ").setScore(8);
		infoBoard.getScore(ChatColor.YELLOW + "   Runners: " + gameManager.runnerTeamSize).setScore(7);
		infoBoard.getScore(runnerKills).setScore(6);
		infoBoard.getScore(runnerRespawns).setScore(5);
		//infoBoard.getScore(runnerRewards).setScore(4);

		infoBoard.getScore("  ").setScore(3);
		infoBoard.getScore(ChatColor.YELLOW + "   Hunters: " + gameManager.hunterTeamSize).setScore(2);
		infoBoard.getScore(hunterDeaths).setScore(1);
		infoBoard.getScore("   ").setScore(0);
	}


	public void playerJoinScoreBoard(Player player) {
		infoBoard.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(board);
		gameManager.getInfoBoard().updateScoreboard();
	}

	public void addRunnerKill() {
		runnerKillsScore += 1;
		gameManager.getInfoBoard().updateScoreboard();
	}

	public void resetsScoreboard() {
		runnerKillsScore = 0;
		gameManager.getInfoBoard().updateScoreboard();
	}
}
