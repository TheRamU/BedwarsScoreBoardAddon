package me.ram.bedwarsscoreboardaddon.addon;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonPlayerInvisibilityEvent;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class InvisibilityPlayer {

	@Getter
	private Game game;
	@Getter
	private Arena arena;
	private List<UUID> players;
	private List<UUID> hplayers;

	public InvisibilityPlayer(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		players = new ArrayList<UUID>();
		hplayers = new ArrayList<UUID>();
	}

	public List<UUID> getPlayers() {
		return players;
	}

	public void removePlayer(Player player) {
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		players.remove(player.getUniqueId());
	}

	public void showPlayerArmor(Player player) {
		hplayers.remove(player.getUniqueId());
		showArmor(player);
	}

	public Boolean isInvisiblePlayer(Player player) {
		return players.contains(player.getUniqueId());
	}

	public void hidePlayer(Player player) {
		BoardAddonPlayerInvisibilityEvent playerInvisibilityEvent = new BoardAddonPlayerInvisibilityEvent(game, player);
		Bukkit.getPluginManager().callEvent(playerInvisibilityEvent);
		if (playerInvisibilityEvent.isCancelled()) {
			return;
		}
		if (!hplayers.contains(player.getUniqueId())) {
			hplayers.add(player.getUniqueId());
		}
		if (players.contains(player.getUniqueId())) {
			return;
		}
		players.add(player.getUniqueId());
		BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
			if (Config.invisibility_player_footstep) {
				arena.addGameTask(new BukkitRunnable() {
					Location loc = player.getLocation().clone();

					@Override
					public void run() {
						if (player.isOnline() && (loc.getX() != player.getLocation().getX() || loc.getY() != player.getLocation().getY() || loc.getZ() != player.getLocation().getZ())) {
							player.getWorld().playEffect(player.getLocation().clone().add((Math.random() - Math.random()) * 0.5, 0.05, (Math.random() - Math.random()) * 0.5), Effect.FOOTSTEP, 0);
						}
					}
				}.runTaskLater(Main.getInstance(), 8L));
			}
		}, 0, 0);
		arena.addGameTask(task);
		arena.addGameTask(new BukkitRunnable() {
			@Override
			public void run() {
				if (!player.isOnline() || !player.hasPotionEffect(PotionEffectType.INVISIBILITY) || !players.contains(player.getUniqueId()) || !game.getPlayers().contains(player) || game.isSpectator(player)) {
					cancel();
					task.cancel();
					players.remove(player.getUniqueId());
					hplayers.remove(player.getUniqueId());
					if (player.isOnline()) {
						showArmor(player);
						if (Config.invisibility_player_hide_particles) {
							for (PotionEffect effect : player.getActivePotionEffects()) {
								player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), false, true), true);
							}
						}
					}
					return;
				}
				if (hplayers.contains(player.getUniqueId())) {
					hideArmor(player);
				}
			}
		}.runTaskTimer(Main.getInstance(), 2L, 1L));
	}

	private void hideArmor(Player player) {
		if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
			try {
				Constructor constructor = Utils.getNMSClass("PacketPlayOutEntityEquipment").getConstructor(Integer.TYPE, Integer.TYPE, Utils.getNMSClass("ItemStack"));
				Object as = Utils.getClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, new ItemStack(Material.AIR));
				Object packet1 = constructor.newInstance(player.getEntityId(), 1, as);
				Object packet2 = constructor.newInstance(player.getEntityId(), 2, as);
				Object packet3 = constructor.newInstance(player.getEntityId(), 3, as);
				Object packet4 = constructor.newInstance(player.getEntityId(), 4, as);
				List<Player> players = game.getPlayerTeam(player).getPlayers();
				for (Player p : game.getPlayers()) {
					if (p != player && !players.contains(p)) {
						Utils.sendPacket(p, packet1);
						Utils.sendPacket(p, packet2);
						Utils.sendPacket(p, packet3);
						Utils.sendPacket(p, packet4);
					}
				}
			} catch (Exception e) {
			}
		} else {
			try {
				Constructor constructor = Utils.getNMSClass("PacketPlayOutEntityEquipment").getConstructor(Integer.TYPE, Utils.getNMSClass("EnumItemSlot"), Utils.getNMSClass("ItemStack"));
				Object as = Utils.getClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, new ItemStack(Material.AIR));
				Object packet1 = constructor.newInstance(player.getEntityId(), Utils.getNMSClass("EnumItemSlot").getField("FEET").get(null), as);
				Object packet2 = constructor.newInstance(player.getEntityId(), Utils.getNMSClass("EnumItemSlot").getField("LEGS").get(null), as);
				Object packet3 = constructor.newInstance(player.getEntityId(), Utils.getNMSClass("EnumItemSlot").getField("CHEST").get(null), as);
				Object packet4 = constructor.newInstance(player.getEntityId(), Utils.getNMSClass("EnumItemSlot").getField("HEAD").get(null), as);
				List<Player> players = game.getPlayerTeam(player).getPlayers();
				for (Player p : game.getPlayers()) {
					if (p != player && !players.contains(p)) {
						Utils.sendPacket(p, packet1);
						Utils.sendPacket(p, packet2);
						Utils.sendPacket(p, packet3);
						Utils.sendPacket(p, packet4);
					}
				}
			} catch (Exception e) {
			}
		}
	}

	private void showArmor(Player player) {
		if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
			try {
				Constructor constructor = Utils.getNMSClass("PacketPlayOutEntityEquipment").getConstructor(Integer.TYPE, Integer.TYPE, Utils.getNMSClass("ItemStack"));
				Method method = Utils.getClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class);
				Object packet1 = constructor.newInstance(player.getEntityId(), 1, method.invoke(null, player.getInventory().getBoots()));
				Object packet2 = constructor.newInstance(player.getEntityId(), 2, method.invoke(null, player.getInventory().getLeggings()));
				Object packet3 = constructor.newInstance(player.getEntityId(), 3, method.invoke(null, player.getInventory().getChestplate()));
				Object packet4 = constructor.newInstance(player.getEntityId(), 4, method.invoke(null, player.getInventory().getHelmet()));
				List<Player> players = game.getPlayerTeam(player).getPlayers();
				for (Player p : game.getPlayers()) {
					if (p != player && !players.contains(p)) {
						Utils.sendPacket(p, packet1);
						Utils.sendPacket(p, packet2);
						Utils.sendPacket(p, packet3);
						Utils.sendPacket(p, packet4);
					}
				}
			} catch (Exception e) {
			}
		} else {
			try {
				Constructor constructor = Utils.getNMSClass("PacketPlayOutEntityEquipment").getConstructor(Integer.TYPE, Utils.getNMSClass("EnumItemSlot"), Utils.getNMSClass("ItemStack"));
				Method method = Utils.getClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class);
				Object packet1 = constructor.newInstance(player.getEntityId(), Utils.getNMSClass("EnumItemSlot").getField("FEET").get(null), method.invoke(null, player.getInventory().getBoots()));
				Object packet2 = constructor.newInstance(player.getEntityId(), Utils.getNMSClass("EnumItemSlot").getField("LEGS").get(null), method.invoke(null, player.getInventory().getLeggings()));
				Object packet3 = constructor.newInstance(player.getEntityId(), Utils.getNMSClass("EnumItemSlot").getField("CHEST").get(null), method.invoke(null, player.getInventory().getChestplate()));
				Object packet4 = constructor.newInstance(player.getEntityId(), Utils.getNMSClass("EnumItemSlot").getField("HEAD").get(null), method.invoke(null, player.getInventory().getHelmet()));
				List<Player> players = game.getPlayerTeam(player).getPlayers();
				for (Player p : game.getPlayers()) {
					if (p != player && !players.contains(p)) {
						Utils.sendPacket(p, packet1);
						Utils.sendPacket(p, packet2);
						Utils.sendPacket(p, packet3);
						Utils.sendPacket(p, packet4);
					}
				}
			} catch (Exception e) {
			}
		}
	}
}
