package me.ram.bedwarsscoreboardaddon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import io.github.bedwarsrel.events.BedwarsGameEndEvent;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import ldcr.BedwarsXP.EventListeners;

public class XPEventListener extends EventListeners implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onItemPickup(PlayerPickupItemEvent e) {
		super.onItemPickup(e);
	}

	@EventHandler
	public void onAnvilOpen(InventoryOpenEvent e) {
		super.onAnvilOpen(e);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		super.onPlayerDeath(e);
	}

	@EventHandler
	public void onBedWarsStart(BedwarsGameStartEvent e) {
		super.onBedWarsStart(e);
	}

	@EventHandler
	public void onBedWarsEnd(BedwarsGameEndEvent e) {
		super.onBedWarsEnd(e);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		super.onPlayerTeleport(e);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		super.onPlayerInteract(e);
	}
}
