package me.ram.bedwarsscoreboardaddon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import ldcr.BedwarsXP.EventListeners;

public class XPEventListener extends EventListeners implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onItemPickup(PlayerPickupItemEvent e) {
		super.onItemPickup(e);
	}
}
