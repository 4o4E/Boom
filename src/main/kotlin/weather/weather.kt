package top.e404.boom.weather

import org.bukkit.World

fun World.sun(length: Int = 10 * 60 * 20) {
    setStorm(false)
    isThundering = false
    weatherDuration = length
}

fun World.rain(length: Int = 10 * 60 * 20) {
    setStorm(true)
    isThundering = false
    weatherDuration = length
}

fun World.thunder(length: Int = 10 * 60 * 20) {
    setStorm(true)
    isThundering = true
    weatherDuration = length
}