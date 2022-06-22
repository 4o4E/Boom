package top.e404.boom.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import top.e404.boom.PL
import top.e404.boom.weather.thunder
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand
import top.e404.eplugin.util.parseAsDuration

object Thunder : ECommand(
    PL,
    "thunder",
    Regex("(?i)thunder"),
    false,
    "boom.weather.thunder"
) {
    override val usage = """&a/bm thunder &f切换当前世界天气在接下来的10分钟内为雷暴
        |&a/bm thunder <世界> &f切换指定世界天气在接下来的10分钟内为雷暴
        |&a/bm thunder <世界> <时长> &f切换指定世界天气在接下来的指定时长内为雷暴
        |  时长单位可用: 天/时/分/秒 支持英文缩写 示例: 1h30m
    """.trimMargin().color()

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>
    ) {
        when (args.size) {
            1 -> {
                val player = plugin.asPlayer(sender, true) ?: return
                player.world.thunder()
                plugin.sendMsgWithPrefix(sender, "&a已设置世界${player.world.name}在接下来的10分钟内天气为雷暴")
            }
            2 -> {
                val world = Bukkit.getWorld(args[1])
                if (world == null) {
                    plugin.sendMsgWithPrefix(sender, "&c不存在名为${args[1]}的世界")
                    return
                }
                world.thunder()
                plugin.sendMsgWithPrefix(sender, "&a已设置世界${args[1]}在接下来的10分钟内天气为雷暴")
            }
            3 -> {
                val (_, worldName, length) = args
                val world = Bukkit.getWorld(worldName)
                if (world == null) {
                    plugin.sendMsgWithPrefix(sender, "&c不存在名为${args[1]}的世界")
                    return
                }
                val duration = length.parseAsDuration()
                if (duration == -1) {
                    plugin.sendMsgWithPrefix(sender, "&c时长${length}格式错误")
                    return
                }
                world.thunder(duration * 20)
                plugin.sendMsgWithPrefix(sender, "&a已设置世界${args[1]}在接下来的10分钟内天气为雷暴")
            }
            else -> sender.sendMessage(usage)
        }
    }
}