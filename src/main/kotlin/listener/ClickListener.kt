package top.e404.boom.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import top.e404.boom.PL
import top.e404.boom.config.ClickType
import top.e404.boom.config.Config
import top.e404.boom.config.Lang
import top.e404.eplugin.listener.EListener

object ClickListener : EListener(PL) {
    @EventHandler
    fun PlayerInteractEvent.onEvent() {
        val block = clickedBlock ?: return
        val hand = hand ?: return
        val cfg = Config.getConfig(player.location) { preventClickBlock } ?: return
        val item = player.inventory.getItem(hand).type.name
        for ((itemRegex, blockRegex, type) in cfg) {
            // 检测破坏 && 动作不是破坏
            if (type == ClickType.LEFT && action != Action.LEFT_CLICK_BLOCK) continue
            // 检测点击 && 动作不是点击
            if (type == ClickType.RIGHT && action != Action.RIGHT_CLICK_BLOCK) continue
            // 物品或方块不匹配
            if (!blockRegex.matches(block.type.name)
                || !itemRegex.matches(item)
            ) continue
            if (player.hasPermission("boom.bypass.block")) {
                plugin.debug {
                    Lang[
                            "click.pass",
                            "player" to player.name,
                            "item" to item,
                            "target" to block.type.name,
                            "world" to player.world.name,
                            "x" to block.x,
                            "y" to block.y,
                            "z" to block.z,
                    ]
                }
                return
            }
            isCancelled = true
            plugin.debug {
                Lang[
                        "click.prevent",
                        "player" to player.name,
                        "item" to item,
                        "target" to block.type.name,
                        "world" to player.world.name,
                        "x" to block.x,
                        "y" to block.y,
                        "z" to block.z,
                ]
            }
            return
        }
        plugin.debug {
            Lang[
                    "click.pass",
                    "player" to player.name,
                    "item" to item,
                    "target" to block.type.name,
                    "world" to player.world.name,
                    "x" to block.x,
                    "y" to block.y,
                    "z" to block.z,
            ]
        }
    }

    @EventHandler
    fun BlockBreakEvent.onEvent() {
        val block = block
        val cfg = Config.getConfig(player.location) { preventClickBlock } ?: return
        val item = player.inventory.itemInMainHand.type.name
        for ((itemRegex, blockRegex, type) in cfg) {
            // 忽略交互检测
            if (type == ClickType.RIGHT) continue
            // 物品或方块不匹配
            if (!blockRegex.matches(block.type.name)
                || !itemRegex.matches(item)
            ) continue
            if (player.hasPermission("boom.bypass.block")) {
                plugin.debug {
                    Lang[
                            "click.pass",
                            "player" to player.name,
                            "item" to item,
                            "target" to block.type.name,
                            "world" to player.world.name,
                            "x" to block.x,
                            "y" to block.y,
                            "z" to block.z,
                    ]
                }
                return
            }
            isCancelled = true
            plugin.debug {
                Lang[
                        "click.prevent",
                        "player" to player.name,
                        "item" to item,
                        "target" to block.type.name,
                        "world" to player.world.name,
                        "x" to block.x,
                        "y" to block.y,
                        "z" to block.z,
                ]
            }
            return
        }
        plugin.debug {
            Lang[
                    "click.pass",
                    "player" to player.name,
                    "item" to item,
                    "target" to block.type.name,
                    "world" to player.world.name,
                    "x" to block.x,
                    "y" to block.y,
                    "z" to block.z,
            ]
        }
    }

    @EventHandler
    fun PlayerInteractEntityEvent.onEvent() {
        val cfg = Config.getConfig(player.location) { preventClickEntity } ?: return
        val item = player.inventory.getItem(hand).type.name
        val entity = rightClicked.type.name
        for ((itemRegex, entityRegex, type) in cfg) {
            // 忽略left, 检测right/all
            if (type == ClickType.LEFT) continue
            // 物品或方块不匹配
            if (!entityRegex.matches(entity)
                || !itemRegex.matches(item)
            ) continue
            if (player.hasPermission("boom.bypass.entity")) {
                plugin.debug {
                    val l = rightClicked.location
                    Lang[
                            "click.pass",
                            "player" to player.name,
                            "item" to item,
                            "target" to entity,
                            "world" to player.world.name,
                            "x" to l.blockX,
                            "y" to l.blockY,
                            "z" to l.blockZ,
                    ]
                }
                return
            }
            isCancelled = true
            plugin.debug {
                val l = rightClicked.location
                Lang[
                        "click.prevent",
                        "player" to player.name,
                        "item" to item,
                        "target" to entity,
                        "world" to player.world.name,
                        "x" to l.blockX,
                        "y" to l.blockY,
                        "z" to l.blockZ,
                ]
            }
            return
        }
        plugin.debug {
            val l = rightClicked.location
            Lang[
                    "click.pass",
                    "player" to player.name,
                    "item" to item,
                    "target" to entity,
                    "world" to player.world.name,
                    "x" to l.blockX,
                    "y" to l.blockY,
                    "z" to l.blockZ,
            ]
        }
    }

    @EventHandler
    fun HangingBreakByEntityEvent.onEvent() {
        val remover = remover
        if (remover !is Player) return
        val entity = entity
        val cfg = Config.getConfig(remover.location) { preventClickEntity } ?: return
        val item = remover.inventory.itemInMainHand.type.name
        for ((itemRegex, entityRegex, type) in cfg) {
            // 忽略交互检测
            if (type == ClickType.RIGHT) continue
            // 物品或实体不匹配
            if (!entityRegex.matches(entity.type.name)
                || !itemRegex.matches(item)
            ) continue
            if (remover.hasPermission("boom.bypass.entity")) {
                plugin.debug {
                    val l = entity.location
                    Lang[
                            "click.pass",
                            "player" to remover.name,
                            "item" to item,
                            "target" to entity.type.name,
                            "world" to remover.world.name,
                            "x" to l.blockX,
                            "y" to l.blockY,
                            "z" to l.blockZ,
                    ]
                }
                return
            }
            isCancelled = true
            plugin.debug {
                val l = entity.location
                Lang[
                        "click.prevent",
                        "player" to remover.name,
                        "item" to item,
                        "target" to entity.type.name,
                        "world" to remover.world.name,
                        "x" to l.blockX,
                        "y" to l.blockY,
                        "z" to l.blockZ,
                ]
            }
            return
        }
        plugin.debug {
            val l = entity.location
            Lang[
                    "click.pass",
                    "player" to remover.name,
                    "item" to item,
                    "target" to entity.type.name,
                    "world" to remover.world.name,
                    "x" to l.blockX,
                    "y" to l.blockY,
                    "z" to l.blockZ,
            ]
        }
    }

    @EventHandler
    fun EntityDamageByEntityEvent.onEvent() {
        val damager = damager
        if (damager !is Player) return
        val cfg = Config.getConfig(damager.location) { preventClickEntity } ?: return
        val item = damager.inventory.itemInMainHand.type.name
        for ((itemRegex, entityRegex, type) in cfg) {
            // 忽略right, 检测left/all
            if (type == ClickType.RIGHT) continue
            // 物品或方块不匹配
            if (!entityRegex.matches(entityType.name)
                || !itemRegex.matches(item)
            ) continue
            if (damager.hasPermission("boom.bypass.entity")) {
                plugin.debug {
                    val l = entity.location
                    Lang[
                            "click.pass",
                            "player" to damager.name,
                            "item" to item,
                            "target" to entity.type.name,
                            "world" to damager.world.name,
                            "x" to l.blockX,
                            "y" to l.blockY,
                            "z" to l.blockZ,
                    ]
                }
                return
            }
            isCancelled = true
            plugin.debug {
                val l = entity.location
                Lang[
                        "click.prevent",
                        "player" to damager.name,
                        "item" to item,
                        "target" to entityType.name,
                        "world" to damager.world.name,
                        "x" to l.blockX,
                        "y" to l.blockY,
                        "z" to l.blockZ,
                ]
            }
            return
        }
        plugin.debug {
            val l = entity.location
            Lang[
                    "click.pass",
                    "player" to damager.name,
                    "item" to item,
                    "target" to entityType.name,
                    "world" to damager.world.name,
                    "x" to l.blockX,
                    "y" to l.blockY,
                    "z" to l.blockZ,
            ]
        }
    }
}