package me.ram.bedwarsscoreboardaddon.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsOpenShopEvent;
import io.github.bedwarsrel.events.BedwarsPlayerKilledEvent;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.addon.Actionbar;
import me.ram.bedwarsscoreboardaddon.addon.DeathMode;
import me.ram.bedwarsscoreboardaddon.addon.GameChest;
import me.ram.bedwarsscoreboardaddon.addon.Graffiti;
import me.ram.bedwarsscoreboardaddon.addon.HealthLevel;
import me.ram.bedwarsscoreboardaddon.addon.Holographic;
import me.ram.bedwarsscoreboardaddon.addon.InvisibilityPlayer;
import me.ram.bedwarsscoreboardaddon.addon.LobbyBlock;
import me.ram.bedwarsscoreboardaddon.addon.NoBreakBed;
import me.ram.bedwarsscoreboardaddon.addon.PlaySound;
import me.ram.bedwarsscoreboardaddon.addon.Rejoin;
import me.ram.bedwarsscoreboardaddon.addon.ResourceUpgrade;
import me.ram.bedwarsscoreboardaddon.addon.Respawn;
import me.ram.bedwarsscoreboardaddon.addon.ScoreBoard;
import me.ram.bedwarsscoreboardaddon.addon.Shop;
import me.ram.bedwarsscoreboardaddon.addon.TimeTask;
import me.ram.bedwarsscoreboardaddon.addon.teamshop.TeamShop;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.storage.PlayerGameStorage;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import me.ram.bedwarsscoreboardaddon.utils.PlaceholderAPIUtil;

public class Arena {

	@Getter
	private Game game;
	@Getter
	private ScoreBoard scoreBoard;
	@Getter
	private PlayerGameStorage playerGameStorage;
	@Getter
	private DeathMode deathMode;
	@Getter
	private HealthLevel healthLevel;
	@Getter
	private NoBreakBed noBreakBed;
	@Getter
	private ResourceUpgrade resourceUpgrade;
	@Getter
	private Holographic holographic;
	@Getter
	private TeamShop teamShop;
	@Getter
	private InvisibilityPlayer invisiblePlayer;
	@Getter
	private LobbyBlock lobbyBlock;
	@Getter
	private Respawn respawn;
	@Getter
	private Actionbar actionbar;
	@Getter
	private Graffiti graffiti;
	@Getter
	private GameChest gameChest;
	@Getter
	private Rejoin rejoin;
	@Getter
	private Shop shop;
	@Getter
	private TimeTask timeTask;
	private Boolean isOver;
	private List<BukkitTask> gameTasks;

	public Arena(Game game) {
		Main.getInstance().getArenaManager().addArena(game.getName(), this);
		this.game = game;
		gameTasks = new ArrayList<BukkitTask>();
		playerGameStorage = new PlayerGameStorage(this);
		scoreBoard = new ScoreBoard(this);
		deathMode = new DeathMode(this);
		healthLevel = new HealthLevel(this);
		noBreakBed = new NoBreakBed(this);
		resourceUpgrade = new ResourceUpgrade(this);
		holographic = new Holographic(this, resourceUpgrade);
		teamShop = new TeamShop(this);
		invisiblePlayer = new InvisibilityPlayer(this);
		lobbyBlock = new LobbyBlock(this);
		respawn = new Respawn(this);
		actionbar = new Actionbar(this);
		graffiti = new Graffiti(this);
		gameChest = new GameChest(this);
		rejoin = new Rejoin(this);
		shop = new Shop(this);
		timeTask = new TimeTask(this);
		isOver = false;
		addGameTask(new BukkitRunnable() {
			@Override
			public void run() {
				if (!game.getState().equals(GameState.RUNNING)) {
					onOver(new BedwarsGameOverEvent(game, null));
					onEnd();
				}
			}
		}.runTaskTimer(Main.getInstance(), 1L, 1L));
	}

	public void addGameTask(BukkitTask task) {
		gameTasks.add(task);
	}

	public Boolean isOver() {
		return isOver;
	}

	public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent e) {
		if (!isAlivePlayer(e.getPlayer())) {
			return;
		}
		Map<String, Integer> beds = playerGameStorage.getPlayerBeds();
		Player player = e.getPlayer();
		if (beds.containsKey(player.getName())) {
			beds.put(player.getName(), beds.get(player.getName()) + 1);
		} else {
			beds.put(player.getName(), 1);
		}
		holographic.onTargetBlockDestroyed(e);
	}

	public void onDeath(Player player) {
		invisiblePlayer.removePlayer(player);
		if (!isGamePlayer(player)) {
			return;
		}
		Map<String, Integer> dies = playerGameStorage.getPlayerDies();
		if (dies.containsKey(player.getName())) {
			dies.put(player.getName(), dies.get(player.getName()) + 1);
		} else {
			dies.put(player.getName(), 1);
		}
		PlaySound.playSound(player, Config.play_sound_sound_death);
		teamShop.removeImmunePlayer(player);
	}

	public void onDamage(EntityDamageEvent e) {
		respawn.onDamage(e);
	}

	public void onInteractEntity(PlayerInteractEntityEvent e) {
		graffiti.onInteractEntity(e);
	}

	public void onInteract(PlayerInteractEvent e) {
		gameChest.onInteract(e);
	}

	public void onHangingBreak(HangingBreakEvent e) {
		graffiti.onHangingBreak(e);
	}

	public void onRespawn(Player player) {
		if (!isGamePlayer(player)) {
			return;
		}
		respawn.onRespawn(player, false);
	}

	public void onPlayerKilled(BedwarsPlayerKilledEvent e) {
		if (!isGamePlayer(e.getPlayer()) || !isGamePlayer(e.getKiller())) {
			return;
		}
		Player player = e.getPlayer();
		Player killer = e.getKiller();
		if (!game.getPlayers().contains(player) || !game.getPlayers().contains(killer) || game.isSpectator(player) || game.isSpectator(killer)) {
			return;
		}
		Map<String, Integer> totalkills = playerGameStorage.getPlayerTotalKills();
		Map<String, Integer> kills = playerGameStorage.getPlayerKills();
		Map<String, Integer> finalkills = playerGameStorage.getPlayerFinalKills();
		if (!game.getPlayerTeam(player).isDead(game)) {
			if (kills.containsKey(killer.getName())) {
				kills.put(killer.getName(), kills.get(killer.getName()) + 1);
			} else {
				kills.put(killer.getName(), 1);
			}
		}
		if (game.getPlayerTeam(player).isDead(game)) {
			if (finalkills.containsKey(killer.getName())) {
				finalkills.put(killer.getName(), finalkills.get(killer.getName()) + 1);
			} else {
				finalkills.put(killer.getName(), 1);
			}
		}
		if (totalkills.containsKey(killer.getName())) {
			totalkills.put(killer.getName(), totalkills.get(killer.getName()) + 1);
		} else {
			totalkills.put(killer.getName(), 1);
		}
		PlaySound.playSound(killer, Config.play_sound_sound_kill);
	}

	public void onOver(BedwarsGameOverEvent e) {
		if (e.getGame().getName().equals(this.game.getName())) {
			isOver = true;
			if (Config.overstats_enabled && e.getWinner() != null) {
				Team winner = e.getWinner();
				Map<String, Integer> totalkills = playerGameStorage.getPlayerTotalKills();
				Map<Integer, List<String>> player_kills = new HashMap<Integer, List<String>>();
				totalkills.forEach((name, kills) -> {
					List<String> players = player_kills.getOrDefault(kills, new ArrayList<String>());
					players.add(name);
					player_kills.put(kills, players);
				});
				List<Integer> kills_top = new ArrayList<Integer>();
				kills_top.addAll(player_kills.keySet());
				Collections.sort(kills_top);
				List<String> player_rank_name = new ArrayList<String>();
				List<Integer> player_rank_kills = new ArrayList<Integer>();
				for (Integer kills : kills_top) {
					for (String name : player_kills.get(kills)) {
						if (player_rank_name.size() < 3) {
							player_rank_name.add(name);
							player_rank_kills.add(kills);
						} else {
							break;
						}
					}
				}
				int size = player_rank_name.size();
				for (int i = 0; i < 3 - size; i++) {
					player_rank_name.add("none");
					player_rank_kills.add(0);
				}
				String win_team_player_list = "";
				for (Player player : winner.getPlayers()) {
					win_team_player_list += win_team_player_list.length() > 0 ? ", " + player.getName() : player.getName();
				}
				for (Player player : game.getPlayers()) {
					for (String msg : Config.overstats_message) {
						msg = PlaceholderAPIUtil.setPlaceholders(player, msg);
						player.sendMessage(msg.replace("{color}", winner.getChatColor() + "").replace("{win_team}", winner.getName()).replace("{win_team_players}", win_team_player_list).replace("{first_1_kills_player}", player_rank_name.get(0)).replace("{first_2_kills_player}", player_rank_name.get(1)).replace("{first_3_kills_player}", player_rank_name.get(2)).replace("{first_1_kills}", player_rank_kills.get(0) + "").replace("{first_2_kills}", player_rank_kills.get(1) + "").replace("{first_3_kills}", player_rank_kills.get(2) + ""));
					}
				}
			}
		}
	}

	public void onEnd() {
		gameTasks.forEach(task -> {
			task.cancel();
		});
		teamShop.onEnd();
		noBreakBed.onEnd();
		holographic.remove();
		shop.remove();
		graffiti.reset();
		gameChest.clearChest();
	}

	public void onDisable() {
		holographic.remove();
		shop.remove();
		graffiti.reset();
		gameChest.clearChest();
	}

	public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
		holographic.onArmorStandManipulate(e);
	}

	public void onClick(InventoryClickEvent e) {
		teamShop.onClick(e);
	}

	public void onItemMerge(ItemMergeEvent e) {
		if (!Config.item_merge && game.getRegion().isInRegion(e.getEntity().getLocation())) {
			e.setCancelled(true);
		}
	}

	public void onPlayerLeave(Player player) {
		holographic.onPlayerLeave(player);
		if (Config.rejoin_enabled) {
			if (game.getState() == GameState.RUNNING && !game.isSpectator(player)) {
				Team team = game.getPlayerTeam(player);
				if (team != null) {
					if (team.getPlayers().size() > 1 && !team.isDead(game)) {
						rejoin.addPlayer(player);
						return;
					}
				}
			}
			rejoin.removePlayer(player.getName());
		}
		respawn.onPlayerLeave(player);
		// teamShop.removeTriggeredPlayer(player);
		teamShop.removeImmunePlayer(player);
	}

	public void onPlayerJoined(Player player) {
		if (Config.rejoin_enabled) {
			rejoin.rejoin(player);
		}
		respawn.onPlayerJoined(player);
		holographic.onPlayerJoin(player);
		graffiti.onPlayerJoin(player);
		shop.onPlayerJoined(player);
	}

	public void onOpenShop(BedwarsOpenShopEvent e) {
		shop.onOpenShop(e);
	}

	private Boolean isGamePlayer(Player player) {
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return false;
		}
		if (!game.getName().equals(this.game.getName())) {
			return false;
		}
		if (game.isSpectator(player)) {
			return false;
		}
		return true;
	}

	private Boolean isAlivePlayer(Player player) {
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return false;
		}
		if (!game.getName().equals(this.game.getName())) {
			return false;
		}
		if (BedwarsUtil.isSpectator(game, player)) {
			return false;
		}
		return true;
	}
}
