package me.ram.bedwarsscoreboardaddon.addon;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.shop.NewItemShop;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.api.HolographicAPI;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonPlayerOpenItemShopEvent;
import me.ram.bedwarsscoreboardaddon.events.BoardAddonPlayerOpenTeamShopEvent;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import io.github.bedwarsrel.events.BedwarsGameOverEvent;
import io.github.bedwarsrel.events.BedwarsGameStartedEvent;
import io.github.bedwarsrel.events.BedwarsOpenShopEvent;
import io.github.bedwarsrel.events.BedwarsPlayerJoinedEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.Gravity;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;

public class Shop implements Listener {

	private WrappedDataWatcher.Serializer booleanserializer;
	private Map<String, List<NPC>> shops;
	private Map<String, List<NPC>> teamshops;
	private Map<String, List<HolographicAPI>> titles;
	private List<Integer> npcid;

	public Shop() {
		shops = new HashMap<String, List<NPC>>();
		teamshops = new HashMap<String, List<NPC>>();
		titles = new HashMap<String, List<HolographicAPI>>();
		npcid = new ArrayList<Integer>();
		if (!BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
			booleanserializer = WrappedDataWatcher.Registry.get(Boolean.class);
		}
		packetListener();
	}

	@EventHandler
	public void onStarted(BedwarsGameStartedEvent e) {
		Game game = e.getGame();
		shops.put(game.getName(), new ArrayList<NPC>());
		teamshops.put(game.getName(), new ArrayList<NPC>());
		titles.put(game.getName(), new ArrayList<HolographicAPI>());
		if (Config.shop_enabled) {
			if (Config.game_shop_item.containsKey(game.getName())) {
				for (String loc : Config.game_shop_item.get(game.getName())) {
					Location location = toLocation(loc);
					if (location != null) {
						shops.get(game.getName()).add(spawnShop(game, location.clone(), Config.shop_item_shop_look, Config.shop_item_shop_type, Config.shop_item_shop_skin));
						setTitle(game, location.clone().add(0, -0.1, 0), Config.shop_item_shop_name);
					}
				}
			}
			if (Config.game_shop_team.containsKey(game.getName())) {
				for (String loc : Config.game_shop_team.get(game.getName())) {
					Location location = toLocation(loc);
					if (location != null) {
						teamshops.get(game.getName()).add(spawnShop(game, location.clone(), Config.shop_team_shop_look, Config.shop_team_shop_type, Config.shop_team_shop_skin));
						setTitle(game, location.clone().add(0, -0.1, 0), Config.shop_team_shop_name);
					}
				}
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game.getState() != GameState.RUNNING || game.getPlayers().size() < 1) {
					cancel();
					for (NPC npc : shops.get(e.getGame().getName())) {
						CitizensAPI.getNPCRegistry().deregister(npc);
					}
					for (NPC npc : teamshops.get(e.getGame().getName())) {
						CitizensAPI.getNPCRegistry().deregister(npc);
					}
					for (HolographicAPI holo : titles.get(e.getGame().getName())) {
						holo.remove();
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 0L);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onNPCLeftClick(NPCLeftClickEvent e) {
		e.setCancelled(onNPCClick(e.getClicker(), e.getNPC(), e.isCancelled()));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onNPCRightClick(NPCRightClickEvent e) {
		e.setCancelled(onNPCClick(e.getClicker(), e.getNPC(), e.isCancelled()));
	}

	private Boolean onNPCClick(Player player, NPC npc, Boolean isCancelled) {
		if (!Config.shop_enabled) {
			return isCancelled;
		}
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game != null) {
			if (shops.get(game.getName()).contains(npc)) {
				if (isGamePlayer(player)) {
					isCancelled = true;
					BoardAddonPlayerOpenItemShopEvent openItemhopEvent = new BoardAddonPlayerOpenItemShopEvent(game, player);
					Bukkit.getPluginManager().callEvent(openItemhopEvent);
					if (!openItemhopEvent.isCancelled()) {
						player.closeInventory();
						NewItemShop itemShop = game.openNewItemShop(player);
						itemShop.setCurrentCategory(null);
						itemShop.openCategoryInventory(player);
					}
				}
			} else if (teamshops.get(game.getName()).contains(npc)) {
				if (isGamePlayer(player)) {
					isCancelled = true;
					BoardAddonPlayerOpenTeamShopEvent openTeamShopEvent = new BoardAddonPlayerOpenTeamShopEvent(game, player);
					Bukkit.getPluginManager().callEvent(openTeamShopEvent);
					if (!openTeamShopEvent.isCancelled()) {
						player.closeInventory();
						Main.getInstance().getArenaManager().getArena(game.getName()).getTeamShop().openTeamShop(player);
					}
				}
			}
		}
		return isCancelled;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onOpenShop(BedwarsOpenShopEvent e) {
		if (!Config.shop_enabled) {
			return;
		}
		Player player = (Player) e.getPlayer();
		if (player.getGameMode().equals(GameMode.SPECTATOR)) {
			e.setCancelled(true);
			return;
		}
		if (e.getEntity() instanceof Villager) {
			if (CitizensAPI.getNPCRegistry().isNPC(e.getEntity()) && teamshops.get(e.getGame().getName()).contains(CitizensAPI.getNPCRegistry().getNPC(e.getEntity()))) {
				e.setCancelled(true);
				player.closeInventory();
				Main.getInstance().getArenaManager().getArena(e.getGame().getName()).getTeamShop().openTeamShop(player);
			}
		}
	}

	@EventHandler
	public void onPlayerJoined(BedwarsPlayerJoinedEvent e) {
		Game game = e.getGame();
		Player player = e.getPlayer();
		if (game.getState() == GameState.RUNNING) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (game.getState() == GameState.RUNNING && player.isOnline() && game.getPlayers().contains(player)) {
						for (HolographicAPI holo : titles.get(game.getName())) {
							holo.display(player);
						}
					}
				}
			}.runTaskLater(Main.getInstance(), 10L);
		}
	}

	private void packetListener() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Main.getInstance(), ListenerPriority.HIGHEST, new PacketType[] { PacketType.Play.Server.ENTITY_METADATA }) {
			@Override
			public void onPacketSending(PacketEvent e) {
				PacketContainer packet = e.getPacket();
				int id = packet.getIntegers().read(0);
				if (npcid.contains(id)) {
					WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
					if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
						wrappedDataWatcher.setObject(3, (byte) 0);
					} else {
						wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, booleanserializer), false);
					}
					packet.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
				}
			}
		});
	}

	private Boolean isGamePlayer(Player player) {
		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (game == null) {
			return false;
		}
		if (BedwarsUtil.isSpectator(game, player)) {
			return false;
		}
		if (player.getGameMode() == GameMode.SPECTATOR) {
			return false;
		}
		return true;
	}

	private NPC spawnShop(Game game, Location location, boolean look, String type, String skin) {
		if (!location.getBlock().getChunk().isLoaded()) {
			location.getBlock().getChunk().load(true);
		}
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
		npc.setProtected(true);
		npc.getTrait(Gravity.class).toggle();
		if (look) {
			npc.getTrait(LookClose.class).toggle();
		}
		npc.spawn(location);
		try {
			EntityType entityType = EntityType.valueOf(type);
			npc.setBukkitEntityType(entityType);
		} catch (Exception e) {
		}
		npcid.add(npc.getEntity().getEntityId());
		hideEntityTag(game, npc.getEntity());
		Config.addShopNPC(npc.getId());
		try {
			if (npc.isSpawned() && npc.getEntity() instanceof SkinnableEntity) {
				SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
				skinnable.setSkinName(skin, true);
			}
		} catch (Exception e) {
		}
		return npc;
	}

	private void hideEntityTag(Game game, Entity entity) {
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			ProtocolManager man = ProtocolLibrary.getProtocolManager();
			PacketContainer packet = man.createPacket(PacketType.Play.Server.ENTITY_METADATA);
			packet.getIntegers().write(0, entity.getEntityId());
			WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
			if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
				wrappedDataWatcher.setObject(3, (byte) 0);
			} else {
				wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, booleanserializer), false);
			}
			packet.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
			for (Player player : game.getPlayers()) {
				try {
					man.sendServerPacket(player, packet);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}, 1L);
	}

	private void setTitle(Game game, Location location, List<String> title) {
		Location loc = location.clone();
		if (!loc.getBlock().getChunk().isLoaded()) {
			loc.getBlock().getChunk().load(true);
		}
		List<String> list = new ArrayList<String>();
		list.addAll(title);
		Collections.reverse(list);
		for (String line : list) {
			HolographicAPI holo = new HolographicAPI(loc, line);
			titles.get(game.getName()).add(holo);
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Player player : game.getPlayers()) {
						holo.display(player);
					}
				}
			}.runTaskLater(Main.getInstance(), 20L);
			loc.add(0, 0.3, 0);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game.getState() == GameState.RUNNING) {
					if (!loc.getBlock().getChunk().isLoaded()) {
						loc.getBlock().getChunk().load(true);
					}
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 0L);
	}

	private Location toLocation(String loc) {
		try {
			String[] ary = loc.split(", ");
			if (Bukkit.getWorld(ary[0]) != null) {
				Location location = new Location(Bukkit.getWorld(ary[0]), Double.valueOf(ary[1]), Double.valueOf(ary[2]), Double.valueOf(ary[3]));
				if (ary.length > 4) {
					location.setYaw(Float.valueOf(ary[4]));
					location.setPitch(Float.valueOf(ary[5]));
				}
				return location;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	@EventHandler
	public void onOver(BedwarsGameOverEvent e) {
		for (NPC npc : shops.get(e.getGame().getName())) {
			CitizensAPI.getNPCRegistry().deregister(npc);
		}
		for (NPC npc : teamshops.get(e.getGame().getName())) {
			CitizensAPI.getNPCRegistry().deregister(npc);
		}
		for (HolographicAPI holo : titles.get(e.getGame().getName())) {
			holo.remove();
		}
	}

	@EventHandler
	public void onDisable(PluginDisableEvent e) {
		if (e.getPlugin().equals(Main.getInstance())) {
			for (String game : shops.keySet()) {
				for (NPC npc : shops.get(game)) {
					CitizensAPI.getNPCRegistry().deregister(npc);
				}
			}
			for (String game : teamshops.keySet()) {
				for (NPC npc : teamshops.get(game)) {
					CitizensAPI.getNPCRegistry().deregister(npc);
				}
			}
			for (String game : titles.keySet()) {
				for (HolographicAPI holo : titles.get(game)) {
					holo.remove();
				}
			}
		}
	}
}
