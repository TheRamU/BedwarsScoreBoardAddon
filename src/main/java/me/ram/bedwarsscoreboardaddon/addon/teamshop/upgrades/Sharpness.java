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

public class Sharpness implements Upgrade {

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

	public Sharpness(Game game, Team team, int level) {
		this.game = game;
		this.team = team;
		this.level = level;
	}

	public UpgradeType getType() {
		return UpgradeType.SHARPNESS;
	}

	public String getName() {
		return Config.teamshop_upgrade_name.get(getType());
	}

	public void runUpgrade() {
		for (Player player : team.getPlayers()) {
			if (BedwarsUtil.isSpectator(game, player)) {
				continue;
			}
			int i = player.getInventory().getContents().length;
			ItemStack[] stacks = player.getInventory().getContents();
			for (int j = 0; j < i; j++) {
				final ItemStack stack = stacks[j];
				if (stack != null && (stack.getType().name().contains("_SWORD") || stack.getType().name().contains("_AXE"))) {
					ItemStack itemStack = stack;
					ItemMeta itemMeta = itemStack.getItemMeta();
					if (level > 0) {
						if (itemMeta.getLore() == null) {
							setSharpness(player, itemStack, itemMeta, j, level, true);
						} else if (!itemMeta.getLore().contains("§s§1§0§0§0§" + level)) {
							setSharpness(player, itemStack, itemMeta, j, level, true);
						}
					} else if (itemMeta.getLore() != null) {
						if (itemMeta.getLore().contains("§s§1§0§0§0§1")) {
							setSharpness(player, itemStack, itemMeta, j, 1, false);
						}
						if (itemMeta.getLore().contains("§s§1§0§0§0§2")) {
							setSharpness(player, itemStack, itemMeta, j, 2, false);
						}
					}
				}
			}
		}
	}

	private void setSharpness(Player player, ItemStack itemStack, ItemMeta itemMeta, int j, int k, boolean b) {
		List<String> lores = new ArrayList<String>();
		int ol = 0;
		if (itemMeta.getLore() != null) {
			for (String lore : itemMeta.getLore()) {
				lores.add(lore);
				if (lore.equals("§s§1§0§0§0§1")) {
					ol = 1;
				}
				if (lore.equals("§s§1§0§0§0§2")) {
					ol = 2;
				}
			}
		}
		if (b) {
			lores.remove("§s§1§0§0§0§1");
			lores.remove("§s§1§0§0§0§2");
			lores.add("§s§1§0§0§0§" + k);
			itemMeta.setLore(lores);
			itemStack.setItemMeta(itemMeta);
			int el = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) - ol;
			itemStack.removeEnchantment(Enchantment.DAMAGE_ALL);
			int nl = el + k;
			if (nl > 0) {
				itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, nl);
			}
		} else {
			lores.remove("§s§1§0§0§0§1");
			lores.remove("§s§1§0§0§0§2");
			itemMeta.setLore(lores);
			itemStack.setItemMeta(itemMeta);
			int el = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) - k;
			itemStack.removeEnchantment(Enchantment.DAMAGE_ALL);
			if (el > 0) {
				itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, el);
			}
		}
		player.getInventory().setItem(j, itemStack);
	}
}
