package top.e404.boom.command

import top.e404.boom.PL
import top.e404.eplugin.command.ECommandManager

/**
 * 指令管理器
 */
object CommandManager : ECommandManager(
    PL,
    "boom",
    Reload,
    Debug,
    World,
    Sun,
    Rain,
    Thunder,
    LongSun,
    Stick
)