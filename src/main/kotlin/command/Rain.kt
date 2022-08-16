package top.e404.boom.command

import org.bukkit.World
import top.e404.boom.weather.rain

/**
 * rain指令
 */
object Rain : WeatherCommand(
    "rain",
    Regex("(?i)rain"),
) {
    override fun World.setWeather() = rain()

    override fun World.setWeather(length: Int) = rain(length)
}