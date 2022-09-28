package top.e404.boom.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntitySpawnEvent
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.boom.config.Lang
import top.e404.eplugin.listener.EListener
import kotlin.random.Random

/**
 * 实体生成监听器
 */
object SpawnListener : EListener(PL) {
    /**
     * 控制实体生成
     */
    @EventHandler
    fun EntitySpawnEvent.onEvent() {
        val entity = entity
        val world = entity.world.name
        val type = entity.type.name
        val chance = Config.getConfig(entity.location) { limitEntitySpawn?.get(type) } ?: return
        if (chance >= 100) return
        if (chance <= 0 || Random.nextInt(101) > chance) {
            isCancelled = true
            val location = entity.location
            plugin.debug {
                Lang[
                        "debug_limit_spawn",
                        "entity" to entity.type.name,
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                ]
            }
        }
    }
}