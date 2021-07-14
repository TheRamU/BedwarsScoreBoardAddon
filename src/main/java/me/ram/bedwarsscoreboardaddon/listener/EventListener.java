package me.ram.bedwarsscoreboardaddon.listener;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import com.google.common.collect.ImmutableMap;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import io.github.bedwarsrel.utils.ChatWriter;

import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.edit.EditGame;
import me.ram.bedwarsscoreboardaddon.events.BedwarsTeamDeadEvent;
import me.ram.bedwarsscoreboardaddon.menu.MenuManager;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.npc.NPC;

public class EventListener implements Listener {

	private Map<String, Map<Event, PacketListener>> deathevents = new HashMap<String, Map<Event, PacketListener>>();;

	public EventListener() {
		onPacketReceiving();
		onPacketSending();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		String message = e.getMessage();
		String[] args = message.split(" ");
		if (args[0].equalsIgnoreCase("/bwsbatp")) {
			e.setCancelled(true);
			if (args.length == 8 && player.hasPermission("bedwarsscoreboardaddon.teleport")) {
				String loc = message.substring(10 + args[1].length(), message.length());
				Location location = toLocation(loc);
				if (location != null) {
					player.teleport(location);
					Main.getInstance().getEditHolographicManager().displayGameLocation(player, args[1]);
				}
			}
			return;
		}
		if (args[0].equalsIgnoreCase("/bw") || args[0].equalsIgnoreCase("/bedwarsrel:bw")) {
			if (args.length > 3) {
				if (args[1].equalsIgnoreCase("addgame")) {
					try {
						Integer.valueOf(args[3]);
						new BukkitRunnable() {
							@Override
							public void run() {
								Game game = BedwarsRel.getInstance().getGameManager().getGame(args[2]);
								EditGame.editGame(player, game);
							}
						}.runTask(Main.getInstance());
					} catch (Exception ex) {
					}
				}
			}
			return;
		}
		if (!args[0].equalsIgnoreCase("/rejoin")) {
			return;
		}
		e.setCancelled(true);
		if (!Config.rejoin_enabled) {
			return;
		}
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game != null) {
			return;
		}
		for (Arena arena : Main.getInstance().getArenaManager().getArenas().values()) {
			if (arena.getRejoin().getPlayers().containsKey(player.getName())) {
				arena.getGame().playerJoins(player);
				return;
			}
		}
		player.sendMessage(Config.rejoin_message_error);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName())) {
			Main.getInstance().getArenaManager().getArenas().get(game.getName()).onDeath(player);
		}
		Team team = game.getPlayerTeam(player);
		if (team == null) {
			return;
		}
		int players = 0;
		for (Player p : team.getPlayers()) {
			if (!game.isSpectator(p)) {
				players++;
			}
		}
		if (game.getState() == GameState.RUNNING && team != null && players <= 1 && !game.isSpectator(player) && team.isDead(game)) {
			Bukkit.getPluginManager().callEvent(new BedwarsTeamDeadEvent(game, team));
			if (Main.getInstance().getArenaManager().getArenas().containsKey(game.getName())) {
				Main.getInstance().getArenaManager().getArenas().get(game.getName()).getRejoin().removeTeam(team.getName());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game != null && Main.getInstance().getArenaManager().getArenas().containsKey(game.getName())) {
			Main.getInstance().getArenaManager().getArenas().get(game.getName()).onRespawn(player);
		}
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			if (player.isOnline()) {
				Main.getInstance().getHolographicManager().getPlayerHolographic(player).forEach(holo -> {
					holo.display(player);
				});
			}
		}, 5L);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player player = e.getPlayer();
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			if (player.isOnline()) {
				Main.getInstance().getHolographicManager().getPlayerHolographic(player).forEach(holo -> {
					holo.display(player);
				});
			}
		}, 1L);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeathLowest(PlayerDeathEvent e) {
		if (!Config.final_killed_enabled) {
			return;
		}
		Player player = e.getEntity();
		Player killer = player.getKiller();
		if (killer == null) {
			return;
		}
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null || game.getState() != GameState.RUNNING || game.isSpectator(player) || !game.getPlayers().contains(killer) || game.isSpectator(killer) || !game.getPlayerTeam(player).isDead(game)) {
			return;
		}
		Map<Event, PacketListener> map = deathevents.getOrDefault(game.getName(), new HashMap<Event, PacketListener>());
		map.put(e, registerPacketListener(killer, game.getPlayerTeam(killer), player, game.getPlayerTeam(player)));
		deathevents.put(game.getName(), map);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeathHighest(PlayerDeathEvent e) {
		if (!Config.final_killed_enabled) {
			return;
		}
		Player player = e.getEntity();
		Player killer = player.getKiller();
		if (killer == null) {
			return;
		}
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null || game.getState() != GameState.RUNNING || game.isSpectator(player) || !game.getPlayers().contains(killer) || game.isSpectator(killer) || !game.getPlayerTeam(player).isDead(game)) {
			return;
		}
		Map<Event, PacketListener> map = deathevents.getOrDefault(game.getName(), new HashMap<Event, PacketListener>());
		if (!map.containsKey(e)) {
			return;
		}
		ProtocolLibrary.getProtocolManager().removePacketListener(map.get(e));
		map.remove(e);
		deathevents.put(game.getName(), map);
		String hearts = "";
		DecimalFormat format = new DecimalFormat("#");
		double health = killer.getHealth() / killer.getMaxHealth() * killer.getHealthScale();
		if (!BedwarsRel.getInstance().getBooleanConfig("hearts-in-halfs", true)) {
			format = new DecimalFormat("#.#");
			health /= 2.0;
		}
		if (BedwarsRel.getInstance().getBooleanConfig("hearts-on-death", true)) {
			hearts = "[" + ChatColor.RED + "\u2764" + format.format(health) + ChatColor.GOLD + "]";
		}
		String string = Config.final_killed_message.replace("{player}", Game.getPlayerWithTeamString(player, game.getPlayerTeam(player), ChatColor.GOLD)).replace("{killer}", Game.getPlayerWithTeamString(killer, game.getPlayerTeam(killer), ChatColor.GOLD, hearts));
		for (Player p : game.getPlayers()) {
			if (p.isOnline()) {
				p.sendMessage(string);
			}
		}
	}

	private PacketListener registerPacketListener(Player killer, Team killerTeam, Player player, Team deathTeam) {
		PacketListener listener = new PacketAdapter(Main.getInstance(), new PacketType[] { PacketType.Play.Server.CHAT }) {
			public void onPacketSending(PacketEvent e) {
				Player p = e.getPlayer();
				WrappedChatComponent chat = e.getPacket().getChatComponents().read(0);
				String hearts = "";
				DecimalFormat format = new DecimalFormat("#");
				double health = killer.getHealth() / killer.getMaxHealth() * killer.getHealthScale();
				if (!BedwarsRel.getInstance().getBooleanConfig("hearts-in-halfs", true)) {
					format = new DecimalFormat("#.#");
					health /= 2.0;
				}
				if (BedwarsRel.getInstance().getBooleanConfig("hearts-on-death", true)) {
					hearts = "[" + ChatColor.RED + "\u2764" + format.format(health) + ChatColor.GOLD + "]";
				}
				WrappedChatComponent[] chats = WrappedChatComponent.fromChatMessage(ChatWriter.pluginMessage(ChatColor.GOLD + BedwarsRel._l((CommandSender) p, "ingame.player.killed", (Map<String, String>) ImmutableMap.of("killer", Game.getPlayerWithTeamString(killer, killerTeam, ChatColor.GOLD, hearts), "player", Game.getPlayerWithTeamString(player, deathTeam, ChatColor.GOLD)))));
				for (WrappedChatComponent c : chats) {
					if (chat.getJson().equals(c.getJson())) {
						e.setCancelled(true);
						break;
					}
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(listener);
		return listener;
	}

	@EventHandler
	public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
		for (Arena arena : Main.getInstance().getArenaManager().getArenas().values()) {
			arena.onArmorStandManipulate(e);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		for (Arena arena : Main.getInstance().getArenaManager().getArenas().values()) {
			arena.onClick(e);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCitizensEnable(CitizensEnableEvent e) {
		File folder = Config.getNPCFile();
		FileConfiguration npcconfig = YamlConfiguration.loadConfiguration(folder);
		if (npcconfig.getKeys(false).contains("npcs")) {
			List<String> npcs = npcconfig.getStringList("npcs");
			List<NPC> gamenpcs = new ArrayList<NPC>();
			for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
				if (npcs.contains(npc.getId() + "")) {
					gamenpcs.add(npc);
				}
			}
			for (NPC npc : gamenpcs) {
				CitizensAPI.getNPCRegistry().deregister(npc);
			}
			npcconfig.set("npcs", new ArrayList<String>());
			try {
				npcconfig.save(folder);
			} catch (IOException e1) {
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamageByEntity(EntityDamageByEntityEvent e) {
		if (!Config.invisibility_player_enabled) {
			return;
		}
		if (e.isCancelled()) {
			return;
		}
		if (!(e.getEntity() instanceof Player && e.getDamager() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getEntity();
		if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			return;
		}
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		if (game.getState() != GameState.RUNNING) {
			return;
		}
		if (BedwarsUtil.isSpectator(game, player)) {
			return;
		}
		if (!Main.getInstance().getArenaManager().getArenas().containsKey(game.getName())) {
			return;
		}
		if (Config.invisibility_player_damage_show_player) {
			Main.getInstance().getArenaManager().getArenas().get(game.getName()).getInvisiblePlayer().removePlayer(player);
		} else {
			Main.getInstance().getArenaManager().getArenas().get(game.getName()).getInvisiblePlayer().showPlayerArmor(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemMerge(ItemMergeEvent e) {
		for (Arena arena : Main.getInstance().getArenaManager().getArenas().values()) {
			arena.onItemMerge(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (Config.hunger_change) {
			return;
		}
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getEntity();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		if (game.getState() != GameState.RUNNING) {
			return;
		}
		e.setFoodLevel(20);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null || game.getState() != GameState.RUNNING || BedwarsUtil.isSpectator(game, player)) {
			return;
		}
		Arena arena = Main.getInstance().getArenaManager().getArenas().get(game.getName());
		if (arena == null) {
			return;
		}
		if (e.getItem().getType().equals(Material.POTION)) {
			ItemStack itemStack = e.getItem().clone();
			PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
			if (Config.clear_bottle) {
				potionMeta.getCustomEffects().forEach(effect -> {
					if (player.hasPotionEffect(effect.getType())) {
						for (PotionEffect playerEffect : player.getActivePotionEffects()) {
							if (playerEffect.getType().equals(effect.getType())) {
								if (effect.getAmplifier() > playerEffect.getAmplifier() || (effect.getAmplifier() == playerEffect.getAmplifier() && effect.getDuration() > playerEffect.getDuration())) {
									player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), false, true), true);
								}
								break;
							}
						}
					} else {
						player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), false, true), true);
					}
				});
				e.setCancelled(true);
				if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
					if (player.getInventory().getItemInHand().isSimilar(itemStack)) {
						player.getInventory().setItemInHand(new ItemStack(Material.AIR));
					}
				} else if (player.getInventory().getItemInMainHand().isSimilar(itemStack)) {
					player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
				} else if (player.getInventory().getItemInOffHand().isSimilar(itemStack)) {
					player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
				}
			}
			if (Config.invisibility_player_enabled) {
				for (PotionEffect potion : potionMeta.getCustomEffects()) {
					if (potion.getType().equals(PotionEffectType.INVISIBILITY)) {
						arena.getInvisiblePlayer().hidePlayer(player);
						if (Config.invisibility_player_hide_particles) {
							for (PotionEffect effect : player.getActivePotionEffects()) {
								player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), true, false), true);
							}
						}
						break;
					}
				}
			}
		}
		if (Config.invisibility_player_enabled && Config.invisibility_player_hide_particles && arena.getInvisiblePlayer().isInvisiblePlayer(player) && (e.getItem().getType() == Material.POTION || e.getItem().getType() == Material.GOLDEN_APPLE || e.getItem().getType() == Material.ROTTEN_FLESH || e.getItem().getType() == Material.RAW_FISH || e.getItem().getType() == Material.RAW_CHICKEN || e.getItem().getType() == Material.SPIDER_EYE || e.getItem().getType() == Material.POISONOUS_POTATO)) {
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				if (player.isOnline()) {
					for (PotionEffect effect : player.getActivePotionEffects()) {
						player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), true, false), true);
					}
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e) {
		Main.getInstance().getArenaManager().getArenas().values().forEach(arena -> {
			arena.onInteract(e);
		});
		if (e.isCancelled()) {
			return;
		}
		if (e.getItem() == null || !(e.getItem().getType() == Material.WATER_BUCKET || e.getItem().getType() == Material.LAVA_BUCKET) || e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Player player = e.getPlayer();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return;
		}
		if (BedwarsUtil.isSpectator(game, player) || player.getGameMode() == GameMode.SPECTATOR) {
			return;
		}
		game.getRegion().addPlacedBlock(e.getClickedBlock().getRelative(e.getBlockFace()), null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		Main.getInstance().getArenaManager().getArenas().values().forEach(arena -> {
			arena.onInteractEntity(e);
		});
	}

	@EventHandler
	public void onChangedWorld(PlayerChangedWorldEvent e) {
		Main.getInstance().getEditHolographicManager().remove(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Main.getInstance().getEditHolographicManager().remove(e.getPlayer());
		Player player = (Player) e.getPlayer();
		MenuManager man = Main.getInstance().getMenuManager();
		man.removePlayer(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e) {
		Main.getInstance().getArenaManager().getArenas().values().forEach(arena -> {
			arena.onDamage(e);
		});
	}

	@EventHandler
	public void onHangingBreak(HangingBreakEvent e) {
		Main.getInstance().getArenaManager().getArenas().values().forEach(arena -> {
			arena.onHangingBreak(e);
		});
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Player player = (Player) e.getPlayer();
		MenuManager man = Main.getInstance().getMenuManager();
		man.removePlayer(player);
	}

	private void onPacketReceiving() {
		PacketListener packetListener = new PacketAdapter(Main.getInstance(), ListenerPriority.HIGHEST, new PacketType[] { PacketType.Play.Client.BLOCK_DIG, PacketType.Play.Client.WINDOW_CLICK }) {
			public void onPacketReceiving(PacketEvent e) {
				Player player = e.getPlayer();
				PacketContainer packet = e.getPacket();
				if (e.getPacketType() == PacketType.Play.Client.BLOCK_DIG) {
					if (!packet.getPlayerDigTypes().read(0).equals(EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK)) {
						return;
					}
					Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
					if (game == null || game.getState() != GameState.RUNNING || !BedwarsUtil.isSpectator(game, player)) {
						return;
					}
					e.setCancelled(true);
					Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
						BlockPosition position = packet.getBlockPositionModifier().read(0);
						Location location = new Location(e.getPlayer().getWorld(), position.getX(), position.getY(), position.getZ());
						location.getBlock().getState().update();
					});
				} else if (e.getPacketType() == PacketType.Play.Client.WINDOW_CLICK) {
					Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
					if (game == null || game.getState() != GameState.RUNNING || game.isSpectator(player)) {
						return;
					}
					int slot = packet.getIntegers().read(1);
					if (slot < 0) {
						return;
					}
					ItemStack itemStack = player.getOpenInventory().getItem(slot);
					if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
						return;
					}
					List<String> lore = itemStack.getItemMeta().getLore();
					ItemStack leggings = player.getInventory().getLeggings();
					ItemStack boots = player.getInventory().getBoots();
					if (leggings == null || boots == null) {
						return;
					}
					if ((lore.contains("§a§r§m§o§r§0§0§1") && ((leggings.getType() == Material.CHAINMAIL_LEGGINGS && leggings.getType() == Material.CHAINMAIL_LEGGINGS) || (leggings.getType() == Material.IRON_LEGGINGS && leggings.getType() == Material.IRON_LEGGINGS) || (leggings.getType() == Material.DIAMOND_LEGGINGS && leggings.getType() == Material.DIAMOND_LEGGINGS))) || (lore.contains("§a§r§m§o§r§0§0§2") && ((leggings.getType() == Material.IRON_LEGGINGS && leggings.getType() == Material.IRON_LEGGINGS) || (leggings.getType() == Material.DIAMOND_LEGGINGS && leggings.getType() == Material.DIAMOND_LEGGINGS))) || (lore.contains("§a§r§m§o§r§0§0§3") && leggings.getType() == Material.DIAMOND_LEGGINGS && leggings.getType() == Material.DIAMOND_LEGGINGS)) {
						e.setCancelled(true);
						new BukkitRunnable() {
							@Override
							public void run() {
								if (player.isOnline()) {
									player.updateInventory();
								}
							}
						}.runTaskLater(Main.getInstance(), 1L);
					}
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
	}

	private void onPacketSending() {
		PacketListener packetListener = new PacketAdapter(Main.getInstance(), ListenerPriority.HIGHEST, new PacketType[] { PacketType.Play.Server.SCOREBOARD_SCORE }) {
			@Override
			public void onPacketSending(PacketEvent e) {
				PacketContainer packet = e.getPacket();
				if (e.getPacketType().equals(PacketType.Play.Server.SCOREBOARD_SCORE) && packet.getScoreboardActions().read(0).equals(ScoreboardAction.REMOVE) && packet.getStrings().read(1).equals("") && getPlayer(packet.getStrings().read(0)) != null) {
					e.setCancelled(true);
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
	}

	private Player getPlayer(String name) {
		if (name == null) {
			return null;
		}
		Player player = Bukkit.getPlayer(name);
		if (player == null) {
			return null;
		}
		if (player.getName().equals(name)) {
			return player;
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}

	private PotionEffect getPotionEffect(Player player, PotionEffectType type) {
		for (PotionEffect effect : player.getActivePotionEffects()) {
			if (effect.getType().equals(type)) {
				return effect;
			}
		}
		return null;
	}

	private Location toLocation(String loc) {
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
}
