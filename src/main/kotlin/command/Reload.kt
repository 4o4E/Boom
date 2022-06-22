package top.e404.boom.command

import org.bukkit.command.CommandSender
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.boom.config.Lang
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object Reload : ECommand(
    PL,
    "reload",
    Regex("(?i)reload|r"),
    false,
    "boom.admin"
) {
    override val usage = "&a/bm reload &f重载插件".color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        plugin.runTaskAsync {
            try {
                Config.load(sender)
                Lang.load(sender)
                plugin.sendMsgWithPrefix(sender, "&a重载完成")
            } catch (t: Throwable) {
                plugin.sendAndWarn(sender, "&c配置文件`config.yml`格式错误", t)
            }
        }
    }
}