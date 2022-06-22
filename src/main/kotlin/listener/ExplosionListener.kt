package top.e404.boom.listener

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.eplugin.listener.EListener

object ExplosionListener : EListener(PL) {
    @EventHandler
    fun EntityExplodeEvent.onEvent() {
        val world = entity.world.name
        val map = Config.getEachOrGlobal(world) { explosion } ?: return
        val type = entity.type.name
        val config = map[type]
        val location = entity.location
        if (config == null || !config.enable) {
            plugin.debug(
                "debug_pass",
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
                "debug_cancel",
                "entity" to type,
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
        } else {
            blockList().clear()
            plugin.debug(
                "debug_prevent",
                "entity" to type,
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
        }
    }

    @EventHandler
    fun EntityChangeBlockEvent.onEvent() {
        val world = entity.world.name
        val location = entity.location
        // 凋灵受伤害时替换方块
        if (entityType == EntityType.WITHER) {
            val map = Config.getEachOrGlobal(world) { explosion } ?: return
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
            if (Config.getEachOrGlobal(world) { preventEndermanPickup } == true) {
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