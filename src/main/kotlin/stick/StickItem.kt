package top.e404.boom.stick

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.util.editItemMeta
import top.e404.eplugin.util.lore
import top.e404.eplugin.util.name

object StickItem {
    private val display = "&e调试棒".color()
    private val lore by lazy {
        listOf(
            "&f调试棒".color(),
            "&f右键点击切换盔甲架和展示框可见性".color(),
            "&f潜行+右键点击切换展示框和盔甲架为切换可交互".color(),
            "&f潜行+右键点击方块或空气将范围1内的".color(),
            "&f不可交互盔甲架为可交互并自动切换无重力".color(),
        )
    }

    val stick = ItemStack(Material.STICK).editItemMeta {
        lore = ArrayList(StickItem.lore)
        setDisplayName(display)
    }
        get() = field.clone()

    fun ItemStack.isStick() = type == Material.STICK
            && name == display
            && lore == StickItem.lore

    fun Player.isHoldStick() = inventory.itemInMainHand.isStick()
}