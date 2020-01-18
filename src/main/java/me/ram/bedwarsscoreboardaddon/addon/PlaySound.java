package me.ram.bedwarsscoreboardaddon.addon;

import java.util.List;
import java.util.Random;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import io.github.bedwarsrel.game.Game;
import me.ram.bedwarsscoreboardaddon.config.Config;

public class PlaySound {

	public static void playSound(Game game, List<String> list) {
		if (Config.play_sound_enabled) {
			String sound = getRandomSound(list);
			if (sound != null) {
				String[] ary = sound.split(",");
				Sound playsound = getSound(ary[0]);
				if (playsound == null) {
					return;
				}
				for (Player player : game.getPlayers()) {
					player.playSound(player.getLocation(), playsound, Float.valueOf(ary[1]), Float.valueOf(ary[2]));
				}
			}
		}
	}

	public static void playSound(Player player, List<String> list) {
		if (Config.play_sound_enabled) {
			String sound = getRandomSound(list);
			if (sound == null) {
				return;
			}
			String[] ary = sound.split(",");
			Sound playsound = getSound(ary[0]);
			if (playsound == null) {
				return;
			}
			player.playSound(player.getLocation(), playsound, Float.valueOf(ary[1]), Float.valueOf(ary[2]));
		}
	}

	private static String getRandomSound(List<String> list) {
		if (list.size() > 0) {
			Random random = new Random();
			int n = random.nextInt(list.size());
			return list.get(n);
		}
		return null;
	}

	private static Sound getSound(String name) {
		try {
			Sound sound = Sound.valueOf(name);
			return sound;
		} catch (Exception e) {
			return null;
		}
	}
}
