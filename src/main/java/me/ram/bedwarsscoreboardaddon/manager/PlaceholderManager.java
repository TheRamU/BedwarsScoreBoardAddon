package me.ram.bedwarsscoreboardaddon.manager;

import java.util.HashMap;
import java.util.Map;

import io.github.bedwarsrel.game.Game;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.placeholder.Placeholder;

public class PlaceholderManager {

	@Getter
	private Game game;
	private Map<String, Placeholder> gamePlaceholder;
	private Map<String, Map<String, Placeholder>> teamPlaceholder;
	private Map<String, Map<String, Placeholder>> playerPlaceholder;

	public PlaceholderManager(Game game) {
		this.game = game;
		gamePlaceholder = new HashMap<String, Placeholder>();
		teamPlaceholder = new HashMap<String, Map<String, Placeholder>>();
		playerPlaceholder = new HashMap<String, Map<String, Placeholder>>();
	}

	public void registerGamePlaceholder(String identifier, Placeholder placeholder) {
		gamePlaceholder.put(identifier, placeholder);
	}

	public void unregisterGamePlaceholder(String identifier) {
		gamePlaceholder.remove(identifier);
	}

	public Map<String, Placeholder> getGamePlaceholder() {
		return gamePlaceholder;
	}

	public void registerTeamPlaceholder(String team, String identifier, Placeholder placeholder) {
		Map<String, Placeholder> placeholders = teamPlaceholder.getOrDefault(team, new HashMap<String, Placeholder>());
		placeholders.put(identifier, placeholder);
		teamPlaceholder.put(team, placeholders);
	}

	public void unregisterTeamPlaceholder(String team, String identifier) {
		Map<String, Placeholder> placeholders = teamPlaceholder.getOrDefault(team, new HashMap<String, Placeholder>());
		placeholders.remove(identifier);
		teamPlaceholder.put(identifier, placeholders);
	}

	public Map<String, Placeholder> getTeamPlaceholder(String team) {
		return teamPlaceholder.getOrDefault(team, new HashMap<String, Placeholder>());
	}

	public Map<String, Map<String, Placeholder>> getTeamPlaceholders() {
		return teamPlaceholder;
	}

	public void registerPlayerPlaceholder(String player, String identifier, Placeholder placeholder) {
		Map<String, Placeholder> placeholders = teamPlaceholder.getOrDefault(player, new HashMap<String, Placeholder>());
		placeholders.put(identifier, placeholder);
		playerPlaceholder.put(player, placeholders);
	}

	public void unregisterPlayerPlaceholder(String player, String identifier) {
		Map<String, Placeholder> placeholders = teamPlaceholder.getOrDefault(player, new HashMap<String, Placeholder>());
		placeholders.remove(identifier);
		playerPlaceholder.put(player, placeholders);
	}

	public Map<String, Placeholder> getPlayerPlaceholder(String player) {
		return playerPlaceholder.getOrDefault(player, new HashMap<String, Placeholder>());
	}

	public Map<String, Map<String, Placeholder>> getPlayerPlaceholders() {
		return playerPlaceholder;
	}
}
