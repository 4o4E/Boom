package top.e404.boom.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.e404.boom.PL
import top.e404.boom.stick.StickItem
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand

object Stick : ECommand(
    PL,
    "stick",
    Regex("(?i)(get)?stick"),
    true,
    "boom.stick"
) {
    override val usage = "&a/bm stick &f获取调试棒(调试盔甲架和展示框)".color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        sender as Player
        val inv = sender.inventory
        val index = inv.firstEmpty()
        if (index == -1) {
            plugin.sendMsgWithPrefix(sender, "&c背包空间不足")
            return
        }
        inv.setItem(index, StickItem.stick)
        plugin.sendMsgWithPrefix(sender, "&a已给予")
    }
}