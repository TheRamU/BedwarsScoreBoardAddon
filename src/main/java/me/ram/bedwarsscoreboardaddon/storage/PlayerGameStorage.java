package me.ram.bedwarsscoreboardaddon.storage;

import java.util.HashMap;
import java.util.Map;
import io.github.bedwarsrel.game.Game;
import me.ram.bedwarsscoreboardaddon.arena.Arena;

public class PlayerGameStorage {

	private Arena arena;
	private Map<String, Integer> totalkills;
	private Map<String, Integer> kills;
	private Map<String, Integer> finalkills;
	private Map<String, Integer> dies;
	private Map<String, Integer> beds;

	public PlayerGameStorage(Arena arena) {
		this.arena = arena;
		totalkills = new HashMap<String, Integer>();
		kills = new HashMap<String, Integer>();
		finalkills = new HashMap<String, Integer>();
		dies = new HashMap<String, Integer>();
		beds = new HashMap<String, Integer>();
	}

	public Arena getArena() {
		return arena;
	}

	public Game getGame() {
		return arena.getGame();
	}

	public Map<String, Integer> getPlayerTotalKills() {
		return totalkills;
	}

	public Map<String, Integer> getPlayerKills() {
		return kills;
	}

	public Map<String, Integer> getPlayerFinalKills() {
		return finalkills;
	}

	public Map<String, Integer> getPlayerDies() {
		return dies;
	}

	public Map<String, Integer> getPlayerBeds() {
		return beds;
	}
}
