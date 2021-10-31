package me.ram.bedwarsscoreboardaddon.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.config.EnumLocale;
import me.ram.bedwarsscoreboardaddon.utils.NetworkUtil;
import me.ram.bedwarsscoreboardaddon.utils.UnicodeUtil;

public class UpdateCheck implements Listener {

	private static String version;
	private static String post;
	private static String[] update;
	private static Map<EnumLocale, List<String>> locale_urls;

	public UpdateCheck() {
		locale_urls = new HashMap<EnumLocale, List<String>>();
		String url1 = "https://code.aliyun.com/TheRamU/Update/raw/master/BedwarsScoreBoardAddon-Chinese.txt";
		String url2 = "https://raw.githubusercontent.com/TheRamU/Update/master/BedwarsScoreBoardAddon-Chinese.txt";
		String url3 = "https://raw.githubusercontent.com/TheRamU/Update/master/BedwarsScoreBoardAddon-English.txt";
		locale_urls.put(EnumLocale.ZH_CN, Arrays.asList(url1, url2));
		locale_urls.put(EnumLocale.ZH_TW, Arrays.asList(url1, url2));
		locale_urls.put(EnumLocale.EN_US, Arrays.asList(url3));
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			if (Config.update_check_enabled) {
				Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
					connectUrl();
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
						if (Config.update_check_enabled && version != null && post != null && update != null && !version.equals(Main.getVersion())) {
							sendInfo(Bukkit.getConsoleSender(), version, post, update);
						}
					}, 100L);
				});
			}
			Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
				if (Config.update_check_enabled) {
					connectUrl();
				}
			}, 20 * 86400, 20 * 86400);
		}, 5);
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		if (Config.update_check_enabled && Config.update_check_report && version != null && post != null && update != null) {
			if (e.getPlayer().hasPermission("bedwarsscoreboardaddon.updatecheck") && !version.equals(Main.getVersion())) {
				sendInfo(e.getPlayer(), version, post, update);
			}
		}
	}

	public static void upCheck(CommandSender sender) {
		sender.sendMessage((String) Main.getInstance().getLocaleConfig().getLanguage("update_checking"));
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			if (connectUrl()) {
				if (version.equals(Main.getVersion())) {
					sender.sendMessage((String) Main.getInstance().getLocaleConfig().getLanguage("no_update"));
				} else {
					sendInfo(sender, version, post, update);
				}
			} else {
				sender.sendMessage((String) Main.getInstance().getLocaleConfig().getLanguage("update_check_failed"));
			}
		});
	}

	private static void sendInfo(CommandSender sender, String ver, String p, String[] u) {
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			sender.sendMessage("§f=====================================================");
			sender.sendMessage("§7 ");
			sender.sendMessage("               §aBedwarsScoreBoardAddon");
			sender.sendMessage("§7 ");
			sender.sendMessage("  §e" + Main.getInstance().getLocaleConfig().getLanguage("update_info"));
			sender.sendMessage("§7 ");
			sender.sendMessage("  §f" + Main.getInstance().getLocaleConfig().getLanguage("running_version") + ": §a" + Main.getVersion());
			sender.sendMessage("  §f" + Main.getInstance().getLocaleConfig().getLanguage("update_version") + ": §a" + ver);
			sender.sendMessage("§7 ");
			sender.sendMessage("  §f" + Main.getInstance().getLocaleConfig().getLanguage("updates") + ": ");
			for (int i = 0; i < u.length; i++) {
				sender.sendMessage("    §f" + (i + 1) + ". §e" + UnicodeUtil.unicodeToCn(u[i]));
			}
			sender.sendMessage("§7 ");
			sender.sendMessage("  §f" + Main.getInstance().getLocaleConfig().getLanguage("update_download") + ": §b§n" + UnicodeUtil.unicodeToCn(p));
			sender.sendMessage("");
			sender.sendMessage("§f=====================================================");
		}, 5L);
	}

	private static boolean connectUrl() {
		String document = getDocument();
		if (document != null && !document.equals("")) {
			String[] info = document.split(",");
			version = info[0];
			post = info[1];
			update = info[2].split(";");
			return true;
		}
		return false;
	}

	private static String getDocument() {
		List<String> list = locale_urls.get(Main.getInstance().getLocaleConfig().getPluginLocale());
		for (String url : list) {
			String document = NetworkUtil.getDocument(url);
			if (document != null) {
				return document;
			}
		}
		return null;
	}
}
