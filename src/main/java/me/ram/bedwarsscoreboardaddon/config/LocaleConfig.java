package me.ram.bedwarsscoreboardaddon.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;

public class LocaleConfig {

	@Getter
	private EnumLocale pluginLocale;
	private Map<String, Object> language;

	public LocaleConfig() {
		language = new HashMap<String, Object>();
	}

	private void loadLanguage() {
		switch (pluginLocale) {
		case ZH_CN:
			language.put("version", "版本");
			language.put("author", "作者");
			language.put("website", "网站");
			language.put("loading", "§f开始加载插件...");
			language.put("loading_failed", "§c插件加载失败！");
			language.put("bedwarsrel_old", "§c错误: §fBedwarsRel版本过低！");
			language.put("no_bedwarsrel", "§c错误: §f缺少必要前置 §aBedwarsRel");
			language.put("no_citizens", "§c错误: §f缺少必要前置 §aCitizens");
			language.put("no_protocollib", "§c错误: §f缺少必要前置 §aProtocolLib");
			language.put("bedwarsxp", "§c错误: §f暂不支持该版本§aBedwarsXP");
			language.put("config_failed", "§c错误: §f配置文件加载失败！");
			language.put("register_listener", "§f正在注册监听器...");
			language.put("listener_success", "§a监听器注册成功！");
			language.put("listener_failed", "§c错误: §f监听器注册失败！");
			language.put("register_command", "§f正在注册指令...");
			language.put("command_success", "§a指令注册成功！");
			language.put("command_failed", "§c错误: §f指令注册失败！");
			language.put("load_success", "§a插件加载成功！");
			language.put("loading_config", "§f正在加载配置文件...");
			language.put("saved_config", "§a默认配置文件已保存！");
			language.put("config_success", "§a配置文件加载成功！");
			language.put("update_checking", "§b§lBWSBA §f>> §a正在检测更新...");
			language.put("no_update", "§b§lBWSBA §f>> §a您使用的已是最新版本！");
			language.put("update_check_failed", "§b§lBWSBA §f>> §c检测更新失败，请检查服务器网络连接！");
			language.put("update_info", "检测到版本更新！");
			language.put("running_version", "当前版本");
			language.put("update_version", "更新版本");
			language.put("updates", "更新内容");
			language.put("update_download", "更新地址");
			break;
		case EN_US:
			language.put("version", "Version");
			language.put("author", "Author");
			language.put("website", "Website");
			language.put("loading", "§fInitialization...");
			language.put("loading_failed", "§cInitialization failed!");
			language.put("bedwarsrel_old", "§cError: §fOutdated version of BedwarsRel!");
			language.put("no_bedwarsrel", "§cError: §aBedwarsRel §fnot loaded!");
			language.put("no_citizens", "§cError: §aCitizens §fnot loaded!");
			language.put("no_protocollib", "§cError: §aProtocolLib §fnot loaded!");
			language.put("bedwarsxp", "§cError: §fNot compatible with this version of §aBedwarsXP");
			language.put("config_failed", "§cError: §fConfiguration load exception!");
			language.put("register_listener", "§fRegistering listener...");
			language.put("listener_success", "§aRegistered listener!");
			language.put("listener_failed", "§cError: §fListener registration exception!");
			language.put("register_command", "§fRegister commands...");
			language.put("command_success", "§aRegistered commands!");
			language.put("command_failed", "§cError: §fCommand registration exception!");
			language.put("load_success", "§aComplete!");
			language.put("loading_config", "§fLoading configuration...");
			language.put("saved_config", "§aConfiguration saved!");
			language.put("config_success", "§aConfiguration loaded!");
			language.put("update_checking", "§b§lBWSBA §f>> §aUpdate check...");
			language.put("no_update", "§b§lBWSBA §f>> §aYou are running the latest version!");
			language.put("update_check_failed", "§b§lBWSBA §f>> §cUpdate check failed! Please check the server network!");
			language.put("update_info", "There are version update!");
			language.put("running_version", "Running version");
			language.put("update_version", "Update version");
			language.put("updates", "Updates");
			language.put("update_download", "Download");
			break;
		case ZH_TW:
			language.put("version", "版本");
			language.put("author", "作者");
			language.put("website", "網站");
			language.put("loading", "§f開始加載插件...");
			language.put("loading_failed", "§c插件加載失敗！");
			language.put("bedwarsrel_old", "§c錯誤: §fBedwarsRel版本過低！");
			language.put("no_bedwarsrel", "§c錯誤: §f缺少必要前置 §aBedwarsRel");
			language.put("no_citizens", "§c錯誤: §f缺少必要前置 §aCitizens");
			language.put("no_protocollib", "§c錯誤: §f缺少必要前置 §aProtocolLib");
			language.put("bedwarsxp", "§c錯誤: §f暫不支持該版本§aBedwarsXP");
			language.put("config_failed", "§c錯誤: §f配置文件加載失敗！");
			language.put("register_listener", "§f正在註冊監聽器...");
			language.put("listener_success", "§a監聽器註冊成功！");
			language.put("listener_failed", "§c錯誤: §f監聽器註冊失敗！");
			language.put("register_command", "§f正在註冊指令...");
			language.put("command_success", "§a指令註冊成功！");
			language.put("command_failed", "§c錯誤: §f指令註冊失敗！");
			language.put("load_success", "§a插件加載成功！");
			language.put("loading_config", "§f正在加載配置文件...");
			language.put("saved_config", "§a默認配置文件已保存！");
			language.put("config_success", "§a配置文件加載成功！");
			language.put("update_checking", "§b§lBWSBA §f>> §a正在檢測更新...");
			language.put("no_update", "§b§lBWSBA §f>> §a您使用的已是最新版本！");
			language.put("update_check_failed", "§b§lBWSBA §f>> §c檢測更新失敗，請檢查服務器網絡連接！");
			language.put("update_info", "檢測到版本更新！");
			language.put("running_version", "當前版本");
			language.put("update_version", "更新版本");
			language.put("updates", "更新內容");
			language.put("update_download", "更新地址");
			break;
		default:
			break;
		}
	}

	public void loadLocaleConfig() {
		File folder = new File(Main.getInstance().getDataFolder(), "/");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder.getAbsolutePath() + "/config.yml");
		if (file.exists()) {
			pluginLocale = getLocaleByName(YamlConfiguration.loadConfiguration(file).getString("locale", "en_US"));
		} else {
			pluginLocale = getSystemLocale();
		}
		loadLanguage();
		saveLocale();
	}

	public Object getLanguage(String str) {
		return language.getOrDefault(str, "null");
	}

	public String getSystemLocaleName() {
		Locale locale = Locale.getDefault();
		return locale.getLanguage() + "_" + locale.getCountry();
	}

	public EnumLocale getSystemLocale() {
		return getLocaleByName(getSystemLocaleName());
	}

	private EnumLocale getLocaleByName(String name) {
		EnumLocale locale = EnumLocale.getByName(name);
		return locale == null ? EnumLocale.EN_US : locale;
	}

	public void saveResource(String resourcePath) {
		try {
			writeToLocal(Main.getInstance().getDataFolder().getPath() + "/" + resourcePath, Main.getInstance().getResource("locale/" + getPluginLocale().getName() + "/" + resourcePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void saveLocale() {
		File folder = new File(Main.getInstance().getDataFolder(), "/locale");
		if (!folder.exists()) {
			folder.mkdirs();
			for (EnumLocale locale : EnumLocale.values()) {
				File locale_folder = new File(folder.getPath(), "/" + locale.getName());
				if (!locale_folder.exists()) {
					locale_folder.mkdirs();
				}
				for (String file : new String[] { "config.yml", "language.yml", "team_shop.yml" }) {
					try {
						writeToLocal(folder.getPath() + "/" + locale.getName() + "/" + file, Main.getInstance().getResource("locale/" + locale.getName() + "/" + file));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static void writeToLocal(String destination, InputStream input) throws IOException {
		int index;
		byte[] bytes = new byte[1024];
		FileOutputStream downloadFile = new FileOutputStream(destination);
		while ((index = input.read(bytes)) != -1) {
			downloadFile.write(bytes, 0, index);
			downloadFile.flush();
		}
		downloadFile.close();
		input.close();
	}
}
