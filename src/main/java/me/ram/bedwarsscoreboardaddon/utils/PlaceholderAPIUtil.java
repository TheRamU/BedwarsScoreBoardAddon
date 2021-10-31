package me.ram.bedwarsscoreboardaddon.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderAPIUtil {

	public static String setPlaceholders(Player player, String text) {
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			return PlaceholderAPI.setPlaceholders(player, text);
		}
		return text;
	}
}
