package me.ram.bedwarsscoreboardaddon.addon;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
	private Map<Player, List<Player>> inv_players;

	public Respawn(Game game) {
		players = new ArrayList<Player>();
		inv_players = new HashMap<Player, List<Player>>();
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public void onPlayerLeave(Player player) {
		if (players.contains(player)) {
			players.remove(player);
		}
		if (inv_players.containsKey(player)) {
			if (player.isOnline()) {
				inv_players.get(player).forEach(p -> {
					if (p.isOnline()) {
						showPlayer(player, p);
					}
				});
			}
			inv_players.remove(player);
		}
	}

	public void onRespawn(Player player, boolean rejoin) {
		if (!Config.respawn_enabled || game.isSpectator(player) || (game.getPlayerTeam(player).isDead(game) && !rejoin)
				|| players.contains(player)) {
			return;
		}
		players.add(player);
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
		if (Config.invisibility_player_enabled) {
			new BukkitRunnable() {
				@Override
				public void run() {
					List<Player> list = inv_players.getOrDefault(player, new ArrayList<Player>());
					if (!player.isOnline() || !players.contains(player)) {
						cancel();
						if (player.isOnline()) {
							game.getPlayers().forEach(p -> {
								if (p.isOnline()) {
									showPlayer(player, p);
								}
							});
							inv_players.remove(player);
						}
						return;
					}
					list.forEach(p -> {
						if (p.isOnline() && !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
							showPlayer(player, p);
						}
					});
					list.clear();
					for (Player p : game.getPlayers()) {
						if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
							hidePlayer(player, p);
							list.add(p);
						}
					}
					inv_players.put(player, list);
				}
			}.runTaskTimer(Main.getInstance(), 1L, 1L);
		}
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			if (!player.isOnline() || !players.contains(player)) {
				return;
			}
			player.setVelocity(new Vector(0, 0, 0));
			player.teleport(location);
			if (!Config.respawn_respawning_title.equals("") || !Config.respawn_respawning_subtitle.equals("")) {
				Utils.sendTitle(player, 0, 50, 0,
						Config.respawn_respawning_title.replace("{respawntime}", Config.respawn_respawn_delay + ""),
						Config.respawn_respawning_subtitle.replace("{respawntime}", Config.respawn_respawn_delay + ""));
			}
			new BukkitRunnable() {
				int respawntime = Config.respawn_respawn_delay;

				@Override
				public void run() {
					if (!player.isOnline()) {
						cancel();
						return;
					}
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
					if (respawntime <= Config.respawn_respawn_delay && respawntime > 0) {
						if (!Config.respawn_respawning_title.equals("")
								|| !Config.respawn_respawning_subtitle.equals("")) {
							Utils.sendTitle(player, 3, 50, 0,
									Config.respawn_respawning_title.replace("{respawntime}", respawntime + ""),
									Config.respawn_respawning_subtitle.replace("{respawntime}", respawntime + ""));
						}
						if (!Config.respawn_respawning_message.equals("")) {
							player.sendMessage(
									Config.respawn_respawning_message.replace("{respawntime}", respawntime + ""));
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
						return;
					}
					respawntime--;
				}
			}.runTaskTimer(Main.getInstance(), 30L, 21L);
		}, 1L);
	}

	public void onDeath(Player player) {
		if (!Config.respawn_enabled || game.isSpectator(player) || game.getPlayerTeam(player).isDead(game)
				|| players.contains(player)) {
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
		if (Config.invisibility_player_enabled) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!player.isOnline()) {
						cancel();
						return;
					}
					List<Player> list = inv_players.getOrDefault(player, new ArrayList<Player>());
					for (Player p : game.getPlayers()) {
						if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
							hidePlayer(player, p);
							list.add(p);
						}
					}
					inv_players.put(player, list);
				}
			}.runTaskLater(Main.getInstance(), 1L);
		}
		player.setGameMode(GameMode.SPECTATOR);
		player.setVelocity(new Vector(0, 0, 0));
	}

	private void hidePlayer(Player player, Player p) {
		if (p.getUniqueId().equals(player.getUniqueId())) {
			return;
		}
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet.getIntegerArrays().write(0, new int[] { p.getEntityId() });
		try {
			protocolManager.sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void showPlayer(Player player, Player p) {
		if (p.getUniqueId().equals(player.getUniqueId())) {
			return;
		}
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		{
			PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
			packet.getIntegers().write(0, p.getEntityId());
			packet.getUUIDs().write(0, p.getUniqueId());
			if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
				try {
					Method method = Utils.getNMSClass("MathHelper").getMethod("floor", double.class);
					packet.getIntegers().write(1, (Integer) method.invoke(null, p.getLocation().getX() * 32.0));
					packet.getIntegers().write(2,
							(Integer) method.invoke(null, (p.getLocation().getY() + 256.0) * 32.0));
					packet.getIntegers().write(3, (Integer) method.invoke(null, p.getLocation().getZ() * 32.0));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				packet.getDoubles().write(0, p.getLocation().getX());
				packet.getDoubles().write(1, p.getLocation().getY() + 256.0);
				packet.getDoubles().write(2, p.getLocation().getZ());
			}
			packet.getBytes().write(0, (byte) (int) (p.getLocation().getYaw() * 256.0F / 360.0F));
			packet.getBytes().write(1, (byte) (int) (p.getLocation().getPitch() * 256.0F / 360.0F));
			packet.getDataWatcherModifier().write(0, WrappedDataWatcher.getEntityWatcher(p));
			try {
				protocolManager.sendServerPacket(player, packet, true);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
		packet.getIntegers().write(0, p.getEntityId());
		if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
			try {
				Method method = Utils.getNMSClass("MathHelper").getMethod("floor", double.class);
				packet.getIntegers().write(1, (Integer) method.invoke(null, p.getLocation().getX() * 32.0));
				packet.getIntegers().write(2, (Integer) method.invoke(null, p.getLocation().getY() * 32.0));
				packet.getIntegers().write(3, (Integer) method.invoke(null, p.getLocation().getZ() * 32.0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			packet.getDoubles().write(0, p.getLocation().getX());
			packet.getDoubles().write(1, p.getLocation().getY());
			packet.getDoubles().write(2, p.getLocation().getZ());
		}
		packet.getBytes().write(0, (byte) (int) (p.getLocation().getYaw() * 256.0F / 360.0F));
		packet.getBytes().write(1, (byte) (int) (p.getLocation().getPitch() * 256.0F / 360.0F));
		packet.getBooleans().write(0, false);
		try {
			protocolManager.sendServerPacket(player, packet, true);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		showArmor(player, p);
	}

	private void showArmor(Player player, Player p) {
		if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
			try {
				Constructor constructor = Utils.getNMSClass("PacketPlayOutEntityEquipment").getConstructor(Integer.TYPE,
						Integer.TYPE, Utils.getNMSClass("ItemStack"));
				Method method = Utils.getClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class);
				Object packet1 = constructor.newInstance(p.getEntityId(), 1,
						method.invoke(null, p.getInventory().getBoots()));
				Object packet2 = constructor.newInstance(p.getEntityId(), 2,
						method.invoke(null, p.getInventory().getLeggings()));
				Object packet3 = constructor.newInstance(p.getEntityId(), 3,
						method.invoke(null, p.getInventory().getChestplate()));
				Object packet4 = constructor.newInstance(p.getEntityId(), 4,
						method.invoke(null, p.getInventory().getHelmet()));
				Object packet5 = constructor.newInstance(p.getEntityId(), 0,
						method.invoke(null, p.getInventory().getItemInHand()));
				Utils.sendPacket(player, packet1);
				Utils.sendPacket(player, packet2);
				Utils.sendPacket(player, packet3);
				Utils.sendPacket(player, packet4);
				Utils.sendPacket(player, packet5);
			} catch (Exception e) {
			}
		} else {
			try {
				Constructor constructor = Utils.getNMSClass("PacketPlayOutEntityEquipment").getConstructor(Integer.TYPE,
						Utils.getNMSClass("EnumItemSlot"), Utils.getNMSClass("ItemStack"));
				Method method = Utils.getClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class);
				Object packet1 = constructor.newInstance(p.getEntityId(),
						Utils.getNMSClass("EnumItemSlot").getField("FEET").get(null),
						method.invoke(null, p.getInventory().getBoots()));
				Object packet2 = constructor.newInstance(p.getEntityId(),
						Utils.getNMSClass("EnumItemSlot").getField("LEGS").get(null),
						method.invoke(null, p.getInventory().getLeggings()));
				Object packet3 = constructor.newInstance(p.getEntityId(),
						Utils.getNMSClass("EnumItemSlot").getField("CHEST").get(null),
						method.invoke(null, p.getInventory().getChestplate()));
				Object packet4 = constructor.newInstance(p.getEntityId(),
						Utils.getNMSClass("EnumItemSlot").getField("HEAD").get(null),
						method.invoke(null, p.getInventory().getHelmet()));
				Object packet6 = constructor.newInstance(p.getEntityId(),
						Utils.getNMSClass("EnumItemSlot").getField("MAINHAND").get(null),
						method.invoke(null, p.getInventory().getItemInMainHand()));
				Object packet7 = constructor.newInstance(p.getEntityId(),
						Utils.getNMSClass("EnumItemSlot").getField("OFFHAND").get(null),
						method.invoke(null, p.getInventory().getItemInOffHand()));
				Utils.sendPacket(player, packet1);
				Utils.sendPacket(player, packet2);
				Utils.sendPacket(player, packet3);
				Utils.sendPacket(player, packet4);
				Utils.sendPacket(player, packet6);
				Utils.sendPacket(player, packet7);
			} catch (Exception e) {
			}
		}
	}
}
