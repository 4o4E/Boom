package top.e404.boom.weather

import org.bukkit.World

/**
 * 设置天气为晴
 *
 * @param length 时长
 */
fun World.sun(length: Int = 10 * 60 * 20) {
    setStorm(false)
    isThundering = false
    weatherDuration = length
}

/**
 * 设置天气为雨
 *
 * @param length 时长
 */
fun World.rain(length: Int = 10 * 60 * 20) {
    setStorm(true)
    isThundering = false
    weatherDuration = length
}

/**
 * 设置天气为雷雨
 *
 * @param length 时长
 */
fun World.thunder(length: Int = 10 * 60 * 20) {
    setStorm(true)
    isThundering = true
    weatherDuration = length
}