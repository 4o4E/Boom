package top.e404.boom.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.boom.PL
import top.e404.boom.config.Lang
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
    override val usage: String
        get() = Lang["plugin_command.usage.ls"].color()

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>
    ) {
        sender as Player
        sender.world.sun(60 * 60 * 20)
        plugin.sendMsgWithPrefix(
            sender,
            Lang[
                    "weather.set",
                    "world" to sender.world.name,
                    "duration" to Lang.parseSecondAsDuration(3600),
                    "weather" to Lang["weather.sun"],
            ]
        )
    }
}