package top.e404.boom.hook

import top.e404.boom.PL
import top.e404.eplugin.hook.EHookManager

/**
 * 钩子管理器
 */
object HookManager : EHookManager(
    PL,
    RgHook
)