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
import top.e404.eplugin.config.JarConfigDefault
import top.e404.eplugin.config.RegionConfig
import top.e404.eplugin.config.serialization.RegexSerialization
import top.e404.eplugin.util.SoundConfig
import top.e404.eplugin.util.forEachOp
import java.text.SimpleDateFormat
import java.util.*

/**
 * 插件配置文件
 */
object Config : ERegionConfig<BoomConfig, RgConfig>(
    plugin = PL,
    path = "config.yml",
    default = JarConfigDefault(PL, "config.yml"),
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

/**
 * 代表一个设置的实现类
 *
 * @property debug 是否启用控制台debug
 * @property update 是否检查更新
 * @property global 全局设置
 * @property region 区域设置
 * @property each 世界设置
 * @property stick 调试棒设置
 */
@Serializable
data class RgConfig(
    var debug: Boolean = false,
    var update: Boolean = true,
    override val global: BoomConfig,
    override val region: Map<String, BoomConfig>,
    override val each: Map<String, BoomConfig>,
    val stick: StickConfig,
) : RegionConfig<BoomConfig>

/**
 * 代表一个具体的设置
 *
 * @property explosion 实体爆炸设置
 * @property disableFireSpread 阻止火焰蔓延
 * @property disableFireBurn 阻止火焰烧毁方块
 * @property protectFarmland 保护耕地方块不被踩坏
 * @property protectVillagerToWitch 阻止村民变成女巫
 * @property protectVillagerToZombie 阻止村民变成僵尸村民
 * @property protectZombieVillagerToVillager 阻止僵尸村民变成村民
 * @property protectZombieToDrowned 阻止僵尸变成溺尸
 * @property preventEndermanPickup 阻止末影人搬起方块
 * @property fixArmorstandPose 在盔甲架生成的时候摆正并设置为有双手
 * @property keepInventoryOnDeath 玩家死亡时保留背包
 * @property keepLevelOnDeath 玩家死亡时保留经验
 * @property preventPlayerDamage 阻止玩家受到伤害
 * @property preventUseBed 阻止玩家使用床
 * @property preventUseRespawnAnchor 阻止玩家使用重生锚
 * @property preventUseCommand 阻止玩家使用指令
 * @property transformUseCommand 指令转接
 * @property limitEntitySpawn 限制实体生成
 * @property preventClickBlock 阻止点击方块
 * @property preventClickEntity 阻止点击实体
 */
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

/**
 * 阻止实体爆炸的设置
 *
 * @property enable 是否启用
 * @property cancel 是否直接取消
 */
@Serializable
data class ExplosionConfig(
    val enable: Boolean = true,
    val cancel: Boolean = false,
    @SerialName("prevent_damage")
    val preventDamage: List<@Serializable(RegexSerialization::class) Regex>? = emptyList()
)

/**
 * 阻止玩家使用的设置
 *
 * @property enable 是否启用
 * @property message 阻止时给玩家发送的消息
 * @property sound 阻止时给玩家播放的音效
 */
@Serializable
data class PreventUseConfig(
    val enable: Boolean,
    val message: String,
    val sound: SoundConfig?
)

/**
 * 阻止玩家使用指令的设置
 *
 * @property enable 是否启用
 * @property message 消息设置
 * @property log 日志设置
 * @property regexes 正则列表
 */
@Serializable
data class PreventUseCommand(
    val enable: Boolean,
    val message: Message?,
    val log: Log?,
    val regexes: List<@Serializable(RegexSerialization::class) Regex>
)

/**
 * 消息设置
 *
 * @property user 给使用指令的玩家发送的消息
 * @property console 控制台的消息
 * @property op 给在线op发送的消息
 */
@Serializable
data class Message(
    val user: String,
    val console: String,
    val op: String,
) {
    /**
     * 玩家执行阻止的指令时发送消息
     *
     * @param player 玩家
     * @param command 执行的指令
     */
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

/**
 * 日志设置
 *
 * @property enable 是否启用
 * @property datetimeFormat 日志时间戳格式
 * @property logFormat 日志格式
 * @property file 日志文件
 */
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

    /**
     * 玩家执行阻止的指令时记录日志
     *
     * @param player 玩家
     * @param command 执行的指令
     */
    fun log(player: Player, command: String) {
        if (enable) PL.runTaskAsync {
            synchronized(file) {
                realFile.appendText("${getLogString(player, command)}\n")
            }
        }
    }
}

/**
 * 指令转接设置
 *
 * @property enable 是否启用
 * @property list 转接指令列表
 */
@Serializable
data class TransformUseCommandConfig(
    val enable: Boolean,
    val list: List<CommandTrigger>
)

/**
 * 转接指令
 *
 * @property regex 匹配正则
 * @property trigger 触发的指令
 * @property permission 触发所需指令
 * @property noperm 无权限时返回的消息
 */
@Serializable
data class CommandTrigger(
    @Serializable(RegexSerialization::class)
    val regex: Regex,
    val trigger: List<String>,
    val permission: String? = null,
    val noperm: String = "",
) {
    /**
     * 检测正则是否匹配
     *
     * @param command 指令
     */
    fun match(command: String) = regex.matches(command)
}

/**
 * 点击方块触发设置
 *
 * @property item 物品id匹配正则
 * @property block 方块id匹配正则
 * @property type 检测类型
 */
@Serializable
data class ClickBlockConfig(
    @Serializable(RegexSerialization::class)
    val item: Regex,
    @Serializable(RegexSerialization::class)
    val block: Regex? = null,
    @Serializable(ClickTypeSerializer::class)
    val type: ClickType
)

/**
 * 点击实体触发设置
 *
 * @property item 物品id匹配正则
 * @property entity 实体id匹配正则
 * @property type 检测类型
 */
@Serializable
data class ClickEntityConfig(
    @Serializable(RegexSerialization::class)
    val item: Regex,
    @Serializable(RegexSerialization::class)
    val entity: Regex,
    @Serializable(ClickTypeSerializer::class)
    val type: ClickType
)

/**
 * [ClickType]的序列化和反序列化器
 */
object ClickTypeSerializer : KSerializer<ClickType> {
    override val descriptor =
        PrimitiveSerialDescriptor("top.e404.boom.config.ClickType.Companion.getSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ClickType {
        val s = decoder.decodeString()
        return ClickType.entries.first { it.regex.matches(s) }
    }

    override fun serialize(encoder: Encoder, value: ClickType) {
        encoder.encodeString(value.name)
    }
}

/**
 * 点击类型
 *
 * @param regex 正则
 */
@Serializable
enum class ClickType(
    regex: String
) {
    LEFT("(?i)l|left"),
    RIGHT("(?i)r|right"),
    ALL("(?i)a|all");

    val regex = Regex(regex)
}

/**
 * 调试棒设置
 *
 * @property name 调试棒名字
 * @property lore 调试棒lore
 */
@Serializable
data class StickConfig(
    val name: String,
    val lore: List<String>
)