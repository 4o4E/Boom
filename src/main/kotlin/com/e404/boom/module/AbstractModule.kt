package com.e404.boom.module

import com.e404.boom.Boom
import com.e404.boom.util.Log
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * 代表一个模块
 */
abstract class AbstractModule(val name: String) {
    /**
     * 该模块对应的配置文件
     */
    private var file: File = File(Boom.instance.dataFolder, "$name.yml")

    /**
     * 该模块的配置
     */
    protected lateinit var config: FileConfiguration

    protected fun initCfg() {
        if (!file.exists()) try {
            Boom.instance.saveResource("$name.yml", false)
        } catch (e: Exception) {
            Log.formatWarn("config_save", mapOf("name" to name), e)
        }
        config = YamlConfiguration.loadConfiguration(file)
    }

    protected fun logFinish() {
        Log.formatInfo("module_load_finish", mapOf("name" to name))
    }

    /**
     * 启用此模块时调用的方法
     */
    abstract fun onEnable()

    /**
     * 重载此模块时调用的方法
     */
    abstract fun onReload()
}