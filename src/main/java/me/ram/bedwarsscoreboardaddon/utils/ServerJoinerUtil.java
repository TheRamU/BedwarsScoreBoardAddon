package me.ram.bedwarsscoreboardaddon.utils;

import org.bukkit.entity.Player;
import com.mcjtf.ServerJoiner.Main;
import com.mcjtf.ServerJoiner.Settings;
import com.mcjtf.ServerJoiner.data.ServerGUI;

public class ServerJoinerUtil {

	public static void sendServer(Player player, String group) {
		if (Main.serverGroup.containsKey(group)) {
			ServerGUI gui = Main.guiMap.get(group);
			String server = gui.getAutoJoinServer();
			if (server == null) {
				player.sendMessage(Settings.msg_no_room);
				return;
			}
			Main.send(player, server);
		}
	}
}
