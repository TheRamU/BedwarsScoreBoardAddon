package me.ram.bedwarsscoreboardaddon.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.server.PluginDisableEvent;
import me.ram.bedwarsscoreboardaddon.Main;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsPlayerKilledEvent;
import io.github.bedwarsrel.events.BedwarsTargetBlockDestroyedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.addon.DeathMode;
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
import me.ram.bedwarsscoreboardaddon.addon.TeamShop;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.storage.PlayerGameStorage;

public class Arena {

	private Game game;
	private ScoreBoard scoreboard;
	private PlayerGameStorage playergamestorage;
	private DeathMode deathmode;
	private HealthLevel healthlevel;
	private NoBreakBed nobreakbed;
	private ResourceUpgrade resourceupgrade;
	private Holographic holographic;
	private TeamShop teamshop;
	private InvisibilityPlayer invisiblePlayer;
	private LobbyBlock lobbyblock;
	private Respawn respawn;
	private Rejoin rejoin;
	private Boolean isover;

	public Arena(Game game) {
		this.game = game;
		playergamestorage = new PlayerGameStorage(game);
		scoreboard = new ScoreBoard(this);
		deathmode = new DeathMode(game);
		healthlevel = new HealthLevel(game);
		nobreakbed = new NoBreakBed(game);
		resourceupgrade = new ResourceUpgrade(game);
		holographic = new Holographic(game, resourceupgrade);
		teamshop = new TeamShop(game);
		invisiblePlayer = new InvisibilityPlayer(game);
		lobbyblock = new LobbyBlock(game);
		respawn = new Respawn(game);
		rejoin = new Rejoin(game);
		isover = false;
	}

	public Game getGame() {
		return game;
	}

	public TeamShop getTeamShop() {
		return teamshop;
	}

	public ScoreBoard getScoreBoard() {
		return scoreboard;
	}

	public PlayerGameStorage getPlayerGameStorage() {
		return playergamestorage;
	}

	public DeathMode getDeathMode() {
		return deathmode;
	}

	public HealthLevel getHealthLevel() {
		return healthlevel;
	}

	public NoBreakBed getNoBreakBed() {
		return nobreakbed;
	}

	public ResourceUpgrade getResourceUpgrade() {
		return resourceupgrade;
	}

	public Holographic getHolographic() {
		return holographic;
	}

	public InvisibilityPlayer getInvisiblePlayer() {
		return invisiblePlayer;
	}

	public LobbyBlock getLobbyBlock() {
		return lobbyblock;
	}

	public Respawn getRespawn() {
		return respawn;
	}

	public Rejoin getRejoin() {
		return rejoin;
	}

	public Boolean isOver() {
		return isover;
	}

	public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent e) {
		if (!isGamePlayer(e.getPlayer())) {
			return;
		}
		Map<String, Integer> beds = playergamestorage.getPlayerBeds();
		Player player = e.getPlayer();
		if (beds.containsKey(player.getName())) {
			beds.put(player.getName(), beds.get(player.getName()) + 1);
		} else {
			beds.put(player.getName(), 1);
		}
		holographic.onTargetBlockDestroyed(e);
	}

	public void onDeath(Player player) {
		if (!isGamePlayer(player)) {
			return;
		}
		Map<String, Integer> dies = playergamestorage.getPlayerDies();
		if (dies.containsKey(player.getName())) {
			dies.put(player.getName(), dies.get(player.getName()) + 1);
		} else {
			dies.put(player.getName(), 1);
		}
		PlaySound.playSound(player, Config.play_sound_sound_death);
		respawn.onDeath(player, false);
	}

	public void onRespawn(Player player) {
		if (!isGamePlayer(player)) {
			return;
		}
		respawn.onRespawn(player);
	}

	public void onPlayerKilled(BedwarsPlayerKilledEvent e) {
		if (!isGamePlayer(e.getPlayer()) || !isGamePlayer(e.getKiller())) {
			return;
		}
		Player player = e.getPlayer();
		Player killer = e.getKiller();
		if (!game.getPlayers().contains(player) || !game.getPlayers().contains(killer) || game.isSpectator(player)
				|| game.isSpectator(killer)) {
			return;
		}
		Map<String, Integer> totalkills = playergamestorage.getPlayerTotalKills();
		Map<String, Integer> kills = playergamestorage.getPlayerKills();
		Map<String, Integer> finalkills = playergamestorage.getPlayerFinalKills();
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
			isover = true;
			if (Config.overstats_enabled && e.getWinner() != null) {
				Team winner = e.getWinner();
				Map<String, Integer> totalkills = playergamestorage.getPlayerTotalKills();
				int kills_1 = 0;
				int kills_2 = 0;
				int kills_3 = 0;
				String kills_1_player = "none";
				String kills_2_player = "none";
				String kills_3_player = "none";
				for (String player : totalkills.keySet()) {
					int k = totalkills.get(player);
					if (k > 0 && k > kills_1) {
						kills_1_player = player;
						kills_1 = k;
					}
				}
				for (String player : totalkills.keySet()) {
					int k = totalkills.get(player);
					if (k > kills_2 && k <= kills_1 && !player.equals(kills_1_player)) {
						kills_2_player = player;
						kills_2 = k;
					}
				}
				for (String player : totalkills.keySet()) {
					int k = totalkills.get(player);
					if (k > kills_3 && k <= kills_2 && !player.equals(kills_1_player)
							&& !player.equals(kills_2_player)) {
						kills_3_player = player;
						kills_3 = k;
					}
				}
				List<String> WinTeamPlayers = new ArrayList<String>();
				for (Player teamplayer : winner.getPlayers()) {
					WinTeamPlayers.add(teamplayer.getName());
				}
				String WinTeamPlayerList = WinTeamPlayers + "";
				WinTeamPlayerList = WinTeamPlayerList.replace("[", "").replace("]", "");
				for (Player player : this.game.getPlayers()) {
					for (String os : Config.overstats_message) {
						player.sendMessage(os.replace("{color}", winner.getChatColor() + "")
								.replace("{win_team}", winner.getName())
								.replace("{win_team_players}", WinTeamPlayerList)
								.replace("{first_1_kills_player}", kills_1_player)
								.replace("{first_2_kills_player}", kills_2_player)
								.replace("{first_3_kills_player}", kills_3_player)
								.replace("{first_1_kills}", kills_1 + "").replace("{first_2_kills}", kills_2 + "")
								.replace("{first_3_kills}", kills_3 + ""));
					}
				}
			}
			nobreakbed.onOver();
			holographic.remove();
			lobbyblock.recovery();
		}
	}

	public void onEnd() {
		lobbyblock.recovery();
	}

	public void onDisable(PluginDisableEvent e) {
		if (e.getPlugin().equals(Main.getInstance())) {
			holographic.remove();
			lobbyblock.recovery();
		}
	}

	public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
		holographic.onArmorStandManipulate(e);
	}

	public void onClick(InventoryClickEvent e) {
		teamshop.onClick(e);
		teamshop.onClickDefense(e);
		teamshop.onClickHaste(e);
		teamshop.onClickHeal(e);
		teamshop.onClickProtection(e);
		teamshop.onClickSharpness(e);
		teamshop.onClickTrap(e);
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
	}

	public void onPlayerJoined(Player player) {
		if (Config.rejoin_enabled) {
			rejoin.rejoin(player);
		}
		holographic.onPlayerJoin(player);
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
}
