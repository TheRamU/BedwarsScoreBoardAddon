package me.ram.bedwarsscoreboardaddon.config;

import net.citizensnpcs.api.CitizensAPI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import io.github.bedwarsrel.BedwarsRel;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades.UpgradeType;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;

public class Config {

	private static FileConfiguration file_config;
	private static FileConfiguration language_config;
	public static boolean update_check_enabled;
	public static boolean update_check_report;
	public static boolean hide_player;
	public static boolean tab_health;
	public static boolean tag_health;
	public static boolean item_merge;
	public static boolean hunger_change;
	public static boolean clear_bottle;
	public static boolean fast_respawn;
	public static String date_format;
	public static boolean chat_format_enabled;
	public static boolean chat_format_chat_lobby;
	public static boolean chat_format_chat_all;
	public static boolean chat_format_chat_spectator;
	public static String chat_format_lobby;
	public static String chat_format_lobby_team;
	public static List<String> chat_format_all_prefix;
	public static String chat_format_ingame;
	public static String chat_format_ingame_all;
	public static String chat_format_spectator;
	public static boolean final_killed_enabled;
	public static String final_killed_message;
	public static List<String> timecommand_startcommand;
	public static boolean select_team_enabled;
	public static String select_team_status_select;
	public static String select_team_status_inteam;
	public static String select_team_status_team_full;
	public static String select_team_no_players;
	public static String select_team_item_name;
	public static List<String> select_team_item_lore;
	public static boolean lobby_block_enabled;
	public static int lobby_block_position_1_x;
	public static int lobby_block_position_1_y;
	public static int lobby_block_position_1_z;
	public static int lobby_block_position_2_x;
	public static int lobby_block_position_2_y;
	public static int lobby_block_position_2_z;
	public static boolean rejoin_enabled;
	public static String rejoin_message_rejoin;
	public static String rejoin_message_error;
	public static boolean bowdamage_enabled;
	public static String bowdamage_title;
	public static String bowdamage_subtitle;
	public static String bowdamage_message;
	public static boolean damagetitle_enabled;
	public static String damagetitle_title;
	public static String damagetitle_subtitle;
	public static boolean jointitle_enabled;
	public static String jointitle_title;
	public static String jointitle_subtitle;
	public static boolean die_out_title_enabled;
	public static String die_out_title_title;
	public static String die_out_title_subtitle;
	public static boolean destroyed_title_enabled;
	public static String destroyed_title_title;
	public static String destroyed_title_subtitle;
	public static boolean start_title_enabled;
	public static List<String> start_title_title;
	public static String start_title_subtitle;
	public static boolean victory_title_enabled;
	public static List<String> victory_title_title;
	public static String victory_title_subtitle;
	public static boolean play_sound_enabled;
	public static List<String> play_sound_sound_start;
	public static List<String> play_sound_sound_death;
	public static List<String> play_sound_sound_kill;
	public static List<String> play_sound_sound_upgrade;
	public static List<String> play_sound_sound_no_resource;
	public static List<String> play_sound_sound_sethealth;
	public static List<String> play_sound_sound_enable_witherbow;
	public static List<String> play_sound_sound_witherbow;
	public static List<String> play_sound_sound_deathmode;
	public static List<String> play_sound_sound_over;
	public static boolean spectator_enabled;
	public static boolean spectator_centre_enabled;
	public static double spectator_centre_height;
	public static String spectator_spectator_target_title;
	public static String spectator_spectator_target_subtitle;
	public static String spectator_quit_spectator_title;
	public static String spectator_quit_spectator_subtitle;
	public static boolean spectator_speed_enabled;
	public static int spectator_speed_slot;
	public static int spectator_speed_item;
	public static String spectator_speed_item_name;
	public static List<String> spectator_speed_item_lore;
	public static String spectator_speed_gui_title;
	public static String spectator_speed_no_speed;
	public static String spectator_speed_speed_1;
	public static String spectator_speed_speed_2;
	public static String spectator_speed_speed_3;
	public static String spectator_speed_speed_4;
	public static boolean spectator_fast_join_enabled;
	public static int spectator_fast_join_slot;
	public static int spectator_fast_join_item;
	public static String spectator_fast_join_item_name;
	public static List<String> spectator_fast_join_item_lore;
	public static String spectator_fast_join_group;
	public static boolean compass_enabled;
	public static String compass_item_name;
	public static String compass_back;
	public static List<String> compass_item_lore;
	public static List<String> compass_lore_send_message;
	public static List<String> compass_lore_select_team;
	public static List<String> compass_lore_select_resources;
	public static List<String> compass_resources;
	public static Map<String, String> compass_resources_name;
	public static String compass_gui_title;
	public static String compass_item_III_II;
	public static String compass_item_IV_II;
	public static String compass_item_V_II;
	public static String compass_item_VI_II;
	public static String compass_item_VII_II;
	public static String compass_item_VIII_II;
	public static String compass_item_III_III;
	public static String compass_item_IV_III;
	public static String compass_item_V_III;
	public static String compass_item_VI_III;
	public static String compass_item_VII_III;
	public static String compass_message_III_II;
	public static String compass_message_IV_II;
	public static String compass_message_V_II;
	public static String compass_message_VI_II;
	public static String compass_message_VII_II;
	public static String compass_message_VIII_II;
	public static String compass_message_III_III;
	public static String compass_message_IV_III;
	public static String compass_message_V_III;
	public static String compass_message_VI_III;
	public static String compass_message_VII_III;
	public static boolean graffiti_enabled;
	public static boolean graffiti_holographic_enabled;
	public static String graffiti_holographic_text;
	public static boolean shop_enabled;
	public static String shop_item_shop_type;
	public static String shop_item_shop_skin;
	public static boolean shop_item_shop_look;
	public static String shop_team_shop_type;
	public static String shop_team_shop_skin;
	public static boolean shop_team_shop_look;
	public static List<String> shop_item_shop_name;
	public static List<String> shop_team_shop_name;
	public static boolean respawn_enabled;
	public static boolean respawn_centre_enabled;
	public static double respawn_centre_height;
	public static boolean respawn_protected_enabled;
	public static int respawn_protected_time;
	public static int respawn_respawn_delay;
	public static String respawn_countdown_title;
	public static String respawn_countdown_subtitle;
	public static String respawn_countdown_message;
	public static String respawn_respawn_title;
	public static String respawn_respawn_subtitle;
	public static String respawn_respawn_message;
	public static boolean giveitem_keeparmor;
	public static Map<String, Object> giveitem_armor_helmet_item;
	public static Map<String, Object> giveitem_armor_chestplate_item;
	public static Map<String, Object> giveitem_armor_leggings_item;
	public static Map<String, Object> giveitem_armor_boots_item;
	public static String giveitem_armor_helmet_give;
	public static String giveitem_armor_chestplate_give;
	public static String giveitem_armor_leggings_give;
	public static String giveitem_armor_boots_give;
	public static boolean giveitem_armor_helmet_move;
	public static boolean giveitem_armor_chestplate_move;
	public static boolean giveitem_armor_leggings_move;
	public static boolean giveitem_armor_boots_move;
	public static boolean sethealth_start_enabled;
	public static int sethealth_start_health;
	public static boolean resourcelimit_enabled;
	public static List<String[]> resourcelimit_limit;
	public static boolean spread_resource_enabled;
	public static boolean spread_resource_launch;
	public static double spread_resource_range;
	public static boolean game_chest_enabled;
	public static int game_chest_range;
	public static String game_chest_message;
	public static boolean invisibility_player_enabled;
	public static boolean invisibility_player_footstep;
	public static boolean invisibility_player_hide_particles;
	public static boolean invisibility_player_damage_show_player;
	public static boolean witherbow_enabled;
	public static int witherbow_gametime;
	public static String witherbow_already_starte;
	public static String witherbow_title;
	public static String witherbow_subtitle;
	public static String witherbow_message;
	public static boolean teamshop_enabled;
	public static String teamshop_upgrade_shop_title;
	public static List<String> teamshop_upgrade_shop_frame;
	public static String teamshop_upgrade_shop_trap_item;
	public static String teamshop_upgrade_shop_trap_name;
	public static List<String> teamshop_upgrade_shop_trap_lore;
	public static String teamshop_trap_shop_title;
	public static List<String> teamshop_trap_shop_back;
	public static String teamshop_message_upgrade;
	public static String teamshop_message_no_resource;
	public static String teamshop_state_no_resource;
	public static String teamshop_state_lock;
	public static String teamshop_state_unlock;
	public static int teamshop_trap_cooldown;
	public static List<String> teamshop_trap_trap_list_trap_1_lock;
	public static List<String> teamshop_trap_trap_list_trap_1_unlock;
	public static List<String> teamshop_trap_trap_list_trap_2_lock;
	public static List<String> teamshop_trap_trap_list_trap_2_unlock;
	public static List<String> teamshop_trap_trap_list_trap_3_lock;
	public static List<String> teamshop_trap_trap_list_trap_3_unlock;
	public static Map<Integer, String> teamshop_trap_level_cost;
	public static Map<UpgradeType, Boolean> teamshop_upgrade_enabled;
	public static Map<UpgradeType, String> teamshop_upgrade_item;
	public static Map<UpgradeType, String> teamshop_upgrade_name;
	public static Map<UpgradeType, Map<Integer, String>> teamshop_upgrade_level_cost;
	public static Map<UpgradeType, Map<Integer, List<String>>> teamshop_upgrade_level_lore;
	public static Map<Integer, List<String>> teamshop_upgrade_iron_forge_level_resources;
	public static int teamshop_upgrade_defense_trigger_range;
	public static int teamshop_upgrade_heal_trigger_range;
	public static int teamshop_upgrade_trap_trigger_range;
	public static String teamshop_upgrade_trap_trigger_title;
	public static String teamshop_upgrade_trap_trigger_subtitle;
	public static String teamshop_upgrade_trap_trigger_message;
	public static int teamshop_upgrade_counter_offensive_trap_trigger_range;
	public static int teamshop_upgrade_counter_offensive_trap_effect_range;
	public static int teamshop_upgrade_alarm_trap_trigger_range;
	public static String teamshop_upgrade_alarm_trap_trigger_title;
	public static String teamshop_upgrade_alarm_trap_trigger_subtitle;
	public static String teamshop_upgrade_alarm_trap_trigger_message;
	public static boolean deathmode_enabled;
	public static int deathmode_gametime;
	public static String deathmode_title;
	public static String deathmode_subtitle;
	public static String deathmode_message;
	public static boolean deathitem_enabled;
	public static List<String> deathitem_items;
	public static boolean deathitem_item_name_chinesize;
	public static String deathitem_message;
	public static boolean nobreakbed_enabled;
	public static int nobreakbed_gametime;
	public static String nobreakbed_nobreakmessage;
	public static String nobreakbed_title;
	public static String nobreakbed_subtitle;
	public static String nobreakbed_message;
	public static boolean spawn_no_build_spawn_enabled;
	public static int spawn_no_build_spawn_range;
	public static boolean spawn_no_build_resource_enabled;
	public static int spawn_no_build_resource_range;
	public static String spawn_no_build_message;
	public static boolean holographic_resource_enabled;
	public static boolean holographic_bed_title_bed_alive_enabled;
	public static boolean holographic_bed_title_bed_destroyed_enabled;
	public static double holographic_resource_speed;
	public static List<String> holographic_resource;
	public static String holographic_bedtitle_bed_alive_title;
	public static String holographic_bedtitle_bed_destroyed_title;
	public static boolean overstats_enabled;
	public static List<String> overstats_message;
	public static String actionbar;
	public static Map<String, Integer> timer;
	public static List<String> planinfo;
	public static String playertag_prefix;
	public static String playertag_suffix;
	public static int scoreboard_interval;
	public static List<String> scoreboard_title;
	public static String scoreboard_you;
	public static String scoreboard_team_bed_status_bed_alive;
	public static String scoreboard_team_bed_status_bed_destroyed;
	public static String scoreboard_team_status_format_bed_alive;
	public static String scoreboard_team_status_format_bed_destroyed;
	public static String scoreboard_team_status_format_team_dead;
	public static Map<String, List<String>> scoreboard_lines;
	public static boolean lobby_scoreboard_enabled;
	public static int lobby_scoreboard_interval;
	public static String lobby_scoreboard_state_waiting;
	public static String lobby_scoreboard_state_countdown;
	public static List<String> lobby_scoreboard_title;
	public static List<String> lobby_scoreboard_lines;
	public static Map<String, List<String>> game_shop_item;
	public static Map<String, List<String>> game_shop_team;
	public static Map<String, String> game_shop_shops;
	public static Map<String, Map<String, List<Location>>> game_team_spawner;
	public static Map<String, String> game_team_spawners;
	public static List<MapView> image_maps;

	private static FileConfiguration getVerifiedConfig(String fileName) {
		Map<String, String> configVersion = new HashMap<String, String>();
		configVersion.put("config.yml", "23");
		configVersion.put("language.yml", "4");
		configVersion.put("team_shop.yml", "6");
		File file = new File(Main.getInstance().getDataFolder(), "/" + fileName);
		if (!file.exists()) {
			Main.getInstance().getLocaleConfig().saveResource(fileName);
			return YamlConfiguration.loadConfiguration(file);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if (!config.contains("version") || !config.getString("version").equals(configVersion.getOrDefault(fileName, ""))) {
			file.renameTo(new File(Main.getInstance().getDataFolder(), "/" + fileName.split("\\.")[0] + "_" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + "_old.yml"));
			Main.getInstance().getLocaleConfig().saveResource(fileName);
			config = YamlConfiguration.loadConfiguration(file);
		}
		return config;
	}

	public static void loadConfig() {
		Main.getInstance().getEditHolographicManager().removeAll();
		String prefix = "[" + Main.getInstance().getDescription().getName() + "] ";
		Bukkit.getConsoleSender().sendMessage(prefix + Main.getInstance().getLocaleConfig().getLanguage("loading_config"));
		File folder = new File(Main.getInstance().getDataFolder(), "/");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		Main.getInstance().getLocaleConfig().loadLocaleConfig();
		file_config = getVerifiedConfig("config.yml");
		language_config = getVerifiedConfig("language.yml");
		FileConfiguration team_shop_config = getVerifiedConfig("team_shop.yml");
		Bukkit.getConsoleSender().sendMessage(prefix + Main.getInstance().getLocaleConfig().getLanguage("saved_config"));
		Main.getInstance().reloadConfig();
		FileConfiguration config = Main.getInstance().getConfig();
		update_check_enabled = config.getBoolean("update_check.enabled");
		update_check_report = config.getBoolean("update_check.report");
		hide_player = config.getBoolean("hide_player");
		tab_health = config.getBoolean("tab_health");
		tag_health = config.getBoolean("tag_health");
		item_merge = config.getBoolean("item_merge");
		hunger_change = config.getBoolean("hunger_change");
		clear_bottle = config.getBoolean("clear_bottle");
		fast_respawn = config.getBoolean("fast_respawn");
		date_format = config.getString("date_format");
		chat_format_enabled = config.getBoolean("chat_format.enabled");
		chat_format_chat_lobby = config.getBoolean("chat_format.chat.lobby");
		chat_format_chat_all = config.getBoolean("chat_format.chat.all");
		chat_format_chat_spectator = config.getBoolean("chat_format.chat.spectator");
		chat_format_all_prefix = config.getStringList("chat_format.all_prefix");
		chat_format_lobby = ColorUtil.color(config.getString("chat_format.lobby"));
		chat_format_lobby_team = ColorUtil.color(config.getString("chat_format.lobby_team"));
		chat_format_ingame = ColorUtil.color(config.getString("chat_format.ingame"));
		chat_format_ingame_all = ColorUtil.color(config.getString("chat_format.ingame_all"));
		chat_format_spectator = ColorUtil.color(config.getString("chat_format.spectator"));
		final_killed_enabled = config.getBoolean("final_killed.enabled");
		final_killed_message = ColorUtil.color(config.getString("final_killed.message"));
		timecommand_startcommand = ColorUtil.colorList(config.getStringList("timecommand.startcommand"));
		giveitem_keeparmor = config.getBoolean("giveitem.keeparmor");
		giveitem_armor_helmet_item = (Map<String, Object>) config.getList("giveitem.armor.helmet.item").get(0);
		giveitem_armor_chestplate_item = (Map<String, Object>) config.getList("giveitem.armor.chestplate.item").get(0);
		giveitem_armor_leggings_item = (Map<String, Object>) config.getList("giveitem.armor.leggings.item").get(0);
		giveitem_armor_boots_item = (Map<String, Object>) config.getList("giveitem.armor.boots.item").get(0);
		giveitem_armor_helmet_give = config.getString("giveitem.armor.helmet.give");
		giveitem_armor_chestplate_give = config.getString("giveitem.armor.chestplate.give");
		giveitem_armor_leggings_give = config.getString("giveitem.armor.leggings.give");
		giveitem_armor_boots_give = config.getString("giveitem.armor.boots.give");
		giveitem_armor_helmet_move = config.getBoolean("giveitem.armor.helmet.move");
		giveitem_armor_chestplate_move = config.getBoolean("giveitem.armor.chestplate.move");
		giveitem_armor_leggings_move = config.getBoolean("giveitem.armor.leggings.move");
		giveitem_armor_boots_move = config.getBoolean("giveitem.armor.boots.move");
		select_team_enabled = config.getBoolean("select_team.enabled");
		select_team_status_select = ColorUtil.color(config.getString("select_team.status.select"));
		select_team_status_inteam = ColorUtil.color(config.getString("select_team.status.inteam"));
		select_team_status_team_full = ColorUtil.color(config.getString("select_team.status.team_full"));
		select_team_no_players = ColorUtil.color(config.getString("select_team.no_players"));
		select_team_item_name = ColorUtil.color(config.getString("select_team.item.name"));
		select_team_item_lore = ColorUtil.colorList(config.getStringList("select_team.item.lore"));
		lobby_block_enabled = config.getBoolean("lobby_block.enabled");
		lobby_block_position_1_x = config.getInt("lobby_block.position_1.x");
		lobby_block_position_1_y = config.getInt("lobby_block.position_1.y");
		lobby_block_position_1_z = config.getInt("lobby_block.position_1.z");
		lobby_block_position_2_x = config.getInt("lobby_block.position_2.x");
		lobby_block_position_2_y = config.getInt("lobby_block.position_2.y");
		lobby_block_position_2_z = config.getInt("lobby_block.position_2.z");
		rejoin_enabled = config.getBoolean("rejoin.enabled");
		rejoin_message_rejoin = ColorUtil.color(config.getString("rejoin.message.rejoin"));
		rejoin_message_error = ColorUtil.color(config.getString("rejoin.message.error"));
		bowdamage_enabled = config.getBoolean("bowdamage.enabled");
		bowdamage_title = ColorUtil.color(config.getString("bowdamage.title"));
		bowdamage_subtitle = ColorUtil.color(config.getString("bowdamage.subtitle"));
		bowdamage_message = ColorUtil.color(config.getString("bowdamage.message"));
		damagetitle_enabled = config.getBoolean("damagetitle.enabled");
		damagetitle_title = ColorUtil.color(config.getString("damagetitle.title"));
		damagetitle_subtitle = ColorUtil.color(config.getString("damagetitle.subtitle"));
		jointitle_enabled = config.getBoolean("jointitle.enabled");
		jointitle_title = ColorUtil.color(config.getString("jointitle.title"));
		jointitle_subtitle = ColorUtil.color(config.getString("jointitle.subtitle"));
		die_out_title_enabled = config.getBoolean("die_out_title.enabled");
		die_out_title_title = ColorUtil.color(config.getString("die_out_title.title"));
		die_out_title_subtitle = ColorUtil.color(config.getString("die_out_title.subtitle"));
		destroyed_title_enabled = config.getBoolean("destroyed_title.enabled");
		destroyed_title_title = ColorUtil.color(config.getString("destroyed_title.title"));
		destroyed_title_subtitle = ColorUtil.color(config.getString("destroyed_title.subtitle"));
		start_title_enabled = config.getBoolean("start_title.enabled");
		start_title_title = ColorUtil.colorList(config.getStringList("start_title.title"));
		start_title_subtitle = ColorUtil.color(config.getString("start_title.subtitle"));
		victory_title_enabled = config.getBoolean("victory_title.enabled");
		victory_title_title = ColorUtil.colorList(config.getStringList("victory_title.title"));
		victory_title_subtitle = ColorUtil.color(config.getString("victory_title.subtitle"));
		play_sound_enabled = config.getBoolean("play_sound.enabled");
		play_sound_sound_start = config.getStringList("play_sound.sound.start");
		play_sound_sound_death = config.getStringList("play_sound.sound.death");
		play_sound_sound_kill = config.getStringList("play_sound.sound.kill");
		play_sound_sound_no_resource = config.getStringList("play_sound.sound.no_resource");
		play_sound_sound_upgrade = config.getStringList("play_sound.sound.upgrade");
		play_sound_sound_sethealth = config.getStringList("play_sound.sound.sethealth");
		play_sound_sound_enable_witherbow = config.getStringList("play_sound.sound.enable_witherbow");
		play_sound_sound_witherbow = config.getStringList("play_sound.sound.witherbow");
		play_sound_sound_deathmode = config.getStringList("play_sound.sound.deathmode");
		play_sound_sound_over = config.getStringList("play_sound.sound.over");
		spectator_enabled = config.getBoolean("spectator.enabled");
		spectator_centre_enabled = config.getBoolean("spectator.centre.enabled");
		spectator_centre_height = config.getDouble("spectator.centre.height");
		spectator_spectator_target_title = ColorUtil.color(config.getString("spectator.spectator_target.title"));
		spectator_spectator_target_subtitle = ColorUtil.color(config.getString("spectator.spectator_target.subtitle"));
		spectator_quit_spectator_title = ColorUtil.color(config.getString("spectator.quit_spectator.title"));
		spectator_quit_spectator_subtitle = ColorUtil.color(config.getString("spectator.quit_spectator.subtitle"));
		spectator_speed_enabled = config.getBoolean("spectator.speed.enabled");
		spectator_speed_slot = config.getInt("spectator.speed.slot");
		spectator_speed_item = config.getInt("spectator.speed.item");
		spectator_speed_item_name = ColorUtil.color(config.getString("spectator.speed.item_name")) + "§7";
		spectator_speed_item_lore = ColorUtil.colorList(config.getStringList("spectator.speed.item_lore"));
		spectator_speed_gui_title = ColorUtil.color(config.getString("spectator.speed.gui_title")) + "§s§s";
		spectator_speed_no_speed = ColorUtil.color(config.getString("spectator.speed.no_speed"));
		spectator_speed_speed_1 = ColorUtil.color(config.getString("spectator.speed.speed_1"));
		spectator_speed_speed_2 = ColorUtil.color(config.getString("spectator.speed.speed_2"));
		spectator_speed_speed_3 = ColorUtil.color(config.getString("spectator.speed.speed_3"));
		spectator_speed_speed_4 = ColorUtil.color(config.getString("spectator.speed.speed_4"));
		spectator_fast_join_enabled = config.getBoolean("spectator.fast_join.enabled");
		spectator_fast_join_slot = config.getInt("spectator.fast_join.slot");
		spectator_fast_join_item = config.getInt("spectator.fast_join.item");
		spectator_fast_join_item_name = ColorUtil.color(config.getString("spectator.fast_join.item_name")) + "§7";
		spectator_fast_join_item_lore = ColorUtil.colorList(config.getStringList("spectator.fast_join.item_lore"));
		spectator_fast_join_group = config.getString("spectator.fast_join.group");
		compass_enabled = config.getBoolean("compass.enabled");
		compass_item_name = ColorUtil.color(config.getString("compass.item_name"));
		compass_item_lore = ColorUtil.colorList(config.getStringList("compass.item_lore"));
		compass_lore_send_message = ColorUtil.colorList(config.getStringList("compass.lore.send_message"));
		compass_lore_select_team = ColorUtil.colorList(config.getStringList("compass.lore.select_team"));
		compass_lore_select_resources = ColorUtil.colorList(config.getStringList("compass.lore.select_resources"));
		compass_resources_name = new HashMap<String, String>();
		compass_resources = new ArrayList<String>();
		for (String type : config.getConfigurationSection("compass.resources").getKeys(false)) {
			compass_resources_name.put(type, ColorUtil.color(config.getString("compass.resources." + type)));
			compass_resources.add(type);
		}
		compass_back = ColorUtil.color(config.getString("compass.back"));
		compass_gui_title = ColorUtil.color(config.getString("compass.gui_title")) + "§c§g";
		compass_item_III_II = ColorUtil.color(config.getString("compass.item.III_II"));
		compass_item_IV_II = ColorUtil.color(config.getString("compass.item.IV_II"));
		compass_item_V_II = ColorUtil.color(config.getString("compass.item.V_II"));
		compass_item_VI_II = ColorUtil.color(config.getString("compass.item.VI_II"));
		compass_item_VII_II = ColorUtil.color(config.getString("compass.item.VII_II"));
		compass_item_VIII_II = ColorUtil.color(config.getString("compass.item.VIII_II"));
		compass_item_III_III = ColorUtil.color(config.getString("compass.item.III_III"));
		compass_item_IV_III = ColorUtil.color(config.getString("compass.item.IV_III"));
		compass_item_V_III = ColorUtil.color(config.getString("compass.item.V_III"));
		compass_item_VI_III = ColorUtil.color(config.getString("compass.item.VI_III"));
		compass_item_VII_III = ColorUtil.color(config.getString("compass.item.VII_III"));
		compass_message_III_II = ColorUtil.color(config.getString("compass.message.III_II"));
		compass_message_IV_II = ColorUtil.color(config.getString("compass.message.IV_II"));
		compass_message_V_II = ColorUtil.color(config.getString("compass.message.V_II"));
		compass_message_VI_II = ColorUtil.color(config.getString("compass.message.VI_II"));
		compass_message_VII_II = ColorUtil.color(config.getString("compass.message.VII_II"));
		compass_message_VIII_II = ColorUtil.color(config.getString("compass.message.VIII_II"));
		compass_message_III_III = ColorUtil.color(config.getString("compass.message.III_III"));
		compass_message_IV_III = ColorUtil.color(config.getString("compass.message.IV_III"));
		compass_message_V_III = ColorUtil.color(config.getString("compass.message.V_III"));
		compass_message_VI_III = ColorUtil.color(config.getString("compass.message.VI_III"));
		compass_message_VII_III = ColorUtil.color(config.getString("compass.message.VII_III"));
		graffiti_enabled = config.getBoolean("graffiti.enabled");
		graffiti_holographic_enabled = config.getBoolean("graffiti.holographic.enabled");
		graffiti_holographic_text = ColorUtil.color(config.getString("graffiti.holographic.text"));
		shop_enabled = config.getBoolean("shop.enabled");
		shop_item_shop_type = config.getString("shop.item_shop.type");
		shop_item_shop_skin = config.getString("shop.item_shop.skin");
		shop_item_shop_look = config.getBoolean("shop.item_shop.look");
		shop_team_shop_type = config.getString("shop.team_shop.type");
		shop_team_shop_skin = config.getString("shop.team_shop.skin");
		shop_team_shop_look = config.getBoolean("shop.team_shop.look");
		shop_item_shop_name = ColorUtil.colorList(config.getStringList("shop.item_shop.name"));
		shop_team_shop_name = ColorUtil.colorList(config.getStringList("shop.team_shop.name"));
		respawn_enabled = config.getBoolean("respawn.enabled");
		respawn_centre_enabled = config.getBoolean("respawn.centre.enabled");
		respawn_centre_height = config.getDouble("respawn.centre.height");
		respawn_protected_enabled = config.getBoolean("respawn.protected.enabled");
		respawn_protected_time = config.getInt("respawn.protected.time");
		respawn_respawn_delay = config.getInt("respawn.respawn_delay");
		respawn_countdown_title = ColorUtil.color(config.getString("respawn.countdown.title"));
		respawn_countdown_subtitle = ColorUtil.color(config.getString("respawn.countdown.subtitle"));
		respawn_countdown_message = ColorUtil.color(config.getString("respawn.countdown.message"));
		respawn_respawn_title = ColorUtil.color(config.getString("respawn.respawn.title"));
		respawn_respawn_subtitle = ColorUtil.color(config.getString("respawn.respawn.subtitle"));
		respawn_respawn_message = ColorUtil.color(config.getString("respawn.respawn.message"));
		sethealth_start_enabled = config.getBoolean("sethealth.start.enabled");
		sethealth_start_health = config.getInt("sethealth.start.health");
		resourcelimit_enabled = config.getBoolean("resourcelimit.enabled");
		resourcelimit_limit = new ArrayList<String[]>();
		for (String w : config.getStringList("resourcelimit.limit")) {
			String[] ary = w.split(",");
			resourcelimit_limit.add(ary);
		}
		spread_resource_enabled = config.getBoolean("spread_resource.enabled");
		spread_resource_launch = config.getBoolean("spread_resource.launch");
		spread_resource_range = config.getDouble("spread_resource.range");
		game_chest_enabled = config.getBoolean("game_chest.enabled");
		game_chest_range = config.getInt("game_chest.range");
		game_chest_message = ColorUtil.color(config.getString("game_chest.message"));
		invisibility_player_enabled = config.getBoolean("invisibility_player.enabled");
		invisibility_player_footstep = config.getBoolean("invisibility_player.footstep");
		invisibility_player_hide_particles = config.getBoolean("invisibility_player.hide_particles");
		invisibility_player_damage_show_player = config.getBoolean("invisibility_player.damage_show_player");
		witherbow_enabled = config.getBoolean("witherbow.enabled");
		witherbow_gametime = config.getInt("witherbow.gametime");
		witherbow_already_starte = ColorUtil.color(config.getString("witherbow.already_starte"));
		witherbow_title = ColorUtil.color(config.getString("witherbow.title"));
		witherbow_subtitle = ColorUtil.color(config.getString("witherbow.subtitle"));
		witherbow_message = ColorUtil.color(config.getString("witherbow.message"));
		teamshop_enabled = team_shop_config.getBoolean("enabled");
		teamshop_upgrade_shop_title = ColorUtil.color(team_shop_config.getString("upgrade_shop.title"));
		teamshop_upgrade_shop_frame = ColorUtil.colorList(team_shop_config.getStringList("upgrade_shop.frame"));
		teamshop_upgrade_shop_trap_item = team_shop_config.getString("upgrade_shop.trap.item");
		teamshop_upgrade_shop_trap_name = ColorUtil.color(team_shop_config.getString("upgrade_shop.trap.name"));
		teamshop_upgrade_shop_trap_lore = ColorUtil.colorList(team_shop_config.getStringList("upgrade_shop.trap.lore"));
		teamshop_trap_shop_title = ColorUtil.color(team_shop_config.getString("trap_shop.title"));
		teamshop_trap_shop_back = ColorUtil.colorList(team_shop_config.getStringList("trap_shop.back"));
		teamshop_message_upgrade = ColorUtil.color(team_shop_config.getString("message.upgrade"));
		teamshop_message_no_resource = ColorUtil.color(team_shop_config.getString("message.no_resource"));
		teamshop_state_no_resource = ColorUtil.color(team_shop_config.getString("state.no_resource"));
		teamshop_state_lock = ColorUtil.color(team_shop_config.getString("state.lock"));
		teamshop_state_unlock = ColorUtil.color(team_shop_config.getString("state.unlock"));
		teamshop_trap_cooldown = team_shop_config.getInt("trap_cooldown");
		teamshop_trap_trap_list_trap_1_lock = ColorUtil.colorList(team_shop_config.getStringList("trap.trap_list.trap_1.lock"));
		teamshop_trap_trap_list_trap_1_unlock = ColorUtil.colorList(team_shop_config.getStringList("trap.trap_list.trap_1.unlock"));
		teamshop_trap_trap_list_trap_2_lock = ColorUtil.colorList(team_shop_config.getStringList("trap.trap_list.trap_2.lock"));
		teamshop_trap_trap_list_trap_2_unlock = ColorUtil.colorList(team_shop_config.getStringList("trap.trap_list.trap_2.unlock"));
		teamshop_trap_trap_list_trap_3_lock = ColorUtil.colorList(team_shop_config.getStringList("trap.trap_list.trap_3.lock"));
		teamshop_trap_trap_list_trap_3_unlock = ColorUtil.colorList(team_shop_config.getStringList("trap.trap_list.trap_3.unlock"));
		teamshop_trap_level_cost = new HashMap<Integer, String>();
		teamshop_trap_level_cost.put(1, team_shop_config.getString("trap.cost.level_1"));
		teamshop_trap_level_cost.put(2, team_shop_config.getString("trap.cost.level_2"));
		teamshop_trap_level_cost.put(3, team_shop_config.getString("trap.cost.level_3"));
		teamshop_upgrade_enabled = new LinkedHashMap<UpgradeType, Boolean>();
		teamshop_upgrade_item = new HashMap<UpgradeType, String>();
		teamshop_upgrade_name = new HashMap<UpgradeType, String>();
		teamshop_upgrade_level_cost = new HashMap<UpgradeType, Map<Integer, String>>();
		teamshop_upgrade_level_lore = new HashMap<UpgradeType, Map<Integer, List<String>>>();
		Map<Integer, List<String>> teamshop_upgrade_level_lore_map = new HashMap<Integer, List<String>>();
		Map<Integer, String> teamshop_upgrade_level_cost_map = new HashMap<Integer, String>();
		teamshop_upgrade_enabled.put(UpgradeType.SHARPNESS, team_shop_config.getBoolean("upgrade.sword_sharpness.enabled"));
		teamshop_upgrade_item.put(UpgradeType.SHARPNESS, team_shop_config.getString("upgrade.sword_sharpness.item"));
		teamshop_upgrade_name.put(UpgradeType.SHARPNESS, ColorUtil.color(team_shop_config.getString("upgrade.sword_sharpness.name")));
		teamshop_upgrade_level_cost_map.put(1, team_shop_config.getString("upgrade.sword_sharpness.level_1.cost"));
		teamshop_upgrade_level_cost_map.put(2, team_shop_config.getString("upgrade.sword_sharpness.level_2.cost"));
		teamshop_upgrade_level_cost.put(UpgradeType.SHARPNESS, teamshop_upgrade_level_cost_map);
		teamshop_upgrade_level_lore_map = new HashMap<Integer, List<String>>();
		teamshop_upgrade_level_lore_map.put(1, ColorUtil.colorList(team_shop_config.getStringList("upgrade.sword_sharpness.level_1.lore")));
		teamshop_upgrade_level_lore_map.put(2, ColorUtil.colorList(team_shop_config.getStringList("upgrade.sword_sharpness.level_2.lore")));
		teamshop_upgrade_level_lore_map.put(3, ColorUtil.colorList(team_shop_config.getStringList("upgrade.sword_sharpness.level_full.lore")));
		teamshop_upgrade_level_lore.put(UpgradeType.SHARPNESS, teamshop_upgrade_level_lore_map);
		teamshop_upgrade_enabled.put(UpgradeType.PROTECTION, team_shop_config.getBoolean("upgrade.armor_protection.enabled"));
		teamshop_upgrade_item.put(UpgradeType.PROTECTION, team_shop_config.getString("upgrade.armor_protection.item"));
		teamshop_upgrade_name.put(UpgradeType.PROTECTION, ColorUtil.color(team_shop_config.getString("upgrade.armor_protection.name")));
		teamshop_upgrade_level_cost_map = new HashMap<Integer, String>();
		teamshop_upgrade_level_cost_map.put(1, team_shop_config.getString("upgrade.armor_protection.level_1.cost"));
		teamshop_upgrade_level_cost_map.put(2, team_shop_config.getString("upgrade.armor_protection.level_2.cost"));
		teamshop_upgrade_level_cost_map.put(3, team_shop_config.getString("upgrade.armor_protection.level_3.cost"));
		teamshop_upgrade_level_cost_map.put(4, team_shop_config.getString("upgrade.armor_protection.level_4.cost"));
		teamshop_upgrade_level_cost.put(UpgradeType.PROTECTION, teamshop_upgrade_level_cost_map);
		teamshop_upgrade_level_lore_map = new HashMap<Integer, List<String>>();
		teamshop_upgrade_level_lore_map.put(1, ColorUtil.colorList(team_shop_config.getStringList("upgrade.armor_protection.level_1.lore")));
		teamshop_upgrade_level_lore_map.put(2, ColorUtil.colorList(team_shop_config.getStringList("upgrade.armor_protection.level_2.lore")));
		teamshop_upgrade_level_lore_map.put(3, ColorUtil.colorList(team_shop_config.getStringList("upgrade.armor_protection.level_3.lore")));
		teamshop_upgrade_level_lore_map.put(4, ColorUtil.colorList(team_shop_config.getStringList("upgrade.armor_protection.level_4.lore")));
		teamshop_upgrade_level_lore_map.put(5, ColorUtil.colorList(team_shop_config.getStringList("upgrade.armor_protection.level_full.lore")));
		teamshop_upgrade_level_lore.put(UpgradeType.PROTECTION, teamshop_upgrade_level_lore_map);
		teamshop_upgrade_enabled.put(UpgradeType.FAST_DIG, team_shop_config.getBoolean("upgrade.fast_dig.enabled"));
		teamshop_upgrade_item.put(UpgradeType.FAST_DIG, team_shop_config.getString("upgrade.fast_dig.item"));
		teamshop_upgrade_name.put(UpgradeType.FAST_DIG, ColorUtil.color(team_shop_config.getString("upgrade.fast_dig.name")));
		Map<Integer, String> teamshop_upgrade_fast_dig_level_cost = new HashMap<Integer, String>();
		teamshop_upgrade_fast_dig_level_cost.put(1, team_shop_config.getString("upgrade.fast_dig.level_1.cost"));
		teamshop_upgrade_fast_dig_level_cost.put(2, team_shop_config.getString("upgrade.fast_dig.level_2.cost"));
		teamshop_upgrade_level_cost.put(UpgradeType.FAST_DIG, teamshop_upgrade_fast_dig_level_cost);
		teamshop_upgrade_level_lore_map = new HashMap<Integer, List<String>>();
		teamshop_upgrade_level_lore_map.put(1, ColorUtil.colorList(team_shop_config.getStringList("upgrade.fast_dig.level_1.lore")));
		teamshop_upgrade_level_lore_map.put(2, ColorUtil.colorList(team_shop_config.getStringList("upgrade.fast_dig.level_2.lore")));
		teamshop_upgrade_level_lore_map.put(3, ColorUtil.colorList(team_shop_config.getStringList("upgrade.fast_dig.level_full.lore")));
		teamshop_upgrade_level_lore.put(UpgradeType.FAST_DIG, teamshop_upgrade_level_lore_map);
		teamshop_upgrade_enabled.put(UpgradeType.IRON_FORGE, team_shop_config.getBoolean("upgrade.iron_forge.enabled"));
		teamshop_upgrade_item.put(UpgradeType.IRON_FORGE, team_shop_config.getString("upgrade.iron_forge.item"));
		teamshop_upgrade_name.put(UpgradeType.IRON_FORGE, ColorUtil.color(team_shop_config.getString("upgrade.iron_forge.name")));
		teamshop_upgrade_iron_forge_level_resources = new HashMap<Integer, List<String>>();
		teamshop_upgrade_level_cost_map = new HashMap<Integer, String>();
		teamshop_upgrade_level_lore_map = new HashMap<Integer, List<String>>();
		for (int i = 1; i < 5; i++) {
			teamshop_upgrade_level_cost_map.put(i, team_shop_config.getString("upgrade.iron_forge.level_" + i + ".cost"));
			teamshop_upgrade_level_lore_map.put(i, ColorUtil.colorList(team_shop_config.getStringList("upgrade.iron_forge.level_" + i + ".lore")));
			teamshop_upgrade_iron_forge_level_resources.put(i, team_shop_config.getStringList("upgrade.iron_forge.level_" + i + ".resources"));
		}
		teamshop_upgrade_iron_forge_level_resources.put(0, team_shop_config.getStringList("upgrade.iron_forge.level_0.resources"));
		teamshop_upgrade_level_cost.put(UpgradeType.IRON_FORGE, teamshop_upgrade_level_cost_map);
		teamshop_upgrade_level_lore_map.put(5, ColorUtil.colorList(team_shop_config.getStringList("upgrade.iron_forge.level_full.lore")));
		teamshop_upgrade_level_lore.put(UpgradeType.IRON_FORGE, teamshop_upgrade_level_lore_map);
		teamshop_upgrade_enabled.put(UpgradeType.HEAL, team_shop_config.getBoolean("upgrade.heal.enabled"));
		teamshop_upgrade_item.put(UpgradeType.HEAL, team_shop_config.getString("upgrade.heal.item"));
		teamshop_upgrade_name.put(UpgradeType.HEAL, ColorUtil.color(team_shop_config.getString("upgrade.heal.name")));
		teamshop_upgrade_heal_trigger_range = team_shop_config.getInt("upgrade.heal.trigger_range");
		teamshop_upgrade_level_cost_map = new HashMap<Integer, String>();
		teamshop_upgrade_level_cost_map.put(1, team_shop_config.getString("upgrade.heal.level_1.cost"));
		teamshop_upgrade_level_cost.put(UpgradeType.HEAL, teamshop_upgrade_level_cost_map);
		teamshop_upgrade_level_lore_map = new HashMap<Integer, List<String>>();
		teamshop_upgrade_level_lore_map.put(1, ColorUtil.colorList(team_shop_config.getStringList("upgrade.heal.level_1.lore")));
		teamshop_upgrade_level_lore_map.put(2, ColorUtil.colorList(team_shop_config.getStringList("upgrade.heal.level_full.lore")));
		teamshop_upgrade_level_lore.put(UpgradeType.HEAL, teamshop_upgrade_level_lore_map);
		teamshop_upgrade_enabled.put(UpgradeType.TRAP, team_shop_config.getBoolean("upgrade.trap.enabled"));
		teamshop_upgrade_item.put(UpgradeType.TRAP, team_shop_config.getString("upgrade.trap.item"));
		teamshop_upgrade_name.put(UpgradeType.TRAP, ColorUtil.color(team_shop_config.getString("upgrade.trap.name")));
		teamshop_upgrade_trap_trigger_range = team_shop_config.getInt("upgrade.trap.trigger_range");
		teamshop_upgrade_trap_trigger_title = ColorUtil.color(team_shop_config.getString("upgrade.trap.trigger.title"));
		teamshop_upgrade_trap_trigger_subtitle = ColorUtil.color(team_shop_config.getString("upgrade.trap.trigger.subtitle"));
		teamshop_upgrade_trap_trigger_message = ColorUtil.color(team_shop_config.getString("upgrade.trap.trigger.message"));
		teamshop_upgrade_level_lore_map = new HashMap<Integer, List<String>>();
		teamshop_upgrade_level_lore_map.put(1, ColorUtil.colorList(team_shop_config.getStringList("upgrade.trap.level_1.lore")));
		teamshop_upgrade_level_lore_map.put(2, ColorUtil.colorList(team_shop_config.getStringList("upgrade.trap.level_full.lore")));
		teamshop_upgrade_level_lore.put(UpgradeType.TRAP, teamshop_upgrade_level_lore_map);
		teamshop_upgrade_enabled.put(UpgradeType.COUNTER_OFFENSIVE_TRAP, team_shop_config.getBoolean("upgrade.counter_offensive_trap.enabled"));
		teamshop_upgrade_item.put(UpgradeType.COUNTER_OFFENSIVE_TRAP, team_shop_config.getString("upgrade.counter_offensive_trap.item"));
		teamshop_upgrade_name.put(UpgradeType.COUNTER_OFFENSIVE_TRAP, ColorUtil.color(team_shop_config.getString("upgrade.counter_offensive_trap.name")));
		teamshop_upgrade_counter_offensive_trap_trigger_range = team_shop_config.getInt("upgrade.counter_offensive_trap.trigger_range");
		teamshop_upgrade_counter_offensive_trap_effect_range = team_shop_config.getInt("upgrade.counter_offensive_trap.effect_range");
		teamshop_upgrade_level_lore_map = new HashMap<Integer, List<String>>();
		teamshop_upgrade_level_lore_map.put(1, ColorUtil.colorList(team_shop_config.getStringList("upgrade.counter_offensive_trap.level_1.lore")));
		teamshop_upgrade_level_lore_map.put(2, ColorUtil.colorList(team_shop_config.getStringList("upgrade.counter_offensive_trap.level_full.lore")));
		teamshop_upgrade_level_lore.put(UpgradeType.COUNTER_OFFENSIVE_TRAP, teamshop_upgrade_level_lore_map);
		teamshop_upgrade_enabled.put(UpgradeType.ALARM_TRAP, team_shop_config.getBoolean("upgrade.alarm_trap.enabled"));
		teamshop_upgrade_item.put(UpgradeType.ALARM_TRAP, team_shop_config.getString("upgrade.alarm_trap.item"));
		teamshop_upgrade_name.put(UpgradeType.ALARM_TRAP, ColorUtil.color(team_shop_config.getString("upgrade.alarm_trap.name")));
		teamshop_upgrade_alarm_trap_trigger_range = team_shop_config.getInt("upgrade.alarm_trap.trigger_range");
		teamshop_upgrade_alarm_trap_trigger_title = ColorUtil.color(team_shop_config.getString("upgrade.alarm_trap.trigger.title"));
		teamshop_upgrade_alarm_trap_trigger_subtitle = ColorUtil.color(team_shop_config.getString("upgrade.alarm_trap.trigger.subtitle"));
		teamshop_upgrade_alarm_trap_trigger_message = ColorUtil.color(team_shop_config.getString("upgrade.alarm_trap.trigger.message"));
		teamshop_upgrade_level_lore_map = new HashMap<Integer, List<String>>();
		teamshop_upgrade_level_lore_map.put(1, ColorUtil.colorList(team_shop_config.getStringList("upgrade.alarm_trap.level_1.lore")));
		teamshop_upgrade_level_lore_map.put(2, ColorUtil.colorList(team_shop_config.getStringList("upgrade.alarm_trap.level_full.lore")));
		teamshop_upgrade_level_lore.put(UpgradeType.ALARM_TRAP, teamshop_upgrade_level_lore_map);
		teamshop_upgrade_enabled.put(UpgradeType.DEFENSE, team_shop_config.getBoolean("upgrade.defense.enabled"));
		teamshop_upgrade_item.put(UpgradeType.DEFENSE, team_shop_config.getString("upgrade.defense.item"));
		teamshop_upgrade_name.put(UpgradeType.DEFENSE, ColorUtil.color(team_shop_config.getString("upgrade.defense.name")));
		teamshop_upgrade_defense_trigger_range = team_shop_config.getInt("upgrade.defense.trigger_range");
		teamshop_upgrade_level_lore_map = new HashMap<Integer, List<String>>();
		teamshop_upgrade_level_lore_map.put(1, ColorUtil.colorList(team_shop_config.getStringList("upgrade.defense.level_1.lore")));
		teamshop_upgrade_level_lore_map.put(2, ColorUtil.colorList(team_shop_config.getStringList("upgrade.defense.level_full.lore")));
		teamshop_upgrade_level_lore.put(UpgradeType.DEFENSE, teamshop_upgrade_level_lore_map);
		deathmode_enabled = config.getBoolean("deathmode.enabled");
		deathmode_gametime = config.getInt("deathmode.gametime");
		deathmode_title = ColorUtil.color(config.getString("deathmode.title"));
		deathmode_subtitle = ColorUtil.color(config.getString("deathmode.subtitle"));
		deathmode_message = ColorUtil.color(config.getString("deathmode.message"));
		deathitem_enabled = config.getBoolean("deathitem.enabled");
		deathitem_items = config.getStringList("deathitem.items");
		deathitem_item_name_chinesize = config.getBoolean("deathitem.item_name_chinesize");
		deathitem_message = ColorUtil.color(config.getString("deathitem.message"));
		nobreakbed_nobreakmessage = ColorUtil.color(config.getString("nobreakbed.nobreakmessage"));
		nobreakbed_enabled = config.getBoolean("nobreakbed.enabled");
		nobreakbed_gametime = config.getInt("nobreakbed.gametime");
		nobreakbed_title = ColorUtil.color(config.getString("nobreakbed.title"));
		nobreakbed_subtitle = ColorUtil.color(config.getString("nobreakbed.subtitle"));
		nobreakbed_message = ColorUtil.color(config.getString("nobreakbed.message"));
		spawn_no_build_spawn_enabled = config.getBoolean("spawn_no_build.spawn.enabled");
		spawn_no_build_spawn_range = config.getInt("spawn_no_build.spawn.range");
		spawn_no_build_resource_enabled = config.getBoolean("spawn_no_build.resource.enabled");
		spawn_no_build_resource_range = config.getInt("spawn_no_build.resource.range");
		spawn_no_build_message = ColorUtil.color(config.getString("spawn_no_build.message"));
		holographic_resource_enabled = config.getBoolean("holographic.resource.enabled");
		holographic_bed_title_bed_alive_enabled = config.getBoolean("holographic.bed_title.bed_alive.enabled");
		holographic_bed_title_bed_destroyed_enabled = config.getBoolean("holographic.bed_title.bed_destroyed.enabled");
		holographic_resource_speed = config.getDouble("holographic.resource.speed");
		holographic_resource = new ArrayList<String>(config.getConfigurationSection("holographic.resource.resources").getKeys(false));
		holographic_bedtitle_bed_destroyed_title = ColorUtil.color(config.getString("holographic.bed_title.bed_destroyed.title"));
		holographic_bedtitle_bed_alive_title = ColorUtil.color(config.getString("holographic.bed_title.bed_alive.title"));
		overstats_enabled = config.getBoolean("overstats.enabled");
		overstats_message = ColorUtil.colorList(config.getStringList("overstats.message"));
		actionbar = ColorUtil.color(config.getString("actionbar"));
		timer = new HashMap<String, Integer>();
		for (String w : config.getConfigurationSection("timer").getKeys(false)) {
			timer.put(w, config.getInt("timer." + w));
		}
		planinfo = new ArrayList<String>(config.getConfigurationSection("planinfo").getKeys(false));
		playertag_prefix = ColorUtil.color(config.getString("playertag.prefix"));
		playertag_suffix = ColorUtil.color(config.getString("playertag.suffix"));
		scoreboard_interval = config.getInt("scoreboard.interval");
		scoreboard_title = ColorUtil.colorList(config.getStringList("scoreboard.title"));
		scoreboard_you = ColorUtil.color(config.getString("scoreboard.you"));
		scoreboard_team_bed_status_bed_alive = ColorUtil.color(config.getString("scoreboard.team_bed_status.bed_alive"));
		scoreboard_team_bed_status_bed_destroyed = ColorUtil.color(config.getString("scoreboard.team_bed_status.bed_destroyed"));
		scoreboard_team_status_format_bed_alive = ColorUtil.color(config.getString("scoreboard.team_status_format.bed_alive"));
		scoreboard_team_status_format_bed_destroyed = ColorUtil.color(config.getString("scoreboard.team_status_format.bed_destroyed"));
		scoreboard_team_status_format_team_dead = ColorUtil.color(config.getString("scoreboard.team_status_format.team_dead"));
		scoreboard_lines = new HashMap<String, List<String>>();
		for (String key : config.getConfigurationSection("scoreboard.lines").getKeys(false)) {
			scoreboard_lines.put(key, ColorUtil.colorList(config.getStringList("scoreboard.lines." + key)));
		}
		lobby_scoreboard_enabled = config.getBoolean("lobby_scoreboard.enabled");
		lobby_scoreboard_interval = config.getInt("lobby_scoreboard.interval");
		lobby_scoreboard_state_waiting = ColorUtil.color(config.getString("lobby_scoreboard.state.waiting"));
		lobby_scoreboard_state_countdown = ColorUtil.color(config.getString("lobby_scoreboard.state.countdown"));
		lobby_scoreboard_title = ColorUtil.colorList(config.getStringList("lobby_scoreboard.title"));
		lobby_scoreboard_lines = new ArrayList<String>();
		for (String w : config.getStringList("lobby_scoreboard.lines")) {
			String line = ColorUtil.color(w);
			if (lobby_scoreboard_lines.size() < 15) {
				if (lobby_scoreboard_lines.contains(line)) {
					lobby_scoreboard_lines.add(conflict(lobby_scoreboard_lines, line));
				} else {
					lobby_scoreboard_lines.add(line);
				}
			}
		}
		loadGameConfig();
		loadImages();
		if (fast_respawn) {
			BedwarsRel.getInstance().getConfig().set("die-on-void", false);
			BedwarsRel.getInstance().saveConfig();
		}
		Bukkit.getConsoleSender().sendMessage(prefix + Main.getInstance().getLocaleConfig().getLanguage("config_success"));
	}

	public static FileConfiguration getConfig() {
		return file_config;
	}

	private static String conflict(List<String> lines, String line) {
		String l = line;
		for (int i = 0; i == 0;) {
			l = l + "§r";
			if (!lines.contains(l)) {
				return l;
			}
		}
		return l;
	}

	public static void setShop(String game, Location location, String type) {
		File file = getGameFile();
		FileConfiguration filec = YamlConfiguration.loadConfiguration(file);
		List<String> loc = new ArrayList<String>();
		if (filec.getStringList(game + ".shop." + type) != null) {
			loc.addAll(filec.getStringList(game + ".shop." + type));
		}
		loc.add(location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ() + ", " + location.getYaw() + ", " + location.getPitch());
		filec.set(game + ".shop." + type, loc);
		try {
			filec.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadGameConfig();
	}

	public static void removeShop(String data) {
		File file = getGameFile();
		FileConfiguration filec = YamlConfiguration.loadConfiguration(file);
		String path = data.split(" - ")[0];
		List<String> loc = new ArrayList<String>();
		if (filec.getStringList(path) != null) {
			loc.addAll(filec.getStringList(path));
		}
		loc.remove(data.split(" - ")[1]);
		filec.set(path, loc);
		try {
			filec.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadGameConfig();
	}

	public static void setTeamSpawner(String game, String team, Location location) {
		File file = getGameFile();
		FileConfiguration filec = YamlConfiguration.loadConfiguration(file);
		List<String> loc = new ArrayList<String>();
		if (filec.getStringList(game + ".team_spawner." + team) != null) {
			loc.addAll(filec.getStringList(game + ".team_spawner." + team));
		}
		loc.add(location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ() + ", " + location.getYaw() + ", " + location.getPitch());
		filec.set(game + ".team_spawner." + team, loc);
		try {
			filec.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadGameConfig();
	}

	private static void loadGameConfig() {
		File folder = new File(Main.getInstance().getDataFolder(), "/");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder.getAbsolutePath() + "/game.yml");
		game_shop_item = new HashMap<String, List<String>>();
		game_shop_team = new HashMap<String, List<String>>();
		game_shop_shops = new HashMap<String, String>();
		game_team_spawner = new HashMap<String, Map<String, List<Location>>>();
		game_team_spawners = new HashMap<String, String>();
		int shopId = 0;
		int spawnerId = 0;
		if (!file.exists()) {
			return;
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (String game : config.getKeys(false)) {
			ConfigurationSection configSec = config.getConfigurationSection(game);
			if (configSec.contains("shop")) {
				ConfigurationSection configss = configSec.getConfigurationSection("shop");
				if (configss.contains("item")) {
					game_shop_item.put(game, configss.getStringList("item"));
					for (String shop : configss.getStringList("item")) {
						game_shop_shops.put(shopId + "", game + ".shop.item - " + shop);
						shopId++;
					}
				}
				if (configss.contains("team")) {
					game_shop_team.put(game, configss.getStringList("team"));
					for (String shop : configss.getStringList("team")) {
						game_shop_shops.put(shopId + "", game + ".shop.team - " + shop);
						shopId++;
					}
				}
			}
			if (configSec.contains("team_spawner")) {
				ConfigurationSection configst = configSec.getConfigurationSection("team_spawner");
				Map<String, List<Location>> map = new HashMap<String, List<Location>>();
				for (String team : configst.getKeys(false)) {
					List<Location> locs = new ArrayList<Location>();
					for (String loc : configst.getStringList(team)) {
						Location location = toLocation(loc);
						if (location != null) {
							locs.add(location);
						}
					}
					map.put(team, locs);
					for (String spawner : configst.getStringList(team)) {
						game_team_spawners.put(spawnerId + "", game + ".team_spawner." + team + " - " + spawner);
						spawnerId++;
					}
				}
				game_team_spawner.put(game, map);
			}
		}
	}

	private static Location toLocation(String loc) {
		try {
			String[] ary = loc.split(", ");
			if (Bukkit.getWorld(ary[0]) != null) {
				Location location = new Location(Bukkit.getWorld(ary[0]), Double.valueOf(ary[1]), Double.valueOf(ary[2]), Double.valueOf(ary[3]));
				if (ary.length > 4) {
					location.setYaw(Float.valueOf(ary[4]));
					location.setPitch(Float.valueOf(ary[5]));
				}
				return location;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static void addShopNPC(Integer id) {
		File folder = getNPCFile();
		FileConfiguration config = YamlConfiguration.loadConfiguration(folder);
		List<String> npcs = new ArrayList<String>();
		if (config.getKeys(false).contains("npcs")) {
			npcs.addAll(config.getStringList("npcs"));
		}
		npcs.add(id + "");
		config.set("npcs", npcs);
		try {
			config.save(folder);
		} catch (IOException e) {
		}
	}

	private static void loadImages() {
		image_maps = new ArrayList<MapView>();
		File folder = new File(Main.getInstance().getDataFolder(), "/images");
		if (!folder.exists()) {
			folder.mkdirs();
			try {
				writeToLocal(folder.getPath() + "/README.txt", Main.getInstance().getResource("images/README.txt"));
				writeToLocal(folder.getPath() + "/1.jpg", Main.getInstance().getResource("images/1.jpg"));
			} catch (Exception e) {
			}
		}
		for (File file : folder.listFiles()) {
			if (!isImage(file)) {
				continue;
			}
			try {
				MapView map = Bukkit.createMap(Bukkit.getWorlds().get(0));
				map.setCenterX(Integer.MAX_VALUE);
				map.setCenterZ(Integer.MAX_VALUE);
				BufferedImage bufferedImage = ImageIO.read(file);
				int x = (128 - bufferedImage.getWidth()) / 2;
				int y = (128 - bufferedImage.getHeight()) / 2;
				map.addRenderer(new MapRenderer() {

					@Override
					public void render(MapView mapView, MapCanvas mapCanvas, Player p) {
						mapCanvas.drawImage(x, y, bufferedImage);
					}
				});
				map.setScale(Scale.CLOSEST);
				image_maps.add(map);
			} catch (IOException e) {
			}
		}
	}

	private static boolean isImage(File file) {
		try {
			return ImageIO.read(file) != null;
		} catch (Exception ex) {
			return false;
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

	public static File getNPCFile() {
		File folder = new File(CitizensAPI.getDataFolder(), "/");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder.getAbsolutePath() + "/npcs.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		return file;
	}

	private static File getGameFile() {
		File folder = new File(Main.getInstance().getDataFolder(), "/");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder.getAbsolutePath() + "/game.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		return file;
	}

	public static String getLanguage(String path) {
		return ColorUtil.color(language_config.getString(path, "null"));
	}

	public static List<String> getLanguageList(String path) {
		if (language_config.contains(path) && language_config.isList(path)) {
			return ColorUtil.colorList(language_config.getStringList(path));
		}
		return Arrays.asList("null");
	}
}
