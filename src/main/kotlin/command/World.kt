package top.e404.boom.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.boom.PL
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object World : ECommand(
    PL,
    "world",
    Regex("(?i)world"),
    true,
    "boom.admin"
) {
    override val usage = "&a/bm world &f查看当前世界名".color()

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>
    ) {
        sender as Player
        plugin.sendMsgWithPrefix(sender, "你当前所在的世界名为: ${sender.world.name}")
    }
}