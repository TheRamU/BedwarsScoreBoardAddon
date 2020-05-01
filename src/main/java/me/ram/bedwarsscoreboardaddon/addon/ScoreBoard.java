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
	private int tc = 0;
	private Map<String, String> timerplaceholder;
	private PlaceholderManager placeholdermanager;
	private Map<String, String> teamstatus;
	private String over_plan_info;
	private String over_plan_time;

	public ScoreBoard(Arena arena) {
		this.arena = arena;
		game = arena.getGame();
		placeholdermanager = new PlaceholderManager();
		teamstatus = new HashMap<String, String>();
		timerplaceholder = new HashMap<String, String>();
		for (String id : Config.timer.keySet()) {
			new BukkitRunnable() {
				int i = Config.timer.get(id);

				@Override
				public void run() {
					if (game.getState() == GameState.RUNNING) {
						String format = i / 60 + ":" + ((i % 60 < 10) ? ("0" + i % 60) : (i % 60));
						timerplaceholder.put("{timer_" + id + "}", format);
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
						ScoreBoard.this.updateScoreboard();
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
		return placeholdermanager;
	}

	public void setTeamStatusFormat(String team, String status) {
		teamstatus.put(team, status);
	}

	public void removeTeamStatusFormat(String team) {
		teamstatus.remove(team);
	}

	public Map<String, String> getTeamStatusFormat() {
		return teamstatus;
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
					this.cancel();
					return;
				}
				game.setTimeLeft(game.getTimeLeft() - 1);
			}
		};
		game.addRunningTask(task.runTaskTimer(BedwarsRel.getInstance(), 0L, 20L));
	}

	public void updateScoreboard() {
		tc++;
		List<String> lines = new ArrayList<String>();
		String plan_info = "null";
		String plan_time = "null";
		for (String plan : Config.planinfo) {
			if (game.getTimeLeft() <= Main.getInstance().getConfig().getInt("planinfo." + plan + ".startametime")
					&& game.getTimeLeft() > Main.getInstance().getConfig().getInt("planinfo." + plan + ".endametime")) {
				plan_info = Main.getInstance().getConfig().getString("planinfo." + plan + ".planinfo");
				plan_time = Main.getInstance().getConfig().getString("planinfo." + plan + ".plantime");
			}
		}
		if (game.getTimeLeft() == 1) {
			over_plan_info = plan_info;
			over_plan_time = plan_time;
		} else if (game.getTimeLeft() < 1) {
			plan_info = over_plan_info;
			plan_time = over_plan_time;
		}
		int ats = 0;
		int rts = 0;
		for (Team team : game.getTeams().values()) {
			if (!team.isDead(game)) {
				ats++;
			}
			if (team.getPlayers().size() > 0) {
				rts++;
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
		String Title = "";
		if (tc >= Config.scoreboard_title.size()) {
			tc = 0;
		}
		int tcs = 0;
		for (String title : Config.scoreboard_title) {
			if (tc == tcs) {
				Title = title.replace("{game}", game.getName()).replace("{time}",
						getFormattedTimeLeft(game.getTimeLeft()));
			}
			tcs++;
		}
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
			Team playerteam = game.getPlayerTeam(player);
			lines.clear();
			String tks = "0";
			String ks = "0";
			String fks = "0";
			String dis = "0";
			String bes = "0";
			Map<String, Integer> totalkills = arena.getPlayerGameStorage().getPlayerTotalKills();
			Map<String, Integer> kills = arena.getPlayerGameStorage().getPlayerKills();
			Map<String, Integer> finalkills = arena.getPlayerGameStorage().getPlayerFinalKills();
			Map<String, Integer> dies = arena.getPlayerGameStorage().getPlayerDies();
			Map<String, Integer> beds = arena.getPlayerGameStorage().getPlayerBeds();
			tks = totalkills.getOrDefault(player.getName(), 0) + "";
			ks = kills.getOrDefault(player.getName(), 0) + "";
			fks = finalkills.getOrDefault(player.getName(), 0) + "";
			dis = dies.getOrDefault(player.getName(), 0) + "";
			bes = beds.getOrDefault(player.getName(), 0) + "";
			String p_t_c = "¡ìf";
			String p_t_ps = "";
			String p_t = "";
			String p_t_b_s = "";
			if (game.getPlayerTeam(player) != null) {
				p_t_c = game.getPlayerTeam(player).getChatColor() + "";
				p_t_ps = game.getPlayerTeam(player).getPlayers().size() + "";
				p_t = game.getPlayerTeam(player).getName();
				p_t_b_s = this.getTeamBedStatus(game, game.getPlayerTeam(player));
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
						if (teamstatus.containsKey(t.getName())) {
							lines.add(teamstatus.get(t.getName()).replace("{you}", you));
						} else {
							lines.add(ls.replace("{team_status}",
									ScoreBoard.this.getTeamStatusFormat(game, t).replace("{you}", you)));
						}
					}
				} else {
					String date = new SimpleDateFormat(Config.date_format).format(new Date());
					String addline = ls.replace("{planinfo}", plan_info).replace("{plantime}", plan_time)
							.replace("{death_mode}", arena.getDeathMode().getDeathmodeTime())
							.replace("{remain_teams}", rts + "").replace("{alive_teams}", ats + "")
							.replace("{alive_players}", alive_players + "")
							.replace("{teams}", game.getTeams().size() + "").replace("{color}", p_t_c)
							.replace("{team_peoples}", p_t_ps).replace("{player_name}", player.getName())
							.replace("{team}", p_t).replace("{beds}", bes).replace("{dies}", dis)
							.replace("{totalkills}", tks).replace("{finalkills}", fks).replace("{kills}", ks)
							.replace("{time}", getGameTime(game.getTimeLeft()))
							.replace("{formattime}", getFormattedTimeLeft(game.getTimeLeft()))
							.replace("{game}", game.getName()).replace("{date}", date)
							.replace("{online}", game.getPlayers().size() + "").replace("{bowtime}", bowtime)
							.replace("{team_bed_status}", p_t_b_s)
							.replace("{no_break_bed}", arena.getNoBreakBed().getTime());
					for (String formattime : arena.getHealthLevel().getLevelTime().keySet()) {
						addline = addline.replace("{sethealthtime_" + formattime + "}",
								arena.getHealthLevel().getLevelTime().get(formattime));
					}
					for (String formattime : arena.getResourceUpgrade().getUpgTime().keySet()) {
						addline = addline.replace("{resource_upgrade_" + formattime + "}",
								arena.getResourceUpgrade().getUpgTime().get(formattime));
					}
					for (String placeholder : placeholdermanager.getGamePlaceholder().keySet()) {
						addline = addline.replace(placeholder,
								placeholdermanager.getGamePlaceholder().get(placeholder));
					}
					for (Team t : game.getTeams().values()) {
						if (addline.contains("{team_" + t.getName() + "_status}")) {
							String stf = this.getTeamStatusFormat(game, t);
							if (game.getPlayerTeam(player) == null) {
								stf = stf.replace("{you}", "");
							} else if (game.getPlayerTeam(player) == t) {
								stf = stf.replace("{you}", Config.scoreboard_you);
							} else {
								stf = stf.replace("{you}", "");
							}
							addline = addline.replace("{team_" + t.getName() + "_status}", stf);
						}
						if (addline.contains("{team_" + t.getName() + "_bed_status}")) {
							addline = addline.replace("{team_" + t.getName() + "_bed_status}",
									this.getTeamBedStatus(game, t));
						}
						if (addline.contains("{team_" + t.getName() + "_peoples}")) {
							addline = addline.replace("{team_" + t.getName() + "_peoples}", t.getPlayers().size() + "");
						}
					}
					if (playerteam == null) {
						for (String teamname : placeholdermanager.getTeamPlaceholders().keySet()) {
							for (String placeholder : placeholdermanager.getTeamPlaceholders().get(teamname).keySet()) {
								addline = addline.replace(placeholder, "");
							}
						}
					} else if (placeholdermanager.getTeamPlaceholders().containsKey(playerteam.getName())) {
						for (String placeholder : placeholdermanager.getTeamPlaceholder(playerteam.getName())
								.keySet()) {
							addline = addline.replace(placeholder,
									placeholdermanager.getTeamPlaceholder(playerteam.getName()).get(placeholder));
						}
					} else {
						for (String teamname : placeholdermanager.getTeamPlaceholders().keySet()) {
							for (String placeholder : placeholdermanager.getTeamPlaceholders().get(teamname).keySet()) {
								addline = addline.replace(placeholder, "");
							}
						}
					}
					if (placeholdermanager.getPlayerPlaceholders().containsKey(player.getName())) {
						for (String placeholder : placeholdermanager.getPlayerPlaceholder(player.getName()).keySet()) {
							addline = addline.replace(placeholder,
									placeholdermanager.getPlayerPlaceholder(player.getName()).get(placeholder));
						}
					} else {
						for (String playername : placeholdermanager.getPlayerPlaceholders().keySet()) {
							for (String placeholder : placeholdermanager.getPlayerPlaceholders().get(playername)
									.keySet()) {
								addline = addline.replace(placeholder, "");
							}
						}
					}
					for (String placeholder : timerplaceholder.keySet()) {
						addline = addline.replace(placeholder, timerplaceholder.get(placeholder));
					}
					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
						addline = PlaceholderAPI.setPlaceholders(player, addline);
					}
					if (lines.contains(addline)) {
						lines.add(this.conflict(lines, addline));
					} else {
						lines.add(addline);
					}
				}
			}
			String title = Title;
			if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
				title = PlaceholderAPI.setPlaceholders(player, title);
			}
			List<String> elements = new ArrayList<String>();
			elements.add(title);
			elements.addAll(lines);
			if (elements.size() < 16) {
				int es = elements.size();
				for (int i = 0; i < 16 - es; i++) {
					elements.add(1, null);
				}
			}
			List<String> ncelements = elementsPro(elements);
			String[] scoreboardelements = ncelements.toArray(new String[ncelements.size()]);
			ScoreboardUtil.setGameScoreboard(player, scoreboardelements, game);
		}
	}

	private String getFormattedTimeLeft(int time) {
		int min = (int) Math.floor(time / 60);
		int sec = time % 60;
		String minStr = ((min < 10) ? ("0" + String.valueOf(min)) : String.valueOf(min));
		String secStr = ((sec < 10) ? ("0" + String.valueOf(sec)) : String.valueOf(sec));
		return minStr + ":" + secStr;
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

	private String conflict(List<String> lines, String line) {
		String l = line;
		for (int i = 0; i == 0;) {
			l = l + "¡ìr";
			if (!lines.contains(l)) {
				return l;
			}
		}
		return l;
	}

	private String getTeamBedStatus(Game game, Team team) {
		return team.isDead(game) ? Config.scoreboard_team_bed_status_bed_destroyed
				: Config.scoreboard_team_bed_status_bed_alive;
	}

	private String getTeamStatusFormat(Game game, Team team) {
		String alive = Config.scoreboard_team_status_format_bed_alive;
		String destroyed = Config.scoreboard_team_status_format_bed_destroyed;
		String status = team.isDead(game) ? destroyed : alive;
		if (team.isDead(game) && team.getPlayers().size() <= 0) {
			status = Config.scoreboard_team_status_format_team_dead;
		}
		return status.replace("{bed_status}", this.getTeamBedStatus(game, team))
				.replace("{color}", team.getChatColor() + "").replace("{team}", team.getName())
				.replace("{players}", team.getPlayers().size() + "");
	}
}
