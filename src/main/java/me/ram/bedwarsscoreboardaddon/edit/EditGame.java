package me.ram.bedwarsscoreboardaddon.edit;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsPlayerLeaveEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.game.TeamColor;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;

public class EditGame implements Listener {

	public EditGame() {
		onPacketSending();
	}

	public static void editGame(Player player, Game game) {
		openMenu(player, game);
		removeEditItem(player);
		giveItems(player, game);
	}

	private static void openMenu(Player player, Game game) {
		Inventory inventory = Bukkit.createInventory(null, 54,
				"¡ìe¡ìd¡ìi¡ìt¡ì8" + Config.getLanguage("inventory.edit_game") + " - " + game.getName());
		ItemStack itemStack = new ItemStack(Material.ENDER_PEARL);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_lobby"));
		List<String> lore = new ArrayList<String>();
		lore.add("");
		if (game.getLobby() == null) {
			lore.add(Config.getLanguage("item.edit_game.lore.set"));
		} else {
			lore.add(Config.getLanguage("item.edit_game.lore.complete"));
		}
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(11, itemStack);
		itemStack.setType(Material.FIREWORK);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_mix_players"));
		lore = new ArrayList<String>();
		lore.add("");
		lore.add(Config.getLanguage("item.edit_game.lore.mix_players").replace("{players}", game.getMinPlayers() + ""));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(12, itemStack);
		itemStack.setType(Material.SIGN);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.edit_team"));
		lore = new ArrayList<String>();
		lore.add("");
		lore.add(Config.getLanguage("item.edit_game.lore.browse"));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(13, itemStack);
		itemStack.setType(Material.NETHER_STAR);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_bed"));
		lore = new ArrayList<String>();
		lore.add("");
		lore.add(Config.getLanguage("item.edit_game.lore.browse"));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(14, itemStack);
		itemStack.setType(Material.FEATHER);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_spawn"));
		lore = new ArrayList<String>();
		lore.add("");
		lore.add(Config.getLanguage("item.edit_game.lore.browse"));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(15, itemStack);
		itemStack.setType(Material.STORAGE_MINECART);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_item_shop"));
		lore = new ArrayList<String>();
		lore.add("");
		lore.add(Config.getLanguage("item.edit_game.lore.set"));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(20, itemStack);
		itemStack.setType(Material.FIREBALL);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_team_shop"));
		lore = new ArrayList<String>();
		lore.add("");
		lore.add(Config.getLanguage("item.edit_game.lore.set"));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(21, itemStack);
		itemStack.setType(Material.BLAZE_POWDER);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_spawner"));
		lore = new ArrayList<String>();
		lore.add("");
		lore.add(Config.getLanguage("item.edit_game.lore.browse"));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(22, itemStack);
		itemStack.setType(Material.BLAZE_ROD);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_region_loc1"));
		lore = new ArrayList<String>();
		lore.add("");
		if (game.getLoc1() == null) {
			lore.add(Config.getLanguage("item.edit_game.lore.set"));
		} else {
			lore.add(Config.getLanguage("item.edit_game.lore.complete"));
		}
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(23, itemStack);
		itemStack.setType(Material.STICK);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.set_region_loc2"));
		lore = new ArrayList<String>();
		lore.add("");
		if (game.getLoc2() == null) {
			lore.add(Config.getLanguage("item.edit_game.lore.set"));
		} else {
			lore.add(Config.getLanguage("item.edit_game.lore.complete"));
		}
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(24, itemStack);
		itemStack.setType(Material.WOOL);
		itemStack.setDurability((short) 3);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.save_game"));
		itemMeta.setLore(new ArrayList<String>());
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(39, itemStack);
		itemStack.setDurability((short) 5);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.start_game"));
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(40, itemStack);
		itemStack.setDurability((short) 14);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.stop_game"));
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(41, itemStack);
		player.closeInventory();
		player.openInventory(inventory);
		Main.getInstance().getEditHolographicManager().displayGameLocation(player, game.getName());
	}

	private static void giveItems(Player player, Game game) {
		ItemStack itemStack = new ItemStack(Material.BOOK);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.menu_item"));
		List<String> lore = new ArrayList<String>();
		String line = "bwsba-editgame-menu-" + game.getName();
		line = "¡ì" + line.replaceAll("(.{1})", "$1¡ì");
		lore.add(line.substring(0, line.length() - 1));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().setItem(0, itemStack);
		itemStack.setType(Material.SKULL_ITEM);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.align_angle_item"));
		lore.clear();
		line = "bwsba-editgame-align";
		line = "¡ì" + line.replaceAll("(.{1})", "$1¡ì");
		lore.add(line.substring(0, line.length() - 1));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().setItem(1, itemStack);
		itemStack.setType(Material.ARMOR_STAND);
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.align_location_item"));
		lore.clear();
		line = "bwsba-editgame-align2";
		line = "¡ì" + line.replaceAll("(.{1})", "$1¡ì");
		lore.add(line.substring(0, line.length() - 1));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		player.getInventory().setItem(2, itemStack);
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
		if (plugin != null && plugin.isEnabled()) {
			itemStack.setType(Material.getMaterial(
					((WorldEditPlugin) plugin).getWorldEdit().getPlatformManager().getConfiguration().navigationWand));
			itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.teleport_tool"));
			lore.clear();
			line = "bwsba-editgame-teleport";
			line = "¡ì" + line.replaceAll("(.{1})", "$1¡ì");
			lore.add(line.substring(0, line.length() - 1));
			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);
			player.getInventory().setItem(3, itemStack);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e) {
		if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		ItemStack itemStack = e.getItem();
		if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
			return;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (!itemMeta.hasLore()) {
			return;
		}
		Player player = e.getPlayer();
		String l = itemMeta.getLore().get(0).replace("¡ì", "");
		if (l.startsWith("bwsba-editgame-menu-")) {
			e.setCancelled(true);
			if (player.hasPermission("bedwarsscoreboardaddon.edit")) {
				Game game = BedwarsRel.getInstance().getGameManager().getGame(l.substring(20, l.length()));
				if (game != null) {
					openMenu(player, game);
				}
			}
		} else if (l.equals("bwsba-editgame-align")) {
			e.setCancelled(true);
			if (player.hasPermission("bedwarsscoreboardaddon.edit")) {
				alignAngle(player);
			}
		} else if (l.equals("bwsba-editgame-align2")) {
			e.setCancelled(true);
			if (player.hasPermission("bedwarsscoreboardaddon.edit")) {
				alignLocation(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		Player player = e.getPlayer();
		ItemStack itemStack;
		if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
			itemStack = player.getItemInHand();
		} else {
			itemStack = player.getInventory().getItemInMainHand();
			if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
				itemStack = player.getInventory().getItemInOffHand();
			}
		}
		if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
			return;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (!itemMeta.hasLore()) {
			return;
		}
		String l = itemMeta.getLore().get(0).replace("¡ì", "");
		if (l.startsWith("bwsba-editgame-menu-")) {
			e.setCancelled(true);
			if (player.hasPermission("bedwarsscoreboardaddon.edit")) {
				Game game = BedwarsRel.getInstance().getGameManager().getGame(l.substring(20, l.length()));
				if (game != null) {
					openMenu(player, game);
				}
			}
		} else if (l.equals("bwsba-editgame-align")) {
			e.setCancelled(true);
			if (player.hasPermission("bedwarsscoreboardaddon.edit")) {
				alignAngle(player);
			}
		} else if (l.equals("bwsba-editgame-align2")) {
			e.setCancelled(true);
			if (player.hasPermission("bedwarsscoreboardaddon.edit")) {
				alignLocation(player);
			}
		}
	}

	private void alignLocation(Player player) {
		Location location = player.getLocation().clone();
		{
			BigDecimal bd = new BigDecimal(location.getX() + "");
			BigDecimal[] result = bd.divideAndRemainder(BigDecimal.valueOf(1));
			double xd = Double.valueOf(result[1].toString());
			List<Double> list = Arrays.asList(-1.0, -0.5, 0.0, 0.5, 1.0);
			double a = Math.abs(list.get(0) - xd);
			double nxd = list.get(0);
			for (Double i : list) {
				double j = Math.abs(i - xd);
				if (j < a) {
					a = j;
					nxd = i;
				}
			}
			location.setX(((int) location.getX()) + nxd);
		}
		{
			BigDecimal bd = new BigDecimal(location.getZ() + "");
			BigDecimal[] result = bd.divideAndRemainder(BigDecimal.valueOf(1));
			double zd = Double.valueOf(result[1].toString());
			List<Double> list = Arrays.asList(-1.0, -0.5, 0.0, 0.5, 1.0);
			double a = Math.abs(list.get(0) - zd);
			double nzd = list.get(0);
			for (Double i : list) {
				double j = Math.abs(i - zd);
				if (j < a) {
					a = j;
					nzd = i;
				}
			}
			location.setZ(((int) location.getZ()) + nzd);
		}
		player.teleport(location);
	}

	private void alignAngle(Player player) {
		Location location = player.getLocation().clone();
		{
			List<Double> list = new ArrayList<Double>();
			for (double i = -360; i <= 360; i += 22.5) {
				list.add(i);
			}
			double yaw = (double) player.getLocation().getYaw();
			double a = Math.abs(list.get(0) - yaw);
			double nyaw = list.get(0);
			for (Double i : list) {
				double j = Math.abs(i - yaw);
				if (j < a) {
					a = j;
					nyaw = i;
				}
			}
			location.setYaw((float) nyaw);
		}
		{
			List<Double> list = new ArrayList<Double>();
			for (double i = -90; i <= 90; i += 22.5) {
				list.add(i);
			}
			double pitch = (double) player.getLocation().getPitch();
			double a = Math.abs(list.get(0) - pitch);
			double npitch = list.get(0);
			for (Double i : list) {
				double j = Math.abs(i - pitch);
				if (j < a) {
					a = j;
					npitch = i;
				}
			}
			location.setPitch((float) npitch);
		}
		player.teleport(location);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory inventory = e.getInventory();
		int slot = e.getRawSlot();
		String title = inventory.getTitle();
		String tit = Config.getLanguage("inventory.edit_game");
		if (title.startsWith("¡ìe¡ìd¡ìi¡ìt¡ì8" + tit + " - ")) {
			e.setCancelled(true);
			String game_name = title.substring(13 + tit.length(), title.length());
			Game game = BedwarsRel.getInstance().getGameManager().getGame(game_name);
			if (game != null) {
				switch (slot) {
				case 11:
					player.closeInventory();
					Bukkit.dispatchCommand(player, "bedwarsrel:bw setlobby " + game_name);
					Main.getInstance().getEditHolographicManager().displayGameLocation(player, game.getName());
					break;
				case 12:
					openAnvilInventory(player, Config.getLanguage("anvil.edit_game.set_mix_players"),
							"bedwarsrel:bw setminplayers " + game_name + " {value}");
					break;
				case 13:
					openEditTeamsMenu(player, game);
					break;
				case 14:
					openEditTeamsLocation(player, game, true);
					break;
				case 15:
					openEditTeamsLocation(player, game, false);
					break;
				case 20:
					player.closeInventory();
					Bukkit.dispatchCommand(player, "bedwarsscoreboardaddon:bwsba shop set item " + game_name);
					Main.getInstance().getEditHolographicManager().displayGameLocation(player, game.getName());
					break;
				case 21:
					player.closeInventory();
					Bukkit.dispatchCommand(player, "bedwarsscoreboardaddon:bwsba shop set team " + game_name);
					Main.getInstance().getEditHolographicManager().displayGameLocation(player, game.getName());
					break;
				case 22:
					openEditSpawner(player, game);
					break;
				case 23:
					player.closeInventory();
					Bukkit.dispatchCommand(player, "bedwarsrel:bw setregion " + game_name + " loc1");
					Main.getInstance().getEditHolographicManager().displayGameLocation(player, game.getName());
					break;
				case 24:
					player.closeInventory();
					Bukkit.dispatchCommand(player, "bedwarsrel:bw setregion " + game_name + " loc2");
					Main.getInstance().getEditHolographicManager().displayGameLocation(player, game.getName());
					break;
				case 39:
					player.closeInventory();
					Bukkit.dispatchCommand(player, "bedwarsrel:bw save " + game_name);
					break;
				case 40:
					player.closeInventory();
					Bukkit.dispatchCommand(player, "bedwarsrel:bw start " + game_name);
					break;
				case 41:
					player.closeInventory();
					Bukkit.dispatchCommand(player, "bedwarsrel:bw stop " + game_name);
					break;
				default:
					break;
				}
			}
		} else if (title.startsWith("¡ìe¡ìd¡ìi¡ìt¡ìt¡ìe¡ìa¡ìm¡ì8" + tit + " - ")) {
			e.setCancelled(true);
			String game_name = title.substring(21 + tit.length(), title.length());
			Game game = BedwarsRel.getInstance().getGameManager().getGame(game_name);
			if (game != null) {
				if (slot == 49) {
					openMenu(player, game);
					return;
				}
				ItemStack itemStack = e.getCurrentItem();
				if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
					ItemMeta itemMeta = itemStack.getItemMeta();
					if (itemMeta.getLore().get(0).equals("¡ì0")) {
						openAnvilInventory(player, Config.getLanguage("anvil.edit_game.set_team_name"),
								"bedwarsrel:bw addteam " + game_name + " {value} "
										+ ColorUtil.remcolor(itemMeta.getDisplayName()));
					} else if (itemMeta.getLore().get(0).equals("¡ì1")) {
						player.closeInventory();
						Bukkit.dispatchCommand(player, "bedwarsrel:bw removeteam " + game_name + " "
								+ ColorUtil.remcolor(itemMeta.getDisplayName()));
						Main.getInstance().getEditHolographicManager().displayGameLocation(player, game.getName());
					}
				}
			}
		} else if (title.startsWith("¡ìe¡ìd¡ìi¡ìt¡ìt¡ìe¡ìa¡ìm¡ìl¡ì8" + tit + " - ")) {
			e.setCancelled(true);
			String game_name = title.substring(23 + tit.length(), title.length());
			Game game = BedwarsRel.getInstance().getGameManager().getGame(game_name);
			if (game != null) {
				if (slot == 49) {
					openMenu(player, game);
					return;
				}
				ItemStack itemStack = e.getCurrentItem();
				if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
					ItemMeta itemMeta = itemStack.getItemMeta();
					if (itemMeta.getLore().get(0).equals("¡ì0")) {
						player.closeInventory();
						Bukkit.dispatchCommand(player, "bedwarsrel:bw setbed " + game_name + " "
								+ ColorUtil.remcolor(itemMeta.getDisplayName()));
						Main.getInstance().getEditHolographicManager().displayGameLocation(player, game.getName());
					} else if (itemMeta.getLore().get(0).equals("¡ì1")) {
						player.closeInventory();
						Bukkit.dispatchCommand(player, "bedwarsrel:bw setspawn " + game_name + " "
								+ ColorUtil.remcolor(itemMeta.getDisplayName()));
						Main.getInstance().getEditHolographicManager().displayGameLocation(player, game.getName());
					}
				}
			}
		} else if (title.startsWith("¡ìe¡ìd¡ìi¡ìt¡ìs¡ì8" + tit + " - ")) {
			e.setCancelled(true);
			String game_name = title.substring(15 + tit.length(), title.length());
			Game game = BedwarsRel.getInstance().getGameManager().getGame(game_name);
			if (game != null) {
				if (slot == 49) {
					openMenu(player, game);
					return;
				}
				ItemStack itemStack = e.getCurrentItem();
				if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
					ItemMeta itemMeta = itemStack.getItemMeta();
					player.closeInventory();
					Bukkit.dispatchCommand(player,
							"bedwarsrel:bw setspawner " + game_name + " " + itemMeta.getLore().get(0).replace("¡ì", ""));
					Main.getInstance().getEditHolographicManager().displayGameLocation(player, game.getName());
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				removeEditItem(e.getPlayer());
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}

	@EventHandler
	public void onLeave(BedwarsPlayerLeaveEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				removeEditItem(e.getPlayer());
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}

	@EventHandler
	public void onDisable(PluginDisableEvent e) {
		if (e.getPlugin().equals(Main.getInstance()) || e.getPlugin().equals(BedwarsRel.getInstance())) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				removeEditItem(player);
			}
		}
	}

	private static void removeEditItem(Player player) {
		if (player.isOnline()) {
			ItemStack[] itemStacks = player.getInventory().getContents();
			for (int i = 0; i < itemStacks.length; i++) {
				ItemStack itemStack = itemStacks[i];
				if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
					ItemMeta itemMeta = itemStack.getItemMeta();
					if (itemMeta.hasLore()) {
						String l = itemMeta.getLore().get(0).replace("¡ì", "");
						if (l.startsWith("bwsba-editgame-")) {
							try {
								player.getInventory().setItem(i, new ItemStack(Material.AIR));
							} catch (Exception e) {
							}
						}
					}
				}
			}
		}
	}

	private static void openEditTeamsMenu(Player player, Game game) {
		Inventory inventory = Bukkit.createInventory(null, 54,
				"¡ìe¡ìd¡ìi¡ìt¡ìt¡ìe¡ìa¡ìm¡ì8" + Config.getLanguage("inventory.edit_game") + " - " + game.getName());
		List<TeamColor> colors = new ArrayList<TeamColor>();
		colors.addAll(Arrays.asList(TeamColor.values()));
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (Team team : game.getTeams().values()) {
			TeamColor teamColor = team.getColor();
			ItemStack itemStack = new ItemStack(Material.WOOL);
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemStack.setDurability(teamColor.getDyeColor().getWoolData());
			itemMeta.setDisplayName(teamColor.getChatColor() + team.getName());
			List<String> lore = new ArrayList<String>();
			lore.add("¡ì1");
			lore.add(Config.getLanguage("item.edit_game.lore.max_players").replace("{players}",
					team.getMaxPlayers() + ""));
			lore.add("");
			lore.add(Config.getLanguage("item.edit_game.lore.remove"));
			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);
			items.add(itemStack);
			colors.remove(teamColor);
		}
		for (TeamColor teamColor : colors) {
			ItemStack itemStack = new ItemStack(Material.WOOL);
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemStack.setDurability(teamColor.getDyeColor().getWoolData());
			itemMeta.setDisplayName(teamColor.getChatColor() + teamColor.name());
			List<String> lore = new ArrayList<String>();
			lore.add("¡ì0");
			lore.add(Config.getLanguage("item.edit_game.lore.add"));
			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);
			items.add(itemStack);
		}
		int i = 10;
		for (ItemStack item : items) {
			if (i == 17) {
				i = 19;
			} else if (i == 26) {
				i = 28;
			}
			inventory.setItem(i, item);
			i++;
		}
		ItemStack itemStack = new ItemStack(Material.ARROW);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.back"));
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(49, itemStack);
		player.closeInventory();
		player.openInventory(inventory);
	}

	private static void openEditTeamsLocation(Player player, Game game, boolean isbed) {
		Inventory inventory = Bukkit.createInventory(null, 54,
				"¡ìe¡ìd¡ìi¡ìt¡ìt¡ìe¡ìa¡ìm¡ìl¡ì8" + Config.getLanguage("inventory.edit_game") + " - " + game.getName());
		int i = 10;
		for (Team team : game.getTeams().values()) {
			if (i == 17) {
				i = 19;
			} else if (i == 26) {
				i = 28;
			}
			TeamColor teamColor = team.getColor();
			ItemStack itemStack = new ItemStack(Material.WOOL);
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemStack.setDurability(teamColor.getDyeColor().getWoolData());
			itemMeta.setDisplayName(teamColor.getChatColor() + team.getName());
			List<String> lore = new ArrayList<String>();
			lore.add(isbed ? "¡ì0" : "¡ì1");
			if (isbed) {
				if (team.getTargetHeadBlock() == null && team.getTargetFeetBlock() == null) {
					lore.add(Config.getLanguage("item.edit_game.lore.set"));
				} else {
					lore.add(Config.getLanguage("item.edit_game.lore.complete"));
				}
			} else {
				if (team.getSpawnLocation() == null) {
					lore.add(Config.getLanguage("item.edit_game.lore.set"));
				} else {
					lore.add(Config.getLanguage("item.edit_game.lore.complete"));
				}
			}
			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);
			inventory.setItem(i, itemStack);
			i++;
		}
		ItemStack itemStack = new ItemStack(Material.ARROW);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.back"));
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(49, itemStack);
		player.closeInventory();
		player.openInventory(inventory);
	}

	private static void openEditSpawner(Player player, Game game) {
		String title = Config.getLanguage("inventory.edit_game");
		Inventory inventory = Bukkit.createInventory(null, 54, "¡ìe¡ìd¡ìi¡ìt¡ìs¡ì8" + title + " - " + game.getName());
		FileConfiguration config = BedwarsRel.getInstance().getConfig();
		int i = 10;
		for (String key : BedwarsRel.getInstance().getConfig().getConfigurationSection("resource").getKeys(false)) {
			try {
				ItemStack itemStack = ItemStack
						.deserialize((Map<String, Object>) config.getList("resource." + key + ".item").get(0));
				ItemMeta itemMeta = itemStack.getItemMeta();
				List<String> lore = new ArrayList<String>();
				String line = key;
				line = "¡ì" + line.replaceAll("(.{1})", "$1¡ì");
				lore.add(line.substring(0, line.length() - 1));
				if (itemMeta.hasLore()) {
					lore.addAll(itemMeta.getLore());
					lore.add("");
				}
				lore.add(Config.getLanguage("item.edit_game.lore.set"));
				itemMeta.setLore(lore);
				itemStack.setItemMeta(itemMeta);
				if (i == 17) {
					i = 19;
				} else if (i == 26) {
					i = 28;
				} else if (i == 35) {
					i = 37;
				} else if (i == 45) {
					break;
				}
				inventory.setItem(i, itemStack);
				i++;
			} catch (Exception e) {
			}
		}
		ItemStack itemStack = new ItemStack(Material.ARROW);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.getLanguage("item.edit_game.name.back"));
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(49, itemStack);
		player.closeInventory();
		player.openInventory(inventory);
	}

	private void onPacketSending() {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		PacketListener packetListener = new PacketAdapter(Main.getInstance(), ListenerPriority.HIGHEST,
				new PacketType[] { PacketType.Play.Client.WINDOW_CLICK }) {
			@Override
			public void onPacketReceiving(PacketEvent e) {
				PacketContainer packet = e.getPacket();
				Player player = e.getPlayer();
				if (e.getPacketType().equals(PacketType.Play.Client.WINDOW_CLICK)) {
					if (packet.getIntegers().read(0) == 0) {
						ItemStack itemStack = packet.getItemModifier().read(0);
						if (packet.getIntegers().read(1) == 2) {
							if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
								ItemMeta itemMeta = itemStack.getItemMeta();
								player.closeInventory();
								if (itemMeta.getDisplayName() != null) {
									String command = itemMeta.getLore().get(0).replace("¡ì", "").replace("{value}",
											itemMeta.getDisplayName());
									String[] args = command.split(" ");
									if (args.length == 5 && args[1].equalsIgnoreCase("addteam")) {
										openAnvilInventory(player,
												Config.getLanguage("anvil.edit_game.set_team_max_players"),
												command + " {value}");
									} else {
										Bukkit.dispatchCommand(player, command);
										Game game = BedwarsRel.getInstance().getGameManager().getGame(args[2]);
										if (game != null) {
											Main.getInstance().getEditHolographicManager().displayGameLocation(player,
													game.getName());
										}
									}
								}
							}
						}
						if (packet.getIntegers().read(1) == 0 && itemStack != null
								&& !itemStack.getType().equals(Material.AIR)) {
							ItemMeta itemMeta = itemStack.getItemMeta();
							new BukkitRunnable() {
								@Override
								public void run() {
									setAnvilItem(player, itemMeta.getDisplayName(), itemMeta.getLore().get(0));
								}
							}.runTaskLater(Main.getInstance(), 1L);
						}
					}
				}
			}
		};
		pm.addPacketListener(packetListener);
	}

	private static void openAnvilInventory(Player player, String game, String str) {
		player.closeInventory();
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		try {
			PacketContainer packet = pm.createPacket(PacketType.Play.Server.OPEN_WINDOW);
			packet.getIntegers().write(0, 0);
			packet.getIntegers().write(1, 0);
			packet.getIntegers().write(2, 0);
			packet.getStrings().write(0, "minecraft:anvil");
			pm.sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				setAnvilItem(player, game, str);
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}

	private static void setAnvilItem(Player player, String game, String str) {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		try {
			PacketContainer pack = pm.createPacket(PacketType.Play.Server.SET_SLOT);
			pack.getIntegers().write(0, 0);
			pack.getIntegers().write(1, 0);
			ItemStack itemStack = new ItemStack(Material.NAME_TAG);
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(game);
			String lore = str;
			lore = "¡ì" + lore.replaceAll("(.{1})", "$1¡ì");
			itemMeta.setLore(Arrays.asList(lore.substring(0, lore.length() - 1)));
			itemStack.setItemMeta(itemMeta);
			pack.getItemModifier().write(0, itemStack);
			pm.sendServerPacket(player, pack);
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}
}
