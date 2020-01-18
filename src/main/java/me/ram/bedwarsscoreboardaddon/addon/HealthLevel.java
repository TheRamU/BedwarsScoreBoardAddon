package me.ram.bedwarsscoreboardaddon.addon;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonSetHealthEvent;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class HealthLevel {

	private Map<String, String> leveltime;
	private Integer nowhealth;

	public Map<String, String> getLevelTime() {
		return leveltime;
	}

	public Integer getNowHealth() {
		return nowhealth;
	}

	public HealthLevel(Game game) {
		leveltime = new HashMap<String, String>();
		nowhealth = 20;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Config.sethealth_start_enabled) {
					for (Player player : game.getPlayers()) {
						nowhealth = Config.sethealth_start_health;
						player.setMaxHealth(Config.sethealth_start_health);
						player.setHealth(player.getMaxHealth());
						if (!Config.start_title_enabled && (!Config.sethealth_start_title.equals("")
								|| !Config.sethealth_start_subtitle.equals(""))) {
							Utils.sendTitle(player, 10, 50, 10, Config.sethealth_start_title,
									Config.sethealth_start_subtitle);
						}
						if (!Config.sethealth_start_message.equals("")) {
							player.sendMessage(Config.sethealth_start_message);
						}
					}
				}
			}
		}.runTaskLater(Main.getInstance(), 0L);
		for (String sh : Main.getInstance().getConfig().getConfigurationSection("sethealth").getKeys(false)) {
			if (!sh.equals("start")) {
				new BukkitRunnable() {
					int gametime = Main.getInstance().getConfig().getInt("sethealth." + sh + ".gametime");
					int maxhealth = Main.getInstance().getConfig().getInt("sethealth." + sh + ".health");
					String title = Main.getInstance().getConfig().getString("sethealth." + sh + ".title");
					String subtitle = Main.getInstance().getConfig().getString("sethealth." + sh + ".subtitle");
					String message = Main.getInstance().getConfig().getString("sethealth." + sh + ".message");
					Boolean isExecuted = false;

					@Override
					public void run() {
						if (game.getState() == GameState.RUNNING) {
							if (isExecuted) {
								cancel();
								return;
							}
							int remtime = game.getTimeLeft() - gametime;
							String formatremtime = remtime / 60 + ":"
									+ ((remtime % 60 < 10) ? ("0" + remtime % 60) : (remtime % 60));
							leveltime.put(sh, formatremtime);
							if (game.getTimeLeft() <= gametime) {
								isExecuted = true;
								BoardAddonSetHealthEvent setHealthEvent = new BoardAddonSetHealthEvent(game);
								Bukkit.getPluginManager().callEvent(setHealthEvent);
								if (setHealthEvent.isCancelled()) {
									cancel();
									return;
								}
								nowhealth = maxhealth;
								for (Player player : game.getPlayers()) {
									double dhealth = maxhealth - player.getMaxHealth();
									player.setMaxHealth(maxhealth);
									if (dhealth > 0) {
										double nhealth = player.getHealth() + dhealth;
										nhealth = nhealth > maxhealth ? maxhealth : nhealth;
										player.setHealth(nhealth);
									}
									if (!title.equals("") || !subtitle.equals("")) {
										Utils.sendTitle(player, 10, 50, 10, ColorUtil.color(title),
												ColorUtil.color(subtitle));
									}
									if (!message.equals("")) {
										player.sendMessage(ColorUtil.color(message));
									}
								}
								PlaySound.playSound(game, Config.play_sound_sound_sethealth);
								cancel();
							}
						} else {
							cancel();
						}
					}
				}.runTaskTimer(Main.getInstance(), 0L, 21L);
			}
		}
	}
}
