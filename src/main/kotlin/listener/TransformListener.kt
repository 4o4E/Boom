package top.e404.boom.listener

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityTransformEvent
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.eplugin.listener.EListener

/**
 * 实体转变监听器
 */
object TransformListener : EListener(PL) {
    @EventHandler
    fun EntityTransformEvent.onEvent() {
        val from = entity.type
        val to = transformedEntity.type
        val world = entity.world.name
        val location = entity.location
        if (from == EntityType.VILLAGER) {
            // 村民 -> 女巫
            if (to == EntityType.WITCH) {
                if (Config.getConfig(location) { protectVillagerToWitch } == true) {
                    isCancelled = true
                    plugin.debug(
                        "transform.prevent_villager_to_witch",
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                    )
                } else plugin.debug(
                    "transform.pass_villager_to_witch",
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
                )
                return
            }
            // 村民 -> 僵尸村民
            if (to == EntityType.ZOMBIE_VILLAGER) {
                if (Config.getConfig(location) { protectVillagerToZombie } == true) {
                    isCancelled = true
                    plugin.debug(
                        "transform.prevent_villager_to_zombie_villager",
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                    )
                } else plugin.debug(
                    "transform.pass_villager_to_zombie_villager",
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
                )
                return
            }
            return
        }
        // 僵尸村民 -> 村民
        if (from == EntityType.ZOMBIE_VILLAGER
            && to == EntityType.VILLAGER
        ) {
            if (Config.getConfig(location) { protectZombieVillagerToVillager } == true) {
                isCancelled = true
                plugin.debug(
                    "transform.prevent_zombie_villager_to_villager",
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
                )
            } else plugin.debug(
                "transform.pass_zombie_villager_to_villager",
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
        }
        // 僵尸 -> 溺尸
        if (from == EntityType.ZOMBIE
            && to == EntityType.DROWNED
        ) {
            if (Config.getConfig(location) { protectZombieToDrowned } == true) {
                isCancelled = true
                plugin.debug(
                    "transform.prevent_zombie_to_drowned",
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
                )
            } else plugin.debug(
                "transform.pass_zombie_to_drowned",
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
            return
        }
    }
}