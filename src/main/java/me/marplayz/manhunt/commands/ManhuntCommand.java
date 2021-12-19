package me.marplayz.manhunt.commands;

import me.marplayz.manhunt.util.InfoBoard;
import me.marplayz.manhunt.util.CustomConfigs;
import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.util.Team;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.manager.GameState;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

import static org.bukkit.Bukkit.broadcast;

public class ManhuntCommand implements CommandExecutor {

	private ManhuntPlugin plugin;
	private GameManager gameManager;
	private InfoBoard infoBoard;

	public ManhuntCommand(ManhuntPlugin plugin) {
		this.plugin = plugin;
	}

	public ManhuntCommand(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public ManhuntCommand(InfoBoard infoBoard) {
		this.infoBoard = infoBoard;
	}

	private int startCountdown;
	private int hunterCooldown;
	private int distanceMessage;

	public static ArrayList<String> hunters = new ArrayList<>();
	public static ArrayList<String> runners = new ArrayList<>();

	public void setLore() {
		hunters.add(0, "");
		hunters.add(1, ChatColor.DARK_GRAY + "  ◦  " + ChatColor.GOLD + "Hunt down the Speedrunner before");
		hunters.add(2, ChatColor.DARK_GRAY + "  ◦  " + ChatColor.GOLD + "the Ender Dragon is slain");
		hunters.add(3, "");
		hunters.add(4, ChatColor.YELLOW + "" + ChatColor.ITALIC + "  →  Click to join!");
		hunters.add(5, "");
		hunters.add(6, ChatColor.DARK_GRAY + "Players joined:");

		runners.add(0, "");
		runners.add(1, ChatColor.DARK_GRAY + "  ◦  " + ChatColor.GOLD + "Defeat the Ender Dragon, but");
		runners.add(2, ChatColor.DARK_GRAY + "  ◦  " + ChatColor.GOLD + "do not get yourself killed");
		runners.add(3, "");
		runners.add(4, ChatColor.YELLOW + "" + ChatColor.ITALIC + "  →  Click to join!");
		runners.add(5, "");
		runners.add(6, ChatColor.DARK_GRAY + "Players joined:");
	}

	String prefix = ManhuntPlugin.prefix;

	//bars
	BossBar barStartCountdown = Bukkit.createBossBar(ChatColor.GOLD + "" + ChatColor.BOLD + "MANHUNT STARTS IN", BarColor.PURPLE, BarStyle.SOLID);
	BossBar barHunterCountdown = Bukkit.createBossBar(ChatColor.GOLD + "" + ChatColor.BOLD + "HUNTERS LEAVE IN", BarColor.RED, BarStyle.SOLID);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Create teams
		new Team("Hunter");
		new Team("Runner");

		String addHunter = "Hadd";
		String addRunner = "Radd";

		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(prefix + ChatColor.RED + "You must be a player, otherwise use /Manhunt Info.");
				return true;
			} else {
				Player p = (Player) sender;
				gameManager.getMainMenu().openMenu(p);
			}
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase(addHunter) || args[0].equalsIgnoreCase(addRunner)) {
				sender.sendMessage(prefix + ChatColor.RED + "You did not specify a player!");
				return true;

			} else if (args[0].equalsIgnoreCase("start")) {
				String forceStart = gameManager.getPlugin().getConfig().getString("force-start");
				if ((gameManager.hunterTeamSize > 0 && gameManager.runnerTeamSize > 0) || forceStart.equalsIgnoreCase("true")) {
					gameManager.setGameState(GameState.STARTING);
				} else {
					sender.sendMessage(prefix + ChatColor.RED + "Not enough players in order to start.");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("info")) {
				messageInfo(sender);
				return true;
			} else if (args[0].equalsIgnoreCase("kits")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(prefix + ChatColor.RED + "You must be a player, otherwise use /Manhunt Info.");
					return true;
				}
				Player player = (Player) sender;
				return true;

			} else if (args[0].equalsIgnoreCase("reload")) {
				if (gameManager.getGameState() == GameState.ACTIVE) {
					sender.sendMessage(prefix + ChatColor.RED + "Game in progress, please stop the game first.");
					return true;
				}
				gameManager.getPlugin().reloadConfig();
				gameManager.getPlugin().saveDefaultConfig();

				CustomConfigs.reloadConfigs();

				ManhuntPlugin.respawns = gameManager.getPlugin().getConfig().getInt("runner-respawns");

				gameManager.getInfoBoard().updateScoreboard();
				gameManager.setGameState(GameState.STOP);
				sender.sendMessage(prefix + ChatColor.GOLD + "Manhunt config has been reloaded.");
				return true;

			} else if (args[0].equalsIgnoreCase("stop")) {
				gameManager.setGameState(GameState.STOP);
				return true;
			} else if (args[0].equalsIgnoreCase("target")) {
				if(sender instanceof Player){
					Player player = (Player) sender;
					gameManager.getCompassMenu().openCompassMenu(player);
				}
				return true;
			} else {
				unknownCommand(sender);
				return true;
			}
		} else if (args.length == 2) {

			if (args[0].equalsIgnoreCase("distance")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(prefix + ChatColor.RED + "You must be a player, otherwise use /Manhunt Info.");
					return true;
				}
				Location senderLoc = ((Player) sender).getLocation();
				try {
					Location otherLoc = Bukkit.getPlayer(args[1]).getLocation();
					sender.sendMessage(String.valueOf((int) (senderLoc.distance(otherLoc))));
				} catch (NullPointerException e) {
					sender.sendMessage(prefix + ChatColor.RED + "Player " + ChatColor.YELLOW + "" + ChatColor.ITALIC + args[1] + ChatColor.RED + " not found!");
				}
				return true;
			}

			String newPlayer = args[1];
			Player onlinePlayer = Bukkit.getPlayer(newPlayer);
			String joinedName = ChatColor.DARK_GRAY + " - " + onlinePlayer.getPlayer().getName();
			//check for real player
			if ((args[0].equalsIgnoreCase(addHunter) || args[0].equalsIgnoreCase(addRunner)) && Bukkit.getPlayerExact(newPlayer) == null) {
				sender.sendMessage(prefix + ChatColor.RED + "Player ''" + newPlayer + "'' not found/online.");
				return true;

				//add player to hunter
			} else if (args[0].equalsIgnoreCase(addHunter)) {
				try {
					if (Team.getTeam(onlinePlayer).getName().equalsIgnoreCase("Hunter")) {
						sender.sendMessage(prefix + ChatColor.RED + onlinePlayer.getDisplayName() + " is already a hunter.");
						return true;
					}
				} catch (NullPointerException e){
					sender.sendMessage(e.toString());
				}

				Bukkit.broadcastMessage(prefix + ChatColor.AQUA + newPlayer + " is now a Hunter");
				Team.getTeam("Hunter").add(onlinePlayer);
				hunters.add(ChatColor.DARK_GRAY + " - " + newPlayer);
				gameManager.hunterTeamSize += 1;
				if(runners.contains(joinedName)){
					runners.remove(joinedName);
					gameManager.runnerTeamSize -= 1;
				}
				gameManager.getInfoBoard().updateScoreboard();
				return true;

			} else if (args[0].equalsIgnoreCase(addRunner)) {
				try {
					if(Objects.requireNonNull(Team.getTeam(onlinePlayer)).getName().equalsIgnoreCase("Runner")) {
						sender.sendMessage(prefix + ChatColor.RED + onlinePlayer.getName() + " is already a runner.");
						return true;
					}
				} catch (NullPointerException e) {
					sender.sendMessage(e.toString());
				}

				Bukkit.broadcastMessage(prefix + ChatColor.AQUA + newPlayer + " is now a Speedrunner");
				Bukkit.getPlayer(newPlayer).sendMessage(prefix + ChatColor.AQUA + "You are now a runner");
				Team.getTeam("Runner").add(onlinePlayer);
				runners.add(ChatColor.DARK_GRAY + " - " + newPlayer);
				gameManager.runnerTeamSize += 1;
				if(hunters.contains(joinedName)){
					hunters.remove(joinedName);
					gameManager.hunterTeamSize -= 1;
				}
				gameManager.getInfoBoard().updateScoreboard();
				return true;

			} else {
				unknownCommand(sender);
				return true;
			}

		} else {
			unknownCommand(sender);
			return true;
		}
		return true;
	}

	private void messageInfo(CommandSender sender) {
		sender.sendMessage(ChatColor.AQUA + " --- Manhunt V" + gameManager.getPlugin().getDescription().getVersion() + " Made By: " +
				ChatColor.LIGHT_PURPLE + gameManager.getPlugin().getDescription().getAuthors() + ChatColor.AQUA + " --- ");
		sender.sendMessage(ChatColor.AQUA + "/Manhunt " + ChatColor.GRAY + "  Open menu");
		sender.sendMessage(ChatColor.AQUA + "/Manhunt Info " + ChatColor.GRAY + "  Show commands");
		sender.sendMessage(ChatColor.AQUA + "/Manhunt Hadd" + ChatColor.GRAY + "  Add player to hunters");
		sender.sendMessage(ChatColor.AQUA + "/Manhunt Radd" + ChatColor.GRAY + "  Add player to Speedrunners");
		sender.sendMessage(ChatColor.AQUA + "/Manhunt Start" + ChatColor.GRAY + "  Starts the Manhunt");
		sender.sendMessage(ChatColor.AQUA + "/Manhunt Stop" + ChatColor.GRAY + "  Stops the Manhunt");
		sender.sendMessage(ChatColor.AQUA + "/Manhunt Reload" + ChatColor.GRAY + "  Reloads the config");
	}

	private void unknownCommand(CommandSender sender) {
		sender.sendMessage(prefix + ChatColor.RED + "Unknown command. Please use /Manhunt Info.");
	}
}
