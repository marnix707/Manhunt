package me.marplayz.manhunt.GUI;

import me.marplayz.manhunt.manager.GameManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class CompassMenu implements Listener {

    private GameManager gameManager;

    public CompassMenu(GameManager gameManager){this.gameManager = gameManager;}

    private final String menuTitle = ChatColor.GOLD + "" + ChatColor.BOLD + "  ☆    "
            + ChatColor.BLUE + ChatColor.BOLD + "Compass Selector" + ChatColor.GOLD + "" + ChatColor.BOLD + "    ☆ ";

    public void openCompassMenu(Player player){
        Inventory selectorInv = Bukkit.createInventory(null, 9, menuTitle);



        player.openInventory(selectorInv);

    }
}
