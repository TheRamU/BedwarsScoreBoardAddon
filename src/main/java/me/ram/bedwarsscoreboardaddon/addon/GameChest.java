package me.ram.bedwarsscoreboardaddon.addon;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;

public class GameChest {

	@Getter
	private Game game;
	@Getter
	private Arena arena;
	@Getter
	private Map<Team, Block> teamChests;

	public GameChest(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		teamChests = new HashMap<Team, Block>();
		game.getPlayers().forEach(player -> {
			player.getEnderChest().clear();
		});
	}

	public Team getChestTeam(Block block) {
		for (Team team : teamChests.keySet()) {
			if (block.equals(teamChests.get(team))) {
				return team;
			}
		}
		return null;
	}

	public void clearChest() {
		game.getPlayers().forEach(player -> {
			player.getEnderChest().clear();
		});
		teamChests.values().forEach(block -> {
			if (block.getType().equals(Material.CHEST)) {
				((Chest) block.getState()).getInventory().clear();
			}
		});
	}

	public void onPlayerLeave(Player player) {
		player.getEnderChest().clear();
	}

	public void onInteract(PlayerInteractEvent e) {
		if (!Config.game_chest_enabled) {
			return;
		}
		if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		Player player = e.getPlayer();
		if (!game.isInGame(player) || BedwarsUtil.isSpectator(game, player)) {
			return;
		}
		Block block = e.getClickedBlock();
		if (!block.getType().equals(Material.CHEST) && !block.getType().equals(Material.ENDER_CHEST)) {
			return;
		}
		e.setCancelled(true);
		block.getState().update();
		Team team = game.getPlayerTeam(player);
		Location location = block.getLocation().add(0.5, 0, 0.5);
		if (block.getType().equals(Material.ENDER_CHEST)) {
			e.setCancelled(false);
		} else if (block.getType().equals(Material.CHEST) && !game.getRegion().isPlacedBlock(block)) {
			for (Team t : game.getTeams().values()) {
				if (!teamChests.containsValue(block) && !teamChests.containsKey(t) && t.getSpawnLocation().distanceSquared(location) <= Math.pow(Config.game_chest_range, 2)) {
					((Chest) block.getState()).getInventory().clear();
					teamChests.put(t, block);
				}
			}
			Team t = getChestTeam(block);
			if (t == null) {
				return;
			}
			if (t.getName().equals(team.getName())) {
				e.setCancelled(false);
				return;
			}
			if (!BedwarsUtil.isDieOut(game, t)) {
				player.sendMessage(Config.game_chest_message.replace("{team}", t.getName()));
				return;
			}
			e.setCancelled(false);
		}
	}
}
