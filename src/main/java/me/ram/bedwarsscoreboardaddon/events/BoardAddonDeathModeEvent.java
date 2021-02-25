package me.ram.bedwarsscoreboardaddon.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import io.github.bedwarsrel.game.Game;

public class BoardAddonDeathModeEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	private Game game;
	private Boolean cancelled = false;

	public BoardAddonDeathModeEvent(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
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
