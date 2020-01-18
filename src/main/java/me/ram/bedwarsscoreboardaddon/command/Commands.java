package me.ram.bedwarsscoreboardaddon.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.arena.Arena;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.edit.EditGame;
import me.ram.bedwarsscoreboardaddon.networld.UpdateCheck;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("bedwarsscoreboardaddon")) {
			if (args.length == 0) {
				sender.sendMessage("¡ìf===========================================================");
				sender.sendMessage("");
				sender.sendMessage("¡ìb                  BedwarsScoreBoardAddon");
				sender.sendMessage("");
				sender.sendMessage("¡ìf  " + Config.getLanguage("commands.version") + ": ¡ìa" + Main.getVersion());
				sender.sendMessage("");
				sender.sendMessage("¡ìf  " + Config.getLanguage("commands.author") + ": ¡ìaRam");
				sender.sendMessage("");
				sender.sendMessage("¡ìf===========================================================");
				return true;
			}
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage("¡ìf===========================================================");
				sender.sendMessage("");
				sender.sendMessage("¡ìb¡ìl BedwarsScoreBoardAddon ¡ìfv" + Main.getVersion() + "  ¡ì7by Ram");
				sender.sendMessage("");
				Config.getLanguageList("commands.help").forEach(line -> {
					sender.sendMessage(line);
				});
				sender.sendMessage("");
				sender.sendMessage("¡ìf===========================================================");
				return true;
			}
			if (args[0].equalsIgnoreCase("upcheck")) {
				if (!sender.hasPermission("bedwarsscoreboardaddon.updatecheck")) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix")
							+ Config.getLanguage("commands.message.no_permission"));
					return true;
				} else {
					UpdateCheck.upCheck(sender);
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("bedwarsscoreboardaddon.reload")) {
					Config.loadConfig();
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.getOpenInventory().getTitle().equalsIgnoreCase(Config.teamshop_title)) {
							for (Arena arena : Main.getInstance().getArenaManager().getArenas().values()) {
								arena.getTeamShop().setTeamShopItem(p, p.getOpenInventory().getTopInventory());
							}
						}
					}
					sender.sendMessage(Config.getLanguage("commands.message.prefix")
							+ Config.getLanguage("commands.message.reloaded"));
					return true;
				} else {
					sender.sendMessage(Config.getLanguage("commands.message.prefix")
							+ Config.getLanguage("commands.message.no_permission"));
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("shop") && args.length > 1) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix")
							+ Config.getLanguage("commands.message.not_player"));
					return true;
				}
				Player player = (Player) sender;
				if (args[1].equalsIgnoreCase("set")) {
					if (!player.hasPermission("bedwarsscoreboardaddon.shop.set")) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix")
								+ Config.getLanguage("commands.message.no_permission"));
						return true;
					} else {
						if (args.length == 2) {
							sender.sendMessage("");
							sender.sendMessage(Config.getLanguage("commands.message.prefix")
									+ Config.getLanguage("commands.message.help.set_item_shop"));
							sender.sendMessage(Config.getLanguage("commands.message.prefix")
									+ Config.getLanguage("commands.message.help.set_team_shop"));
							return true;
						}
						if (args[2].equalsIgnoreCase("item")) {
							if (args.length == 4) {
								if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
									Config.setShop(args[3], player.getLocation(), "item");
									player.sendMessage(Config.getLanguage("commands.message.prefix")
											+ Config.getLanguage("commands.message.set_item_shop"));
									Main.getInstance().getHolographicManager().displayGameLocation(player, args[3]);
								} else {
									player.sendMessage(Config.getLanguage("commands.message.prefix")
											+ Config.getLanguage("commands.message.set_shop_error"));
									player.sendMessage(Config.getLanguage("commands.message.prefix")
											+ Config.getLanguage("commands.message.failed_set_shop"));
								}
								return true;
							} else {
								sender.sendMessage("");
								sender.sendMessage(Config.getLanguage("commands.message.prefix")
										+ Config.getLanguage("commands.message.help.set_item_shop"));
								return true;
							}
						}
						if (args[2].equalsIgnoreCase("team")) {
							if (args.length == 4) {
								if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
									Config.setShop(args[3], player.getLocation(), "team");
									player.sendMessage(Config.getLanguage("commands.message.prefix")
											+ Config.getLanguage("commands.message.set_item_shop"));
									Main.getInstance().getHolographicManager().displayGameLocation(player, args[3]);
								} else {
									player.sendMessage(Config.getLanguage("commands.message.prefix")
											+ Config.getLanguage("commands.message.set_shop_error"));
									player.sendMessage(Config.getLanguage("commands.message.prefix")
											+ Config.getLanguage("commands.message.failed_set_shop"));
								}
								return true;
							} else {
								sender.sendMessage("");
								sender.sendMessage(Config.getLanguage("commands.message.prefix")
										+ Config.getLanguage("commands.message.help.set_team_shop"));
								return true;
							}
						}
						sender.sendMessage("");
						sender.sendMessage(Config.getLanguage("commands.message.prefix")
								+ Config.getLanguage("commands.message.help.set_item_shop"));
						sender.sendMessage(Config.getLanguage("commands.message.prefix")
								+ Config.getLanguage("commands.message.help.set_team_shop"));
						return true;
					}
				}
				if (args[1].equalsIgnoreCase("list")) {
					if (!player.hasPermission("bedwarsscoreboardaddon.shop.list")) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix")
								+ Config.getLanguage("commands.message.no_permission"));
						return true;
					} else {
						if (args.length == 2) {
							sender.sendMessage("");
							sender.sendMessage(Config.getLanguage("commands.message.prefix")
									+ Config.getLanguage("commands.message.help.shop_list"));
							return true;
						}
						if (!Config.shop_item.containsKey(args[2]) && !Config.shop_team.containsKey(args[2])) {
							player.sendMessage(Config.getLanguage("commands.message.prefix")
									+ Config.getLanguage("commands.message.shop_list_error"));
							return true;
						}
						String game = args[2];
						sendShopList(player, game);
						return true;
					}
				}
				if (args[1].equalsIgnoreCase("remove")) {
					if (!player.hasPermission("bedwarsscoreboardaddon.shop.remove")) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix")
								+ Config.getLanguage("commands.message.no_permission"));
						return true;
					} else {
						if (args.length == 2) {
							sender.sendMessage(Config.getLanguage("commands.message.prefix")
									+ Config.getLanguage("commands.message.help.remove_shop"));
							return true;
						}
						if (Config.shop_shops.containsKey(args[2])) {
							String shop = Config.shop_shops.get(args[2]);
							Config.removeShop(shop);
							String game = shop.split("\\.")[1];
							Main.getInstance().getHolographicManager().displayGameLocation(player, game);
							if (args.length > 3 && args[3].equalsIgnoreCase("true")) {
								sendShopList(player, game);
							}
							player.sendMessage(Config.getLanguage("commands.message.prefix")
									+ Config.getLanguage("commands.message.remove_shop"));
							return true;
						} else {
							player.sendMessage(Config.getLanguage("commands.message.prefix")
									+ Config.getLanguage("commands.message.failed_remove_shop"));
							return true;
						}
					}
				}
			}
			if (args[0].equalsIgnoreCase("shop")) {
				sender.sendMessage("");
				sender.sendMessage(Config.getLanguage("commands.message.prefix")
						+ Config.getLanguage("commands.message.help.shop_list"));
				sender.sendMessage(Config.getLanguage("commands.message.prefix")
						+ Config.getLanguage("commands.message.help.remove_shop"));
				sender.sendMessage(Config.getLanguage("commands.message.prefix")
						+ Config.getLanguage("commands.message.help.set_item_shop"));
				sender.sendMessage(Config.getLanguage("commands.message.prefix")
						+ Config.getLanguage("commands.message.help.set_team_shop"));
				return true;
			}
			if (args[0].equalsIgnoreCase("edit")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix")
							+ Config.getLanguage("commands.message.not_player"));
					return true;
				}
				if (!sender.hasPermission("bedwarsscoreboardaddon.edit")) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix")
							+ Config.getLanguage("commands.message.no_permission"));
					return true;
				} else {
					if (args.length == 1) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix")
								+ Config.getLanguage("commands.message.help.edit_game"));
						return true;
					}
					Game game = BedwarsRel.getInstance().getGameManager().getGame(args[1]);
					if (game == null) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix")
								+ Config.getLanguage("commands.message.edit_game_error"));
					} else {
						EditGame.editGame((Player) sender, game);
					}
					return true;
				}
			}
			sender.sendMessage(Config.getLanguage("commands.message.prefix")
					+ Config.getLanguage("commands.message.help.unknown"));
		}
		return true;
	}

	private void sendShopList(Player player, String game) {
		player.sendMessage("");
		player.sendMessage(Config.getLanguage("commands.message.shop_list"));
		player.sendMessage("");
		if (Config.shop_item.containsKey(game)) {
			for (String loc : Config.shop_item.get(game)) {
				try {
					Config.shop_shops.forEach((id, pl) -> {
						if (pl.equals("shop." + game + ".item - " + loc)) {
							player.sendMessage("¡ìf ID: ¡ìa" + id + " ¡ìf[¡ìe" + loc.replace(",", "¡ìf,¡ìe") + "¡ìf]");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName()
									+ " {\"text\":\" \",\"extra\":[{\"text\":\""
									+ Config.getLanguage("button.shop_list_teleport")
									+ "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bwsbatp " + game + " "
									+ loc + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\""
									+ Config.getLanguage("show_text.shop_list_teleport")
									+ "\"}},{\"text\":\"  \"},{\"text\":\""
									+ Config.getLanguage("button.shop_list_remove")
									+ "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bedwarsscoreboardaddon:bwsba shop remove "
									+ id + " true\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\""
									+ Config.getLanguage("show_text.shop_list_remove") + "\"}}]}");
							player.sendMessage("");
						}
					});
				} catch (Exception e) {
				}
			}
		}
		if (Config.shop_team.containsKey(game)) {
			for (String loc : Config.shop_team.get(game)) {
				try {
					Config.shop_shops.forEach((id, pl) -> {
						if (pl.equals("shop." + game + ".team - " + loc)) {
							player.sendMessage("¡ìf ID: ¡ìa" + id + " ¡ìf[¡ìe" + loc.replace(",", "¡ìf,¡ìe") + "¡ìf]");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName()
									+ " {\"text\":\" \",\"extra\":[{\"text\":\""
									+ Config.getLanguage("button.shop_list_teleport")
									+ "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bwsbatp " + game + " "
									+ loc + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\""
									+ Config.getLanguage("show_text.shop_list_teleport")
									+ "\"}},{\"text\":\"  \"},{\"text\":\""
									+ Config.getLanguage("button.shop_list_remove")
									+ "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bedwarsscoreboardaddon:bwsba shop remove "
									+ id + " true\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\""
									+ Config.getLanguage("show_text.shop_list_remove") + "\"}}]}");
							player.sendMessage("");
						}
					});
				} catch (Exception e) {
				}
			}
		}
		Main.getInstance().getHolographicManager().displayGameLocation(player, game);
	}
}
