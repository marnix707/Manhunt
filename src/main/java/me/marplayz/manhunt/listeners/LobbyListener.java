package me.marplayz.manhunt.listeners;

import me.marplayz.manhunt.manager.GameManager;
import me.marplayz.manhunt.states.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyListener implements Listener {

	private GameManager gameManager;

	public LobbyListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	//Open menu with sword
	@EventHandler
	public void swordLobbyClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack inHand = event.getItem();
		Action act = event.getAction();

		if (gameManager.getGameState() != GameState.LOBBY) return;
		if (inHand == null) return;

		if (inHand.getType() == Material.NETHERITE_SWORD && (act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK)) {
			p.performCommand("mh");
		}
	}

	@EventHandler
	public void playerLobbyDamage(EntityDamageEvent event) {
		if (gameManager.getGameState() != GameState.LOBBY) return;

		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerLobbyDropItem(PlayerDropItemEvent event) {
		if (gameManager.getGameState() != GameState.LOBBY) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void playerLobbyMoveItem(InventoryClickEvent event) {
		if (gameManager.getGameState() != GameState.LOBBY) return;
		event.setCancelled(true);
	}
}