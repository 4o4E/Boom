package top.e404.boom.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerCommandSendEvent
import org.bukkit.event.server.ServerCommandEvent
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.boom.config.Lang
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.listener.EListener
import top.e404.eplugin.util.execAsCommand

/**
 * 指令监听器
 */
object CommandListener : EListener(PL) {
    /**
     * 阻止补全指令名字
     */
    @EventHandler
    fun PlayerCommandSendEvent.onEvent() {
        if (player.hasPermission("boom.bypass.command")) return
        val cfg = Config.getConfig(player.location) { preventUseCommand }
        if (cfg != null
            && cfg.enable
        ) {
            val remove = commands.filter { command ->
                cfg.regexes.any { regex -> regex.matches(command) }
            }
            remove.forEach { commands.remove(it) }
            plugin.debug {
                Lang[
                        "command.debug_remove_complete",
                        "player" to player.name,
                        "remove" to remove,
                        "commands" to commands
                ]
            }
        } else plugin.debug {
            Lang[
                    "command.debug_not_remove_complete",
                    "player" to player.name,
            ]
        }
    }

    /**
     * 使用指令
     */
    @EventHandler
    fun PlayerCommandPreprocessEvent.onEvent() {
        val command = message.substring(1) // 去除开头/
        // 检测禁用指令
        if (!player.hasPermission("boom.bypass.command")) {
            val preventCfg = Config.getConfig(player.location) { preventUseCommand }
            if (preventCfg != null
                && preventCfg.enable
                && preventCfg.regexes.any { regex -> regex.matches(command) }
            ) {
                plugin.debug {
                    Lang[
                            "command.debug_player_use_disabled",
                            "player" to player.name,
                            "command" to command,
                    ]
                }
                isCancelled = true
                preventCfg.message?.send(player, command)
                preventCfg.log?.log(player, command)
                return
            }
        }
        // 检测指令转接
        val transformCfg = Config.getConfig(player.location) { transformUseCommand }
        if (transformCfg != null
            && transformCfg.enable
        ) {
            for (trigger in transformCfg.list) if (trigger.match(command)) {
                // 无权限
                if (trigger.permission?.let { player.hasPermission(it) } == false) {
                    plugin.debug {
                        Lang[
                                "command.debug_player_transformed_without_permission",
                                "player" to player.name,
                                "command" to command,
                                "trigger" to trigger.trigger,
                        ]
                    }
                    if (trigger.noperm != "") player.sendMessage(trigger.noperm.color)
                    isCancelled = true
                    return
                }
                plugin.debug {
                    Lang[
                            "command.debug_player_use_transformed",
                            "player" to player.name,
                            "command" to command,
                            "trigger" to trigger.trigger,
                    ]
                }
                trigger.trigger.forEach { it.execAsCommand(player) }
                isCancelled = true
                return
            }
        }
    }

    /**
     * 控制台使用指令
     */
    @EventHandler
    fun ServerCommandEvent.onEvent() {
        // 检测指令转接
        val (enable, list) = Config.config.global.transformUseCommand ?: return
        if (enable) for (trigger in list) if (trigger.match(command)) {
            plugin.debug {
                Lang[
                        "command.debug_console_use_transformed",
                        "command" to command,
                        "trigger" to trigger.trigger,
                ]
            }
            trigger.trigger.forEach { it.execAsCommand() }
            isCancelled = true
            return
        }
    }
}