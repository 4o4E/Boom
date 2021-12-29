package com.e404.boom.module

import com.e404.boom.event.IListener
import com.e404.boom.util.Log
import com.e404.boom.util.isMatch
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent

/**
 * 爆炸相关检查
 */
object ExplosionHandler : AbstractModule("explosion"), IListener {
    override fun onEnable() {
        register()
        onReload()
    }

    override fun onReload() {
        initCfg()
        logFinish()
    }

    /**
     * 处理实体爆炸的方块伤害
     */
    @EventHandler
    fun onExplosion(event: EntityExplodeEvent) {
        val bomber = event.entity
        val world = bomber.world.name
        val l = bomber.location
        val placeholder = mapOf(
            "bomber" to bomber.type.name,
            "world" to world,
            "x" to l.x,
            "y" to l.y,
            "z" to l.z
        )
        Log.debug("entity_explosion", placeholder)
        val cfg = config.getConfigurationSection(bomber.type.name)!!
        // 禁用的世界
        if (world.isMatch(cfg.getStringList("disable_world"))) return
        // 方块破坏
        if (cfg.getBoolean("disable_block_damage")) event.blockList().clear()
    }

    /**
     * 处理实体爆炸的实体伤害
     */
    @EventHandler
    fun explosionDamage(event: EntityDamageByEntityEvent) {
        // 过滤非实体爆炸伤害
        if (event.cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) return
        // 受到伤害
        val to = event.damager.type.name
        // 造成伤害
        val from = event.entity.type.name
        val l = event.damager.location
        val placeholder = mapOf(
            "from" to from,
            "to" to to,
            "x" to l.x,
            "y" to l.y,
            "z" to l.z
        )
        Log.debug("entity_explosion_damage", placeholder)
        val cfg = config.getConfigurationSection(from) ?: return
        if (from.isMatch(cfg.getStringList("disable_entity_damage_type"))) event.isCancelled = true
    }
}