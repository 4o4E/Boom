package top.e404.boom.config

import top.e404.boom.PL
import top.e404.eplugin.config.ELangManager

/**
 * 插件语言文件
 */
object Lang : ELangManager(PL) {
    fun parseSecondAsDuration(second: Int): String {
        var t = second
        var s = ""
        var temp = t % 60
        if (temp != 0) s = "${temp}${this["time.second"]}"
        t /= 60
        if (t == 0) return s
        temp = t % 60
        if (temp != 0) s = "${temp}${this["time.minute"]}$s"
        t /= 60
        if (t == 0) return s
        temp = t % 24
        if (temp != 0) s = "${temp}${this["time.hour"]}$s"
        t /= 24
        if (t == 0) return s
        s = "${t}${this["time.day"]}$s"
        return s
    }
}