package me.ram.bedwarsscoreboardaddon.addon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonPlayerRespawnEvent;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class Respawn {

	private Game game;
	private List<Player> players;

	public Respawn(Game game) {
		players = new ArrayList<Player>();
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public void onRespawn(Player player) {
		if (!Config.respawn_centre_enabled) {
			return;
		}
		if (!players.contains(player)) {
			return;
		}
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
		Location location = new Location(world, (x / Double.valueOf(i)), Config.respawn_centre_height,
				(z / Double.valueOf(i)));
		new BukkitRunnable() {
			@Override
			public void run() {
				if (players.contains(player)) {
					player.setVelocity(new Vector(0, 0, 0));
					player.teleport(location);
				}
			}
		}.runTaskLater(Main.getInstance(), 1L);
	}

	public void onDeath(Player player, boolean rejoin) {
		if (!Config.respawn_enabled || game.isSpectator(player) || (game.getPlayerTeam(player).isDead(game) && !rejoin)
				|| players.contains(player)) {
			return;
		}
		players.add(player);
		int ateams = 0;
		for (Team team : game.getTeams().values()) {
			if (!(team.isDead(game) && team.getPlayers().size() <= 0)) {
				ateams++;
			}
		}
		if (ateams <= 1) {
			return;
		}
		List<Player> invplayers = new ArrayList<Player>();
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		if (Config.invisibility_player_enabled) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (player.isOnline() && players.contains(player)) {
						for (Player p : game.getPlayers()) {
							if (!p.getUniqueId().equals(player.getUniqueId())
									&& p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
								PacketContainer packet = protocolManager
										.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
								packet.getIntegerArrays().write(0, new int[] { p.getEntityId() });
								try {
									protocolManager.sendServerPacket(player, packet);
									if (!invplayers.contains(p)) {
										invplayers.add(p);
									}
								} catch (InvocationTargetException e1) {
									e1.printStackTrace();
								}
							}
						}
					} else {
						cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), 1L, 1L);
		}
		player.setGameMode(GameMode.SPECTATOR);
		player.setVelocity(new Vector(0, 0, 0));
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!player.isOnline()) {
					cancel();
					return;
				}
				if (!player.isDead()) {
					if (!Config.respawn_respawning_title.equals("") || !Config.respawn_respawning_subtitle.equals("")) {
						Utils.sendTitle(player, 0, 50, 0,
								Config.respawn_respawning_title.replace("{respawntime}",
										Config.respawn_respawn_delay + ""),
								Config.respawn_respawning_subtitle.replace("{respawntime}",
										Config.respawn_respawn_delay + ""));
					}
					new BukkitRunnable() {
						int respawntime = Config.respawn_respawn_delay;

						@Override
						public void run() {
							if (!players.contains(player)) {
								player.setGameMode(GameMode.SURVIVAL);
								cancel();
								return;
							}
							if (game.getPlayerTeam(player) == null) {
								player.setGameMode(GameMode.SURVIVAL);
								cancel();
								return;
							}
							if (respawntime <= Config.respawn_respawn_delay && respawntime != 0) {
								if (!Config.respawn_respawning_title.equals("")
										|| !Config.respawn_respawning_subtitle.equals("")) {
									Utils.sendTitle(player, 3, 50, 0,
											Config.respawn_respawning_title.replace("{respawntime}", respawntime + ""),
											Config.respawn_respawning_subtitle.replace("{respawntime}",
													respawntime + ""));
								}
								if (!Config.respawn_respawning_message.equals("")) {
									player.sendMessage(Config.respawn_respawning_message.replace("{respawntime}",
											respawntime + ""));
								}
							}
							if (respawntime <= 0) {
								cancel();
								players.remove(player);
								player.setVelocity(new Vector(0, 0, 0));
								player.teleport(game.getPlayerTeam(player).getSpawnLocation());
								player.setGameMode(GameMode.SURVIVAL);
								if (!Config.respawn_respawned_title.equals("")
										|| !Config.respawn_respawned_subtitle.equals("")) {
									Utils.sendTitle(player, 10, 50, 10, Config.respawn_respawned_title,
											Config.respawn_respawned_subtitle);
								}
								if (!Config.respawn_respawned_message.equals("")) {
									player.sendMessage(Config.respawn_respawned_message);
								}
								player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 0), true);
								Bukkit.getPluginManager().callEvent(new BoardAddonPlayerRespawnEvent(game, player));
								if (Config.invisibility_player_enabled) {
									for (Player p : invplayers) {
										if (p.isOnline()) {
											PacketContainer packet = protocolManager
													.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
											packet.getIntegers().write(0, p.getEntityId());
											packet.getUUIDs().write(0, p.getUniqueId());
											if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
												try {
													Method method = Utils.getNMSClass("MathHelper").getMethod("floor",
															double.class);
													packet.getIntegers().write(1, (Integer) method.invoke(null,
															p.getLocation().getX() * 32.0));
													packet.getIntegers().write(2, (Integer) method.invoke(null,
															p.getLocation().getY() * 32.0));
													packet.getIntegers().write(3, (Integer) method.invoke(null,
															p.getLocation().getZ() * 32.0));
												} catch (Exception e) {
													e.printStackTrace();
												}
											} else {
												packet.getDoubles().write(0, p.getLocation().getX());
												packet.getDoubles().write(1, p.getLocation().getY());
												packet.getDoubles().write(2, p.getLocation().getZ());
											}
											packet.getBytes().write(0,
													(byte) (int) (p.getLocation().getYaw() * 256.0F / 360.0F));
											packet.getBytes().write(1,
													(byte) (int) (p.getLocation().getPitch() * 256.0F / 360.0F));
											packet.getDataWatcherModifier().write(0,
													WrappedDataWatcher.getEntityWatcher(p));
											try {
												protocolManager.sendServerPacket(player, packet);
											} catch (InvocationTargetException e1) {
												e1.printStackTrace();
											}
										}
									}
								}
								return;
							}
							respawntime--;
						}
					}.runTaskTimer(Main.getInstance(), 30L, 21L);
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 0L);
	}
}
