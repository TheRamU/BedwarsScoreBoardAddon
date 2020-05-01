package me.ram.bedwarsscoreboardaddon.addon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;

public class LobbyBlock {

	private Game game;
	private Map<Block, BlockStorage> lobbyblocks = new HashMap<Block, BlockStorage>();

	public LobbyBlock(Game game) {
		this.game = game;
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
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game.getState() != GameState.RUNNING) {
					cancel();
					recovery();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 0L);
	}

	public void recovery() {
		for (Block block : lobbyblocks.keySet()) {
			game.getRegion().removePlacedBlock(block);
			lobbyblocks.get(block).getBlock(block);
		}
		lobbyblocks.clear();
	}

	public Game getGame() {
		return game;
	}

	private void removeBlock(Location loc) {
		Location location = loc.clone();
		Location location1 = location.clone().add(Config.lobby_block_position_1_x, Config.lobby_block_position_1_y,
				Config.lobby_block_position_1_z);
		Location location2 = location.clone().add(Config.lobby_block_position_2_x, Config.lobby_block_position_2_y,
				Config.lobby_block_position_2_z);
		for (int X : this.getAllNumber((int) location1.getX(), (int) location2.getX())) {
			location.setX(X);
			for (int Y : this.getAllNumber((int) location1.getY(), (int) location2.getY())) {
				location.setY(Y);
				for (int Z : this.getAllNumber((int) location1.getZ(), (int) location2.getZ())) {
					location.setZ(Z);
					Block block = location.getBlock();
					if (block != null && !block.getType().equals(Material.AIR)) {
						lobbyblocks.put(block, new BlockStorage(block));
						block.setType(Material.AIR);
					}
				}
			}
		}
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

	private class BlockStorage {
		private Material type;
		private byte data;
		private MaterialData materialData;

		private BlockStorage(Block block) {
			type = block.getType();
			data = block.getData();
			materialData = block.getState().getData();
		}

		private Block getBlock(Block block) {
			block.setType(type);
			block.setData(data);
			block.getState().setData(materialData);
			return block;
		}
	}
}
