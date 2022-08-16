package top.e404.boom.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.eplugin.listener.EListener

/**
 * 伤害监听器
 */
object DamageListener : EListener(PL) {
    // 伤害取消
    @EventHandler
    fun EntityDamageEvent.onEvent() {
        val entity = entity
        if (entity !is Player) return
        val world = entity.world.name
        val location = entity.location
        if (Config.getConfig(location) { preventPlayerDamage } == true) {
            isCancelled = true
            plugin.debug(
                "player_damage.prevent",
                "player" to entity.name,
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
            return
        }
        plugin.debug(
            "player_damage.pass",
            "player" to entity.name,
            "world" to world,
            "x" to location.blockX,
            "y" to location.blockY,
            "z" to location.blockZ,
        )
    }
}