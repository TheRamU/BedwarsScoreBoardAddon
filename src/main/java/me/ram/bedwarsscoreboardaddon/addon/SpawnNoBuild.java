package me.ram.bedwarsscoreboardaddon.addon;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.config.Config;

public class SpawnNoBuild implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent e) {
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
		if (game == null || !Config.spawn_no_build_enabled) {
			return;
		}
		if (game.getState() == GameState.RUNNING) {
			Block block = e.getBlock();
			Player player = e.getPlayer();
			for (Team team : game.getTeams().values()) {
				if (team.getSpawnLocation()
						.distance(block.getLocation().clone().add(0.5, 0, 0.5)) <= Config.spawn_no_build_spawn_range) {
					e.setCancelled(true);
					player.sendMessage(Config.spawn_no_build_message);
					return;
				}
			}
			for (ResourceSpawner spawner : game.getResourceSpawners()) {
				if (spawner.getLocation().distance(
						block.getLocation().clone().add(0.5, 0, 0.5)) <= Config.spawn_no_build_resource_range) {
					e.setCancelled(true);
					player.sendMessage(Config.spawn_no_build_message);
					return;
				}
			}
		}
	}
}
