package me.ram.bedwarsscoreboardaddon.addon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsPlayerJoinEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import me.clip.placeholderapi.PlaceholderAPI;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ScoreboardUtil;

public class LobbyScoreBoard implements Listener {

	private String title = "";

	private String getDate() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(Config.date_format);
		return format.format(date);
	}

	public LobbyScoreBoard() {
		new BukkitRunnable() {
			int i = 0;
			int tc = 0;

			@Override
			public void run() {
				i--;
				if (i <= 0) {
					i = Config.lobby_scoreboard_interval;
					title = Config.lobby_scoreboard_title.get(tc);
					tc++;
					if (tc >= Config.lobby_scoreboard_title.size()) {
						tc = 0;
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 1L);
	}

	@EventHandler
	public void onJoin(BedwarsPlayerJoinEvent e) {
		if (!Config.lobby_scoreboard_enabled) {
			return;
		}
		Game game = e.getGame();
		Player player = e.getPlayer();
		BedwarsRel.getInstance().getConfig().set("lobby-scoreboard.content", getLine(player, game));
		int tc = 0;
		new BukkitRunnable() {
			int i = 0;

			@Override
			public void run() {
				if (player.isOnline() && e.getGame().getPlayers().contains(player)
						&& e.getGame().getState() == GameState.WAITING) {
					i--;
					if (i <= 0) {
						i = Config.lobby_scoreboard_interval;
						updateScoreboard(player, game, tc);
					}
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 1L);
	}

	private void updateScoreboard(Player player, Game game, int tc) {
		List<String> ncelements = new ArrayList<String>();
		ncelements.add(title.replace("{game}", game.getName()));
		BedwarsRel.getInstance().getConfig().set("lobby-scoreboard.title", title);
		ncelements.addAll(getLine(player, game));
		ncelements = elementsPro(ncelements);
		if (ncelements.size() < 16) {
			int es = ncelements.size();
			for (int i = 0; i < 16 - es; i++) {
				ncelements.add(1, null);
			}
		}
		String[] scoreboardelements = ncelements.toArray(new String[ncelements.size()]);
		ScoreboardUtil.setScoreboard(player, scoreboardelements);
	}

	private List<String> getLine(Player player, Game game) {
		List<String> line = new ArrayList<String>();
		String state = Config.lobby_scoreboard_state_waiting;
		String countdown = "null";
		int needplayers = game.getMinPlayers() - game.getPlayers().size();
		needplayers = needplayers < 0 ? 0 : needplayers;
		if (game.getLobbyCountdown() != null) {
			state = Config.lobby_scoreboard_state_countdown;
			int lobbytime = game.getLobbyCountdown().getLobbytime();
			int counter = game.getLobbyCountdown().getCounter() + 1;
			counter = counter > lobbytime ? lobbytime : counter;
			countdown = counter + "";
		}
		for (String li : Config.lobby_scoreboard_lines) {
			String l = li.replace("{date}", getDate()).replace("{state}", state).replace("{game}", game.getName())
					.replace("{players}", game.getPlayers().size() + "")
					.replace("{maxplayers}", game.getMaxPlayers() + "")
					.replace("{minplayers}", game.getMinPlayers() + "").replace("{needplayers}", needplayers + "")
					.replace("{countdown}", countdown);
			if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
				l = PlaceholderAPI.setPlaceholders(player, l);
			}
			line.add(l);
		}
		return line;
	}

	private List<String> elementsPro(List<String> lines) {
		ArrayList<String> nclines = new ArrayList<String>();
		for (String ls : lines) {
			String l = ls;
			if (l != null) {
				if (nclines.contains(l)) {
					for (int i = 0; i == 0;) {
						l = l + "¡ìr";
						if (!nclines.contains(l)) {
							nclines.add(l);
							break;
						}
					}
				} else {
					nclines.add(l);
				}
			} else {
				nclines.add(l);
			}
		}
		return nclines;
	}
}
