package me.marplayz.manhunt.listeners;

import me.marplayz.manhunt.GUI.SettingMenu;
import me.marplayz.manhunt.ManhuntPlugin;
import me.marplayz.manhunt.listeners.EMPListener;
import me.marplayz.manhunt.listeners.PortalListener;
import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.tasks.CompassCooldownTask;
import me.marplayz.manhunt.util.Team;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class TrackerListener implements Listener{

    private ManhuntPlugin plugin;
    private GameManager gameManager;
    public static int empTimer;

    public TrackerListener(ManhuntPlugin plugin) {
        this.plugin = plugin;
    }

    public TrackerListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    String prefix = ManhuntPlugin.prefix;

    @EventHandler
    public void TrackerClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack inHand = event.getItem();
        Action act = event.getAction();

        if (inHand == null) return;
        if (Team.getTeam("Runner") == null && Team.getTeam("Hunter") == null) return;

        ItemStack compass = new ItemStack(Material.COMPASS, 1);
        ItemMeta itemMeta = compass.getItemMeta();
        itemMeta.setDisplayName(gameManager.getInventoryManager().compassName);
        compass.setItemMeta(itemMeta);

        ItemStack emp = new ItemStack(Objects.requireNonNull(SettingMenu.getHead("emp")));
        ItemMeta empMeta = emp.getItemMeta();
        empMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Electromagnetic Pulse " + ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "(" + empTimer + " Seconds )");
        emp.setItemMeta(empMeta);

        if (inHand.getType() == Material.COMPASS && inHand.equals(compass) && (act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK)) {

            //Check if hunter in Nether
            if (p.getWorld().getEnvironment() == World.Environment.NETHER) {
                p.sendMessage(prefix + ChatColor.RED + "Compass does not work in the nether!");
                return;
            }
            //Check for active EMP
            if (gameManager.getItemListener().empToggle) {
                p.sendMessage(prefix + ChatColor.RED + "EMP is still active.");
                return;
            }
            //Check for cooldown
            if (gameManager.getTrackerManager().compassCooldownMap.containsKey(p.getName())) {
                if (gameManager.getTrackerManager().compassCooldownMap.get(p.getName()) > System.currentTimeMillis()) {
                    long cooldownLeft = (gameManager.getTrackerManager().compassCooldownMap.get(p.getName()) - System.currentTimeMillis()) / 1000;
                    p.sendMessage(prefix + ChatColor.RED + "Your compass is still on cooldown. [" + (int) cooldownLeft + " second(s)]");
                    return;
                }
            }

            //Check for runners online
            if (gameManager.runnerTeamSize <= 0) {
                //Click to end
                TextComponent quitMessage = new TextComponent("            CLICK TO END GAME");
                quitMessage.setColor(net.md_5.bungee.api.ChatColor.RED);
                quitMessage.setBold(true);
                quitMessage.setUnderlined(false);
                quitMessage.setItalic(true);
                quitMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/manhunt stop"));

                //send message
                p.sendMessage(prefix + ChatColor.RED + "No Speedrunner found!");
                p.spigot().sendMessage(quitMessage);
                return;
            }

            if (gameManager.runnerTeamSize == 1) {
                for (Player runner : Bukkit.getOnlinePlayers()) {
                    if (Team.getTeam(runner).getName().equals("Runner")) {
                        gameManager.getTrackerManager().setCompassTarget(p, runner);
                    }
                }
            } else {
                gameManager.getCompassMenu().openCompassMenu(p);
            }
        }
    }
}


