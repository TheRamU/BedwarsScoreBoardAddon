
<h1 align="center"><br>🛏</br>BedwarsScoreBoardAddon</h1>

## 📌 介绍/What's this?

🪐

这是一个依赖BedwarsRel运行的插件，它可以拓展你的起床战争，为你的起床战争增添更多乐趣  

This is a plugin running on BedwarsRel. It can expand your BedWars function. Make your BedWars like Hypixel!  

## ✅ 下载/Donwloads

> 插件/Plugin:
- 下载地址: https://www.mcbbs.net/thread-814322-1-1.html  
- Download: https://www.spigotmc.org/resources/74991/

> 前置依赖/Depends：
- [BedwarsRel 1.3.6](https://www.spigotmc.org/resources/6799/)
- [ProtocolLib](https://www.spigotmc.org/resources/1997/)
- [Citizens](https://dev.bukkit.org/bukkit-plugins/citizens)

> 依赖/SoftDepends：
- [PlaceholderAPI](https://www.spigotmc.org/resources/6245/)
- [ServerJoiner](https://www.spigotmc.org/resources/53694/)
- [BedwarsXP](https://www.mcbbs.net/thread-667617-1-1.html)
- [WorldEdit](https://dev.bukkit.org/projects/worldedit)

## 📒 命令/Commands
<details>
  <summary>点击查看命令</summary>
 
| 命令 | 描述 | 权限 |
| --------- | ----- | ------- |
| /bwsba  | 显示插件信息 | | |
| /bwsba help  | 显示帮助菜单 | |
| /bwsba reload  | 重新载入配置文件 | bedwarsscoreboardaddon.reload |
| /bwsba upcheck  | 检查版本更新 | bedwarsscoreboardaddon.updatecheck |
| /bwsba edit <游戏>  | 编辑游戏 | bedwarsscoreboardaddon.edit |
| /bwsba shop list <游戏>  | 已设置商店列表 | bedwarsscoreboardaddon.shop.list |
| /bwsba shop remove <ID>  | 移除一个商店 | bedwarsscoreboardaddon.shop.remove |
| /bwsba shop set item <游戏>  | 设置一个道具商店 | bedwarsscoreboardaddon.shop.set |
| /bwsba shop set team <游戏>  | 设置一个队伍商店 | bedwarsscoreboardaddon.shop.set |
| /bwsba spawner list <游戏>  | 队伍资源点列表 | bedwarsscoreboardaddon.spawner.list |
| /bwsba spawner remove <ID>  | 移除队伍资源点 | bedwarsscoreboardaddon.remove.list |
| /bwsba spawner add <游戏> <队伍>  | 添加队伍资源点 | bedwarsscoreboardaddon.add.list |  
</details>
<details>
  <summary>Click to show the commands</summary>

| Command | Description | Permission |
| --------- | ----- | ------- |
| /bwsba  | Plugin info | | |
| /bwsba help  | Get help | |
| /bwsba reload  | Reload configuration | bedwarsscoreboardaddon.reload |
| /bwsba upcheck  | Update check | bedwarsscoreboardaddon.updatecheck |
| /bwsba edit <Game>  | Edit game | bedwarsscoreboardaddon.edit |
| /bwsba shop list <Game>  | Shop list | bedwarsscoreboardaddon.shop.list |
| /bwsba shop remove <ID>  | Remove a shop | bedwarsscoreboardaddon.shop.remove |
| /bwsba shop set item <Game>  | Add a item shop | bedwarsscoreboardaddon.shop.set |
| /bwsba shop set team <Game>  | Add a team shop | bedwarsscoreboardaddon.shop.set |
| /bwsba spawner list <Game>  | Team spawner list | bedwarsscoreboardaddon.spawner.list |
| /bwsba spawner remove <ID>  | Remove a team spawner | bedwarsscoreboardaddon.remove.list |
| /bwsba spawner add <Game> <Team>  | Add a team spawner | bedwarsscoreboardaddon.add.list |
</details>

## ⚙ 安装与设置/Install & Settings
### > 安装/Install

```ini
1.确保服务器安装了前置插件BedwarsRel 1.3.6, ProtocolLib, Citizens  

3.将下载的插件(Jar文件)放入服务器目录下的 "plugins" 文件夹内  

5.重启(启动)服务器  
```

```ini
1.Confirm your server is running BedwarsRel 1.3.6, ProtocolLib, Citizens  

3.Put the downloaded plugin (Jar file) into the "plugins" folder under the server root directory  

5.Restart(Start) server  
```

###  > 切换语言/Change language

```ini
1.打开目录 "plugins\BedwarsScoreBoardAddon\locale\"  
1.Open folder "plugins\BedwarsScoreBoardAddon\locale\"  
```

![](https://raw.githubusercontent.com/TheRamU/BedwarsScoreBoardAddon/master/images/locale.png)

```ini
2.找到要切换的语言并进入目录  
2.Find the language you want to change, and enter this folder  
```

![](https://raw.githubusercontent.com/TheRamU/BedwarsScoreBoardAddon/master/images/language.png)

```ini
3.将目录下的所有文件复制，替换掉 "plugins\BedwarsScoreBoardAddon\" 目录下的原文件  
3.Copy all files in this folder, replace the original file in the "plugins\BedwarsScoreBoardAddon\" folder  
```

![](https://raw.githubusercontent.com/TheRamU/BedwarsScoreBoardAddon/master/images/replace_language.png)

```ini
4.执行指令 "/bwsba reload" 重载配置文件  
4.Use command "/bwsba reload" to reload configuration  
```

###  > 添加商店/Add Shop

```ini
1.执行命令 "/bwsba edit <游戏>" 进入游戏编辑模式，点击 "设置游戏/队伍商店"
1.Use command "/bwsba edit <Game>" to enter game edit mode，Click "Set item/team shop"
```

![](https://raw.githubusercontent.com/TheRamU/BedwarsScoreBoardAddon/master/images/set_shop.png)

```ini
2.执行命令 "/bw join <游戏>" 就行测试
2.Use command "/bw join <Game>" to test
```

![](https://raw.githubusercontent.com/TheRamU/BedwarsScoreBoardAddon/master/images/shop.png)

###  > 添加钻石资源/Add Diamond Resources

```ini
1.编辑文件 "plugins/BedwarsRel/config.yml"，在 "resource" 中添加:
1.Edit file ""plugins/BedwarsRel/config.yml", Find "resource", add:

  diamond:  
    item:  
    - type: DIAMOND  
      meta:  
        ==: ItemMeta  
        meta-type: UNSPECIFIC  
        display-name: "§bDiamond"  
    spawn-interval: 30000  
    spread: 0.0  

```

![](https://raw.githubusercontent.com/TheRamU/BedwarsScoreBoardAddon/master/images/edit_bwr_config.png)

```ini
2.执行命令 "/bwsba edit <游戏>" 进入游戏编辑模式，点击 "设置资源生成点-游戏资源生成点-钻石"
3.Use command "/bwsba edit <Game>" to enter game edit mode，Click "Set resource spawner-Game resource spawner-Diamond"
```

![](https://raw.githubusercontent.com/TheRamU/BedwarsScoreBoardAddon/master/images/add_diamond.png)

```ini
2.执行命令 "/bw join <游戏>" 就行测试
2.Use command "/bw join <Game>" to test
```

![](https://raw.githubusercontent.com/TheRamU/BedwarsScoreBoardAddon/master/images/diamond_generator.png)
