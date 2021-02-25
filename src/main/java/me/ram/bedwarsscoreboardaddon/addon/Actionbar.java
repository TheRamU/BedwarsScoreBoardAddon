package me.ram.bedwarsscoreboardaddon.addon;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.manager.PlaceholderManager;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class Actionbar {
	@Getter
	private Game game;
	@Getter
	private PlaceholderManager placeholderManager;

	public Actionbar(Game game) {
		this.game = game;
		placeholderManager = new PlaceholderManager();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game.getState() != GameState.RUNNING) {
					cancel();
					return;
				}
				sendActionbar();
			}
		}.runTaskTimer(Main.getInstance(), 0L, 21L);
	}

	private void sendActionbar() {
		for (Player player : game.getPlayers()) {
			int wither = game.getTimeLeft() - Config.witherbow_gametime;
			String format = wither / 60 + ":" + ((wither % 60 < 10) ? ("0" + wither % 60) : (wither % 60));
			String bowtime = null;
			if (wither > 0) {
				bowtime = format;
			}
			if (wither <= 0) {
				bowtime = Config.witherbow_already_starte;
			}
			if (game.getPlayerTeam(player) != null) {
				if (player.getLocation().getWorld().equals(game.getPlayerTeam(player).getSpawnLocation().getWorld())) {
					int alive_players = 0;
					for (Player p : game.getPlayers()) {
						if (!game.isSpectator(p)) {
							alive_players++;
						}
					}
					Team playerteam = game.getPlayerTeam(player);
					String ab = Config.actionbar.replace("{team_peoples}", game.getPlayerTeam(player).getPlayers().size() + "").replace("{bowtime}", bowtime).replace("{color}", game.getPlayerTeam(player).getChatColor() + "").replace("{team}", game.getPlayerTeam(player).getName()).replace("{range}", (int) player.getLocation().distance(game.getPlayerTeam(player).getSpawnLocation()) + "").replace("{time}", (game.getTimeLeft() / 60) + "").replace("{formattime}", getFormattedTimeLeft(game.getTimeLeft())).replace("{game}", game.getName()).replace("{date}", new SimpleDateFormat(Config.date_format).format(new Date())).replace("{online}", Bukkit.getOnlinePlayers().size() + "").replace("{alive_players}", alive_players + "");
					for (String placeholder : placeholderManager.getGamePlaceholder().keySet()) {
						ab = ab.replace(placeholder, placeholderManager.getGamePlaceholder().get(placeholder));
					}
					if (playerteam == null) {
						for (String teamname : placeholderManager.getTeamPlaceholders().keySet()) {
							for (String placeholder : placeholderManager.getTeamPlaceholders().get(teamname).keySet()) {
								ab = ab.replace(placeholder, "");
							}
						}
					} else if (placeholderManager.getTeamPlaceholders().containsKey(playerteam.getName())) {
						for (String placeholder : placeholderManager.getTeamPlaceholder(playerteam.getName()).keySet()) {
							ab = ab.replace(placeholder, placeholderManager.getTeamPlaceholder(playerteam.getName()).get(placeholder));
						}
					} else {
						for (String teamname : placeholderManager.getTeamPlaceholders().keySet()) {
							for (String placeholder : placeholderManager.getTeamPlaceholders().get(teamname).keySet()) {
								ab = ab.replace(placeholder, "");
							}
						}
					}
					if (placeholderManager.getPlayerPlaceholders().containsKey(player.getName())) {
						for (String placeholder : placeholderManager.getPlayerPlaceholder(player.getName()).keySet()) {
							ab = ab.replace(placeholder, placeholderManager.getPlayerPlaceholder(player.getName()).get(placeholder));
						}
					} else {
						for (String playername : placeholderManager.getPlayerPlaceholders().keySet()) {
							for (String placeholder : placeholderManager.getPlayerPlaceholders().get(playername).keySet()) {
								ab = ab.replace(placeholder, "");
							}
						}
					}
					Utils.sendPlayerActionbar(player, ab);
				}
			}
		}
	}

	private String getFormattedTimeLeft(int time) {
		int min = (int) Math.floor(time / 60);
		int sec = time % 60;
		String minStr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
		String secStr = ((sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec));
		return minStr + ":" + secStr;
	}
}
