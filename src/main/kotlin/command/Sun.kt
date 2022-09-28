package top.e404.boom.command

import org.bukkit.World
import top.e404.boom.weather.sun

/**
 * sun指令
 */
object Sun : WeatherCommand(
    "sun",
    "(?i)sun",
) {
    override fun World.setWeather() = sun()

    override fun World.setWeather(length: Int) = sun(length)
}