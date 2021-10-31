package me.ram.bedwarsscoreboardaddon.utils;

import org.bukkit.entity.Player;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;

public class BedwarsUtil {

	public static boolean isRespawning(Player player) {
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return false;
		}
		return isRespawning(game, player);
	}

	public static boolean isRespawning(Game game, Player player) {
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		if (arena == null) {
			return false;
		}
		return arena.getRespawn().isRespawning(player);
	}

	public static boolean isSpectator(Player player) {
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return false;
		}
		return isSpectator(game, player);
	}

	public static boolean isSpectator(Game game, Player player) {
		return game.isSpectator(player) || isRespawning(game, player);
	}

	public static boolean isDieOut(Game game, Team team) {
		if (!team.isDead(game)) {
			return false;
		}
		for (Player player : team.getPlayers()) {
			if (!game.isSpectator(player)) {
				return false;
			}
		}
		return true;
	}
}
