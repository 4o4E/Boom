package top.e404.boom.listener

import org.bukkit.entity.ArmorStand
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import top.e404.boom.PL
import top.e404.boom.config.Lang
import top.e404.boom.stick.StickItem.isHoldStick
import top.e404.eplugin.listener.EListener
import top.e404.eplugin.util.mcVer

/**
 * 调试棒监听器
 */
object StickListener : EListener(PL) {
    /**
     * 监听玩家交互方块
     */
    @EventHandler
    fun PlayerInteractEvent.onEvent() {
        if (hand != EquipmentSlot.HAND
            || !player.isHoldStick()
            || !plugin.hasPerm(player, "boom.stick", true)
        ) return
        isCancelled = true
        // 潜行交互设置盔甲架可交互
        if (player.isSneaking) {
            val block = clickedBlock
            val armorStand =
                (block?.world?.getNearbyEntities(block.location, 1.2, 1.2, 1.2)
                    ?: player.getNearbyEntities(1.2, 1.2, 1.2))
                    .firstNotNullOfOrNull { it as? ArmorStand }
            if (armorStand == null) {
                plugin.sendMsgWithPrefix(player, Lang["armorstand.invalid"])
                return
            }
            if (armorStand.isMarker) {
                armorStand.isMarker = false
                armorStand.setGravity(true)
                plugin.sendMsgWithPrefix(player, Lang["armorstand.unset_marker"])
            } else {
                armorStand.isMarker = true
                armorStand.setGravity(false)
                plugin.sendMsgWithPrefix(player, Lang["armorstand.set_marker"])
            }
            return
        }
        // 交互方块旋转其中的盔甲架
        if (action == Action.LEFT_CLICK_BLOCK) {
            val block = clickedBlock ?: return
            val armorStand = block.world
                .getNearbyEntities(block.location, 1.2, 1.2, 1.2)
                .firstNotNullOfOrNull { it as? ArmorStand }
            if (armorStand == null) {
                plugin.sendMsgWithPrefix(player, Lang["armorstand.invalid"])
                return
            }
            armorStand.apply {
                location.let {
                    it.yaw = ((it.yaw / 22.5).toInt() + 1) * 22.5F % 360F
                    teleport(it)
                }
            }
            plugin.sendMsgWithPrefix(player, Lang["armorstand.rotated"])
        }
    }

    /**
     * 监听玩家交互盔甲架
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun PlayerArmorStandManipulateEvent.onEvent() {
        val entity = rightClicked
        if (hand != EquipmentSlot.HAND
            || slot != EquipmentSlot.HAND
            || !player.isHoldStick()
            || !plugin.hasPerm(player, "boom.stick", true)
        ) return
        isCancelled = true
        if (player.isSneaking) {
            entity.isMarker = true
            plugin.sendMsgWithPrefix(player, Lang["armorstand.set_marker"])
            return
        }
        if (entity.isVisible) {
            entity.isVisible = false
            plugin.sendMsgWithPrefix(player, Lang["armorstand.unset_visible"])
        } else {
            entity.isVisible = true
            plugin.sendMsgWithPrefix(player, Lang["armorstand.set_visible"])
        }
    }

    /**
     * 监听玩家伤害盔甲架
     */
    @EventHandler
    fun EntityDamageByEntityEvent.onEvent() {
        val player = damager
        val entity = entity
        if (player !is Player
            || entity !is ArmorStand
            || !player.isHoldStick()
            || !plugin.hasPerm(player, "boom.stick", true)
        ) return
        isCancelled = true
        entity.teleport(entity.location.apply {
            yaw = ((yaw / 22.5F).toInt() + 1) * 22.5F
        })
        plugin.sendMsgWithPrefix(player, Lang["armorstand.rotated"])
    }

    /**
     * 监听玩家交互展示框
     */
    @EventHandler
    fun PlayerInteractEntityEvent.onEvent() {
        val entity = rightClicked
        if (!player.isHoldStick()
            || hand != EquipmentSlot.HAND
            || !plugin.hasPerm(player, "boom.stick", true)
            || entity !is ItemFrame
        ) return
        isCancelled = true
        if ((mcVer?.minor ?: 0) < 16) {
            plugin.sendMsgWithPrefix(player, Lang["item_frame.unsupported_fixed_version"])
            return
        }
        if (player.isSneaking) {
            if (entity.isFixed) {
                entity.isFixed = false
                plugin.sendMsgWithPrefix(player, Lang["item_frame.unset_fixed"])
            } else {
                entity.isFixed = true
                plugin.sendMsgWithPrefix(player, Lang["item_frame.set_fixed"])
            }
            return
        }
        if (entity.isVisible) {
            entity.isVisible = false
            plugin.sendMsgWithPrefix(player, Lang["item_frame.unset_visible"])
        } else {
            entity.isVisible = true
            plugin.sendMsgWithPrefix(player, Lang["item_frame.set_visible"])
        }
    }
}