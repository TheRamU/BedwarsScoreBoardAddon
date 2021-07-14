package me.ram.bedwarsscoreboardaddon.events;

import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import io.github.bedwarsrel.game.Game;

public class BoardAddonPlayerShootWitherBowEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	private Game game;
	private Player player;
	private WitherSkull witherSkull;
	private Boolean cancelled = false;

	public BoardAddonPlayerShootWitherBowEvent(Game game, Player player, WitherSkull witherSkull) {
		this.game = game;
		this.player = player;
		this.witherSkull = witherSkull;
	}

	public Game getGame() {
		return game;
	}

	public Player getPlayer() {
		return player;
	}

	public WitherSkull getWitherSkull() {
		return witherSkull;
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
