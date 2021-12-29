package com.e404.boom

import com.e404.boom.command.Exec
import com.e404.boom.command.Tab.tab0
import com.e404.boom.command.Tab.tab1
import com.e404.boom.module.*
import com.e404.boom.util.Lang
import com.e404.boom.util.Log
import com.e404.boom.util.hasPerm
import org.bstats.bukkit.Metrics
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.JavaPlugin

class Boom : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var instance: Boom

        @JvmStatic
        val prefix = "§7[§aBoom§7]"

        @JvmStatic
        lateinit var modules: List<AbstractModule>

        @JvmStatic
        fun onReload(commandSender: CommandSender? = null) {
            var sender = commandSender
            if (sender is ConsoleCommandSender) sender = null
            // 载入config
            reloadPluginConfig(sender)
            // 载入语言文件
            Lang.load(instance.config.getString("lang") ?: "en_US")
            // 载入模块
            for (module in modules) module.onReload()
        }

        @JvmStatic
        fun reloadPluginConfig(sender: CommandSender?) {
            // 载入config
            val placeholder = mapOf("name" to "config")
            try {
                instance.saveDefaultConfig()
            } catch (e: Exception) {
                val warn = Lang.getWarnFormat("config_save", placeholder)
                Log.warn(warn, e)
                sender?.sendMessage(warn)
            }
            try {
                instance.reloadConfig()
                Log.formatInfo("config_load_finish", mapOf("name" to "config"))
            } catch (e: Exception) {
                val warn = Lang.getWarnFormat("config_reload", placeholder)
                Log.warn(warn, e)
                sender?.sendMessage(warn)
            }
        }
    }

    override fun onEnable() {
        instance = this
        Metrics(this, 11445)
        modules = listOf<AbstractModule>(
            ExplosionHandler,
            BlockHandler,
            ChatHandler,
            EntityHandler
        )
        // 载入config
        reloadPluginConfig(null)
        // 载入语言文件
        Lang.load(instance.config.getString("lang") ?: "en_US")
        // 载入模块
        for (module in modules) module.onEnable()
        Log.info("&f成功载入, 作者404E, 反馈问题请联系作者qq: 869951226")
    }

    override fun onDisable() {
        Log.info("&f成功卸载, 作者404E, 反馈问题请联系作者qq: 869951226")
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        when(args.size) {
            0 -> sender.sendHelp()
            1 -> Exec.exec1(sender, args)
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): MutableList<String> {
        val list = ArrayList<String>()
        when(args.size) {
            0 -> list.tab0(sender, args)
            1 -> list.tab1(sender, args)
        }
        return list
    }

    private fun CommandSender.sendHelp() {
        val path = if (hasPerm("admin")) "admin_help" else "help"
        val placeholder = mapOf("version" to description.version)
        sendMessage(Lang.getCommandFormat(path, placeholder))
    }
}