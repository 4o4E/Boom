package top.e404.boom.listener

import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.util.EulerAngle
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.eplugin.listener.EListener

object ArmorStandListener : EListener(PL) {
    // 盔甲架修正
    @EventHandler
    fun CreatureSpawnEvent.onEvent() {
        val entity = entity
        if (entity !is ArmorStand) return
        val world = entity.world.name
        val location = entity.location
        if (Config.getEachOrGlobal(world) { fixArmorstandPose } != true) {
            plugin.debug(
                "armorstand.debug_fix",
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
            return
        }
        entity.setArms(true)
        entity.leftArmPose = EulerAngle(-0.1746, 0.0, -0.1746)
        entity.rightArmPose = EulerAngle(-0.1746, 0.0, 0.1746)
        entity.teleport(location.apply {
            yaw = (yaw / 22.5F).toInt() * 22.5F
        })
        plugin.debug(
            "armorstand.debug_unfix",
            "world" to world,
            "x" to location.blockX,
            "y" to location.blockY,
            "z" to location.blockZ,
        )
        return
    }
}