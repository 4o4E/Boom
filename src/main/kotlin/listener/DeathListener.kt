package top.e404.boom.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.eplugin.listener.EListener

/**
 * 死亡监听器
 */
object DeathListener : EListener(PL) {
    @EventHandler
    fun PlayerDeathEvent.onEvent() {
        val lvl = Config.getConfig(entity.location) { keepLevelOnDeath } == true
        val inv = Config.getConfig(entity.location) { keepInventoryOnDeath } == true
        if (lvl) {
            droppedExp = 0
            keepLevel = true
        }
        if (inv) {
            drops.clear()
            keepInventory = true
        }
        val l = entity.location
        plugin.debug(
            "debug_on_death",
            "lvl" to lvl,
            "inv" to inv,
            "player" to entity.name,
            "world" to entity.world.name,
            "x" to l.blockX,
            "y" to l.blockY,
            "z" to l.blockZ,
        )
    }
}