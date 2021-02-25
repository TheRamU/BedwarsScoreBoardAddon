package me.ram.bedwarsscoreboardaddon.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import me.ram.bedwarsscoreboardaddon.config.Config;

public class CommandTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> suggest = getSuggest(sender, args);
		String last = args[args.length - 1];
		if (suggest != null && !last.equals("")) {
			List<String> list = new ArrayList<String>();
			suggest.forEach(s -> {
				if (s.startsWith(last)) {
					list.add(s);
				}
			});
			return list;
		}
		return suggest;
	}

	private List<String> getSuggest(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return Arrays.asList("help", "shop", "spawner", "edit", "reload", "upcheck");
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("shop")) {
				return Arrays.asList("list", "remove", "set");
			}
			if (args[0].equalsIgnoreCase("spawner")) {
				return Arrays.asList("list", "remove", "add");
			}
			if (args[0].equalsIgnoreCase("edit")) {
				return getGames();
			}
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("shop") && args[1].equalsIgnoreCase("set")) {
				return Arrays.asList("item", "team");
			}
			if (args[0].equalsIgnoreCase("shop") && args[1].equalsIgnoreCase("list") && sender.hasPermission("bedwarsscoreboardaddon.shop.list")) {
				return getGames();
			}
			if (args[0].equalsIgnoreCase("shop") && args[1].equalsIgnoreCase("remove") && sender.hasPermission("bedwarsscoreboardaddon.shop.remove")) {
				List<String> list = new ArrayList<String>();
				list.addAll(Config.game_shop_shops.keySet());
				return list;
			}
			if (args[0].equalsIgnoreCase("spawner") && args[1].equalsIgnoreCase("list")) {
				return getGames();
			}
			if (args[0].equalsIgnoreCase("spawner") && args[1].equalsIgnoreCase("list") && sender.hasPermission("bedwarsscoreboardaddon.spawner.list")) {
				return getGames();
			}
			if (args[0].equalsIgnoreCase("spawner") && args[1].equalsIgnoreCase("add") && sender.hasPermission("bedwarsscoreboardaddon.spawner.add")) {
				return getGames();
			}
			if (args[0].equalsIgnoreCase("spawner") && args[1].equalsIgnoreCase("remove") && sender.hasPermission("bedwarsscoreboardaddon.spawner.remove")) {
				List<String> list = new ArrayList<String>();
				list.addAll(Config.game_team_spawners.keySet());
				return list;
			}
		} else if (args.length == 4) {
			if (args[0].equalsIgnoreCase("shop") && args[1].equalsIgnoreCase("set") && (args[2].equalsIgnoreCase("item") || args[2].equalsIgnoreCase("team")) && sender.hasPermission("bedwarsscoreboardaddon.shop.set")) {
				return getGames();
			}
			if (args[0].equalsIgnoreCase("spawner") && args[1].equalsIgnoreCase("add") && sender.hasPermission("bedwarsscoreboardaddon.spawner.add")) {
				String game = args[2];
				return getTeams(game);
			}
		}
		return new ArrayList<String>();
	}

	private List<String> getGames() {
		List<String> list = new ArrayList<String>();
		BedwarsRel.getInstance().getGameManager().getGames().forEach(game -> {
			list.add(game.getName());
		});
		return list;
	}

	private List<String> getTeams(String g) {
		List<String> list = new ArrayList<String>();
		Game game = BedwarsRel.getInstance().getGameManager().getGame(g);
		if (game == null) {
			return list;
		}
		list.addAll(game.getTeams().keySet());
		return list;
	}
}
