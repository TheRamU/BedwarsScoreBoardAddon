package me.ram.bedwarsscoreboardaddon.addon;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.utils.SoundMachine;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;

public class FastRespawn implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onDamage(EntityDamageEvent e) {
		if (!Config.fast_respawn) {
			return;
		}
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getEntity();
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null || !game.getState().equals(GameState.RUNNING)) {
			return;
		}
		if (BedwarsUtil.isSpectator(game, player) || player.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}
		if (e.getDamage() <= 0) {
			return;
		}
		if (e.getFinalDamage() < player.getHealth() && !e.getCause().equals(DamageCause.VOID)) {
			return;
		}
		e.setCancelled(true);
		e.setDamage(0);
		Location location = player.getLocation();
		location.getWorld().playSound(location, SoundMachine.get("HURT_FLESH", "ENTITY_PLAYER_HURT"), 1, 1);
		PlayerInventory inventory = player.getInventory();
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (ItemStack item : inventory.getContents()) {
			if (item != null) {
				items.add(item);
			}
		}
		if (inventory.getHelmet() != null) {
			items.add(inventory.getHelmet());
		}
		if (inventory.getChestplate() != null) {
			items.add(inventory.getChestplate());
		}
		if (inventory.getLeggings() != null) {
			items.add(inventory.getLeggings());
		}
		if (inventory.getBoots() != null) {
			items.add(inventory.getBoots());
		}
		player.closeInventory();
		PlayerDeathEvent deathEvent = new PlayerDeathEvent(player, items, 0, null);
		Bukkit.getPluginManager().callEvent(deathEvent);
		if (!deathEvent.getKeepInventory()) {
			inventory.clear();
			inventory.setHelmet(new ItemStack(Material.AIR));
			inventory.setChestplate(new ItemStack(Material.AIR));
			inventory.setLeggings(new ItemStack(Material.AIR));
			inventory.setBoots(new ItemStack(Material.AIR));
			deathEvent.getDrops().forEach(item -> {
				if (item != null && !item.getType().equals(Material.AIR)) {
					location.getWorld().dropItemNaturally(location, item);
				}
			});
		}
		player.getActivePotionEffects().forEach(effect -> {
			player.removePotionEffect(effect.getType());
		});
		player.setFoodLevel(20);
		player.setHealth(player.getMaxHealth());
		PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(player, player.getWorld().getSpawnLocation(), false);
		Bukkit.getPluginManager().callEvent(respawnEvent);
		player.teleport(respawnEvent.getRespawnLocation());
	}
}
