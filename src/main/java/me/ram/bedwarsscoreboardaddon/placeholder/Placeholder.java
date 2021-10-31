package me.ram.bedwarsscoreboardaddon.placeholder;

import org.bukkit.entity.Player;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;

public abstract class Placeholder {

	public abstract String onPlayerPlaceholderRequest(Game game, Player player);

	public abstract String onGamePlaceholderRequest(Game game);

	public abstract String onTeamPlaceholderRequest(Team team);

}
