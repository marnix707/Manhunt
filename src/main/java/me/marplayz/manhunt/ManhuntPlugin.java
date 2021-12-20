package me.marplayz.manhunt;

import me.marplayz.manhunt.GUI.*;
import me.marplayz.manhunt.commands.ManhuntCommand;
import me.marplayz.manhunt.util.CustomConfigs;
import me.marplayz.manhunt.listeners.*;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.manager.PlayerManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class ManhuntPlugin extends JavaPlugin {

	public static String prefix;
	public static int respawns;
	public static int respawnsFinal;
	public static int startCountdownConfig;
	public int hunterCountdownConfig;
	public static int compassCooldownConfig;
	public static int distanceTimer;
	public static int empTimerConfig;

	private GameManager gameManager;
	private PlayerManager playerManager;

	public List<World> worlds;

	@Override
	public void onEnable() {
		super.onEnable();
		setupConfig();
		worlds = getServer().getWorlds();

		this.gameManager = new GameManager(this);

		//Command
		this.getCommand("Manhunt").setExecutor(new ManhuntCommand(gameManager));

		//Event listener
		getServer().getPluginManager().registerEvents(new LobbyListener(gameManager), this);
		getServer().getPluginManager().registerEvents(new SettingMenu(gameManager), this);
		getServer().getPluginManager().registerEvents(new MainMenu(gameManager), this);
		getServer().getPluginManager().registerEvents(new CompassMenu(gameManager), this);
		getServer().getPluginManager().registerEvents(new GameModeMenu(gameManager), this);
		getServer().getPluginManager().registerEvents(new KitsMenu(gameManager), this);
		getServer().getPluginManager().registerEvents(new EMPListener(gameManager), this);
		getServer().getPluginManager().registerEvents(new PortalListener(gameManager), this);
		getServer().getPluginManager().registerEvents(new CompassListener(gameManager), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(gameManager), this);
		getServer().getPluginManager().registerEvents(new DeathListener(gameManager), this);
		getServer().getPluginManager().registerEvents(new DragonListener(gameManager), this);


		for (Player player : Bukkit.getOnlinePlayers()) {
			gameManager.getInfoBoard().playerJoinScoreBoard(player);
		}

		//Disable insomnia
		worlds.get(0).setGameRule(GameRule.DO_INSOMNIA, false);

		/*gameManager.getManhuntCommand().setLore();*/


		getLogger().info(ChatColor.GREEN + "Manhunt " + getDescription().getVersion() + " has been loaded.");
	}

	@Override
	public void onDisable() {
		gameManager.cleanup();
		getLogger().info(ChatColor.RED + "Manhunt has been disabled.");
		saveDefaultConfig();
	}

	public void setupConfig() {
		this.getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		//load custom config
		CustomConfigs.setup();
		CustomConfigs.get().addDefault("start.runner.items", "WOODEN_AXE");
		CustomConfigs.get().addDefault("reward.runner.items", "BEEF;10");
		CustomConfigs.get().options().copyDefaults(true);
		CustomConfigs.saveCustomConfig();

		//initialize values
		prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));
		startCountdownConfig = getConfig().getInt("start-countdown");
		hunterCountdownConfig = getConfig().getInt("hunter-cooldown");
		compassCooldownConfig = getConfig().getInt("compass-cooldown-amount");
		empTimerConfig = getConfig().getInt("emp-timer");
		distanceTimer = getConfig().getInt("distance-timer");
		respawns = getConfig().getInt("runner-respawns");
		final int respawnsFinal = respawns;
	}
}
