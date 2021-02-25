package me.ram.bedwarsscoreboardaddon.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import io.github.bedwarsrel.game.Game;

public class BoardAddonPlayerOpenTeamShopEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	private Game game;
	private Player player;
	private Boolean cancelled = false;

	public BoardAddonPlayerOpenTeamShopEvent(Game game, Player player) {
		this.game = game;
		this.player = player;
	}

	public Game getGame() {
		return game;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
