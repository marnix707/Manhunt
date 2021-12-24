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
	double distance = 0;
	private Location newLocation;
	private Location oldLocation;

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
		if (gameManager.getGameState() != GameState.ACTIVE) return;
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
		World.Environment deathEnvironment = p.getPlayer().getWorld().getEnvironment();
		FileConfiguration config = gameManager.getPlugin().getConfig();

		if (Team.getTeam(p) != null && Team.getTeam(p).getName().equalsIgnoreCase("Runner")) {

			//instant respawn player (maybe remove task) just do instant
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
				public void run() {
					p.spigot().respawn();
				}
			}, 0);
			runnerDeathsInt += 1;
			ManhuntPlugin.respawns--;
			gameManager.getInfoBoard().updateScoreboard();

			//Lose manhunt no lives left
			if (ManhuntPlugin.respawns == 0) {
				Bukkit.broadcastMessage(prefix + ChatColor.AQUA + "" + p.getName() + ChatColor.RED + " has lost the manhunt!");
				p.sendTitle(ChatColor.RED + "You lost!", ChatColor.BLUE + "Better luck next time", 0, 60, 20);
				event.getDrops().clear();
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
					public void run() {
						p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
						gameManager.setGameState(GameState.WON);
					}
				}, 1);
				return;
			}

			if (config.getString("runner-inv-keep").equals("true")) {
				event.getDrops().clear();
				event.setKeepInventory(true);
				event.setKeepLevel(true);
			}

			//Lives left, respawn accordingly

			switch (deathEnvironment) {
				case THE_END:
					if (config.getString("runner-end-regional-respawn").equalsIgnoreCase("true")) {
						oldLocation = PortalListener.locationEndPortalRunner.getBlock().getLocation();
						newLocation = RandomLocation(PortalListener.locationEndPortalRunner);
					} else return;
					break;
				case NETHER:
					if (config.getString("runner-nether-regional-respawn").equalsIgnoreCase("true")) {
						oldLocation = PortalListener.locationPortalRunner.getBlock().getLocation();
						newLocation = RandomLocation(PortalListener.locationPortalRunner);
					} else return;
					break;
				case NORMAL:
					if (config.getString("runner-regional-respawn").equalsIgnoreCase("true")) {
						Location deathLocation = p.getLocation();
						oldLocation = p.getLocation().getBlock().getLocation();
						newLocation = RandomLocation(deathLocation);
					} else return;
					break;
			}

			distance = oldLocation.distance(newLocation);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
				public void run() {
					p.teleport(newLocation);
					p.playSound(p.getLocation(), Sound.valueOf(config.getString("runner-death-sound")), 10, 1);
					p.sendMessage(prefix + ChatColor.GOLD + "You respawned " + (int) distance + " blocks away");
					p.sendMessage(prefix + ChatColor.RED + ManhuntPlugin.respawns + " live(s) left!");
					gameManager.getRespawnEffect().respawnParticle(p);
				}
			}, 0);
		}
	}

	@EventHandler
	public void HunterDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		World.Environment deathEnvironment = p.getPlayer().getWorld().getEnvironment();
		FileConfiguration config = gameManager.getPlugin().getConfig();

		if (Team.getTeam(p) != null && Team.getTeam(p).getName() == "Hunter") {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
				public void run() {
					p.spigot().respawn();
				}
			}, 0);

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
			gameManager.getInfoBoard().updateScoreboard();

			if (config.getString("hunter-inv-keep").equals("true")) {
				event.getDrops().clear();
				event.setKeepInventory(true);
				event.setKeepLevel(true);
			}
			switch (deathEnvironment) {
				case THE_END:
					if (config.getString("hunter-end-regional-respawn").equalsIgnoreCase("true")) {
						oldLocation = PortalListener.locationEndPortalHunter.getBlock().getLocation();
						newLocation = RandomLocation(PortalListener.locationEndPortalHunter);
					} else return;
					break;
				case NETHER:
					if (config.getString("hunter-nether-regional-respawn").equalsIgnoreCase("true")) {
						oldLocation = PortalListener.locationPortalHunter.getBlock().getLocation();
						newLocation = RandomLocation(PortalListener.locationPortalHunter);
					} else return;
					break;
				case NORMAL:
					if (config.getString("hunter-regional-respawn").equalsIgnoreCase("true")) {
						Location deathLocation = p.getLocation();
						oldLocation = p.getLocation().getBlock().getLocation();
						newLocation = RandomLocation(deathLocation);
					} else return;
					break;
			}

			distance = oldLocation.distance(newLocation);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new Runnable() {
				public void run() {
					p.teleport(newLocation);
					p.sendMessage(prefix + ChatColor.GOLD + "You respawned " + (int) distance + " blocks away");
				}
			}, 0);
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

		float randomMultiple = random.nextInt((int) upper - (int) lower) + (int) lower;
		int randomNegative = random.nextInt(2) - 1;
		if (randomNegative == 0) {
			randomNegative++;
		}
		int randomNegative2 = random.nextInt(2) - 1;
		if (randomNegative2 == 0) {
			randomNegative2++;
		}

		Location randomLoc = deathLoc.add(randomMultiple / randomNegative, 0, randomMultiple / randomNegative2);
		int x = randomLoc.getBlockX();
		int z = randomLoc.getBlockZ();
		int y = randomLoc.getWorld().getHighestBlockYAt(x, z);

		return new Location(deathLoc.getWorld(), x, y + 1, z);
	}
}
