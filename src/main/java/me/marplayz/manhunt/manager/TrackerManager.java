package me.marplayz.manhunt.manager;

import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.listeners.PortalListener;
import me.marplayz.manhunt.states.TrackerState;
import me.marplayz.manhunt.tasks.CompassCooldownTask;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class TrackerManager {
    private final GameManager gameManager;
    private TrackerState trackerState = TrackerState.POSITIONAL;

    public TrackerManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public TrackerState getTrackerState() {
        return trackerState;
    }

    public Map<String, Long> compassCooldownMap = new HashMap<String, Long>();
    private me.marplayz.manhunt.listeners.EMPListener EMPListener;

    public int compassCooldown = 0;
    String prefix = ManhuntPlugin.prefix;

    public void setCompassTarget(Player p, Player target) {
        FileConfiguration config = gameManager.getPlugin().getConfig();
        compassCooldown = config.getInt("compass-cooldown-amount");

        //Set hunter compass target to runner
        //Switch case corresponding TrackerState
        if (target.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            switch (getTrackerState()) {
                case POSITIONAL:
                    p.setCompassTarget(target.getLocation());
                    p.sendMessage(prefix + ChatColor.AQUA + "Your compass is now pointing to " + target.getName() + ChatColor.GOLD + " [POSITIONAL]");
                    break;
                case VECTOR:
                    Location playerToTarget = target.getLocation().subtract(p.getLocation());
                    p.setCompassTarget(playerToTarget.toVector().normalize().multiply(100).toLocation(p.getWorld()));
                    p.sendMessage(prefix + ChatColor.AQUA + "Your compass is now pointing to " + target.getName() + ChatColor.GOLD + " [DIRECTIONAL]");
                    break;
                case NEARBY:
                    p.setCompassTarget(target.getLocation());
                    p.sendMessage(prefix + ChatColor.AQUA + "Your compass is now pointing to " + target.getName() + ChatColor.GOLD + " [NEARBY]");
                    break;
            }
            //Set hunter compass target to runner's portal
        }
        if (config.getString("compass-portal").equalsIgnoreCase("true") && target.getWorld().getEnvironment().equals(World.Environment.NETHER)
                && PortalListener.locationPortalRunner != null) {
            p.setCompassTarget(PortalListener.locationPortalRunner);
            p.sendMessage(prefix + ChatColor.AQUA + "netehrYour compass is now pointing to " + ChatColor.GOLD + target.getName() + ChatColor.AQUA + "'s Portal");
        } else if (config.getString("compass-end-portal").equalsIgnoreCase("true") && target.getWorld().getEnvironment().equals(World.Environment.THE_END)
                && PortalListener.locationEndPortalRunner != null) {
            p.setCompassTarget(PortalListener.locationEndPortalRunner);
            p.sendMessage(prefix + ChatColor.AQUA + "endYour compass is now pointing to " + ChatColor.GOLD + target.getName() + ChatColor.AQUA + "'s Portal");
        }
        if (config.getString("compass-cooldown").equalsIgnoreCase("false")) {
            return;
        }

        //Action bar task
        CompassCooldownTask compassCooldownTask = new CompassCooldownTask(gameManager);
        compassCooldownTask.runTaskTimer(gameManager.getPlugin(), 0, 2);

        //Send Ready Message and sound
        Bukkit.getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), new

                Runnable() {
                    @Override
                    public void run() {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 1);
                        //p.sendMessage(prefix + ChatColor.GREEN + "Your compass is ready.");
                    }
                }, 20L * compassCooldown);
        //set cooldown
        compassCooldownMap.put(p.getName(), System.currentTimeMillis() + compassCooldown * 1000L);
    }

    public void setTrackerState(TrackerState trackerState) {
        this.trackerState = trackerState;
    }
}
