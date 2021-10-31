package me.ram.bedwarsscoreboardaddon.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.config.Config;
import me.ram.bedwarsscoreboardaddon.edit.EditGame;
import me.ram.bedwarsscoreboardaddon.network.UpdateCheck;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("bedwarsscoreboardaddon")) {
			if (args.length == 0) {
				sender.sendMessage("§f=====================================================");
				sender.sendMessage("");
				sender.sendMessage("§b               BedwarsScoreBoardAddon");
				sender.sendMessage("");
				sender.sendMessage("§f  " + Main.getInstance().getLocaleConfig().getLanguage("version") + ": §a" + Main.getVersion());
				sender.sendMessage("");
				sender.sendMessage("§f  " + Main.getInstance().getLocaleConfig().getLanguage("author") + ": §aRam");
				sender.sendMessage("");
				sender.sendMessage("§f=====================================================");
				return true;
			}
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage("§f=====================================================");
				sender.sendMessage("");
				sender.sendMessage("§b§l BedwarsScoreBoardAddon §fv" + Main.getVersion() + "  §7by Ram");
				sender.sendMessage("");
				Config.getLanguageList("commands.help").forEach(line -> {
					sender.sendMessage(line);
				});
				sender.sendMessage("");
				sender.sendMessage("§f=====================================================");
				return true;
			}
			if (args[0].equalsIgnoreCase("upcheck")) {
				if (!sender.hasPermission("bedwarsscoreboardaddon.updatecheck")) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.no_permission"));
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
						if (Main.getInstance().getMenuManager().isOpen(p)) {
							p.closeInventory();
						}
					}
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.reloaded"));
					return true;
				} else {
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.no_permission"));
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("spawner") && args.length > 1) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.not_player"));
					return true;
				}
				Player player = (Player) sender;
				if (args[1].equalsIgnoreCase("add")) {
					if (!player.hasPermission("bedwarsscoreboardaddon.spawner.set")) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.no_permission"));
						return true;
					}
					if (args.length < 4) {
						sender.sendMessage("");
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.add_spawner"));
						return true;
					}
					Config.setTeamSpawner(args[2], args[3], player.getLocation());
					player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.add_spawner"));
					Main.getInstance().getEditHolographicManager().displayGameLocation(player, args[2]);
					return true;
				}
				if (args[1].equalsIgnoreCase("list")) {
					if (!player.hasPermission("bedwarsscoreboardaddon.spawner.list")) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.no_permission"));
						return true;
					}
					if (args.length == 2) {
						sender.sendMessage("");
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.spawner_list"));
						return true;
					}
					if (!Config.game_team_spawner.containsKey(args[2])) {
						player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.spawner_list_error"));
						return true;
					}
					String game = args[2];
					sendSpawnerList(player, game);
					return true;
				}
				if (args[1].equalsIgnoreCase("remove")) {
					if (!player.hasPermission("bedwarsscoreboardaddon.spawner.remove")) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.no_permission"));
						return true;
					}
					if (args.length == 2) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.remove_spawner"));
						return true;
					}
					if (Config.game_team_spawners.containsKey(args[2])) {
						String shop = Config.game_team_spawners.get(args[2]);
						Config.removeShop(shop);
						String game = shop.split("\\.")[0];
						Main.getInstance().getEditHolographicManager().displayGameLocation(player, game);
						if (args.length > 3 && args[3].equalsIgnoreCase("true")) {
							sendSpawnerList(player, game);
						}
						player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.remove_spawner"));
						return true;
					} else {
						player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.failed_remove_spawner"));
						return true;
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("spawner")) {
				sender.sendMessage("");
				sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.spawner_list"));
				sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.remove_spawner"));
				sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.add_spawner"));
				return true;
			}
			if (args[0].equalsIgnoreCase("shop") && args.length > 1) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.not_player"));
					return true;
				}
				Player player = (Player) sender;
				if (!Main.getInstance().isEnabledCitizens() && (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("list"))) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Main.getInstance().getLocaleConfig().getLanguage("no_citizens"));
					return true;
				}
				if (args[1].equalsIgnoreCase("set")) {
					if (!player.hasPermission("bedwarsscoreboardaddon.shop.set")) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.no_permission"));
						return true;
					}
					if (args.length == 2) {
						sender.sendMessage("");
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.set_item_shop"));
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.set_team_shop"));
						return true;
					}
					if (args[2].equalsIgnoreCase("item")) {
						if (args.length == 4) {
							if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
								Config.setShop(args[3], player.getLocation(), "item");
								player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.set_item_shop"));
								Main.getInstance().getEditHolographicManager().displayGameLocation(player, args[3]);
							} else {
								player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.set_shop_error"));
								player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.failed_set_shop"));
							}
							return true;
						} else {
							sender.sendMessage("");
							sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.set_item_shop"));
							return true;
						}
					}
					if (args[2].equalsIgnoreCase("team")) {
						if (args.length == 4) {
							if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
								Config.setShop(args[3], player.getLocation(), "team");
								player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.set_item_shop"));
								Main.getInstance().getEditHolographicManager().displayGameLocation(player, args[3]);
							} else {
								player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.set_shop_error"));
								player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.failed_set_shop"));
							}
							return true;
						} else {
							sender.sendMessage("");
							sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.set_team_shop"));
							return true;
						}
					}
					sender.sendMessage("");
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.set_item_shop"));
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.set_team_shop"));
					return true;

				}
				if (args[1].equalsIgnoreCase("list")) {
					if (!player.hasPermission("bedwarsscoreboardaddon.shop.list")) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.no_permission"));
						return true;
					}
					if (args.length == 2) {
						sender.sendMessage("");
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.shop_list"));
						return true;
					}
					if (!Config.game_shop_item.containsKey(args[2]) && !Config.game_shop_team.containsKey(args[2])) {
						player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.shop_list_error"));
						return true;
					}
					String game = args[2];
					sendShopList(player, game);
					return true;
				}
				if (args[1].equalsIgnoreCase("remove")) {
					if (!player.hasPermission("bedwarsscoreboardaddon.shop.remove")) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.no_permission"));
						return true;
					}
					if (args.length == 2) {
						sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.remove_shop"));
						return true;
					}
					if (Config.game_shop_shops.containsKey(args[2])) {
						String shop = Config.game_shop_shops.get(args[2]);
						Config.removeShop(shop);
						String game = shop.split("\\.")[0];
						Main.getInstance().getEditHolographicManager().displayGameLocation(player, game);
						if (args.length > 3 && args[3].equalsIgnoreCase("true")) {
							sendShopList(player, game);
						}
						player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.remove_shop"));
						return true;
					} else {
						player.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.failed_remove_shop"));
						return true;
					}
				}
			}
			if (args[0].equalsIgnoreCase("shop")) {
				sender.sendMessage("");
				sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.shop_list"));
				sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.remove_shop"));
				sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.set_item_shop"));
				sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.set_team_shop"));
				return true;
			}
			if (args[0].equalsIgnoreCase("edit")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.not_player"));
					return true;
				}
				if (!sender.hasPermission("bedwarsscoreboardaddon.edit")) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.no_permission"));
					return true;
				}
				if (args.length == 1) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.edit_game"));
					return true;
				}
				Game game = BedwarsRel.getInstance().getGameManager().getGame(args[1]);
				if (game == null) {
					sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.edit_game_error"));
				} else {
					EditGame.editGame((Player) sender, game);
				}
				return true;
			}
			sender.sendMessage(Config.getLanguage("commands.message.prefix") + Config.getLanguage("commands.message.help.unknown"));
		}
		return true;
	}

	private void sendShopList(Player player, String game) {
		player.sendMessage("");
		player.sendMessage(Config.getLanguage("commands.message.shop_list"));
		player.sendMessage("");
		if (Config.game_shop_item.containsKey(game)) {
			for (String loc : Config.game_shop_item.get(game)) {
				try {
					Config.game_shop_shops.forEach((id, pl) -> {
						if (pl.equals(game + ".shop.item - " + loc)) {
							player.sendMessage("§f ID: §a" + id + " §f[§e" + loc.replace(",", "§f,§e") + "§f]");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " {\"text\":\" \",\"extra\":[{\"text\":\"" + Config.getLanguage("button.list_teleport") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bwsbatp " + game + " " + loc + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + Config.getLanguage("show_text.list_teleport") + "\"}},{\"text\":\"  \"},{\"text\":\"" + Config.getLanguage("button.list_remove") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bedwarsscoreboardaddon:bwsba shop remove " + id + " true\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + Config.getLanguage("show_text.list_remove") + "\"}}]}");
							player.sendMessage("");
						}
					});
				} catch (Exception e) {
				}
			}
		}
		if (Config.game_shop_team.containsKey(game)) {
			for (String loc : Config.game_shop_team.get(game)) {
				try {
					Config.game_shop_shops.forEach((id, pl) -> {
						if (pl.equals(game + ".shop.team - " + loc)) {
							player.sendMessage("§f ID: §a" + id + " §f[§e" + loc.replace(",", "§f,§e") + "§f]");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " {\"text\":\" \",\"extra\":[{\"text\":\"" + Config.getLanguage("button.list_teleport") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bwsbatp " + game + " " + loc + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + Config.getLanguage("show_text.list_teleport") + "\"}},{\"text\":\"  \"},{\"text\":\"" + Config.getLanguage("button.list_remove") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bedwarsscoreboardaddon:bwsba shop remove " + id + " true\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + Config.getLanguage("show_text.list_remove") + "\"}}]}");
							player.sendMessage("");
						}
					});
				} catch (Exception e) {
				}
			}
		}
		Main.getInstance().getEditHolographicManager().displayGameLocation(player, game);
	}

	private void sendSpawnerList(Player player, String game) {
		player.sendMessage("");
		player.sendMessage(Config.getLanguage("commands.message.spawner_list"));
		player.sendMessage("");
		if (Config.game_team_spawner.containsKey(game)) {
			Config.game_team_spawner.get(game).forEach((team, locs) -> {
				locs.forEach(line -> {
					try {
						String loc = locationToString(line);
						Config.game_team_spawners.forEach((id, pl) -> {
							if (pl.equals(game + ".team_spawner." + team + " - " + loc)) {
								player.sendMessage("§f ID: §a" + id + " §f[" + team + "§f]" + " §f[§e" + loc.replace(",", "§f,§e") + "§f]");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " {\"text\":\" \",\"extra\":[{\"text\":\"" + Config.getLanguage("button.list_teleport") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bwsbatp " + game + " " + loc + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + Config.getLanguage("show_text.list_teleport") + "\"}},{\"text\":\"  \"},{\"text\":\"" + Config.getLanguage("button.list_remove") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bedwarsscoreboardaddon:bwsba spawner remove " + id + " true\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + Config.getLanguage("show_text.list_remove") + "\"}}]}");
								player.sendMessage("");
							}
						});
					} catch (Exception e) {
					}
				});
			});
		}
		Main.getInstance().getEditHolographicManager().displayGameLocation(player, game);
	}

	private String locationToString(Location location) {
		return location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ() + ", " + location.getYaw() + ", " + location.getPitch();
	}
}
