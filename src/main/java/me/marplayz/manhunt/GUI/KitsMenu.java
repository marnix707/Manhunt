package me.marplayz.manhunt.GUI;

import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.ManhuntPlugin;
import org.bukkit.Bukkit;
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

public class KitsMenu implements Listener {

	private final GameManager gameManager;
	private ManhuntPlugin plugin;
	private final String prefix = ManhuntPlugin.prefix;

/*	public KitsMenu(ManhuntPlugin plugin) {
		this.plugin = plugin;
	}*/

	public KitsMenu(GameManager gameManager){this.gameManager = gameManager;}

	private final String menuKitsTitle = ChatColor.GOLD + "" + ChatColor.BOLD + "☆       "
			+ ChatColor.BLUE + ChatColor.BOLD + "Kits Settings" + ChatColor.GOLD + "" + ChatColor.BOLD + "      ☆ ";

	public ItemStack CreateKits(String name, Material material, int amount, String runnerConfig, String hunterConfig) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itemMeta = item.getItemMeta();

		itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Start With " + name);
		itemMeta.setLore(Arrays.asList("", ChatColor.BLUE + "Runner Starts with " + name + " " + ChatColor.GOLD + "" + ChatColor.BOLD + gameManager.getPlugin().getConfig().getString(runnerConfig).toUpperCase(),
				ChatColor.BLUE + "Hunter Starts with " + name + " " + ChatColor.GOLD + "" + ChatColor.BOLD + gameManager.getPlugin().getConfig().getString(hunterConfig).toUpperCase(), "",
				ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Left click to change runner",
				ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Right click to change hunter"));
		item.setItemMeta(itemMeta);
		return item;
	}

	public void openKitsSettings(Player p) {
		//Create inventory
		Inventory kitsSettingsInventory = Bukkit.createInventory(null, 36, menuKitsTitle);

		//Return book
		ItemStack book = new ItemStack(Material.BOOK);
		ItemMeta bookMeta = book.getItemMeta();

		bookMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Go back to settings");
		book.setItemMeta(bookMeta);

		//clear all kits
		ItemStack clearKits = new ItemStack(Material.BARRIER);
		ItemMeta clearKitsMeta = clearKits.getItemMeta();

		clearKitsMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Disable All Kits");
		clearKits.setItemMeta(clearKitsMeta);

		//instagram
		ItemStack insta = SettingMenu.getHead("instagram");
		ItemMeta instaMeta = insta.getItemMeta();
		instaMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "ADD US ON INSTAGRAM");
		insta.setItemMeta(instaMeta);

		//emp
		ItemStack emp = SettingMenu.getHead("emp");
		ItemMeta empMeta = emp.getItemMeta();
		empMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Start With EMP");
		empMeta.setLore(Arrays.asList("", ChatColor.BLUE + "Hunter Starts with EMP " + ChatColor.GOLD + "" + ChatColor.BOLD + gameManager.getPlugin().getConfig().getString("emp").toUpperCase(), "",
				ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Click to change"));
		emp.setItemMeta(empMeta);

		//Place all items in inventory
		kitsSettingsInventory.setItem(27, book);
		kitsSettingsInventory.setItem(35, clearKits);
		kitsSettingsInventory.setItem(31, insta);
		kitsSettingsInventory.setItem(10, CreateKits("Wooden Sword", Material.WOODEN_SWORD, 1, "wood-sword-runner", "wood-sword-hunter"));
		kitsSettingsInventory.setItem(11, CreateKits("Stone Pickaxe", Material.STONE_PICKAXE, 1, "stone-pick-runner", "stone-pick-hunter"));
		kitsSettingsInventory.setItem(12, CreateKits("10 Beef", Material.COOKED_BEEF, 10, "food-runner", "food-hunter"));
		kitsSettingsInventory.setItem(13, emp);

		//darken all
		ItemStack darkPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta darkPaneMeta = darkPane.getItemMeta();

		darkPaneMeta.setDisplayName(ChatColor.WHITE + " ");
		darkPane.setItemMeta(darkPaneMeta);

		for (int i = 0; i < 36; i++) {
			if (kitsSettingsInventory.getItem(i) == null) {
				kitsSettingsInventory.setItem(i, darkPane);
			}
		}
		p.openInventory(kitsSettingsInventory);
	}

	public void disableKits() {
		gameManager.getPlugin().getConfig().set("wood-sword-runner", "false");
		gameManager.getPlugin().getConfig().set("wood-sword-hunter", "false");
		gameManager.getPlugin().getConfig().set("stone-pick-runner", "false");
		gameManager.getPlugin().getConfig().set("stone-pick-hunter", "false");
		gameManager.getPlugin().getConfig().set("food-runner", "false");
		gameManager.getPlugin().getConfig().set("food-hunter", "false");
		gameManager.getPlugin().getConfig().set("emp", "false");
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		InventoryView menu = event.getView();

		if (menu.getTitle().equalsIgnoreCase(menuKitsTitle)) {
			event.setCancelled(true);
			if (!(event.getCurrentItem() == null || player.getPlayer() == null)) {

				Material itemClicked = event.getCurrentItem().getType();

				switch (itemClicked) {
					case BOOK:
						gameManager.getSettingMenu().OpenSettings(player);
						break;
					case BARRIER:
						//set all kits to false
						disableKits();

						gameManager.getPlugin().saveConfig();
						gameManager.getPlugin().reloadConfig();
						openKitsSettings(player);

						player.sendMessage(prefix + ChatColor.GOLD + "Disabled all kits.");
						break;

					case WOODEN_SWORD:
						//detect left click
						if (event.getClick().isLeftClick()) {
							if (gameManager.getPlugin().getConfig().getString("wood-sword-runner").equalsIgnoreCase("true")) {
								gameManager.getPlugin().getConfig().set("wood-sword-runner", "false");
							} else {
								gameManager.getPlugin().getConfig().set("wood-sword-runner", "true");
							}
							//detect right click
						} else {
							if (gameManager.getPlugin().getConfig().getString("wood-sword-hunter").equalsIgnoreCase("true")) {
								gameManager.getPlugin().getConfig().set("wood-sword-hunter", "false");
							} else {
								gameManager.getPlugin().getConfig().set("wood-sword-hunter", "true");
							}
						}
						gameManager.getPlugin().saveConfig();
						gameManager.getPlugin().reloadConfig();
						openKitsSettings(player);
						break;

					case STONE_PICKAXE:
						//detect left click
						if (event.getClick().isLeftClick()) {
							if (gameManager.getPlugin().getConfig().getString("stone-pick-runner").equalsIgnoreCase("true")) {
								gameManager.getPlugin().getConfig().set("stone-pick-runner", "false");
							} else {
								gameManager.getPlugin().getConfig().set("stone-pick-runner", "true");
							}
							//detect right click
						} else {
							if (gameManager.getPlugin().getConfig().getString("stone-pick-hunter").equalsIgnoreCase("true")) {
								gameManager.getPlugin().getConfig().set("stone-pick-hunter", "false");
							} else {
								gameManager.getPlugin().getConfig().set("stone-pick-hunter", "true");
							}
						}
						gameManager.getPlugin().saveConfig();
						gameManager.getPlugin().reloadConfig();
						openKitsSettings(player);
						break;

					case COOKED_BEEF:
						//detect left click
						if (event.getClick().isLeftClick()) {
							if (gameManager.getPlugin().getConfig().getString("food-runner").equalsIgnoreCase("true")) {
								gameManager.getPlugin().getConfig().set("food-runner", "false");
							} else {
								gameManager.getPlugin().getConfig().set("food-runner", "true");
							}
							//detect right click
						} else {
							if (gameManager.getPlugin().getConfig().getString("food-hunter").equalsIgnoreCase("true")) {
								gameManager.getPlugin().getConfig().set("food-hunter", "false");
							} else {
								gameManager.getPlugin().getConfig().set("food-hunter", "true");
							}
						}
						gameManager.getPlugin().saveConfig();
						gameManager.getPlugin().reloadConfig();
						openKitsSettings(player);
						break;
					case PLAYER_HEAD:
						if (gameManager.getPlugin().getConfig().getString("emp").equalsIgnoreCase("true")) {
							gameManager.getPlugin().getConfig().set("emp", "false");
						} else {
							gameManager.getPlugin().getConfig().set("emp", "true");
						}
						gameManager.getPlugin().saveConfig();
						gameManager.getPlugin().reloadConfig();
						openKitsSettings(player);
						break;
						//detect right click
				}
			}
		}
	}
}

