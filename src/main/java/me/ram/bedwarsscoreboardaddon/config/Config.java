package me.ram.bedwarsscoreboardaddon.config;

import net.citizensnpcs.api.CitizensAPI;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;

public class Config {

	public static boolean update_check;
	public static boolean hide_player;
	public static boolean tab_health;
	public static boolean tag_health;
	public static boolean item_merge;
	public static boolean hunger_change;
	public static boolean clear_bottle;
	public static String date_format;
	public static boolean chatformat_enabled;
	public static String chatformat_lobby;
	public static String chatformat_lobby_team;
	public static List<String> chatformat_all_prefix;
	public static String chatformat_ingame;
	public static String chatformat_ingame_all;
	public static String chatformat_spectator;
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
	public static int respawn_respawn_delay;
	public static String respawn_respawning_title;
	public static String respawn_respawning_subtitle;
	public static String respawn_respawning_message;
	public static String respawn_respawned_title;
	public static String respawn_respawned_subtitle;
	public static String respawn_respawned_message;
	public static boolean giveitem_keeparmor;
	public static Map<String, Object> giveitem_armor_helmet_item;
	public static Map<String, Object> giveitem_armor_chestplate_item;
	public static Map<String, Object> giveitem_armor_leggings_item;
	public static Map<String, Object> giveitem_armor_boots_item;
	public static boolean giveitem_armor_helmet_give;
	public static boolean giveitem_armor_chestplate_give;
	public static boolean giveitem_armor_leggings_give;
	public static boolean giveitem_armor_boots_give;
	public static boolean giveitem_armor_helmet_move;
	public static boolean giveitem_armor_chestplate_move;
	public static boolean giveitem_armor_leggings_move;
	public static boolean giveitem_armor_boots_move;
	public static boolean sethealth_start_enabled;
	public static int sethealth_start_health;
	public static String sethealth_start_title;
	public static String sethealth_start_subtitle;
	public static String sethealth_start_message;
	public static boolean resourcelimit_enabled;
	public static List<String[]> resourcelimit_limit;
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
	public static String teamshop_title;
	public static String teamshop_message;
	public static String teamshop_no_resource;
	public static List<String> teamshop_frame;
	public static String teamshop_upgrade_fast_dig_item;
	public static String teamshop_upgrade_fast_dig_name;
	public static String teamshop_upgrade_fast_dig_level_1_cost;
	public static List<String> teamshop_upgrade_fast_dig_level_1_lore;
	public static String teamshop_upgrade_fast_dig_level_2_cost;
	public static List<String> teamshop_upgrade_fast_dig_level_2_lore;
	public static List<String> teamshop_upgrade_fast_dig_level_full_lore;
	public static String teamshop_upgrade_sword_sharpness_item;
	public static String teamshop_upgrade_sword_sharpness_name;
	public static String teamshop_upgrade_sword_sharpness_level_1_cost;
	public static String teamshop_upgrade_sword_sharpness_level_2_cost;
	public static List<String> teamshop_upgrade_sword_sharpness_level_1_lore;
	public static List<String> teamshop_upgrade_sword_sharpness_level_2_lore;
	public static List<String> teamshop_upgrade_sword_sharpness_level_full_lore;
	public static String teamshop_upgrade_armor_protection_item;
	public static String teamshop_upgrade_armor_protection_name;
	public static String teamshop_upgrade_armor_protection_level_1_cost;
	public static String teamshop_upgrade_armor_protection_level_2_cost;
	public static String teamshop_upgrade_armor_protection_level_3_cost;
	public static String teamshop_upgrade_armor_protection_level_4_cost;
	public static List<String> teamshop_upgrade_armor_protection_level_1_lore;
	public static List<String> teamshop_upgrade_armor_protection_level_2_lore;
	public static List<String> teamshop_upgrade_armor_protection_level_3_lore;
	public static List<String> teamshop_upgrade_armor_protection_level_4_lore;
	public static List<String> teamshop_upgrade_armor_protection_level_full_lore;
	public static String teamshop_upgrade_trap_item;
	public static String teamshop_upgrade_trap_name;
	public static int teamshop_upgrade_trap_trigger_range;
	public static String teamshop_upgrade_trap_trigger_title;
	public static String teamshop_upgrade_trap_trigger_subtitle;
	public static String teamshop_upgrade_trap_trigger_message;
	public static String teamshop_upgrade_trap_level_1_cost;
	public static List<String> teamshop_upgrade_trap_level_1_lore;
	public static List<String> teamshop_upgrade_trap_level_full_lore;
	public static Boolean teamshop_upgrade_defense_permanent;
	public static String teamshop_upgrade_defense_item;
	public static String teamshop_upgrade_defense_name;
	public static int teamshop_upgrade_defense_trigger_range;
	public static String teamshop_upgrade_defense_level_1_cost;
	public static List<String> teamshop_upgrade_defense_level_1_lore;
	public static List<String> teamshop_upgrade_defense_level_full_lore;
	public static String teamshop_upgrade_heal_item;
	public static String teamshop_upgrade_heal_name;
	public static int teamshop_upgrade_heal_trigger_range;
	public static String teamshop_upgrade_heal_level_1_cost;
	public static List<String> teamshop_upgrade_heal_level_1_lore;
	public static List<String> teamshop_upgrade_heal_level_full_lore;
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
	public static boolean spawn_no_build_enabled;
	public static int spawn_no_build_spawn_range;
	public static int spawn_no_build_resource_range;
	public static String spawn_no_build_message;
	public static boolean holographic_resource_enabled;
	public static boolean holographic_bed_title_enabled;
	public static double holographic_resource_speed;
	public static List<String> holographic_resource;
	public static String holographic_bedtitle_bed_alive;
	public static String holographic_bedtitle_bed_destroyed;
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
	public static Map<String, List<String>> shop_item;
	public static Map<String, List<String>> shop_team;
	public static Map<String, String> shop_shops;
	private static FileConfiguration language_config;

	public static void loadConfig() {
		Main.getInstance().getHolographicManager().removeAll();
		String prefix = "[" + Main.getInstance().getDescription().getName() + "] ";
		Bukkit.getConsoleSender().sendMessage(prefix + "§f正在加载配置文件...");
		File folder = new File(Main.getInstance().getDataFolder(), "/");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File cfile = new File(folder.getAbsolutePath() + "/config.yml");
		if (!cfile.exists()) {
			Main.getInstance().saveResource("config.yml", false);
		}
		File tsfile = new File(folder.getAbsolutePath() + "/team_shop.yml");
		if (!tsfile.exists()) {
			Main.getInstance().saveResource("team_shop.yml", false);
		}
		FileConfiguration cfilec = YamlConfiguration.loadConfiguration(cfile);
		if (cfilec.getString("version") == null || !cfilec.getString("version").equals(Main.getVersion())) {
			cfile.renameTo(new File(folder.getAbsolutePath() + "/config_"
					+ new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".yml"));
			Main.getInstance().saveResource("config.yml", false);
			cfilec = YamlConfiguration.loadConfiguration(cfile);
		}
		language_config = YamlConfiguration.loadConfiguration(getLanguageFile());
		FileConfiguration tsfilec = YamlConfiguration.loadConfiguration(tsfile);
		Bukkit.getConsoleSender().sendMessage(prefix + "§a默认配置文件已保存！");
		Main.getInstance().reloadConfig();
		FileConfiguration config = Main.getInstance().getConfig();
		update_check = config.getBoolean("update_check");
		hide_player = config.getBoolean("hide_player");
		tab_health = config.getBoolean("tab_health");
		tag_health = config.getBoolean("tag_health");
		item_merge = config.getBoolean("item_merge");
		hunger_change = config.getBoolean("hunger_change");
		clear_bottle = config.getBoolean("clear_bottle");
		date_format = config.getString("date_format");
		chatformat_enabled = config.getBoolean("chatformat.enabled");
		chatformat_all_prefix = config.getStringList("chatformat.all_prefix");
		chatformat_lobby = ColorUtil.color(Main.getInstance().getConfig().getString("chatformat.lobby"));
		chatformat_lobby_team = ColorUtil.color(Main.getInstance().getConfig().getString("chatformat.lobby_team"));
		chatformat_ingame = ColorUtil.color(Main.getInstance().getConfig().getString("chatformat.ingame"));
		chatformat_ingame_all = ColorUtil.color(Main.getInstance().getConfig().getString("chatformat.ingame_all"));
		chatformat_spectator = ColorUtil.color(Main.getInstance().getConfig().getString("chatformat.spectator"));
		final_killed_enabled = config.getBoolean("final_killed.enabled");
		final_killed_message = ColorUtil.color(config.getString("final_killed.message"));
		timecommand_startcommand = ColorUtil
				.listcolor(Main.getInstance().getConfig().getStringList("timecommand.startcommand"));
		giveitem_keeparmor = config.getBoolean("giveitem.keeparmor");
		giveitem_armor_helmet_item = (Map<String, Object>) config.getList("giveitem.armor.helmet.item").get(0);
		giveitem_armor_chestplate_item = (Map<String, Object>) config.getList("giveitem.armor.chestplate.item").get(0);
		giveitem_armor_leggings_item = (Map<String, Object>) config.getList("giveitem.armor.leggings.item").get(0);
		giveitem_armor_boots_item = (Map<String, Object>) config.getList("giveitem.armor.boots.item").get(0);
		giveitem_armor_helmet_give = config.getBoolean("giveitem.armor.helmet.give");
		giveitem_armor_chestplate_give = config.getBoolean("giveitem.armor.chestplate.give");
		giveitem_armor_leggings_give = config.getBoolean("giveitem.armor.leggings.give");
		giveitem_armor_boots_give = config.getBoolean("giveitem.armor.boots.give");
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
		select_team_item_lore = ColorUtil.listcolor(config.getStringList("select_team.item.lore"));
		lobby_block_enabled = config.getBoolean("lobby_block.enabled");
		lobby_block_position_1_x = config.getInt("lobby_block.position_1.x");
		lobby_block_position_1_y = config.getInt("lobby_block.position_1.y");
		lobby_block_position_1_z = config.getInt("lobby_block.position_1.z");
		lobby_block_position_2_x = config.getInt("lobby_block.position_2.x");
		lobby_block_position_2_y = config.getInt("lobby_block.position_2.y");
		lobby_block_position_2_z = config.getInt("lobby_block.position_2.z");
		rejoin_enabled = config.getBoolean("rejoin.enabled");
		rejoin_message_rejoin = ColorUtil.color(Main.getInstance().getConfig().getString("rejoin.message.rejoin"));
		rejoin_message_error = ColorUtil.color(Main.getInstance().getConfig().getString("rejoin.message.error"));
		bowdamage_enabled = config.getBoolean("bowdamage.enabled");
		bowdamage_title = ColorUtil.color(Main.getInstance().getConfig().getString("bowdamage.title"));
		bowdamage_subtitle = ColorUtil.color(Main.getInstance().getConfig().getString("bowdamage.subtitle"));
		bowdamage_message = ColorUtil.color(Main.getInstance().getConfig().getString("bowdamage.message"));
		damagetitle_enabled = config.getBoolean("damagetitle.enabled");
		damagetitle_title = ColorUtil.color(Main.getInstance().getConfig().getString("damagetitle.title"));
		damagetitle_subtitle = ColorUtil.color(Main.getInstance().getConfig().getString("damagetitle.subtitle"));
		jointitle_enabled = config.getBoolean("jointitle.enabled");
		jointitle_title = ColorUtil.color(Main.getInstance().getConfig().getString("jointitle.title"));
		jointitle_subtitle = ColorUtil.color(Main.getInstance().getConfig().getString("jointitle.subtitle"));
		die_out_title_enabled = config.getBoolean("die_out_title.enabled");
		die_out_title_title = ColorUtil.color(Main.getInstance().getConfig().getString("die_out_title.title"));
		die_out_title_subtitle = ColorUtil.color(Main.getInstance().getConfig().getString("die_out_title.subtitle"));
		destroyed_title_enabled = config.getBoolean("destroyed_title.enabled");
		destroyed_title_title = ColorUtil.color(Main.getInstance().getConfig().getString("destroyed_title.title"));
		destroyed_title_subtitle = ColorUtil
				.color(Main.getInstance().getConfig().getString("destroyed_title.subtitle"));
		start_title_enabled = config.getBoolean("start_title.enabled");
		start_title_title = ColorUtil.listcolor(Main.getInstance().getConfig().getStringList("start_title.title"));
		start_title_subtitle = ColorUtil.color(Main.getInstance().getConfig().getString("start_title.subtitle"));
		victory_title_enabled = config.getBoolean("victory_title.enabled");
		victory_title_title = ColorUtil.listcolor(Main.getInstance().getConfig().getStringList("victory_title.title"));
		victory_title_subtitle = ColorUtil.color(Main.getInstance().getConfig().getString("victory_title.subtitle"));
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
		spectator_spectator_target_title = ColorUtil
				.color(Main.getInstance().getConfig().getString("spectator.spectator_target.title"));
		spectator_spectator_target_subtitle = ColorUtil
				.color(Main.getInstance().getConfig().getString("spectator.spectator_target.subtitle"));
		spectator_quit_spectator_title = ColorUtil
				.color(Main.getInstance().getConfig().getString("spectator.quit_spectator.title"));
		spectator_quit_spectator_subtitle = ColorUtil
				.color(Main.getInstance().getConfig().getString("spectator.quit_spectator.subtitle"));
		spectator_speed_enabled = config.getBoolean("spectator.speed.enabled");
		spectator_speed_slot = config.getInt("spectator.speed.slot");
		spectator_speed_item = config.getInt("spectator.speed.item");
		spectator_speed_item_name = ColorUtil
				.color(Main.getInstance().getConfig().getString("spectator.speed.item_name")) + "§7";
		spectator_speed_item_lore = ColorUtil
				.listcolor(Main.getInstance().getConfig().getStringList("spectator.speed.item_lore"));
		spectator_speed_gui_title = ColorUtil
				.color(Main.getInstance().getConfig().getString("spectator.speed.gui_title")) + "§s§s";
		spectator_speed_no_speed = ColorUtil
				.color(Main.getInstance().getConfig().getString("spectator.speed.no_speed"));
		spectator_speed_speed_1 = ColorUtil.color(Main.getInstance().getConfig().getString("spectator.speed.speed_1"));
		spectator_speed_speed_2 = ColorUtil.color(Main.getInstance().getConfig().getString("spectator.speed.speed_2"));
		spectator_speed_speed_3 = ColorUtil.color(Main.getInstance().getConfig().getString("spectator.speed.speed_3"));
		spectator_speed_speed_4 = ColorUtil.color(Main.getInstance().getConfig().getString("spectator.speed.speed_4"));
		spectator_fast_join_enabled = config.getBoolean("spectator.fast_join.enabled");
		spectator_fast_join_slot = config.getInt("spectator.fast_join.slot");
		spectator_fast_join_item = config.getInt("spectator.fast_join.item");
		spectator_fast_join_item_name = ColorUtil
				.color(Main.getInstance().getConfig().getString("spectator.fast_join.item_name")) + "§7";
		spectator_fast_join_item_lore = ColorUtil
				.listcolor(Main.getInstance().getConfig().getStringList("spectator.fast_join.item_lore"));
		spectator_fast_join_group = config.getString("spectator.fast_join.group");
		compass_enabled = config.getBoolean("compass.enabled");
		compass_item_name = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item_name"));
		compass_item_lore = ColorUtil.listcolor(Main.getInstance().getConfig().getStringList("compass.item_lore"));
		compass_lore_send_message = ColorUtil
				.listcolor(Main.getInstance().getConfig().getStringList("compass.lore.send_message"));
		compass_lore_select_team = ColorUtil
				.listcolor(Main.getInstance().getConfig().getStringList("compass.lore.select_team"));
		compass_lore_select_resources = ColorUtil
				.listcolor(Main.getInstance().getConfig().getStringList("compass.lore.select_resources"));
		compass_resources_name = new HashMap<String, String>();
		compass_resources = new ArrayList<String>();
		for (String type : Main.getInstance().getConfig().getConfigurationSection("compass.resources").getKeys(false)) {
			compass_resources_name.put(type,
					ColorUtil.color(Main.getInstance().getConfig().getString("compass.resources." + type)));
			compass_resources.add(type);
		}
		compass_back = ColorUtil.color(Main.getInstance().getConfig().getString("compass.back"));
		compass_gui_title = ColorUtil.color(Main.getInstance().getConfig().getString("compass.gui_title")) + "§c§g";
		compass_item_III_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item.III_II"));
		compass_item_IV_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item.IV_II"));
		compass_item_V_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item.V_II"));
		compass_item_VI_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item.VI_II"));
		compass_item_VII_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item.VII_II"));
		compass_item_VIII_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item.VIII_II"));
		compass_item_III_III = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item.III_III"));
		compass_item_IV_III = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item.IV_III"));
		compass_item_V_III = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item.V_III"));
		compass_item_VI_III = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item.VI_III"));
		compass_item_VII_III = ColorUtil.color(Main.getInstance().getConfig().getString("compass.item.VII_III"));
		compass_message_III_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.message.III_II"));
		compass_message_IV_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.message.IV_II"));
		compass_message_V_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.message.V_II"));
		compass_message_VI_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.message.VI_II"));
		compass_message_VII_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.message.VII_II"));
		compass_message_VIII_II = ColorUtil.color(Main.getInstance().getConfig().getString("compass.message.VIII_II"));
		compass_message_III_III = ColorUtil.color(Main.getInstance().getConfig().getString("compass.message.III_III"));
		compass_message_IV_III = ColorUtil.color(Main.getInstance().getConfig().getString("compass.message.IV_III"));
		compass_message_V_III = ColorUtil.color(Main.getInstance().getConfig().getString("compass.message.V_III"));
		compass_message_VI_III = ColorUtil.color(Main.getInstance().getConfig().getString("compass.message.VI_III"));
		compass_message_VII_III = ColorUtil.color(Main.getInstance().getConfig().getString("compass.message.VII_III"));
		shop_enabled = config.getBoolean("shop.enabled");
		shop_item_shop_type = config.getString("shop.item_shop.type");
		shop_item_shop_skin = config.getString("shop.item_shop.skin");
		shop_item_shop_look = config.getBoolean("shop.item_shop.look");
		shop_team_shop_type = config.getString("shop.team_shop.type");
		shop_team_shop_skin = config.getString("shop.team_shop.skin");
		shop_team_shop_look = config.getBoolean("shop.team_shop.look");
		shop_item_shop_name = ColorUtil.listcolor(Main.getInstance().getConfig().getStringList("shop.item_shop.name"));
		shop_team_shop_name = ColorUtil.listcolor(Main.getInstance().getConfig().getStringList("shop.team_shop.name"));
		respawn_enabled = config.getBoolean("respawn.enabled");
		respawn_centre_enabled = config.getBoolean("respawn.centre.enabled");
		respawn_centre_height = config.getDouble("respawn.centre.height");
		respawn_respawn_delay = config.getInt("respawn.respawn_delay");
		respawn_respawning_title = ColorUtil
				.color(Main.getInstance().getConfig().getString("respawn.respawning.title"));
		respawn_respawning_subtitle = ColorUtil
				.color(Main.getInstance().getConfig().getString("respawn.respawning.subtitle"));
		respawn_respawning_message = ColorUtil
				.color(Main.getInstance().getConfig().getString("respawn.respawning.message"));
		respawn_respawned_title = ColorUtil.color(Main.getInstance().getConfig().getString("respawn.respawned.title"));
		respawn_respawned_subtitle = ColorUtil
				.color(Main.getInstance().getConfig().getString("respawn.respawned.subtitle"));
		respawn_respawned_message = ColorUtil
				.color(Main.getInstance().getConfig().getString("respawn.respawned.message"));
		sethealth_start_enabled = config.getBoolean("sethealth.start.enabled");
		sethealth_start_health = config.getInt("sethealth.start.health");
		sethealth_start_title = ColorUtil.color(Main.getInstance().getConfig().getString("sethealth.start.title"));
		sethealth_start_subtitle = ColorUtil
				.color(Main.getInstance().getConfig().getString("sethealth.start.subtitle"));
		sethealth_start_message = ColorUtil.color(Main.getInstance().getConfig().getString("sethealth.start.message"));
		resourcelimit_enabled = config.getBoolean("resourcelimit.enabled");
		resourcelimit_limit = new ArrayList<String[]>();
		for (String w : Main.getInstance().getConfig().getStringList("resourcelimit.limit")) {
			String[] ary = w.split(",");
			resourcelimit_limit.add(ary);
		}
		invisibility_player_enabled = config.getBoolean("invisibility_player.enabled");
		invisibility_player_footstep = config.getBoolean("invisibility_player.footstep");
		invisibility_player_hide_particles = config.getBoolean("invisibility_player.hide_particles");
		invisibility_player_damage_show_player = config.getBoolean("invisibility_player.damage_show_player");
		witherbow_enabled = config.getBoolean("witherbow.enabled");
		witherbow_gametime = config.getInt("witherbow.gametime");
		witherbow_already_starte = ColorUtil
				.color(Main.getInstance().getConfig().getString("witherbow.already_starte"));
		witherbow_title = ColorUtil.color(Main.getInstance().getConfig().getString("witherbow.title"));
		witherbow_subtitle = ColorUtil.color(Main.getInstance().getConfig().getString("witherbow.subtitle"));
		witherbow_message = ColorUtil.color(Main.getInstance().getConfig().getString("witherbow.message"));
		teamshop_enabled = tsfilec.getBoolean("enabled");
		teamshop_title = ColorUtil.color(tsfilec.getString("title") + "§1§0§0§0§0§0");
		teamshop_message = ColorUtil.color(tsfilec.getString("message"));
		teamshop_no_resource = ColorUtil.color(tsfilec.getString("no_resource"));
		teamshop_frame = ColorUtil.listcolor(tsfilec.getStringList("frame"));
		teamshop_upgrade_fast_dig_item = tsfilec.getString("upgrade.fast_dig.item");
		teamshop_upgrade_fast_dig_name = ColorUtil.color(tsfilec.getString("upgrade.fast_dig.name"));
		teamshop_upgrade_fast_dig_level_1_cost = tsfilec.getString("upgrade.fast_dig.level_1.cost");
		teamshop_upgrade_fast_dig_level_2_cost = tsfilec.getString("upgrade.fast_dig.level_2.cost");
		teamshop_upgrade_fast_dig_level_1_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.fast_dig.level_1.lore"));
		teamshop_upgrade_fast_dig_level_2_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.fast_dig.level_2.lore"));
		teamshop_upgrade_fast_dig_level_full_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.fast_dig.level_full.lore"));
		teamshop_upgrade_sword_sharpness_item = tsfilec.getString("upgrade.sword_sharpness.item");
		teamshop_upgrade_sword_sharpness_name = ColorUtil.color(tsfilec.getString("upgrade.sword_sharpness.name"));
		teamshop_upgrade_sword_sharpness_level_1_cost = tsfilec.getString("upgrade.sword_sharpness.level_1.cost");
		teamshop_upgrade_sword_sharpness_level_2_cost = tsfilec.getString("upgrade.sword_sharpness.level_2.cost");
		teamshop_upgrade_sword_sharpness_level_1_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.sword_sharpness.level_1.lore"));
		teamshop_upgrade_sword_sharpness_level_2_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.sword_sharpness.level_2.lore"));
		teamshop_upgrade_sword_sharpness_level_full_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.sword_sharpness.level_full.lore"));
		teamshop_upgrade_armor_protection_item = tsfilec.getString("upgrade.armor_protection.item");
		teamshop_upgrade_armor_protection_name = ColorUtil.color(tsfilec.getString("upgrade.armor_protection.name"));
		teamshop_upgrade_armor_protection_level_1_cost = tsfilec.getString("upgrade.armor_protection.level_1.cost");
		teamshop_upgrade_armor_protection_level_2_cost = tsfilec.getString("upgrade.armor_protection.level_2.cost");
		teamshop_upgrade_armor_protection_level_3_cost = tsfilec.getString("upgrade.armor_protection.level_3.cost");
		teamshop_upgrade_armor_protection_level_4_cost = tsfilec.getString("upgrade.armor_protection.level_4.cost");
		teamshop_upgrade_armor_protection_level_1_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.armor_protection.level_1.lore"));
		teamshop_upgrade_armor_protection_level_2_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.armor_protection.level_2.lore"));
		teamshop_upgrade_armor_protection_level_3_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.armor_protection.level_3.lore"));
		teamshop_upgrade_armor_protection_level_4_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.armor_protection.level_4.lore"));
		teamshop_upgrade_armor_protection_level_full_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.armor_protection.level_full.lore"));
		teamshop_upgrade_trap_item = tsfilec.getString("upgrade.trap.item");
		teamshop_upgrade_trap_name = ColorUtil.color(tsfilec.getString("upgrade.trap.name"));
		teamshop_upgrade_trap_trigger_range = tsfilec.getInt("upgrade.trap.trigger_range");
		teamshop_upgrade_trap_level_1_cost = tsfilec.getString("upgrade.trap.level_1.cost");
		teamshop_upgrade_trap_trigger_title = ColorUtil.color(tsfilec.getString("upgrade.trap.trigger.title"));
		teamshop_upgrade_trap_trigger_subtitle = ColorUtil.color(tsfilec.getString("upgrade.trap.trigger.subtitle"));
		teamshop_upgrade_trap_trigger_message = ColorUtil.color(tsfilec.getString("upgrade.trap.trigger.message"));
		teamshop_upgrade_trap_level_1_lore = ColorUtil.listcolor(tsfilec.getStringList("upgrade.trap.level_1.lore"));
		teamshop_upgrade_trap_level_full_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.trap.level_full.lore"));
		teamshop_upgrade_defense_permanent = tsfilec.getBoolean("upgrade.defense.permanent");
		teamshop_upgrade_defense_item = tsfilec.getString("upgrade.defense.item");
		teamshop_upgrade_defense_name = ColorUtil.color(tsfilec.getString("upgrade.defense.name"));
		teamshop_upgrade_defense_trigger_range = tsfilec.getInt("upgrade.defense.trigger_range");
		teamshop_upgrade_defense_level_1_cost = tsfilec.getString("upgrade.defense.level_1.cost");
		teamshop_upgrade_defense_level_1_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.defense.level_1.lore"));
		teamshop_upgrade_defense_level_full_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.defense.level_full.lore"));
		teamshop_upgrade_heal_item = tsfilec.getString("upgrade.heal.item");
		teamshop_upgrade_heal_name = ColorUtil.color(tsfilec.getString("upgrade.heal.name"));
		teamshop_upgrade_heal_trigger_range = tsfilec.getInt("upgrade.heal.trigger_range");
		teamshop_upgrade_heal_level_1_cost = tsfilec.getString("upgrade.heal.level_1.cost");
		teamshop_upgrade_heal_level_1_lore = ColorUtil.listcolor(tsfilec.getStringList("upgrade.heal.level_1.lore"));
		teamshop_upgrade_heal_level_full_lore = ColorUtil
				.listcolor(tsfilec.getStringList("upgrade.heal.level_full.lore"));
		deathmode_enabled = config.getBoolean("deathmode.enabled");
		deathmode_gametime = config.getInt("deathmode.gametime");
		deathmode_title = ColorUtil.color(Main.getInstance().getConfig().getString("deathmode.title"));
		deathmode_subtitle = ColorUtil.color(Main.getInstance().getConfig().getString("deathmode.subtitle"));
		deathmode_message = ColorUtil.color(Main.getInstance().getConfig().getString("deathmode.message"));
		deathitem_enabled = config.getBoolean("deathitem.enabled");
		deathitem_items = config.getStringList("deathitem.items");
		deathitem_item_name_chinesize = config.getBoolean("deathitem.item_name_chinesize");
		deathitem_message = ColorUtil.color(Main.getInstance().getConfig().getString("deathitem.message"));
		nobreakbed_nobreakmessage = ColorUtil
				.color(Main.getInstance().getConfig().getString("nobreakbed.nobreakmessage"));
		nobreakbed_enabled = config.getBoolean("nobreakbed.enabled");
		nobreakbed_gametime = config.getInt("nobreakbed.gametime");
		nobreakbed_title = ColorUtil.color(Main.getInstance().getConfig().getString("nobreakbed.title"));
		nobreakbed_subtitle = ColorUtil.color(Main.getInstance().getConfig().getString("nobreakbed.subtitle"));
		nobreakbed_message = ColorUtil.color(Main.getInstance().getConfig().getString("nobreakbed.message"));
		spawn_no_build_enabled = config.getBoolean("spawn_no_build.enabled");
		spawn_no_build_spawn_range = config.getInt("spawn_no_build.spawn_range");
		spawn_no_build_resource_range = config.getInt("spawn_no_build.resource_range");
		spawn_no_build_message = ColorUtil.color(Main.getInstance().getConfig().getString("spawn_no_build.message"));
		holographic_resource_enabled = config.getBoolean("holographic.resource.enabled");
		holographic_bed_title_enabled = config.getBoolean("holographic.bed_title.enabled");
		holographic_resource_speed = config.getDouble("holographic.resource.speed");
		holographic_resource = new ArrayList<String>();
		for (String s : Main.getInstance().getConfig().getConfigurationSection("holographic.resource.resources")
				.getKeys(false)) {
			holographic_resource.add(s);
		}
		holographic_bedtitle_bed_destroyed = ColorUtil
				.color(Main.getInstance().getConfig().getString("holographic.bed_title.bed_destroyed"));
		holographic_bedtitle_bed_alive = ColorUtil
				.color(Main.getInstance().getConfig().getString("holographic.bed_title.bed_alive"));
		overstats_enabled = config.getBoolean("overstats.enabled");
		overstats_message = ColorUtil.listcolor(Main.getInstance().getConfig().getStringList("overstats.message"));
		actionbar = ColorUtil.color(Main.getInstance().getConfig().getString("actionbar"));
		timer = new HashMap<String, Integer>();
		for (String w : Main.getInstance().getConfig().getConfigurationSection("timer").getKeys(false)) {
			timer.put(w, Main.getInstance().getConfig().getInt("timer." + w));
		}
		planinfo = new ArrayList<String>();
		for (String w : Main.getInstance().getConfig().getConfigurationSection("planinfo").getKeys(false)) {
			planinfo.add(w);
		}
		playertag_prefix = ColorUtil.color(Main.getInstance().getConfig().getString("playertag.prefix"));
		playertag_suffix = ColorUtil.color(Main.getInstance().getConfig().getString("playertag.suffix"));
		scoreboard_interval = config.getInt("scoreboard.interval");
		scoreboard_title = ColorUtil.listcolor(Main.getInstance().getConfig().getStringList("scoreboard.title"));
		scoreboard_you = ColorUtil.color(Main.getInstance().getConfig().getString("scoreboard.you"));
		scoreboard_team_bed_status_bed_alive = ColorUtil
				.color(Main.getInstance().getConfig().getString("scoreboard.team_bed_status.bed_alive"));
		scoreboard_team_bed_status_bed_destroyed = ColorUtil
				.color(Main.getInstance().getConfig().getString("scoreboard.team_bed_status.bed_destroyed"));
		scoreboard_team_status_format_bed_alive = ColorUtil
				.color(Main.getInstance().getConfig().getString("scoreboard.team_status_format.bed_alive"));
		scoreboard_team_status_format_bed_destroyed = ColorUtil
				.color(Main.getInstance().getConfig().getString("scoreboard.team_status_format.bed_destroyed"));
		scoreboard_team_status_format_team_dead = ColorUtil
				.color(Main.getInstance().getConfig().getString("scoreboard.team_status_format.team_dead"));
		scoreboard_lines = new HashMap<String, List<String>>();
		for (String key : Main.getInstance().getConfig().getConfigurationSection("scoreboard.lines").getKeys(false)) {
			scoreboard_lines.put(key,
					ColorUtil.listcolor(Main.getInstance().getConfig().getStringList("scoreboard.lines." + key)));
		}
		/*
		 * for (String w :
		 * Main.getInstance().getConfig().getStringList("scoreboard.lines")) { String
		 * line = ColorUtil.color(w); if (scoreboard_lines.size() < 15) { if
		 * (scoreboard_lines.contains(line)) {
		 * scoreboard_lines.add(conflict(scoreboard_lines, line)); } else {
		 * scoreboard_lines.add(line); } } }
		 */
		lobby_scoreboard_enabled = config.getBoolean("lobby_scoreboard.enabled");
		lobby_scoreboard_interval = config.getInt("lobby_scoreboard.interval");
		lobby_scoreboard_state_waiting = ColorUtil
				.color(Main.getInstance().getConfig().getString("lobby_scoreboard.state.waiting"));
		lobby_scoreboard_state_countdown = ColorUtil
				.color(Main.getInstance().getConfig().getString("lobby_scoreboard.state.countdown"));
		lobby_scoreboard_title = ColorUtil
				.listcolor(Main.getInstance().getConfig().getStringList("lobby_scoreboard.title"));
		lobby_scoreboard_lines = new ArrayList<String>();
		for (String w : Main.getInstance().getConfig().getStringList("lobby_scoreboard.lines")) {
			String line = ColorUtil.color(w);
			if (lobby_scoreboard_lines.size() < 15) {
				if (lobby_scoreboard_lines.contains(line)) {
					lobby_scoreboard_lines.add(conflict(lobby_scoreboard_lines, line));
				} else {
					lobby_scoreboard_lines.add(line);
				}
			}
		}
		updataShop();
		Bukkit.getConsoleSender().sendMessage(prefix + "§a配置文件加载成功！");
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
		if (filec.getStringList("shop." + game + "." + type) != null) {
			loc.addAll(filec.getStringList("shop." + game + "." + type));
		}
		loc.add(location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ()
				+ ", " + location.getYaw() + ", " + location.getPitch());
		filec.set("shop." + game + "." + type, loc);
		try {
			filec.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		updataShop();
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
		updataShop();
	}

	private static void updataShop() {
		File folder = new File(Main.getInstance().getDataFolder(), "/");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder.getAbsolutePath() + "/game.yml");
		shop_item = new HashMap<String, List<String>>();
		shop_team = new HashMap<String, List<String>>();
		shop_shops = new HashMap<String, String>();
		int i = 0;
		if (!file.exists()) {
			return;
		}
		FileConfiguration filec = YamlConfiguration.loadConfiguration(file);
		if (!filec.getKeys(false).contains("shop")) {
			return;
		}
		for (String game : filec.getConfigurationSection("shop").getKeys(false)) {
			if (filec.getStringList("shop." + game + ".item") != null) {
				shop_item.put(game, filec.getStringList("shop." + game + ".item"));
				for (String shop : filec.getStringList("shop." + game + ".item")) {
					shop_shops.put(i + "", "shop." + game + ".item - " + shop);
					i++;
				}
			}
			if (filec.getStringList("shop." + game + ".team") != null) {
				shop_team.put(game, filec.getStringList("shop." + game + ".team"));
				for (String shop : filec.getStringList("shop." + game + ".team")) {
					shop_shops.put(i + "", "shop." + game + ".team - " + shop);
					i++;
				}
			}
		}
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
		if (language_config.contains(path) && language_config.isString(path)) {
			return ColorUtil.color(language_config.getString(path));
		}
		return "null";
	}

	public static List<String> getLanguageList(String path) {
		if (language_config.contains(path) && language_config.isList(path)) {
			return ColorUtil.listcolor(language_config.getStringList(path));
		}
		return Arrays.asList("null");
	}

	private static File getLanguageFile() {
		File folder = new File(Main.getInstance().getDataFolder(), "/");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder.getAbsolutePath() + "/language.yml");
		if (!file.exists()) {
			Main.getInstance().saveResource("language.yml", false);
		}
		return file;
	}
}
