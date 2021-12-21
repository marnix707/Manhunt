package me.marplayz.manhunt.listeners;

import me.marplayz.manhunt.util.InfoBoard;
import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
			case STARTING:
				gameManager.getPlayerManager().giveLobbyKit(player);
				Bukkit.broadcastMessage(prefix + ChatColor.AQUA + player.getDisplayName() + ChatColor.GOLD + " has joined the lobby. [" + Bukkit.getServer().getOnlinePlayers().size() + "/16]");
				break;
			case ACTIVE:
				if (Team.hasTeam(player)) {
					if (Team.getTeam(player).getName().equalsIgnoreCase("Runner")) {
						Bukkit.broadcastMessage(prefix + ChatColor.AQUA + "[RUNER] " + player.getDisplayName() + ChatColor.GOLD + " joined the game.");
					} else {
						Bukkit.broadcastMessage(prefix + ChatColor.AQUA + "[HUNTER] " + player.getDisplayName() + ChatColor.GOLD + " joined the game.");
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
				Bukkit.broadcastMessage(prefix + ChatColor.AQUA + player.getDisplayName() + ChatColor.GOLD + " has left the lobby. [" + (Bukkit.getServer().getOnlinePlayers().size() - 1) + "/16]");
				break;
			case ACTIVE:
				if (Team.hasTeam(player)) {
					if (Team.getTeam(player).getName().equalsIgnoreCase("Runner")) {
						Bukkit.broadcastMessage(prefix + ChatColor.AQUA + "[RUNER] " + player.getDisplayName() + ChatColor.GOLD + " has left the game.");
					} else {
						Bukkit.broadcastMessage(prefix + ChatColor.AQUA + "[HUNTER] " + player.getDisplayName() + ChatColor.GOLD + " has left the game.");
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
