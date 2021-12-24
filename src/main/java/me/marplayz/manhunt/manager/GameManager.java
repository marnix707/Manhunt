package me.marplayz.manhunt.manager;

import me.marplayz.manhunt.GUI.*;
import me.marplayz.manhunt.listeners.*;
import me.marplayz.manhunt.particles.RespawnEffect;
import me.marplayz.manhunt.tasks.CompassCooldownTask;
import me.marplayz.manhunt.util.InfoBoard;
import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.util.Team;
import me.marplayz.manhunt.commands.ManhuntCommand;

import me.marplayz.manhunt.tasks.DistanceCheckTask;
import me.marplayz.manhunt.tasks.GameStartCountdownTask;
import me.marplayz.manhunt.tasks.HunterStartCountdownTask;
import me.marplayz.manhunt.util.WorldGeneration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.checkerframework.checker.units.qual.C;

import static me.marplayz.manhunt.listeners.DeathListener.runnerDeathsInt;

public class GameManager {

	public static int hunterStartCountdown;
	private final ManhuntPlugin plugin;
	private final PlayerManager playerManager;
	private final InfoBoard infoBoard;
	private final EMPListener EMPListener;
	private final InventoryManager inventoryManager;
	private final SettingMenu settingMenu;
	private final MainMenu mainMenu;
	private ManhuntCommand getManhuntCommand;
	private final KitsMenu kitsMenu;
	private GameStartCountdownTask gameStartCountdownTask;
	private ManhuntCommand manhuntCommand;
	private final GameModeManager gameModeManager;
	private final CompassMenu compassMenu;
	private final CompassListener compassListener;
	private final RespawnEffect respawnEffect;
	private final WorldGeneration worldGeneration;

	private GameState gameState = GameState.LOBBY;

	public GameManager(ManhuntPlugin plugin) {
		this.plugin = plugin;

		this.playerManager = new PlayerManager(this);
		this.EMPListener = new EMPListener(this);
		this.inventoryManager = new InventoryManager(this);
		this.settingMenu = new SettingMenu(this);
		this.mainMenu = new MainMenu(this);
		PlayerJoinListener playerJoinListener = new PlayerJoinListener(this);
		this.infoBoard = new InfoBoard(this);
		this.gameModeManager = new GameModeManager(this);
		GameModeMenu gameMode = new GameModeMenu(this);
		DeathListener deathListener = new DeathListener(this);
		this.kitsMenu = new KitsMenu(this);
		this.compassMenu = new CompassMenu(this);
		this.compassListener = new CompassListener(this);
		DragonListener dragonListener = new DragonListener(this);
		this.respawnEffect = new RespawnEffect(this);
		CompassCooldownTask compassCooldownTask = new CompassCooldownTask(this);
		this.worldGeneration = new WorldGeneration(this);
	}

	public int hunterTeamSize = 0;
	public int runnerTeamSize = 0;

	private final String prefix = ManhuntPlugin.prefix;

	public void setGameState(GameState gameState) {
		if (this.gameState == gameState) return;
		if (this.gameState == GameState.ACTIVE && gameState == GameState.STARTING) {
			Bukkit.broadcastMessage(prefix + ChatColor.RED + "A game is already in progress, please stop it first.");
			return;
		}

		this.gameState = gameState;

		switch (gameState) {
			case LOBBY:
				System.out.println(prefix + ChatColor.GOLD + "Lobby");
				plugin.worlds.get(0).setDifficulty(Difficulty.PEACEFUL);
				for (Player p : Bukkit.getOnlinePlayers()) {
					playerManager.giveLobbyKit(p);
				}
				break;

			case STARTING:
				Bukkit.broadcastMessage(prefix + ChatColor.AQUA + "Match will start in " + ManhuntPlugin.startCountdownConfig + " seconds!");
				ManhuntPlugin.respawnsFinal = ManhuntPlugin.respawns;

				ManhuntPlugin.respawns = plugin.getConfig().getInt("runner-respawns");
				DeathListener.hunterDeathsInt = 0;
				runnerDeathsInt = 0;
				getInfoBoard().updateScoreboard();

				GameStartCountdownTask gameStartCountdownTask = new GameStartCountdownTask(this);
				gameStartCountdownTask.runTaskTimer(plugin, 0, 20);
				break;

			case ACTIVE:
				System.out.println(prefix + ChatColor.GOLD + "Match started");
				plugin.worlds.get(0).setDifficulty(Difficulty.HARD);
				getInfoBoard().updateScoreboard();

				//Bossbar
				hunterStartCountdown = plugin.hunterCountdownConfig;
				HunterStartCountdownTask hunterStartCountdownTask = new HunterStartCountdownTask(this);
				hunterStartCountdownTask.runTaskTimer(plugin, 0, 20L);

				for (Player p : Bukkit.getOnlinePlayers()) {
					if (Team.getTeam(p) == null) {
						p.sendMessage(prefix + ChatColor.GOLD + "Match started! You are spectating.");
						getPlayerManager().giveStartingKitSpectator(p);
					}

					if (Team.getTeam(p) != null && Team.getTeam(p).getName().equalsIgnoreCase("Runner")) {
						getPlayerManager().giveStartingKitRunner(p);

						//Distance check
						String distanceCheck = plugin.getConfig().getString("distance-check");
						if (distanceCheck.equalsIgnoreCase("true")) {
							DistanceCheckTask distanceCheckTask = new DistanceCheckTask(this);
							distanceCheckTask.runTaskTimer(plugin, 0, 20);
						}

					} else if (Team.getTeam(p) != null && Team.getTeam(p).getName().equalsIgnoreCase("Hunter")) {
						getPlayerManager().giveStartingKitHunter(p);
					}
				}
				break;

			case STOP:
				Bukkit.broadcastMessage(prefix + ChatColor.GOLD + "Match ended.");
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.teleport(plugin.worlds.get(0).getSpawnLocation());
				}
				stopCurrentGame();
				break;

			case WON:
				System.out.println(prefix + ChatColor.GOLD + "Match won.");
				World lobbyWorld = plugin.worlds.get(0);
				Location lobbyLocation = lobbyWorld.getSpawnLocation();
				for (Player player : Bukkit.getOnlinePlayers()) {
					if(!Team.hasTeam(player)){return;}

					player.teleport(lobbyLocation);
					player.playSound(lobbyLocation, Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
					player.playSound(lobbyLocation, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 10, 1);
					//Fireworks
					Firework startFirework = (Firework) lobbyWorld.spawnEntity(lobbyLocation, EntityType.FIREWORK);
					FireworkMeta startFireworkMeta = startFirework.getFireworkMeta();
					startFireworkMeta.addEffect(FireworkEffect.builder()
							.flicker(true)
							.trail(true)
							.with(FireworkEffect.Type.BALL_LARGE)
							.withColor(Color.RED)
							.withFade(Color.BLUE)
							.build());
					startFireworkMeta.setPower(1);
					startFirework.setFireworkMeta(startFireworkMeta);
				}
				stopCurrentGame();
				break;
		}
	}

	public void stopCurrentGame() {

		//delayed cancel task in order to update bossbars to remove themselfs
/*		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				Bukkit.getScheduler().cancelTasks(plugin);
			}
		},21);*/

		for (Player players : Bukkit.getOnlinePlayers()) {
			try {
				Team.getTeam("Hunter").remove(players);
				Team.getTeam("Runner").remove(players);
			} catch (NullPointerException e) {
				System.out.println("A team was not empty.");
			}
		}
		//Reset all variables and update scoreboard
		ManhuntPlugin.respawns = plugin.getConfig().getInt("runner-respawns");
		DeathListener.hunterDeathsInt = 0;
		DeathListener.runnerDeathsInt = 0;
		ManhuntCommand.runners.clear();
		ManhuntCommand.hunters.clear();
		runnerTeamSize = 0;
		hunterTeamSize = 0;
		playerManager.resetRewards();

		getInfoBoard().resetsScoreboard();
		setGameState(GameState.LOBBY);
		//Bukkit.getScheduler().cancelTasks(plugin);
	}


	public void cleanup() {
		setGameState(GameState.STOP);
	}

	public GameState getGameState() {
		return gameState;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public EMPListener getItemListener() {
		return EMPListener;
	}

	public InfoBoard getInfoBoard() {
		return infoBoard;
	}

	public CompassListener getCompassListener() {
		return compassListener;
	}


	//menus
	public MainMenu getMainMenu() {
		return mainMenu;
	}

	public KitsMenu getKitsMenu() {
		return kitsMenu;
	}

	public SettingMenu getSettingMenu() {
		return settingMenu;
	}

	public CompassMenu getCompassMenu() {
		return compassMenu;
	}

	public GameModeManager getGameModeManager() {
		return gameModeManager;
	}


	//Tasks
	public GameStartCountdownTask getGameStartCountdownTask() {
		return gameStartCountdownTask;
	}

	//Other

	public ManhuntPlugin getPlugin() {
		return this.plugin;
	}

	public ManhuntCommand getManhuntCommand() {
		return manhuntCommand;
	}

	public RespawnEffect getRespawnEffect() {
		return respawnEffect;
	}

	public WorldGeneration getWorldGeneration() {
		return worldGeneration;
	}
}
