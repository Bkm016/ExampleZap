package ink.ptms.examplezap

import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.command
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.platform.util.isAir
import taboolib.platform.util.takeItem

/**
 * 插件功能：
 * 1. Zap 附魔强化（基于 ItemReleaseEvent）
 *  消耗背包中的钻石，强化手中物品的锋利附魔
 * 2. Zap 属性强化（基于 ItemReleaseEvent.Display）
 *  消耗背包中的钻石，强化手中物品的伤害属性
 */
object ExampleZap : Plugin() {

    override fun onEnable() {
        // 命令部分
        command("upgrade") {
            literal("enchant") {
                execute<Player> { sender, _, _ ->
                    val itemInHand = sender.itemInHand
                    if (itemInHand.isAir()) {
                        sender.sendMessage("物品不能为空")
                        return@execute
                    }
                    val itemStream = ZaphkielAPI.read(itemInHand)
                    if (itemStream.isVanilla()) {
                        sender.sendMessage("物品不能强化")
                        return@execute
                    }
                    if (!sender.inventory.takeItem { it.type == Material.DIAMOND }) {
                        sender.sendMessage("你没有足够的钻石")
                        return@execute
                    }
                    // 获取等级
                    val enchant = itemStream.getZaphkielData().getDeep("upgrade.enchant")?.asInt() ?: 0
                    // 增加等级
                    itemStream.getZaphkielData().putDeep("upgrade.enchant", enchant + 1)
                    // 重构物品并返还给玩家
                    sender.setItemInHand(itemStream.rebuildToItemStack(sender))
                    sender.sendMessage("强化成功, 当前等级 ${enchant + 1}")
                }
            }
            literal("attribute") {
                execute<Player> { sender, _, _ ->
                    val itemInHand = sender.itemInHand
                    if (itemInHand.isAir()) {
                        sender.sendMessage("物品不能为空")
                        return@execute
                    }
                    val itemStream = ZaphkielAPI.read(itemInHand)
                    if (itemStream.isVanilla()) {
                        sender.sendMessage("物品不能强化")
                        return@execute
                    }
                    if (!sender.inventory.takeItem { it.type == Material.DIAMOND }) {
                        sender.sendMessage("你没有足够的钻石")
                        return@execute
                    }
                    // 获取等级
                    val enchant = itemStream.getZaphkielData().getDeep("upgrade.attribute")?.asInt() ?: 0
                    // 增加等级
                    itemStream.getZaphkielData().putDeep("upgrade.attribute", enchant + 1)
                    // 重构物品并返还给玩家
                    sender.setItemInHand(itemStream.rebuildToItemStack(sender))
                    sender.sendMessage("强化成功, 当前等级 ${enchant + 1}")
                }
            }
        }
    }

    /**
     * 附魔强化 "前端" 部分
     */
    @SubscribeEvent
    fun e(e: ItemReleaseEvent) {
        // 获取 "后端" 附魔等级
        val enchant = e.itemStream.getZaphkielData().getDeep("upgrade.enchant")?.asInt() ?: return
        // 给予附魔
        e.itemMeta.addEnchant(Enchantment.DAMAGE_ALL, enchant, true)
    }

    /**
     * 属性强化 "前端" 部分
     */
    @SubscribeEvent
    fun e(e: ItemReleaseEvent.Display) {
        // 获取 "后端" 属性等级
        val attribute = e.itemStream.getZaphkielData().getDeep("upgrade.attribute")?.asInt() ?: return
        // 给予属性描述
        e.addLore("attribute", listOf("", "&f +$attribute &7攻击力", ""))
    }
}