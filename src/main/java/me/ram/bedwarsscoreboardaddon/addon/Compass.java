package me.ram.bedwarsscoreboardaddon.addon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;

public class Compass implements Listener {

	private ItemStack compassItem;

	public Compass() {
		onBlockPlace();
	}

	@EventHandler
	public void onStarted(BedwarsGameStartedEvent e) {
		compassItem = getItemItem();
		if (Config.compass_enabled) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Player player : e.getGame().getPlayers()) {
						if (!e.getGame().isSpectator(player)) {
							giveCompass(player);
						}
					}
				}
			}.runTaskLater(Main.getInstance(), 5L);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (!Config.compass_enabled || game == null) {
			return;
		}
		if (!game.isSpectator(player)) {
			giveCompass(player);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (!Config.compass_enabled || game == null) {
			return;
		}
		if (!game.getPlayers().contains(player)) {
			return;
		}
		if (game.getState() != GameState.RUNNING) {
			return;
		}
		if (game.isSpectator(player)) {
			return;
		}
		if (e.getItem() != null && e.getItem().isSimilar(compassItem)
				&& (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			e.setCancelled(true);
			openMainMenu(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onOpen(InventoryOpenEvent e) {
		if (e.getInventory().getTitle().equals(Config.compass_gui_title)) {
			e.setCancelled(false);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!Config.compass_enabled) {
			return;
		}
		Player player = (Player) e.getWhoClicked();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		if (!game.getPlayers().contains(player)) {
			return;
		}
		if (game.getState() != GameState.RUNNING) {
			return;
		}
		if (game.isSpectator(player)) {
			return;
		}
		Inventory inventory = e.getInventory();
		if (inventory.getTitle().equals(Config.compass_gui_title)) {
			e.setCancelled(true);
			Team team = game.getPlayerTeam(player);
			int slot = e.getRawSlot();
			if (slot == 11) {
				sendMessage(player, team, Config.compass_message_III_II.replace("{player}", player.getName()));
			} else if (slot == 12) {
				sendMessage(player, team, Config.compass_message_IV_II.replace("{player}", player.getName()));
			} else if (slot == 13) {
				sendMessage(player, team, Config.compass_message_V_II.replace("{player}", player.getName()));
			} else if (slot == 14) {
				openSelectTeamMenu(player, "¡ìa¡ìi");
			} else if (slot == 15) {
				openSelectResourcesMenu(player, "¡ìc");
			} else if (slot == 16) {
				openSelectResourcesMenu(player, "¡ìh");
			} else if (slot == 20) {
				sendMessage(player, team, Config.compass_message_III_III.replace("{player}", player.getName()));
			} else if (slot == 21) {
				sendMessage(player, team, Config.compass_message_IV_III.replace("{player}", player.getName()));
			} else if (slot == 22) {
				sendMessage(player, team, Config.compass_message_V_III.replace("{player}", player.getName()));
			} else if (slot == 23) {
				openSelectTeamMenu(player, "¡ìa");
			} else if (slot == 24) {
				openSelectResourcesMenu(player, "¡ìn");
			}
		} else if (inventory.getTitle().equals(Config.compass_gui_title + "¡ìa¡ìi")) {
			e.setCancelled(true);
			int slot = e.getRawSlot();
			if (slot == 31) {
				openMainMenu(player);
			}
			if (e.getCurrentItem() != null) {
				ItemStack itemStack = e.getCurrentItem();
				if (itemStack.getType().equals(Material.WOOL)) {
					String teamname = ColorUtil.remcolor(itemStack.getItemMeta().getDisplayName());
					if (game.getTeam(teamname) != null) {
						Team team = game.getTeam(teamname);
						sendMessage(player, game.getPlayerTeam(player),
								Config.compass_message_VI_II.replace("{player}", player.getName())
										.replace("{color}", team.getChatColor() + "")
										.replace("{team}", team.getName()));
					}
				}
			}
		} else if (inventory.getTitle().equals(Config.compass_gui_title + "¡ìa")) {
			e.setCancelled(true);
			int slot = e.getRawSlot();
			if (slot == 31) {
				openMainMenu(player);
			}
			if (e.getCurrentItem() != null) {
				ItemStack itemStack = e.getCurrentItem();
				if (itemStack.getType().equals(Material.WOOL)) {
					String teamname = ColorUtil.remcolor(itemStack.getItemMeta().getDisplayName());
					if (game.getTeam(teamname) != null) {
						Team team = game.getTeam(teamname);
						sendMessage(player, game.getPlayerTeam(player),
								Config.compass_message_VI_III.replace("{player}", player.getName())
										.replace("{color}", team.getChatColor() + "")
										.replace("{team}", team.getName()));
					}
				}
			}
		} else if (inventory.getTitle().equals(Config.compass_gui_title + "¡ìc")) {
			e.setCancelled(true);
			int slot = e.getRawSlot();
			if (slot == 31) {
				openMainMenu(player);
			}
			if (e.getCurrentItem() != null && Config.compass_resources.contains(e.getCurrentItem().getType().name())) {
				sendMessage(player, game.getPlayerTeam(player),
						Config.compass_message_VII_II.replace("{player}", player.getName()).replace("{resource}",
								e.getCurrentItem().getItemMeta().getDisplayName()));
			}
		} else if (inventory.getTitle().equals(Config.compass_gui_title + "¡ìn")) {
			e.setCancelled(true);
			int slot = e.getRawSlot();
			if (slot == 31) {
				openMainMenu(player);
			}
			if (e.getCurrentItem() != null && Config.compass_resources.contains(e.getCurrentItem().getType().name())) {
				sendMessage(player, game.getPlayerTeam(player),
						Config.compass_message_VII_III.replace("{player}", player.getName()).replace("{resource}",
								e.getCurrentItem().getItemMeta().getDisplayName()));
			}
		} else if (inventory.getTitle().equals(Config.compass_gui_title + "¡ìh")) {
			e.setCancelled(true);
			int slot = e.getRawSlot();
			if (slot == 31) {
				openMainMenu(player);
			}
			if (e.getCurrentItem() != null && Config.compass_resources.contains(e.getCurrentItem().getType().name())) {
				sendMessage(player, game.getPlayerTeam(player),
						Config.compass_message_VIII_II.replace("{player}", player.getName()).replace("{resource}",
								e.getCurrentItem().getItemMeta().getDisplayName()));
			}
		}
	}

	private void onBlockPlace() {
		PacketListener packetListener = new PacketAdapter(Main.getInstance(), ListenerPriority.HIGHEST,
				new PacketType[] { PacketType.Play.Client.BLOCK_PLACE }) {
			public void onPacketReceiving(PacketEvent e) {
				Player player = e.getPlayer();
				Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
				if (!Config.compass_enabled || game == null) {
					return;
				}
				if (!game.getPlayers().contains(player)) {
					return;
				}
				if (game.getState() != GameState.RUNNING) {
					return;
				}
				if (game.isSpectator(player)) {
					return;
				}
				if (player.getInventory().getItemInHand() != null
						&& player.getInventory().getItemInHand().isSimilar(compassItem)) {
					e.setCancelled(true);
					if (!player.getOpenInventory().getTitle().equals(Config.compass_gui_title)) {
						openMainMenu(player);
					}
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
	}

	private void sendMessage(Player player, Team team, String message) {
		player.closeInventory();
		for (Player p : team.getPlayers()) {
			p.sendMessage(message);
		}
	}

	private void openMainMenu(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 36, Config.compass_gui_title);
		ItemStack itemStack = new ItemStack(Material.BOOK);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_III_II);
		itemMeta.setLore(Config.compass_lore_send_message);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(11, itemStack);
		itemStack = new ItemStack(Material.BOOK);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_IV_II);
		itemMeta.setLore(Config.compass_lore_send_message);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(12, itemStack);
		itemStack = new ItemStack(Material.BOOK);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_III_III);
		itemMeta.setLore(Config.compass_lore_send_message);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(20, itemStack);
		itemStack = new ItemStack(Material.BOOK);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_IV_III);
		itemMeta.setLore(Config.compass_lore_send_message);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(21, itemStack);
		itemStack = new ItemStack(Material.IRON_FENCE);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_V_II);
		itemMeta.setLore(Config.compass_lore_send_message);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(13, itemStack);
		itemStack = new ItemStack(Material.IRON_FENCE);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_V_III);
		itemMeta.setLore(Config.compass_lore_send_message);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(22, itemStack);
		itemStack = new ItemStack(Material.IRON_SWORD);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_VI_II);
		itemMeta.setLore(Config.compass_lore_select_team);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(14, itemStack);
		itemStack = new ItemStack(Material.IRON_SWORD);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_VI_III);
		itemMeta.setLore(Config.compass_lore_select_team);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(23, itemStack);
		itemStack = new ItemStack(Material.DIAMOND);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_VII_II);
		itemMeta.setLore(Config.compass_lore_select_resources);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(15, itemStack);
		itemStack = new ItemStack(Material.DIAMOND);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_VII_III);
		itemMeta.setLore(Config.compass_lore_select_resources);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(24, itemStack);
		itemStack = new ItemStack(Material.CHEST);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_VIII_II);
		itemMeta.setLore(Config.compass_lore_select_resources);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(16, itemStack);
		player.openInventory(inventory);
	}

	private void openSelectTeamMenu(Player player, String gui) {
		Inventory inventory = Bukkit.createInventory(null, 36, Config.compass_gui_title + gui);
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		int i = 10;
		for (Team team : game.getTeams().values()) {
			if (!team.equals(game.getPlayerTeam(player))) {
				if (i == 17) {
					i = 19;
				}
				Wool wool = new Wool(team.getColor().getDyeColor());
				ItemStack itemStack = wool.toItemStack(1);
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName(team.getChatColor() + team.getName());
				itemMeta.setLore(Config.compass_lore_send_message);
				itemStack.setItemMeta(itemMeta);
				inventory.setItem(i, itemStack);
				i++;
			}
		}
		ItemStack itemStack = new ItemStack(Material.ARROW);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_back);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(31, itemStack);
		player.openInventory(inventory);
	}

	private void openSelectResourcesMenu(Player player, String gui) {
		Inventory inventory = Bukkit.createInventory(null, 36, Config.compass_gui_title + gui);
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		int i = 10;
		for (String type : Config.compass_resources) {
			if (i == 17) {
				i = 19;
			}
			ItemStack itemStack = new ItemStack(Material.valueOf(type));
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(Config.compass_resources_name.get(type));
			itemMeta.setLore(Config.compass_lore_send_message);
			itemStack.setItemMeta(itemMeta);
			inventory.setItem(i, itemStack);
			i++;
		}
		ItemStack itemStack = new ItemStack(Material.ARROW);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_back);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(31, itemStack);
		player.openInventory(inventory);
	}

	private static ItemStack getItemItem() {
		ItemStack itemStack = new ItemStack(Material.COMPASS);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.compass_item_name);
		itemMeta.setLore(Config.compass_item_lore);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public static void giveCompass(Player player) {
		player.getInventory().setItem(8, getItemItem());
	}
}
