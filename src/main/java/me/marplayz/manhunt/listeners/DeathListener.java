package me.marplayz.manhunt.listeners;

import me.marplayz.manhunt.util.InfoBoard;
import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.util.Team;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.manager.GameState;
import me.marplayz.manhunt.manager.PlayerManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class DeathListener implements Listener {

	private ManhuntPlugin plugin;
	private GameManager gameManager;
	private PlayerManager playerManager;
	private GameState gameState;
	private InfoBoard infoBoard;

	public DeathListener(ManhuntPlugin plugin) {
		this.plugin = plugin;
	}

	public DeathListener(GameManager gameManager) {
		this.gameManager = gameManager;

		this.playerManager = getPlayerManager();
	}

	//public Location locationPortalRunner;
	public static int hunterDeathsInt = 0;
	public static int runnerDeathsInt = 0;

	String prefix = ManhuntPlugin.prefix;

	public PlayerManager getPlayerManager() {
		return playerManager;
	}


	@EventHandler
	public void PlayerRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		if (Team.getTeam(p) != null && Team.getTeam(p).getName() == "Hunter" && gameManager.getPlugin().getConfig().getString("hunter-inv-keep").equalsIgnoreCase("false")) {

			ItemStack compass = new ItemStack(Material.COMPASS, 1);
			ItemMeta meta = compass.getItemMeta();
			meta.setDisplayName(gameManager.getInventoryManager().compassName);
			compass.setItemMeta(meta);

			p.getInventory().addItem(compass);
		}
	}

	@EventHandler
	public void InventoryRemoval(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		if(gameManager.getGameState() != GameState.ACTIVE) return;
		Player player = (Player) event.getPlayer();

		if (gameManager.getPlayerManager().missingAnyRewards()) {
			try {
				Team playerTeam = Team.getTeam(player);
				if (playerTeam.getName().equalsIgnoreCase("Runner") && player.getInventory().firstEmpty() >= 0) {
					gameManager.getPlayerManager().GiveReward(player);
				}
			} catch (NullPointerException e) {
				System.out.println(e);
			}
		}
	}

	@EventHandler
	public void RunnerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		FileConfiguration config = gameManager.getPlugin().getConfig();

		if (Team.getTeam(p) != null && Team.getTeam(p).getName().equalsIgnoreCase("Runner")) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
				public void run() {
					p.spigot().respawn();
				}
			}, 1);
			runnerDeathsInt += 1;
			ManhuntPlugin.respawns--;

			//respawn system
			if (ManhuntPlugin.respawns == 0) {
				Bukkit.broadcastMessage(prefix + ChatColor.AQUA + "" + p.getName() + " has lost the manhunt!");
				p.playSound(p.getLocation(), Sound.valueOf(config.getString("runner-death-sound")), 10, 1);
				gameManager.setGameState(GameState.WON);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
					public void run() {
						p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
					}
				}, 2);
				return;
			}

			if (config.getString("runner-inv-keep").equals("true")) {
				event.getDrops().clear();
				event.setKeepInventory(true);
				event.setKeepLevel(true);
			}
			if (config.getString("regional-respawn").equalsIgnoreCase("true")) {

				Location deathLoc = p.getLocation();

				//store before adding new location to it
				Location deathLocation = deathLoc;//remove to block location
				Location newLocation = RandomLocation(deathLoc);

				double distance = deathLocation.distance(newLocation);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
					public void run() {
						p.teleport(newLocation);
						p.sendMessage(prefix + ChatColor.GOLD + "You have been respawned " + (int) distance + " blocks away! " + ManhuntPlugin.respawns + " Lives left!");
					}
				}, 2);

			}
			gameManager.getInfoBoard().updateScoreboard();
		}
	}

	@EventHandler
	public void HunterDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		FileConfiguration config = gameManager.getPlugin().getConfig();

		if (Team.getTeam(p) != null && Team.getTeam(p).getName() == "Hunter") {
			hunterDeathsInt++;
			//If killed by runner then give bonus
			if (p.getKiller() != null) {
				if (Team.getTeam(p.getKiller()) != null) {
					if (p.getKiller() != null && Team.getTeam(p.getKiller()).getName().equalsIgnoreCase("Runner")
							&& config.getString("runner-kill-reward").equalsIgnoreCase("true")) {
						Player killer = p.getKiller();
						gameManager.getInfoBoard().addRunnerKill();
						gameManager.getPlayerManager().GiveReward(killer);
					}
				}
			}

			if (config.getString("hunter-inv-keep").equals("true")) {
				event.getDrops().clear();
				event.setKeepInventory(true);
				event.setKeepLevel(true);
			}
			if (p.getPlayer().getWorld().getEnvironment().equals(World.Environment.NORMAL)
					&& config.getString("regional-respawn").equalsIgnoreCase("true")) {

				Location deathLoc = p.getLocation();

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
					public void run() {
						p.spigot().respawn();
					}
				}, 1);

				Location deathLocation = deathLoc.getBlock().getLocation();
				Location newLocation = RandomLocation(deathLoc);

				double distance = deathLocation.distance(newLocation);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
					public void run() {
						p.teleport(newLocation);
						p.sendMessage(prefix + ChatColor.GOLD + "You have been respawned " + (int) distance + " blocks away!");
					}
				}, 1);

				//death in the nether spawn near portal
			} else if (p.getPlayer().getWorld().getEnvironment().equals(World.Environment.NETHER)
					&& config.getString("regional-portal-respawn").equalsIgnoreCase("true")) {

				//store location portal and get new distance from portal location
				Location locPortal = PortalListener.locationPortalHunter;
				Location newLocationPortal = RandomLocation(PortalListener.locationPortalHunter);

				double distance = locPortal.distance(newLocationPortal);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
					public void run() {
						p.spigot().respawn();
					}
				}, 1);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
					public void run() {
						if (!(PortalListener.locationPortalHunter == null)) {
							p.teleport(newLocationPortal);
							p.sendMessage(prefix + ChatColor.GOLD + "I told you");
							p.sendMessage(prefix + ChatColor.GOLD + "You have been respawned " + (int) distance + " blocks away!");
						}
					}
				}, 1);
			}
			gameManager.getInfoBoard().updateScoreboard();
		}
	}

	//get new random location based from a given location with parameters from config
	public Location RandomLocation(Location deathLoc) {
		FileConfiguration config = gameManager.getPlugin().getConfig();
		Random random = new Random();
		int max = config.getInt("max-range");
		int min = config.getInt("min-range");
		double upper = Math.sqrt((float) (Math.pow(max, 2) / 2));
		double lower = Math.sqrt((float) (Math.pow(min, 2) / 2));

		int randomMultiple = random.nextInt((int) upper - (int) lower) + (int) lower;
		int randomNegative = random.nextInt(2) - 1;
		if (randomNegative == 0) {
			randomNegative++;
		}

		Location randomLoc = deathLoc.add(randomMultiple / randomNegative, 0, randomMultiple / randomNegative);
		int x = randomLoc.getBlockX();
		int z = randomLoc.getBlockZ();
		int y = randomLoc.getWorld().getHighestBlockYAt(x, z);

		return new Location(deathLoc.getWorld(), x, y + 1, z);
	}
}
