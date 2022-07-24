package top.e404.boom.command

import org.bukkit.command.CommandSender
import top.e404.boom.PL
import top.e404.boom.config.Config
import top.e404.boom.config.Lang
import top.e404.boom.hook.HookManager
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object Reload : ECommand(
    PL,
    "reload",
    Regex("(?i)reload|r"),
    false,
    "boom.admin"
) {
    override val usage: String
        get() = Lang["plugin_command.usage.reload"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        plugin.runTaskAsync {
            try {
                Config.load(sender)
                Lang.load(sender)
                HookManager.checkHooks()
                plugin.sendMsgWithPrefix(sender, Lang["plugin_command.reload_done"])
            } catch (t: Throwable) {
                plugin.sendAndWarn(
                    sender,
                    Lang[
                            "message.invalid_config",
                            "file" to "config.yml"
                    ],
                    t
                )
            }
        }
    }
}