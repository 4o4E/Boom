package com.e404.boom.util

import com.e404.boom.Boom
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object Lang {
    private var langConfig: YamlConfiguration

    init {
        langConfig = Boom.instance.getResource("lang/en_US.yml").use {
            it!!.bufferedReader().use { br -> YamlConfiguration.loadConfiguration(br) }
        }
    }

    /**
     * 加载语言文件
     *
     * @param lang 语言代号
     */
    fun load(lang: String) {
        val plugin = Boom.instance
        // 初始化语言文件夹
        val folder = File(plugin.dataFolder, "lang")
        if (folder.isFile) folder.delete()
        folder.mkdirs()
        // 初始化语言文件
        val langFile = File(plugin.dataFolder, "lang/$lang.yml")
        if (!langFile.exists()) {
            var resource: InputStream? = plugin.getResource("lang/$lang.yml")
            if (resource == null) {
                resource = plugin.getResource("lang/en_US.yml")
                Log.warn("自带语言文件中不存在`$lang.yml`, 将通过`en_US.yml`创建语言文件副本")
                Log.warn("$lang.yml does not exist in the built-in language file. It will be passed through en_US create a copy of the language file")
            }
            FileOutputStream(langFile).use { fos ->
                resource!!.use { it.copyTo(fos) }
            }
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile)
        Log.formatInfo("lang_load_finish", mapOf("lang" to lang))
    }

    /**
     * 获取语言文件中的debug信息
     *
     * @param path debug_format下的路径
     * @param placeholder 占位符map
     * @param default 不存在此内容时返回的内容
     * @return 经过占位符替换的debug信息
     */
    fun getDebugFormat(path: String, placeholder: Map<String, Any>, default: String = path): String {
        return getString("debug_format.$path", placeholder, default)
    }

    /**
     * 获取语言文件中的info信息
     *
     * @param path info_format下的路径
     * @param placeholder 占位符map
     * @param default 不存在此内容时返回的内容
     * @return 经过占位符替换的debug信息
     */
    fun getInfoFormat(path: String, placeholder: Map<String, Any>): String {
        return getString("info_format.$path", placeholder, "info_format.$path")
    }

    /**
     * 获取语言文件中的warn信息
     *
     * @param path warn_format下的路径
     * @param placeholder 占位符map
     * @param default 不存在此内容时返回的内容
     * @return 经过占位符替换的debug信息
     */
    fun getWarnFormat(path: String, placeholder: Map<String, Any>): String {
        return getString("warn_format.$path", placeholder, "warn_format.$path")
    }

    /**
     * 获取语言文件中的command信息
     *
     * @param path debug_format下的路径
     * @param placeholder 占位符map
     * @param default 不存在此内容时返回的内容
     * @return 经过占位符替换的debug信息
     */
    fun getCommandFormat(path: String, placeholder: Map<String, Any>): String {
        return getString("command.$path", placeholder, "command.$path")
    }

    /**
     * 获取当亲语言中的文本内容
     *
     * @param path 路径
     * @param placeholder 占位符 格式为 <"world", world> 将会替换字符串中的 {world} 为 world
     * @param default 若获取到的数据为空则返回默认值
     * @return 语言内容
     */
    fun getString(path: String, placeholder: Map<String, Any>, default: String): String {
        return langConfig.getString(path)?.setPlaceholder(placeholder) ?: default
    }

    /**
     * 获取当亲语言中的列表内容
     *
     * @param path 路径
     * @param placeholder 占位符 格式为 <"world", world> 将会替换字符串中的 {world} 为 world
     * @return 语言内容
     */
    fun getStringList(path: String, placeholder: Map<String, Any>): MutableList<String> {
        return langConfig.getStringList(path).map { it.setPlaceholder(placeholder) }.toMutableList()
    }
}