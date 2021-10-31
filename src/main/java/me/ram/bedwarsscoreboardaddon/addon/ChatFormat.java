package me.ram.bedwarsscoreboardaddon.addon;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.PlaceholderAPIUtil;

public class ChatFormat implements Listener {

	@EventHandler
	public void onCmd(PlayerCommandPreprocessEvent e) {
		if (!Config.chat_format_enabled) {
			return;
		}
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
		if (game == null) {
			return;
		}
		Player player = e.getPlayer();
		if (game.getState() != GameState.RUNNING || game.isSpectator(player)) {
			return;
		}
		if (e.getMessage().length() <= 7) {
			return;
		}

		String prefix = e.getMessage().substring(0, 7);
		if (!prefix.equals("/shout ")) {
			return;
		}
		e.setCancelled(true);
		if (!Config.chat_format_chat_all) {
			return;
		}
		String msg = PlaceholderAPIUtil.setPlaceholders(player, Config.chat_format_ingame_all);
		String playermsg = e.getMessage();
		playermsg = playermsg.substring(7, playermsg.length());
		for (Player p : game.getPlayers()) {
			p.sendMessage(msg.replace("{player}", player.getName()).replace("{message}", playermsg).replace("{color}", game.getPlayerTeam(player).getChatColor() + "").replace("{team}", game.getPlayerTeam(player).getName()));
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (!Config.chat_format_enabled) {
			return;
		}
		Player player = e.getPlayer();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		e.setCancelled(true);
		if (game.getState() == GameState.WAITING) {
			if (Config.chat_format_chat_lobby) {
				if (game.getPlayerTeam(player) == null) {
					String msg = PlaceholderAPIUtil.setPlaceholders(player, Config.chat_format_lobby);
					msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage());
					for (Player p : game.getPlayers()) {
						p.sendMessage(msg);
					}
				} else {
					String msg = Config.chat_format_lobby_team;
					msg = PlaceholderAPIUtil.setPlaceholders(player, msg);
					Team team = game.getPlayerTeam(player);
					msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage()).replace("{color}", team.getChatColor().toString()).replace("{team}", team.getName());
					for (Player p : game.getPlayers()) {
						p.sendMessage(msg);
					}
				}
			}

		} else if (game.getState() == GameState.RUNNING) {
			if (game.isSpectator(player)) {
				if (Config.chat_format_chat_spectator) {
					String msg = Config.chat_format_spectator;
					msg = PlaceholderAPIUtil.setPlaceholders(player, msg);
					msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage());
					for (Player p : game.getPlayers()) {
						p.sendMessage(msg);
					}
				}
			} else {
				if (Config.chat_format_chat_all) {
					String prefix = "";
					boolean all = false;
					for (String pref : Config.chat_format_all_prefix) {
						if (e.getMessage().startsWith(pref)) {
							all = true;
							prefix = pref;
						}
					}
					if (all) {
						String playermsg = e.getMessage();
						playermsg = playermsg.substring(prefix.length(), playermsg.length());
						String msg = Config.chat_format_ingame_all;
						msg = PlaceholderAPIUtil.setPlaceholders(player, msg);
						msg = msg.replace("{player}", player.getName()).replace("{message}", playermsg).replace("{color}", game.getPlayerTeam(player).getChatColor() + "").replace("{team}", game.getPlayerTeam(player).getName());
						for (Player p : game.getPlayers()) {
							p.sendMessage(msg);
						}
						return;
					}
				}
				String msg = Config.chat_format_ingame;
				msg = PlaceholderAPIUtil.setPlaceholders(player, msg);
				msg = msg.replace("{player}", player.getName()).replace("{message}", e.getMessage()).replace("{color}", game.getPlayerTeam(player).getChatColor() + "").replace("{team}", game.getPlayerTeam(player).getName());
				for (Player p : game.getPlayerTeam(player).getPlayers()) {
					p.sendMessage(msg);
				}
			}
		}
	}
}
