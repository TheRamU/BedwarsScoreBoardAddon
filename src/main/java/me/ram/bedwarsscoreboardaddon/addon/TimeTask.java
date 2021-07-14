package me.ram.bedwarsscoreboardaddon.addon;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.game.Game;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;

public class TimeTask {

	@Getter
	private Game game;
	@Getter
	private Arena arena;

	public TimeTask(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		for (String cmd : Config.timecommand_startcommand) {
			if (cmd.equals("")) {
				continue;
			}
			if (cmd.contains("{player}")) {
				for (Player player : game.getPlayers()) {
					Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getServer().getConsoleSender(), ColorUtil.color(cmd.replace("{player}", player.getName())));
				}
			} else {
				Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getServer().getConsoleSender(), ColorUtil.color(cmd));
			}
		}
		for (String cmds : Main.getInstance().getConfig().getConfigurationSection("timecommand").getKeys(false)) {
			arena.addGameTask(new BukkitRunnable() {
				int gametime = Main.getInstance().getConfig().getInt("timecommand." + cmds + ".gametime");
				List<String> cmdlist = Main.getInstance().getConfig().getStringList("timecommand." + cmds + ".command");

				@Override
				public void run() {
					if (game.getTimeLeft() <= gametime) {
						for (String cmd : cmdlist) {
							if (cmd.equals("")) {
								continue;
							}
							if (cmd.contains("{player}")) {
								for (Player player : game.getPlayers()) {
									Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getServer().getConsoleSender(), ColorUtil.color(cmd.replace("{player}", player.getName())));
								}
							} else {
								Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getServer().getConsoleSender(), ColorUtil.color(cmd));
							}
						}
						cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), 0L, 21L));
		}
	}
}
