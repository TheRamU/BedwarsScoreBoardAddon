package me.ram.bedwarsscoreboardaddon.addon;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;

public class HidePlayer implements Listener {

	public HidePlayer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Config.hide_player) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						List<Player> players = new ArrayList<Player>();
						Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
						if (game == null) {
							for (Game g : BedwarsRel.getInstance().getGameManager().getGames()) {
								players.addAll(g.getPlayers());
							}
						} else {
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (game.getPlayers().contains(p)) {
									if (game.isSpectator(p)) {
										players.add(p);
									}
								} else {
									players.add(p);
								}
							}
						}
						if (game == null) {
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (players.contains(p)) {
									if (player != p) {
										player.hidePlayer(p);
									}
								} else {
									if (player != p) {
										player.showPlayer(p);
									}
								}
							}
						} else {
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (player != p) {
									if (players.contains(p)) {
										player.hidePlayer(p);
									} else {
										if (p.getGameMode() == GameMode.SPECTATOR) {
											player.hidePlayer(p);
										} else {
											player.showPlayer(p);
										}
									}
								}
							}
						}
					}
				} else {
					for (Player player : Bukkit.getOnlinePlayers()) {
						List<Player> players = new ArrayList<Player>();
						Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
						if (game != null && game.getState() == GameState.RUNNING && !game.isSpectator(player)) {
							for (Player p : game.getPlayers()) {
								if (game.isSpectator(p) || p.getGameMode() == GameMode.SPECTATOR) {
									players.add(p);
								}
							}
						}
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (player != p) {
								if (players.contains(p)) {
									player.hidePlayer(p);
								} else {
									player.showPlayer(p);
								}
							}
						}
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 1L, 1L);
	}
}
