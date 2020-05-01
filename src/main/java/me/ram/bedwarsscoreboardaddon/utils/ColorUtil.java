package me.ram.bedwarsscoreboardaddon.utils;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

public class ColorUtil {

	public static String color(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static List<String> listcolor(List<String> list) {
		List<String> clist = new ArrayList<String>();
		for (String l : list) {
			clist.add(ChatColor.translateAlternateColorCodes('&', l));
		}
		return clist;
	}

	public static String remcolor(String s) {
		return ChatColor.stripColor(s);
	}

	public static List<String> remlistcolor(List<String> list) {
		List<String> clist = new ArrayList<String>();
		for (String l : list) {
			clist.add(ChatColor.stripColor(l));
		}
		return clist;
	}
}
