package top.e404.boom.hook

import top.e404.boom.PL
import top.e404.eplugin.hook.EHookManager

object HookManager : EHookManager(
    PL,
    RgHook
)