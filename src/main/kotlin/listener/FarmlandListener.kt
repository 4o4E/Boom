package top.e404.boom.listener

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerInteractEvent
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.boom.config.Lang
import top.e404.eplugin.listener.EListener

/**
 * 耕地踩踏监听器
 */
object FarmlandListener : EListener(PL) {
    /**
     * 监听玩家踩耕地
     */
    @EventHandler
    fun PlayerInteractEvent.onEvent() {
        val block = clickedBlock ?: return
        if (action != Action.PHYSICAL
            || block.type != Material.FARMLAND
        ) return
        val world = player.world.name
        val location = block.location
        if (Config.getConfig(location) { protectFarmland } == true) {
            isCancelled = true
            plugin.debug {
                Lang[
                        "farmland.debug_protect_by_player",
                        "player" to player.name,
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                ]
            }
        } else plugin.debug {
            Lang[
                    "farmland.debug_pass_by_player",
                    "player" to player.name,
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
            ]
        }
    }

    /**
     * 监听实体踩耕地
     */
    @EventHandler
    fun EntityInteractEvent.onEvent() {
        val world = entity.world.name
        val location = block.location
        if (block.type != Material.FARMLAND) return
        if (Config.getConfig(location) { protectFarmland } == true) {
            isCancelled = true
            plugin.debug {
                Lang[
                        "farmland.debug_protect_by_entity",
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                ]
            }
        } else plugin.debug {
            Lang[
                    "farmland.debug_pass_by_entity",
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
            ]
        }
    }
}