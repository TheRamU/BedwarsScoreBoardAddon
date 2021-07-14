package me.ram.bedwarsscoreboardaddon.addon;

import com.comphenix.protocol.*;
import com.comphenix.protocol.wrappers.*;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import me.ram.bedwarsscoreboardaddon.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.comphenix.protocol.events.*;

public class NoBreakBed {

	@Getter
	private Game game;
	@Getter
	private Arena arena;
	private boolean bre;
	private String formattime = "00:00";
	private PacketListener packetListener;

	public NoBreakBed(Arena arena) {
		this.arena = arena;
		this.game = arena.getGame();
		bre = false;
		if (!Config.nobreakbed_enabled) {
			return;
		}
		registerPacketListener();
		arena.addGameTask(new BukkitRunnable() {
			@Override
			public void run() {
				int time = game.getTimeLeft() - Config.nobreakbed_gametime;
				String ftime = time / 60 + ":" + ((time % 60 < 10) ? ("0" + time % 60) : (time % 60));
				formattime = ftime;
				if (game.getTimeLeft() <= Config.nobreakbed_gametime) {
					bre = true;
					if (Config.nobreakbed_enabled) {
						for (Player player : game.getPlayers()) {
							if (!Config.nobreakbed_title.equals("") || !Config.nobreakbed_subtitle.equals("")) {
								Utils.sendTitle(player, 10, 50, 10, Config.nobreakbed_title, Config.nobreakbed_subtitle);
							}
							if (!Config.nobreakbed_message.equals("")) {
								player.sendMessage(Config.nobreakbed_message);
							}
						}
					}
					cancel();
					return;
				}
			}
		}.runTaskTimer(Main.getInstance(), 0L, 21L));
	}

	public String getTime() {
		return formattime;
	}

	public void onEnd() {
		if (packetListener != null) {
			ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
		}
	}

	private void registerPacketListener() {
		packetListener = new PacketAdapter(Main.getInstance(), ListenerPriority.HIGHEST, new PacketType[] { PacketType.Play.Client.BLOCK_DIG }) {
			public void onPacketReceiving(PacketEvent e) {
				if (!Config.nobreakbed_enabled) {
					return;
				}
				Player player = e.getPlayer();
				if (BedwarsUtil.isSpectator(game, player) || game.getState() != GameState.RUNNING) {
					return;
				}
				if (!bre && e.getPacketType() == PacketType.Play.Client.BLOCK_DIG) {
					PacketContainer packet = e.getPacket();
					BlockPosition position = packet.getBlockPositionModifier().read(0);
					Block block = new Location(player.getWorld(), position.getX(), position.getY(), position.getZ()).getBlock();
					if (!block.getType().equals(game.getTargetMaterial())) {
						return;
					}
					if (!packet.getPlayerDigTypes().read(0).equals(EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK)) {
						return;
					}
					player.sendMessage(Config.nobreakbed_nobreakmessage);
					e.setCancelled(true);
					block.getState().update();
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
	}
}
