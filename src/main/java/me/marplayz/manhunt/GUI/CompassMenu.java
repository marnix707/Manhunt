package me.marplayz.manhunt.GUI;

import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.util.Heads;
import me.marplayz.manhunt.util.Team;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class CompassMenu implements Listener {

    private GameManager gameManager;

    public CompassMenu(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private final String menuTitle = ChatColor.GOLD + "" + ChatColor.BOLD + "   ☆ "
            + ChatColor.RED + ChatColor.BOLD + "Compass Selector" + ChatColor.GOLD + "" + ChatColor.BOLD + " ☆ ";


    public void openCompassMenu(Player player) {
        Inventory selectorInv = Bukkit.createInventory(null, 9, menuTitle);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (Team.getTeam(online) != null && Team.getTeam(online).getName().equalsIgnoreCase("Runner")) {
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
                skullMeta.setOwningPlayer(online);
                skullMeta.setDisplayName(online.getPlayer().getDisplayName());
                playerHead.setItemMeta(skullMeta);
                selectorInv.addItem(playerHead);
            }
        }
        player.openInventory(selectorInv);
    }

    @EventHandler
    public void MenuClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(menuTitle)) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem() == null || player.getPlayer() == null) {
                return;
            }
            if(event.getCurrentItem().getType() != Material.PLAYER_HEAD){return;}


            String targetName = event.getCurrentItem().getItemMeta().getDisplayName();
            try {
                gameManager.getCompassListener().setCompassTarget(player, Bukkit.getPlayer(targetName));
                event.getView().close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}