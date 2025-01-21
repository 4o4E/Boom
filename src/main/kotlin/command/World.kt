package top.e404.boom.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.boom.PL
import top.e404.boom.config.Lang
import top.e404.eplugin.command.ECommand

/**
 * world指令
 */
object World : ECommand(
    PL,
    "world",
    "(?i)world",
    true,
    "boom.admin"
) {
    override val usage get() = Lang["plugin_command.usage.world"]

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>
    ) {
        sender as Player
        plugin.sendMsgWithPrefix(sender, Lang["plugin_command.world", "world" to sender.world.name])
    }
}