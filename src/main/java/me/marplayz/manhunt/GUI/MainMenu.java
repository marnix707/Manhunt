package me.marplayz.manhunt.GUI;

import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.commands.ManhuntCommand;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.states.GameState;
import me.marplayz.manhunt.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

import static me.marplayz.manhunt.commands.ManhuntCommand.hunters;
import static me.marplayz.manhunt.commands.ManhuntCommand.runners;

public class MainMenu implements Listener {

	String prefix = ManhuntPlugin.prefix;

	private GameManager gameManager;

	public MainMenu(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public static String menuTitle = ChatColor.GOLD + "" + ChatColor.BOLD + "  ☆    "
			+ ChatColor.BLUE + ChatColor.BOLD + "Manhunt Menu" + ChatColor.GOLD + "" + ChatColor.BOLD + "    ☆ ";

	public void openMenu(Player p) {

		Inventory menuMain = Bukkit.createInventory(null, 54, menuTitle);

		//dark pane
		ItemStack darkPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta darkPaneMeta = darkPane.getItemMeta();

		darkPaneMeta.setDisplayName(ChatColor.GRAY + " ");
		darkPane.setItemMeta(darkPaneMeta);
		//green pane
		ItemStack greenPane = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);

		//Dragon head for runner
		ItemStack runnerItem = new ItemStack(Material.GOLDEN_BOOTS);
		runnerItem.addUnsafeEnchantment(Enchantment.LUCK, 1);
		ItemMeta runnerMeta = runnerItem.getItemMeta();

		/*Arrays.asList(ChatColor.GOLD + "Kill the Ender Dragon, but do not get killed yourself",*/
		runnerMeta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Click To Join");
		runnerMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		runnerMeta.setLore(runners);
		runnerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		runnerItem.setItemMeta(runnerMeta);

		//Crossbow for hunter
		ItemStack hunterItem = new ItemStack(Material.COMPASS);
		ItemMeta hunterMeta = hunterItem.getItemMeta();

		hunterMeta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Click To Join");
		hunterMeta.setLore(hunters);
		hunterItem.setItemMeta(hunterMeta);

		//Book for settings
		ItemStack bookItem = new ItemStack(Material.WRITABLE_BOOK);
		ItemMeta bookMeta = bookItem.getItemMeta();

		bookMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Settings");
		bookItem.setItemMeta(bookMeta);

		//Book for hunter info
		ItemStack hunterInfoItem = new ItemStack(Material.PAPER);
		ItemMeta hunterInfoItemMeta = hunterInfoItem.getItemMeta();

		hunterInfoItemMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Hunter");
		hunterInfoItemMeta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "----------------------------------------", ChatColor.GOLD + "    ✦  " + ChatColor.BLUE + "Hunt down the Runner using a tracker",
				ChatColor.GOLD + "    ✦  " + ChatColor.BLUE + "before the enderdragon is slain", ChatColor.DARK_GRAY + "----------------------------------------", ""));
		hunterInfoItem.setItemMeta(hunterInfoItemMeta);

		//Book for runner info
		ItemStack runnerInfoItem = new ItemStack(Material.PAPER);
		ItemMeta runnerInfoItemMeta = runnerInfoItem.getItemMeta();

		runnerInfoItemMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Runner");
		runnerInfoItemMeta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "----------------------------------------", ChatColor.GOLD + "    ✦  " + ChatColor.BLUE + "Defeat the enderdragon as soon",
				ChatColor.GOLD + "    ✦  " + ChatColor.BLUE + "as possible and don''t die", ChatColor.DARK_GRAY + "----------------------------------------", ""));
		runnerInfoItem.setItemMeta(runnerInfoItemMeta);

		//Green to start
		ItemStack greenItem = new ItemStack(Material.LIME_CONCRETE);
		ItemMeta greenMeta = greenItem.getItemMeta();

		greenMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Start");
		greenItem.setItemMeta(greenMeta);

		//Green to start not enough players
		ItemStack green0Item = new ItemStack(Material.BARRIER);
		ItemMeta green0Meta = green0Item.getItemMeta();

		green0Meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Start (Not enough players)");
		green0Item.setItemMeta(green0Meta);

		//Red to stop
		ItemStack redItem = new ItemStack(Material.RED_CONCRETE);
		ItemMeta redMeta = redItem.getItemMeta();

		redMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Stop");
		redItem.setItemMeta(redMeta);

		//Set items in menu
		if ((gameManager.runnerTeamSize > 0 && gameManager.hunterTeamSize > 0)) {
			menuMain.setItem(49, greenItem);
		} else {
			menuMain.setItem(49, green0Item);
		}

		menuMain.setItem(28, hunterItem);
		menuMain.setItem(34, runnerItem);
		menuMain.setItem(29, hunterInfoItem);
		menuMain.setItem(33, runnerInfoItem);
		if (p.hasPermission("manhunt.settings")) {
			menuMain.setItem(31, bookItem);
		} else {
			menuMain.setItem(31, darkPane);
		}
/*		if(gameManager.getGameState() != GameState.LOBBY) {
			menuMain.setItem(53, redItem);
		}*/

		GameModeMenu.placeGamemode(menuMain);

		greenPane.setItemMeta(darkPaneMeta);

		for (int i = 0; i < 54; i += 1) {

			if (menuMain.getItem(i) == null) {
				if (i > 4 && i<=8 || i == 17 || i == 26 || i == 35 || i == 44 || i == 41 || i == 32 || i == 23 || i >= 50 ) {
					menuMain.setItem(i, redPane);
				} else if (i < 4 || i == 9 || i == 18 || i == 27 || i == 36 || i == 39 || i == 30 || i == 21 || (i >= 45 && i <= 48)) {
					menuMain.setItem(i, greenPane);
				}else
				{
					menuMain.setItem(i, darkPane);
				}
			}
		}
		p.openInventory(menuMain);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		InventoryView menu = event.getView();

		//Check if menu, watch chatcolor
		if (menu.getTitle().equalsIgnoreCase(menuTitle)) {
			event.setCancelled(true);
			String joinedName = ChatColor.BLUE + " - " + p.getPlayer().getName();


			if (!(event.getCurrentItem() == null || p.getPlayer() == null)) {
				//joining hunters
				if (event.getCurrentItem().getType() == Material.COMPASS) {
					if (gameManager.getGameState() == GameState.ACTIVE) {
						p.sendMessage(prefix + ChatColor.RED + "Unable to join team, game in progress.");
						return;
					}
					p.sendMessage(prefix + ChatColor.AQUA + "You are now a hunter");
					//add to teams and scoreboard
					Team.getTeam("Hunter").add(p.getPlayer());
					if (!(ManhuntCommand.hunters.contains(joinedName))) {
						ManhuntCommand.hunters.add(joinedName);
						gameManager.hunterTeamSize += 1;
					}
					if (ManhuntCommand.runners.contains(joinedName)) {
						ManhuntCommand.runners.remove(joinedName);
						gameManager.runnerTeamSize -= 1;
					}
					gameManager.getMainMenu().openMenu(p.getPlayer());
					gameManager.getInfoBoard().updateScoreboard();
					//joining runners team
				} else if (event.getCurrentItem().getType() == Material.GOLDEN_BOOTS) {
					if (gameManager.getGameState() == GameState.ACTIVE) {
						p.sendMessage(prefix + ChatColor.RED + "Unable to join team, game in progress.");
						return;
					}
					p.sendMessage(prefix + ChatColor.AQUA + "You are now a Speedrunner");
					Team.getTeam("Runner").add(p.getPlayer());
					if (!(ManhuntCommand.runners.contains(joinedName))) {
						ManhuntCommand.runners.add(joinedName);
						gameManager.runnerTeamSize += 1;
					}
					if (ManhuntCommand.hunters.contains(joinedName)) {
						ManhuntCommand.hunters.remove(joinedName);
						gameManager.hunterTeamSize -= 1;
					}
					gameManager.getInfoBoard().updateScoreboard();
					gameManager.getMainMenu().openMenu(p.getPlayer());
				} else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "" + ChatColor.BOLD + "Start")) {
					p.performCommand("mh start");
					menu.close();
				} else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "" + ChatColor.BOLD + "Stop")) {
					p.performCommand("mh stop");
					gameManager.getMainMenu().openMenu(p.getPlayer());
				} else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Settings")) {
					gameManager.getSettingMenu().OpenSettings(p);
				}
			}
			//If settings is open
		}
	}
}
