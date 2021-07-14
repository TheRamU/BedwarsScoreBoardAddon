package me.ram.bedwarsscoreboardaddon.addon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;

import io.github.bedwarsrel.game.Game;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.api.HolographicAPI;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;

public class Graffiti {

	@Getter
	private Game game;
	@Getter
	private Arena arena;
	private Map<ItemFrame, HolographicAPI> holographics;
	private List<ItemFrame> itemFrames;

	public Graffiti(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		itemFrames = new ArrayList<ItemFrame>();
		holographics = new HashMap<ItemFrame, HolographicAPI>();
		if (Config.graffiti_enabled) {
			Location loc1 = game.getLoc1();
			Location loc2 = game.getLoc2();
			Location centre = loc1.clone().add(loc2).multiply(0.5);
			int x = Math.abs(loc1.getBlockX() - loc2.getBlockX());
			int y = Math.abs(loc1.getBlockY() - loc2.getBlockY());
			int z = Math.abs(loc1.getBlockZ() - loc2.getBlockZ());
			arena.addGameTask(new BukkitRunnable() {

				@Override
				public void run() {
					for (Entity entity : centre.getWorld().getNearbyEntities(centre, x, y, z)) {
						if (entity instanceof ItemFrame && !entity.isDead()) {
							ItemFrame frame = (ItemFrame) entity;
							frame.setItem(new ItemStack(Material.AIR));
							frame.setRotation(Rotation.NONE);
							if (!Config.graffiti_holographic_enabled) {
								continue;
							}
							Location loc1 = frame.getLocation().getBlock().getLocation();
							Location loc2 = frame.getLocation().getBlock().getRelative(frame.getAttachedFace()).getLocation();
							Location loc = loc1.add(loc2.subtract(loc1).multiply(0.375)).add(0.5, -2.25, 0.5);
							HolographicAPI holo = new HolographicAPI(loc, Config.graffiti_holographic_text);
							game.getPlayers().forEach(p -> {
								holo.display(p);
							});
							holographics.put(frame, holo);
						}
					}
				}
			}.runTaskAsynchronously(Main.getInstance()));
		}
	}

	public void reset() {
		itemFrames.forEach(itemFrame -> {
			if (!itemFrame.isDead()) {
				itemFrame.setItem(new ItemStack(Material.AIR));
				itemFrame.setCustomNameVisible(false);
			}
		});
		holographics.values().forEach(holo -> {
			holo.remove();
		});
	}

	public void onPlayerJoin(Player player) {
		holographics.values().forEach(holo -> {
			if (!holo.isRemoved()) {
				holo.display(player);
			}
		});
	}

	public void onHangingBreak(HangingBreakEvent e) {
		if (!Config.graffiti_enabled) {
			return;
		}
		Hanging hanging = e.getEntity();
		if (!(e.getEntity() instanceof ItemFrame)) {
			return;
		}
		if (!game.getRegion().isInRegion(hanging.getLocation())) {
			return;
		}
		e.setCancelled(true);
		ItemFrame frame = (ItemFrame) hanging;
		if (isDraw(frame)) {
			return;
		}
		if (!(e instanceof HangingBreakByEntityEvent)) {
			return;
		}
		HangingBreakByEntityEvent event = (HangingBreakByEntityEvent) e;
		Entity remover = event.getRemover();
		if (!(remover instanceof Player)) {
			return;
		}
		Player player = (Player) remover;
		if (!game.isInGame(player) || BedwarsUtil.isSpectator(game, player) || player.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}
		drawImage(frame);
	}

	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (!Config.graffiti_enabled) {
			return;
		}
		Player player = e.getPlayer();
		if (!game.isInGame(player) || BedwarsUtil.isSpectator(game, player) || player.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}
		if (!(e.getRightClicked() instanceof ItemFrame)) {
			return;
		}
		e.setCancelled(true);
		ItemFrame frame = (ItemFrame) e.getRightClicked();
		if (!game.getRegion().isInRegion(frame.getLocation())) {
			return;
		}
		if (isDraw(frame)) {
			return;
		}
		drawImage(frame);
	}

	private void drawImage(ItemFrame frame) {
		frame.setItem(new ItemStack(Material.MAP, 1, getRandomMap().getId()));
		itemFrames.add(frame);
		if (holographics.containsKey(frame)) {
			holographics.get(frame).remove();
		}
		Location loc1 = frame.getLocation().getBlock().getLocation();
		Location loc2 = frame.getLocation().getBlock().getRelative(frame.getAttachedFace().getOppositeFace()).getLocation();
		Location loc = loc1.add(loc2.subtract(loc1).multiply(0.5)).add(0.5, 0.5, 0.5);
		for (int i = 0; i < 4; i++) {
			sendParticles(loc.clone().add(getRandomNumber(), getRandomNumber() * 4, getRandomNumber()), 1f, 0, Color.GRAY, false);
		}
		sendParticles(loc.clone().add(getRandomNumber(), getRandomNumber() * 4, getRandomNumber()), 1.5f, 1, Color.YELLOW, false);
	}

	private MapView getRandomMap() {
		if (Config.image_maps.size() > 0) {
			Random random = new Random();
			int n = random.nextInt(Config.image_maps.size());
			return Config.image_maps.get(n);
		}
		MapView map = Bukkit.createMap(game.getRegion().getWorld());
		map.setCenterX(Integer.MAX_VALUE);
		map.setCenterZ(Integer.MAX_VALUE);
		return map;
	}

	private double getRandomNumber() {
		return (Math.random() - Math.random()) * 0.125;
	}

	private boolean isDraw(ItemFrame frame) {
		ItemStack item = frame.getItem();
		return item != null && item.getType().equals(Material.MAP);
	}

	private void sendParticles(Location location, float depth, int data, Color color, boolean isLong) {
		ProtocolManager man = ProtocolLibrary.getProtocolManager();
		PacketContainer packet = man.createPacket(PacketType.Play.Server.WORLD_PARTICLES);
		packet.getParticles().write(0, Particle.SPELL_MOB);
		packet.getFloat().write(0, (float) location.getX());
		packet.getFloat().write(1, (float) location.getY());
		packet.getFloat().write(2, (float) location.getZ());
		packet.getFloat().write(3, (float) color.getRed() / 255F);
		packet.getFloat().write(4, (float) color.getGreen() / 255F);
		packet.getFloat().write(5, (float) color.getBlue() / 255F);
		packet.getFloat().write(6, depth);
		packet.getIntegers().write(0, data);
		packet.getBooleans().write(0, isLong);
		game.getPlayers().forEach(p -> {
			try {
				man.sendServerPacket(p, packet);
			} catch (Exception e) {
			}
		});
	}
}
