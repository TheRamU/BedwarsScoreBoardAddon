package me.ram.bedwarsscoreboardaddon.addon;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.game.GameState;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;

public class TimeTask implements Listener {

	@EventHandler
	public void onStarted(BedwarsGameStartedEvent e) {
		for (String cmd : Config.timecommand_startcommand) {
			if (!cmd.equals("")) {
				if (cmd.contains("{player}")) {
					for (Player player : e.getGame().getPlayers()) {
						Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getServer().getConsoleSender(), ColorUtil.color(cmd.replace("{player}", player.getName())));
					}
				} else {
					Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getServer().getConsoleSender(), ColorUtil.color(cmd));
				}
			}
		}
		for (String cmds : Main.getInstance().getConfig().getConfigurationSection("timecommand").getKeys(false)) {
			new BukkitRunnable() {
				int gametime = Main.getInstance().getConfig().getInt("timecommand." + cmds + ".gametime");
				List<String> cmdlist = Main.getInstance().getConfig().getStringList("timecommand." + cmds + ".command");
				Boolean isExecuted = false;

				@Override
				public void run() {
					if (e.getGame().getState() == GameState.RUNNING) {
						if (isExecuted) {
							cancel();
							return;
						}
						if (e.getGame().getTimeLeft() <= gametime) {
							isExecuted = true;
							for (String cmd : cmdlist) {
								if (!cmd.equals("")) {
									if (cmd.contains("{player}")) {
										for (Player player : e.getGame().getPlayers()) {
											Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getServer().getConsoleSender(), ColorUtil.color(cmd.replace("{player}", player.getName())));
										}
									} else {
										Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getServer().getConsoleSender(), ColorUtil.color(cmd));
									}
								}
							}
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
