package me.ram.bedwarsscoreboardaddon.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import io.github.bedwarsrel.BedwarsRel;

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
			return Arrays.asList("help", "shop", "edit", "reload", "upcheck");
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("shop")) {
				return Arrays.asList("list", "remove", "set");
			}
			if (args[0].equalsIgnoreCase("edit")) {
				List<String> list = new ArrayList<String>();
				BedwarsRel.getInstance().getGameManager().getGames().forEach(game -> {
					list.add(game.getName());
				});
				return list;
			}
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("shop") && args[1].equalsIgnoreCase("set")) {
				return Arrays.asList("item", "team");
			}
			if (args[0].equalsIgnoreCase("shop") && args[1].equalsIgnoreCase("list")
					&& sender.hasPermission("bedwarsscoreboardaddon.shop.list")) {
				List<String> list = new ArrayList<String>();
				BedwarsRel.getInstance().getGameManager().getGames().forEach(game -> {
					list.add(game.getName());
				});
				return list;
			}
		} else if (args.length == 4 && args[0].equalsIgnoreCase("shop") && args[1].equalsIgnoreCase("set")
				&& (args[2].equalsIgnoreCase("item") || args[2].equalsIgnoreCase("team"))
				&& sender.hasPermission("bedwarsscoreboardaddon.shop.set")) {
			List<String> list = new ArrayList<String>();
			BedwarsRel.getInstance().getGameManager().getGames().forEach(game -> {
				list.add(game.getName());
			});
			return list;
		}
		return new ArrayList<String>();
	}
}
