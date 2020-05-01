package me.ram.bedwarsscoreboardaddon;

import java.util.concurrent.Callable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import me.ram.bedwarsscoreboardaddon.addon.GiveItem;
import me.ram.bedwarsscoreboardaddon.addon.HidePlayer;
import me.ram.bedwarsscoreboardaddon.addon.LobbyScoreBoard;
import me.ram.bedwarsscoreboardaddon.addon.Shop;
import me.ram.bedwarsscoreboardaddon.addon.SpawnNoBuild;
import me.ram.bedwarsscoreboardaddon.addon.Spectator;
import me.ram.bedwarsscoreboardaddon.addon.TimeTask;
import me.ram.bedwarsscoreboardaddon.addon.Title;
import me.ram.bedwarsscoreboardaddon.addon.WitherBow;
import me.ram.bedwarsscoreboardaddon.command.BedwarsRelCommandTabCompleter;
import me.ram.bedwarsscoreboardaddon.command.CommandTabCompleter;
import me.ram.bedwarsscoreboardaddon.command.Commands;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.edit.EditGame;
import me.ram.bedwarsscoreboardaddon.listener.EventListener;
import me.ram.bedwarsscoreboardaddon.listener.XPEventListener;
import me.ram.bedwarsscoreboardaddon.manager.ArenaManager;
import me.ram.bedwarsscoreboardaddon.manager.EditHolographicManager;
import me.ram.bedwarsscoreboardaddon.manager.HolographicManager;
import me.ram.bedwarsscoreboardaddon.metrics.Metrics;
import me.ram.bedwarsscoreboardaddon.networld.UpdateCheck;

public class Main extends JavaPlugin {

	private static Main instance;
	private ArenaManager arenamanager;
	@Getter
	private EditHolographicManager editHolographicManager;
	@Getter
	private HolographicManager holographicManager;

	public static Main getInstance() {
		return instance;
	}

	public static String getVersion() {
		return "2.10.3";
	}

	public ArenaManager getArenaManager() {
		return arenamanager;
	}

	public void onEnable() {
		if (!getDescription().getName().equals("BedwarsScoreBoardAddon")
				|| !getDescription().getVersion().equals(getVersion())) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		instance = this;
		arenamanager = new ArenaManager();
		editHolographicManager = new EditHolographicManager();
		holographicManager = new HolographicManager();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Bukkit.getPluginManager().getPlugin("BedwarsRel") == null
						|| Bukkit.getPluginManager().getPlugin("Citizens") == null
						|| Bukkit.getPluginManager().getPlugin("ProtocolLib") == null
						|| (Bukkit.getPluginManager().isPluginEnabled("BedwarsRel")
								&& Bukkit.getPluginManager().isPluginEnabled("Citizens")
								&& Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))) {
					cancel();
					Bukkit.getConsoleSender().sendMessage("§f========================================");
					Bukkit.getConsoleSender().sendMessage("§7");
					Bukkit.getConsoleSender().sendMessage("         §bBedwarsScoreBoardAddon");
					Bukkit.getConsoleSender().sendMessage("§7");
					Bukkit.getConsoleSender().sendMessage(" §a版本: " + Main.getVersion());
					Bukkit.getConsoleSender().sendMessage("§7");
					Bukkit.getConsoleSender().sendMessage(" §a作者: Ram");
					Bukkit.getConsoleSender().sendMessage("§7");
					Bukkit.getConsoleSender().sendMessage("§f========================================");
					init();
				}
			}
		}.runTaskTimer(this, 0L, 0L);
	}

	private void init() {
		Boolean debug = false;
		try {
			debug = this.getConfig().getBoolean("init_debug");
		} catch (Exception e) {
		}
		String prefix = "[" + this.getDescription().getName() + "] ";
		Bukkit.getConsoleSender().sendMessage(prefix + "§f开始加载插件...");
		if (Bukkit.getPluginManager().getPlugin("BedwarsRel") != null) {
			if (!Bukkit.getPluginManager().getPlugin("BedwarsRel").getDescription().getVersion().equals("1.3.6")) {
				Bukkit.getConsoleSender().sendMessage(prefix + "§c错误: §fBedwarsRel版本过低！");
				Bukkit.getConsoleSender().sendMessage(prefix + "§c插件加载失败！");
				Bukkit.getPluginManager().disablePlugin(instance);
				return;
			}
		} else {
			Bukkit.getConsoleSender().sendMessage(prefix + "§c错误: §f缺少必要前置 §aBedwarsRel");
			Bukkit.getConsoleSender().sendMessage(prefix + "§c插件加载失败！");
			Bukkit.getPluginManager().disablePlugin(instance);
			return;
		}
		if (Bukkit.getPluginManager().getPlugin("Citizens") == null) {
			Bukkit.getConsoleSender().sendMessage(prefix + "§c错误: §f缺少必要前置 §aCitizens");
			Bukkit.getConsoleSender().sendMessage(prefix + "§c插件加载失败！");
			Bukkit.getPluginManager().disablePlugin(instance);
			return;
		}
		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
			Bukkit.getConsoleSender().sendMessage(prefix + "§c错误: §f缺少必要前置 §aProtocolLib");
			Bukkit.getConsoleSender().sendMessage(prefix + "§c插件加载失败！");
			Bukkit.getPluginManager().disablePlugin(instance);
			return;
		}
		if (Bukkit.getPluginManager().isPluginEnabled("BedwarsXP")) {
			try {
				Class.forName("ldcr.BedwarsXP.EventListeners").getConstructor().newInstance();
				Plugin plugin = Bukkit.getPluginManager().getPlugin("BedwarsXP");
				for (RegisteredListener listener : HandlerList.getRegisteredListeners(plugin)) {
					if (listener.getListener() instanceof EventListeners) {
						HandlerList.unregisterAll(listener.getListener());
					}
				}
				Bukkit.getPluginManager().registerEvents(new XPEventListener(), this);
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(prefix + "§c错误: §f暂不支持该版本§aBedwarsXP");
				Bukkit.getConsoleSender().sendMessage(prefix + "§c插件加载失败！");
				Bukkit.getPluginManager().disablePlugin(instance);
				return;
			}
		}
		try {
			Config.loadConfig();
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(prefix + "§c错误: §f配置文件加载失败！");
			Bukkit.getConsoleSender().sendMessage(prefix + "§c插件加载失败！");
			Bukkit.getPluginManager().disablePlugin(instance);
			if (debug) {
				e.printStackTrace();
			}
			return;
		}
		try {
			Bukkit.getConsoleSender().sendMessage(prefix + "§f正在注册监听器...");
			this.registerEvents();
			Bukkit.getConsoleSender().sendMessage(prefix + "§a监听器注册成功！");
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(prefix + "§c错误: §f监听器注册失败！");
			Bukkit.getConsoleSender().sendMessage(prefix + "§c插件加载失败！");
			Bukkit.getPluginManager().disablePlugin(instance);
			if (debug) {
				e.printStackTrace();
			}
			return;
		}
		try {
			Bukkit.getConsoleSender().sendMessage(prefix + "§f正在注册指令...");
			Bukkit.getPluginCommand("bedwarsscoreboardaddon").setExecutor(new Commands());
			Bukkit.getPluginCommand("bedwarsscoreboardaddon").setTabCompleter(new CommandTabCompleter());
			Bukkit.getPluginCommand("bw").setTabCompleter(new BedwarsRelCommandTabCompleter());
			Bukkit.getConsoleSender().sendMessage(prefix + "§a指令注册成功！");
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(prefix + "§c错误: §f指令注册失败！");
			Bukkit.getConsoleSender().sendMessage(prefix + "§c插件加载失败！");
			Bukkit.getPluginManager().disablePlugin(instance);
			if (debug) {
				e.printStackTrace();
			}
			return;
		}
		Bukkit.getConsoleSender().sendMessage(prefix + "§a插件加载成功！");
		try {
			Metrics metrics = new Metrics(this);
			metrics.addCustomChart(new Metrics.SimplePie("pluginPrefix", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return BedwarsRel.getInstance().getConfig().getString("chat-prefix",
							ChatColor.GRAY + "[" + ChatColor.AQUA + "BedWars" + ChatColor.GRAY + "]");
				}
			}));
			metrics.addCustomChart(new Metrics.SimplePie("language", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "Chinese";
				}
			}));
		} catch (Exception e) {
		}
		BedwarsRel.getInstance().getConfig().set("teamname-on-tab", false);
		BedwarsRel.getInstance().saveConfig();
	}

	private void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getPluginManager().registerEvents(new LobbyScoreBoard(), this);
		Bukkit.getPluginManager().registerEvents(new SpawnNoBuild(), this);
		Bukkit.getPluginManager().registerEvents(new UpdateCheck(), this);
		Bukkit.getPluginManager().registerEvents(new ChatFormat(), this);
		Bukkit.getPluginManager().registerEvents(new HidePlayer(), this);
		Bukkit.getPluginManager().registerEvents(new WitherBow(), this);
		Bukkit.getPluginManager().registerEvents(new DeathItem(), this);
		Bukkit.getPluginManager().registerEvents(new Spectator(), this);
		Bukkit.getPluginManager().registerEvents(new GiveItem(), this);
		Bukkit.getPluginManager().registerEvents(new TimeTask(), this);
		Bukkit.getPluginManager().registerEvents(new EditGame(), this);
		Bukkit.getPluginManager().registerEvents(new Compass(), this);
		Bukkit.getPluginManager().registerEvents(new Title(), this);
		Bukkit.getPluginManager().registerEvents(new Shop(), this);
	}
}
