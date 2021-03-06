package me.marplayz.manhunt.GUI;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.util.CustomConfigs;
import me.marplayz.manhunt.util.InfoBoard;
import me.marplayz.manhunt.util.Heads;
import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.listeners.DeathListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

public class SettingMenu implements Listener {

	private static ManhuntPlugin plugin;
	private GameManager gameManager;

	String prefix = ManhuntPlugin.prefix;


	public SettingMenu(ManhuntPlugin plugin) {
		SettingMenu.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public SettingMenu(InfoBoard infoBoard){
	}

	public SettingMenu(GameManager gameManager){this.gameManager = gameManager;}

	//menu titles
	private static final String menuSettingsTitle = ChatColor.GOLD + "" + ChatColor.BOLD + "☆    "
			+ ChatColor.BLUE + ChatColor.BOLD + "Manhunt Settings" + ChatColor.GOLD + "" + ChatColor.BOLD + "     ☆ ";
	String menuCooldownTitle = ChatColor.GOLD + "" + ChatColor.BOLD + "☆    "
			+ ChatColor.BLUE + ChatColor.BOLD + "Cooldown Settings" + ChatColor.GOLD + "" + ChatColor.BOLD + "    ☆ ";
	String menuDistanceTitle = ChatColor.GOLD + "" + ChatColor.BOLD + "☆    "
			+ ChatColor.BLUE + ChatColor.BOLD + "Distance Settings" + ChatColor.GOLD + "" + ChatColor.BOLD + "    ☆ ";
	;
	String menuCompassTitle = ChatColor.GOLD + "" + ChatColor.BOLD + "☆    "
			+ ChatColor.BLUE + ChatColor.BOLD + "Tracker Settings" + ChatColor.GOLD + "" + ChatColor.BOLD + "    ☆ ";

	String respawnMenuTitle = ChatColor.GOLD + "" + ChatColor.BOLD + "☆"
			+ ChatColor.BLUE + ChatColor.BOLD + "SpeedRunner Lives" + ChatColor.GOLD + "" + ChatColor.BOLD + "☆ ";


	//create skull
	public static ItemStack getHead(String name) {
		for (Heads head : Heads.values()) {
			if (head.getName().equals(name)) {
				return head.getItemStack();
			}
		}
		return null;
	}

	public static ItemStack createSkull(String url, String name) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
		if (url.isEmpty()) return head;

		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);

		profile.getProperties().put("textures", new Property("textures", url));

		try {
			Field profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException error) {
			error.printStackTrace();
		}
		head.setItemMeta(headMeta);
		return head;
	}
	//end create skull

	//Settings Inventory
	public void OpenSettings(Player p) {
		Inventory menuSettings = Bukkit.createInventory(null, 54, menuSettingsTitle);

		//Hunter cooldown
		ItemStack clock = new ItemStack(Material.CLOCK);
		ItemMeta clockMeta = clock.getItemMeta();

		clockMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Hunter Cooldown");
		clockMeta.setLore(Arrays.asList("",ChatColor.GOLD +"" +ChatColor.ITALIC + "   ⌚ "+ ChatColor.GRAY + ""+ ChatColor.ITALIC + ": "+ gameManager.getPlugin().getConfig().getString("hunter-cooldown")
				+ ChatColor.GRAY + ""+ ChatColor.ITALIC + " second(s)"));

		clock.setItemMeta(clockMeta);

		//book
		ItemStack book = new ItemStack(Material.BOOK);
		ItemMeta bookMeta = book.getItemMeta();

		bookMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Go back to Menu");
		book.setItemMeta(bookMeta);

		//kits menu
		ItemStack kitsMenu = new ItemStack(Material.LEATHER_CHESTPLATE);
		kitsMenu.addUnsafeEnchantment(Enchantment.LUCK, 1);
		ItemMeta kitsMenuMeta = kitsMenu.getItemMeta();

		kitsMenuMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Starting Kits");
		kitsMenuMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		kitsMenu.setItemMeta(kitsMenuMeta);

		//compass portal
		ItemStack pearl = new ItemStack(Material.OBSIDIAN);
		ItemMeta pearlMeta = pearl.getItemMeta();

		pearlMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Portal Tracking " + ChatColor.RED + "" + ChatColor.BOLD + "Off");
		pearlMeta.setLore(Arrays.asList("", ChatColor.BLUE + "Make the tracker point to the",
				ChatColor.BLUE + "portal the runner left in. "));
		pearl.setItemMeta(pearlMeta);

		ItemStack ender = new ItemStack(Material.CRYING_OBSIDIAN);
		ItemMeta enderMeta = pearl.getItemMeta();

		enderMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Portal Tracking "+ ChatColor.GREEN + "" + ChatColor.BOLD + "On");
		ender.setItemMeta(enderMeta);

		String compassPortal = gameManager.getPlugin().getConfig().getString("compass-portal");

		//compass cooldown
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta compassMeta = compass.getItemMeta();

		if (gameManager.getPlugin().getConfig().getString("compass-cooldown").equalsIgnoreCase("true")) {
			compassMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ("Tracker cooldown ") + ChatColor.GREEN + ChatColor.BOLD + "On");
			compassMeta.setLore(Arrays.asList("",ChatColor.GOLD +"" +ChatColor.ITALIC + "   ⌚ "+ ChatColor.GRAY + ""+ ChatColor.ITALIC + ": "+ gameManager.getPlugin().getConfig().getString("compass-cooldown-amount")
					+ ChatColor.GRAY + ""+ ChatColor.ITALIC + " second(s)"));
		} else {
			compassMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ("Tracker cooldown ") + ChatColor.RED + ChatColor.BOLD + "Off");
		}
		compass.setItemMeta(compassMeta);

		//Distance check
		ItemStack distanceItem = new ItemStack(Material.SPYGLASS);
		ItemMeta distanceMeta = distanceItem.getItemMeta();

		distanceMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ("Distance Check ") + ChatColor.RED + ChatColor.BOLD + "Off");
		distanceItem.setItemMeta(distanceMeta);

		String distanceCheck = gameManager.getPlugin().getConfig().getString("distance-check");

		//rewards
		ItemStack reward = SettingMenu.getHead("present");
		ItemMeta rewardMeta = reward.getItemMeta();

		String rewardConfig = gameManager.getPlugin().getConfig().getString("runner-kill-reward");

		rewardMeta.setLore(CustomConfigs.get().getStringList("reward.runner.items"));

				/*Arrays.asList("", ChatColor.GOLD + "" + ChatColor.BOLD + "   ♚ Rewards Set ♚",
				ChatColor.GRAY + "" + ChatColor.ITALIC + "1x - " + ChatColor.BLUE + "Get a Heal",
				ChatColor.GRAY + "" + ChatColor.ITALIC + "2x - " + ChatColor.BLUE + "Get 10 Beef",
				ChatColor.GRAY + "" + ChatColor.ITALIC + "3x - " + ChatColor.BLUE + "Get a Bucket",
				ChatColor.GRAY + "" + ChatColor.ITALIC + "4x - " + ChatColor.BLUE + "Get a Golden Apple"));*/

		//Reward check
		if (rewardConfig.equalsIgnoreCase("true")) {
			rewardMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ("Runner Rewards ") + ChatColor.GREEN + ChatColor.BOLD + "On");
		} else {
			rewardMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ("Runner Rewards ") + ChatColor.RED + ChatColor.BOLD + "Off");
		}
		reward.setItemMeta(rewardMeta);

		//regional spawn
		ItemStack redBed = new ItemStack(Material.RED_BED);
		ItemMeta redBedMeta = redBed.getItemMeta();

		ItemStack greenBed = new ItemStack(Material.GREEN_BED);
		ItemMeta greenBedMeta = greenBed.getItemMeta();

		redBedMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Regional Respawn " + ChatColor.RED + ChatColor.BOLD + "Off");
		greenBedMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Regional Respawn " + ChatColor.GREEN + ChatColor.BOLD + "On");
		greenBedMeta.setLore(Arrays.asList("", ChatColor.BLUE + "Portal respawn is set to " + ChatColor.BLUE + gameManager.getPlugin().getConfig().getString("regional-portal-respawn"), "",
				ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Left click to change respawn, right",
				ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "click to change portal respawn "));

		redBed.setItemMeta(redBedMeta);
		greenBed.setItemMeta(greenBedMeta);

		String regionalRespawn = gameManager.getPlugin().getConfig().getString("regional-respawn");

		//inventory keep
		ItemStack chest = new ItemStack(Material.CHEST);
		ItemMeta chestMeta = greenBed.getItemMeta();

		chestMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Keep Inventory");
		chestMeta.setLore(Arrays.asList("", ChatColor.BLUE + "Runner Inventory Keep set to " + ChatColor.GOLD + "" + ChatColor.BOLD + gameManager.getPlugin().getConfig().getString("runner-inv-keep").toUpperCase(),
				ChatColor.BLUE + "Hunter Inventory Keep set to " + ChatColor.GOLD + "" + ChatColor.BOLD + gameManager.getPlugin().getConfig().getString("hunter-inv-keep").toUpperCase(), "",
				ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Left click to change runner",
				ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Right click to change hunter"));

		chest.setItemMeta(chestMeta);

		//Life system
		ItemStack rewspawnItem = new ItemStack(Material.TOTEM_OF_UNDYING);
		ItemMeta rewspawnMeta = rewspawnItem.getItemMeta();

		rewspawnMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Runner Lives");
		rewspawnMeta.setLore(Arrays.asList( "",	ChatColor.RED + "" + ChatColor.ITALIC +  "   ❤ " + ChatColor.GRAY + ""+ ChatColor.ITALIC + ": "+  gameManager.getPlugin().getConfig().getString("runner-respawns")
						+ ChatColor.GRAY + "" + ChatColor.ITALIC+ "x"));
		rewspawnItem.setItemMeta(rewspawnMeta);

		//place items
		menuSettings.setItem(1, compass);
		menuSettings.setItem(5, clock);
		menuSettings.setItem(7, rewspawnItem);
		menuSettings.setItem(23, reward);
		menuSettings.setItem(25, kitsMenu);
		menuSettings.setItem(37, chest);
		menuSettings.setItem(53, book);


		if (distanceCheck.equalsIgnoreCase("true")) {
			distanceMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ("Distance Check ") + ChatColor.GREEN + ChatColor.BOLD + "On");
			distanceMeta.setLore(Arrays.asList("", ChatColor.BLUE + "Runner Distance Check set to " + ChatColor.GOLD + "" + ChatColor.BOLD + gameManager.getPlugin().getConfig().getString("distance-check").toUpperCase(),
					ChatColor.BLUE + "Hunter Distance Check set to " + ChatColor.GOLD + "" + ChatColor.BOLD + gameManager.getPlugin().getConfig().getString("distance-check-hunter").toUpperCase(), "",
					ChatColor.GOLD + "" + ChatColor.ITALIC +  "   ⌚ " + ChatColor.GRAY + "" + ChatColor.ITALIC + ": " + gameManager.getPlugin().getConfig().getString("distance-timer")
							+ ChatColor.GRAY + "" + ChatColor.ITALIC+ " minute(s)"));

			distanceItem.setItemMeta(distanceMeta);
			menuSettings.setItem(3, distanceItem);
		} else {
			distanceMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Distance Check Off");
			distanceMeta.setLore(null);
			distanceItem.setItemMeta(distanceMeta);
			menuSettings.setItem(3, distanceItem);
		}

		if (regionalRespawn.equalsIgnoreCase("true")) {
			menuSettings.setItem(21, greenBed)
			;
		} else {
			menuSettings.setItem(21, redBed);
		}

		if (compassPortal.equalsIgnoreCase("true")) {
			menuSettings.setItem(19, ender);
		} else {
			menuSettings.setItem(19, pearl);
		}

		//fill with glass pane

		ItemStack darkPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta darkPaneMeta = darkPane.getItemMeta();

		darkPaneMeta.setDisplayName(ChatColor.GRAY + " ");
		darkPane.setItemMeta(darkPaneMeta);

		for (int i = 0; i < 54; i++) {
			if (menuSettings.getItem(i) == null) {
				menuSettings.setItem(i, darkPane);
			}
		}
		p.openInventory(menuSettings);

	}

	public void OpenCooldownSettings(Player p) {
		Inventory cooldownSettings = Bukkit.createInventory(null, 36, menuCooldownTitle);

		//Return book
		ItemStack book = new ItemStack(Material.BOOK);
		ItemMeta bookMeta = book.getItemMeta();

		bookMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Go back to settings");
		book.setItemMeta(bookMeta);
		//compass

		ItemStack cooldownItem = new ItemStack(Material.PAPER);
		ItemMeta cooldownMeta = cooldownItem.getItemMeta();

		cooldownMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Hunter Cooldown");
		cooldownMeta.setLore(Arrays.asList("" + ChatColor.BLUE + "Amount of seconds the hunter has to wait before starting ",
				ChatColor.BLUE + "Set to " + ChatColor.BLUE + gameManager.getPlugin().getConfig().getString("hunter-cooldown") + ChatColor.BLUE + " seconds"));
		cooldownItem.setItemMeta(cooldownMeta);

		cooldownSettings.setItem(35, book);
		cooldownSettings.setItem(0, cooldownItem);


		for (int i = 0; i < 18; i++) {
			ItemStack sec = new ItemStack(Material.EMERALD);
			ItemMeta secMeta = sec.getItemMeta();
			secMeta.setDisplayName(i * 5 + "");
			sec.setItemMeta(secMeta);
			cooldownSettings.setItem(i + 9, sec);
		}

		ItemStack darkPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta darkPaneMeta = darkPane.getItemMeta();

		darkPaneMeta.setDisplayName(ChatColor.GRAY + " ");
		darkPane.setItemMeta(darkPaneMeta);

		for (int i = 0; i < 36; i++) {
			if (cooldownSettings.getItem(i) == null) {
				cooldownSettings.setItem(i, darkPane);
			}
		}
		p.openInventory(cooldownSettings);
	}

	public void OpenDistanceSettings(Player p) {
		Inventory distanceSettings = Bukkit.createInventory(null, 36, menuDistanceTitle);

		//Return book
		ItemStack book = new ItemStack(Material.BOOK);
		ItemMeta bookMeta = book.getItemMeta();

		bookMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Go back to settings");
		book.setItemMeta(bookMeta);

		//stick and rod item
		ItemStack distanceItem = new ItemStack(Material.PAPER);
		ItemMeta distanceMeta = distanceItem.getItemMeta();


		//Runner and hunter item
		ItemStack runnerCheck = new ItemStack(Material.DRAGON_HEAD);
		ItemMeta runnerCheckMeta = runnerCheck.getItemMeta();

		ItemStack hunterCheck = new ItemStack(Material.CROSSBOW);
		ItemMeta hunterCheckMeta = hunterCheck.getItemMeta();

		String distanceCheck = gameManager.getPlugin().getConfig().getString("distance-check");
		String distanceCheckHunter = gameManager.getPlugin().getConfig().getString("distance-check-hunter");

		//Runner name check
		if (distanceCheck.equalsIgnoreCase("true")) {
			runnerCheckMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Runner Distance Check On");
			distanceMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Distance Check On");
			distanceMeta.setLore(Arrays.asList(ChatColor.BLUE + "The Speedrunner will get the absolute distance",
					ChatColor.BLUE + "between him and the hunter every interval", "",

					ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Distance check is set to " + ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + gameManager.getPlugin().getConfig().getString("distance-timer")
							+ ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + " minutes"));
			distanceItem.setItemMeta(distanceMeta);

		} else {
			runnerCheckMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Runner Distance Check Off");

			distanceMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Distance Check Off");
			distanceMeta.setLore(Arrays.asList(ChatColor.BLUE + "The Speedrunner will get the absolute distance",
					ChatColor.BLUE + "between him and the hunter every interval", "",

					ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Distance check is set to " + "" + ChatColor.DARK_GRAY + "" + ChatColor.ITALIC +
							gameManager.getPlugin().getConfig().getString("distance-timer") + ChatColor.DARK_GRAY + " minutes"));
			distanceItem.setItemMeta(distanceMeta);
		}


		//Hunter name check
		if (distanceCheckHunter.equalsIgnoreCase("true")) {
			hunterCheckMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Hunter Distance Check On");
		} else {
			hunterCheckMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Hunter Distance Check Off");
		}
		runnerCheck.setItemMeta(runnerCheckMeta);
		hunterCheck.setItemMeta(hunterCheckMeta);

		distanceSettings.setItem(35, book);
		distanceSettings.setItem(0, distanceItem);
		distanceSettings.setItem(4, hunterCheck);
		distanceSettings.setItem(5, runnerCheck);

		for (int i = 0; i < 18; i++) {
			ItemStack sec = new ItemStack(Material.EMERALD);
			ItemMeta secMeta = sec.getItemMeta();
			secMeta.setDisplayName(i + 1 + "");
			sec.setItemMeta(secMeta);
			distanceSettings.setItem(i + 9, sec);
		}

		//darken all
		ItemStack darkPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta darkPaneMeta = darkPane.getItemMeta();

		darkPaneMeta.setDisplayName(ChatColor.WHITE + " ");
		darkPane.setItemMeta(darkPaneMeta);

		for (int i = 0; i < 36; i++) {
			if (distanceSettings.getItem(i) == null) {
				distanceSettings.setItem(i, darkPane);
			}
		}
		p.openInventory(distanceSettings);
	}

	public void OpenCompassSettings(Player p) {
		Inventory compassSettings = Bukkit.createInventory(null, 36, menuCompassTitle);

		//Return book
		ItemStack book = new ItemStack(Material.BOOK);
		ItemMeta bookMeta = book.getItemMeta();

		bookMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Go back to settings");
		book.setItemMeta(bookMeta);


		//Compass
		ItemStack compass = new ItemStack(Material.PAPER);
		ItemMeta compassMeta = compass.getItemMeta();

		compassMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Tracker Cooldown");
		compassMeta.setLore(Arrays.asList(ChatColor.BLUE + "Be able to set a cooldown before using",
				ChatColor.BLUE + "the tracker again to ensure a more", ChatColor.BLUE + "fair game for the Speedrunner"));
		compass.setItemMeta(compassMeta);

		//compass cooldown on or off
		ItemStack compassOff = new ItemStack(Material.RED_CONCRETE);
		ItemMeta compassOffMeta = compassOff.getItemMeta();

		compassOffMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ("Tracker cooldown ") + ChatColor.RED + ChatColor.BOLD + "Off");
		compassOff.setItemMeta(compassOffMeta);

		ItemStack compassOn = new ItemStack(Material.LIME_CONCRETE);
		ItemMeta compassOnMeta = compassOn.getItemMeta();

		compassOnMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ("Tracker cooldown ") + ChatColor.GREEN + ChatColor.BOLD + "On");
		compassOnMeta.setLore(Arrays.asList(ChatColor.BLUE + "Tracker cooldown is set to ",
				ChatColor.BLUE + gameManager.getPlugin().getConfig().getString("compass-cooldown-amount") + ChatColor.BLUE + " seconds"));
		compassOn.setItemMeta(compassOnMeta);

		String compassCooldown = gameManager.getPlugin().getConfig().getString("compass-cooldown");
		if (compassCooldown.equalsIgnoreCase("true")) {
			compassSettings.setItem(4, compassOn);
		} else {
			compassSettings.setItem(4, compassOff);
		}

		compassSettings.setItem(35, book);
		compassSettings.setItem(0, compass);

		for (int i = 1; i < 19; i++) {
			ItemStack sec = new ItemStack(Material.EMERALD);
			ItemMeta secMeta = sec.getItemMeta();
			secMeta.setDisplayName(i * 5 + "");
			sec.setItemMeta(secMeta);
			compassSettings.setItem(i + 8, sec);
		}

		//darken all
		ItemStack darkPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta darkPaneMeta = darkPane.getItemMeta();

		darkPaneMeta.setDisplayName(ChatColor.WHITE + " ");
		darkPane.setItemMeta(darkPaneMeta);

		for (int i = 0; i < 36; i++) {
			if (compassSettings.getItem(i) == null) {
				compassSettings.setItem(i, darkPane);
			}
		}
		p.openInventory(compassSettings);
	}

	private void OpenRespawnMenu(Player p) {
		Inventory respawnSettings = Bukkit.createInventory(null, 36, respawnMenuTitle);

		//Return book
		ItemStack book = new ItemStack(Material.BOOK);
		ItemMeta bookMeta = book.getItemMeta();

		bookMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Go back to settings");
		book.setItemMeta(bookMeta);

		//Fill with lives
		ItemStack liveNotActive = new ItemStack(Material.GRAY_DYE);
		ItemStack liveActive = new ItemStack(Material.LIME_DYE);
		ItemMeta liveMeta = liveNotActive.getItemMeta();
		liveMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "0");
		liveNotActive.setItemMeta(liveMeta);

		String heart = "❤";
		StringBuilder heart_bfr = new StringBuilder();

		for (int k = 1; k < 8; k++) {
			heart_bfr.append(heart);
			liveMeta.setDisplayName(String.valueOf(k) + "" + ChatColor.ITALIC + "x - " + ChatColor.RESET + "" + ChatColor.RED + heart_bfr.toString());
			liveNotActive.setItemMeta(liveMeta);
			liveActive.setItemMeta(liveMeta);

			if (k == gameManager.getPlugin().getConfig().getInt("runner-respawns")) {
				respawnSettings.setItem(k + 9, liveActive);
			} else {
				respawnSettings.setItem(k + 9, liveNotActive);
			}
		}

		respawnSettings.setItem(35, book);

		//darken all
		ItemStack darkPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta darkPaneMeta = darkPane.getItemMeta();
		darkPaneMeta.setDisplayName(ChatColor.WHITE + " ");
		darkPane.setItemMeta(darkPaneMeta);

		for (int i = 0; i < 36; i++) {
			if (respawnSettings.getItem(i) == null) {
				respawnSettings.setItem(i, darkPane);
			}
		}

		p.openInventory(respawnSettings);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		InventoryView menu = event.getView();

		//Check if settings menu, watch chatcolor
		if (menu.getTitle().equalsIgnoreCase(menuSettingsTitle)) {
			event.setCancelled(true);
			if (!(event.getCurrentItem() == null || p.getPlayer() == null)) {
				if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Go back to Menu")) {
					p.performCommand("mh");
					//force start
				} else if (event.getCurrentItem().getType() == Material.OBSIDIAN) {
					gameManager.getPlugin().getConfig().set("compass-portal", "true");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 1);
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					OpenSettings(p);
				} else if (event.getCurrentItem().getType() == Material.CRYING_OBSIDIAN) {
					gameManager.getPlugin().getConfig().set("compass-portal", "false");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 0);
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					OpenSettings(p);
					//regional respawn
				} else if (event.getCurrentItem().getType() == Material.RED_BED) {
					gameManager.getPlugin().getConfig().set("regional-respawn", "true");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 1);
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					OpenSettings(p);
				} else if (event.getCurrentItem().getType() == SettingMenu.getHead("present").getType()) {
					if (gameManager.getPlugin().getConfig().getString("runner-kill-reward").equalsIgnoreCase("true")) {
						gameManager.getPlugin().getConfig().set("runner-kill-reward", "false");
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 0);
					} else {
						gameManager.getPlugin().getConfig().set("runner-kill-reward", "true");
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 1);
					}
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					OpenSettings(p);
				} else if (event.getCurrentItem().getType() == Material.GREEN_BED) {
					if (event.getClick().isLeftClick()) {
						gameManager.getPlugin().getConfig().set("regional-respawn", "false");
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 0);
						//detect right click
					} else {
						if (gameManager.getPlugin().getConfig().get("regional-portal-respawn").equals("true")) {
							gameManager.getPlugin().getConfig().set("regional-portal-respawn", "false");
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 0);
						} else {
							gameManager.getPlugin().getConfig().set("regional-portal-respawn", "true");
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 1);
						}
					}
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					OpenSettings(p);
				} else if (event.getCurrentItem().getType() == Material.CHEST) {
					String runnerKeep = gameManager.getPlugin().getConfig().get("runner-inv-keep").toString();
					String hunterKeep = gameManager.getPlugin().getConfig().get("hunter-inv-keep").toString();
					//detect left click
					if (event.getClick().isLeftClick()) {
						if (runnerKeep.equalsIgnoreCase("true")) {
							gameManager.getPlugin().getConfig().set("runner-inv-keep", "false");
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 0);
						} else {
							gameManager.getPlugin().getConfig().set("runner-inv-keep", "true");
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 1);
						}
						//detect right click
					} else {
						if (hunterKeep.equalsIgnoreCase("true")) {
							gameManager.getPlugin().getConfig().set("hunter-inv-keep", "false");
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 0);
						} else {
							gameManager.getPlugin().getConfig().set("hunter-inv-keep", "true");
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 1);
						}
					}
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					OpenSettings(p);
					//compass cooldown
				} else if (event.getCurrentItem().getType() == Material.COMPASS) {
					OpenCompassSettings(p);
					//cooldown
				} else if (event.getCurrentItem().getType() == Material.CLOCK) {
					OpenCooldownSettings(p);
					//kits settings
				} else if (event.getCurrentItem().getType() == Material.LEATHER_CHESTPLATE) {
					gameManager.getKitsMenu().openKitsSettings(p);
					//distance check
				} else if (event.getCurrentItem().getType() == Material.SPYGLASS) {
					OpenDistanceSettings(p);
				} else if (event.getCurrentItem().getType() == Material.TOTEM_OF_UNDYING) {
					OpenRespawnMenu(p);
				}

			}

			//COMPASS SETTINGS

		} else if (menu.getTitle().equalsIgnoreCase(menuCompassTitle)) {
			event.setCancelled(true);
			if (!(event.getCurrentItem() == null || p.getPlayer() == null)) {
				if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Go back to settings")) {
					OpenSettings(p);

				} else if (event.getCurrentItem().getType() == Material.EMERALD) {
					String seconds = event.getCurrentItem().getItemMeta().getDisplayName();
					int sec = Integer.parseInt(seconds);
					gameManager.getPlugin().getConfig().set("compass-cooldown-amount", sec);
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					p.sendMessage(prefix + ChatColor.AQUA + "Tracker cooldown is set to " + sec + " seconds.");
					OpenCompassSettings(p);

				} else if (event.getCurrentItem().getType() == Material.LIME_CONCRETE) {
					gameManager.getPlugin().getConfig().set("compass-cooldown", "false");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 0);
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					OpenCompassSettings(p);
				} else if (event.getCurrentItem().getType() == Material.RED_CONCRETE) {
					gameManager.getPlugin().getConfig().set("compass-cooldown", "true");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 1);
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					OpenCompassSettings(p);
				}
			}
			//cooldown menu
		} else if (menu.getTitle().equalsIgnoreCase(menuCooldownTitle)) {
			event.setCancelled(true);
			if (!(event.getCurrentItem() == null || p.getPlayer() == null)) {
				if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Go back to settings")) {
					OpenSettings(p);

				} else if (event.getCurrentItem().getType() == Material.EMERALD) {
					String seconds = event.getCurrentItem().getItemMeta().getDisplayName();
					int sec = Integer.parseInt(seconds);
					gameManager.getPlugin().getConfig().set("hunter-cooldown", sec);
					gameManager.getTrackerManager().compassCooldown = sec * 10;
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					gameManager.getPlugin().hunterCountdownConfig = sec;
					p.sendMessage(prefix + ChatColor.AQUA + "Hunter cooldown is set to " + sec + " seconds.");
					OpenCooldownSettings(p);
				}
			}
			//Distance settings
		} else if (menu.getTitle().equalsIgnoreCase(menuDistanceTitle)) {
			event.setCancelled(true);
			if (!(event.getCurrentItem() == null || p.getPlayer() == null)) {
				if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Go back to settings")) {
					OpenSettings(p);
					return;

				} else if (event.getCurrentItem().getType() == Material.EMERALD) {
					String seconds = event.getCurrentItem().getItemMeta().getDisplayName();
					int sec = Integer.parseInt(seconds);
					gameManager.getPlugin().getConfig().set("distance-timer", sec);
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					ManhuntPlugin.distanceTimer = sec;
					p.sendMessage(prefix + ChatColor.AQUA + "Distance check is set to " + sec + " minutes.");
					OpenDistanceSettings(p);

					//Enable distance check for runner and hunter
					//Runner check
				} else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Runner Distance Check On")) {
					gameManager.getPlugin().getConfig().set("distance-check", "false");
					gameManager.getPlugin().getConfig().set("distance-check-hunter", "false");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 0);
				} else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Runner Distance Check Off")) {
					gameManager.getPlugin().getConfig().set("distance-check", "true");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 1);
				}

				//hunter check
				if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Hunter Distance Check On")) {
					gameManager.getPlugin().getConfig().set("distance-check-hunter", "false");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 0);
				} else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Hunter Distance Check Off")) {
					gameManager.getPlugin().getConfig().set("distance-check-hunter", "true");
					gameManager.getPlugin().getConfig().set("distance-check", "true");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10, 1);
				}
				gameManager.getPlugin().saveConfig();
				gameManager.getPlugin().reloadConfig();
				OpenDistanceSettings(p);

			}
		} else if (menu.getTitle().equalsIgnoreCase(respawnMenuTitle)) {
			event.setCancelled(true);
			if (!(event.getCurrentItem() == null || p.getPlayer() == null)) {
				if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "" + ChatColor.BOLD + "Go back to settings")) {
					OpenSettings(p);

				} else if (event.getCurrentItem().getType() == Material.GRAY_DYE) {
					char lives = event.getCurrentItem().getItemMeta().getDisplayName().charAt(0);
					int livesInt = Integer.parseInt(String.valueOf(lives));

					DeathListener.runnerDeathsInt = 0;

					gameManager.getPlugin().getConfig().set("runner-respawns", livesInt);
					gameManager.getPlugin().saveConfig();
					gameManager.getPlugin().reloadConfig();
					ManhuntPlugin.respawns = gameManager.getPlugin().getConfig().getInt("runner-respawns");
					gameManager.getInfoBoard().updateScoreboard();

					p.sendMessage(prefix + ChatColor.AQUA + "Runner lives set to " + livesInt);
					OpenRespawnMenu(p);
				}
			}
		}
	}
}


