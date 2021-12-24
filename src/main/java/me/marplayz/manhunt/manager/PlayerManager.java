package me.marplayz.manhunt.manager;


import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.tasks.GameStartCountdownTask;
import me.marplayz.manhunt.util.CustomConfigs;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PlayerManager {

	private GameManager gameManager;
	private GameStartCountdownTask gameStartCountdownTask;
	private ManhuntPlugin plugin;

	public PlayerManager(ManhuntPlugin plugin) {
		this.plugin = plugin;
	}

	public PlayerManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}


	public int reward = 0;
	public boolean missingRewardsCheck = false;
	private final String prefix = ManhuntPlugin.prefix;

	public void GiveReward(Player killer) {
		if (killer.getInventory().firstEmpty() <= -1 && reward > 0) {
			killer.sendMessage(prefix + ChatColor.RED + "Inventory is full! Please remove an item in order to receive your reward.");
			missingRewardsCheck = true;
		} else {
			missingRewardsCheck = false;
			String rewardMessage = prefix + ChatColor.GREEN + "" + ChatColor.BOLD + "[" + (reward + 1) + "/4] " + ChatColor.BLUE + "You received " + ChatColor.GOLD + ""
					+ ChatColor.BOLD;

			List<String> itemsList = CustomConfigs.get().getStringList("reward.runner.items");
			if (itemsList.size() <= reward) {
				killer.sendMessage(prefix + ChatColor.BLUE + "No more rewards left.");
				return;
			}
			String[] split = itemsList.get(reward).split(";");
			killer.getInventory().addItem(new ItemStack(Material.valueOf(split[0]), Integer.parseInt(split[1])));
			killer.sendMessage(rewardMessage + split[1] + " " + split[0]);
			reward++;
		}
	}

	public void healPlayer(Player p) {
		p.setGameMode(GameMode.SURVIVAL);
		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
		p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setSaturation(20);
		p.setExhaustion(0);
		p.setLevel(0);
	}

	public void giveLobbyKit(Player p) {
		p.getInventory().clear();
		healPlayer(p);
		p.setGameMode(GameMode.ADVENTURE);
		gameManager.getInventoryManager().GiveLobbyKit(p);
	}

	public void giveStartingKitSpectator(Player p) {
		p.setGameMode(GameMode.SPECTATOR);
	}

	public void giveStartingKitRunner(Player p) {
		//Messages
		String StartMessageRunner = prefix + ChatColor.GREEN + "Hunters leave in " + gameManager.getPlugin().hunterCountdownConfig + " " + ChatColor.GREEN + "seconds!";

		p.getWorld().setTime(0);
		p.getWorld().setClearWeatherDuration(20 * 60 * 60);
		p.sendMessage(StartMessageRunner);
		healPlayer(p);
		p.getInventory().clear();
		try {
			p.teleport(Bukkit.getServer().getWorld("Manhunt").getSpawnLocation());
		} catch (NullPointerException e) {
			System.out.println(ChatColor.RED + "No manhunt world found");
		}
		p.getWorld().setTime(0);

		p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getConfig().getInt("runner-max-health"));

		gameManager.getInventoryManager().GiveRunnerKit(p);

		p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 10, 1);
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
		p.playSound(p.getLocation(), Sound.valueOf(gameManager.getPlugin().getConfig().getString("start-sound")), 10, 1);

		//Fireworks
		Firework startFirework = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
		FireworkMeta startFireworkMeta = startFirework.getFireworkMeta();
		startFireworkMeta.addEffect(FireworkEffect.builder()
				.flicker(true)
				.trail(true)
				.with(FireworkEffect.Type.BALL_LARGE)
				.withColor(Color.GREEN)
				.withFade(Color.ORANGE)
				.build());
		startFireworkMeta.setPower(0);
		startFirework.setFireworkMeta(startFireworkMeta);
	}

	public void giveStartingKitHunter(Player p) {
		//Messages
		String StartMessageHunter = prefix + ChatColor.GREEN + "The Speedrunner is gone. You have to wait " + gameManager.getPlugin().hunterCountdownConfig + " " + ChatColor.GREEN + "seconds!";

		healPlayer(p);

		p.sendMessage(StartMessageHunter);
		int potionEffectTime = gameManager.getPlugin().hunterCountdownConfig;
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * potionEffectTime, 50));
		p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * potionEffectTime, 50));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * potionEffectTime, 50));
		p.setNoDamageTicks(20 * potionEffectTime);

		p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getConfig().getInt("hunter-max-health"));


		p.getInventory().clear();
		try {
			p.teleport(Bukkit.getServer().getWorld("Manhunt").getSpawnLocation());
		} catch (NullPointerException e) {
			System.out.println(ChatColor.RED + "No manhunt world found");
		}
		gameManager.getInventoryManager().GiveHunterKit(p);

		//sounds
		p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 10, 1);
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
		p.playSound(p.getLocation(), Sound.valueOf(gameManager.getPlugin().getConfig().getString("start-sound")), 10, 1);

		//particles
		p.spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 2);
		if (potionEffectTime == 0) return;
	}

	public void resetRewards() {
		reward = 0;
		missingRewardsCheck = false;
	}

	public boolean missingAnyRewards() {
		return missingRewardsCheck;
	}

	public FileConfiguration getConfig() {
		return gameManager.getPlugin().getConfig();
	}
}
