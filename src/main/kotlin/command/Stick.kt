package top.e404.boom.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.boom.PL
import top.e404.boom.config.Lang
import top.e404.boom.stick.StickItem
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

/**
 * stick指令
 */
object Stick : ECommand(
    PL,
    "stick",
    Regex("(?i)(get)?stick"),
    true,
    "boom.stick"
) {
    override val usage: String
        get() = Lang["plugin_command.usage.stick"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        sender as Player
        val inv = sender.inventory
        val index = inv.firstEmpty()
        if (index == -1) {
            plugin.sendMsgWithPrefix(
                sender,
                Lang["message.backpack_full"]
            )
            return
        }
        inv.setItem(index, StickItem.stick)
        plugin.sendMsgWithPrefix(
            sender,
            Lang["plugin_command.stick_give"]
        )
    }
}