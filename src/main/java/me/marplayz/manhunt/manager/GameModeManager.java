package me.marplayz.manhunt.manager;

import me.marplayz.manhunt.GUI.GameModeState;
import org.bukkit.configuration.file.FileConfiguration;

public class GameModeManager {
	private GameModeState gameModeState = GameModeState.CUSTOM;

	private final GameManager gameManager;

	public GameModeManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public GameModeState getGameModeState() {
		return gameModeState;
	}

	public void setGameModeState(GameModeState gameModeState) {
		this.gameModeState = gameModeState;
		FileConfiguration config = gameManager.getPlugin().getConfig();

		switch (gameModeState) {
			case CUSTOM:
				break;

			case CLASSIC:
				config.set("regional-respawn", "false");
				config.set("regional-portal-respawn", "false");
				config.set("distance-check", "false");
				config.set("distance-check-hunter", "false");
				config.set("hunter-cooldown", 0);
				config.set("compass-cooldown", "false");
				config.set("compass-cooldown-amount", 0);
				config.set("compass-portal", "false");
				config.set("runner-kill-reward", "false");
				config.set("runner-inv-keep", "false");
				config.set("hunter-inv-keep", "false");
				break;

			case GODFATHER:
				config.set("regional-respawn", "true");
				config.set("regional-portal-respawn", "false");
				config.set("distance-check", "true");
				config.set("distance-check-hunter", "false");
				config.set("hunter-cooldown", 30);
				config.set("compass-cooldown", "true");
				config.set("compass-cooldown-amount", 15);
				config.set("compass-portal", "false");
				config.set("runner-kill-reward", "false");
				config.set("runner-inv-keep", "false");
				config.set("hunter-inv-keep", "false");
				break;

			case ENHANCED:
				config.set("regional-respawn", "true");
				config.set("regional-portal-respawn", "true");
				config.set("distance-check", "true");
				config.set("distance-check-hunter", "false");
				config.set("hunter-cooldown", 30);
				config.set("compass-cooldown", "true");
				config.set("compass-cooldown-amount", 10);
				config.set("compass-portal", "false");
				config.set("runner-kill-reward", "false");
				config.set("runner-inv-keep", "false");
				config.set("hunter-inv-keep", "false");
				break;

			case RAPID:
				config.set("regional-respawn", "true");
				config.set("regional-portal-respawn", "true");
				config.set("distance-check", "true");
				config.set("distance-check-hunter", "true");
				config.set("hunter-cooldown", 15);
				config.set("compass-cooldown", "true");
				config.set("compass-cooldown-amount", 10);
				config.set("compass-portal", "true");
				config.set("runner-kill-reward", "false");
				config.set("runner-inv-keep", "false");
				config.set("hunter-inv-keep", "false");
				break;

			case CREATIVE:
				config.set("regional-respawn", "true");
				config.set("regional-portal-respawn", "true");
				config.set("distance-check", "true");
				config.set("distance-check-hunter", "true");
				config.set("hunter-cooldown", 15);
				config.set("compass-cooldown", "true");
				config.set("compass-cooldown-amount", 10);
				config.set("compass-portal", "true");
				config.set("runner-kill-reward", "true");
				config.set("runner-inv-keep", "false");
				config.set("hunter-inv-keep", "true");
				break;
		}

		//Disable all kits
		config.set("wood-sword-runner", "false");
		config.set("wood-sword-hunter", "false");
		config.set("stone-pick-runner", "false");
		config.set("stone-pick-hunter", "false");
		config.set("food-runner", "false");
		config.set("food-hunter", "false");
		config.set("emp", "false");
		
		gameManager.getPlugin().saveConfig();
		gameManager.getPlugin().reloadConfig();

	}
}
