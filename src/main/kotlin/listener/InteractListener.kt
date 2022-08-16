package top.e404.boom.listener

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.eplugin.listener.EListener

/**
 * 交互监听器
 */
object InteractListener : EListener(PL) {
    /**
     * 监听玩家交互方块
     */
    @EventHandler
    fun PlayerInteractEvent.onEvent() {
        // 只检测交互方块
        if (action != Action.RIGHT_CLICK_BLOCK || hand != EquipmentSlot.HAND) return
        // 潜行且手中有方块 + 交互 只会尝试放置, 不会触发交互
        if (player.isSneaking && player.isHoldingBlock()) return
        val block = clickedBlock ?: return
        val type = block.type.name
        val world = block.world.name
        val location = block.location
        // 床
        if (type.endsWith("_BED") || type == "BED") {
            val cfg = Config.getConfig(location) { preventUseBed }
            if (cfg != null && cfg.enable) {
                plugin.debug(
                    "bed.prevent_use",
                    "player" to player.name,
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
                )
                isCancelled = true
                if (cfg.message != "") plugin.sendMsgWithPrefix(player, cfg.message)
                cfg.sound?.playTo(player)
            } else plugin.debug(
                "bed.pass_use",
                "player" to player.name,
                "world" to world,
                "x" to location.blockX,
                "y" to location.blockY,
                "z" to location.blockZ,
            )
            return
        }
        // 重生锚
        if (type == "RESPAWN_ANCHOR") {
            val cfg = Config.getConfig(location) { preventUseRespawnAnchor }
            if (cfg != null
                && cfg.enable
                && ("respawn_anchor[charges=4]" in block.blockData.asString
                        || !player.isHoldGlowStone())
            ) {
                plugin.debug(
                    "respawn_anchor.prevent_use",
                    "player" to player.name,
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
                )
                isCancelled = true
                if (cfg.message != "") plugin.sendMsgWithPrefix(player, cfg.message)
                cfg.sound?.playTo(player)
            } else
                plugin.debug(
                    "respawn_anchor.pass_use",
                    "player" to player.name,
                    "world" to world,
                    "x" to location.blockX,
                    "y" to location.blockY,
                    "z" to location.blockZ,
                )
            return
        }
    }

    private fun Player.isHoldingBlock() = inventory.run { itemInMainHand.isBlock() || itemInOffHand.isBlock() }

    private fun ItemStack.isBlock() = type != Material.AIR && type.isBlock

    private fun Player.isHoldGlowStone() = inventory.run {
        itemInMainHand.type == Material.GLOWSTONE || itemInOffHand.type == Material.GLOWSTONE
    }
}