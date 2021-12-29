package com.e404.boom.util

import org.bukkit.entity.Player
import org.slf4j.LoggerFactory

object Log {
    // 接收debug信息的玩家
    @JvmStatic
    private val debugger = ArrayList<Player>()

    @JvmStatic
    private val log = LoggerFactory.getLogger("&aBoom&r".color())

    @JvmStatic
    private val debugLog = LoggerFactory.getLogger("&bBoomDebug&r".color())

    @JvmStatic
    fun String.color() = this.replace("&", "§")

    @JvmStatic
    fun info(s: String) = log.info(s.color())

    @JvmStatic
    fun formatInfo(path: String, placeholder: Map<String, Any>) {
        log.info(Lang.getInfoFormat(path, placeholder).color())
    }

    @JvmStatic
    fun warn(s: String, throwable: Throwable? = null) {
        if (throwable != null) log.warn("&c$s".color(), throwable)
        else log.warn("&c$s".color())
    }

    @JvmStatic
    fun formatWarn(path: String, placeholder: Map<String, Any>, throwable: Throwable? = null) {
        if (throwable != null) log.warn("&c${Lang.getWarnFormat(path, placeholder)}".color(), throwable)
        else log.warn("&c${Lang.getWarnFormat(path, placeholder)}".color())
    }

    /**
     * 输出debug信息, 若存在接收debug消息的玩家则同时发送至其聊天栏
     *
     * @param path debug信息路径
     * @param placeholder 占位符
     */
    @JvmStatic
    fun debug(path: String, placeholder: Map<String, Any>) {
        val s = Lang.getDebugFormat(path, placeholder)
        if (getCfg().getBoolean("debug")) {
            debugLog.info("&b$s".color())
            for (p in debugger) p.sendMsgWithPrefix("&b$s".color())
        }
    }
}