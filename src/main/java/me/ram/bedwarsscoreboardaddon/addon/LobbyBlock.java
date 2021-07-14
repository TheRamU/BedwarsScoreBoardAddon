package me.ram.bedwarsscoreboardaddon.addon;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bedwarsrel.game.Game;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;

public class LobbyBlock {

	@Getter
	private Game game;
	@Getter
	private Arena arena;

	public LobbyBlock(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		Location lobby = game.getLobby().clone();
		if (!Config.lobby_block_enabled) {
			return;
		}
		Block block = lobby.getBlock();
		if (block == null || block.getType().equals(Material.AIR)) {
			block = lobby.clone().add(0, -1, 0).getBlock();
		}
		if (block == null || block.getType().equals(Material.AIR)) {
			return;
		}
		removeBlock(block.getLocation());
	}

	private void removeBlock(Location loc) {
		List<Block> list = new ArrayList<Block>();
		Location location = loc.clone();
		Location location1 = location.clone().add(Config.lobby_block_position_1_x, Config.lobby_block_position_1_y, Config.lobby_block_position_1_z);
		Location location2 = location.clone().add(Config.lobby_block_position_2_x, Config.lobby_block_position_2_y, Config.lobby_block_position_2_z);
		for (int X : this.getAllNumber((int) location1.getX(), (int) location2.getX())) {
			location.setX(X);
			for (int Y : this.getAllNumber((int) location1.getY(), (int) location2.getY())) {
				location.setY(Y);
				for (int Z : this.getAllNumber((int) location1.getZ(), (int) location2.getZ())) {
					location.setZ(Z);
					Block block = location.getBlock();
					if (block != null && !block.getType().equals(Material.AIR)) {
						list.add(block);
					}
				}
			}
		}
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			for (Block b : list) {
				for (Player p : game.getPlayers()) {
					p.sendBlockChange(b.getLocation(), Material.AIR, (byte) 0);
				}
			}
		});
		arena.addGameTask(new BukkitRunnable() {
			int i = 0;

			@Override
			public void run() {
				for (int j = 0; j < 50; j++) {
					if (i >= list.size()) {
						cancel();
						return;
					}
					Block block = list.get(i);
					Chunk chunk = block.getChunk();
					if (!chunk.isLoaded()) {
						chunk.load(true);
					}
					game.getRegion().addBreakedBlock(block);
					block.setType(Material.AIR);
					i++;
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 0));
	}

	private List<Integer> getAllNumber(int a, int b) {
		List<Integer> nums = new ArrayList<Integer>();
		int min = a;
		int max = b;
		if (a > b) {
			min = b;
			max = a;
		}
		for (int i = min; i < max + 1; i++) {
			nums.add(i);
		}
		return nums;
	}
}
