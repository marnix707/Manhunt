package me.marplayz.manhunt.listeners;

import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.manager.PlayerManager;
import me.marplayz.manhunt.util.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PortalListener implements Listener {

	private ManhuntPlugin plugin;
	private GameManager gameManager;
	private PlayerManager playerManager;


	public PortalListener(ManhuntPlugin plugin) {
		this.plugin = plugin;
	}

	public PortalListener(GameManager gameManager) {
		this.gameManager = gameManager;

		this.playerManager = getPlayerManager();
	}

	public static Location locationPortalRunner;
	public static Location locationEndPortalRunner;

	public static Location locationPortalHunter;
	public static Location locationEndPortalHunter;
	public PlayerManager getPlayerManager() {
		return playerManager;
	}


	String prefix = ManhuntPlugin.prefix;

	@EventHandler
	public void RunnerPortal(PlayerTeleportEvent event) {
		if ((gameManager.getPlugin().getConfig().getString("compass-portal").equalsIgnoreCase("true") || gameManager.getPlugin().getConfig().getString("runner-nether-regional-respawn").equalsIgnoreCase("true"))
			&& Team.getTeam(event.getPlayer()) != null) {
			if (event.getFrom().getWorld().getEnvironment().equals(World.Environment.NORMAL) && event.getTo().getWorld().getEnvironment().equals(World.Environment.NETHER)
					&& Team.getTeam(event.getPlayer()).getName().equals("Runner")) {
				Player p = event.getPlayer();
				locationPortalRunner = event.getFrom();
				p.sendMessage(prefix + ChatColor.GOLD + "Portal tracked");
			}
		}

		if ((gameManager.getPlugin().getConfig().getString("compass-end-portal").equalsIgnoreCase("true") || gameManager.getPlugin().getConfig().getString("runner-end-regional-respawn").equalsIgnoreCase("true"))
			&& Team.getTeam(event.getPlayer()) != null) {
			if (event.getFrom().getWorld().getEnvironment().equals(World.Environment.NORMAL) && event.getTo().getWorld().getEnvironment().equals(World.Environment.THE_END)
					&& Team.getTeam(event.getPlayer()).getName().equals("Runner")) {
				Player p = event.getPlayer();
				locationEndPortalRunner = event.getFrom();
				p.sendMessage(prefix + ChatColor.GOLD + "Portal tracked");
			}
		}
	}

	@EventHandler
	public void HunterPortal(PlayerTeleportEvent event) {

		//Track portal to the nether
		if (gameManager.getPlugin().getConfig().getString("hunter-nether-regional-respawn").equalsIgnoreCase("true") && Team.getTeam(event.getPlayer()) != null) {
			if (event.getFrom().getWorld().getEnvironment().equals(World.Environment.NORMAL) && event.getTo().getWorld().getEnvironment().equals(World.Environment.NETHER)
					&& Team.getTeam(event.getPlayer()).getName().equals("Hunter")) {
				Player p = event.getPlayer();
				locationPortalHunter = event.getFrom();
				p.sendMessage(prefix + ChatColor.GOLD + "We got your back!");
			}
		}
		//Track portal to the end
		if (gameManager.getPlugin().getConfig().getString("hunter-end-regional-respawn").equalsIgnoreCase("true") && Team.getTeam(event.getPlayer()) != null) {
			if (event.getFrom().getWorld().getEnvironment().equals(World.Environment.NORMAL) && event.getTo().getWorld().getEnvironment().equals(World.Environment.THE_END)
					&& Team.getTeam(event.getPlayer()).getName().equals("Hunter")) {
				Player p = event.getPlayer();
				locationEndPortalHunter = event.getFrom();
				p.sendMessage(prefix + ChatColor.GOLD + "We got your back!");
			}
		}
	}
}
