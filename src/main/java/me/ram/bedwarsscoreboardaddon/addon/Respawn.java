package me.ram.bedwarsscoreboardaddon.addon;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonPlayerRespawnEvent;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class Respawn {

	@Getter
	private Game game;
	@Getter
	private Arena arena;
	private List<Player> players;
	private Map<Player, Long> protected_time;

	public Respawn(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		players = new ArrayList<Player>();
		protected_time = new HashMap<Player, Long>();
		arena.addGameTask(new BukkitRunnable() {
			@Override
			public void run() {
				players.forEach(player -> {
					hideInventory(player);
				});
			}
		}.runTaskTimer(Main.getInstance(), 1L, 1L));
	}

	public boolean isRespawning(Player player) {
		return players.contains(player);
	}

	private void addRespawningPlayer(Player player) {
		if (!players.contains(player)) {
			players.add(player);
		}
		game.getPlayers().forEach(p -> {
			hidePlayer(p, player);
		});
	}

	private void removeRespawningPlayer(Player player) {
		if (players.contains(player)) {
			players.remove(player);
		}
		game.getPlayers().forEach(p -> {
			showPlayer(p, player);
		});
	}

	public void onPlayerLeave(Player player) {
		removeRespawningPlayer(player);
		if (protected_time.containsKey(player)) {
			protected_time.remove(player);
		}
	}

	public void onPlayerJoined(Player player) {
		players.forEach(p -> {
			hidePlayer(player, p);
		});
	}

	private void sendGameModeChange(Player player, int mode) {
		ProtocolManager man = ProtocolLibrary.getProtocolManager();
		PacketContainer packet = man.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
		packet.getIntegers().write(0, 3);
		packet.getFloat().write(0, (float) mode);
		try {
			man.sendServerPacket(player, packet, false);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void onRespawn(Player player, boolean rejoin) {
		if (!Config.respawn_enabled || game.isSpectator(player) || (game.getPlayerTeam(player).isDead(game) && !rejoin) || players.contains(player)) {
			return;
		}
		int ateams = 0;
		for (Team team : game.getTeams().values()) {
			if (!(team.isDead(game) && team.getPlayers().size() <= 0)) {
				ateams++;
			}
		}
		if (ateams <= 1) {
			return;
		}
		addRespawningPlayer(player);
		player.setGameMode(GameMode.SURVIVAL);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
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
		Location location = new Location(world, (x / Double.valueOf(i)), Config.respawn_centre_height, (z / Double.valueOf(i)));
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			if (!game.getState().equals(GameState.RUNNING)) {
				return;
			}
			if (!player.isOnline() || !players.contains(player)) {
				return;
			}
			player.setVelocity(new Vector(0, 0, 0));
			player.teleport(location);
			player.setAllowFlight(true);
			player.setFlying(true);
			sendGameModeChange(player, 3);
			arena.addGameTask(new BukkitRunnable() {
				int respawntime = Config.respawn_respawn_delay;

				@Override
				public void run() {
					if (!player.isOnline()) {
						cancel();
						return;
					}
					if (!players.contains(player)) {
						cancel();
						player.setGameMode(GameMode.SURVIVAL);
						sendGameModeChange(player, 0);
						player.setAllowFlight(false);
						player.removePotionEffect(PotionEffectType.INVISIBILITY);
						player.updateInventory();
						return;
					}
					if (game.getPlayerTeam(player) == null) {
						cancel();
						player.setGameMode(GameMode.SURVIVAL);
						sendGameModeChange(player, 0);
						player.setAllowFlight(false);
						player.removePotionEffect(PotionEffectType.INVISIBILITY);
						player.updateInventory();
						return;
					}
					if (respawntime <= Config.respawn_respawn_delay && respawntime > 0) {
						if (!Config.respawn_countdown_title.equals("") || !Config.respawn_countdown_subtitle.equals("")) {
							Utils.sendTitle(player, 2, 50, 0, Config.respawn_countdown_title.replace("{respawntime}", respawntime + ""), Config.respawn_countdown_subtitle.replace("{respawntime}", respawntime + ""));
						}
						if (!Config.respawn_countdown_message.equals("")) {
							player.sendMessage(Config.respawn_countdown_message.replace("{respawntime}", respawntime + ""));
						}
					}
					if (respawntime <= 0) {
						cancel();
						removeRespawningPlayer(player);
						protected_time.put(player, System.currentTimeMillis());
						player.teleport(game.getPlayerTeam(player).getSpawnLocation());
						player.setVelocity(new Vector(0, 0.01, 0));
						player.setAllowFlight(false);
						player.setGameMode(GameMode.SURVIVAL);
						sendGameModeChange(player, 0);
						player.getActivePotionEffects().forEach(effect -> {
							player.removePotionEffect(effect.getType());
						});
						player.setFoodLevel(20);
						player.updateInventory();
						if (!Config.respawn_respawn_title.equals("") || !Config.respawn_respawn_subtitle.equals("")) {
							Utils.sendTitle(player, 10, 30, 10, Config.respawn_respawn_title, Config.respawn_respawn_subtitle);
						}
						if (!Config.respawn_respawn_message.equals("")) {
							player.sendMessage(Config.respawn_respawn_message);
						}
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 0), true);
						Bukkit.getPluginManager().callEvent(new BoardAddonPlayerRespawnEvent(game, player));
						return;
					}
					respawntime--;
				}
			}.runTaskTimer(Main.getInstance(), 0L, 21L));
		}, 1L);
	}

	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getEntity();
		if (!game.isInGame(player)) {
			return;
		}
		if (!protected_time.containsKey(player)) {
			return;
		}
		int protime = 500;
		if (Config.respawn_protected_enabled) {
			protime = Config.respawn_protected_time > 0 ? Config.respawn_protected_time * 1000 : 500;
		}
		if ((System.currentTimeMillis() - protected_time.get(player)) < protime) {
			e.setCancelled(true);
			return;
		}
		protected_time.remove(player);
	}

	private void hidePlayer(Player p1, Player p2) {
		if (p1.getUniqueId().equals(p2.getUniqueId())) {
			return;
		}
		if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_12")) {
			p1.hidePlayer(Main.getInstance(), p2);
		} else {
			p1.hidePlayer(p2);
		}
	}

	private void showPlayer(Player p1, Player p2) {
		if (p1.getUniqueId().equals(p2.getUniqueId())) {
			return;
		}
		if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_12")) {
			p1.showPlayer(Main.getInstance(), p2);
		} else {
			p1.showPlayer(p2);
		}
	}

	private void hideInventory(Player player) {
		PlayerInventory inventory = player.getInventory();
		boolean is1_8 = BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8");
		for (int i = 0; i < (is1_8 ? 40 : 45); i++) {
			try {
				ItemStack item = inventory.getItem(i);
				int slot = i;
				if (i >= 36) {
					slot = 44 - i;
				} else if (i <= 8) {
					slot = i + 36;
				}
				if (!is1_8 && i == 40) {
					slot = 45;
				}
				if (item != null && !item.getType().equals(Material.AIR)) {
					sendHideSlot(player, slot);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sendHideSlot(Player player, int slot) {
		ProtocolManager man = ProtocolLibrary.getProtocolManager();
		PacketContainer packet = man.createPacket(PacketType.Play.Server.SET_SLOT);
		packet.getIntegers().write(0, 0);
		packet.getIntegers().write(1, slot);
		packet.getItemModifier().write(0, new ItemStack(Material.AIR));
		try {
			man.sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
