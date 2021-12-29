package com.e404.boom.module

import com.e404.boom.event.IListener
import com.e404.boom.util.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.CreeperPowerEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTransformEvent
import kotlin.random.Random

object EntityHandler : AbstractModule("entity"), IListener {
    private lateinit var spawnConfig: ConfigurationSection

    private lateinit var entityConfig: ConfigurationSection
    private var entityEnable: Boolean = true
    private lateinit var entityNeedClean: MutableList<String>

    private lateinit var itemConfig: ConfigurationSection
    private var itemEnable: Boolean = true
    private lateinit var itemNotClean: MutableList<String>

    private lateinit var transformConfig: ConfigurationSection
    private lateinit var preventList: List<PreventTransform>
    private lateinit var creeperConfig: ConfigurationSection


    override fun onEnable() {
        register()
        onReload()
    }

    override fun onReload() {
        initCfg()
        spawnConfig = config.getConfigurationSection("spawn")!!

        entityConfig = config.getConfigurationSection("clean_entity")!!
        entityEnable = entityConfig.getBoolean("enable")
        entityNeedClean = entityConfig.getStringList("allow_clean_entity")

        itemConfig = config.getConfigurationSection("clean_item")!!
        itemEnable = itemConfig.getBoolean("enable")
        itemNotClean = itemConfig.getStringList("not_clean_type")

        transformConfig = config.getConfigurationSection("entity_transform")!!
        creeperConfig = transformConfig.getConfigurationSection("creeper")!!
        preventList = transformConfig.getList("other")
            ?.map { PreventTransform(it) }
            ?.filter { it.effective }
            ?: emptyList()
        logFinish()
    }

    /**
     * 限制实体生成概率
     */
    @EventHandler
    fun onSpawn(event: EntitySpawnEvent) {
        val type = event.entityType.name
        var rate = spawnConfig.getInt("global.$type", -1)
        if (rate in 0..100) {
            if (Random.nextInt(0, 100) > rate) event.isCancelled = true
            return
        }
        rate = spawnConfig.getInt("world_settings.${event.entity.world.name}.$type", -1)
        if (rate in 0..100) {
            if (Random.nextInt(0, 100) > rate) event.isCancelled = true
        }
    }

    /**
     * 计划清理任务, 此方法只应在启动和重载时调用
     */
    fun schedule() {
        if (entityEnable) scheduleCleanTask(entityConfig) { cleanEntity(null) }
        if (itemEnable) scheduleCleanTask(itemConfig) { cleanItem(null) }
    }

    /**
     * 计划一类清理任务(包括通知, 重复执行, 只应在启动和重载时调用)
     *
     * @param cfg 此任务对应的配置文件
     * @param task 详细任务
     */
    private fun scheduleCleanTask(cfg: ConfigurationSection, task: () -> Unit) {
        // 间隔时间, 单位刻
        val time = cfg.getLong("time")
        // 计划消息提醒
        EScheduler.scheduleRepeat(true, 0, time * 20) {
            val notice = cfg.getConfigurationSection("notice")
            // 计划提醒
            if (notice != null) for (key in notice.getKeys(false))
                if (key.matches(Regex("\\d+"))) {
                    val delay = (time - key.toLong()) * 20
                    if (delay < 1) {
                        Log.warn("任务的提前提醒时长大于间隔时长, 不支持这样的操作, 请自行修改")
                        continue
                    }
                    EScheduler.schedule(true, delay) {
                        notice.getString(key)?.also {
                            Log.info(it)
                            sendAllMsg(it)
                        }
                    }
                }
            // 计划清理 在time秒后执行task
            EScheduler.schedule(true, time * 20, task)
        }
    }

    /**
     * 清理实体
     *
     * @param by 发送者, 若为空则代表是定时任务触发
     * 若不为空, 处理完成后将只发送给发送者
     */
    fun cleanEntity(by: CommandSender?) {
        // 统计
        var all = 0
        var count = 0
        // 清理的类型
        val entityNeedClean = entityConfig.getStringList("need_clean_entity")
        // 禁用的世界
        val disable = entityConfig.getStringList("disable_world")
        // 清理
        for (world in Bukkit.getWorlds()) if (!world.name.isMatch(disable)) {
            world.livingEntities
                .also { all += it.size }
                .filter {
                    it.removeWhenFarAway // 过滤 远离玩家会消失
                            && !it.isLeashed // 过滤 未被栓绳拴住
                            && !it.isInsideVehicle // 过滤 不在船或矿车上
                            && it.type.name.isMatch(entityNeedClean) // 过滤 是需要清理的实体类型
                }
                .also { count += it.size }
                .forEach { it.remove() }
        }
        // 通知
        notice(by, entityConfig, mapOf("count" to count, "all" to all))
    }

    /**
     * 清理掉落物
     *
     * @param by 发送者, 若为空则代表是定时任务触发, 若不为空则处理完成后将只发送给发送者
     */
    fun cleanItem(by: CommandSender?) {
        // 统计
        var all = 0
        var count = 0
        // 禁用的世界
        val disable = entityConfig.getStringList("disable_world")
        // 清理
        for (world in Bukkit.getWorlds()) if (!world.name.isMatch(disable)) {
            world.entities
                .filter { it.type == EntityType.DROPPED_ITEM }
                .also { all += it.size }
                .map { it as Item }
                .filter { !it.itemStack.type.name.isMatch(itemNotClean) }
                .also { count += it.size }
                .forEach { it.remove() }
        }
        // 通知
        notice(by, itemConfig, mapOf("count" to count, "all" to all))
    }

    private fun notice(by: CommandSender?, cfg: ConfigurationSection, placeholders: Map<String, Any>) {
        var s = cfg.getString("finish")?.trim() ?: "finish"
        if (s == "") return
        s = s.setPlaceholder(placeholders)
        if (by == null) {
            s.sendToAllWithPrefix()
            return
        }
        if (by is Player) by.sendMsgWithPrefix(s)
    }

    @EventHandler
    fun creeper(event: CreeperPowerEvent) {
        val creeper = event.entity
        val placeholder = mapOf(
            "world" to creeper.world.name,
            "x" to creeper.location.blockX,
            "y" to creeper.location.blockY,
            "z" to creeper.location.blockZ
        )
        Log.debug("creeper_transform", placeholder)
        if (creeperConfig.getBoolean("enable")
            && !creeper.world.name.isMatch(creeperConfig.getStringList("disable_world"))
        ) event.isCancelled = true
    }

    @EventHandler
    fun transform(event: EntityTransformEvent) {
        for (pt in preventList) if (pt.prevent(event)) return
    }

    class PreventTransform(obj: Any?) {
        var effective = true
        private lateinit var from: EntityType
        private lateinit var to: EntityType
        private lateinit var disable: List<String>

        init {
            try {
                val map = obj as Map<*, *>
                from = EntityType.valueOf(map["from"].toString())
                to = EntityType.valueOf(map["to"].toString())
                disable = map["disable_world"].let {
                    when (it) {
                        is Iterable<*> -> it.toList().map { s -> s.toString() }
                        is String -> listOf(it)
                        else -> emptyList()
                    }
                }
            } catch (e: Exception) {
                effective = false
                Log.formatWarn("entity_transform_load", emptyMap(), e)
            }
        }

        /**
         * 检查阻止转换, 若类型对应并且不在禁用的世界中, 则会阻止转换并发送debug信息
         *
         * @param event EntityTransformEvent
         * @return 若阻止转换则返回true
         */
        fun prevent(event: EntityTransformEvent): Boolean {
            if (event.transformedEntity.type != to
                || event.entity.type != from
                || event.entity.world.name.isMatch(disable)
            ) return false
            event.isCancelled = true
            val placeholder = mapOf(
                "world" to event.entity.world.name,
                "x" to event.entity.location.blockX,
                "y" to event.entity.location.blockY,
                "z" to event.entity.location.blockZ,
                "from" to from.name,
                "to" to to.name
            )
            Log.debug("entity_transform", placeholder)
            return true
        }
    }
}