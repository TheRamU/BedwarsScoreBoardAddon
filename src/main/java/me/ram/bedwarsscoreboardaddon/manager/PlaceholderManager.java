package me.ram.bedwarsscoreboardaddon.manager;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderManager {

	private Map<String, String> GamePlaceholder;
	private Map<String, Map<String, String>> TeamPlaceholder;
	private Map<String, Map<String, String>> PlayerPlaceholder;

	public PlaceholderManager() {
		GamePlaceholder = new HashMap<String, String>();
		TeamPlaceholder = new HashMap<String, Map<String, String>>();
		PlayerPlaceholder = new HashMap<String, Map<String, String>>();
	}

	public void setGamePlaceholder(String placeholder, String info) {
		GamePlaceholder.put(placeholder, info);
	}

	public void removeGamePlaceholder(String placeholder) {
		GamePlaceholder.remove(placeholder);
	}

	public Map<String, String> getGamePlaceholder() {
		return GamePlaceholder;
	}

	public void setTeamPlaceholder(String team, String placeholder, String info) {
		Map<String, String> placeholders = TeamPlaceholder.getOrDefault(team, new HashMap<String, String>());
		placeholders.put(placeholder, info);
		TeamPlaceholder.put(team, placeholders);
	}

	public void removeTeamPlaceholder(String team, String placeholder) {
		Map<String, String> placeholders = TeamPlaceholder.getOrDefault(team, new HashMap<String, String>());
		placeholders.remove(placeholder);
		TeamPlaceholder.put(placeholder, placeholders);
	}

	public Map<String, String> getTeamPlaceholder(String team) {
		return TeamPlaceholder.getOrDefault(team, new HashMap<String, String>());
	}

	public Map<String, Map<String, String>> getTeamPlaceholders() {
		return TeamPlaceholder;
	}

	public void setPlayerPlaceholder(String player, String placeholder, String info) {
		Map<String, String> placeholders = TeamPlaceholder.getOrDefault(player, new HashMap<String, String>());
		placeholders.put(placeholder, info);
		PlayerPlaceholder.put(player, placeholders);
	}

	public void removePlayerPlaceholder(String player, String placeholder) {
		Map<String, String> placeholders = TeamPlaceholder.getOrDefault(player, new HashMap<String, String>());
		placeholders.remove(placeholder);
		PlayerPlaceholder.put(placeholder, placeholders);
	}

	public Map<String, String> getPlayerPlaceholder(String player) {
		return PlayerPlaceholder.getOrDefault(player, new HashMap<String, String>());
	}

	public Map<String, Map<String, String>> getPlayerPlaceholders() {
		return PlayerPlaceholder;
	}
}
