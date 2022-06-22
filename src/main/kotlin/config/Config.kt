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
import top.e404.eplugin.util.*
import java.text.SimpleDateFormat
import java.util.*

object Config : EConfig(
    plugin = PL,
    path = "config.yml",
    default = Companion.JarConfig(PL, "config.yml")
) {
    var debug = false
    var update = true
    lateinit var global: WorldConfig
        private set
    lateinit var each: Map<String, WorldConfig>
        private set

    fun <T> getEachOrGlobal(world: String, block: WorldConfig.() -> T?): T? {
        return each[world]?.block() ?: global.block()
    }

    override fun YamlConfiguration.onLoad() {
        debug = getBoolean("debug")
        update = getBoolean("update", true)
        global = getWorldConfig("global")!!
        each = getConfigurationSection("each")
            ?.getKeys(false)
            ?.mapNotNull { world ->
                val worldConfig = getWorldConfig(world)
                    ?: return@mapNotNull null
                world to worldConfig
            }?.toMap() ?: emptyMap()
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
        val preventUseBed: PreventUseConfig?,
        val preventUseRespawnAnchor: PreventUseConfig?,
        val preventUseCommand: PreventUseCommand?,
        val transformUseCommand: TransformUseCommandConfig?,
        val limitEntitySpawn: Map<String, Int>?,
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
                preventUseBed = cfg.getPreventUseConfig("prevent_use_bed"),
                preventUseRespawnAnchor = cfg.getPreventUseConfig("prevent_use_respawn_anchor"),
                preventUseCommand = cfg.getPreventUseCommand("prevent_use_command"),
                transformUseCommand = cfg.getTransformUseCommand("transform_use_command"),
                limitEntitySpawn = cfg.getConfigurationSection("limit_entity_spawn")?.let { limit ->
                    limit.getKeys(false).associate { key -> key.formatAsConst() to getInt(key) }
                }
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
                        plugin.warn("跳过错误的正则: $it", e)
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
}