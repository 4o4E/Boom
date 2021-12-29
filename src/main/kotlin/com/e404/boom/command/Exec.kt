package com.e404.boom.command

import com.e404.boom.Boom
import org.bukkit.command.CommandSender

object Exec {
    /**
     * 执行参数长度为1的指令
     *
     * @param sender 发送者
     * @param args 指令参数
     */
    fun exec1(sender: CommandSender, args: Array<out String>) {
        when(val arg = args[0].lowercase()) {
            "reload" -> Boom.onReload(sender)
        }
    }
}