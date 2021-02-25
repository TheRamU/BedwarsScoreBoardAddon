package me.ram.bedwarsscoreboardaddon.addon;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import io.github.bedwarsrel.events.BedwarsPlayerKilledEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import me.ram.bedwarsscoreboardaddon.config.Config;

public class DeathItem implements Listener {

	@EventHandler
	public void onKilled(BedwarsPlayerKilledEvent e) {
		if (e.getKiller() == null || e.getPlayer() == null) {
			return;
		}
		Player killer = e.getKiller();
		Player player = e.getPlayer();
		Game game = e.getGame();
		if (game.getState() != GameState.RUNNING) {
			return;
		}
		if (game.getPlayerTeam(player) == null || game.getPlayerTeam(killer) == null) {
			return;
		}
		if (!Config.deathitem_enabled) {
			return;
		}
		if (game.isSpectator(killer) || killer.isDead() || killer.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}
		Map<ItemStack, Integer> playeritems = new HashMap<ItemStack, Integer>();
		for (ItemStack itemStack : player.getInventory().getContents()) {
			if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
				try {
					for (String items : Config.deathitem_items) {
						if (itemStack.getType().equals(Material.valueOf(items))) {
							Boolean l = true;
							for (ItemStack item : playeritems.keySet()) {
								if (item.getType() == itemStack.getType()) {
									playeritems.put(item, playeritems.get(item) + itemStack.getAmount());
									l = false;
									break;
								}
							}
							if (l) {
								playeritems.put(itemStack, itemStack.getAmount());
							}
						}
					}
				} catch (Exception ex) {
				}
			}
		}
		for (ItemStack item : playeritems.keySet()) {
			ItemStack itemStack = item.clone();
			itemStack.setAmount(playeritems.get(item));
			killer.getInventory().addItem(itemStack);
			String itemName = item.getType().name();
			if (Config.deathitem_item_name_chinesize) {
				itemName = getRealName(item);
			}
			killer.sendMessage(getColor(item) + Config.deathitem_message.replace("{amount}", playeritems.get(item) + "").replace("{item}", itemName));
		}
	}

	private String getColor(ItemStack item) {
		switch (item.getTypeId()) {
		case 264:
			return "§b";
		case 265:
			return "§f";
		case 266:
			return "§6";
		case 336:
			return "§c";
		case 388:
			return "§2";
		default:
			return "§f";
		}
	}

	public String getRealName(ItemStack item) {
		Map<Integer, String> m = new HashMap<Integer, String>();
		m.put(1, "石头");
		m.put(2, "草方块");
		m.put(3, "泥土");
		m.put(4, "圆石");
		m.put(5, "木板");
		m.put(6, "树苗");
		m.put(7, "基岩");
		m.put(8, "流动的水");
		m.put(9, "水");
		m.put(10, "流动的岩浆");
		m.put(11, "岩浆");
		m.put(12, "沙子");
		m.put(13, "砂砾");
		m.put(14, "金矿");
		m.put(15, "铁矿");
		m.put(16, "煤矿");
		m.put(17, "原木");
		m.put(18, "树叶");
		m.put(19, "海绵");
		m.put(20, "玻璃");
		m.put(21, "青晶石矿");
		m.put(22, "青晶石块");
		m.put(23, "发射器");
		m.put(24, "沙石");
		m.put(25, "音符盒");
		m.put(26, "床的一半(Bug方块)");
		m.put(27, "动力铁轨");
		m.put(28, "探测铁轨");
		m.put(29, "粘性活塞");
		m.put(30, "蜘蛛网");
		m.put(31, "草");
		m.put(32, "枯死的树苗");
		m.put(33, "活塞");
		m.put(34, "活塞的头(Bug方块)");
		m.put(35, "羊毛");
		m.put(36, "移动的活塞(Bug方块)");
		m.put(37, "小黄花");
		m.put(38, "花朵");
		m.put(39, "棕色蘑菇");
		m.put(40, "红色蘑菇");
		m.put(41, "金块");
		m.put(42, "铁块");
		m.put(43, "堆叠的半砖");
		m.put(44, "半砖");
		m.put(45, "砖块");
		m.put(46, "TNT");
		m.put(47, "书柜");
		m.put(48, "青苔砖");
		m.put(49, "黑曜石");
		m.put(50, "火把");
		m.put(51, "火");
		m.put(52, "刷怪笼");
		m.put(53, "橡木楼梯");
		m.put(54, "箱子");
		m.put(55, "引燃的红石导线(Bug方块)");
		m.put(56, "钻石原矿");
		m.put(57, "钻石块");
		m.put(58, "工作台");
		m.put(59, "小麦方块(Bug方块)");
		m.put(60, "耕地(Bug方块)");
		m.put(61, "熔炉");
		m.put(62, "烧着的熔炉(Bug方块)");
		m.put(63, "站着的告示牌(Bug方块)");
		m.put(64, "半边的门(Bug方块)");
		m.put(65, "梯子");
		m.put(66, "铁轨");
		m.put(67, "石楼梯");
		m.put(68, "墙上的告示牌(Bug方块)");
		m.put(69, "拉杆");
		m.put(70, "石制压力板");
		m.put(71, "半边铁门(Bug方块)");
		m.put(72, "木制压力板");
		m.put(73, "红石矿");
		m.put(74, "亮着的红石矿(Bug方块)");
		m.put(75, "不亮的红石火把(Bug方块)");
		m.put(76, "红石火把");
		m.put(77, "石制按钮");
		m.put(78, "雪");
		m.put(79, "冰");
		m.put(80, "雪方块");
		m.put(81, "仙人掌");
		m.put(82, "黏土块");
		m.put(83, "甘蔗(Bug方块)");
		m.put(84, "唱片机");
		m.put(85, "木制栅栏");
		m.put(86, "南瓜");
		m.put(87, "地狱岩");
		m.put(88, "灵魂沙");
		m.put(89, "荧石");
		m.put(90, "传送门(Bug方块)");
		m.put(91, "亮着的南瓜灯");
		m.put(92, "蛋糕(Bug方块)");
		m.put(93, "红石中继器(关)");
		m.put(94, "红石中继器(开)");
		m.put(95, "彩色玻璃");
		m.put(96, "木制陷阱门");
		m.put(97, "刷怪石");
		m.put(98, "石砖");
		m.put(99, "蘑菇块");
		m.put(100, "蘑菇块");
		m.put(101, "铁栅栏");
		m.put(102, "玻璃板");
		m.put(103, "西瓜块");
		m.put(104, "南瓜藤(Bug方块)");
		m.put(105, "西瓜藤(Bug方块)");
		m.put(106, "藤蔓");
		m.put(107, "栅栏门");
		m.put(108, "红砖楼梯");
		m.put(109, "平滑石砖楼梯");
		m.put(110, "菌丝");
		m.put(111, "睡莲");
		m.put(112, "地狱砖块");
		m.put(113, "地狱砖栅栏");
		m.put(114, "地狱砖楼梯");
		m.put(115, "地狱疣(Bug方块)");
		m.put(116, "附魔台");
		m.put(117, "站着的炼药台(Bug方块)");
		m.put(118, "炼药锅(Bug方块)");
		m.put(119, "末地传送门(Bug方块)");
		m.put(120, "末地传送门框架(?)");
		m.put(121, "末地石");
		m.put(122, "龙蛋");
		m.put(123, "红石灯");
		m.put(124, "亮着的红石灯(Bug方块)");
		m.put(125, "双层的木板台阶");
		m.put(126, "木板台阶");
		m.put(127, "可可果");
		m.put(128, "沙石台阶");
		m.put(129, "绿宝石矿");
		m.put(130, "末影箱");
		m.put(131, "拌线钩");
		m.put(132, "拌线钩的线(Bug方块)");
		m.put(133, "绿宝石块");
		m.put(134, "楼梯");
		m.put(135, "楼梯");
		m.put(136, "楼梯");
		m.put(137, "命令方块");
		m.put(138, "信标");
		m.put(139, "圆石墙");
		m.put(143, "木制按钮");
		m.put(144, "头颅(Bug方块)");
		m.put(145, "铁砧");
		m.put(146, "陷阱箱");
		m.put(147, "金制压力板");
		m.put(148, "铁制压力板");
		m.put(151, "日光探测板");
		m.put(152, "红石块");
		m.put(153, "石英矿");
		m.put(154, "漏斗");
		m.put(155, "石英块");
		m.put(156, "石英楼梯");
		m.put(157, "激活铁轨");
		m.put(158, "投掷器");
		m.put(159, "彩色黏土");
		m.put(160, "彩色玻璃板");
		m.put(161, "树叶");
		m.put(162, "原木");
		m.put(163, "楼梯");
		m.put(164, "楼梯");
		m.put(165, "粘液块");
		m.put(166, "屏障");
		m.put(167, "铁制陷阱门");
		m.put(168, "海晶石");
		m.put(169, "海晶灯");
		m.put(170, "干草块");
		m.put(171, "羊毛地毯");
		m.put(172, "硬化黏土");
		m.put(173, "煤炭块");
		m.put(174, "干冰");
		m.put(175, "花");
		m.put(179, "红沙石");
		m.put(180, "红沙石楼梯");
		m.put(181, "双层沙石台阶");
		m.put(182, "沙石半砖");
		m.put(183, "栅栏门");
		m.put(184, "栅栏门");
		m.put(185, "栅栏门");
		m.put(186, "栅栏门");
		m.put(187, "栅栏门");
		m.put(188, "栅栏门");
		m.put(189, "栅栏门");
		m.put(190, "栅栏门");
		m.put(191, "栅栏门");
		m.put(192, "栅栏门");
		m.put(256, "铁铲");
		m.put(257, "铁镐");
		m.put(258, "铁斧");
		m.put(259, "打火石");
		m.put(260, "小苹果");
		m.put(261, "弓");
		m.put(262, "箭");
		m.put(263, "煤炭");
		m.put(264, "钻石");
		m.put(265, "铁锭");
		m.put(266, "金锭");
		m.put(267, "铁剑");
		m.put(268, "木剑");
		m.put(269, "木铲");
		m.put(270, "木镐");
		m.put(271, "木斧");
		m.put(272, "石剑");
		m.put(273, "石铲");
		m.put(274, "石镐");
		m.put(275, "石斧");
		m.put(276, "钻石剑");
		m.put(277, "钻石铲");
		m.put(278, "钻石镐");
		m.put(279, "钻石斧");
		m.put(280, "木棍");
		m.put(281, "碗");
		m.put(282, "蘑菇煲");
		m.put(283, "金剑");
		m.put(284, "金铲");
		m.put(285, "金镐");
		m.put(286, "金斧");
		m.put(287, "线");
		m.put(288, "羽毛");
		m.put(289, "火药");
		m.put(290, "木锄");
		m.put(291, "石锄");
		m.put(292, "铁锄");
		m.put(293, "钻石锄");
		m.put(294, "金锄");
		m.put(295, "小麦种子");
		m.put(296, "小麦");
		m.put(297, "面包");
		m.put(298, "皮革头盔");
		m.put(299, "皮革胸甲");
		m.put(300, "皮革裤子");
		m.put(301, "皮革靴子");
		m.put(302, "锁链头盔");
		m.put(303, "锁链胸甲");
		m.put(304, "锁链裤子");
		m.put(305, "锁链靴子");
		m.put(306, "铁头盔");
		m.put(307, "铁胸甲");
		m.put(308, "铁裤子");
		m.put(309, "铁靴子");
		m.put(310, "钻石头盔");
		m.put(311, "钻石胸甲");
		m.put(312, "钻石裤子");
		m.put(313, "钻石靴子");
		m.put(314, "金头盔");
		m.put(315, "金胸甲");
		m.put(316, "金裤子");
		m.put(317, "金靴子");
		m.put(318, "硕石");
		m.put(319, "生猪肉");
		m.put(320, "熟猪肉");
		m.put(321, "画");
		m.put(322, "金苹果");
		m.put(323, "告示牌");
		m.put(324, "木门");
		m.put(325, "桶");
		m.put(326, "水桶");
		m.put(327, "岩浆桶");
		m.put(328, "矿车");
		m.put(329, "鞍");
		m.put(330, "铁门");
		m.put(331, "红石");
		m.put(332, "雪球");
		m.put(333, "船");
		m.put(334, "皮革");
		m.put(335, "牛奶");
		m.put(336, "红砖");
		m.put(337, "黏土");
		m.put(338, "甘蔗");
		m.put(339, "纸");
		m.put(340, "书");
		m.put(341, "史莱姆球");
		m.put(342, "箱子矿车");
		m.put(343, "熔炉矿车");
		m.put(344, "鸡蛋");
		m.put(345, "书");
		m.put(346, "钓鱼竿");
		m.put(347, "钟");
		m.put(348, "荧石粉");
		m.put(349, "鱼");
		m.put(350, "熟鱼");
		m.put(351, "染料");
		m.put(352, "骨头");
		m.put(353, "糖");
		m.put(354, "蛋糕");
		m.put(355, "床");
		m.put(356, "红石中继器");
		m.put(357, "曲奇");
		m.put(358, "地图(有内容的)");
		m.put(359, "剪刀");
		m.put(360, "西瓜");
		m.put(361, "南瓜种子");
		m.put(362, "西瓜种子");
		m.put(363, "生牛肉");
		m.put(364, "牛排");
		m.put(365, "生鸡肉");
		m.put(366, "鸡肉");
		m.put(367, "腐肉");
		m.put(368, "末影之眼");
		m.put(369, "烈焰棒");
		m.put(370, "恶魂之泪");
		m.put(371, "金粒");
		m.put(372, "地狱疣");
		m.put(373, "药水");
		m.put(374, "空玻璃瓶");
		m.put(375, "蜘蛛眼");
		m.put(376, "发酵的蜘蛛眼");
		m.put(377, "烈焰粉");
		m.put(378, "烈焰膏");
		m.put(379, "炼药台");
		m.put(380, "炼药锅");
		m.put(381, "末影之眼");
		m.put(382, "发光的西瓜");
		m.put(383, "刷怪蛋");
		m.put(384, "经验之瓶");
		m.put(385, "火焰弹");
		m.put(386, "书与笔");
		m.put(387, "成书");
		m.put(388, "绿宝石");
		m.put(389, "物品展示框");
		m.put(390, "花盆");
		m.put(391, "胡萝卜");
		m.put(392, "土豆");
		m.put(393, "烤土豆");
		m.put(394, "毒土豆");
		m.put(395, "地图");
		m.put(396, "金胡萝卜");
		m.put(397, "头颅");
		m.put(398, "胡萝卜钓竿");
		m.put(399, "下界之星");
		m.put(400, "南瓜派");
		m.put(401, "烟花");
		m.put(402, "烟火之星");
		m.put(403, "附魔书");
		m.put(404, "红石比较器");
		m.put(405, "地狱砖");
		m.put(406, "石英");
		m.put(407, "TNT矿车");
		m.put(408, "漏斗矿车");
		m.put(409, "海晶碎片");
		m.put(410, "海晶");
		m.put(411, "生兔肉");
		m.put(412, "熟兔肉");
		m.put(413, "兔肉煲");
		m.put(414, "兔腿");
		m.put(415, "兔皮");
		m.put(416, "盔甲架");
		m.put(417, "铁马铠");
		m.put(418, "金马铠");
		m.put(419, "钻石马铠");
		m.put(420, "栓绳");
		m.put(421, "命名牌");
		m.put(422, "命令方块矿车");
		m.put(423, "生羊肉");
		m.put(424, "熟羊肉");
		m.put(425, "旗帜");
		m.put(427, "木门");
		m.put(428, "木门");
		m.put(429, "木门");
		m.put(430, "木门");
		m.put(431, "木门");
		m.put(2256, "唱片");
		m.put(2257, "唱片");
		m.put(2258, "唱片");
		m.put(2259, "唱片");
		m.put(2260, "唱片");
		m.put(2261, "唱片");
		m.put(2262, "唱片");
		m.put(2263, "唱片");
		m.put(2264, "唱片");
		m.put(2265, "唱片");
		m.put(2266, "唱片");
		m.put(2267, "唱片");
		return m.getOrDefault(item.getTypeId(), item.getType().name());
	}
}
