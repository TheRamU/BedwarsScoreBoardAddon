package me.ram.bedwarsscoreboardaddon.addon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.api.HolographicAPI;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;

public class Holographic {

	private Game game;
	private List<HolographicAPI> ablocks;
	private List<HolographicAPI> atitles;
	private List<HolographicAPI> btitles;
	private Map<String, HolographicAPI> pbtitles;
	private ResourceUpgrade resourceupgrade;
	private HashMap<HolographicAPI, Location> armorloc = new HashMap<HolographicAPI, Location>();
	private HashMap<HolographicAPI, Boolean> armorupward = new HashMap<HolographicAPI, Boolean>();
	private HashMap<HolographicAPI, Integer> armoralgebra = new HashMap<HolographicAPI, Integer>();

	public Holographic(Game game, ResourceUpgrade resourceupgrade) {
		this.game = game;
		ablocks = new ArrayList<HolographicAPI>();
		atitles = new ArrayList<HolographicAPI>();
		btitles = new ArrayList<HolographicAPI>();
		pbtitles = new HashMap<String, HolographicAPI>();
		this.resourceupgrade = resourceupgrade;
		if (Config.holographic_resource_enabled) {
			for (String r : Config.holographic_resource) {
				this.setArmorStand(game, r);
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Team team : game.getTeams().values()) {
					Location location = team.getTargetHeadBlock().clone().add(0.5, 0, 0.5);
					HolographicAPI holo = new HolographicAPI(location.clone().add(0, -1.25, 0),
							Config.holographic_bedtitle_bed_alive);
					for (Player player : team.getPlayers()) {
						holo.display(player);
					}
					pbtitles.put(team.getName(), holo);
					new BukkitRunnable() {
						@Override
						public void run() {
							if (game.getState() != GameState.RUNNING || team.isDead(game)) {
								cancel();
								holo.remove();
							}
						}
					}.runTaskTimer(Main.getInstance(), 1L, 1L);
				}
			}
		}.runTaskLater(Main.getInstance(), 20L);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game.getState() != GameState.RUNNING || game.getPlayers().size() < 1) {
					this.cancel();
					for (HolographicAPI holo : ablocks) {
						holo.remove();
					}
					for (HolographicAPI holo : atitles) {
						holo.remove();
					}
					for (HolographicAPI holo : btitles) {
						holo.remove();
					}
					for (HolographicAPI holo : pbtitles.values()) {
						holo.remove();
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 1L, 1L);
	}

	public Game getGame() {
		return game;
	}

	public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent e) {
		if (Config.holographic_bed_title_enabled) {
			Team team = e.getTeam();
			Game game = e.getGame();
			if (pbtitles.containsKey(team.getName())) {
				pbtitles.get(team.getName()).remove();
			}
			Location loc = team.getTargetHeadBlock().clone().add(0, -1, 0);
			if (loc.getX() == loc.getBlock().getLocation().getX()) {
				loc.add(0.5, 0, 0);
			}
			if (loc.getZ() == loc.getBlock().getLocation().getZ()) {
				loc.add(0, 0, 0.5);
			}
			if (!loc.getBlock().getChunk().isLoaded()) {
				loc.getBlock().getChunk().load(true);
			}
			HolographicAPI holo = new HolographicAPI(loc, Config.holographic_bedtitle_bed_destroyed.replace("{player}",
					game.getPlayerTeam(e.getPlayer()).getChatColor() + e.getPlayer().getName()));
			btitles.add(holo);
			for (Player player : game.getPlayers()) {
				holo.display(player);
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					if (game.getState() == GameState.RUNNING) {
						if (!holo.getLocation().getBlock().getChunk().isLoaded()) {
							holo.getLocation().getBlock().getChunk().load(true);
						}
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), 0L, 0L);
		}
	}

	private void setArmorStand(Game game, String res) {
		for (ResourceSpawner spawner : game.getResourceSpawners()) {
			for (ItemStack itemStack : spawner.getResources()) {
				if (itemStack.getType() == Material.getMaterial(
						Main.getInstance().getConfig().getInt("holographic.resource.resources." + res + ".item"))) {
					if (!spawner.getLocation().getBlock().getChunk().isLoaded()) {
						spawner.getLocation().getBlock().getChunk().load(true);
					}
					HolographicAPI holo = new HolographicAPI(spawner.getLocation().clone().add(0, Main.getInstance()
							.getConfig().getDouble("holographic.resource.resources." + res + ".height"), 0), null);
					holo.setEquipment(Arrays.asList(new ItemStack(Material.getMaterial(Main.getInstance().getConfig()
							.getInt("holographic.resource.resources." + res + ".block")))));
					new BukkitRunnable() {
						@Override
						public void run() {
							for (Player player : game.getPlayers()) {
								holo.display(player);
							}
						}
					}.runTaskLater(Main.getInstance(), 20L);
					ArrayList<String> titles = new ArrayList<String>();
					for (String title : Main.getInstance().getConfig()
							.getStringList("holographic.resource.resources." + res + ".title")) {
						titles.add(ColorUtil.color(title));
					}
					this.setArmorStandRun(game, holo, titles, itemStack);
				}
			}
		}
	}

	private void setArmorStandRun(Game game, HolographicAPI holo, ArrayList<String> titles, ItemStack itemStack) {
		Collections.reverse(titles);
		Location aslocation = holo.getLocation().clone().add(0, 0.35, 0);
		for (String title : titles) {
			this.setTitle(game, aslocation, title, itemStack);
			aslocation.add(0, 0.4, 0);
		}
		ablocks.add(holo);
		new BukkitRunnable() {
			double y = holo.getLocation().getY();

			@Override
			public void run() {
				if (game.getState() == GameState.RUNNING) {
					if (!holo.getLocation().getBlock().getChunk().isLoaded()) {
						holo.getLocation().getBlock().getChunk().load(true);
					}
					moveArmorStand(holo, y, game);
				} else {
					armorloc.remove(holo);
					armorupward.remove(holo);
					armoralgebra.remove(holo);
					holo.remove();
					ablocks.remove(holo);
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 1L, 1L);
	}

	private void setTitle(Game game, Location location, String title, ItemStack itemStack) {
		if (!location.getBlock().getChunk().isLoaded()) {
			location.getBlock().getChunk().load(true);
		}
		HolographicAPI holo = new HolographicAPI(location, " ");
		atitles.add(holo);
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : game.getPlayers()) {
					holo.display(player);
				}
			}
		}.runTaskLater(Main.getInstance(), 20L);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game.getState() == GameState.RUNNING) {
					if (!holo.getLocation().getBlock().getChunk().isLoaded()) {
						holo.getLocation().getBlock().getChunk().load(true);
					}
					String customName = title;
					customName = customName.replace("{level}", resourceupgrade.getLevel().get(itemStack.getType()));
					for (Material sitem : resourceupgrade.getSpawnTime().keySet()) {
						if (itemStack.getType() == sitem) {
							customName = customName.replace("{generate_time}",
									resourceupgrade.getSpawnTime().get(sitem) + "");
						}
					}
					holo.setTitle(customName);
				} else {
					holo.remove();
					atitles.remove(holo);
					cancel();
					return;
				}
			}
		}.runTaskTimer(Main.getInstance(), 5L, 5L);
	}

	public void onPlayerLeave(Player player) {
		if (player.isOnline()) {
			for (HolographicAPI holo : pbtitles.values()) {
				holo.destroy(player);
			}
		}
	}

	public void onPlayerJoin(Player player) {
		if (game.getState() != GameState.RUNNING) {
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game.getState() == GameState.RUNNING && player.isOnline() && game.getPlayers().contains(player)) {
					for (HolographicAPI holo : ablocks) {
						holo.display(player);
					}
					for (HolographicAPI holo : atitles) {
						holo.display(player);
					}
					for (HolographicAPI holo : btitles) {
						holo.display(player);
					}
					Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
					if (arena.getRejoin().getPlayers().containsKey(player.getName())) {
						Team team = game.getPlayerTeam(player);
						if (team != null && !team.isDead(game)) {
							pbtitles.get(team.getName()).display(player);
						}
					}
				}
			}
		}.runTaskLater(Main.getInstance(), 10L);
	}

	public void remove() {
		for (HolographicAPI holo : ablocks) {
			holo.remove();
		}
		for (HolographicAPI holo : atitles) {
			holo.remove();
		}
		for (HolographicAPI holo : btitles) {
			holo.remove();
		}
		for (HolographicAPI holo : pbtitles.values()) {
			holo.remove();
		}
	}

	public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
		Player player = e.getPlayer();
		Game getGame = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (getGame == null) {
			return;
		}
		if (!getGame.getPlayers().contains(player)) {
			return;
		}
		if (getGame.getState() != GameState.WAITING && getGame.getState() == GameState.RUNNING) {
			e.setCancelled(true);
		}
	}

	private void moveArmorStand(HolographicAPI holo, double height, Game game) {
		if (!armorloc.containsKey(holo)) {
			armorloc.put(holo, holo.getLocation().clone());
		}
		if (!armorupward.containsKey(holo)) {
			armorupward.put(holo, true);
		}
		if (!armoralgebra.containsKey(holo)) {
			armoralgebra.put(holo, 0);
		}
		armoralgebra.put(holo, armoralgebra.get(holo) + 1);
		Location location = armorloc.get(holo);
		if (location.getY() >= height + 0.30) {
			armoralgebra.put(holo, 0);
			armorupward.put(holo, false);
		} else if (location.getY() <= height - 0.30) {
			armoralgebra.put(holo, 0);
			armorupward.put(holo, true);
		}
		Integer algebra = armoralgebra.get(holo);
		if (39 > algebra) {
			if (armorupward.get(holo)) {
				location.setY(location.getY() + 0.015);
			} else {
				location.setY(location.getY() - 0.015);
			}
		} else if (algebra >= 50) {
			armoralgebra.put(holo, 0);
			armorupward.put(holo, !armorupward.get(holo));
		}
		Float turn = 1f;
		if (!armorupward.get(holo)) {
			turn = -turn;
		}
		Float changeyaw = (float) 0;
		if (algebra == 1 || algebra == 40) {
			changeyaw += 2f * turn;
		} else if (algebra == 2 || algebra == 39) {
			changeyaw += 3f * turn;
		} else if (algebra == 3 || algebra == 38) {
			changeyaw += 4f * turn;
		} else if (algebra == 4 || algebra == 37) {
			changeyaw += 5f * turn;
		} else if (algebra == 5 || algebra == 36) {
			changeyaw += 6f * turn;
		} else if (algebra == 6 || algebra == 35) {
			changeyaw += 7f * turn;
		} else if (algebra == 7 || algebra == 34) {
			changeyaw += 8f * turn;
		} else if (algebra == 8 || algebra == 33) {
			changeyaw += 9f * turn;
		} else if (algebra == 9 || algebra == 32) {
			changeyaw += 10f * turn;
		} else if (algebra == 10 || algebra == 31) {
			changeyaw += 11f * turn;
		} else if (algebra == 11 || algebra == 30) {
			changeyaw += 11f * turn;
		} else if (algebra == 12 || algebra == 29) {
			changeyaw += 12f * turn;
		} else if (algebra == 13 || algebra == 28) {
			changeyaw += 12f * turn;
		} else if (algebra == 14 || algebra == 27) {
			changeyaw += 13f * turn;
		} else if (algebra == 15 || algebra == 26) {
			changeyaw += 13f * turn;
		} else if (algebra == 16 || algebra == 25) {
			changeyaw += 14f * turn;
		} else if (algebra == 17 || algebra == 24) {
			changeyaw += 14f * turn;
		} else if (algebra == 18 || algebra == 23) {
			changeyaw += 15f * turn;
		} else if (algebra == 19 || algebra == 22) {
			changeyaw += 15f * turn;
		} else if (algebra == 20 || algebra == 21) {
			changeyaw += 16f * turn;
		} else if (algebra == 41) {
			changeyaw += 2f * turn;
		} else if (algebra == 42) {
			changeyaw += 2f * turn;
		} else if (algebra == 43) {
			changeyaw += 2f * turn;
		} else if (algebra == 44) {
			changeyaw += 1f * turn;
		} else if (algebra == 45) {
			changeyaw += -1f * turn;
		} else if (algebra == 46) {
			changeyaw += -1f * turn;
		} else if (algebra == 47) {
			changeyaw += -2f * turn;
		} else if (algebra == 48) {
			changeyaw += -2f * turn;
		} else if (algebra == 49) {
			changeyaw += -2f * turn;
		} else if (algebra == 50) {
			changeyaw += -2f * turn;
		}
		double yaw = location.getYaw();
		yaw += (changeyaw * Config.holographic_resource_speed);
		yaw = yaw > 360 ? (yaw - 360) : yaw;
		yaw = yaw < -360 ? (yaw + 360) : yaw;
		location.setYaw((float) yaw);
		holo.teleport(location);
	}
}
