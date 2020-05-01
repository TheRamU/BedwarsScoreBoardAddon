package me.ram.bedwarsscoreboardaddon.utils;

import java.text.DecimalFormat;
import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.scoreboard.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;

public class ScoreboardUtil {

	private static Map<Player, Scoreboard> scoreboards = new HashMap<Player, Scoreboard>();
	private static Map<Player, Map<Player, Integer>> player_health = new HashMap<Player, Map<Player, Integer>>();

	private static String[] cutUnranked(String[] content) {
		String[] elements = Arrays.copyOf(content, 16);
		if (elements[0] == null) {
			elements[0] = "BedWars";
		}
		if (elements[0].length() > 32) {
			elements[0] = elements[0].substring(0, 32);
		}
		for (int i = 1; i < elements.length; ++i) {
			if (elements[i] != null && elements[i].length() > 40) {
				elements[i] = elements[i].substring(0, 40);
			}
		}
		return elements;
	}

	public static Map<Player, Scoreboard> getScoreboards() {
		return scoreboards;
	}

	public static void removePlayer(Player player) {
		if (scoreboards.containsKey(player)) {
			scoreboards.remove(player);
		}
		if (player_health.containsKey(player)) {
			player_health.remove(player);
		}
	}

	public static void setLobbyScoreboard(Player p, String[] elements, Game game) {
		elements = cutUnranked(elements);
		Scoreboard scoreboard = p.getScoreboard();
		try {
			if (scoreboard == null || scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()
					|| scoreboard.getObjectives().size() != 1) {
				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				scoreboard = p.getScoreboard();
			}
			if (scoreboard.getObjective("bwsba-lobby") == null) {
				scoreboard.registerNewObjective("bwsba-lobby", "dummy");
				scoreboard.getObjective("bwsba-lobby").setDisplaySlot(DisplaySlot.SIDEBAR);
			}
			scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(elements[0]);
			for (int i = 1; i < elements.length; ++i) {
				if (elements[i] != null
						&& scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).getScore() != 16 - i) {
					scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).setScore(16 - i);
					for (String string : scoreboard.getEntries()) {
						if (scoreboard.getObjective("bwsba-lobby").getScore(string).getScore() == 16 - i
								&& !string.equals(elements[i])) {
							scoreboard.resetScores(string);
						}
					}
				}
			}
			for (String entry : scoreboard.getEntries()) {
				boolean toErase = true;
				for (String element : elements) {
					if (element != null && element.equals(entry) && scoreboard.getObjective("bwsba-lobby")
							.getScore(entry).getScore() == 16 - Arrays.asList(elements).indexOf(element)) {
						toErase = false;
						break;
					}
				}
				if (toErase) {
					scoreboard.resetScores(entry);
				}
			}
			for (Team t : game.getTeams().values()) {
				org.bukkit.scoreboard.Team team = scoreboard.getTeam(game.getName() + ":" + t.getName());
				if (team == null) {
					team = scoreboard.registerNewTeam(game.getName() + ":" + t.getName());
				}
				team.setAllowFriendlyFire(false);
				team.setPrefix(t.getChatColor().toString());
				for (Player pl : t.getPlayers()) {
					if (!team.hasPlayer((OfflinePlayer) pl)) {
						team.addPlayer((OfflinePlayer) pl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setGameScoreboard(Player p, String[] elements, Game game) {
		boolean exist = scoreboards.containsKey(p);
		if (!exist) {
			scoreboards.put(p, Bukkit.getScoreboardManager().getNewScoreboard());
		}
		elements = cutUnranked(elements);
		Scoreboard scoreboard = scoreboards.get(p);
		try {
			if (scoreboard.getObjective("bwsba-game") == null) {
				scoreboard.registerNewObjective("bwsba-game", "dummy");
				scoreboard.getObjective("bwsba-game").setDisplaySlot(DisplaySlot.SIDEBAR);
			}
			ProtocolManager m = ProtocolLibrary.getProtocolManager();
			if ((p.getScoreboard() == null || !p.getScoreboard().equals(scoreboard)) && !exist) {
				if (Config.tab_health) {
					try {
						PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
						packet.getIntegers().write(0, 0);
						packet.getStrings().write(0, "bwsba-game-list");
						packet.getStrings().write(1, "bwsba-game-list");
						m.sendServerPacket(p, packet);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
						packet.getIntegers().write(0, 0);
						packet.getStrings().write(0, "bwsba-game-list");
						m.sendServerPacket(p, packet);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (Config.tag_health) {
					try {
						PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
						packet.getIntegers().write(0, 0);
						packet.getStrings().write(0, "bwsba-game-name");
						packet.getStrings().write(1, "bwsba-game-name");
						m.sendServerPacket(p, packet);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
						packet.getIntegers().write(0, 2);
						packet.getStrings().write(0, "bwsba-game-name");
						m.sendServerPacket(p, packet);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
						packet.getIntegers().write(0, 2);
						packet.getStrings().write(0, "bwsba-game-name");
						packet.getStrings().write(1, "¡ìc\u2764");
						m.sendServerPacket(p, packet);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(elements[0]);
			for (int i = 1; i < elements.length; ++i) {
				if (elements[i] != null
						&& scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).getScore() != 16 - i) {
					scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).setScore(16 - i);
					for (String string : scoreboard.getEntries()) {
						if (scoreboard.getObjective("bwsba-game").getScore(string).getScore() == 16 - i
								&& !string.equals(elements[i])) {
							scoreboard.resetScores(string);
						}
					}
				}
			}
			for (String entry : scoreboard.getEntries()) {
				boolean toErase = true;
				for (String element : elements) {
					if (element != null && element.equals(entry) && scoreboard.getObjective("bwsba-game")
							.getScore(entry).getScore() == 16 - Arrays.asList(elements).indexOf(element)) {
						toErase = false;
						break;
					}
				}
				if (toErase) {
					scoreboard.resetScores(entry);
				}
			}
			if (!player_health.containsKey(p)) {
				player_health.put(p, new HashMap<Player, Integer>());
			}
			Map<Player, Integer> map = player_health.get(p);
			for (Player pl : game.getPlayers()) {
				DecimalFormat format = new DecimalFormat("##");
				int i = Integer.valueOf(format.format(pl.getHealth()));
				if (map.getOrDefault(pl, 0) != i) {
					if (Config.tab_health) {
						try {
							PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
							packet.getIntegers().write(0, i);
							packet.getStrings().write(0, pl.getName());
							packet.getStrings().write(1, "bwsba-game-list");
							m.sendServerPacket(p, packet);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (Config.tag_health) {
						try {
							PacketContainer packet = m.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
							packet.getIntegers().write(0, i);
							packet.getStrings().write(0, pl.getName());
							packet.getStrings().write(1, "bwsba-game-name");
							m.sendServerPacket(p, packet);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					map.put(pl, i);
				}
			}
			Team playerteam = game.getPlayerTeam(p);
			List<UUID> players = Main.getInstance().getArenaManager().getArena(game.getName()).getInvisiblePlayer()
					.getPlayers();
			for (Team t : game.getTeams().values()) {
				org.bukkit.scoreboard.Team team = scoreboard.getTeam(game.getName() + ":" + t.getName());
				if (team == null) {
					team = scoreboard.registerNewTeam(game.getName() + ":" + t.getName());
				}
				if (!Config.playertag_prefix.equals("")) {
					team.setPrefix(Config.playertag_prefix.replace("{color}", t.getChatColor() + "").replace("{team}",
							t.getName()));
				}
				if (!Config.playertag_suffix.equals("")) {
					team.setSuffix(Config.playertag_suffix.replace("{color}", t.getChatColor() + "").replace("{team}",
							t.getName()));
				}
				team.setAllowFriendlyFire(false);
				for (Player pl : t.getPlayers()) {
					if (!team.hasPlayer((OfflinePlayer) pl)) {
						if (!players.contains(pl.getUniqueId())) {
							team.addPlayer((OfflinePlayer) pl);
						} else if (playerteam != null && playerteam.getPlayers().contains(pl)) {
							team.addPlayer((OfflinePlayer) pl);
						} else {
							String listName = pl.getPlayerListName();
							if (listName == null || listName.equals(pl.getName())) {
								String prefix = team.getPrefix();
								String suffix = team.getSuffix();
								prefix = prefix == null ? "" : prefix;
								suffix = suffix == null ? "" : suffix;
								String name = prefix + pl.getName() + suffix;
								if (listName == null || !name.equals(listName)) {
									pl.setPlayerListName(prefix + pl.getName() + suffix);
								}
							}
						}
					}
				}
			}
			if (playerteam != null && players.contains(p.getUniqueId())) {
				for (Team t : game.getTeams().values()) {
					if (!t.getName().equals(playerteam.getName())) {
						for (Player player : t.getPlayers()) {
							Scoreboard scoreboard2 = player.getScoreboard();
							for (org.bukkit.scoreboard.Team team : scoreboard2.getTeams()) {
								if (playerteam.getPlayers().contains(p)) {
									team.removePlayer(p);
								}
							}
						}
					}
				}
			}
			if (p.getScoreboard() == null || !p.getScoreboard().equals(scoreboard)) {
				p.setScoreboard(scoreboard);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
