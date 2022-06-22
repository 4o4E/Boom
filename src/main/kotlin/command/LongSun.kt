package top.e404.boom.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.boom.PL
import top.e404.boom.weather.sun
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object LongSun : ECommand(
    PL,
    "ls",
    Regex("(?i)ls|longsun"),
    true,
    "boom.weather.sun"
) {
    override val usage = "&a/bm ls &f切换当前世界天气在接下来的一小时内为晴".color()

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>
    ) {
        sender as Player
        sender.world.sun(60 * 60 * 20)
    }
}