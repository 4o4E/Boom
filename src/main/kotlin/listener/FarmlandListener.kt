package top.e404.boom.listener

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerInteractEvent
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.eplugin.listener.EListener

object FarmlandListener : EListener(PL) {
    @EventHandler
    fun PlayerInteractEvent.onEvent() {
        val block = clickedBlock ?: return
        if (action != Action.PHYSICAL
            || block.type != Material.FARMLAND
        ) return
        val world = player.world.name
        val location = block.location
        if (Config.getEachOrGlobal(player.world.name) { protectFarmland } == true) {
            isCancelled = true
            plugin.debug(
                "farmland.debug_protect_by_player",
                "player" to player.name,
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
        } else plugin.debug(
            "farmland.debug_pass_by_player",
            "player" to player.name,
            "world" to world,
            "x" to location.blockX,
            "y" to location.blockY,
            "z" to location.blockZ,
        )
    }

    @EventHandler
    fun EntityInteractEvent.onEvent() {
        val world = entity.world.name
        val location = block.location
        if (block.type != Material.FARMLAND) return
        if (Config.getEachOrGlobal(entity.world.name) { protectFarmland } == true) {
            isCancelled = true
            plugin.debug(
                "farmland.debug_protect_by_entity",
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
        } else plugin.debug(
            "farmland.debug_pass_by_entity",
            "world" to world,
            "x" to location.blockX,
            "y" to location.blockY,
            "z" to location.blockZ,
        )
    }
}