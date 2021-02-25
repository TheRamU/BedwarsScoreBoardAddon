package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsResourceSpawnEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import lombok.Setter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;

public class IronForge implements Upgrade, Listener {

	@Getter
	private Game game;
	@Getter
	private Team team;
	@Getter
	private int level;
	@Getter
	@Setter
	private String buyer;
	private Map<Material, Integer> resources;
	private Map<Material, Double> resource_spread;
	private List<Location> locations;
	private Map<Item, ItemData> drop_items;

	public IronForge(Game game, Team team, int level) {
		this.game = game;
		this.team = team;
		this.level = level;
		resources = new HashMap<Material, Integer>();
		resource_spread = new HashMap<Material, Double>();
		locations = new ArrayList<Location>();
		drop_items = new HashMap<Item, ItemData>();
		loadResource();
		loadLocations();
		Arena arena = Main.getInstance().getArenaManager().getArena(game.getName());
		Map<Material, Integer> map = new HashMap<Material, Integer>();
		arena.addGameTask(new BukkitRunnable() {

			@Override
			public void run() {
				if (locations.size() > 0) {
					for (Material type : resources.keySet()) {
						int i = map.getOrDefault(type, resources.get(type));
						if (i >= resources.get(type)) {
							i = 0;
							locations.forEach(loc -> {
								dropItem(loc, type);
							});
						}
						i++;
						map.put(type, i);
					}
				}
				new ArrayList<Item>(drop_items.keySet()).forEach(item -> {
					if ((System.currentTimeMillis() - drop_items.get(item).getTime()) > 800) {
						drop_items.remove(item);
					}
				});

			}
		}.runTaskTimer(Main.getInstance(), 0L, 1L));
	}

	private void dropItem(Location loc, Material type) {
		int es = 0;
		for (Entity entity : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
			if (entity instanceof Item) {
				Item item = (Item) entity;
				if (item.getItemStack().getType().equals(type)) {
					es += item.getItemStack().getAmount();
				}
			}
		}
		boolean drop = true;
		if (Config.resourcelimit_enabled) {
			for (String[] rl : Config.resourcelimit_limit) {
				if (rl[0].equals(type.name()) && es >= Integer.valueOf(rl[1])) {
					drop = false;
				}
			}
		}
		Block block = loc.getBlock();
		Boolean inchest = block.getType().equals(Material.CHEST) && BedwarsRel.getInstance().getBooleanConfig("spawn-resources-in-chest", true);
		if (drop || inchest) {
			ItemStack itemStack = new ItemStack(type);
			BedwarsResourceSpawnEvent event = new BedwarsResourceSpawnEvent(game, loc, itemStack.clone());
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				if (inchest && isCanContain(((Chest) block.getState()).getInventory(), itemStack)) {
					((Chest) block.getState()).getInventory().addItem(itemStack.clone());
				} else if (drop) {
					dropItem(loc.clone().add(0, 0.325, 0), itemStack, resource_spread.get(type));
				}
			}
		}
	}

	private void dropItem(Location loc, ItemStack itemStack, double spread) {
		if (!Config.spread_resource_enabled) {
			dropItem(loc, itemStack, spread, false, null);
			return;
		}
		List<Player> players = getNearbyPlayers(loc, itemStack);
		if (players.size() <= 1) {
			dropItem(loc, itemStack, spread, false, null);
			return;
		}
		Random rand = new Random();
		for (Player player : players) {
			Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
				if (game.getState().equals(GameState.RUNNING)) {
					drop_items.put(dropItem(loc, itemStack, 0, true, player), new ItemData(player, System.currentTimeMillis()));
				}
			}, rand.nextInt(11));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPickupItem(PlayerPickupItemEvent e) {
		Player player = e.getPlayer();
		Item item = e.getItem();
		if (!drop_items.containsKey(item)) {
			return;
		}
		if (!drop_items.get(item).getPlayer().getUniqueId().equals(player.getUniqueId())) {
			e.setCancelled(true);
			return;
		}
		drop_items.remove(item);
	}

	private List<Player> getNearbyPlayers(Location location, ItemStack item) {
		List<Player> players = new ArrayList<Player>();
		for (Entity entity : location.getWorld().getNearbyEntities(location, Config.spread_resource_range, 0.5, Config.spread_resource_range)) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				if (game.isInGame(player) && !BedwarsUtil.isSpectator(game, player) && isCanContain(player, item)) {
					players.add(player);
				}
			}
		}
		return players;
	}

	private Item dropItem(Location loc, ItemStack itemStack, double spread, boolean istrack, Player player) {
		Item item = loc.getWorld().dropItem(loc, itemStack);
		item.setPickupDelay(10);
		if (istrack) {
			Vector vector = new Vector();
			if (Config.spread_resource_launch) {
				item.setPickupDelay(0);
				Location subloc = player.getLocation().subtract(loc);
				vector.setX(subloc.getX());
				vector.setZ(subloc.getZ());
				vector.multiply(0.125);
			}
			vector.setY(0);
			item.setVelocity(vector);
		} else {
			Vector vector = item.getVelocity();
			vector.multiply(spread);
			vector.setY(0);
			item.setVelocity(vector);
		}
		return item;
	}

	public UpgradeType getType() {
		return UpgradeType.IRON_FORGE;
	}

	public String getName() {
		return Config.teamshop_upgrade_name.get(getType());
	}

	public void runUpgrade() {
	}

	public void setLevel(int level) {
		this.level = level;
		loadResource();
	}

	private void loadResource() {
		resources.clear();
		resource_spread.clear();
		Config.teamshop_upgrade_iron_forge_level_resources.get(level).forEach(line -> {
			String[] ary = line.split(",");
			try {
				Material type = Material.valueOf(ary[0]);
				Integer interval = Integer.valueOf(ary[1]);
				double spread = Double.valueOf(ary[2]);
				resources.put(type, interval);
				resource_spread.put(type, spread);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void loadLocations() {
		if (Config.game_team_spawner.containsKey(game.getName()) && Config.game_team_spawner.get(game.getName()).containsKey(team.getName())) {
			for (Location loc : Config.game_team_spawner.get(game.getName()).get(team.getName())) {
				locations.add(loc.clone());
			}
		}
	}

	private boolean isCanContain(Inventory inv, ItemStack item) {
		int space = 0;
		for (ItemStack stack : inv.getContents()) {
			if (stack == null) {
				space += item.getMaxStackSize();
			} else if (stack.getType() == item.getType() && stack.getDurability() == item.getDurability()) {
				space += item.getMaxStackSize() - stack.getAmount();
			}
		}
		return space >= item.getAmount();
	}

	private boolean isCanContain(Player player, ItemStack item) {
		int space = 0;
		for (ItemStack stack : player.getInventory().getContents()) {
			if (stack == null) {
				space += item.getMaxStackSize();
			} else if (stack.isSimilar(item)) {
				space += item.getMaxStackSize() - stack.getAmount();
			}
		}
		return space >= item.getAmount();
	}

	public class ItemData {
		@Getter
		private Player player;
		@Getter
		private long time;

		public ItemData(Player player, long time) {
			this.player = player;
			this.time = time;
		}
	}
}
