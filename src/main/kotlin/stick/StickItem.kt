package top.e404.boom.stick

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import top.e404.boom.config.Config
import top.e404.eplugin.util.editItemMeta
import top.e404.eplugin.util.lore
import top.e404.eplugin.util.name

object StickItem {
    private val display: String
        get() = Config.stickName
    private val lore: List<String>
        get() = Config.stickLore

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