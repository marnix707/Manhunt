package me.marplayz.manhunt.manager;

import me.marplayz.manhunt.GUI.SettingMenu;
import me.marplayz.manhunt.util.CustomConfigs;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class InventoryManager {

	private GameManager gameManager;

	public InventoryManager(GameManager gameManager){this.gameManager = gameManager;}

	//give kits From config
	private static final ItemStack woodenSword = new ItemStack(Material.WOODEN_SWORD);
	private static final ItemStack stonePickaxe = new ItemStack(Material.STONE_PICKAXE);
	private static final ItemStack beef = new ItemStack(Material.COOKED_BEEF, 10);
	private static final ItemStack emp = SettingMenu.getHead("emp");
	private static final ItemMeta empMeta = emp.getItemMeta();

	public String compassName = ChatColor.RED + "" + ChatColor.BOLD + "Target Speedrunner" + ChatColor.GRAY + "" + " (Right Click)";

	public void GiveRunnerKit(Player runner) {
		if (gameManager.getPlugin().getConfig().getString("wood-sword-runner").equalsIgnoreCase("true")) {
			runner.getInventory().addItem(woodenSword);
		}
		if (gameManager.getPlugin().getConfig().getString("stone-pick-runner").equalsIgnoreCase("true")) {
			runner.getInventory().addItem(stonePickaxe);
		}
		if (gameManager.getPlugin().getConfig().getString("food-runner").equalsIgnoreCase("true")) {
			runner.getInventory().addItem(beef);
		}
		if (gameManager.getPlugin().getConfig().getString("emp").equalsIgnoreCase("true")) {
			int timer = gameManager.getPlugin().getConfig().getInt("emp-timer");
			empMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Electromagnetic Pulse " + ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "(" + timer + " Seconds )");
			emp.setItemMeta(empMeta);
			runner.getInventory().setItem(8, emp);
		}

		//Custom kits
		List<String> itemsList = CustomConfigs.get().getStringList("start.runner.items");
		for (String item : itemsList) {
			String[] split = item.split(";");
			runner.getInventory().addItem(new ItemStack(Material.valueOf(split[0]), Integer.parseInt(split[1])));
		}
	}

	public void GiveHunterKit(Player hunter) {
		//give compass
		ItemStack compass = new ItemStack(Material.COMPASS, 1);
		ItemMeta meta = compass.getItemMeta();
		meta.setDisplayName(compassName);
		compass.setItemMeta(meta);

		hunter.getInventory().addItem(compass);

		//Give config kits
		if (gameManager.getPlugin().getConfig().getString("wood-sword-hunter").equalsIgnoreCase("true")) {
			hunter.getInventory().addItem(woodenSword);
		}
		if (gameManager.getPlugin().getConfig().getString("stone-pick-hunter").equalsIgnoreCase("true")) {
			hunter.getInventory().addItem(stonePickaxe);
		}
		if (gameManager.getPlugin().getConfig().getString("food-hunter").equalsIgnoreCase("true")) {
			hunter.getInventory().addItem(beef);
		}
		//Custom kits
		List<String> itemsList = CustomConfigs.get().getStringList("start.hunter.items");
		for (String item : itemsList) {
			String[] split = item.split(";");
			hunter.getInventory().addItem(new ItemStack(Material.valueOf(split[0]), Integer.parseInt(split[1])));
		}

	}

	//Not so much from config

	public void GiveLobbyKit(Player player){
		ItemStack menuSword = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta menuSwordMeta = menuSword.getItemMeta();

		menuSwordMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Open Menu" + ChatColor.GRAY + "" + " (Right Click)");
		menuSword.setItemMeta(menuSwordMeta);



		player.getInventory().addItem(menuSword);
	}
}
