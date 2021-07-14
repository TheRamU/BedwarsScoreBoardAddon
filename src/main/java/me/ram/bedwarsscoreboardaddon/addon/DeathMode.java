package me.ram.bedwarsscoreboardaddon.addon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonDeathModeEvent;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class DeathMode {

	@Getter
	private Game game;
	@Getter
	private Arena arena;
	private String deathmode_time;

	public String getDeathmodeTime() {
		return deathmode_time;
	}

	public DeathMode(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		arena.addGameTask(new BukkitRunnable() {
			Boolean isExecuted = false;

			@Override
			public void run() {
				int deathmodetime = game.getTimeLeft() - Config.deathmode_gametime;
				deathmode_time = deathmodetime / 60 + ":" + ((deathmodetime % 60 < 10) ? ("0" + deathmodetime % 60) : (deathmodetime % 60));
				if (Config.deathmode_enabled) {
					if (isExecuted) {
						cancel();
						return;
					}
					if (game.getTimeLeft() <= Config.deathmode_gametime) {
						isExecuted = true;
						BoardAddonDeathModeEvent deathModeEvent = new BoardAddonDeathModeEvent(game);
						Bukkit.getPluginManager().callEvent(deathModeEvent);
						if (deathModeEvent.isCancelled()) {
							cancel();
							return;
						}
						for (Player player : game.getPlayers()) {
							if (!Config.deathmode_title.equals("") || !Config.deathmode_subtitle.equals("")) {
								Utils.sendTitle(player, 10, 80, 10, Config.deathmode_title, Config.deathmode_subtitle);
							}
							if (!Config.deathmode_message.equals("")) {
								player.sendMessage(Config.deathmode_message);
							}
						}
						for (Team team : game.getTeams().values()) {
							destroyBlock(game, team);
						}
						PlaySound.playSound(game, Config.play_sound_sound_deathmode);
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 21L));
	}

	private void destroyBlock(Game game, Team team) {
		Material type = team.getTargetHeadBlock().getBlock().getType();
		if (type.equals(game.getTargetMaterial())) {
			if (type.equals(Material.BED_BLOCK)) {
				if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
					team.getTargetFeetBlock().getBlock().setType(Material.AIR);
				} else {
					team.getTargetHeadBlock().getBlock().setType(Material.AIR);
				}
			} else {
				team.getTargetHeadBlock().getBlock().setType(Material.AIR);
			}
		}
	}
}
