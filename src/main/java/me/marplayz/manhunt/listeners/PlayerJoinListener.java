package me.marplayz.manhunt.listeners;

import me.marplayz.manhunt.commands.ManhuntCommand;
import me.marplayz.manhunt.states.GameState;
import me.marplayz.manhunt.util.InfoBoard;
import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerJoinListener implements Listener {

	private static ManhuntPlugin plugin;
	private InfoBoard infoBoard;
	private GameManager gameManager;

	public PlayerJoinListener(ManhuntPlugin plugin) {
		PlayerJoinListener.plugin = plugin;
	}

	public PlayerJoinListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	private final String prefix = ManhuntPlugin.prefix;
	private int autoEndTask;

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		gameManager.getInfoBoard().playerJoinScoreBoard(player);
		event.setJoinMessage(null);
		Bukkit.getScheduler().cancelTask(autoEndTask);

		switch (gameManager.getGameState()) {
			case LOBBY:
				player.getWorld().setTime(5000);
				player.getWorld().setClearWeatherDuration(1000000);
				player.teleport(player.getWorld().getSpawnLocation());
				player.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
			case STARTING:
				gameManager.getPlayerManager().giveLobbyKit(player);
				Bukkit.broadcastMessage(prefix + ChatColor.AQUA + player.getDisplayName() + ChatColor.GOLD + " has joined the lobby. [" + Bukkit.getServer().getOnlinePlayers().size() + "/16]");
				break;
			case ACTIVE:
				player.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
				if (Team.hasTeam(player)) {
					if (Team.getTeam(player).getName().equalsIgnoreCase("Runner")) {
						Bukkit.broadcastMessage(prefix + ChatColor.RED + "[RUNNER] " + player.getDisplayName() + ChatColor.GOLD + " joined the game.");
					} else {
						Bukkit.broadcastMessage(prefix + ChatColor.GREEN + "[HUNTER] " + player.getDisplayName() + ChatColor.GOLD + " joined the game.");
					}
				} else {
					gameManager.getPlayerManager().giveStartingKitSpectator(player);
					Bukkit.broadcastMessage(prefix + ChatColor.AQUA + "[SPECTATOR] " + player.getDisplayName() + ChatColor.GOLD + " joined the game.");
					player.sendMessage(prefix + ChatColor.GOLD + "A game is currently in progress, you are now spectating!");
				}
				break;
		}
	}

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		gameManager.getInfoBoard().playerJoinScoreBoard(player);
		event.setQuitMessage(null);

		switch (gameManager.getGameState()) {
			case LOBBY:
			case STARTING:
				if (ManhuntCommand.runners.contains(player.getName())) {
					ManhuntCommand.runners.remove(player.getName());
					gameManager.runnerTeamSize -= 1;
				}
				if (ManhuntCommand.hunters.contains(player.getName())) {
					ManhuntCommand.hunters.remove(player.getName());
					gameManager.hunterTeamSize -= 1;
				}

				Bukkit.broadcastMessage(prefix + ChatColor.AQUA + player.getDisplayName() + ChatColor.GOLD + " has left the lobby. [" + (Bukkit.getServer().getOnlinePlayers().size() - 1) + "/16]");
				if(gameManager.hunterTeamSize == 0 || gameManager.runnerTeamSize == 0){
					Bukkit.broadcastMessage(prefix + ChatColor.RED + "Not enough players to start.");
					gameManager.setGameState(GameState.STOP);
				}
				break;
			case ACTIVE:
				if (Team.hasTeam(player)) {
					if (Team.getTeam(player).getName().equalsIgnoreCase("Runner")) {
						Bukkit.broadcastMessage(prefix + ChatColor.RED + "[RUNNER] " + player.getDisplayName() + ChatColor.GOLD + " has left the game.");
					} else {
						Bukkit.broadcastMessage(prefix + ChatColor.GREEN + "[HUNTER] " + player.getDisplayName() + ChatColor.GOLD + " has left the game.");
					}

					//auto end match when no players are online
					if (Bukkit.getServer().getOnlinePlayers().size() == 0 || gameManager.getPlugin().getConfig().getString("auto-end").equalsIgnoreCase("true")) {
						Bukkit.broadcastMessage(prefix + ChatColor.RED + "No players found, ending match in " + gameManager.getPlugin().getConfig().getInt("auto-end-minutes") + " minute(s)");
						autoEndTask = Bukkit.getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
							@Override
							public void run() {
								gameManager.stopCurrentGame();
								Bukkit.broadcastMessage(prefix + ChatColor.RED + "Match automatically ended.");
							}
						}, 20L * 60 * gameManager.getPlugin().getConfig().getInt("auto-end-minutes"));
					}
					break;
				} else {
					Bukkit.broadcastMessage(prefix + ChatColor.AQUA + "[SPECTATOR] " + player.getDisplayName() + ChatColor.GOLD + " has left the game.");
				}
		}
	}
}
