package top.e404.boom.listener

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.eplugin.listener.EListener

/**
 * 爆炸监听器
 */
object ExplosionListener : EListener(PL) {
    /**
     * 监听实体爆炸
     */
    @EventHandler
    fun EntityExplodeEvent.onEvent() {
        val world = entity.world.name
        val location = entity.location
        val map = Config.getConfig(location) { explosion } ?: return
        val type = entity.type.name
        val config = map[type]
        if (config == null || !config.enable) {
            plugin.debug(
                "explosion.debug_pass",
                "entity" to type,
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
            return
        }
        if (config.cancel) {
            isCancelled = true
            plugin.debug(
                "explosion.debug_cancel",
                "entity" to type,
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
        } else {
            blockList().clear()
            plugin.debug(
                "explosion.debug_prevent",
                "entity" to type,
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
        }
    }

    /**
     * 监听实体改变方块
     */
    @EventHandler
    fun EntityChangeBlockEvent.onEvent() {
        val world = entity.world.name
        val location = entity.location
        // 凋灵受伤害时替换方块
        if (entityType == EntityType.WITHER) {
            val map = Config.getConfig(location) { explosion } ?: return
            val config = map["WITHER"] ?: return
            if (config.enable) {
                isCancelled = true
                plugin.debug(
                    "wither.debug_prevent_replace",
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
                )
            } else plugin.debug(
                "wither.debug_pass_replace",
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
            return
        }
        // 末影人搬起方块
        if (entityType == EntityType.ENDERMAN) {
            if (Config.getConfig(location) { preventEndermanPickup } == true) {
                isCancelled = true
                plugin.debug(
                    "enderman.debug_prevent_pickup",
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
                )
            } else plugin.debug(
                "enderman.debug_pass_pickup",
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
            return
        }
    }
}