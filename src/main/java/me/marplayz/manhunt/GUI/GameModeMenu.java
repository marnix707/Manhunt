package me.marplayz.manhunt.GUI;

import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.manager.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GameModeMenu implements Listener {

	private static ManhuntPlugin plugin;
	/*private static int modeSelected = 0;*/
	public int startCountdown;

	String prefix = ManhuntPlugin.prefix;
	private final GameManager gameManager;

	public GameModeMenu(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public static final String classicName = ChatColor.GOLD + "" + ChatColor.BOLD + "            Classic Mode" + ChatColor.RED + "" + ChatColor.BOLD + " [MASTER]";
	public static final String godName = ChatColor.GOLD + "" + ChatColor.BOLD + "            Godfather Mode" + ChatColor.RED + "" + ChatColor.BOLD + " [HARD]";
	public static final String enhancedName = ChatColor.GOLD + "" + ChatColor.BOLD + "            Enhanced Mode" + ChatColor.YELLOW + "" + ChatColor.BOLD + " [MEDIUM]";
	public static final String rapidName = ChatColor.GOLD + "" + ChatColor.BOLD + "            Rapid Mode" + ChatColor.GREEN + "" + ChatColor.BOLD + " [EASY]";
	public static final String creativeName = ChatColor.GOLD + "" + ChatColor.BOLD + "            Creative Mode" + ChatColor.GREEN + "" + ChatColor.BOLD + " [FUN]";

	public static void placeGamemode(Inventory menuMain) {
		//modes: Classic, Godfather, Enhanced, Rapid, Creative
		//classic
		ItemStack classic = SettingMenu.getHead("classic");
		ItemMeta classicMeta = classic.getItemMeta();
		classicMeta.setDisplayName(classicName);
		classicMeta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "----------------------------------------", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "All bonus features are off",
				ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Hunter cooldown is set to 0 seconds", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Compass cooldown is set to 0 seconds",
				ChatColor.DARK_GRAY + "----------------------------------------", ""));
		classic.setItemMeta(classicMeta);

		//godfather
		ItemStack godfather = SettingMenu.getHead("godfather");
		ItemMeta godfatherMeta = godfather.getItemMeta();
		godfatherMeta.setDisplayName(godName);
		godfatherMeta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "----------------------------------------", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Regional respawn is on",
				ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Distance check is on for the Speedrunner", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Hunter cooldown is set to 30 seconds",
				ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Compass cooldown is set to 15 seconds", ChatColor.DARK_GRAY + "----------------------------------------", ""));
		godfather.setItemMeta(godfatherMeta);

		//Enhanced
		ItemStack enhanced = SettingMenu.getHead("enhanced");
		ItemMeta enhancedMeta = enhanced.getItemMeta();
		enhancedMeta.setDisplayName(enhancedName);
		enhancedMeta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "----------------------------------------", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Regional and portal respawn is on.",
				ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Distance check is on for the Speedrunner", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Hunter cooldown is set to 30 seconds",
				ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Compass cooldown is set to 10 seconds", ChatColor.DARK_GRAY + "----------------------------------------", ""));
		enhanced.setItemMeta(enhancedMeta);

		//Rapid
		ItemStack rapid = SettingMenu.getHead("rapid");
		ItemMeta rapidMeta = rapid.getItemMeta();
		rapidMeta.setDisplayName(rapidName);
		rapidMeta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "----------------------------------------", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Regional and portal respawn is on.",
				ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Distance check is on for both.", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Portal compass is on",
				ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Hunter cooldown is set to 15 seconds", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Compass cooldown is set to 10 seconds", ChatColor.DARK_GRAY + "----------------------------------------", ""));
		rapid.setItemMeta(rapidMeta);

		//Creative
		ItemStack creative = SettingMenu.getHead("creative");
		ItemMeta creativeMeta = creative.getItemMeta();
		creativeMeta.setDisplayName(creativeName);
		creativeMeta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "----------------------------------------", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Regional and portal respawn is on.",
				ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Distance check is on for both.", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Portal compass is on", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Inventory keep is on",
				ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Runner rewards is on", ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Hunter cooldown is set to 15 seconds",
				ChatColor.GREEN + "    ✦  " + ChatColor.BLUE + "Compass cooldown is set to 10 seconds", ChatColor.DARK_GRAY + "----------------------------------------", ""));
		creative.setItemMeta(creativeMeta);

		menuMain.setItem(11, classic);
		menuMain.setItem(12, godfather);
		menuMain.setItem(13, enhanced);
		menuMain.setItem(14, rapid);
		menuMain.setItem(15, creative);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		InventoryView menu = event.getView();

		if (!menu.getTitle().equalsIgnoreCase(MainMenu.menuTitle)) {
			return;
		}
		event.setCancelled(true);
		if (event.getCurrentItem() == null || player.getPlayer() == null || event.getCurrentItem().getType() != Material.PLAYER_HEAD) {
			return;
		}
		if(!player.hasPermission("manhunt.settings")){
			player.sendMessage(prefix + "" + ChatColor.RED + "You can not do that!");
			return;
		}

		String itemClicked = event.getCurrentItem().getItemMeta().getDisplayName();

		if (itemClicked.equals(classicName)) {
			gameManager.getGameModeManager().setGameModeState(GameModeState.CLASSIC);
			gameManager.getMainMenu().openMenu(player);
			player.sendMessage(prefix + ChatColor.GOLD + "Classic Mode Enabled!");

		} else if (itemClicked.equals(godName)) {
			gameManager.getGameModeManager().setGameModeState(GameModeState.GODFATHER);
			gameManager.getMainMenu().openMenu(player);
			player.sendMessage(prefix + ChatColor.GOLD + "Godfather Mode Enabled!");

		} else if (itemClicked.equals(enhancedName)) {
			gameManager.getGameModeManager().setGameModeState(GameModeState.ENHANCED);
			gameManager.getMainMenu().openMenu(player);
			player.sendMessage(prefix + ChatColor.GOLD + "Enhanced Mode Enabled!");

		} else if (itemClicked.equals(rapidName)) {
			gameManager.getGameModeManager().setGameModeState(GameModeState.RAPID);
			gameManager.getMainMenu().openMenu(player);
			player.sendMessage(prefix + ChatColor.GOLD + "Rapid Mode Enabled!");

		} else if (itemClicked.equals(creativeName)) {
			gameManager.getGameModeManager().setGameModeState(GameModeState.CREATIVE);
			gameManager.getMainMenu().openMenu(player);
			player.sendMessage(prefix + ChatColor.GOLD + "Creative Mode Enabled!");
		}
	}
}
