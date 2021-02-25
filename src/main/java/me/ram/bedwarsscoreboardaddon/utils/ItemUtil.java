package me.ram.bedwarsscoreboardaddon.utils;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {

	public static ItemStack createItem(String name) {
		try {
			return new ItemStack(Material.valueOf(name));
		} catch (Exception e) {
		}
		return new ItemStack(Material.AIR);
	}

	public static ItemStack createItem(String name, int amount) {
		try {
			return new ItemStack(Material.valueOf(name), amount);
		} catch (Exception e) {
		}
		return new ItemStack(Material.AIR);
	}

	public static ItemStack createItem(String name, int amount, short damage) {
		try {
			return new ItemStack(Material.valueOf(name), amount, damage);
		} catch (Exception e) {
		}
		return new ItemStack(Material.AIR);
	}

	public static void setItemName(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}

	public static void setItemLore(ItemStack item, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public static void setItemUnbreak(ItemStack item, boolean unbreak) {
		ItemMeta meta = item.getItemMeta();
		try {
			meta.setUnbreakable(unbreak);
		} catch (Exception e) {
			meta.spigot().setUnbreakable(unbreak);
		}
		item.setItemMeta(meta);
	}

	public static void addItemFlags(ItemStack item, ItemFlag... flags) {
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(flags);
		item.setItemMeta(meta);
	}

	public String getItemName(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName()) {
			return item.getType().name();
		}
		return item.getItemMeta().getDisplayName();
	}

	public List<String> getItemLore(ItemStack item) {
		return item.getItemMeta().getLore();
	}

	public Set<ItemFlag> getItemFlags(ItemStack item) {
		return item.getItemMeta().getItemFlags();
	}
}
