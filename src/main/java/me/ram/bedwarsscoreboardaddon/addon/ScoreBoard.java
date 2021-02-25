package me.ram.bedwarsscoreboardaddon.addon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import me.clip.placeholderapi.PlaceholderAPI;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.manager.PlaceholderManager;
import me.ram.bedwarsscoreboardaddon.utils.ScoreboardUtil;

public class ScoreBoard {

	private Arena arena;
	private Game game;
	private int title_index = 0;
	private Map<String, String> timer_placeholder;
	private PlaceholderManager placeholderManager;
	private Map<String, String> team_status;
	private Map<String, String> over_plan_info;

	public ScoreBoard(Arena arena) {
		this.arena = arena;
		game = arena.getGame();
		placeholderManager = new PlaceholderManager();
		team_status = new HashMap<String, String>();
		timer_placeholder = new HashMap<String, String>();
		over_plan_info = new HashMap<String, String>();
		for (String id : Config.timer.keySet()) {
			new BukkitRunnable() {
				int i = Config.timer.get(id);

				@Override
				public void run() {
					if (game.getState() == GameState.RUNNING) {
						String format = i / 60 + ":" + ((i % 60 < 10) ? ("0" + i % 60) : (i % 60));
						timer_placeholder.put("{timer_" + id + "}", format);
						i--;
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), 0L, 21L);
		}
		new BukkitRunnable() {
			int i = Config.scoreboard_interval;

			@Override
			public void run() {
				i--;
				if (i <= 0) {
					i = Config.scoreboard_interval;
					if (game.getState() != GameState.WAITING && game.getState() == GameState.RUNNING) {
						updateScoreboard();
					} else
						cancel();
					return;
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 1L);
		new BukkitRunnable() {
			@Override
			public void run() {
				for (BukkitTask task : game.getRunningTasks()) {
					task.cancel();
				}
				game.getRunningTasks().clear();
				startTimerCountdown(game);
			}
		}.runTaskLater(Main.getInstance(), 19L);
	}

	public PlaceholderManager getPlaceholderManager() {
		return placeholderManager;
	}

	public void setTeamStatusFormat(String team, String status) {
		team_status.put(team, status);
	}

	public void removeTeamStatusFormat(String team) {
		team_status.remove(team);
	}

	public Map<String, String> getTeamStatusFormat() {
		return team_status;
	}

	private String getGameTime(int time) {
		return String.valueOf(time / 60);
	}

	private void startTimerCountdown(Game game) {
		BukkitRunnable task = new BukkitRunnable() {
			public void run() {
				if (game.getTimeLeft() == 0) {
					game.setOver(true);
					game.getCycle().checkGameOver();
					cancel();
					return;
				}
				game.setTimeLeft(game.getTimeLeft() - 1);
			}
		};
		game.addRunningTask(task.runTaskTimer(BedwarsRel.getInstance(), 0L, 20L));
	}

	public void updateScoreboard() {
		List<String> lines = new ArrayList<String>();
		Map<String, String> plan_infos = new HashMap<String, String>();
		for (String plan : Config.planinfo) {
			if (game.getTimeLeft() <= Main.getInstance().getConfig().getInt("planinfo." + plan + ".start_time") && game.getTimeLeft() > Main.getInstance().getConfig().getInt("planinfo." + plan + ".end_time")) {
				for (String key : Main.getInstance().getConfig().getConfigurationSection("planinfo." + plan + ".plans").getKeys(false)) {
					plan_infos.put(key, Main.getInstance().getConfig().getString("planinfo." + plan + ".plans." + key));
				}
			}
		}
		if (game.getTimeLeft() == 1) {
			over_plan_info = plan_infos;
		} else if (game.getTimeLeft() < 1) {
			plan_infos = over_plan_info;
		}
		int alive_teams = 0;
		int remain_teams = 0;
		for (Team team : game.getTeams().values()) {
			if (!team.isDead(game)) {
				alive_teams++;
			}
			if (team.getPlayers().size() > 0) {
				remain_teams++;
			}
		}
		int wither = game.getTimeLeft() - Config.witherbow_gametime;
		String format = wither / 60 + ":" + ((wither % 60 < 10) ? ("0" + wither % 60) : (wither % 60));
		String bowtime = null;
		if (wither > 0) {
			bowtime = format;
		}
		if (wither <= 0) {
			bowtime = Config.witherbow_already_starte;
		}
		String score_title = "";
		if (title_index >= Config.scoreboard_title.size()) {
			title_index = 0;
		}
		score_title = Config.scoreboard_title.size() < 1 ? "BedWars" : Config.scoreboard_title.get(title_index).replace("{game}", game.getName()).replace("{time}", getFormattedTimeLeft(game.getTimeLeft()));
		title_index++;
		String teams = game.getTeams().size() + "";
		List<String> scoreboard_lines;
		if (Config.scoreboard_lines.containsKey(teams)) {
			scoreboard_lines = Config.scoreboard_lines.get(teams);
		} else if (Config.scoreboard_lines.containsKey("default")) {
			scoreboard_lines = Config.scoreboard_lines.get("default");
		} else {
			scoreboard_lines = Arrays.asList("", "{team_status}", "");
		}
		int alive_players = 0;
		for (Player p : game.getPlayers()) {
			if (!game.isSpectator(p)) {
				alive_players++;
			}
		}
		for (Player player : game.getPlayers()) {
			Team player_team = game.getPlayerTeam(player);
			lines.clear();
			String player_total_kills = arena.getPlayerGameStorage().getPlayerTotalKills().getOrDefault(player.getName(), 0) + "";
			String player_kills = arena.getPlayerGameStorage().getPlayerKills().getOrDefault(player.getName(), 0) + "";
			String player_final_kills = arena.getPlayerGameStorage().getPlayerFinalKills().getOrDefault(player.getName(), 0) + "";
			String player_dis = arena.getPlayerGameStorage().getPlayerDies().getOrDefault(player.getName(), 0) + "";
			String player_bes = arena.getPlayerGameStorage().getPlayerBeds().getOrDefault(player.getName(), 0) + "";
			String player_team_color = "§f";
			String player_team_players = "";
			String player_team_name = "";
			String player_team_bed_status = "";
			if (game.getPlayerTeam(player) != null) {
				player_team_color = game.getPlayerTeam(player).getChatColor() + "";
				player_team_players = game.getPlayerTeam(player).getPlayers().size() + "";
				player_team_name = game.getPlayerTeam(player).getName();
				player_team_bed_status = getTeamBedStatus(game, game.getPlayerTeam(player));
			}
			for (String ls : scoreboard_lines) {
				if (ls.contains("{team_status}")) {
					for (Team t : game.getTeams().values()) {
						String you = "";
						if (game.getPlayerTeam(player) != null) {
							if (game.getPlayerTeam(player) == t) {
								you = Config.scoreboard_you;
							} else {
								you = "";
							}
						}
						if (team_status.containsKey(t.getName())) {
							lines.add(team_status.get(t.getName()).replace("{you}", you));
						} else {
							lines.add(ls.replace("{team_status}", getTeamStatusFormat(game, t).replace("{you}", you)));
						}
					}
				} else {
					String date = new SimpleDateFormat(Config.date_format).format(new Date());
					String add_line = ls;
					for (String key : plan_infos.keySet()) {
						add_line = add_line.replace("{plan_" + key + "}", plan_infos.get(key));
					}
					add_line = add_line.replace("{death_mode}", arena.getDeathMode().getDeathmodeTime()).replace("{remain_teams}", remain_teams + "").replace("{alive_teams}", alive_teams + "").replace("{alive_players}", alive_players + "").replace("{teams}", game.getTeams().size() + "").replace("{color}", player_team_color).replace("{team_peoples}", player_team_players).replace("{player_name}", player.getName()).replace("{team}", player_team_name).replace("{beds}", player_bes).replace("{dies}", player_dis).replace("{totalkills}", player_total_kills).replace("{finalkills}", player_final_kills).replace("{kills}", player_kills).replace("{time}", getGameTime(game.getTimeLeft())).replace("{formattime}", getFormattedTimeLeft(game.getTimeLeft())).replace("{game}", game.getName()).replace("{date}", date).replace("{online}", game.getPlayers().size() + "").replace("{bowtime}", bowtime).replace("{team_bed_status}", player_team_bed_status).replace("{no_break_bed}", arena.getNoBreakBed().getTime());
					for (String key : arena.getHealthLevel().getLevelTime().keySet()) {
						add_line = add_line.replace("{sethealthtime_" + key + "}", arena.getHealthLevel().getLevelTime().get(key));
					}
					for (String key : arena.getResourceUpgrade().getUpgTime().keySet()) {
						add_line = add_line.replace("{resource_upgrade_" + key + "}", arena.getResourceUpgrade().getUpgTime().get(key));
					}
					for (String key : placeholderManager.getGamePlaceholder().keySet()) {
						add_line = add_line.replace(key, placeholderManager.getGamePlaceholder().get(key));
					}
					for (Team t : game.getTeams().values()) {
						if (add_line.contains("{team_" + t.getName() + "_status}")) {
							String stf = getTeamStatusFormat(game, t);
							if (game.getPlayerTeam(player) == null) {
								stf = stf.replace("{you}", "");
							} else if (game.getPlayerTeam(player) == t) {
								stf = stf.replace("{you}", Config.scoreboard_you);
							} else {
								stf = stf.replace("{you}", "");
							}
							add_line = add_line.replace("{team_" + t.getName() + "_status}", stf);
						}
						if (add_line.contains("{team_" + t.getName() + "_bed_status}")) {
							add_line = add_line.replace("{team_" + t.getName() + "_bed_status}", getTeamBedStatus(game, t));
						}
						if (add_line.contains("{team_" + t.getName() + "_peoples}")) {
							add_line = add_line.replace("{team_" + t.getName() + "_peoples}", t.getPlayers().size() + "");
						}
					}
					if (player_team == null) {
						for (String teamname : placeholderManager.getTeamPlaceholders().keySet()) {
							for (String placeholder : placeholderManager.getTeamPlaceholders().get(teamname).keySet()) {
								add_line = add_line.replace(placeholder, "");
							}
						}
					} else if (placeholderManager.getTeamPlaceholders().containsKey(player_team.getName())) {
						for (String placeholder : placeholderManager.getTeamPlaceholder(player_team.getName()).keySet()) {
							add_line = add_line.replace(placeholder, placeholderManager.getTeamPlaceholder(player_team.getName()).get(placeholder));
						}
					} else {
						for (String teamname : placeholderManager.getTeamPlaceholders().keySet()) {
							for (String placeholder : placeholderManager.getTeamPlaceholders().get(teamname).keySet()) {
								add_line = add_line.replace(placeholder, "");
							}
						}
					}
					if (placeholderManager.getPlayerPlaceholders().containsKey(player.getName())) {
						for (String placeholder : placeholderManager.getPlayerPlaceholder(player.getName()).keySet()) {
							add_line = add_line.replace(placeholder, placeholderManager.getPlayerPlaceholder(player.getName()).get(placeholder));
						}
					} else {
						for (String playername : placeholderManager.getPlayerPlaceholders().keySet()) {
							for (String placeholder : placeholderManager.getPlayerPlaceholders().get(playername).keySet()) {
								add_line = add_line.replace(placeholder, "");
							}
						}
					}
					for (String placeholder : timer_placeholder.keySet()) {
						add_line = add_line.replace(placeholder, timer_placeholder.get(placeholder));
					}
					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
						add_line = PlaceholderAPI.setPlaceholders(player, add_line);
					}
					lines.add(add_line);
				}
			}
			String title = score_title;
			if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
				title = PlaceholderAPI.setPlaceholders(player, title);
			}
			ScoreboardUtil.setGameScoreboard(player, title, lines, game);
		}
	}

	private String getFormattedTimeLeft(int time) {
		int min = (int) Math.floor(time / 60);
		int sec = time % 60;
		String minStr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
		String secStr = ((sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec));
		return minStr + ":" + secStr;
	}

	private String getTeamBedStatus(Game game, Team team) {
		return team.isDead(game) ? Config.scoreboard_team_bed_status_bed_destroyed : Config.scoreboard_team_bed_status_bed_alive;
	}

	private String getTeamStatusFormat(Game game, Team team) {
		String alive = Config.scoreboard_team_status_format_bed_alive;
		String destroyed = Config.scoreboard_team_status_format_bed_destroyed;
		String status = team.isDead(game) ? destroyed : alive;
		if (team.isDead(game) && team.getPlayers().size() <= 0) {
			status = Config.scoreboard_team_status_format_team_dead;
		}
		return status.replace("{bed_status}", getTeamBedStatus(game, team)).replace("{color}", team.getChatColor() + "").replace("{color_initials}", team.getChatColor().name().substring(0, 1)).replace("{color_name}", upperInitials(team.getChatColor().name())).replace("{players}", team.getPlayers().size() + "").replace("{team_initials}", team.getName().substring(0, 1)).replace("{team}", team.getName());
	}

	private String upperInitials(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
}
