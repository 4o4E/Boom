package top.e404.boom.command

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.CommandSender
import top.e404.boom.PL
import top.e404.boom.config.Lang
import top.e404.eplugin.command.ECommand
import top.e404.eplugin.util.parseAsDuration

/**
 * 对天气指令的抽象
 *
 * @property name 指令名字
 * @property regex 指令正则
 */
abstract class WeatherCommand(
    override val name: String,
    regex: String
) : ECommand(
    PL,
    name,
    regex,
    false,
    "boom.weather.${name}"
) {
    companion object {
        val complete = listOf("1d", "1h", "30m", "10m")
    }

    override val usage get() = Lang["plugin_command.usage.${name}"]

    protected abstract fun World.setWeather()
    protected abstract fun World.setWeather(length: Int)

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String>
    ) {
        when (args.size) {
            1 -> {
                val player = plugin.asPlayer(sender, true) ?: return
                player.world.setWeather()
                plugin.sendMsgWithPrefix(
                    sender,
                    Lang[
                        "weather.set",
                        "world" to player.world.name,
                        "duration" to Lang.parseSecondAsDuration(600),
                        "weather" to Lang["weather.${name}"],
                    ]
                )
            }

            2 -> {
                val world = Bukkit.getWorld(args[1])
                if (world == null) {
                    plugin.sendMsgWithPrefix(
                        sender,
                        Lang[
                            "message.invalid_world",
                            "world" to args[1]
                        ]
                    )
                    return
                }
                world.setWeather()
                plugin.sendMsgWithPrefix(
                    sender,
                    Lang[
                        "weather.set",
                        "world" to args[1],
                        "duration" to Lang.parseSecondAsDuration(600),
                        "weather" to Lang["weather.${name}"],
                    ]
                )
            }

            3 -> {
                val (_, worldName, length) = args
                val world = Bukkit.getWorld(worldName)
                if (world == null) {
                    plugin.sendMsgWithPrefix(
                        sender,
                        Lang[
                            "message.invalid_world",
                            "world" to args[1]
                        ]
                    )
                    return
                }
                val duration = length.parseAsDuration()
                if (duration == -1) {
                    plugin.sendMsgWithPrefix(
                        sender,
                        Lang[
                            "message.invalid_duration",
                            "duration" to length
                        ]
                    )
                    return
                }
                world.setWeather(duration * 20)
                plugin.sendMsgWithPrefix(
                    sender,
                    Lang[
                        "weather.set",
                        "world" to args[1],
                        "duration" to Lang.parseSecondAsDuration(duration),
                        "weather" to Lang["weather.${name}"],
                    ]
                )
            }

            else -> sender.sendMessage(usage)
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        args: Array<out String>,
        complete: MutableList<String>
    ) {
        when (args.size) {
            2 -> Bukkit.getWorlds().forEach { complete.add(it.name) }
            3 -> complete.addAll(WeatherCommand.complete)
        }
    }
}