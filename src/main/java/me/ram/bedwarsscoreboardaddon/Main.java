package me.ram.bedwarsscoreboardaddon;

import java.util.concurrent.Callable;

import org.bstats.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.BedwarsRel;
import ldcr.BedwarsXP.EventListeners;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.addon.ChatFormat;
import me.ram.bedwarsscoreboardaddon.addon.Compass;
import me.ram.bedwarsscoreboardaddon.addon.DeathItem;
import me.ram.bedwarsscoreboardaddon.addon.FastRespawn;
import me.ram.bedwarsscoreboardaddon.addon.GiveItem;
import me.ram.bedwarsscoreboardaddon.addon.HidePlayer;
import me.ram.bedwarsscoreboardaddon.addon.LobbyScoreBoard;
import me.ram.bedwarsscoreboardaddon.addon.SpawnNoBuild;
import me.ram.bedwarsscoreboardaddon.addon.Spectator;
import me.ram.bedwarsscoreboardaddon.addon.Title;
import me.ram.bedwarsscoreboardaddon.addon.WitherBow;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.commands.BedwarsRelCommandTabCompleter;
import me.ram.bedwarsscoreboardaddon.commands.CommandTabCompleter;
import me.ram.bedwarsscoreboardaddon.commands.Commands;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.config.LocaleConfig;
import me.ram.bedwarsscoreboardaddon.edit.EditGame;
import me.ram.bedwarsscoreboardaddon.listener.EventListener;
import me.ram.bedwarsscoreboardaddon.listener.GameListener;
import me.ram.bedwarsscoreboardaddon.listener.ShopListener;
import me.ram.bedwarsscoreboardaddon.listener.XPEventListener;
import me.ram.bedwarsscoreboardaddon.manager.ArenaManager;
import me.ram.bedwarsscoreboardaddon.manager.EditHolographicManager;
import me.ram.bedwarsscoreboardaddon.manager.HolographicManager;
import me.ram.bedwarsscoreboardaddon.menu.MenuManager;
import me.ram.bedwarsscoreboardaddon.network.UpdateCheck;

/**
 * @author Ram
 * @version 2.13.1
 */
public class Main extends JavaPlugin {

	@Getter
	private static Main instance;
	@Getter
	private ArenaManager arenaManager;
	@Getter
	private EditHolographicManager editHolographicManager;
	@Getter
	private HolographicManager holographicManager;
	@Getter
	private MenuManager menuManager;
	@Getter
	private LocaleConfig localeConfig;
	@Getter
	private boolean enabledCitizens;

	public static String getVersion() {
		return "2.13.1";
	}

	@Override
	public FileConfiguration getConfig() {
		FileConfiguration config = Config.getConfig();
		return config == null ? super.getConfig() : config;
	}

	public void onEnable() {
		if (!getDescription().getName().equals("BedwarsScoreBoardAddon") || !getDescription().getVersion().equals(getVersion()) || !getDescription().getAuthors().contains("Ram")) {
			try {
				new Exception("Please don't edit plugin.yml!").printStackTrace();
			} catch (Exception e) {
			}
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		instance = this;
		arenaManager = new ArenaManager();
		editHolographicManager = new EditHolographicManager();
		holographicManager = new HolographicManager();
		menuManager = new MenuManager();
		localeConfig = new LocaleConfig();
		Main.getInstance().getLocaleConfig().loadLocaleConfig();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Bukkit.getPluginManager().getPlugin("BedwarsRel") == null || Bukkit.getPluginManager().getPlugin("Citizens") == null || Bukkit.getPluginManager().getPlugin("ProtocolLib") == null || (Bukkit.getPluginManager().isPluginEnabled("BedwarsRel") && Bukkit.getPluginManager().isPluginEnabled("Citizens") && Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))) {
					cancel();
					printMessage("§f===========================================================");
					printMessage("§7 ");
					printMessage("§b                  BedwarsScoreBoardAddon");
					printMessage("§7 ");
					printMessage("§7 ");
					printMessage("§f  " + getLocaleConfig().getLanguage("version") + ": §a" + Main.getVersion());
					printMessage("§7 ");
					printMessage("§f  " + getLocaleConfig().getLanguage("author") + ": §aRam");
					printMessage("§7 ");
					printMessage("§f  " + getLocaleConfig().getLanguage("website") + ": §ehttps://github.com/TheRamU/BedwarsScoreBoardAddon");
					printMessage("§7 ");
					printMessage("§f===========================================================");
					init();
				}
			}
		}.runTaskTimer(this, 1L, 1L);
	}

	public void onDisable() {
		if (instance == null) {
			return;
		}
		menuManager.getPlayers().forEach(player -> {
			if (player.isOnline()) {
				player.closeInventory();
			}
		});
		for (Arena arena : arenaManager.getArenas().values()) {
			arena.onDisable();
		}
		editHolographicManager.removeAll();
	}

	private void init() {
		Boolean debug = false;
		try {
			debug = getConfig().getBoolean("init_debug");
		} catch (Exception e) {
		}
		String prefix = "[" + getDescription().getName() + "] ";
		printMessage(prefix + getLocaleConfig().getLanguage("loading"));
		boolean isDependent = true;
		if (Bukkit.getPluginManager().getPlugin("BedwarsRel") != null) {
			if (!Bukkit.getPluginManager().getPlugin("BedwarsRel").getDescription().getVersion().equals("1.3.6")) {
				printMessage(prefix + getLocaleConfig().getLanguage("bedwarsrel_incompatible"));
				isDependent = false;
			}
		} else {
			printMessage(prefix + getLocaleConfig().getLanguage("no_bedwarsrel"));
			isDependent = false;
		}
		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
			printMessage(prefix + getLocaleConfig().getLanguage("no_protocollib"));
			isDependent = false;
		}
		if (!isDependent) {
			printMessage(prefix + getLocaleConfig().getLanguage("loading_failed"));
			Bukkit.getPluginManager().disablePlugin(instance);
			return;
		}
		enabledCitizens = Bukkit.getPluginManager().getPlugin("Citizens") != null;
		if (Bukkit.getPluginManager().isPluginEnabled("BedwarsXP")) {
			try {
				new ldcr.BedwarsXP.EventListeners();
				Plugin plugin = Bukkit.getPluginManager().getPlugin("BedwarsXP");
				for (RegisteredListener listener : HandlerList.getRegisteredListeners(plugin)) {
					if (listener.getListener() instanceof EventListeners) {
						HandlerList.unregisterAll(listener.getListener());
					}
				}
				Bukkit.getPluginManager().registerEvents(new XPEventListener(), this);
			} catch (Exception e) {
				printMessage(prefix + getLocaleConfig().getLanguage("bedwarsxp"));
				printMessage(prefix + getLocaleConfig().getLanguage("loading_failed"));
				Bukkit.getPluginManager().disablePlugin(instance);
				return;
			}
		}
		try {
			Config.loadConfig();
		} catch (Exception e) {
			printMessage(prefix + getLocaleConfig().getLanguage("config_failed"));
			printMessage(prefix + getLocaleConfig().getLanguage("loading_failed"));
			Bukkit.getPluginManager().disablePlugin(instance);
			if (debug) {
				e.printStackTrace();
			}
			return;
		}
		try {
			printMessage(prefix + getLocaleConfig().getLanguage("register_listener"));
			this.registerEvents();
			printMessage(prefix + getLocaleConfig().getLanguage("listener_success"));
		} catch (Exception e) {
			printMessage(prefix + getLocaleConfig().getLanguage("listener_failed"));
			printMessage(prefix + getLocaleConfig().getLanguage("loading_failed"));
			Bukkit.getPluginManager().disablePlugin(instance);
			if (debug) {
				e.printStackTrace();
			}
			return;
		}
		try {
			printMessage(prefix + getLocaleConfig().getLanguage("register_command"));
			Bukkit.getPluginCommand("bedwarsscoreboardaddon").setExecutor(new Commands());
			Bukkit.getPluginCommand("bedwarsscoreboardaddon").setTabCompleter(new CommandTabCompleter());
			Bukkit.getPluginCommand("bw").setTabCompleter(new BedwarsRelCommandTabCompleter());
			printMessage(prefix + getLocaleConfig().getLanguage("command_success"));
		} catch (Exception e) {
			printMessage(prefix + getLocaleConfig().getLanguage("command_failed"));
			printMessage(prefix + getLocaleConfig().getLanguage("loading_failed"));
			Bukkit.getPluginManager().disablePlugin(instance);
			if (debug) {
				e.printStackTrace();
			}
			return;
		}
		printMessage(prefix + getLocaleConfig().getLanguage("load_success"));
		try {
			Metrics metrics = new Metrics(this);
			metrics.addCustomChart(new Metrics.SimplePie("pluginPrefix", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return BedwarsRel.getInstance().getConfig().getString("chat-prefix", ChatColor.GRAY + "[" + ChatColor.AQUA + "BedWars" + ChatColor.GRAY + "]");
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("language", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return localeConfig.getPluginLocale().getName();
				}
			}));
		} catch (Exception e) {
		}
		BedwarsRel.getInstance().getConfig().set("teamname-on-tab", false);
		BedwarsRel.getInstance().saveConfig();
	}

	private void printMessage(String str) {
		Bukkit.getConsoleSender().sendMessage(str);
	}

	private void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginManager().registerEvents(new GameListener(), this);
		Bukkit.getPluginManager().registerEvents(new LobbyScoreBoard(), this);
		Bukkit.getPluginManager().registerEvents(new SpawnNoBuild(), this);
		Bukkit.getPluginManager().registerEvents(new UpdateCheck(), this);
		if (enabledCitizens) {
			Bukkit.getPluginManager().registerEvents(new ShopListener(), this);
		}
		Bukkit.getPluginManager().registerEvents(new FastRespawn(), this);
		Bukkit.getPluginManager().registerEvents(new ChatFormat(), this);
		Bukkit.getPluginManager().registerEvents(new HidePlayer(), this);
		Bukkit.getPluginManager().registerEvents(new WitherBow(), this);
		Bukkit.getPluginManager().registerEvents(new DeathItem(), this);
		Bukkit.getPluginManager().registerEvents(new Spectator(), this);
		Bukkit.getPluginManager().registerEvents(new GiveItem(), this);
		Bukkit.getPluginManager().registerEvents(new EditGame(), this);
		Bukkit.getPluginManager().registerEvents(new Compass(), this);
		Bukkit.getPluginManager().registerEvents(new Title(), this);
	}
}
