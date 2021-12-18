package me.marplayz.manhunt.util;

import me.marplayz.manhunt.manager.GameManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Team {

	private static List<Team> allTeams = new ArrayList<Team>();

	private static HashMap<String, Team> playerTeams = new HashMap<String, Team>();

	private String teamName;

	private GameManager gameManager;
	private InfoBoard infoBoard;

	public Team(GameManager gameManager){this.gameManager = gameManager;}

	public Team(InfoBoard infoBoard){this.infoBoard = infoBoard;}

	public Team(String teamName) {
		this.teamName = teamName;
		allTeams.add(this);
	}

	public String getName() {
		return teamName;
	}

	public void add(Player player) {
		playerTeams.put(player.getName(), this);
	}

	public boolean remove(Player player) {
		if (!hasTeam(player))
			return false;
		playerTeams.remove(player.getName());
		return true;
	}

	public int sizeTeam() {
		return playerTeams.size();
	}

	public static boolean hasTeam(Player player) {
		return playerTeams.containsKey(player.getName());
	}

	public static Team getTeam(String name) {
		for (Team t : allTeams)
			if (t.teamName.equalsIgnoreCase(name))
				return t;
		return null;
	}

	public static Team getTeam(Player player) {
		if (!hasTeam(player))
			return null;
		return playerTeams.get(player.getName());
	}

/*	public static String getMembers(String teamName) {
		return playerTeams.g);*/

	//}
}
