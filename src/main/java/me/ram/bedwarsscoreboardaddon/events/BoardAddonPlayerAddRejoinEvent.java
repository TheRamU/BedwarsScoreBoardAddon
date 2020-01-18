package me.ram.bedwarsscoreboardaddon.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.bedwarsrel.game.Game;
import me.ram.bedwarsscoreboardaddon.addon.Rejoin;

public class BoardAddonPlayerAddRejoinEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	private Game game;
	private Player player;
	private Boolean cancelled = false;
	private Rejoin rejoin;
	
	public BoardAddonPlayerAddRejoinEvent(Game game, Player player, Rejoin rejoin) {
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
	
	public boolean isCancelled() {
		return cancelled;
	}
	    
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
    	return handlers;
    }
}
