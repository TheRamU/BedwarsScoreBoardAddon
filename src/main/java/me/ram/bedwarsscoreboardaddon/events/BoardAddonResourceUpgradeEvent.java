package me.ram.bedwarsscoreboardaddon.events;

import java.util.List;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import io.github.bedwarsrel.game.Game;

public class BoardAddonResourceUpgradeEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	private Game game;
	private List<String> upgrade;
	private Boolean cancelled = false;

	public BoardAddonResourceUpgradeEvent(Game game, List<String> upgrade) {
		this.game = game;
		this.upgrade = upgrade;
	}

	public Game getGame() {
		return game;
	}

	public List<String> getUpgrade() {
		return upgrade;
	}

	public void setUpgrade(List<String> upgrade) {
		this.upgrade = upgrade;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(final boolean cancel) {
		this.cancelled = cancel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
