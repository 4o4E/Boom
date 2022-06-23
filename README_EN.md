# Boom

> Bukkit APi-based management plugin

Support `Bukkit` branch such as `Bukkit`/`Spigot`/`Paper`/`Purpur`

Support `1.13.x`-`1.19.x`, `1.16.x`-`1.18.x` tested

Provide complete language files, welcome pr other language files

**1.12 and below are not supported**

**`Sponge` core is not supported**

**Does not support `Mohist`/`Arclight`/`CatServer` etc. any server core with mod support added, handling of MOD entities is not supported**(You can use it, but please don't report any problems here)

[![Release](https://img.shields.io/github/v/release/4o4E/Boom?label=Release)](https://github.com/4o4E/Boom/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/4o4E/Boom/total?label=Download)](https://github.com/4o4E/Boom/releases)

# support function

All of the following configurations can be configured individually by world(Under `global` are the global settings, Under `each.<world name>` are the settings for individual worlds)

- control entity explosion
- prevent fire from spreading
- prevent fire from destroying blocks
- protect farmland from being trampled by entities
- prevent entity transformation (villager, witch, zombie villager, drowned)
- prevent enderman from lifting blocks
- straighten itself when making armorstand spawn(made armorstand have two hands by default)
- keep items on death
- preserve experiences on death
- prevent the use of the bed
- prevent the use of respawn anchor
- prevent players from using commands(The function of command transform will skip this detection)
- command transform enter command `a` to use command `b`(can trigger multiple commands)(supports the transfer of console commands)
- restrict entity generation(percentage)

# configuration

[config.yml](src/main/resources/config.yml)

# lang

[lang.yml](src/main/resources/lang.yml)

pr other language files are welcome

# how to configure

```yaml
# The configuration under global is the global configuration
global:
  explosion:
    CREEPER:
      enable: false
      cancel: false
  disable_fire_spread: false
  disable_fire_burn: false
  # other configuration is omitted here...

# The configuration under each is the configuration of a separate world
each:
  # !! In actual processing, the configuration of the corresponding world will be found first, if not found, the configuration in global will be used !!
  # Here you only need to write a configuration different from global, the same can be omitted
  # Here example_world1 and example_world2 are used as world names, and they are changed to the names of their own worlds in actual use.
  # such as world or world_nether, etc.
  # World names are case-sensitive, no extra spaces
  # If you don't know the name of your world, you can use the client (requires permission) to execute bm world to view the name of the world you are currently in.
  example_world1:
    explosion:
      CREEPER:
        enable: true
        cancel: false
    disable_fire_spread: false
    disable_fire_burn: false
  example_world2:
    explosion:
      CREEPER:
        enable: true
        cancel: false
    disable_fire_spread: true
    disable_fire_burn: true
  # other configuration is omitted here...

```

According to the above configuration

In the world `example_world1`, creeper explosions do not break blocks

In the world `example_world2`, fire does not spread and burn blocks

In all other worlds, creeper explosions break blocks, fire spreads and burns blocks

# command

> The main command of the plugin is `boom`, including the alias `bm`, if it conflicts with other plugin commands, please use `boom`

- `/bm reload` Reload plugin
- `/bm debug` Toggles acceptance of debug messages
- `/bm world` View current world name
- `/bm sun` Toggle current world weather to sunny for the next 10 minutes
- `/bm sun <world>` Toggles specifying that the world weather will be sunny for the next 10 minutes
- `/bm sun <world> <duration>` Toggles the specified world weather to be sunny for the next specified period of time
- `/bm rain` Toggle current world weather to rain for the next 10 minutes
- `/bm rain <world>` Toggles specifying world weather to be rain for the next 10 minutes
- `/bm rain <world> <duration>` Toggle the specified world weather to rain for the next specified duration
- `/bm thunder` Toggle current world weather to thunderstorms for the next 10 minutes
- `/bm thunder <world>` Toggles specifying world weather to be thunderstorms for the next 10 minutes
- `/bm thunder <world> <duration>` Toggles the specified world weather to be thunderstorms for the next specified duration
- `/bm ls` Toggle current world weather to sunny for the next hour
- `/bm stick` Get the debug stick(used to modify armor stands and display frames)

# permission

- `boom.admin` Allow use of plugin directives
- `boom.bypass.command` Allow skipping instruction filtering
- `boom.weather` All weather commands are allowed

  **child permission node**
  - `boom.weather.sun` Allow to switch the weather to sunny
  - `boom.weather.rain` Allow to switch weather to rain
  - `boom.weather.thunder` Allow switching weather to thunderstorm

- `boom.stick` Allows getting the debug stick, modifying armor stand and item frame with the debug stick

# download

- [latest version](https://github.com/4o4E/Boom/releases/latest)

# known issues

- [ ] Armor Stand Debug Stick Sneak-clicking on an armorstand to modify its hitbox sometimes triggers twice in a row

  Solution: Sneak and click on the block close to the armorstand
- [x] ~~Lightning protection for villagers will prevent villagers from becoming zombie villagers(killed by zombies is directly dead)~~ already fixed

# update record

```
2021.01.07 插件发布-1.0.0
2021.01.07 插件更新-1.0.1（添加reload指令）
2021.01.11 插件更新-1.0.2（添加阻止时的粒子、音效自定义内容）
2021.01.18 插件更新-1.0.3（修复bug）
2021.02.01 插件更新-1.0.4（添加阻止蝙蝠生成的选项）（主要是自己的空岛服要用XD）
2021.02.09 插件更新-1.0.5  (添加禁用指令的使用和tab补全)
2021.02.14 插件更新-1.0.6（移除单独的阻止蝙蝠生成选项，添加所有生物的概率生成/禁用生成）
2021.02.21 插件更新-1.1.0（重写部分代码，优化性能，添加农田保护，火焰蔓延开关，指令转接/指令简化）
（更新必须删除配置文件，新版本配置文件完全不兼容旧版本）
2021.02.22 插件更新-1.1.1（添加保护村民不被雷击转化成女巫的保护)
2021.02.23 插件更新-1.1.2（添加展示框调整工具，允许使用调试棒切换可见/可交互）
2021.02.25 插件更新-1.2.0（修复bug，优化代码，调整默认配置文件）
（更新必须删除配置文件，新版本配置文件完全不兼容旧版本）
2021.02.25 插件更新-1.2.1（新增盔甲架调试功能，允许使用调试棒切换可见/可交互）
2021.03.02 插件更新-1.2.2（修复bug，新增更新检查）
2021.03.06 插件更新-1.2.3（修复bug，新增天气控制）
2021.03.07 插件更新-1.2.4（修复bug，新增玩家使用禁用命令后控制台执行命令）
2021.03.28 插件更新-1.2.5（修复bug，修改禁用指令的格式，新增模糊匹配）
2021.05.23 插件更新-1.3.0（修复bug，新增按世界配置禁用指令，优化盔甲架调试功能，添加bstats统计数据）
（更新必须删除配置文件，新版本配置文件完全不兼容旧版本）
2021.06.12 插件更新-1.3.1（删除遗留的调试信息）
2021.06.12 插件更新-1.3.2（修复帮助信息不显示的问题）
2021.11.07 插件更新-1.3.3（修复help指令显示的错误内容）
2022.06.23 插件更新-2.0.0（用kotlin重写，并且修复高版本无法阻止使用床的问题，新增死亡掉落相关设置, 优化处理流程, 增加debug日志）
（更新必须删除配置文件，新版本配置文件完全不兼容旧版本）
```

# bstats

![bstats](https://bstats.org/signatures/bukkit/Boom.svg)