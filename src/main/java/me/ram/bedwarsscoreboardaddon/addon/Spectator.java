package me.ram.bedwarsscoreboardaddon.addon;

import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.events.BedwarsPlayerJoinedEvent;
import io.github.bedwarsrel.events.BedwarsPlayerLeaveEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.LocationUtil;
import me.ram.bedwarsscoreboardaddon.utils.ServerJoinerUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class Spectator implements Listener {

	private List<Player> players = new ArrayList<Player>();
	private ItemStack speeditem;
	private ItemStack joinitem;
	private List<Material> resitems = new ArrayList<Material>();

	public Spectator() {
		onPacketReceiving();
	}

	private void onPacketReceiving() {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		pm.addPacketListener(
				new PacketAdapter(Main.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.USE_ENTITY) {
					public void onPacketReceiving(PacketEvent e) {
						Player player = e.getPlayer();
						PacketContainer packet = e.getPacket();
						if (packet.getEntityUseActions().read(0).equals(EntityUseAction.INTERACT_AT)) {
							return;
						}
						Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
						if (game == null || !game.getState().equals(GameState.RUNNING) || game.isSpectator(player)) {
							return;
						}

						int id = packet.getIntegers().read(0);
						Player target = null;
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (p.getEntityId() == id) {
								target = p;
								break;
							}
						}
						if (target == null) {
							return;
						}
						if (!game.getPlayers().contains(target) || !game.isSpectator(target)) {
							return;
						}
						target.teleport(target.getLocation().add(0, 5, 0));
					}
				});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		player.setFlySpeed((float) 0.1);
		player.removePotionEffect(PotionEffectType.SPEED);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}

	@EventHandler
	public void onLeave(BedwarsPlayerLeaveEvent e) {
		Player player = e.getPlayer();
		player.setFlySpeed((float) 0.1);
		player.removePotionEffect(PotionEffectType.SPEED);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPickupItem(PlayerPickupItemEvent e) {
		if (!Config.spectator_enabled) {
			return;
		}
		Player player = e.getPlayer();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null || !game.getState().equals(GameState.RUNNING) || !game.isSpectator(player)) {
			return;
		}
		e.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
			Player player = (Player) e.getEntity();
			Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
			if (game == null || !game.getState().equals(GameState.RUNNING) || !game.isSpectator(player)) {
				return;
			}
			Location location = player.getLocation();
			WorldBorder border = location.getWorld().getWorldBorder();
			Location center = border.getCenter();
			double x = location.getX() > center.getX() ? location.getX() - center.getX()
					: center.getX() - location.getX();
			double z = location.getZ() > center.getZ() ? location.getZ() - center.getZ()
					: center.getZ() - location.getZ();
			double radius = border.getSize() / 2.0;
			if (x > radius || z > radius) {
				return;
			}
			player.teleport(player.getLocation().add(0, 5, 0));
		}
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (!Config.spectator_enabled) {
			return;
		}
		Player player = e.getPlayer();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		if (arena != null && arena.isOver()) {
			return;
		}
		if (!(e.getRightClicked() instanceof Player)) {
			return;
		}
		Player target = (Player) e.getRightClicked();
		if (!(game.getPlayers().contains(player) && game.getPlayers().contains(target))) {
			return;
		}
		if (game.getState() != GameState.RUNNING) {
			return;
		}
		if (!game.isSpectator(player) || game.isSpectator(target)) {
			return;
		}
		player.setGameMode(GameMode.SPECTATOR);
		player.setSpectatorTarget(e.getRightClicked());
	}

	private List<Material> getResource() {
		List<Material> items = new ArrayList<Material>();
		ConfigurationSection config = BedwarsRel.getInstance().getConfig().getConfigurationSection("resource");
		for (String res : config.getKeys(false)) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) BedwarsRel.getInstance().getConfig()
					.getList("resource." + res + ".item");
			for (Map<String, Object> resource : list) {
				ItemStack itemStack = ItemStack.deserialize(resource);
				items.add(itemStack.getType());
			}
		}
		return items;
	}

	@EventHandler
	public void onStarted(BedwarsGameStartedEvent e) {
		for (Player player : e.getGame().getPlayers()) {
			player.setFlySpeed((float) 0.1);
			player.removePotionEffect(PotionEffectType.SPEED);
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
		resitems.clear();
		resitems.addAll(getResource());
		new BukkitRunnable() {
			@Override
			public void run() {
				if (e.getGame().getState() == GameState.RUNNING) {
					if (Config.spectator_enabled) {
						for (Player player : e.getGame().getPlayers()) {
							if (player.getSpectatorTarget() == null) {
								if (players.contains(player)) {
									players.remove(player);
									if (!Config.spectator_quit_spectator_title.equals("")
											|| !Config.spectator_quit_spectator_subtitle.equals("")) {
										Utils.sendTitle(player, 1, 30, 1, Config.spectator_quit_spectator_title,
												Config.spectator_quit_spectator_subtitle);
										player.setGameMode(GameMode.SURVIVAL);
										player.setAllowFlight(true);
										player.addPotionEffect(
												new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0),
												true);
									}
								}
							} else if (player.getSpectatorTarget() instanceof Player) {
								Player spectatorTarget = (Player) player.getSpectatorTarget();
								if (!players.contains(player)) {
									if (e.getGame().getPlayers().contains(spectatorTarget)) {
										if (e.getGame().getPlayers().contains(spectatorTarget)
												&& !e.getGame().isSpectator(spectatorTarget)
												&& e.getGame().isSpectator(player)) {
											players.add(player);
											if (!Config.spectator_spectator_target_title.equals("")
													|| !Config.spectator_spectator_target_subtitle.equals("")) {
												Utils.sendTitle(player, 1, 30, 1,
														Config.spectator_spectator_target_title
																.replace("{player}", spectatorTarget.getName())
																.replace("{color}",
																		e.getGame().getPlayerTeam(spectatorTarget)
																				.getChatColor() + "")
																.replace("{team}",
																		e.getGame().getPlayerTeam(spectatorTarget)
																				.getName()),
														Config.spectator_spectator_target_subtitle
																.replace("{player}", spectatorTarget.getName())
																.replace("{color}",
																		e.getGame().getPlayerTeam(spectatorTarget)
																				.getChatColor() + "")
																.replace("{team}", e.getGame()
																		.getPlayerTeam(spectatorTarget).getName()));
											}
										} else {
											player.setSpectatorTarget(null);
										}
									} else {
										player.setSpectatorTarget(null);
									}
								}
								if (!e.getGame().getPlayers().contains(spectatorTarget)) {
									player.setSpectatorTarget(null);
								}
							} else {
								player.setSpectatorTarget(null);
							}
						}
					}
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 0L);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (e.getGame().getState() == GameState.RUNNING) {
					if (Config.spectator_enabled && Config.spectator_speed_enabled) {
						ItemStack itemStack = new ItemStack(Material.getMaterial(Config.spectator_speed_item));
						ItemMeta itemMeta = itemStack.getItemMeta();
						itemMeta.setDisplayName(Config.spectator_speed_item_name);
						itemMeta.setLore(Config.spectator_speed_item_lore);
						itemStack.setItemMeta(itemMeta);
						speeditem = itemStack;
						Game game = e.getGame();
						for (Player player : game.getPlayers()) {
							if (game.isSpectator(player)
									&& player.getInventory().getItem(Config.spectator_speed_slot - 1) == null) {
								player.getInventory().setItem(Config.spectator_speed_slot - 1, itemStack);
							}
						}
					}
					if (Config.spectator_enabled && Config.spectator_fast_join_enabled) {
						ItemStack itemStack = new ItemStack(Material.getMaterial(Config.spectator_fast_join_item));
						ItemMeta itemMeta = itemStack.getItemMeta();
						itemMeta.setDisplayName(Config.spectator_fast_join_item_name);
						itemMeta.setLore(Config.spectator_fast_join_item_lore);
						itemStack.setItemMeta(itemMeta);
						joinitem = itemStack;
						Game game = e.getGame();
						for (Player player : game.getPlayers()) {
							if (game.isSpectator(player)
									&& player.getInventory().getItem(Config.spectator_fast_join_slot - 1) == null) {
								player.getInventory().setItem(Config.spectator_fast_join_slot - 1, itemStack);
							}
						}
					}
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 5L);
		Timer logTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (e.getGame().getState() == GameState.RUNNING) {
					if (Config.spectator_enabled) {
						Game game = e.getGame();
						for (Player player : game.getPlayers()) {
							if (game.isSpectator(player)) {
								if (player.getLocation().getY() < 0) {
									player.teleport(player.getLocation().add(0, 128, 0));
								}
								for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 2, 3.5,
										2)) {
									if (entity instanceof Arrow || entity instanceof Fireball
											|| entity instanceof WitherSkull || entity instanceof TNTPrimed) {
										if (player.getGameMode() != GameMode.SPECTATOR) {
											player.teleport(LocationUtil.getPosition(player.getLocation(),
													entity.getLocation()));
											player.setVelocity(LocationUtil
													.getPositionVector(player.getLocation(), entity.getLocation())
													.multiply(0.07));
										}
										break;
									}
								}
							}
						}
					}
				} else {
					this.cancel();
				}
			}
		};
		logTimer.scheduleAtFixedRate(task, 500, 10);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		Team team = game.getPlayerTeam(player);
		if (Config.spectator_enabled && game.getState() == GameState.RUNNING && team != null && team.isDead(game)) {
			if (Config.spectator_centre_enabled) {
				World world = game.getRegion().getWorld();
				int i = 0;
				double x = 0;
				double z = 0;
				for (Team t : game.getTeams().values()) {
					if (t.getSpawnLocation().getWorld().getName().equals(world.getName())) {
						x += t.getSpawnLocation().getX();
						z += t.getSpawnLocation().getZ();
						i++;
					}
				}
				Location location = new Location(world, (x / Double.valueOf(i)), Config.spectator_centre_height,
						(z / Double.valueOf(i)));
				new BukkitRunnable() {
					@Override
					public void run() {
						if (player.isOnline() && game.getState() == GameState.RUNNING && game.isSpectator(player)) {
							player.setVelocity(new Vector(0, 0, 0));
							player.teleport(location);
						}
					}
				}.runTaskLater(Main.getInstance(), 1L);
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					if (player.isOnline() && game.getState() == GameState.RUNNING && game.isSpectator(player)) {
						player.setGameMode(GameMode.SURVIVAL);
						player.setAllowFlight(true);
						player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0),
								true);
					}
				}
			}.runTaskLater(Main.getInstance(), 20L);
		}
	}

	@EventHandler
	public void onJoinGame(BedwarsPlayerJoinedEvent e) {
		Game game = e.getGame();
		Player player = e.getPlayer();
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		if (Config.spectator_enabled && game.getState() == GameState.RUNNING
				&& !arena.getRejoin().getPlayers().containsKey(player.getName())) {
			if (Config.spectator_centre_enabled) {
				World world = game.getRegion().getWorld();
				int i = 0;
				double x = 0;
				double z = 0;
				for (Team team : game.getTeams().values()) {
					if (team.getSpawnLocation().getWorld().getName().equals(world.getName())) {
						x += team.getSpawnLocation().getX();
						z += team.getSpawnLocation().getZ();
						i++;
					}
				}
				Location location = new Location(world, (x / Double.valueOf(i)), Config.spectator_centre_height,
						(z / Double.valueOf(i)));
				player.setVelocity(new Vector(0, 0, 0));
				player.teleport(location);
				new BukkitRunnable() {
					@Override
					public void run() {
						if (player.isOnline() && game.getPlayers().contains(player)
								&& game.getState() == GameState.RUNNING && game.isSpectator(player)) {
							player.setVelocity(new Vector(0, 0, 0));
							player.teleport(location);
						}
					}
				}.runTaskLater(Main.getInstance(), 20L);
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					player.setGameMode(GameMode.SURVIVAL);
					player.setAllowFlight(true);
					player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
				}
			}.runTaskLater(Main.getInstance(), 20L);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(Config.spectator_enabled && Config.spectator_speed_enabled)) {
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
		if (!game.isSpectator(player)) {
			return;
		}
		Inventory inventory = e.getInventory();
		if (inventory.getTitle().equals(Config.spectator_speed_gui_title)) {
			e.setCancelled(true);
			int slot = e.getRawSlot();
			if (slot == 11) {
				player.setFlySpeed((float) 0.1);
				player.removePotionEffect(PotionEffectType.SPEED);
			} else if (slot == 12) {
				player.setFlySpeed((float) 0.2);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
			} else if (slot == 13) {
				player.setFlySpeed((float) 0.3);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
			} else if (slot == 14) {
				player.setFlySpeed((float) 0.4);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
			} else if (slot == 15) {
				player.setFlySpeed((float) 0.5);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3), true);
			} else {
				return;
			}
			player.closeInventory();
		}
		if (e.getCurrentItem() != null) {
			if (e.getCurrentItem().isSimilar(speeditem)) {
				e.setCancelled(true);
				openInventory(player);
			}
			if (e.getCurrentItem().isSimilar(joinitem)) {
				e.setCancelled(true);
				if (Bukkit.getPluginManager().isPluginEnabled("ServerJoiner")) {
					ServerJoinerUtil.sendServer(player, Config.spectator_fast_join_group);
				}
			}
		}
	}

	public void openInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 27, Config.spectator_speed_gui_title);
		ItemStack itemStack = new ItemStack(Material.LEATHER_BOOTS);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.spectator_speed_no_speed);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(11, itemStack);
		itemStack = new ItemStack(Material.CHAINMAIL_BOOTS);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.spectator_speed_speed_1);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(12, itemStack);
		itemStack = new ItemStack(Material.IRON_BOOTS);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.spectator_speed_speed_2);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(13, itemStack);
		itemStack = new ItemStack(Material.GOLD_BOOTS);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.spectator_speed_speed_3);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(14, itemStack);
		itemStack = new ItemStack(Material.DIAMOND_BOOTS);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Config.spectator_speed_speed_4);
		itemStack.setItemMeta(itemMeta);
		inventory.setItem(15, itemStack);
		player.openInventory(inventory);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onOpen(InventoryOpenEvent e) {
		if (e.getInventory().getTitle().equals(Config.spectator_speed_gui_title)) {
			e.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e) {
		if (!(Config.spectator_enabled && Config.spectator_speed_enabled)) {
			return;
		}
		Player player = e.getPlayer();
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
		if (!game.isSpectator(player)) {
			return;
		}
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			e.setCancelled(true);
		}
		if (players.contains(player)
				&& (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
			game.openSpectatorCompass(player);
			e.setCancelled(true);
			return;
		}
		if (e.getItem() != null && e.getItem().isSimilar(speeditem)
				&& (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			openInventory(player);
			e.setCancelled(true);
		}
		if (e.getItem() != null && e.getItem().isSimilar(joinitem)
				&& (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			e.setCancelled(true);
			if (Bukkit.getPluginManager().isPluginEnabled("ServerJoiner")) {
				ServerJoinerUtil.sendServer(player, Config.spectator_fast_join_group);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteractSpectator(PlayerInteractEvent e) {
		if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.isCancelled()) {
			return;
		}
		if (!Config.spectator_enabled) {
			return;
		}
		Player player = e.getPlayer();
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
		ItemStack itemStack = e.getItem();
		if (itemStack == null) {
			return;
		}
		if (!itemStack.getType().isBlock()) {
			return;
		}
		Location location = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation().add(0.5, 0.5, 0.5);
		for (Entity entity : location.getWorld().getNearbyEntities(location, 0.51, 1.5, 0.51)) {
			if (entity instanceof Player) {
				Player p = (Player) entity;
				if (game.getPlayers().contains(p) && game.isSpectator(p)) {
					p.teleport(p.getLocation().clone().add(0, 2, 0));
				}
			}
		}
	}
}
