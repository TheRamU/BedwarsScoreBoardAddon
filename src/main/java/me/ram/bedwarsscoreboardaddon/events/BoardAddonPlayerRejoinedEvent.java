package me.ram.bedwarsscoreboardaddon.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import io.github.bedwarsrel.game.Game;
import me.ram.bedwarsscoreboardaddon.addon.Rejoin;

public class BoardAddonPlayerRejoinedEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	private Game game;
	private Player player;
	private Rejoin rejoin;

	public BoardAddonPlayerRejoinedEvent(Game game, Player player, Rejoin rejoin) {
		this.game = game;
		this.player = player;
		this.rejoin = rejoin;
	}

	public Game getGame() {
		return game;
	}

	public Player getPlayer() {
		return player;
	}

	public Rejoin getRejoin() {
		return rejoin;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
