package top.e404.boom.listener

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.boom.config.Lang
import top.e404.eplugin.config.matches
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
            plugin.debug {
                Lang[
                        "explosion.debug_pass",
                        "entity" to type,
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                ]
            }
            return
        }
        if (config.cancel) {
            isCancelled = true
            plugin.debug {
                Lang[
                        "explosion.debug_cancel",
                        "entity" to type,
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                ]
            }
        } else {
            blockList().clear()
            plugin.debug {
                Lang[
                        "explosion.debug_prevent",
                        "entity" to type,
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                ]
            }
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
                plugin.debug {
                    Lang[
                            "wither.debug_prevent_replace",
                            "world" to world,
                            "x" to location.blockX,
                            "y" to location.blockY,
                            "z" to location.blockZ,
                    ]
                }
            } else plugin.debug {
                Lang[
                        "wither.debug_pass_replace",
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                ]
            }
            return
        }
        // 末影人搬起方块
        if (entityType == EntityType.ENDERMAN) {
            if (Config.getConfig(location) { preventEndermanPickup } == true) {
                isCancelled = true
                plugin.debug {
                    Lang[
                            "enderman.debug_prevent_pickup",
                            "world" to world,
                            "x" to location.blockX,
                            "y" to location.blockY,
                            "z" to location.blockZ,
                    ]
                }
            } else plugin.debug {
                Lang[
                        "enderman.debug_pass_pickup",
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                ]
            }
            return
        }
    }

    /**
     * 监听实体爆炸的实体伤害
     */
    @EventHandler
    fun EntityDamageByEntityEvent.onEvent() {
        if (cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) return
        val world = entity.world.name
        val entityLocation = entity.location
        val damagerLocation = damager.location
        val map = Config.getConfig(entityLocation) { explosion } ?: return
        val config = map[damager.type.name]
        if (config == null
            || !config.enable
            || config.preventDamage?.matches(entity.type.name) != true
        ) {
            plugin.debug {
                Lang[
                        "explosion.debug_entity_damage_pass",
                        "damager_entity" to damager.type.name,
                        "damager_world" to world,
                        "damager_x" to damagerLocation.blockX,
                        "damager_y" to damagerLocation.blockY,
                        "damager_z" to damagerLocation.blockZ,
                        "entity" to entity.type.name,
                        "world" to world,
                        "x" to entityLocation.blockX,
                        "y" to entityLocation.blockY,
                        "z" to entityLocation.blockZ,
                ]
            }
            return
        }
        isCancelled = true
        plugin.debug {
            Lang[
                    "explosion.debug_entity_damage_prevent",
                    "damager_entity" to damager.type.name,
                    "damager_world" to world,
                    "damager_x" to damagerLocation.blockX,
                    "damager_y" to damagerLocation.blockY,
                    "damager_z" to damagerLocation.blockZ,
                    "entity" to entity.type.name,
                    "world" to world,
                    "x" to entityLocation.blockX,
                    "y" to entityLocation.blockY,
                    "z" to entityLocation.blockZ,
            ]
        }
    }
}