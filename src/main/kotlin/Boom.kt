package top.e404.boom

import top.e404.boom.command.CommandManager
import top.e404.boom.config.Config
import top.e404.boom.config.Lang
import top.e404.boom.listener.*
import top.e404.boom.update.Update
import top.e404.eplugin.EPlugin

class Boom : EPlugin() {
    companion object {
        val logo = listOf(
            """&6 ______     ______     ______     __    __   """,
            """&6/\  == \   /\  __ \   /\  __ \   /\ "-./  \  """,
            """&6\ \  __<   \ \ \/\ \  \ \ \/\ \  \ \ \-./\ \ """,
            """&6 \ \_____\  \ \_____\  \ \_____\  \ \_\ \ \_\""",
            """&6  \/_____/   \/_____/   \/_____/   \/_/  \/_/"""
        )
    }

    override val debugPrefix: String
        get() = langManager.getOrElse("debug_prefix") { "&7[&aBoom&7]" }
    override val prefix: String
        get() = langManager.getOrElse("prefix") { "&7[&aBoom&7]" }

    override fun enableDebug() = Config.debug
    override val bstatsId = 14814
    override val langManager by lazy { Lang }

    override fun onEnable() {
        PL = this
        bstats()
        Config.load(null)
        Lang.load(null)
        CommandManager.register()
        ExplosionListener.register()
        FireListener.register()
        FarmlandListener.register()
        TransformListener.register()
        InteractListener.register()
        CommandListener.register()
        SpawnListener.register()
        ArmorStandListener.register()
        StickListener.register()
        DeathListener.register()
        Update.init()
        for (line in logo) info(line)
        info("&a加载完成, 作者404E, 感谢使用")
    }

    override fun onDisable() {
        cancelAllTask()
        info("&a卸载完成, 作者404E, 感谢使用")
    }
}

lateinit var PL: Boom
    private set