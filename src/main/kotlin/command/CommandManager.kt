package top.e404.boom.command

import top.e404.boom.PL
import top.e404.eplugin.command.ECommandManager

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