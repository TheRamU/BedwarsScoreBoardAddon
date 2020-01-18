package me.ram.bedwarsscoreboardaddon.addon;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.events.BedwarsPlayerJoinedEvent;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.Utils;

public class Title implements Listener {

	private Map<String, Integer> Times = new HashMap<String, Integer>();

	@EventHandler
	public void start(BedwarsGameStartedEvent e) {
		Game game = e.getGame();
		Times.put(e.getGame().getName(), e.getGame().getTimeLeft());
		if (Config.start_title_enabled) {
			for (Player player : e.getGame().getPlayers()) {
				Utils.clearTitle(player);
			}
			int delay = game.getRegion().getWorld().getName().equals(game.getLobby().getWorld().getName()) ? 5 : 30;
			new BukkitRunnable() {
				int rn = 0;

				@Override
				public void run() {
					if (rn < Config.start_title_title.size()) {
						for (Player player : e.getGame().getPlayers()) {
							Utils.sendTitle(player, 0, 80, 5, Config.start_title_title.get(rn),
									Config.start_title_subtitle);
						}
						rn++;
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), delay, 0L);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				PlaySound.playSound(e.getGame(), Config.play_sound_sound_start);
			}
		}.runTaskLater(Main.getInstance(), 30L);
	}

	@EventHandler
	public void onDestroyed(BedwarsTargetBlockDestroyedEvent e) {
		if (Config.destroyed_title_enabled) {
			for (Player player : e.getTeam().getPlayers()) {
				Utils.sendTitle(player, 1, 30, 1, Config.destroyed_title_title, Config.destroyed_title_subtitle);
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (getGame == null) {
			return;
		}
		if (Config.die_out_title_enabled) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (getGame.getState() == GameState.RUNNING) {
						if (getGame.isSpectator(player)) {
							Utils.sendTitle(player, 1, 80, 5, Config.die_out_title_title,
									Config.die_out_title_subtitle);
						}
					}
				}
			}.runTaskLater(Main.getInstance(), 5L);
		}
	}

	@EventHandler
	public void onOver(BedwarsGameOverEvent e) {
		if (Config.victory_title_enabled) {
			Team team = e.getWinner();
			int time = Times.getOrDefault(e.getGame().getName(), 3600) - e.getGame().getTimeLeft();
			String formattime = time / 60 + ":" + ((time % 60 < 10) ? ("0" + time % 60) : (time % 60));
			new BukkitRunnable() {
				@Override
				public void run() {
					if (team != null && team.getPlayers() != null) {
						for (Player player : team.getPlayers()) {
							if (player.isOnline()) {
								Utils.clearTitle(player);
							}
						}
					}
				}
			}.runTaskLater(Main.getInstance(), 1L);
			new BukkitRunnable() {
				int rn = 0;

				@Override
				public void run() {
					if (rn < Config.victory_title_title.size()) {
						if (team != null && team.getPlayers() != null) {
							for (Player player : team.getPlayers()) {
								if (player.isOnline()) {
									Utils.sendTitle(player, 0, 80, 5,
											Config.victory_title_title.get(rn).replace("{time}", formattime)
													.replace("{color}", team.getChatColor() + "")
													.replace("{team}", team.getName()),
											Config.victory_title_subtitle.replace("{time}", formattime)
													.replace("{color}", team.getChatColor() + "")
													.replace("{team}", team.getName()));
								}
							}
							rn++;
						} else {
							this.cancel();
						}
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), 40L, 0L);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				PlaySound.playSound(e.getGame(), Config.play_sound_sound_over);
			}
		}.runTaskLater(Main.getInstance(), 40L);
	}

	@EventHandler
	public void Join(BedwarsPlayerJoinedEvent e) {
		for (Player player : e.getGame().getPlayers()) {
			if (player.getName().contains(",") || player.getName().contains("[") || player.getName().contains("]")) {
				player.kickPlayer("");
			}
			if (!(e.getGame().getState() != GameState.WAITING && e.getGame().getState() == GameState.RUNNING)) {
				if (Config.jointitle_enabled) {
					Utils.sendTitle(player, e.getPlayer(), 5, 50, 5,
							Config.jointitle_title.replace("{player}", e.getPlayer().getName()),
							Config.jointitle_subtitle.replace("{player}", e.getPlayer().getName()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void DamageTitle(EntityDamageByEntityEvent e) {
		if (e.isCancelled() || !(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
			return;
		}
		Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer((Player) e.getDamager());
		if (getGame == null) {
			return;
		}
		if (Config.damagetitle_enabled && getGame.getState() == GameState.RUNNING) {
			if (!(getGame.getPlayers().contains((Player) e.getDamager())
					&& getGame.getPlayers().contains((Player) e.getEntity()))) {
				return;
			}
			if (getGame.isSpectator((Player) e.getDamager()) || getGame.isSpectator((Player) e.getEntity())) {
				return;
			}
			Damageable dp = (Damageable) e.getEntity();
			Player victim = (Player) dp;
			DecimalFormat df = new DecimalFormat("0.00");
			DecimalFormat df2 = new DecimalFormat("#");
			if (e.isCancelled()) {
				return;
			}
			if (!Config.damagetitle_title.equals("") || !Config.damagetitle_subtitle.equals("")) {
				new BukkitRunnable() {
					@Override
					public void run() {
						Utils.sendTitle((Player) e.getDamager(), victim, 0, 20, 0,
								Config.damagetitle_title.replace("{player}", victim.getName())
										.replace("{damage}", df.format(e.getDamage()))
										.replace("{health}", df2.format(dp.getHealth()))
										.replace("{maxhealth}", df2.format(dp.getMaxHealth())),
								Config.damagetitle_subtitle.replace("{player}", victim.getName())
										.replace("{damage}", df.format(e.getDamage()))
										.replace("{health}", df2.format(dp.getHealth()))
										.replace("{maxhealth}", df2.format(dp.getMaxHealth())));
					}
				}.runTaskLater(Main.getInstance(), 0L);
			}
		}
	}

	@EventHandler
	public void BowDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Arrow)) {
			return;
		}
		Arrow a1 = (Arrow) e.getDamager();
		if (a1.getShooter() instanceof Player) {
			Player shooter = (Player) a1.getShooter();
			Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(shooter);
			if (getGame == null) {
				return;
			}
			if (Config.bowdamage_enabled && getGame.getState() != GameState.WAITING
					&& getGame.getState() == GameState.RUNNING) {
				Damageable dp = (Damageable) e.getEntity();
				if (dp instanceof Player) {
					Player victim = (Player) dp;
					Integer damage = (int) e.getFinalDamage();
					DecimalFormat df2 = new DecimalFormat("#");
					if (getGame.getPlayerTeam(shooter) == getGame.getPlayerTeam(victim)) {
						e.setCancelled(true);
					}
					if (e.isCancelled()) {
						return;
					}
					if (!dp.isDead()) {
						if (dp.getHealth() > 0) {
							new BukkitRunnable() {
								@Override
								public void run() {
									if (!Config.bowdamage_title.equals("") || !Config.bowdamage_subtitle.equals("")) {
										Utils.sendTitle(shooter, victim, 0, 20, 0,
												Config.bowdamage_title.replace("{player}", victim.getName())
														.replace("{damage}", damage + "")
														.replace("{health}", df2.format(dp.getHealth()))
														.replace("{maxhealth}", df2.format(dp.getMaxHealth())),
												Config.bowdamage_subtitle.replace("{player}", victim.getName())
														.replace("{damage}", damage + "")
														.replace("{health}", df2.format(dp.getHealth()))
														.replace("{maxhealth}", df2.format(dp.getMaxHealth())));
									}
									if (!Config.bowdamage_message.equals("")) {
										Utils.sendMessage(shooter, victim,
												Config.bowdamage_message.replace("{player}", victim.getName())
														.replace("{damage}", damage + "")
														.replace("{health}", df2.format(dp.getHealth()))
														.replace("{maxhealth}", df2.format(dp.getMaxHealth())));
									}
								}
							}.runTaskLater(Main.getInstance(), 0L);
						}
					}
				}
			}
		}
	}
}
