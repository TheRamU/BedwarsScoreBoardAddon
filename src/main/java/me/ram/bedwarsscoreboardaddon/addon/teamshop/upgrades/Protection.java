package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import lombok.Getter;
import lombok.Setter;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;

public class Protection implements Upgrade {

	@Getter
	private Game game;
	@Getter
	private Team team;
	@Getter
	@Setter
	private int level;
	@Getter
	@Setter
	private String buyer;

	public Protection(Game game, Team team, int level) {
		this.game = game;
		this.team = team;
		this.level = level;
	}

	public UpgradeType getType() {
		return UpgradeType.PROTECTION;
	}

	public String getName() {
		return Config.teamshop_upgrade_name.get(getType());
	}

	public void runUpgrade() {
		for (Player player : team.getPlayers()) {
			if (BedwarsUtil.isSpectator(game, player)) {
				continue;
			}
			int i = player.getInventory().getContents().length + 4;
			ItemStack[] stacks = player.getInventory().getContents();
			for (int m = 0; m < i; m++) {
				int j = m - 4;
				ItemStack stack = null;
				if (j >= 0) {
					stack = stacks[j];
				} else if (j == -1) {
					stack = player.getInventory().getHelmet();
				} else if (j == -2) {
					stack = player.getInventory().getChestplate();
				} else if (j == -3) {
					stack = player.getInventory().getLeggings();
				} else if (j == -4) {
					stack = player.getInventory().getBoots();
				}
				if (stack != null && (stack.getType().name().endsWith("_HELMET") || stack.getType().name().endsWith("_CHESTPLATE") || stack.getType().name().endsWith("_LEGGINGS") || stack.getType().name().endsWith("_BOOTS"))) {
					ItemStack itemStack = stack;
					ItemMeta itemMeta = itemStack.getItemMeta();
					if (level > 0) {
						if (itemMeta.getLore() == null) {
							setProtection(player, itemStack, itemMeta, j, level, true);
						} else if (!itemMeta.getLore().contains("§a§1§0§0§0§" + level)) {
							setProtection(player, itemStack, itemMeta, j, level, true);
						}
					} else if (itemMeta.getLore() != null) {
						if (itemMeta.getLore().contains("§a§1§0§0§0§1")) {
							setProtection(player, itemStack, itemMeta, j, 1, false);
						} else if (itemMeta.getLore().contains("§a§1§0§0§0§2")) {
							setProtection(player, itemStack, itemMeta, j, 2, false);
						} else if (itemMeta.getLore().contains("§a§1§0§0§0§3")) {
							setProtection(player, itemStack, itemMeta, j, 3, false);
						} else if (itemMeta.getLore().contains("§a§1§0§0§0§4")) {
							setProtection(player, itemStack, itemMeta, j, 4, false);
						}
					}
				}
			}
		}
	}

	private void setProtection(Player player, ItemStack itemStack, ItemMeta itemMeta, int j, int k, boolean b) {
		List<String> lores = new ArrayList<String>();
		int ol = 0;
		if (itemMeta.getLore() != null) {
			for (String lore : itemMeta.getLore()) {
				lores.add(lore);
				if (lore.equals("§a§1§0§0§0§1")) {
					ol = 1;
				}
				if (lore.equals("§a§1§0§0§0§2")) {
					ol = 2;
				}
				if (lore.equals("§a§1§0§0§0§3")) {
					ol = 3;
				}
				if (lore.equals("§a§1§0§0§0§4")) {
					ol = 4;
				}
			}
		}
		if (b) {
			lores.remove("§a§1§0§0§0§1");
			lores.remove("§a§1§0§0§0§2");
			lores.remove("§a§1§0§0§0§3");
			lores.remove("§a§1§0§0§0§4");
			lores.add("§a§1§0§0§0§" + k);
			itemMeta.setLore(lores);
			itemStack.setItemMeta(itemMeta);
			int el = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) - ol;
			itemStack.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
			int nl = el + k;
			if (nl > 0) {
				itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, nl);
			}
		} else {
			lores.remove("§a§1§0§0§0§1");
			lores.remove("§a§1§0§0§0§2");
			lores.remove("§a§1§0§0§0§3");
			lores.remove("§a§1§0§0§0§4");
			itemMeta.setLore(lores);
			itemStack.setItemMeta(itemMeta);
			int el = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) - k;
			itemStack.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
			if (el > 0) {
				itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, el);
			}
		}
		if (j >= 0) {
			player.getInventory().setItem(j, itemStack);
		} else if (j == -1) {
			player.getInventory().setHelmet(itemStack);
		} else if (j == -2) {
			player.getInventory().setChestplate(itemStack);
		} else if (j == -3) {
			player.getInventory().setLeggings(itemStack);
		} else if (j == -4) {
			player.getInventory().setBoots(itemStack);
		}
	}
}
