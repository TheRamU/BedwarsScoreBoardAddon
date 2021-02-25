package me.ram.bedwarsscoreboardaddon.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.ram.bedwarsscoreboardaddon.api.HolographicAPI;

public class HolographicManager {

	private List<HolographicAPI> holographics;

	public HolographicManager() {
		holographics = new ArrayList<HolographicAPI>();
	}

	public void addHolographic(HolographicAPI holo) {
		if (!holographics.contains(holo)) {
			holographics.add(holo);
		}
	}

	public void removeHolographic(HolographicAPI holo) {
		if (holographics.contains(holo)) {
			holographics.remove(holo);
		}
	}

	public void deleteHolographic(HolographicAPI holo) {
		if (holographics.contains(holo)) {
			holo.remove();
			holographics.remove(holo);
		}
	}

	public List<HolographicAPI> getPlayerHolographic(Player player) {
		List<HolographicAPI> list = new ArrayList<HolographicAPI>();
		for (HolographicAPI holo : holographics) {
			if (holo.getPlayers().contains(player)) {
				list.add(holo);
			}
		}
		return list;
	}
}
