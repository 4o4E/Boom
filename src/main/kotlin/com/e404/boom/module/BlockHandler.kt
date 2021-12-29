package com.e404.boom.module

import com.e404.boom.event.IListener
import com.e404.boom.util.Log
import com.e404.boom.util.isMatch
import com.e404.boom.util.playSound
import com.e404.boom.util.sendMsgWithPrefix
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerInteractEvent

/**
 * 方块相关检查
 */
object BlockHandler : AbstractModule("block"), IListener {
    private lateinit var endermanConfig: ConfigurationSection
    private lateinit var witherConfig: ConfigurationSection
    private lateinit var respawnConfig: ConfigurationSection
    private lateinit var bedConfig: ConfigurationSection
    private lateinit var farmlandConfig: ConfigurationSection
    private lateinit var fireConfig: ConfigurationSection

    override fun onEnable() {
        register()
        onReload()
    }

    override fun onReload() {
        initCfg()
        endermanConfig = config.getConfigurationSection("ENDERMAN")!!
        witherConfig = config.getConfigurationSection("WITHER")!!
        respawnConfig = config.getConfigurationSection("RESPAWN")!!
        bedConfig = config.getConfigurationSection("BED")!!
        farmlandConfig = config.getConfigurationSection("FARMLAND")!!
        fireConfig = config.getConfigurationSection("FIRE")!!
        logFinish()
    }

    /**
     * 末影人搬起方块
     */
    @EventHandler
    fun enderman(event: EntityChangeBlockEvent) {
        if (event.entityType != EntityType.ENDERMAN) return
        val l = event.block.location
        val world = event.block.world.name
        val placeholder = mapOf(
            "world" to world,
            "block" to event.block.type.name,
            "x" to l.blockX,
            "y" to l.blockY,
            "z" to l.blockZ
        )
        Log.debug("enderman_pick_up", placeholder)
        if (endermanConfig.getBoolean("enable")
            && !world.isMatch(endermanConfig.getStringList("disable_world"))
        ) event.isCancelled = true
    }

    /**
     * 重生锚点击检查
     */
    @EventHandler
    fun respawn(event: PlayerInteractEvent) {
        val block = event.clickedBlock
        if (event.action != Action.RIGHT_CLICK_BLOCK || block == null) return
        val data = block.blockData.asString
        if (!data.contains("respawn_anchor")) return
        val p = event.player
        val placeholder = mapOf(
            "player" to p.name,
            "world" to p.world.name,
            "x" to block.x,
            "y" to block.y,
            "z" to block.z
        )
        Log.debug("respawn_use", placeholder)
        if (data.contains("respawn_anchor[charges=4]")
            || (p.inventory.itemInMainHand.type != Material.GLOWSTONE
                    && p.inventory.itemInOffHand.type != Material.GLOWSTONE)
        ) {
            event.isCancelled = true
            val message = respawnConfig.getString("message")?.trim() ?: ""
            if (message != "") p.sendMsgWithPrefix(message)
            p.playSound(respawnConfig.getString("sound") ?: "")
        }
    }

    /**
     * 床点击检查
     */
    @EventHandler
    fun bed(event: PlayerBedEnterEvent) {
        val p = event.player
        val l = event.bed.location
        val placeholder = mapOf(
            "player" to p.name,
            "world" to p.world.name,
            "x" to l.blockX,
            "y" to l.blockY,
            "z" to l.blockZ
        )
        Log.debug("bed_use", placeholder)
        if (!bedConfig.getBoolean("enable")
            || event.bed.world.name.isMatch(bedConfig.getStringList("disable_world"))
        ) return
        event.isCancelled = true
        val message = bedConfig.getString("message")?.trim() ?: ""
        if (message != "") p.sendMsgWithPrefix(message)
        p.playSound(bedConfig.getString("sound") ?: "")
    }

    /**
     * 玩家耕地防踩
     */
    @EventHandler
    fun playerFarmland(event: PlayerInteractEvent) {
        val block = event.clickedBlock
        if (event.action != Action.PHYSICAL
            || block == null
            || block.type != Material.FARMLAND) return
        val placeholder = mapOf(
            "world" to block.world.name,
            "x" to block.x,
            "y" to block.y,
            "z" to block.z
        )
        if (!farmlandConfig.getBoolean("enable")
            || block.world.name.isMatch(farmlandConfig.getStringList("disable_world"))
        ) return
        event.isCancelled = true
        Log.debug("farmland_trample", placeholder)
    }

    /**
     * 实体耕地防踩
     */
    @EventHandler
    fun entityFarmland(event: EntityInteractEvent) {
        if (event.block.type != Material.FARMLAND) return
        val block = event.block
        val placeholder = mapOf(
            "world" to block.world.name,
            "x" to block.x,
            "y" to block.y,
            "z" to block.z
        )
        Log.debug("farmland_trample", placeholder)
        if (!farmlandConfig.getBoolean("enable")
            || event.block.world.name.isMatch(farmlandConfig.getStringList("disable_world"))
        ) return
        event.isCancelled = true
    }

    /**
     * 火焰蔓延
     */
    @EventHandler
    fun fire(event: BlockBurnEvent) {
        val block = event.block
        val placeholder = mapOf(
            "world" to block.world.name,
            "block" to block.type.name,
            "x" to block.x,
            "y" to block.y,
            "z" to block.z
        )
        Log.debug("fire_spread", placeholder)
        if (!fireConfig.getBoolean("enable")
            || event.block.world.name.isMatch(fireConfig.getStringList("disable_world"))
        ) return
        event.isCancelled = true
    }
}