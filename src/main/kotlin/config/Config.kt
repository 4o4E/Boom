package top.e404.boom.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.entity.Player
import top.e404.boom.PL
import top.e404.boom.hook.RgHook
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.EPlugin.Companion.placeholder
import top.e404.eplugin.config.ERegionConfig
import top.e404.eplugin.config.JarConfig
import top.e404.eplugin.config.RegionConfig
import top.e404.eplugin.config.serialization.RegexSerialization
import top.e404.eplugin.util.SoundConfig
import top.e404.eplugin.util.forEachOp
import java.text.SimpleDateFormat
import java.util.*

object Config : ERegionConfig<BoomConfig, RgConfig>(
    plugin = PL,
    path = "config.yml",
    default = JarConfig(PL, "config.yml"),
    serializer = RgConfig.serializer(),
    rgHook = RgHook
) {
    var debug: Boolean
        get() = config.debug
        set(value) {
            config.debug = value
        }

    val stickName: String
        get() = config.stick.name
    val stickLore: List<String>
        get() = config.stick.lore.map { it.color() }
}

@Serializable
data class RgConfig(
    var debug: Boolean = false,
    var update: Boolean = true,
    override val global: BoomConfig,
    override val region: Map<String, BoomConfig>,
    override val each: Map<String, BoomConfig>,
    val stick: StickConfig,
) : RegionConfig<BoomConfig>

@Serializable
data class BoomConfig(
    val explosion: Map<String, ExplosionConfig>? = null,
    @SerialName("disable_fire_spread")
    val disableFireSpread: Boolean? = null,
    @SerialName("disable_fire_burn")
    val disableFireBurn: Boolean? = null,
    @SerialName("protect_farmland")
    val protectFarmland: Boolean? = null,
    @SerialName("prevent_villager_to_witch")
    val protectVillagerToWitch: Boolean? = null,
    @SerialName("prevent_villager_to_zombie")
    val protectVillagerToZombie: Boolean? = null,
    @SerialName("prevent_zombie_villager_to_villager")
    val protectZombieVillagerToVillager: Boolean? = null,
    @SerialName("prevent_zombie_to_drowned")
    val protectZombieToDrowned: Boolean? = null,
    @SerialName("prevent_enderman_pickup")
    val preventEndermanPickup: Boolean? = null,
    @SerialName("fix_armorstand_pose")
    val fixArmorstandPose: Boolean? = null,
    @SerialName("keep_inventory_on_death")
    val keepInventoryOnDeath: Boolean? = null,
    @SerialName("keep_level_on_death")
    val keepLevelOnDeath: Boolean? = null,
    @SerialName("prevent_player_damage")
    val preventPlayerDamage: Boolean? = null,
    @SerialName("prevent_use_bed")
    val preventUseBed: PreventUseConfig? = null,
    @SerialName("prevent_use_respawn_anchor")
    val preventUseRespawnAnchor: PreventUseConfig? = null,
    @SerialName("prevent_use_command")
    val preventUseCommand: PreventUseCommand? = null,
    @SerialName("transform_use_command")
    val transformUseCommand: TransformUseCommandConfig? = null,
    @SerialName("limit_entity_spawn")
    val limitEntitySpawn: Map<String, Int>? = null,
    @SerialName("prevent_click_block")
    val preventClickBlock: List<ClickBlockConfig>? = null,
    @SerialName("prevent_click_entity")
    val preventClickEntity: List<ClickEntityConfig>? = null,
)

@Serializable
data class ExplosionConfig(
    val enable: Boolean = true,
    val cancel: Boolean = false
)

@Serializable
data class PreventUseConfig(
    val enable: Boolean,
    val message: String,
    val sound: SoundConfig?
)

@Serializable
data class PreventUseCommand(
    val enable: Boolean,
    val message: Message?,
    val log: Log?,
    val regexes: List<@Serializable(RegexSerialization::class) Regex>
)

@Serializable
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
        if (console != "") PL.info(console.p())
        if (op != "") forEachOp { PL.sendMsgWithPrefix(it, op.p()) }
    }
}

@Serializable
data class Log(
    val enable: Boolean,
    @SerialName("datetime_format")
    val datetimeFormat: String,
    @SerialName("log_format")
    val logFormat: String,
    val file: String,
) {
    private val sdf by lazy { SimpleDateFormat(datetimeFormat) }

    private val realFile by lazy { PL.dataFolder.resolve(file) }

    private fun getLogString(player: Player, command: String) =
        logFormat.placeholder(
            "datetime" to sdf.format(Date()),
            "player" to player.name,
            "ip" to (player.address?.run { "$hostName:$port" } ?: "unknown"),
            "command" to command
        )

    fun log(player: Player, command: String) {
        if (enable) PL.runTaskAsync {
            synchronized(file) {
                realFile.appendText("${getLogString(player, command)}\n")
            }
        }
    }
}

@Serializable
data class TransformUseCommandConfig(
    val enable: Boolean,
    val list: List<CommandTrigger>
)

@Serializable
data class CommandTrigger(
    @Serializable(RegexSerialization::class)
    val regex: Regex,
    val trigger: List<String>,
    val permission: String? = null,
    val noperm: String = "",
) {
    fun match(command: String) = regex.matches(command)
}

@Serializable
data class ClickBlockConfig(
    @Serializable(RegexSerialization::class)
    val item: Regex,
    @Serializable(RegexSerialization::class)
    val block: Regex,
    @Serializable(ClickTypeSerializer::class)
    val type: ClickType
)

@Serializable
data class ClickEntityConfig(
    @Serializable(RegexSerialization::class)
    val item: Regex,
    @Serializable(RegexSerialization::class)
    val entity: Regex,
    @Serializable(ClickTypeSerializer::class)
    val type: ClickType
)

object ClickTypeSerializer : KSerializer<ClickType> {
    override val descriptor =
        PrimitiveSerialDescriptor("top.e404.boom.config.ClickType.Companion.getSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ClickType {
        val s = decoder.decodeString()
        return ClickType.values().first { it.regex.matches(s) }
    }

    override fun serialize(encoder: Encoder, value: ClickType) {
        encoder.encodeString(value.name)
    }
}

@Serializable
enum class ClickType(
    regex: String
) {
    LEFT("(?i)l|left"),
    RIGHT("(?i)r|right"),
    ALL("(?i)a|all");

    val regex = Regex(regex)
}

@Serializable
data class StickConfig(
    val name: String,
    val lore: List<String>
)