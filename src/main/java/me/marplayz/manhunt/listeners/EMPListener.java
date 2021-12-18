package me.marplayz.manhunt.listeners;

import me.marplayz.manhunt.GUI.SettingMenu;
import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.manager.GameState;
import me.marplayz.manhunt.tasks.EmpTimerTask;
import me.marplayz.manhunt.util.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class EMPListener implements Listener {

	public static int empTimer;
	private GameManager gameManager;

	public EMPListener(ManhuntPlugin plugin) {
	}

	public EMPListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public boolean empToggle = false;

	String prefix = ManhuntPlugin.prefix;

	@EventHandler
	public void ItemClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack inHand = event.getItem();

		if (inHand == null) return;
		if (gameManager.getGameState() != GameState.ACTIVE) return;
		if (Team.getTeam("Runner") == null && Team.getTeam("Hunter") == null) return;

		ItemStack emp = new ItemStack(Objects.requireNonNull(SettingMenu.getHead("emp")));
		ItemMeta empMeta = emp.getItemMeta();
		empMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Electromagnetic Pulse " + ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "(" + empTimer + " Seconds )");
		emp.setItemMeta(empMeta);

		//EMP placed
		if (inHand.getType().equals(Material.PLAYER_HEAD) && inHand.getType().equals(emp.getType())) {
			if (Team.getTeam("Runner") == null && Team.getTeam("Hunter") == null) return;
			empToggle = true;
			event.setCancelled(true);
			p.getInventory().remove(inHand);

			//Start task
			EmpTimerTask empTimerTask = new EmpTimerTask(gameManager);
			empTimerTask.runTaskTimer(gameManager.getPlugin(), 0, 20);

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (Team.getTeam(player) == null) return;
				player.sendMessage(prefix + ChatColor.RED + "An EMP has been used!");

				if (gameManager.hunterTeamSize > 0 && Team.getTeam(player).getName().equalsIgnoreCase("Runner")) {
					final Location runnerloc = player.getLocation();

					//PARTICLE START
					Location location = player.getLocation();
					Location location2 = player.getLocation();

					int radius = 1;
					final double[] y = {0};

					new BukkitRunnable() {
						@Override
						public void run() {
							double x = radius * Math.cos(y[0]);
							double z = radius * Math.sin(y[0]);
							for (Player players : Bukkit.getOnlinePlayers()) {
								players.spawnParticle(Particle.REDSTONE, location.add(x, y[0], z), 200, new Particle.DustOptions(Color.RED, 5));
								players.spawnParticle(Particle.REDSTONE, location2.add(z, y[0], x), 200, new Particle.DustOptions(Color.YELLOW, 5));
								y[0] = y[0] + 0.1;
							}
						}
					}.runTaskTimerAsynchronously(gameManager.getPlugin(), 0, 2);
					//PARTICLE END
					for (Player players : Bukkit.getOnlinePlayers()) {
						players.playSound(runnerloc, Sound.ENTITY_GENERIC_EXPLODE, 20, 1);
					}
				}
			}
		}
	}
}

