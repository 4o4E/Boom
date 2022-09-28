package top.e404.boom.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockIgniteEvent
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.boom.config.Lang
import top.e404.eplugin.listener.EListener

/**
 * 火焰相关监听器
 */
object FireListener : EListener(PL) {
    /**
     * 监听方块被烧毁
     */
    @EventHandler
    fun BlockBurnEvent.onEvent() {
        val world = block.world.name
        val location = block.location
        if (Config.getConfig(location) { disableFireBurn } == true) {
            isCancelled = true
            plugin.debug {
                Lang[
                        "fire.debug_prevent_spread",
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                ]
            }
        } else plugin.debug {
            Lang[
                    "fire.debug_pass_spread",
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
            ]
        }
    }

    /**
     * 监听火焰蔓延
     */
    @EventHandler
    fun BlockIgniteEvent.onEvent() {
        val world = block.world.name
        val location = block.location
        if (Config.getConfig(location) { disableFireSpread } == true
            && cause == BlockIgniteEvent.IgniteCause.SPREAD
        ) {
            isCancelled = true
            plugin.debug {
                Lang[
                        "fire.debug_prevent_ignite",
                        "world" to world,
                        "x" to location.blockX,
                        "y" to location.blockY,
                        "z" to location.blockZ,
                ]
            }
        } else plugin.debug {
            Lang[
                    "fire.debug_pass_ignite",
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
            ]
        }
    }
}