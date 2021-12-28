package me.marplayz.manhunt.particles;

import me.marplayz.manhunt.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnEffect {

	private final GameManager gameManager;
	private int totemTask;
	private int fireTask;
	private int empTask;

	public RespawnEffect(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public void respawnParticle(Player player) {
		totemTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameManager.getPlugin(), new Runnable() {
			double var, var2 = 0;
			Location loc, first, second, third, fourth;
			int time = 20 * 3;

			@Override
			public void run() {
				if (time <= 0) {
					Bukkit.getScheduler().cancelTask(totemTask);
				}
				var += Math.PI / 16;
				var2 -= Math.PI / 16;


				loc = player.getLocation();
				first = loc.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
				second = loc.clone().add(Math.cos(var + Math.PI), Math.sin(var + Math.PI) + 1, Math.sin(var + Math.PI));
				third = loc.clone().add(Math.cos(var2), Math.sin(var2) + 1, Math.sin(var2));
				fourth = loc.clone().add(Math.cos(var2 + Math.PI), Math.sin(2 + Math.PI) + 1, Math.sin(var2 + Math.PI));

				player.getWorld().spawnParticle(Particle.TOTEM, first, 0);
				player.getWorld().spawnParticle(Particle.TOTEM, second, 0);
				player.getWorld().spawnParticle(Particle.TOTEM, third, 0);
				player.getWorld().spawnParticle(Particle.TOTEM, fourth, 0);
				time--;
			}
		}, 0, 1);
	}

	public void activeParticle(Player player) {
		fireTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameManager.getPlugin(), new Runnable() {
			double var = 0;
			final Location loc = player.getLocation();
			final double r = 1.5;

			@Override
			public void run() {
				if (var > 2 * Math.PI) {
					Bukkit.getScheduler().cancelTask(fireTask);
				}
				var += Math.PI / 10;
				for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 40) {
					double x = r * Math.cos(theta) * Math.sin(var);
					double y = r * Math.cos(var) * +1.5;
					double z = r * Math.sin(theta) * Math.sin(var);

					loc.add(x, y, z);
					player.getWorld().spawnParticle(Particle.FLAME, loc, 0, 0, 0, 0, 1);
					loc.subtract(x, y, z);
				}
			}
		}, 0, 1);
	}

	public void empParticle(Player player) {
		empTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameManager.getPlugin(), new Runnable() {
			int time = 20*3;
			//PARTICLE START
			final Location location = player.getLocation();
			final Location location2 = player.getLocation();

			final int radius = 1;
			final double[] y = {0};

			@Override
			public void run() {
				if (time <= 0) {
					Bukkit.getScheduler().cancelTask(empTask);
				}
				double x = radius * Math.cos(y[0]);
				double z = radius * Math.sin(y[0]);
				for (Player players : Bukkit.getOnlinePlayers()) {
					players.spawnParticle(Particle.REDSTONE, location.add(x, y[0], z), 200, new Particle.DustOptions(Color.RED, 5));
					players.spawnParticle(Particle.REDSTONE, location2.add(z, y[0], x), 200, new Particle.DustOptions(Color.YELLOW, 5));
					y[0] = y[0] + 0.1;
				}
				time--;
			}
		}, 0, 1);
	}
}
