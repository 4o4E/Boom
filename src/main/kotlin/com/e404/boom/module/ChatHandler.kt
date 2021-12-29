package com.e404.boom.module

import com.e404.boom.Boom
import com.e404.boom.event.IListener
import com.e404.boom.util.*
import com.e404.boom.util.Log.color
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.command.TabCompleter
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Constructor
import java.text.SimpleDateFormat
import java.util.*

object ChatHandler : AbstractModule("chat"), IListener {
    private lateinit var helpConfig: ConfigurationSection

    private lateinit var commandConstructor: Constructor<PluginCommand>
    private lateinit var chatConfig: ConfigurationSection
    private lateinit var commandConfig: ConfigurationSection

    private lateinit var message: String
    private var record = false
    private lateinit var sdf: SimpleDateFormat
    private lateinit var format: String

    private val recordList = ArrayList<String>()

    override fun onEnable() {
        // 初始化PluginCommand反射部分
        commandConstructor = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
        commandConstructor.isAccessible = true
        register()
        onReload()
        EScheduler.scheduleAsyncRepeat(false, 0, 6000) {
            if (recordList.isNotEmpty()) FileOutputStream(File(Boom.instance.dataFolder, "record.log"), true).use {
                it.bufferedWriter().use { bw -> for (s in recordList) bw.write("$s\n") }
            }
        }
    }

    override fun onReload() {
        initCfg()
        helpConfig = config.getConfigurationSection("help_override")!!

        chatConfig = config.getConfigurationSection("chat_filter")!!
        commandConfig = config.getConfigurationSection("command_prevent")!!
        message = commandConfig.getString("message")?.trim() ?: ""
        val recordConfig = commandConfig.getConfigurationSection("record")!!
        record = recordConfig.getBoolean("enable")
        sdf = SimpleDateFormat(recordConfig.getString("time") ?: "")
        format = recordConfig.getString("format") ?: ""
        loadCommandGroup()
        logFinish()
    }

    private fun loadCommandGroup() {
        val commands = (config.getList("command_group") ?: emptyList<Any?>())
            .filterNotNull()
            .map { CommandGroup(it).toCommand() }
        val server = Bukkit.getServer()
        val klass = server.javaClass
        klass.getDeclaredMethod("getCommandMap")
            .invoke(server)
            .let { it as SimpleCommandMap }
            .registerAll("Boom", commands)
    }

    class CommandGroup(obj: Any) {
        private val name: String
        private val alias: List<String>
        private val command: List<String>
        private val perm: String?
        private val noPerm: String?
        private val success: List<String>
        private val disableMessage: List<String>
        private val disableWorld: List<String>

        init {
            val map = obj as Map<*, *>
            name = map["name"].toString()
            alias = map["alias"].asList()
            command = map["command"].asList()
            perm = map["perm"].asString()
            noPerm = map["no_perm"].asString()
            success = map["success"].asList()
            disableMessage = map["disable_message"].asList()
            disableWorld = map["disable_world"].asList()
        }

        private fun Any?.asList(): List<String> {
            if (this == null) return emptyList()
            val list = ArrayList<String>()
            fun add(s: String) {
                if (s.trim() != "") list.add(s)
            }
            if (this is Iterable<*>) for (i in this) add(i.toString())
            else add(toString())
            return list
        }

        private fun Any?.asString(): String? {
            if (this == null) return null
            val s = toString()
            return if (s.trim() == "") null else s
        }

        fun isEffective() = name.trim() != ""

        fun toCommand(): PluginCommand? {
            return try {
                val cmd = commandConstructor.newInstance(name, Boom.instance)
                cmd.aliases = alias
                cmd.setExecutor(CommandExecutor { sender, _, _, _ ->
                    // 控制台跳过检查
                    if (sender is Player) {
                        // 检查禁止的世界
                        if (sender.world.name.isMatch(disableWorld)) {
                            for (s in disableMessage) sender.sendMessage(s.color())
                            return@CommandExecutor true
                        }
                        // 检查权限
                        if (perm != null && !sender.hasPermission(perm)) {
                            if (noPerm != null) sender.sendMessage(noPerm.color())
                            return@CommandExecutor true
                        }
                    }
                    // 执行指令
                    for (c in command) c.exec(sender)
                    // 成功消息
                    for (s in success) sender.sendMessage(s.color())
                    true
                })
                cmd.tabCompleter = TabCompleter { _, _, _, _ -> ArrayList<String>() }
                return cmd
            } catch (e: Exception) {
                Log.warn("注册指令`${name}`时出现异常", e)
                null
            }
        }
    }


    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        if (!chatConfig.getBoolean("enable")) return
        // 按全局聊天检测
        if (event.message.isMatch(chatConfig.getStringList("global"))) {
            event.prevent()
            return
        }
        // 按世界聊天检测
        if (event.message.isMatch(chatConfig.getStringList("world_settings.${event.player.world.name}"))) {
            event.prevent()
        }
    }

    private fun AsyncPlayerChatEvent.prevent() {
        isCancelled = true
        // 日志记录
        val log = chatConfig.getString("log")?.trim() ?: ""
        if (log != "") {
            Log.info(log.replace("{player}", player.name)
                .replace("{message}", message))
        }
        // 向玩家发送消息
        val message = chatConfig.getString("message")?.trim() ?: ""
        if (message != "") player.sendMessage(message.color())
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        // 去除开头/的指令原文
        val command = event.message.substring(1)
        val p = event.player
        // 检测原版help
        if (helpConfig.getBoolean("enable")
            && !p.world.name.isMatch(helpConfig.getStringList("disable_world"))
        ) {
            if (command.matches(Regex("(?i)((minecraft|bukkit):)?(help|\\?)( .*)?"))
                && helpConfig.getBoolean("minecraft")
            ) {
                event.isCancelled = true
                Log.debug("help_override", mapOf("player" to p.name))
                val help = helpConfig.getString("text") ?: ""
                if (help != "") p.sendMessage(help)
                return
            }
        }
        // 检测禁用指令
        if (!commandConfig.getBoolean("enable")) return
        // 检测全局禁用指令
        if (command.isMatch(commandConfig.getStringList("global"))) {
            event.prevent()
            return
        }
        // 检测世界禁用指令
        if (command.isMatch(commandConfig.getStringList("world_settings.${p.world.name}"))) {
            event.prevent()
        }
    }

    private fun PlayerCommandPreprocessEvent.prevent() {
        isCancelled = true
        if (message != "") player.sendMessage(message.color())
        val placeholder = mapOf(
            "{player}" to player.name,
            "{command}" to message,
            "{time}" to sdf.format(Date())
        )
        val log = commandConfig.getString("log") ?: ""
        if (log != "") Log.info(log.setPlaceholder(placeholder).color())
        if (!record) return
        recordList.add(format.setPlaceholder(placeholder))
    }
}