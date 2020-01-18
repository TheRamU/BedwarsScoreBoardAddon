package me.ram.bedwarsscoreboardaddon.addon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsResourceSpawnEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.ResourceSpawner;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonResourceUpgradeEvent;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;

public class ResourceUpgrade implements Listener {
	private Map<Material, Integer> Interval;
	private Map<Material, Integer> SpawnTime;
	private Map<String, String> UpgTime;
	private Map<Material, String> Level;

	public Map<String, String> getUpgTime() {
		return UpgTime;
	}

	public Map<Material, Integer> getSpawnTime() {
		return SpawnTime;
	}

	public Map<Material, String> getLevel() {
		return Level;
	}

	public ResourceUpgrade(Game game) {
		Interval = new HashMap<Material, Integer>();
		SpawnTime = new HashMap<Material, Integer>();
		UpgTime = new HashMap<String, String>();
		Level = new HashMap<Material, String>();
		for (ResourceSpawner spawner : game.getResourceSpawners()) {
			for (ItemStack itemStack : spawner.getResources()) {
				Level.put(itemStack.getType(), "I");
				Interval.put(itemStack.getType(), (int) (spawner.getInterval() / 50));
			}
			Location sloc = spawner.getLocation();
			for (ItemStack itemStack : spawner.getResources()) {
				new BukkitRunnable() {
					Location loc = new Location(sloc.getWorld(), sloc.getX(), sloc.getY(), sloc.getZ());
					int i = Interval.get(itemStack.getType());

					@Override
					public void run() {
						if (game.getState() != GameState.WAITING && game.getState() == GameState.RUNNING) {
							SpawnTime.put(itemStack.getType(), ((int) (i / 20) + 1));
							if (i <= 0) {
								i = Interval.get(itemStack.getType());
								int es = 0;
								for (Entity entity : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
									if (entity instanceof Item) {
										Item item = (Item) entity;
										if (item.getItemStack().getType() == itemStack.getType()) {
											es += item.getItemStack().getAmount();
										}
									}
								}
								boolean drop = true;
								if (Config.resourcelimit_enabled) {
									for (String[] rl : Config.resourcelimit_limit) {
										if (Material.valueOf(rl[0]) == itemStack.getType()) {
											if (es >= Integer.valueOf(rl[1])) {
												drop = false;
											}
										}
									}
								}
								Block block = loc.getBlock();
								Boolean inchest = block.getType().equals(Material.CHEST)
										&& BedwarsRel.getInstance().getBooleanConfig("spawn-resources-in-chest", true);
								if (drop || inchest) {
									BedwarsResourceSpawnEvent event = new BedwarsResourceSpawnEvent(game, loc,
											itemStack.clone());
									Bukkit.getPluginManager().callEvent(event);
									if (!event.isCancelled()) {
										if (inchest && spawner.canContainItem(((Chest) block.getState()).getInventory(),
												itemStack)) {
											((Chest) block.getState()).getInventory().addItem(itemStack.clone());
										} else if (drop) {
											Item item = loc.getWorld().dropItemNaturally(loc, itemStack);
											item.setPickupDelay(0);
											item.setVelocity(item.getVelocity().multiply(spawner.getSpread()));
										}
									}
								}
							}
							i--;
						} else {
							cancel();
						}
					}
				}.runTaskTimer(Main.getInstance(), 0L, 1L);
			}
		}
		for (String rs : Main.getInstance().getConfig().getConfigurationSection("resourceupgrade").getKeys(false)) {
			new BukkitRunnable() {
				int gametime = Main.getInstance().getConfig().getInt("resourceupgrade." + rs + ".gametime");
				List<String> upgrade = Main.getInstance().getConfig()
						.getStringList("resourceupgrade." + rs + ".upgrade");
				String message = Main.getInstance().getConfig().getString("resourceupgrade." + rs + ".message");
				Boolean isExecuted = false;

				@Override
				public void run() {
					if (game.getState() == GameState.RUNNING) {
						if (isExecuted) {
							cancel();
							return;
						}
						int remtime = game.getTimeLeft() - gametime;
						String formatremtime = remtime / 60 + ":"
								+ ((remtime % 60 < 10) ? ("0" + remtime % 60) : (remtime % 60));
						UpgTime.put(rs, formatremtime);
						if (game.getTimeLeft() <= gametime) {
							isExecuted = true;
							BoardAddonResourceUpgradeEvent resourceUpgradeEvent = new BoardAddonResourceUpgradeEvent(
									game, upgrade);
							Bukkit.getPluginManager().callEvent(resourceUpgradeEvent);
							if (resourceUpgradeEvent.isCancelled()) {
								cancel();
								return;
							}
							for (String upg : resourceUpgradeEvent.getUpgrade()) {
								String[] ary = upg.split(",");
								if (Level.containsKey(Material.valueOf(ary[0]))) {
									Level.put(Material.valueOf(ary[0]), getLevel(Level.get(Material.valueOf(ary[0]))));
									Interval.put(Material.valueOf(ary[0]), Integer.valueOf(ary[1]));
								}
							}
							for (Player player : game.getPlayers()) {
								player.sendMessage(ColorUtil.color(message));
							}
							PlaySound.playSound(game, Config.play_sound_sound_upgrade);
							cancel();
						}
					} else {
						cancel();
					}
				}
			}.runTaskTimer(Main.getInstance(), 0L, 21L);
		}
	}

	public String getLevel(String level) {
		String l = "I";
		if (level.equals("I")) {
			l = "II";
		}
		if (level.equals("II")) {
			l = "III";
		}
		if (level.equals("III")) {
			l = "IV";
		}
		if (level.equals("IV")) {
			l = "V";
		}
		if (level.equals("V")) {
			l = "VI";
		}
		if (level.equals("VI")) {
			l = "VII";
		}
		if (level.equals("VII")) {
			l = "VIII";
		}
		if (level.equals("VIII")) {
			l = "IX";
		}
		if (level.equals("IX")) {
			l = "X";
		}
		if (level.equals("X")) {
			l = "X";
		}
		return l;
	}
}
