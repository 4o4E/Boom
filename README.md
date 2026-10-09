# Boom

[English](README_EN.md)

> 基于BukkitAPI的管理插件

支持`Bukkit`/`Spigot`/`Paper`/`Purpur`等`Bukkit`的分支核心

支持`1.13.x`及以上版本，`1.16.x`-`1.19.x`经过测试

提供完整语言文件, 欢迎pr其他的语言文件

**不支持`1.12`及以下版本, 不支持`Sponge`核心, 不支持`Mohist`/`Arclight`/`CatServer`等任何的添加了mod支持的服务端核心，不支持MOD实体的处理(你可以用, 但是遇到问题请不要在这儿反馈)**

[![Release](https://img.shields.io/github/v/release/4o4E/Boom?label=Release)](https://github.com/4o4E/Boom/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/4o4E/Boom/total?label=Download)](https://github.com/4o4E/Boom/releases)

## 支持功能

以下所有配置均可以按世界/WorldGuard区域单独配置(在`global`下的是全局设置，在`each.<世界名>`下的是单独世界的设置，`region.<区域名>`下的是区域设置，处理优先级：区域(`region`) -> 世界(`each`) -> 全局(`global`))
- 控制实体爆炸
- 阻止火焰蔓延
- 阻止火焰烧毁方块
- 保护耕地不被实体踩坏
- 阻止实体转换(村民, 女巫, 僵尸村民, 溺尸)
- 阻止末影人搬起方块
- 使盔甲架生成的时候摆正自己(使盔甲架默认有双手)
- 死亡时保留物品
- 死亡时保留等级
- 阻止使用床
- 阻止使用重生锚
- 阻止使用指令(指令转接的功能跳过此检测)
- 指令转接，输入指令a以使用指令b(可以触发多条指令)(支持控制台指令的转接)
- 限制实体生成(百分比)
- 阻止玩家点击实体/方块

## 配置文件

[config.yml](src/main/resources/config.yml)

## 语言文件

[lang.yml](src/main/resources/lang.yml)

欢迎pr其他的语言文件

## 如何配置

```yaml
# global下的配置是全局配置
global:
  explosion:
    CREEPER:
      enable: false
      cancel: false
  disable_fire_spread: false
  disable_fire_burn: false
  # 此处省略其他配置...

# each下的配置是单独世界的配置
each:
  # !! 实际处理时会优先寻找对应世界的配置, 若未找到则会使用global中的配置 !!
  # 此处只需要写不同于global的配置, 相同的可以省略
  # 此处用 example_world1 和 example_world2 作为世界名字, 实际使用时将其改成自己世界的名字
  # 比如 world 或者 world_nether 等
  # 世界名字区分大小写, 不能有多余空格
  # 不知道自己世界名字的可以用客户端(需要权限)执行 /bm world 查看当前所在的世界名字
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
  # 此处省略其他配置...

```

按照以上配置

在世界`example_world1`中, 苦力怕的爆炸不会破坏方块

在世界`example_world2`中, 火焰不会蔓延和烧坏方块

其他所有世界中则苦力怕的爆炸会破坏方块, 火焰会蔓延和烧坏方块

## 指令

> 插件主命令为`boom`，包括别名`bm`，如果与其他插件指令冲突，请使用`boom`
- `/bm reload` 重载插件
- `/bm debug` 切换debug消息的接受与否
- `/bm world` 查看当前世界名
- `/bm sun` 切换当前世界天气在接下来的10分钟内为晴
- `/bm sun <世界>` 切换指定世界天气在接下来的10分钟内为晴
- `/bm sun <世界> <时长>` 切换指定世界天气在接下来的指定时长内为晴
- `/bm rain` 切换当前世界天气在接下来的10分钟内为雨
- `/bm rain <世界>` 切换指定世界天气在接下来的10分钟内为雨
- `/bm rain <世界> <时长>` 切换指定世界天气在接下来的指定时长内为雨
- `/bm thunder` 切换当前世界天气在接下来的10分钟内为雷暴
- `/bm thunder <世界>` 切换指定世界天气在接下来的10分钟内为雷暴
- `/bm thunder <世界> <时长>` 切换指定世界天气在接下来的指定时长内为雷暴
- `/bm ls` 切换当前世界天气在接下来的一小时内为晴
- `/bm stick` 获取调试棒(用于修改盔甲架和展示框)

## 权限

- `boom.admin` 允许使用插件指令

- `boom.bypass.command` 允许跳过指令过滤

- `boom.weather` 允许使用所有天气指令

  **子权限节点**

  - `boom.weather.sun` 允许切换天气为晴

  - `boom.weather.rain` 允许切换天气为雨

  - `boom.weather.thunder` 允许切换天气为雷暴

- `boom.stick` 允许获取并使用调试棒修改盔甲架/展示框

- `boom.bypass.*` 允许跳过点击实体和方块的限制

  **子权限节点**

  - `boom.bypass.block` 允许跳过点击方块的限制
  
  - `boom.bypass.entity` 允许跳过点击实体的限制

## 下载

- [最新版](https://github.com/4o4E/Boom/releases/latest)

## 已知问题

- [ ] 盔甲架调试棒潜行点击盔甲架修改其碰撞箱时有时会连续触发两次

  解决方法: 潜行点击距离盔甲架相近的方块

- [x] ~~村民防雷击会阻止村民变成僵尸村民(被僵尸打死就是直接死了)~~ 已修复

## 更新记录

完整更新记录及升级说明请参阅 [CHANGELOG.md](CHANGELOG.md)。

## bstats

[![bstats](https://bstats.org/signatures/bukkit/Boom.svg)](https://bstats.org/plugin/bukkit/Boom/11445)
