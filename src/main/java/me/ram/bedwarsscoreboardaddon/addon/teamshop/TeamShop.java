package me.ram.bedwarsscoreboardaddon.addon.teamshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import ldcr.BedwarsXP.api.XPManager;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.addon.PlaySound;
import me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades.IronForge;
import me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades.Protection;
import me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades.Sharpness;
import me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades.Upgrade;
import me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades.UpgradeType;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.menu.MenuManager;
import me.ram.bedwarsscoreboardaddon.menu.MenuType;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import me.ram.bedwarsscoreboardaddon.utils.ItemUtil;

public class TeamShop {

	@Getter
	private Game game;
	private Map<Team, List<Upgrade>> upgrades;
	private Map<Team, List<Upgrade>> upgrades_trap;
	private Map<Team, Map<Player, Long>> player_cooldown;
	private List<Player> immune_players;
	private List<Listener> listeners;

	public TeamShop(Game game) {
		this.game = game;
		upgrades = new HashMap<Team, List<Upgrade>>();
		upgrades_trap = new HashMap<Team, List<Upgrade>>();
		player_cooldown = new HashMap<Team, Map<Player, Long>>();
		immune_players = new ArrayList<Player>();
		listeners = new ArrayList<Listener>();
		for (Team team : game.getTeams().values()) {
			List<Upgrade> list = new ArrayList<Upgrade>();
			list.add(new Protection(game, team, 0));
			list.add(new Sharpness(game, team, 0));
			IronForge ironForge = new IronForge(game, team, 0);
			list.add(ironForge);
			upgrades.put(team, list);
			Bukkit.getPluginManager().registerEvents(ironForge, Main.getInstance());
			listeners.add(ironForge);
		}
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		arena.addGameTask(new BukkitRunnable() {

			@Override
			public void run() {
				upgrades.values().forEach(list -> {
					list.forEach(upgrade -> {
						upgrade.runUpgrade();
					});
				});
				upgrades_trap.values().forEach(list -> {
					if (list.size() > 0) {
						list.get(0).runUpgrade();
					}
				});
				player_cooldown.values().forEach(map -> {
					Iterator<Entry<Player, Long>> iter = map.entrySet().iterator();
					while (iter.hasNext()) {
						Entry<Player, Long> entry = iter.next();
						Player player = entry.getKey();
						if (BedwarsUtil.isSpectator(game, player) || player.isDead() || player.getGameMode().equals(GameMode.SPECTATOR)) {
							iter.remove();
						}
					}
				});
			}
		}.runTaskTimer(Main.getInstance(), 0L, 5L));
	}

	public void openTeamShop(Player player) {
		if (!Config.teamshop_enabled) {
			return;
		}
		if (game.getState() != GameState.RUNNING || BedwarsUtil.isSpectator(game, player) || player.getGameMode() == GameMode.SPECTATOR) {
			return;
		}
		int trap_amount = 0;
		for (UpgradeType type : Config.teamshop_upgrade_enabled.keySet()) {
			if (Config.teamshop_upgrade_enabled.get(type) && type.isTrap()) {
				trap_amount++;
			}
		}
		Inventory inventory = Bukkit.createInventory(null, trap_amount > 0 ? 45 : 27, Config.teamshop_upgrade_shop_title);
		setTeamShopItem(player, inventory);
		player.closeInventory();
		player.openInventory(inventory);
		Main.getInstance().getMenuManager().addPlayer(player, MenuType.TEAM_SHOP, inventory);
	}

	public void openTeamShopTrap(Player player) {
		if (!Config.teamshop_enabled) {
			return;
		}
		if (game.getState() != GameState.RUNNING || BedwarsUtil.isSpectator(game, player) || player.getGameMode() == GameMode.SPECTATOR) {
			return;
		}
		Inventory inventory = Bukkit.createInventory(null, 27, Config.teamshop_trap_shop_title);
		setTeamShopTrapItem(player, inventory);
		player.closeInventory();
		player.openInventory(inventory);
		Main.getInstance().getMenuManager().addPlayer(player, MenuType.TEAM_SHOP_TRAP, inventory);
	}

	public void onEnd() {
		listeners.forEach(listener -> {
			HandlerList.unregisterAll(listener);
		});
	}

	public void setTeamShopTrapItem(Player player, Inventory inventory) {
		inventory.clear();
		int slot = 10;
		for (UpgradeType type : Config.teamshop_upgrade_enabled.keySet()) {
			if (Config.teamshop_upgrade_enabled.get(type) && type.isTrap()) {
				ItemStack itemStack = ItemUtil.createItem(Config.teamshop_upgrade_item.get(type));
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName(getStateColor(player, type) + Config.teamshop_upgrade_name.get(type));
				itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
				int level = getPlayerTeamUpgradeTrapLevel(player);
				int lev = level;
				lev = lev > 2 ? 2 : lev;
				String next_cost = Config.teamshop_trap_level_cost.get(lev + 1).split(",")[1];
				if (level < 3) {
					itemMeta.setLore(Config.teamshop_upgrade_level_lore.get(type).get(1));
				} else {
					itemMeta.setLore(Config.teamshop_upgrade_level_lore.get(type).get(2));
				}
				itemMeta.setLore(replaceLore(itemMeta.getLore(), "{state}", getState(player, type), "{cost}", next_cost));
				itemStack.setItemMeta(itemMeta);
				inventory.setItem(slot, itemStack);
				slot++;
			}
		}
		ItemStack back = new ItemStack(Material.ARROW);
		ItemUtil.setItemName(back, getItemName(Config.teamshop_trap_shop_back));
		ItemUtil.setItemLore(back, getItemLore(Config.teamshop_trap_shop_back));
		inventory.setItem(22, back);
		player.updateInventory();
	}

	public void setTeamShopItem(Player player, Inventory inventory) {
		inventory.clear();
		int slot = 10;
		int trap_amount = 0;
		for (UpgradeType type : Config.teamshop_upgrade_enabled.keySet()) {
			if (Config.teamshop_upgrade_enabled.get(type)) {
				if (type.isTrap()) {
					trap_amount++;
					continue;
				}
				ItemStack itemStack = ItemUtil.createItem(Config.teamshop_upgrade_item.get(type));
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName(getStateColor(player, type) + Config.teamshop_upgrade_name.get(type));
				itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
				itemMeta.setLore(Config.teamshop_upgrade_level_lore.get(type).get(getPlayerTeamUpgradeLevel(player, type) + 1));
				itemMeta.setLore(replaceLore(itemMeta.getLore(), "{state}", getState(player, type)));
				itemStack.setItemMeta(itemMeta);
				inventory.setItem(slot, itemStack);
				slot++;
			}
		}
		if (trap_amount > 0) {
			ItemStack glasspane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
			ItemUtil.setItemName(glasspane, getItemName(Config.teamshop_upgrade_shop_frame));
			ItemUtil.setItemLore(glasspane, getItemLore(Config.teamshop_upgrade_shop_frame));
			inventory.setItem(18, glasspane);
			inventory.setItem(19, glasspane);
			inventory.setItem(20, glasspane);
			inventory.setItem(21, glasspane);
			inventory.setItem(22, glasspane);
			inventory.setItem(23, glasspane);
			inventory.setItem(24, glasspane);
			inventory.setItem(25, glasspane);
			inventory.setItem(26, glasspane);
			List<Upgrade> list = upgrades_trap.getOrDefault(game.getPlayerTeam(player), new ArrayList<Upgrade>());
			int size = list.size();
			ItemStack traps = new ItemStack(Material.valueOf(Config.teamshop_upgrade_shop_trap_item));
			ItemUtil.setItemName(traps, Config.teamshop_upgrade_shop_trap_name);
			ItemUtil.setItemLore(traps, Config.teamshop_upgrade_shop_trap_lore);
			inventory.setItem(slot, traps);
			ItemStack trap = new ItemStack(Material.STAINED_GLASS, 1, (short) 8);
			int lev = size;
			lev = lev > 2 ? 2 : lev;
			String next_cost = Config.teamshop_trap_level_cost.get(lev + 1).split(",")[1];
			if (size > 0) {
				Upgrade upgrade = list.get(0);
				trap.setType(Material.valueOf(Config.teamshop_upgrade_item.get(upgrade.getType())));
				trap.setDurability((short) 0);
				ItemUtil.setItemName(trap, getItemName(Config.teamshop_trap_trap_list_trap_1_unlock).replace("{trap}", upgrade.getName()).replace("{buyer}", upgrade.getBuyer()).replace("{cost}", next_cost));
				ItemUtil.setItemLore(trap, replaceLore(getItemLore(Config.teamshop_trap_trap_list_trap_1_unlock), "{trap}", upgrade.getName(), "{buyer}", upgrade.getBuyer(), "{cost}", next_cost));
			} else {
				ItemUtil.setItemName(trap, getItemName(Config.teamshop_trap_trap_list_trap_1_lock).replace("{cost}", next_cost));
				ItemUtil.setItemLore(trap, replaceLore(getItemLore(Config.teamshop_trap_trap_list_trap_1_lock), "{cost}", next_cost));
			}
			ItemUtil.addItemFlags(trap, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
			inventory.setItem(30, trap);
			trap = new ItemStack(Material.STAINED_GLASS, 1, (short) 8);
			if (size > 1) {
				Upgrade upgrade = list.get(1);
				trap.setType(Material.valueOf(Config.teamshop_upgrade_item.get(upgrade.getType())));
				trap.setDurability((short) 0);
				ItemUtil.setItemName(trap, getItemName(Config.teamshop_trap_trap_list_trap_2_unlock).replace("{trap}", upgrade.getName()).replace("{buyer}", upgrade.getBuyer()).replace("{cost}", next_cost));
				ItemUtil.setItemLore(trap, replaceLore(getItemLore(Config.teamshop_trap_trap_list_trap_2_unlock), "{trap}", upgrade.getName(), "{buyer}", upgrade.getBuyer(), "{cost}", next_cost));
			} else {
				ItemUtil.setItemName(trap, getItemName(Config.teamshop_trap_trap_list_trap_2_lock).replace("{cost}", next_cost));
				ItemUtil.setItemLore(trap, replaceLore(getItemLore(Config.teamshop_trap_trap_list_trap_2_lock), "{cost}", next_cost));
			}
			ItemUtil.addItemFlags(trap, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
			inventory.setItem(31, trap);
			trap = new ItemStack(Material.STAINED_GLASS, 1, (short) 8);
			if (size > 2) {
				Upgrade upgrade = list.get(2);
				trap.setType(Material.valueOf(Config.teamshop_upgrade_item.get(upgrade.getType())));
				trap.setDurability((short) 0);
				ItemUtil.setItemName(trap, getItemName(Config.teamshop_trap_trap_list_trap_3_unlock).replace("{trap}", upgrade.getName()).replace("{buyer}", upgrade.getBuyer()).replace("{cost}", next_cost));
				ItemUtil.setItemLore(trap, replaceLore(getItemLore(Config.teamshop_trap_trap_list_trap_3_unlock), "{trap}", upgrade.getName(), "{buyer}", upgrade.getBuyer(), "{cost}", next_cost));
			} else {
				ItemUtil.setItemName(trap, getItemName(Config.teamshop_trap_trap_list_trap_3_lock).replace("{cost}", next_cost));
				ItemUtil.setItemLore(trap, replaceLore(getItemLore(Config.teamshop_trap_trap_list_trap_3_lock), "{cost}", next_cost));
			}
			ItemUtil.addItemFlags(trap, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
			inventory.setItem(32, trap);
		}
		player.updateInventory();
	}

	public void updateTeamShop(Player player) {
		Team team = game.getPlayerTeam(player);
		if (team == null) {
			return;
		}
		for (Player p : team.getPlayers()) {
			MenuManager mman = Main.getInstance().getMenuManager();
			if (mman.isOpen(p, MenuType.TEAM_SHOP)) {
				setTeamShopItem(p, mman.getInventory(p));
			} else if (mman.isOpen(p, MenuType.TEAM_SHOP_TRAP)) {
				setTeamShopTrapItem(p, mman.getInventory(p));
			}
		}
	}

	private Map<String, Integer> getUpgradeSlot(boolean trap) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		int slot = 10;
		for (UpgradeType type : Config.teamshop_upgrade_enabled.keySet()) {
			if (Config.teamshop_upgrade_enabled.get(type) && trap == type.isTrap()) {
				map.put(type.name(), slot);
				slot++;
			}
		}
		if (!trap) {
			map.put("TRAPS", slot);
		}
		return map;
	}

	public void onClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getWhoClicked();
		new BukkitRunnable() {
			@Override
			public void run() {
				ItemStack[] stacks = player.getInventory().getContents();
				for (int i = 0; i < stacks.length; i++) {
					ItemStack stack = stacks[i];
					if (stack == null) {
						continue;
					}
					ItemMeta meta = stack.getItemMeta();
					if (!meta.hasLore()) {
						continue;
					}
					if (meta.getLore().contains("§a§r§m§o§r§0§0§1") || meta.getLore().contains("§a§r§m§o§r§0§0§2") || meta.getLore().contains("§a§r§m§o§r§0§0§3")) {
						stack.setType(Material.AIR);
						player.getInventory().setItem(i, stack);
						player.updateInventory();
						ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
						ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
						if (meta.getLore().contains("§a§r§m§o§r§0§0§1")) {
							leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
							boots = new ItemStack(Material.CHAINMAIL_BOOTS);
						} else if (meta.getLore().contains("§a§r§m§o§r§0§0§2")) {
							leggings = new ItemStack(Material.IRON_LEGGINGS);
							boots = new ItemStack(Material.IRON_BOOTS);
						} else if (meta.getLore().contains("§a§r§m§o§r§0§0§3")) {
							leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
							boots = new ItemStack(Material.DIAMOND_BOOTS);
						}
						ItemMeta leggingsMeta = leggings.getItemMeta();
						ItemMeta bootsMeta = leggings.getItemMeta();
						leggingsMeta.spigot().setUnbreakable(true);
						bootsMeta.spigot().setUnbreakable(true);
						leggings.setItemMeta(leggingsMeta);
						boots.setItemMeta(bootsMeta);
						player.getInventory().setLeggings(leggings);
						player.getInventory().setBoots(boots);
						break;
					} else if (stack.getType() != Material.WOOD_SWORD && meta.getLore().contains("§s§w§o§r§d")) {
						player.getInventory().remove(Material.WOOD_SWORD);
						List<String> lore = meta.getLore();
						lore.remove("§s§w§o§r§d");
						meta.setLore(lore);
						stack.setItemMeta(meta);
						ItemStack item = stack.clone();
						player.getInventory().setItem(i, new ItemStack(Material.AIR));
						player.getInventory().addItem(item);
						player.updateInventory();
						break;
					}
				}
			}
		}.runTaskLater(Main.getInstance(), 1L);
		MenuManager mman = Main.getInstance().getMenuManager();
		if (mman.isOpen(player, MenuType.TEAM_SHOP)) {
			e.setCancelled(true);
			Team team = game.getPlayerTeam(player);
			if (team == null) {
				return;
			}
			Map<String, Integer> upgradeSlot = getUpgradeSlot(false);
			if (e.getRawSlot() == upgradeSlot.getOrDefault(UpgradeType.SHARPNESS.name(), -233333)) {
				int level = getPlayerTeamUpgradeLevel(player, UpgradeType.SHARPNESS);
				if (level < 2) {
					if (pay(player, Config.teamshop_upgrade_level_cost.get(UpgradeType.SHARPNESS).get(level + 1))) {
						getPlayerTeamUpgrade(player, UpgradeType.SHARPNESS).setLevel(level + 1);
						updateTeamShop(player);
						for (Player p : team.getPlayers()) {
							p.sendMessage(Config.teamshop_message_upgrade.replace("{player}", player.getName()).replace("{upgrade}", ColorUtil.removeColor(Config.teamshop_upgrade_name.get(UpgradeType.SHARPNESS))).replace("{level}", getLevel(getPlayerTeamUpgradeLevel(player, UpgradeType.SHARPNESS))));
						}
					} else {
						player.sendMessage(Config.teamshop_message_no_resource);
						PlaySound.playSound(player, Config.play_sound_sound_no_resource);
					}
				}
			} else if (e.getRawSlot() == upgradeSlot.getOrDefault(UpgradeType.PROTECTION.name(), -233333)) {
				int level = getPlayerTeamUpgradeLevel(player, UpgradeType.PROTECTION);
				if (level < 4) {
					if (pay(player, Config.teamshop_upgrade_level_cost.get(UpgradeType.PROTECTION).get(level + 1))) {
						getPlayerTeamUpgrade(player, UpgradeType.PROTECTION).setLevel(level + 1);
						updateTeamShop(player);
						for (Player p : team.getPlayers()) {
							p.sendMessage(Config.teamshop_message_upgrade.replace("{player}", player.getName()).replace("{upgrade}", ColorUtil.removeColor(Config.teamshop_upgrade_name.get(UpgradeType.PROTECTION))).replace("{level}", getLevel(getPlayerTeamUpgradeLevel(player, UpgradeType.PROTECTION))));
						}
					} else {
						player.sendMessage(Config.teamshop_message_no_resource);
						PlaySound.playSound(player, Config.play_sound_sound_no_resource);
					}
				}
			} else if (e.getRawSlot() == upgradeSlot.getOrDefault(UpgradeType.FAST_DIG.name(), -233333)) {
				int level = getPlayerTeamUpgradeLevel(player, UpgradeType.FAST_DIG);
				if (level < 2) {
					if (pay(player, Config.teamshop_upgrade_level_cost.get(UpgradeType.FAST_DIG).get(level + 1))) {
						getPlayerTeamUpgrade(player, UpgradeType.FAST_DIG).setLevel(level + 1);
						updateTeamShop(player);
						for (Player p : team.getPlayers()) {
							p.sendMessage(Config.teamshop_message_upgrade.replace("{player}", player.getName()).replace("{upgrade}", ColorUtil.removeColor(Config.teamshop_upgrade_name.get(UpgradeType.FAST_DIG))).replace("{level}", getLevel(getPlayerTeamUpgradeLevel(player, UpgradeType.FAST_DIG))));
						}
					} else {
						player.sendMessage(Config.teamshop_message_no_resource);
						PlaySound.playSound(player, Config.play_sound_sound_no_resource);
					}
				}
			} else if (e.getRawSlot() == upgradeSlot.getOrDefault(UpgradeType.IRON_FORGE.name(), -233333)) {
				int level = getPlayerTeamUpgradeLevel(player, UpgradeType.IRON_FORGE);
				if (level < 4) {
					if (pay(player, Config.teamshop_upgrade_level_cost.get(UpgradeType.IRON_FORGE).get(level + 1))) {
						getPlayerTeamUpgrade(player, UpgradeType.IRON_FORGE).setLevel(level + 1);
						updateTeamShop(player);
						for (Player p : team.getPlayers()) {
							p.sendMessage(Config.teamshop_message_upgrade.replace("{player}", player.getName()).replace("{upgrade}", ColorUtil.removeColor(Config.teamshop_upgrade_name.get(UpgradeType.IRON_FORGE))).replace("{level}", getLevel(getPlayerTeamUpgradeLevel(player, UpgradeType.IRON_FORGE))));
						}
					} else {
						player.sendMessage(Config.teamshop_message_no_resource);
						PlaySound.playSound(player, Config.play_sound_sound_no_resource);
					}
				}
			} else if (e.getRawSlot() == upgradeSlot.getOrDefault(UpgradeType.HEAL.name(), -233333)) {
				if (getPlayerTeamUpgradeLevel(player, UpgradeType.HEAL) < 1) {
					if (pay(player, Config.teamshop_upgrade_level_cost.get(UpgradeType.HEAL).get(1))) {
						getPlayerTeamUpgrade(player, UpgradeType.HEAL).setLevel(1);
						updateTeamShop(player);
						for (Player p : team.getPlayers()) {
							p.sendMessage(Config.teamshop_message_upgrade.replace("{player}", player.getName()).replace("{upgrade}", ColorUtil.removeColor(Config.teamshop_upgrade_name.get(UpgradeType.HEAL))).replace("{level}", ""));
						}
					} else {
						player.sendMessage(Config.teamshop_message_no_resource);
						PlaySound.playSound(player, Config.play_sound_sound_no_resource);
					}
				}
			} else if (e.getRawSlot() == upgradeSlot.getOrDefault("TRAPS", -233333)) {
				openTeamShopTrap(player);
			}
		} else if (mman.isOpen(player, MenuType.TEAM_SHOP_TRAP)) {
			e.setCancelled(true);
			Team team = game.getPlayerTeam(player);
			if (team == null) {
				return;
			}
			int level = getPlayerTeamUpgradeTrapLevel(player);
			Map<String, Integer> upgradeSlot = getUpgradeSlot(true);
			if (e.getRawSlot() == 22) {
				openTeamShop(player);
			} else if (e.getRawSlot() == upgradeSlot.getOrDefault(UpgradeType.TRAP.name(), -233333)) {
				if (level < 3) {
					if (pay(player, Config.teamshop_trap_level_cost.get(level + 1))) {
						createTrapUpgrade(player, UpgradeType.TRAP);
						updateTeamShop(player);
						for (Player p : team.getPlayers()) {
							p.sendMessage(Config.teamshop_message_upgrade.replace("{player}", player.getName()).replace("{upgrade}", ColorUtil.removeColor(Config.teamshop_upgrade_name.get(UpgradeType.TRAP))).replace("{level}", ""));
						}
						openTeamShop(player);
					} else {
						player.sendMessage(Config.teamshop_message_no_resource);
						PlaySound.playSound(player, Config.play_sound_sound_no_resource);
					}
				}
			} else if (e.getRawSlot() == upgradeSlot.getOrDefault(UpgradeType.COUNTER_OFFENSIVE_TRAP.name(), -233333)) {
				if (level < 3) {
					if (pay(player, Config.teamshop_trap_level_cost.get(level + 1))) {
						createTrapUpgrade(player, UpgradeType.COUNTER_OFFENSIVE_TRAP);
						updateTeamShop(player);
						for (Player p : team.getPlayers()) {
							p.sendMessage(Config.teamshop_message_upgrade.replace("{player}", player.getName()).replace("{upgrade}", ColorUtil.removeColor(Config.teamshop_upgrade_name.get(UpgradeType.COUNTER_OFFENSIVE_TRAP))).replace("{level}", ""));
						}
						openTeamShop(player);
					} else {
						player.sendMessage(Config.teamshop_message_no_resource);
						PlaySound.playSound(player, Config.play_sound_sound_no_resource);
					}
				}
			} else if (e.getRawSlot() == upgradeSlot.getOrDefault(UpgradeType.ALARM_TRAP.name(), -233333)) {
				if (level < 3) {
					if (pay(player, Config.teamshop_trap_level_cost.get(level + 1))) {
						createTrapUpgrade(player, UpgradeType.ALARM_TRAP);
						updateTeamShop(player);
						for (Player p : team.getPlayers()) {
							p.sendMessage(Config.teamshop_message_upgrade.replace("{player}", player.getName()).replace("{upgrade}", ColorUtil.removeColor(Config.teamshop_upgrade_name.get(UpgradeType.ALARM_TRAP))).replace("{level}", ""));
						}
						openTeamShop(player);
					} else {
						player.sendMessage(Config.teamshop_message_no_resource);
						PlaySound.playSound(player, Config.play_sound_sound_no_resource);
					}
				}
			} else if (e.getRawSlot() == upgradeSlot.getOrDefault(UpgradeType.DEFENSE.name(), -233333)) {
				if (level < 3) {
					if (pay(player, Config.teamshop_trap_level_cost.get(level + 1))) {
						createTrapUpgrade(player, UpgradeType.DEFENSE);
						updateTeamShop(player);
						for (Player p : team.getPlayers()) {
							p.sendMessage(Config.teamshop_message_upgrade.replace("{player}", player.getName()).replace("{upgrade}", ColorUtil.removeColor(Config.teamshop_upgrade_name.get(UpgradeType.DEFENSE))).replace("{level}", ""));
						}
						openTeamShop(player);
					} else {
						player.sendMessage(Config.teamshop_message_no_resource);
						PlaySound.playSound(player, Config.play_sound_sound_no_resource);
					}
				}
			}
		}
	}

	public void removeTrap(Upgrade upgrade) {
		Team team = upgrade.getTeam();
		if (!upgrades_trap.containsKey(team)) {
			return;
		}
		List<Upgrade> list = upgrades_trap.get(team);
		if (list.contains(upgrade)) {
			list.remove(upgrade);
		}
	}

	public void addCoolingPlayer(Team team, Player player) {
		if (isImmunePlayer(player)) {
			return;
		}
		if (!player_cooldown.containsKey(team)) {
			player_cooldown.put(team, new HashMap<Player, Long>());
		}
		player_cooldown.get(team).put(player, System.currentTimeMillis());
	}

	public void removeCoolingPlayer(Team team, Player player) {
		if (!player_cooldown.containsKey(team)) {
			player_cooldown.put(team, new HashMap<Player, Long>());
		}
		Map<Player, Long> map = player_cooldown.get(team);
		if (map.containsKey(player)) {
			map.remove(player);
		}
	}

	public void removeCoolingPlayer(Player player) {
		player_cooldown.values().forEach(map -> {
			if (map.containsKey(player)) {
				map.remove(player);
			}
		});
	}

	public boolean isCoolingPlayer(Team team, Player player) {
		if (!player_cooldown.containsKey(team)) {
			player_cooldown.put(team, new HashMap<Player, Long>());
		}
		return (System.currentTimeMillis() - player_cooldown.get(team).getOrDefault(player, 0L)) < (Config.teamshop_trap_cooldown * 1000);
	}

	public List<Player> getImmunePlayers() {
		return immune_players;
	}

	public boolean isImmunePlayer(Player player) {
		return immune_players.contains(player);
	}

	public void addImmunePlayer(Player player) {
		if (!immune_players.contains(player)) {
			immune_players.add(player);
		}
	}

	public void removeImmunePlayer(Player player) {
		if (immune_players.contains(player)) {
			immune_players.remove(player);
		}
	}

	private boolean pay(Player player, String cost) {
		String[] ary = cost.split(",");
		if (ary[0].equals("XP")) {
			if (Bukkit.getPluginManager().isPluginEnabled("BedwarsXP")) {
				if (XPManager.getXPManager(game.getName()).getXP(player) >= Integer.valueOf(ary[1])) {
					XPManager.getXPManager(game.getName()).takeXP(player, Integer.valueOf(ary[1]));
					return true;
				}
			}
		} else if (isEnoughItem(player, ary)) {
			takeItem(player, ary);
			return true;
		}
		return false;
	}

	private boolean isEnough(Player player, String cost) {
		String[] ary = cost.split(",");
		if (ary[0].equals("XP")) {
			if (Bukkit.getPluginManager().isPluginEnabled("BedwarsXP")) {
				return XPManager.getXPManager(game.getName()).getXP(player) >= Integer.valueOf(ary[1]);
			}
		} else {
			return isEnoughItem(player, ary);
		}
		return false;
	}

	private boolean isEnoughItem(Player player, String[] ary) {
		int k = 0;
		int i = player.getInventory().getContents().length;
		ItemStack[] stacks = player.getInventory().getContents();
		for (int j = 0; j < i; j++) {
			final ItemStack stack = stacks[j];
			if (stack != null) {
				if (stack.getType().equals(Material.valueOf(ary[0]))) {
					k = k + stack.getAmount();
				}
			}
		}
		if (k >= Integer.valueOf(ary[1])) {
			return true;
		}
		return false;
	}

	private void takeItem(Player player, String[] ary) {
		int ta = Integer.valueOf(ary[1]);
		int i = player.getInventory().getContents().length;
		ItemStack[] stacks = player.getInventory().getContents();
		for (int j = 0; j < i; j++) {
			final ItemStack stack = stacks[j];
			if (stack != null) {
				if (stack.getType().equals(Material.valueOf(ary[0])) && ta > 0) {
					if (stack.getAmount() >= ta) {
						stack.setAmount(stack.getAmount() - ta);
						ta = 0;
					} else if (stack.getAmount() < ta) {
						ta = ta - stack.getAmount();
						stack.setAmount(0);
					}
					player.getInventory().setItem(j, stack);
				}
			}
		}
	}

	private List<String> replaceLore(List<String> lore, String... args) {
		List<String> list = new ArrayList<String>();
		if (lore == null || lore.isEmpty()) {
			return list;
		}
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < ((int) args.length / 2); i++) {
			int j = i * 2;
			map.put(args[j], args[j + 1]);
		}
		lore.forEach(line -> {
			for (String key : map.keySet()) {
				line = line.replace(key, map.get(key));
			}
			list.add(line);
		});
		return list;
	}

	private String getStateColor(Player player, UpgradeType type) {
		int value = getStateValue(player, type);
		return value == 0 ? "§c" : value == 1 ? "§e" : "§a";
	}

	private String getState(Player player, UpgradeType type) {
		int value = getStateValue(player, type);
		return value == 0 ? Config.teamshop_state_no_resource : value == 1 ? Config.teamshop_state_lock : Config.teamshop_state_unlock;
	}

	private int getStateValue(Player player, UpgradeType type) {
		if (type.isTrap()) {
			int level = getPlayerTeamUpgradeTrapLevel(player);
			if (level >= 3) {
				return 2;
			}
			String cost = Config.teamshop_trap_level_cost.get(level + 1);
			return isEnough(player, cost) ? 1 : 0;
		}
		int level = getPlayerTeamUpgradeLevel(player, type);
		if (level >= Config.teamshop_upgrade_level_cost.get(type).size()) {
			return 2;
		}
		String cost = Config.teamshop_upgrade_level_cost.get(type).get(level + 1);
		return isEnough(player, cost) ? 1 : 0;
	}

	private Upgrade getPlayerTeamUpgrade(Player player, UpgradeType type) {
		Team team = game.getPlayerTeam(player);
		if (team == null) {
			return null;
		}
		if (!upgrades.containsKey(team)) {
			upgrades.put(team, new ArrayList<Upgrade>());
		}
		for (Upgrade upgrade : upgrades.get(team)) {
			if (upgrade.getType().equals(type)) {
				return upgrade;
			}
		}
		try {
			List<Upgrade> list = upgrades.getOrDefault(team, new ArrayList<Upgrade>());
			Upgrade upgrade = type.getUpgradeClass().getConstructor(Game.class, Team.class, int.class).newInstance(game, team, 0);
			list.add(upgrade);
			upgrades.put(team, list);
			return upgrade;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void createTrapUpgrade(Player player, UpgradeType type) {
		Team team = game.getPlayerTeam(player);
		if (team == null) {
			return;
		}
		try {
			List<Upgrade> list = upgrades_trap.getOrDefault(team, new ArrayList<Upgrade>());
			Upgrade upgrade = type.getUpgradeClass().getConstructor(Game.class, Team.class, int.class).newInstance(game, team, 1);
			upgrade.setBuyer(player.getName());
			list.add(upgrade);
			upgrades_trap.put(team, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getPlayerTeamUpgradeLevel(Player player, UpgradeType type) {
		Team team = game.getPlayerTeam(player);
		if (team == null) {
			return 0;
		}
		if (!upgrades.containsKey(team)) {
			return 0;
		}
		for (Upgrade upgrade : upgrades.get(team)) {
			if (upgrade.getType().equals(type)) {
				return upgrade.getLevel();
			}
		}
		return 0;
	}

	private int getPlayerTeamUpgradeTrapLevel(Player player) {
		Team team = game.getPlayerTeam(player);
		if (!upgrades_trap.containsKey(team)) {
			return 0;
		}
		return upgrades_trap.get(game.getPlayerTeam(player)).size();
	}

	private String getLevel(int i) {
		switch (i) {
		case 1:
			return "I";
		case 2:
			return "II";
		case 3:
			return "III";
		case 4:
			return "IV";
		default:
			return "";
		}
	}

	private String getItemName(List<String> list) {
		if (list.size() > 0) {
			return list.get(0);
		}
		return "§f";
	}

	private List<String> getItemLore(List<String> list) {
		List<String> lore = new ArrayList<String>();
		if (list.size() > 1) {
			lore.addAll(list);
			lore.remove(0);
		}
		return lore;
	}
}
