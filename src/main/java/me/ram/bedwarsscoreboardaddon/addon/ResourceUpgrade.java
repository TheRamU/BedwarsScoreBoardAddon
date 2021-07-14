package me.ram.bedwarsscoreboardaddon.addon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsResourceSpawnEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.ResourceSpawner;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonResourceUpgradeEvent;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;

public class ResourceUpgrade {

	@Getter
	private Game game;
	@Getter
	private Arena arena;
	private Map<Material, Integer> interval;
	private Map<Material, Integer> spawn_time;
	private Map<String, String> upg_time;
	private Map<Material, String> levels;

	public Map<String, String> getUpgTime() {
		return upg_time;
	}

	public Map<Material, Integer> getSpawnTime() {
		return spawn_time;
	}

	public Map<Material, String> getLevel() {
		return levels;
	}

	public ResourceUpgrade(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		interval = new HashMap<Material, Integer>();
		spawn_time = new HashMap<Material, Integer>();
		upg_time = new HashMap<String, String>();
		levels = new HashMap<Material, String>();
		for (ResourceSpawner spawner : game.getResourceSpawners()) {
			for (ItemStack itemStack : spawner.getResources()) {
				levels.put(itemStack.getType(), "I");
				interval.put(itemStack.getType(), (int) (spawner.getInterval() / 50));
			}
			Location sloc = spawner.getLocation();
			for (ItemStack itemStack : spawner.getResources()) {
				arena.addGameTask(new BukkitRunnable() {
					Location loc = new Location(sloc.getWorld(), sloc.getX(), sloc.getY(), sloc.getZ());
					int i = 0;

					@Override
					public void run() {
						spawn_time.put(itemStack.getType(), ((int) (i / 20) + 1));
						if (i <= 0) {
							i = interval.get(itemStack.getType());
							int es = 0;
							for (Entity entity : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
								if (entity instanceof Item) {
									Item item = (Item) entity;
									if (item.getItemStack().getType().equals(itemStack.getType())) {
										es += item.getItemStack().getAmount();
									}
								}
							}
							boolean drop = true;
							if (Config.resourcelimit_enabled) {
								for (String[] rl : Config.resourcelimit_limit) {
									if (rl[0].equals(itemStack.getType().name()) && es >= Integer.valueOf(rl[1])) {
										drop = false;
									}
								}
							}
							Block block = loc.getBlock();
							Boolean inchest = block.getType().equals(Material.CHEST) && BedwarsRel.getInstance().getBooleanConfig("spawn-resources-in-chest", true);
							if (drop || inchest) {
								BedwarsResourceSpawnEvent event = new BedwarsResourceSpawnEvent(game, loc, itemStack.clone());
								Bukkit.getPluginManager().callEvent(event);
								if (!event.isCancelled()) {
									if (inchest && spawner.canContainItem(((Chest) block.getState()).getInventory(), itemStack)) {
										((Chest) block.getState()).getInventory().addItem(itemStack.clone());
									} else if (drop) {
										ConfigurationSection config = Main.getInstance().getConfig().getConfigurationSection("holographic.resource.resources");
										String res_name = getResourceName(itemStack.getTypeId());
										double i = res_name == null || !config.getBoolean(res_name + ".drop", false) ? 0 : config.getDouble(res_name + ".height", 0.0);
										i = i > 0 ? i : 0.325;
										Item item = loc.getWorld().dropItem(loc.clone().add(0, i, 0), itemStack);
										item.setPickupDelay(0);
										Vector vector = item.getVelocity();
										vector.multiply(spawner.getSpread());
										vector.setY(0);
										item.setVelocity(vector);
									}
								}
							}
						}
						i--;
					}
				}.runTaskTimer(Main.getInstance(), 0L, 1L));
			}
		}
		for (String rs : Main.getInstance().getConfig().getConfigurationSection("resourceupgrade").getKeys(false)) {
			arena.addGameTask(new BukkitRunnable() {
				int gametime = Main.getInstance().getConfig().getInt("resourceupgrade." + rs + ".gametime");
				List<String> upgrade = Main.getInstance().getConfig().getStringList("resourceupgrade." + rs + ".upgrade");
				String message = Main.getInstance().getConfig().getString("resourceupgrade." + rs + ".message");
				Boolean isExecuted = false;

				@Override
				public void run() {
					if (isExecuted) {
						cancel();
						return;
					}
					int remtime = game.getTimeLeft() - gametime;
					String formatremtime = remtime / 60 + ":" + ((remtime % 60 < 10) ? ("0" + remtime % 60) : (remtime % 60));
					upg_time.put(rs, formatremtime);
					if (game.getTimeLeft() <= gametime) {
						isExecuted = true;
						BoardAddonResourceUpgradeEvent resourceUpgradeEvent = new BoardAddonResourceUpgradeEvent(game, upgrade);
						Bukkit.getPluginManager().callEvent(resourceUpgradeEvent);
						if (resourceUpgradeEvent.isCancelled()) {
							cancel();
							return;
						}
						for (String upg : resourceUpgradeEvent.getUpgrade()) {
							String[] ary = upg.split(",");
							if (levels.containsKey(Material.valueOf(ary[0]))) {
								levels.put(Material.valueOf(ary[0]), getLevel(levels.get(Material.valueOf(ary[0]))));
								interval.put(Material.valueOf(ary[0]), Integer.valueOf(ary[1]));
							}
						}
						for (Player player : game.getPlayers()) {
							player.sendMessage(ColorUtil.color(message));
						}
						PlaySound.playSound(game, Config.play_sound_sound_upgrade);
						cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), 0L, 21L));
		}
	}

	private String getResourceName(int id) {
		FileConfiguration config = Main.getInstance().getConfig();
		for (String r : Config.holographic_resource) {
			if (id == config.getInt("holographic.resource.resources." + r + ".item", 0)) {
				return r;
			}
		}
		return null;
	}

	private String getLevel(String level) {
		switch (level) {
		case "I":
			return "II";
		case "II":
			return "III";
		case "III":
			return "IV";
		case "IV":
			return "V";
		case "V":
			return "VI";
		case "VI":
			return "VII";
		case "VII":
			return "VIII";
		case "VIII":
			return "IX";
		case "IX":
			return "X";
		case "X":
			return "X";
		default:
			return "I";
		}
	}
}
