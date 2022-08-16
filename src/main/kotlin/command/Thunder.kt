package top.e404.boom.command

import org.bukkit.World
import top.e404.boom.weather.thunder

/**
 * thunder指令
 */
object Thunder : WeatherCommand(
    "thunder",
    Regex("(?i)thunder"),
) {
    override fun World.setWeather() = thunder()

    override fun World.setWeather(length: Int) = thunder(length)
}