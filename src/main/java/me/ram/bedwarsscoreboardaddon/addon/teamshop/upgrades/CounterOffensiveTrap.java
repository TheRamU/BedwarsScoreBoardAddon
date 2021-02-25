package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import lombok.Setter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.addon.teamshop.TeamShop;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;

public class CounterOffensiveTrap implements Upgrade {

	@Getter
	private Game game;
	@Getter
	private Team team;
	@Getter
	@Setter
	private int level;
	@Getter
	@Setter
	private String buyer;

	public CounterOffensiveTrap(Game game, Team team, int level) {
		this.game = game;
		this.team = team;
		this.level = level;
	}

	public UpgradeType getType() {
		return UpgradeType.COUNTER_OFFENSIVE_TRAP;
	}

	public String getName() {
		return Config.teamshop_upgrade_name.get(getType());
	}

	public void runUpgrade() {
		if (level < 1) {
			return;
		}
		TeamShop teamShop = Main.getInstance().getArenaManager().getArena(game.getName()).getTeamShop();
		for (Player player : game.getPlayers()) {
			if (BedwarsUtil.isSpectator(game, player) || player.getGameMode() == GameMode.SPECTATOR) {
				continue;
			}
			if (team != game.getPlayerTeam(player) && team.getTargetFeetBlock().distanceSquared(player.getLocation()) <= Math.pow(Config.teamshop_upgrade_counter_offensive_trap_trigger_range, 2)) {
				if (teamShop.isCoolingPlayer(team, player) || teamShop.isImmunePlayer(player)) {
					continue;
				}
				level = 0;
				teamShop.removeTrap(this);
				teamShop.addCoolingPlayer(team, player);
				addEffect();
				if (team.getPlayers().size() > 0) {
					Main.getInstance().getArenaManager().getArena(game.getName()).getTeamShop().updateTeamShop(team.getPlayers().get(0));
				}
				break;
			}
		}
	}

	private void addEffect() {
		for (Player player : team.getPlayers()) {
			if (!player.getGameMode().equals(GameMode.SPECTATOR) && !BedwarsUtil.isSpectator(game, player) && team.getTargetFeetBlock().distance(player.getLocation()) <= Config.teamshop_upgrade_counter_offensive_trap_effect_range) {
				addPlayerEffect(player, PotionEffectType.SPEED, 200, 0);
				addPlayerEffect(player, PotionEffectType.JUMP, 200, 1);
			}
		}
	}

	private void addPlayerEffect(Player player, PotionEffectType type, int duration, int amplifier) {
		boolean add = true;
		if (player.hasPotionEffect(type)) {
			PotionEffect effect = getPotionEffect(player, type);
			int level = effect.getAmplifier();
			if (level < amplifier) {
				add = true;
			} else if (level == amplifier && effect.getDuration() < duration) {
				add = true;
			} else {
				add = false;
			}
		}
		if (add) {
			player.addPotionEffect(new PotionEffect(type, duration, amplifier), true);
		}
	}

	private PotionEffect getPotionEffect(Player player, PotionEffectType type) {
		try {
			return player.getPotionEffect(type);
		} catch (Exception e) {
		}
		for (PotionEffect effect : player.getActivePotionEffects()) {
			if (effect.getType().equals(type)) {
				return effect;
			}
		}
		return null;
	}
}
