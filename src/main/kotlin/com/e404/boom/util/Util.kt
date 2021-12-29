package com.e404.boom.util

import com.e404.boom.Boom
import com.e404.boom.util.Log.color
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * 发送带前缀并且替换&的消息
 *
 * @param s 消息
 */
fun CommandSender.sendMsgWithPrefix(s: String) {
    sendMessage("${Boom.prefix} $s".color())
}

/**
 * 给在线op发送带前缀并且替换&的消息
 *
 * @param s 消息
 */
fun sendOpMsg(s: String) {
    Bukkit.getOperators()
        .filter { it.isOnline }
        .forEach { it.player!!.sendMsgWithPrefix(s) }
}

/**
 * 给所有在线玩家发送消息
 *
 * @param s 消息
 */
fun sendAllMsg(s: String) {
    for (p in Bukkit.getOnlinePlayers()) p.sendMsgWithPrefix(s)
}

/**
 * 获取玩家主手的物品
 *
 * @return 物品, 如果是AIR返回null
 */
fun Player.getItemInMainHand(): ItemStack? {
    val item = inventory.itemInMainHand
    if (item.type == Material.AIR) return null
    return item
}

/**
 * 检查权限节点, 若没有权限则发送提醒消息
 *
 * @param perm 权限节点名字(省略utools.)
 * @return 若sender有此权限节点返回true
 */
fun CommandSender.checkPerm(perm: String): Boolean {
    val permission = "boom.${perm}"
    if (hasPermission(permission)) return true
    sendMsgWithPrefix("&c无权限")
    val placeholder = mapOf(
        "sender" to name,
        "perm" to permission
    )
    Log.debug("no_perm", placeholder)
    return false
}

/**
 * 检查权限节点, 若没有权限不会发送提醒消息
 *
 * @param perm 权限节点名字(省略utools.)
 * @return 若sender有此权限节点返回true
 */
fun CommandSender.hasPerm(perm: String): Boolean {
    val permission = "boom.${perm}"
    if (hasPermission(permission)) return true
    val placeholder = mapOf(
        "sender" to name,
        "perm" to permission
    )
    Log.debug("no_perm", placeholder)
    return false
}

/**
 * 检查sender是否是玩家, 若不是玩家则发送提醒消息
 *
 * @return 若sender是player返回true
 */
fun CommandSender.isPlayer(): Boolean {
    if (this is Player) return true
    sendMsgWithPrefix("&c此指令仅玩家可用")
    return false
}

/**
 * 文本匹配正则列表
 *
 * @param list 正则列表
 * @return 若列表中存在匹配的正则则返回true
 */
fun String.isMatch(list: List<String>): Boolean {
    for (regex in list) if (matches(Regex(regex))) return true
    return false
}

/**
 * 获得配置文件
 *
 * @return 配置文件
 */
fun getCfg(): FileConfiguration {
    return Boom.instance.config
}

/**
 * 执行指令
 *
 * @param sender 执行的对象
 */
fun String.exec(sender: CommandSender = Bukkit.getConsoleSender()) {
    Bukkit.dispatchCommand(sender, this)
}

/**
 * 通过名字获取音效, 若不存在则在控制台提示(不播放)
 *
 * @param sound 音效名字
 */
fun Player.playSound(sound: String) {
    val name = sound.trim()
    if (name != "") {
        val s = try {
            Sound.valueOf(name)
        } catch (e: Exception) {
            Log.formatWarn("sound_not_found", mapOf("sound" to name), e)
            return
        }
        playSound(location, s, 1F, 1F)
    }
}

/**
 * 字符串批量替换占位符
 *
 * @param placeholder 占位符 格式为 <"world", world> 将会替换字符串中的 {world} 为 world
 * @return 经过替换的字符串
 */
fun String.setPlaceholder(placeholder: Map<String, Any>): String {
    var s = this
    for ((k, v) in placeholder.entries) s = s.replace("{$k}", v.toString())
    return s
}

/**
 * 将消息发送给所有在线玩家
 */
fun String.sendToAllWithPrefix() {
    for (player in Bukkit.getOnlinePlayers()) player.sendMsgWithPrefix(this.color())
}

/**
 * 将消息发送给所有在线op
 */
fun String.sendToOperatorWithPrefix() {
    for (player in Bukkit.getOnlinePlayers()) if (player.isOp) player.sendMsgWithPrefix(this.color())
}