package top.e404.boom.config

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import top.e404.boom.PL
import top.e404.boom.config.Config.PreventUseCommand.Companion.getPreventUseCommandLog
import top.e404.boom.config.Config.PreventUseCommand.Companion.getPreventUseCommandMessage
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.EPlugin.Companion.formatAsConst
import top.e404.eplugin.EPlugin.Companion.placeholder
import top.e404.eplugin.config.EConfig
import top.e404.eplugin.config.JarConfig
import top.e404.eplugin.util.*
import java.text.SimpleDateFormat
import java.util.*

object Config : EConfig(
    plugin = PL,
    path = "config.yml",
    default = JarConfig(PL, "config.yml")
) {
    var debug = false
    var update = true
    lateinit var global: WorldConfig
        private set
    lateinit var each: Map<String, WorldConfig>
        private set

    lateinit var stickName: String
    lateinit var stickLore: List<String>

    fun <T> getEachOrGlobal(world: String, block: WorldConfig.() -> T?): T? {
        return each[world]?.block() ?: global.block()
    }

    override fun YamlConfiguration.onLoad() {
        debug = getBoolean("debug")
        update = getBoolean("update", true)
        global = getWorldConfig("global")!!
        val eachCfg = getConfigurationSection("each")
        each = eachCfg?.getKeys(false)
            ?.mapNotNull { world ->
                val worldConfig = eachCfg.getWorldConfig(world)
                    ?: return@mapNotNull null
                world to worldConfig
            }
            ?.toMap()
            ?: emptyMap()
        stickName = getString("stick.name")?.color() ?: "stick.name"
        stickLore = getStringList("stick.lore").map { it.color() }
    }

    // 扩展

    data class WorldConfig(
        val explosion: Map<String, ExplosionConfig>?,
        val disableFireSpread: Boolean?,
        val disableFireBurn: Boolean?,
        val protectFarmland: Boolean?,
        val protectVillagerToWitch: Boolean?,
        val protectVillagerToZombie: Boolean?,
        val protectZombieVillagerToVillager: Boolean?,
        val protectZombieToDrowned: Boolean?,
        val preventEndermanPickup: Boolean?,
        val fixArmorstandPose: Boolean?,
        val keepInventoryOnDeath: Boolean?,
        val keepLevelOnDeath: Boolean?,
        val preventPlayerDamage: Boolean?,
        val preventUseBed: PreventUseConfig?,
        val preventUseRespawnAnchor: PreventUseConfig?,
        val preventUseCommand: PreventUseCommand?,
        val transformUseCommand: TransformUseCommandConfig?,
        val limitEntitySpawn: Map<String, Int>?,
        val preventClickBlock: List<ClickBlockConfig>,
        val preventClickEntity: List<ClickEntityConfig>,
    )

    fun ConfigurationSection.getWorldConfig(path: String) =
        getConfigurationSection(path)?.let { cfg ->
            WorldConfig(
                explosion = cfg.getConfigurationSection("explosion")?.let { explosion ->
                    explosion.getKeys(false).mapNotNull { key ->
                        val explosionCfg = explosion.getExplosionConfig(key)
                            ?: return@mapNotNull null
                        key to explosionCfg
                    }.toMap()
                },
                disableFireSpread = cfg.getBooleanOrNull("disable_fire_spread"),
                disableFireBurn = cfg.getBooleanOrNull("disable_fire_burn"),
                protectFarmland = cfg.getBooleanOrNull("protect_farmland"),
                protectVillagerToWitch = cfg.getBooleanOrNull("prevent_villager_to_witch"),
                protectVillagerToZombie = cfg.getBooleanOrNull("prevent_villager_to_zombie"),
                protectZombieVillagerToVillager = cfg.getBooleanOrNull("prevent_zombie_villager_to_villager"),
                protectZombieToDrowned = cfg.getBooleanOrNull("prevent_zombie_to_drowned"),
                preventEndermanPickup = cfg.getBooleanOrNull("prevent_enderman_pickup"),
                fixArmorstandPose = cfg.getBooleanOrNull("fix_armorstand_pose"),
                keepInventoryOnDeath = cfg.getBooleanOrNull("keep_inventory_on_death"),
                keepLevelOnDeath = cfg.getBooleanOrNull("keep_level_on_death"),
                preventPlayerDamage = cfg.getBooleanOrNull("prevent_player_damage"),
                preventUseBed = cfg.getPreventUseConfig("prevent_use_bed"),
                preventUseRespawnAnchor = cfg.getPreventUseConfig("prevent_use_respawn_anchor"),
                preventUseCommand = cfg.getPreventUseCommand("prevent_use_command"),
                transformUseCommand = cfg.getTransformUseCommand("transform_use_command"),
                limitEntitySpawn = cfg.getConfigurationSection("limit_entity_spawn")?.let { limit ->
                    limit.getKeys(false).associate { key -> key.formatAsConst() to (limit.getIntOrNull(key) ?: 100) }
                },
                preventClickBlock = cfg.getMapList("prevent_click_block").mapNotNull { map ->
                    map.asClickBlockConfig()
                },
                preventClickEntity = cfg.getMapList("prevent_click_entity").mapNotNull { map ->
                    map.asClickEntityConfig()
                },
            )
        }

    fun ConfigurationSection.getExplosionConfig(path: String) =
        getConfigurationSection(path)?.let { cfg ->
            ExplosionConfig(
                cfg.getBoolean("enable"),
                cfg.getBoolean("cancel")
            )
        }

    data class ExplosionConfig(
        val enable: Boolean,
        val cancel: Boolean
    )

    fun ConfigurationSection.getPreventUseConfig(path: String) =
        getConfigurationSection(path)?.let { cfg ->
            PreventUseConfig(
                cfg.getBoolean("enable"),
                cfg.getString("message") ?: "",
                cfg.getSoundConfig("sound")
            )
        }

    data class PreventUseConfig(
        val enable: Boolean,
        val message: String,
        val sound: SoundConfig?
    )

    fun ConfigurationSection.getPreventUseCommand(path: String) =
        getConfigurationSection(path)?.let { cfg ->
            PreventUseCommand(
                cfg.getBoolean("enable"),
                cfg.getPreventUseCommandMessage("message"),
                cfg.getPreventUseCommandLog("log"),
                cfg.getStringList("regexes").mapNotNull {
                    try {
                        Regex(it)
                    } catch (e: Exception) {
                        plugin.warn(Lang["message.invalid_regex", "regex" to it], e)
                        null
                    }
                }
            )
        }

    data class PreventUseCommand(
        val enable: Boolean,
        val message: Message?,
        val log: Log?,
        val regexes: List<Regex>
    ) {
        companion object {
            data class Message(
                val user: String,
                val console: String,
                val op: String,
            ) {
                fun send(player: Player, command: String) {
                    fun String.p() = placeholder(
                        "player" to player.name,
                        "ip" to player.address.toString(),
                        "command" to command
                    )
                    if (user != "") player.sendMessage(user.p().color())
                    if (console != "") plugin.info(console.p())
                    if (op != "") forEachOp { plugin.sendMsgWithPrefix(it, op.p()) }
                }
            }

            fun ConfigurationSection.getPreventUseCommandMessage(path: String) =
                getConfigurationSection(path)?.let { cfg ->
                    Message(
                        cfg.getString("user") ?: "",
                        cfg.getString("console") ?: "",
                        cfg.getString("op") ?: "",
                    )
                }

            data class Log(
                val enable: Boolean,
                val datetimeFormat: String,
                val logFormat: String,
                val file: String,
            ) {
                private val sdf by lazy { SimpleDateFormat(datetimeFormat) }

                private val realFile by lazy { plugin.dataFolder.resolve(file) }

                private fun getLogString(player: Player, command: String) =
                    logFormat.placeholder(
                        "datetime" to sdf.format(Date()),
                        "player" to player.name,
                        "ip" to (player.address?.run { "$hostName:$port" } ?: "unknown"),
                        "command" to command
                    )

                fun log(player: Player, command: String) {
                    if (enable) plugin.runTaskAsync {
                        synchronized(file) {
                            realFile.appendText("${getLogString(player, command)}\n")
                        }
                    }
                }
            }

            fun ConfigurationSection.getPreventUseCommandLog(path: String) =
                getConfigurationSection(path)?.let { cfg ->
                    Log(
                        cfg.getBoolean("enable"),
                        cfg.getString("datetime_format") ?: "yyyy.MM.dd HH:mm:ss",
                        cfg.getString("log_format") ?: "[{datetime}] {player}({ip}): {command}",
                        cfg.getString("file") ?: "record.log",
                    )
                }
        }
    }

    fun ConfigurationSection.getTransformUseCommand(path: String) =
        getConfigurationSection(path)?.let { cfg ->
            TransformUseCommandConfig(
                cfg.getBoolean("enable"),
                cfg.getMapList("list").mapNotNull {
                    if (it.keys.isEmpty()) return@mapNotNull null
                    TransformUseCommandConfig.Companion.CommandTrigger(
                        it["regex"].toString().toRegex(),
                        (it["trigger"] as List<*>).map { s -> s.toString() },
                        it["permission"]?.toString(),
                        it["noperm"]?.toString() ?: ""
                    )
                }
            )
        }

    data class TransformUseCommandConfig(
        val enable: Boolean,
        val list: List<CommandTrigger>
    ) {
        companion object {
            data class CommandTrigger(
                val regex: Regex,
                val trigger: List<String>,
                val permission: String?,
                val noperm: String,
            ) {
                fun match(command: String) = regex.matches(command)
            }
        }
    }


    fun Map<*, *>.asClickBlockConfig(): ClickBlockConfig? {
        val item = this["item"]?.toString()?.toRegex() ?: return null
        val block = this["block"]?.toString()?.toRegex() ?: return null
        val type = this["type"]?.toString()?.asClickType() ?: return null
        return ClickBlockConfig(item, block, type)
    }

    data class ClickBlockConfig(
        val item: Regex,
        val block: Regex,
        val type: ClickType
    )

    fun Map<*, *>.asClickEntityConfig(): ClickEntityConfig? {
        val item = this["item"]?.toString()?.toRegex() ?: return null
        val entity = this["entity"]?.toString()?.toRegex() ?: return null
        val type = this["type"]?.toString()?.asClickType() ?: return null
        return ClickEntityConfig(item, entity, type)
    }

    data class ClickEntityConfig(
        val item: Regex,
        val entity: Regex,
        val type: ClickType
    )

    fun String.asClickType() = ClickType.values().firstOrNull { it.regex.matches(this) }

    enum class ClickType(
        regex: String
    ) {
        LEFT("(?i)l|left"),
        RIGHT("(?i)r|right"),
        ALL("(?i)a|all");

        val regex = Regex(regex)
    }
}